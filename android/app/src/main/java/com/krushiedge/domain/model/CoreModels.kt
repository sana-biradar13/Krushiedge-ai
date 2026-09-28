package com.krushiedge.domain.model

import kotlinx.serialization.Serializable

/**
 * Core domain models for KrushiEdge AI.
 * These are the stable data contracts used across all layers.
 */

// ── Farm & Field ─────────────────────────────────────────

data class Farm(
    val id: String,
    val name: String,
    val village: String,
    val crop: CropType,
    val areaAcres: Double,
    val cropStage: CropStage,
    val soilType: SoilType? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class Field(
    val id: String,
    val farmId: String,
    val name: String,
    val boundaryPoints: List<GeoPoint> = emptyList(),
    val areaAcres: Double,
    val gridZones: List<FieldGrid> = emptyList()
)

data class FieldGrid(
    val gridId: String,
    val fieldId: String,
    val row: Int,
    val col: Int,
    val centerLat: Double,
    val centerLon: Double,
    val risk: RiskLevel = RiskLevel.INSUFFICIENT,
    val confidence: Double = 0.0,
    val timestamp: Long = 0L,
    val evidence: List<EvidenceType> = emptyList(),
    val dataSource: DataSource = DataSource.NONE
)

data class GeoPoint(
    val latitude: Double,
    val longitude: Double
)

// ── Enums ────────────────────────────────────────────────

enum class CropType(val code: String) {
    RAGI("ragi"),
    RICE("rice"),
    JOWAR("jowar"),
    MAIZE("maize"),
    GROUNDNUT("groundnut"),
    COTTON("cotton"),
    SUGARCANE("sugarcane"),
    TOMATO("tomato"),
    ONION("onion"),
    SUNFLOWER("sunflower")
}

enum class CropStage(val code: String) {
    SOWING("sowing"),
    GERMINATION("germination"),
    VEGETATIVE("vegetative"),
    FLOWERING("flowering"),
    GRAIN_FILLING("grain_filling"),
    MATURITY("maturity"),
    HARVEST("harvest")
}

enum class SoilType(val code: String) {
    RED("red"),
    BLACK("black"),
    ALLUVIAL("alluvial"),
    LATERITE("laterite"),
    SANDY("sandy"),
    CLAY("clay"),
    LOAMY("loamy")
}

/**
 * Risk levels with strict ordering.
 * Color + Icon + Text must always be used together.
 */
enum class RiskLevel(val priority: Int) {
    NORMAL(0),
    MONITOR(1),
    INSPECT(2),
    URGENT(3),
    INSUFFICIENT(-1)
}

enum class EvidenceType(val code: String) {
    VEGETATION_TREND("vegetation_trend"),
    SOIL_MOISTURE("soil_moisture"),
    WEATHER("weather"),
    CROP_PHOTO("crop_photo"),
    FARMER_OBSERVATION("farmer_observation"),
    SATELLITE("satellite"),
    HISTORICAL("historical")
}

enum class DataSource {
    OBSERVED,
    ESTIMATED,
    REFINED,
    FARMER_EVIDENCE,
    DEMO,
    NONE
}

// ── AI ───────────────────────────────────────────────────

enum class AiMode {
    LOCAL,
    REMOTE,
    HYBRID,
    OFFLINE
}

enum class StressLevel(val code: String) {
    NONE("none"),
    LOW("low"),
    MODERATE("moderate"),
    HIGH("high"),
    SEVERE("severe")
}

// ── Urgency ──────────────────────────────────────────────

enum class Urgency {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}
