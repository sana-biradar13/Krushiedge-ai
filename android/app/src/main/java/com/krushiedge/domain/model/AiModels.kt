package com.krushiedge.domain.model

/**
 * AI-specific domain models.
 * Used by the AI provider abstraction and agronomic decision engine.
 */

data class CropAnalysisInput(
    val crop: CropType,
    val cropStage: CropStage,
    val fieldId: String,
    val gridId: String? = null,
    val imageData: ByteArray? = null,
    val imagePath: String? = null,
    val ndvi: Double? = null,
    val ndviTrend: Double? = null,
    val soilMoisture: Double? = null,
    val rainfall: Double? = null,
    val temperature: Double? = null,
    val humidity: Double? = null,
    val historicalObservations: List<Observation> = emptyList()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CropAnalysisInput) return false
        return crop == other.crop &&
                cropStage == other.cropStage &&
                fieldId == other.fieldId &&
                gridId == other.gridId &&
                imagePath == other.imagePath
    }

    override fun hashCode(): Int {
        var result = crop.hashCode()
        result = 31 * result + cropStage.hashCode()
        result = 31 * result + fieldId.hashCode()
        result = 31 * result + (gridId?.hashCode() ?: 0)
        result = 31 * result + (imagePath?.hashCode() ?: 0)
        return result
    }
}

data class CropAnalysisResult(
    val crop: CropType,
    val stressLevel: StressLevel,
    val diseaseRisk: Double,
    val waterStressRisk: Double,
    val nutrientRisk: Double,
    val confidence: Double,
    val evidence: List<EvidenceType>,
    val modelVersion: String,
    val aiMode: AiMode,
    val inferenceTimeMs: Long = 0L,
    val timestamp: Long = System.currentTimeMillis()
)

data class Observation(
    val id: String,
    val fieldId: String,
    val gridId: String? = null,
    val timestamp: Long,
    val ndvi: Double? = null,
    val soilMoisture: Double? = null,
    val temperature: Double? = null,
    val rainfall: Double? = null,
    val stressLevel: StressLevel? = null,
    val confidence: Double? = null,
    val source: DataSource = DataSource.OBSERVED,
    val notes: String? = null
)

data class Recommendation(
    val id: String,
    val fieldId: String,
    val gridId: String? = null,
    val action: String,
    val reason: String,
    val urgency: Urgency,
    val confidence: Double,
    val evidence: List<EvidenceDetail>,
    val timestamp: Long = System.currentTimeMillis(),
    val isAbstention: Boolean = false,
    val safetyDisclaimer: String? = null
)

data class EvidenceDetail(
    val type: EvidenceType,
    val description: String,
    val value: String? = null,
    val trend: String? = null,
    val contributionWeight: Double = 0.0
)

data class WeatherData(
    val temperature: Double,
    val humidity: Double,
    val rainfallProbability: Double,
    val windSpeed: Double,
    val forecastTimestamp: Long,
    val source: String,
    val isStale: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

data class SatelliteData(
    val ndvi: Double,
    val ndre: Double? = null,
    val ndwi: Double? = null,
    val acquisitionDate: Long,
    val cloudPercentage: Double,
    val source: String = "Sentinel-2",
    val isDemo: Boolean = false
)

// ── Prediction (stored after AI analysis) ────────────────

data class Prediction(
    val id: String,
    val fieldId: String,
    val gridId: String? = null,
    val crop: CropType,
    val stressLevel: StressLevel,
    val diseaseRisk: Double,
    val waterStressRisk: Double,
    val nutrientRisk: Double,
    val confidence: Double,
    val evidence: List<EvidenceType>,
    val modelVersion: String,
    val aiMode: AiMode,
    val inferenceTimeMs: Long,
    val timestamp: Long = System.currentTimeMillis()
)

// ── Network Status ───────────────────────────────────────

enum class ConnectivityStatus {
    ONLINE,
    SYNCING,
    OFFLINE,
    SYNC_PENDING,
    DATA_STALE
}

// ── Model Version ────────────────────────────────────────

data class ModelVersion(
    val modelId: String,
    val version: String,
    val sha256: String,
    val sizeBytes: Long,
    val createdAt: Long,
    val minAppVersion: String,
    val status: ModelStatus
)

enum class ModelStatus {
    ACTIVE,
    PENDING,
    ROLLBACK,
    FAILED
}
