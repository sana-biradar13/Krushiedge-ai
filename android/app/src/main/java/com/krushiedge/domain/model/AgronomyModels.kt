package com.krushiedge.domain.model

/**
 * Rich domain models for Agronomy, Disease Diagnosis, Integrated Pest Management (IPM),
 * Smart Irrigation, APMC Market Intelligence, and Multilingual Explanations.
 */

// ── Disease & Pest Diagnosis ─────────────────────────────

data class DiseaseDiagnosis(
    val id: String,
    val crop: CropType,
    val diseaseNameEn: String,
    val diseaseNameKn: String,
    val diseaseNameHi: String,
    val pathogenType: PathogenType,
    val confidence: Double,
    val severity: SeverityLevel,
    val affectedParts: List<PlantPart>,
    val symptomsSummary: LocalizedText,
    val causes: LocalizedText,
    val treatmentPlan: TreatmentPlan,
    val preventiveMeasures: List<LocalizedText>,
    val spreadRiskUnderCurrentWeather: RiskLevel,
    val aiConfidenceExplanation: LocalizedText,
    val isVerifiedByAgronomist: Boolean = true,
    val detectedAt: Long = System.currentTimeMillis()
)

enum class PathogenType {
    FUNGAL,
    BACTERIAL,
    VIRAL,
    PEST_INSECT,
    NUTRIENT_DEFICIENCY,
    PHYSIOLOGICAL_DISORDER,
    UNKNOWN
}

enum class SeverityLevel {
    EARLY_STAGE,
    MODERATE,
    SEVERE,
    CRITICAL
}

enum class PlantPart {
    LEAF,
    STEM,
    ROOT,
    FLOWER,
    FRUIT_GRAIN,
    WHOLE_PLANT
}

data class TreatmentPlan(
    val chemicalOptions: List<ChemicalTreatment>,
    val organicOptions: List<OrganicTreatment>,
    val culturalPractices: List<LocalizedText>,
    val safetyPrecautions: List<LocalizedText>,
    val waitingPeriodBeforeHarvestDays: Int = 0
)

data class ChemicalTreatment(
    val activeIngredient: String,
    val tradeNames: List<String>,
    val dosagePerLiterWater: String,
    val sprayVolumePerAcreLiters: Int,
    val applicationMethod: String,
    val estimatedCostPerAcreInr: Double,
    val governmentApproved: Boolean = true,
    val toxicityLabel: ToxicityClass = ToxicityClass.BLUE_MODERATELY_TOXIC
)

data class OrganicTreatment(
    val name: LocalizedText,
    val recipeOrSource: LocalizedText,
    val dosagePerAcre: String,
    val applicationMethod: String,
    val preparationTimeHours: Int = 0,
    val estimatedCostPerAcreInr: Double
)

enum class ToxicityClass(val colorHex: String, val label: String) {
    GREEN_CAUTION("#4CAF50", "Green - Slightly Toxic"),
    BLUE_MODERATELY_TOXIC("#2196F3", "Blue - Moderately Toxic"),
    YELLOW_HIGHLY_TOXIC("#FF9800", "Yellow - Highly Toxic"),
    RED_EXTREMELY_TOXIC("#F44336", "Red - Extremely Toxic / Regulated")
}

// ── Smart Irrigation & Water Management ───────────────────

data class IrrigationAdvisory(
    val id: String,
    val fieldId: String,
    val crop: CropType,
    val cropStage: CropStage,
    val soilType: SoilType,
    val currentSoilMoisturePct: Double,
    val targetSoilMoisturePct: Double,
    val referenceEvapotranspirationMmDay: Double,
    val cropCoefficientKc: Double,
    val dailyCropWaterNeedMm: Double,
    val recommendedWaterMm: Double,
    val irrigationRequired: Boolean,
    val pumpRunHours: Double,
    val nextIrrigationWindowStart: Long,
    val nextIrrigationWindowEnd: Long,
    val rainForecastMitigation: String?,
    val expectedWaterSavingsPct: Double,
    val reasoning: LocalizedText,
    val generatedAt: Long = System.currentTimeMillis()
)

// ── Pest Outbreak Forecast ───────────────────────────────

data class PestForecast(
    val id: String,
    val crop: CropType,
    val pestNameEn: String,
    val pestNameKn: String,
    val pestNameHi: String,
    val scientificName: String,
    val currentRisk: RiskLevel,
    val riskTrend7Days: List<DayRiskProjection>,
    val primaryWeatherTrigger: String,
    val earlyWarningSigns: List<LocalizedText>,
    val immediateMonitoringAdvice: LocalizedText,
    val pheromoneTrapRecommended: Boolean = false,
    val trapCountPerAcre: Int = 0,
    val bioControlAgents: List<String> = emptyList()
)

data class DayRiskProjection(
    val dayOffset: Int,
    val dateEpochMs: Long,
    val riskLevel: RiskLevel,
    val predictedTempMax: Double,
    val predictedHumidityAvg: Double,
    val triggerProbability: Double
)

// ── APMC Mandi Market Intelligence ────────────────────────

data class MandiPriceInfo(
    val crop: CropType,
    val variety: String,
    val mandiName: String,
    val district: String,
    val state: String,
    val distanceKm: Double,
    val minPricePerQuintalInr: Double,
    val maxPricePerQuintalInr: Double,
    val modalPricePerQuintalInr: Double,
    val priceChange24hInr: Double,
    val estimatedTransportCostPerQuintalInr: Double,
    val netEffectivePricePerQuintalInr: Double,
    val priceTrend7Days: PriceTrendDirection,
    val recommendation: MandiRecommendation,
    val updatedDate: String
)

enum class PriceTrendDirection {
    RISING,
    STABLE,
    FALLING
}

enum class MandiRecommendation {
    SELL_NOW_HIGHEST_NET,
    HOLD_PRICES_RISING,
    EXPLORE_NEIGHBORING_MANDI,
    STANDARD_RATE
}

// ── Multilingual & Explainability ────────────────────────

data class LocalizedText(
    val en: String,
    val kn: String,
    val hi: String
) {
    fun get(languageCode: String): String {
        return when (languageCode.lowercase()) {
            "kn", "kannada" -> kn.ifBlank { en }
            "hi", "hindi" -> hi.ifBlank { en }
            else -> en
        }
    }
}

data class ExplainabilityFact(
    val title: LocalizedText,
    val description: LocalizedText,
    val evidenceWeightPct: Double,
    val iconType: String,
    val source: String
)
