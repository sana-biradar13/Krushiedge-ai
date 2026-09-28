package com.krushiedge.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.krushiedge.data.local.entity.AdvisoryEntity
import com.krushiedge.data.local.entity.FarmEntity
import com.krushiedge.data.local.entity.FieldEntity
import com.krushiedge.data.local.entity.MandiPriceEntity
import com.krushiedge.data.local.entity.OfflineSyncQueueEntity
import com.krushiedge.data.local.entity.ScanRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmDao {
    @Query("SELECT * FROM farms ORDER BY updatedAt DESC")
    fun getAllFarms(): Flow<List<FarmEntity>>

    @Query("SELECT * FROM farms WHERE id = :farmId LIMIT 1")
    suspend fun getFarmById(farmId: String): FarmEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFarm(farm: FarmEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(farms: List<FarmEntity>)
}

@Dao
interface FieldDao {
    @Query("SELECT * FROM fields WHERE farmId = :farmId")
    fun getFieldsForFarm(farmId: String): Flow<List<FieldEntity>>

    @Query("SELECT * FROM fields WHERE id = :fieldId LIMIT 1")
    suspend fun getFieldById(fieldId: String): FieldEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertField(field: FieldEntity)
}

@Dao
interface ScanRecordDao {
    @Query("SELECT * FROM scan_records ORDER BY timestamp DESC")
    fun getAllScans(): Flow<List<ScanRecordEntity>>

    @Query("SELECT * FROM scan_records WHERE fieldId = :fieldId ORDER BY timestamp DESC")
    fun getScansForField(fieldId: String): Flow<List<ScanRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: ScanRecordEntity)

    @Query("SELECT COUNT(*) FROM scan_records WHERE syncStatus = 'PENDING'")
    fun getPendingSyncCount(): Flow<Int>
}

@Dao
interface AdvisoryDao {
    @Query("SELECT * FROM advisories ORDER BY generatedAt DESC")
    fun getAllAdvisories(): Flow<List<AdvisoryEntity>>

    @Query("SELECT * FROM advisories WHERE fieldId = :fieldId ORDER BY generatedAt DESC")
    fun getAdvisoriesForField(fieldId: String): Flow<List<AdvisoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdvisory(advisory: AdvisoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(advisories: List<AdvisoryEntity>)
}

@Dao
interface OfflineSyncDao {
    @Query("SELECT * FROM offline_sync_queue ORDER BY createdAt ASC")
    suspend fun getPendingQueue(): List<OfflineSyncQueueEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun enqueue(item: OfflineSyncQueueEntity): Long

    @Query("DELETE FROM offline_sync_queue WHERE queueId = :queueId")
    suspend fun dequeue(queueId: Long)

    @Query("DELETE FROM offline_sync_queue")
    suspend fun clearAll()
}

@Dao
interface MandiDao {
    @Query("SELECT * FROM mandi_prices WHERE crop = :cropName ORDER BY distanceKm ASC")
    fun getPricesForCrop(cropName: String): Flow<List<MandiPriceEntity>>

    @Query("SELECT * FROM mandi_prices ORDER BY distanceKm ASC")
    fun getAllMandiPrices(): Flow<List<MandiPriceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrices(prices: List<MandiPriceEntity>)
}

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): com.krushiedge.data.local.entity.UserEntity?

    @Query("SELECT * FROM users WHERE identifier = :identifier LIMIT 1")
    suspend fun getUserByIdentifier(identifier: String): com.krushiedge.data.local.entity.UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: com.krushiedge.data.local.entity.UserEntity)

    @Query("UPDATE users SET preferredLanguage = :language WHERE id = :id")
    suspend fun updateUserLanguage(id: String, language: String)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUser(id: String)
}

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather_cache WHERE id = :id LIMIT 1")
    fun observeWeather(id: String = "primary_weather"): Flow<com.krushiedge.data.local.entity.WeatherCacheEntity?>

    @Query("SELECT * FROM weather_cache WHERE id = :id LIMIT 1")
    suspend fun getWeather(id: String = "primary_weather"): com.krushiedge.data.local.entity.WeatherCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: com.krushiedge.data.local.entity.WeatherCacheEntity)
}

