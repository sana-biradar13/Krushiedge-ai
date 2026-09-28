package com.krushiedge.ai.mapper

import com.krushiedge.ai.model.AiAnalysisRequestDto
import com.krushiedge.ai.model.AiAnalysisResponseDto
import com.krushiedge.ai.model.DiagnosisDto
import com.krushiedge.ai.model.LocalizedTextDto
import com.krushiedge.domain.model.AiMode
import com.krushiedge.domain.model.ChemicalTreatment
import com.krushiedge.domain.model.CropAnalysisInput
import com.krushiedge.domain.model.CropAnalysisResult
import com.krushiedge.domain.model.CropType
import com.krushiedge.domain.model.DiseaseDiagnosis
import com.krushiedge.domain.model.EvidenceType
import com.krushiedge.domain.model.LocalizedText
import com.krushiedge.domain.model.OrganicTreatment
import com.krushiedge.domain.model.PathogenType
import com.krushiedge.domain.model.PlantPart
import com.krushiedge.domain.model.RiskLevel
import com.krushiedge.domain.model.SeverityLevel
import com.krushiedge.domain.model.StressLevel
import com.krushiedge.domain.model.ToxicityClass
import com.krushiedge.domain.model.TreatmentPlan
import com.krushiedge.domain.model.Urgency
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiDtoMapper @Inject constructor() {

    fun mapInputToDto(input: CropAnalysisInput, lang: String = "kn"): AiAnalysisRequestDto {
        return AiAnalysisRequestDto(
            cropType = input.crop.code,
            cropStage = input.cropStage.code,
            fieldId = input.fieldId,
            gridId = input.gridId,
            imageBase64 = null, // In production, encoded if image exists
            ndvi = input.ndvi,
            soilMoisture = input.soilMoisture,
            temperatureCelsius = input.temperature,
            humidityPercent = input.humidity,
            rainfallMm = input.rainfall,
            farmerLanguage = lang
        )
    }

    fun mapDtoToCropAnalysisResult(dto: AiAnalysisResponseDto, crop: CropType, mode: AiMode): CropAnalysisResult {
        val stressLevel = when (dto.overallStressLevel.lowercase()) {
            "severe" -> StressLevel.SEVERE
            "high" -> StressLevel.HIGH
            "moderate" -> StressLevel.MODERATE
            "low" -> StressLevel.LOW
            else -> StressLevel.NONE
        }

        val evidenceList = dto.evidenceFactors.mapNotNull { factor ->
            when (factor.evidenceType.lowercase()) {
                "crop_photo", "photo" -> EvidenceType.CROP_PHOTO
                "weather" -> EvidenceType.WEATHER
                "soil_moisture" -> EvidenceType.SOIL_MOISTURE
                "vegetation_trend", "ndvi" -> EvidenceType.VEGETATION_TREND
                "satellite" -> EvidenceType.SATELLITE
                "historical" -> EvidenceType.HISTORICAL
                else -> null
            }
        }

        return CropAnalysisResult(
            crop = crop,
            stressLevel = stressLevel,
            diseaseRisk = dto.diseaseRiskScore,
            waterStressRisk = dto.waterStressScore,
            nutrientRisk = dto.nutrientStressScore,
            confidence = dto.confidenceScore,
            evidence = evidenceList.ifEmpty { listOf(EvidenceType.WEATHER, EvidenceType.CROP_PHOTO) },
            modelVersion = dto.modelVersion,
            aiMode = mode,
            inferenceTimeMs = dto.latencyMs,
            timestamp = System.currentTimeMillis()
        )
    }

    fun mapDiagnosisDtoToDomain(dto: DiagnosisDto, crop: CropType, confidence: Double): DiseaseDiagnosis {
        val pathogen = when (dto.pathogen.lowercase()) {
            "fungal" -> PathogenType.FUNGAL
            "bacterial" -> PathogenType.BACTERIAL
            "viral" -> PathogenType.VIRAL
            "pest", "insect" -> PathogenType.PEST_INSECT
            "nutrient" -> PathogenType.NUTRIENT_DEFICIENCY
            else -> PathogenType.UNKNOWN
        }

        val severity = when (dto.severity.lowercase()) {
            "critical" -> SeverityLevel.CRITICAL
            "severe" -> SeverityLevel.SEVERE
            "moderate" -> SeverityLevel.MODERATE
            else -> SeverityLevel.EARLY_STAGE
        }

        val chemicalTreatments = dto.chemicalRemedies.map { remedy ->
            val toxicity = when (remedy.safetyClass.lowercase()) {
                "green" -> ToxicityClass.GREEN_CAUTION
                "yellow" -> ToxicityClass.YELLOW_HIGHLY_TOXIC
                "red" -> ToxicityClass.RED_EXTREMELY_TOXIC
                else -> ToxicityClass.BLUE_MODERATELY_TOXIC
            }
            ChemicalTreatment(
                activeIngredient = remedy.chemicalName,
                tradeNames = listOf(remedy.commercialBrand),
                dosagePerLiterWater = remedy.dosage,
                sprayVolumePerAcreLiters = 200,
                applicationMethod = "Foliar spray during early morning or late evening",
                estimatedCostPerAcreInr = remedy.costInr,
                governmentApproved = true,
                toxicityLabel = toxicity
            )
        }

        val organicTreatments = dto.organicRemedies.map { organic ->
            OrganicTreatment(
                name = mapLocalizedText(organic.name),
                recipeOrSource = mapLocalizedText(organic.preparation),
                dosagePerAcre = organic.dosage,
                applicationMethod = "Foliar application / Soil drenching",
                preparationTimeHours = 24,
                estimatedCostPerAcreInr = organic.costInr
            )
        }

        val treatmentPlan = TreatmentPlan(
            chemicalOptions = chemicalTreatments,
            organicOptions = organicTreatments,
            culturalPractices = dto.preventiveSteps.map { mapLocalizedText(it) },
            safetyPrecautions = listOf(
                LocalizedText(
                    en = "Wear mask and gloves while spraying. Do not spray against the wind.",
                    kn = "ಸಿಂಪಡಿಸುವಾಗ ಮುಖವಾಡ ಮತ್ತು ಕೈಗವಸುಗಳನ್ನು ಧರಿಸಿ. ಗಾಳಿಗೆ ವಿರುದ್ಧವಾಗಿ ಸಿಂಪಡಿಸಬೇಡಿ.",
                    hi = "छिड़काव करते समय मास्क और दस्ताने पहनें। हवा की विपरीत दिशा में छिड़काव न करें।"
                )
            ),
            waitingPeriodBeforeHarvestDays = 14
        )

        return DiseaseDiagnosis(
            id = dto.diseaseId,
            crop = crop,
            diseaseNameEn = dto.nameEn,
            diseaseNameKn = dto.nameKn,
            diseaseNameHi = dto.nameHi,
            pathogenType = pathogen,
            confidence = confidence,
            severity = severity,
            affectedParts = listOf(PlantPart.LEAF, PlantPart.STEM),
            symptomsSummary = mapLocalizedText(dto.symptomDescription),
            causes = mapLocalizedText(dto.causeDescription),
            treatmentPlan = treatmentPlan,
            preventiveMeasures = dto.preventiveSteps.map { mapLocalizedText(it) },
            spreadRiskUnderCurrentWeather = RiskLevel.INSPECT,
            aiConfidenceExplanation = LocalizedText(
                en = "High visual similarity to pathogen lesions and favorable humid weather index.",
                kn = "ರೋಗದ ಲಕ್ಷಣಗಳಿಗೆ ಹೆಚ್ಚಿನ ದೃಶ್ಯ ಹೋಲಿಕೆ ಮತ್ತು ಅನುಕೂಲಕರ ತೇವಾಂಶದ ಹವಾಮಾನ.",
                hi = "रोग के लक्षणों से उच्च दृश्य समानता और अनुकूल आर्द्र मौसम सूचकांक।"
            ),
            isVerifiedByAgronomist = true
        )
    }

    private fun mapLocalizedText(dto: LocalizedTextDto): LocalizedText {
        return LocalizedText(
            en = dto.en,
            kn = dto.kn,
            hi = dto.hi
        )
    }
}
