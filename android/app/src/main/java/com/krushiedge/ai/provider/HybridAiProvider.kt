package com.krushiedge.ai.provider

import com.krushiedge.domain.model.AiMode
import com.krushiedge.domain.model.CropAnalysisInput
import com.krushiedge.domain.model.CropAnalysisResult
import com.krushiedge.util.NetworkMonitor
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Hybrid AI Provider.
 * Automatically selects between Edge on-device inference (LocalAiProvider) and Cloud inference (RemoteAiProvider).
 *
 * Routing Rules:
 * 1. If offline / weak connection -> Immediately runs on LocalAiProvider (Zero latency delay).
 * 2. If online -> Attempts Remote Cloud AI with 3.5s timeout.
 * 3. On Remote timeout or error -> Seamlessly falls back to LocalAiProvider without failing.
 */
@Singleton
class HybridAiProvider @Inject constructor(
    private val localAiProvider: LocalAiProvider,
    private val remoteAiProvider: RemoteAiProvider,
    private val networkMonitor: NetworkMonitor
) : AiProvider {

    override suspend fun analyzeCrop(input: CropAnalysisInput): Result<CropAnalysisResult> {
        val isOnline = networkMonitor.isOnline()

        if (isOnline) {
            // Try Remote AI with graceful 4-second timeout
            val remoteResult = withTimeoutOrNull(4000L) {
                remoteAiProvider.analyzeCrop(input)
            }

            if (remoteResult != null && remoteResult.isSuccess) {
                return remoteResult
            }
        }

        // Fallback to local on-device inference
        val localResult = localAiProvider.analyzeCrop(input)
        return if (localResult.isSuccess) {
            // Flag as Hybrid / Local fallback
            Result.success(localResult.getOrThrow().copy(aiMode = if (isOnline) AiMode.HYBRID else AiMode.LOCAL))
        } else {
            localResult
        }
    }

    override suspend fun isAvailable(): Boolean {
        return localAiProvider.isAvailable() || remoteAiProvider.isAvailable()
    }

    override fun providerName(): String = "HybridAiProvider (Edge + Cloud Fallback)"
}
