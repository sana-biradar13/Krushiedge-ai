package com.krushiedge.ai.provider

import com.krushiedge.domain.model.AiMode
import com.krushiedge.domain.model.CropAnalysisInput
import com.krushiedge.domain.model.CropAnalysisResult
import com.krushiedge.domain.model.CropType
import com.krushiedge.domain.model.EvidenceType
import com.krushiedge.domain.model.StressLevel
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mock / Demo AI Provider for fast offline testing, showcase mode, and simulated field trials.
 * Populates realistic crop health metrics for southern/central Indian agro-ecological zones.
 */
@Singleton
class MockAiProvider @Inject constructor() : AiProvider {

    override suspend fun analyzeCrop(input: CropAnalysisInput): Result<CropAnalysisResult> {
        delay(350) // Simulate fast realistic local inference latency

        val (diseaseRisk, waterRisk, nutrientRisk, stress) = when (input.crop) {
            CropType.RAGI -> Quad(0.68, 0.22, 0.15, StressLevel.HIGH) // Ragi Blast alert
            CropType.GROUNDNUT -> Quad(0.55, 0.40, 0.20, StressLevel.MODERATE) // Tikka leaf spot
            CropType.COTTON -> Quad(0.72, 0.30, 0.35, StressLevel.SEVERE) // Pink Bollworm risk
            CropType.TOMATO -> Quad(0.81, 0.25, 0.10, StressLevel.SEVERE) // Early Blight
            CropType.RICE -> Quad(0.42, 0.18, 0.25, StressLevel.MODERATE) // Brown spot
            else -> Quad(0.20, 0.15, 0.10, StressLevel.LOW)
        }

        val result = CropAnalysisResult(
            crop = input.crop,
            stressLevel = stress,
            diseaseRisk = diseaseRisk,
            waterStressRisk = waterRisk,
            nutrientRisk = nutrientRisk,
            confidence = 0.93,
            evidence = listOf(
                EvidenceType.CROP_PHOTO,
                EvidenceType.WEATHER,
                EvidenceType.VEGETATION_TREND,
                EvidenceType.SOIL_MOISTURE
            ),
            modelVersion = "krushiedge-demo-v1.0",
            aiMode = AiMode.LOCAL,
            inferenceTimeMs = 350L,
            timestamp = System.currentTimeMillis()
        )

        return Result.success(result)
    }

    override suspend fun isAvailable(): Boolean = true

    override fun providerName(): String = "MockAiProvider (Agronomy Showcase)"

    private data class Quad(
        val disease: Double,
        val water: Double,
        val nutrient: Double,
        val stress: StressLevel
    )
}
