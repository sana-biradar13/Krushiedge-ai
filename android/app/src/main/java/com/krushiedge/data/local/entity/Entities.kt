package com.krushiedge.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "farms")
data class FarmEntity(
    @PrimaryKey val id: String,
    val name: String,
    val village: String,
    val crop: String,
    val areaAcres: Double,
    val cropStage: String,
    val soilType: String?,
    val latitude: Double?,
    val longitude: Double?,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(tableName = "fields")
data class FieldEntity(
    @PrimaryKey val id: String,
    val farmId: String,
    val name: String,
    val areaAcres: Double,
    val boundaryPointsJson: String,
    val gridZonesJson: String
)

@Entity(tableName = "scan_records")
data class ScanRecordEntity(
    @PrimaryKey val id: String,
    val fieldId: String,
    val crop: String,
    val cropStage: String,
    val imagePath: String?,
    val diseaseNameEn: String?,
    val diseaseNameKn: String?,
    val diseaseNameHi: String?,
    val confidence: Double,
    val stressLevel: String,
    val aiMode: String,
    val isOffline: Boolean,
    val syncStatus: String, // PENDING, SYNCED, FAILED
    val timestamp: Long
)

@Entity(tableName = "advisories")
data class AdvisoryEntity(
    @PrimaryKey val id: String,
    val fieldId: String,
    val type: String, // IRRIGATION, PEST, DISEASE, NUTRIENT, HARVEST
    val titleEn: String,
    val titleKn: String,
    val titleHi: String,
    val detailsJson: String,
    val urgency: String,
    val isActioned: Boolean = false,
    val generatedAt: Long
)

@Entity(tableName = "offline_sync_queue")
data class OfflineSyncQueueEntity(
    @PrimaryKey(autoGenerate = true) val queueId: Long = 0,
    val operationType: String, // UPLOAD_SCAN, SYNC_FARM, REPORT_PEST
    val payloadJson: String,
    val retryCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "mandi_prices")
data class MandiPriceEntity(
    @PrimaryKey val id: String,
    val crop: String,
    val variety: String,
    val mandiName: String,
    val district: String,
    val state: String,
    val distanceKm: Double,
    val modalPriceInr: Double,
    val minPriceInr: Double,
    val maxPriceInr: Double,
    val priceChange24hInr: Double,
    val updatedDate: String
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val identifier: String, // Phone or email
    val passwordHash: String,
    val salt: String,
    val preferredLanguage: String, // kn, hi, en
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "weather_cache")
data class WeatherCacheEntity(
    @PrimaryKey val id: String = "primary_weather",
    val latitude: Double,
    val longitude: Double,
    val temperatureC: Double,
    val humidityPct: Int,
    val precipitationMm: Double,
    val rainProbabilityPct: Int,
    val windSpeedKmH: Double,
    val weatherCode: Int,
    val conditionText: String,
    val hourlyJson: String,
    val dailyJson: String,
    val lastUpdatedMillis: Long
)

