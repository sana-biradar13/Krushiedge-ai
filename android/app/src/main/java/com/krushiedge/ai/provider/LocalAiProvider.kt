package com.krushiedge.ai.provider

import com.krushiedge.ai.local.LocalAiEngine
import com.krushiedge.domain.model.AiMode
import com.krushiedge.domain.model.CropAnalysisInput
import com.krushiedge.domain.model.CropAnalysisResult
import javax.inject.Inject

/**
 * On-device AI provider using TensorFlow Lite.
 *
 * NO NETWORK REQUEST is made during inference.
 * Works in Airplane Mode.
 *
 * Pipeline:
 *   Camera Image → Resize → Normalize → Local Model → Prediction → Confidence
 */
class LocalAiProvider @Inject constructor(
    private val localAiEngine: LocalAiEngine
) : AiProvider {

    override suspend fun analyzeCrop(
        input: CropAnalysisInput
    ): Result<CropAnalysisResult> {
        return try {
            val result = localAiEngine.analyzeCrop(input)
            Result.success(result.copy(aiMode = AiMode.LOCAL))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isAvailable(): Boolean {
        return localAiEngine.isModelLoaded()
    }

    override fun providerName(): String = "LocalAiProvider"
}
