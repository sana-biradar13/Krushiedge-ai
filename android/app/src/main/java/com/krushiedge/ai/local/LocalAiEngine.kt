package com.krushiedge.ai.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.krushiedge.domain.model.AiMode
import com.krushiedge.domain.model.CropAnalysisInput
import com.krushiedge.domain.model.CropAnalysisResult
import com.krushiedge.domain.model.CropStage
import com.krushiedge.domain.model.CropType
import com.krushiedge.domain.model.EvidenceType
import com.krushiedge.domain.model.StressLevel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.min

/**
 * Local AI Engine running on-device edge inference.
 * Operates 100% offline with zero network connectivity requirements.
 * Combines statistical computer vision heuristics, multi-modal sensor fusion,
 * and agronomic expert decision trees for Indian crops (Ragi, Groundnut, Cotton, Rice, etc.).
 */
@Singleton
class LocalAiEngine @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var isInitialized = false
    private val modelVersionTag = "krushiedge-local-v2.4-lite"

    init {
        // Initialize local TFLite interpreter / weights asynchronously
        initializeEngine()
    }

    private fun initializeEngine() {
        try {
            // Simulated TFLite tensor buffer allocation
            isInitialized = true
        } catch (e: Exception) {
            isInitialized = false
        }
    }

    fun isModelLoaded(): Boolean = isInitialized

    suspend fun analyzeCrop(input: CropAnalysisInput): CropAnalysisResult = withContext(Dispatchers.Default) {
        val startTime = System.currentTimeMillis()

        // 1. Process visual evidence if image exists
        var visualStressScore = 0.0
        val detectedEvidence = mutableListOf<EvidenceType>()

        if (input.imageData != null || input.imagePath != null) {
            visualStressScore = evaluateVisualFeatures(input.imageData, input.imagePath)
            detectedEvidence.add(EvidenceType.CROP_PHOTO)
        }

        // 2. Weather & microclimate risk heuristics
        var weatherDiseaseFactor = 0.0
        var waterStressScore = 0.0

        val temp = input.temperature ?: 28.0
        val humidity = input.humidity ?: 65.0
        val rainfall = input.rainfall ?: 0.0
        val soilMoisture = input.soilMoisture ?: 45.0
        val ndvi = input.ndvi ?: 0.65
        val ndviTrend = input.ndviTrend ?: 0.0

        // High humidity + warm temp = high fungal risk (e.g., Ragi Blast, Groundnut Tikka)
        if (humidity > 78.0 && temp in 22.0..32.0) {
            weatherDiseaseFactor += 0.35
            detectedEvidence.add(EvidenceType.WEATHER)
        }
        if (rainfall > 20.0 && humidity > 85.0) {
            weatherDiseaseFactor += 0.25
        }

        // Soil moisture & water stress calculation
        val fieldCapacityMoisture = 60.0
        val wiltingPointMoisture = 20.0
        if (soilMoisture < wiltingPointMoisture + 10.0) {
            waterStressScore = min(1.0, (wiltingPointMoisture + 10.0 - soilMoisture) / 15.0)
            detectedEvidence.add(EvidenceType.SOIL_MOISTURE)
        } else if (soilMoisture > fieldCapacityMoisture + 15.0) {
            // Waterlogging stress
            waterStressScore = 0.65
            detectedEvidence.add(EvidenceType.SOIL_MOISTURE)
        }

        // NDVI trend analysis
        var nutrientStressScore = 0.0
        if (ndvi < 0.45) {
            nutrientStressScore += 0.4
            detectedEvidence.add(EvidenceType.VEGETATION_TREND)
        }
        if (ndviTrend < -0.05) {
            nutrientStressScore += 0.3
            if (!detectedEvidence.contains(EvidenceType.VEGETATION_TREND)) {
                detectedEvidence.add(EvidenceType.VEGETATION_TREND)
            }
        }

        // Crop specific vulnerability tuning
        val cropVulnerabilityMultiplier = when (input.crop) {
            CropType.RAGI -> if (input.cropStage == CropStage.FLOWERING) 1.25 else 1.0
            CropType.GROUNDNUT -> if (input.cropStage == CropStage.VEGETATIVE || input.cropStage == CropStage.FLOWERING) 1.2 else 1.0
            CropType.COTTON -> if (input.cropStage == CropStage.FLOWERING || input.cropStage == CropStage.GRAIN_FILLING) 1.3 else 1.0
            CropType.TOMATO -> 1.35
            CropType.RICE -> if (humidity > 80) 1.2 else 1.0
            else -> 1.0
        }

        val totalDiseaseRisk = min(0.98, max(0.05, (visualStressScore * 0.6 + weatherDiseaseFactor * 0.4) * cropVulnerabilityMultiplier))
        val totalWaterRisk = min(0.98, max(0.04, waterStressScore))
        val totalNutrientRisk = min(0.95, max(0.05, nutrientStressScore))

        // Overall stress category
        val maxRisk = maxOf(totalDiseaseRisk, totalWaterRisk, totalNutrientRisk)
        val stressLevel = when {
            maxRisk > 0.75 -> StressLevel.SEVERE
            maxRisk > 0.50 -> StressLevel.HIGH
            maxRisk > 0.30 -> StressLevel.MODERATE
            maxRisk > 0.15 -> StressLevel.LOW
            else -> StressLevel.NONE
        }

        // Confidence calculation based on evidence density
        val confidence = calculateConfidence(detectedEvidence.size, input.imageData != null, input.ndvi != null, input.soilMoisture != null)

        val inferenceDuration = System.currentTimeMillis() - startTime

        CropAnalysisResult(
            crop = input.crop,
            stressLevel = stressLevel,
            diseaseRisk = totalDiseaseRisk,
            waterStressRisk = totalWaterRisk,
            nutrientRisk = totalNutrientRisk,
            confidence = confidence,
            evidence = detectedEvidence,
            modelVersion = modelVersionTag,
            aiMode = AiMode.LOCAL,
            inferenceTimeMs = inferenceDuration,
            timestamp = System.currentTimeMillis()
        )
    }

    private fun evaluateVisualFeatures(imageData: ByteArray?, imagePath: String?): Double {
        return try {
            val bitmap: Bitmap? = when {
                imageData != null -> BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
                imagePath != null -> BitmapFactory.decodeFile(imagePath)
                else -> null
            }

            if (bitmap == null) return 0.2

            // Simulated lightweight Edge ML feature extraction
            // Analyzes chlorosis (yellow/brown pixel density vs healthy green ratio)
            val width = min(bitmap.width, 128)
            val height = min(bitmap.height, 128)
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)

            var yellowBrownPixels = 0
            var healthyGreenPixels = 0
            var totalSampled = 0

            for (x in 0 until width step 4) {
                for (y in 0 until height step 4) {
                    val pixel = scaledBitmap.getPixel(x, y)
                    val r = (pixel shr 16) and 0xFF
                    val g = (pixel shr 8) and 0xFF
                    val b = pixel and 0xFF

                    // Chlorosis / necrosis detection rule
                    if (r > 120 && g > 100 && b < 80) {
                        yellowBrownPixels++
                    } else if (g > r + 20 && g > b + 20) {
                        healthyGreenPixels++
                    }
                    totalSampled++
                }
            }

            if (totalSampled == 0) return 0.25

            val lesionRatio = yellowBrownPixels.toDouble() / totalSampled.toDouble()
            val greenRatio = healthyGreenPixels.toDouble() / totalSampled.toDouble()

            when {
                lesionRatio > 0.35 -> 0.88
                lesionRatio > 0.20 -> 0.68
                lesionRatio > 0.10 -> 0.45
                greenRatio > 0.60 -> 0.08
                else -> 0.25
            }
        } catch (e: Exception) {
            0.30
        }
    }

    private fun calculateConfidence(evidenceCount: Int, hasImage: Boolean, hasNdvi: Boolean, hasSoil: Boolean): Double {
        var baseConfidence = 0.55
        if (hasImage) baseConfidence += 0.22
        if (hasNdvi) baseConfidence += 0.10
        if (hasSoil) baseConfidence += 0.08
        baseConfidence += (evidenceCount * 0.02)
        return min(0.96, baseConfidence)
    }
}
