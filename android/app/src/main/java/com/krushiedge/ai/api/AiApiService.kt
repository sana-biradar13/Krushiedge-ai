package com.krushiedge.ai.api

import com.krushiedge.ai.model.AiAnalysisRequestDto
import com.krushiedge.ai.model.AiAnalysisResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Structured Retrofit API interface for Cloud AI Backend.
 */
interface AiApiService {

    @POST("v1/agronomy/analyze-crop")
    suspend fun analyzeCrop(
        @Body request: AiAnalysisRequestDto,
        @Header("X-Client-Version") clientVersion: String = "2.4.0",
        @Header("X-Language") language: String = "kn"
    ): Response<AiAnalysisResponseDto>
}
