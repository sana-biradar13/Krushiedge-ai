package com.krushiedge.domain.repository

import com.krushiedge.domain.model.CropAnalysisInput
import com.krushiedge.domain.model.CropAnalysisResult
import com.krushiedge.domain.model.CropStage
import com.krushiedge.domain.model.CropType
import com.krushiedge.domain.model.DiseaseDiagnosis
import com.krushiedge.domain.model.Farm
import com.krushiedge.domain.model.Field
import com.krushiedge.domain.model.IrrigationAdvisory
import com.krushiedge.domain.model.MandiPriceInfo
import com.krushiedge.domain.model.PestForecast
import com.krushiedge.domain.model.Recommendation
import kotlinx.coroutines.flow.Flow

interface FarmRepository {
    fun getFarms(): Flow<List<Farm>>
    suspend fun getFarmById(id: String): Farm?
    suspend fun saveFarm(farm: Farm)
    fun getFieldsForFarm(farmId: String): Flow<List<Field>>
    suspend fun getFieldById(fieldId: String): Field?
}

interface CropDoctorRepository {
    suspend fun diagnoseCrop(input: CropAnalysisInput): Result<CropAnalysisResult>
    suspend fun getDetailedDiagnosis(crop: CropType, analysisResult: CropAnalysisResult): DiseaseDiagnosis
    suspend fun saveScanRecord(result: CropAnalysisResult, imagePath: String?, diagnosis: DiseaseDiagnosis?)
    fun getRecentScans(): Flow<List<CropAnalysisResult>>
}

interface IrrigationRepository {
    suspend fun getIrrigationAdvisory(fieldId: String, crop: CropType, stage: CropStage): IrrigationAdvisory
    fun observeAdvisory(fieldId: String): Flow<IrrigationAdvisory?>
}

interface PestForecastRepository {
    suspend fun getPestForecast(crop: CropType): List<PestForecast>
}

interface MarketRepository {
    fun getMandiPrices(crop: CropType): Flow<List<MandiPriceInfo>>
    suspend fun refreshPrices(): Result<Unit>
}

interface OfflineSyncRepository {
    fun getPendingSyncCount(): Flow<Int>
    suspend fun syncPendingItems(): Result<Int>
}
