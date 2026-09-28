package com.krushiedge.ai.provider

import com.krushiedge.domain.model.CropAnalysisInput
import com.krushiedge.domain.model.CropAnalysisResult

/**
 * Core AI provider abstraction.
 *
 * The UI and domain layers depend ONLY on this interface.
 * They never know whether the analysis is running locally,
 * remotely, or in hybrid mode.
 *
 * Implementations:
 * - RemoteAiProvider: Uses AI API (Gemini, etc.)
 * - LocalAiProvider: Uses on-device TFLite model
 * - HybridAiProvider: Tries remote first, falls back to local
 */
interface AiProvider {

    /**
     * Analyze crop health from available inputs.
     *
     * @param input Structured crop analysis input
     * @return Result wrapping success or failure
     */
    suspend fun analyzeCrop(
        input: CropAnalysisInput
    ): Result<CropAnalysisResult>

    /**
     * Check if this provider is currently available.
     */
    suspend fun isAvailable(): Boolean

    /**
     * Get the provider name for logging/diagnostics.
     */
    fun providerName(): String
}
