package com.krushiedge.ai.provider

import com.krushiedge.ai.api.AiApiService
import com.krushiedge.ai.mapper.AiDtoMapper
import com.krushiedge.ai.validation.AiResponseValidator
import com.krushiedge.domain.model.AiMode
import com.krushiedge.domain.model.CropAnalysisInput
import com.krushiedge.domain.model.CropAnalysisResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Remote AI Provider.
 * Sends multi-modal crop telemetry, NDVI, and imagery to cloud models.
 * Applies safety validation & hallucination filters on response.
 */
@Singleton
class RemoteAiProvider @Inject constructor(
    private val apiService: AiApiService,
    private val dtoMapper: AiDtoMapper,
    private val validator: AiResponseValidator
) : AiProvider {

    override suspend fun analyzeCrop(input: CropAnalysisInput): Result<CropAnalysisResult> = withContext(Dispatchers.IO) {
        try {
            val requestDto = dtoMapper.mapInputToDto(input)
            val response = apiService.analyzeCrop(requestDto)

            if (response.isSuccessful && response.body() != null) {
                val validation = validator.validateAndSanitize(response.body()!!)
                val domainResult = dtoMapper.mapDtoToCropAnalysisResult(
                    validation.sanitizedResponse,
                    input.crop,
                    AiMode.REMOTE
                )
                Result.success(domainResult)
            } else {
                Result.failure(Exception("Remote AI API failed with HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isAvailable(): Boolean {
        // Can be pinged or verified against connection state
        return true
    }

    override fun providerName(): String = "RemoteAiProvider (Cloud Gemini Agronomy)"
}
