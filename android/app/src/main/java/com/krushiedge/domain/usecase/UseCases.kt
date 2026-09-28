package com.krushiedge.domain.usecase

import com.krushiedge.ai.explainability.ExplainableAiEngine
import com.krushiedge.domain.model.CropAnalysisInput
import com.krushiedge.domain.model.CropAnalysisResult
import com.krushiedge.domain.model.CropStage
import com.krushiedge.domain.model.CropType
import com.krushiedge.domain.model.DiseaseDiagnosis
import com.krushiedge.domain.model.ExplainabilityFact
import com.krushiedge.domain.model.Farm
import com.krushiedge.domain.model.Field
import com.krushiedge.domain.model.IrrigationAdvisory
import com.krushiedge.domain.model.LocalizedText
import com.krushiedge.domain.model.MandiPriceInfo
import com.krushiedge.domain.model.PestForecast
import com.krushiedge.domain.repository.CropDoctorRepository
import com.krushiedge.domain.repository.FarmRepository
import com.krushiedge.domain.repository.IrrigationRepository
import com.krushiedge.domain.repository.MarketRepository
import com.krushiedge.domain.repository.OfflineSyncRepository
import com.krushiedge.domain.repository.PestForecastRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

data class CropDiagnosisFullReport(
    val analysisResult: CropAnalysisResult,
    val detailedDiagnosis: DiseaseDiagnosis,
    val explainabilityFacts: List<ExplainabilityFact>,
    val counterfactualAdvice: String
)

class AnalyzeCropUseCase @Inject constructor(
    private val cropDoctorRepository: CropDoctorRepository,
    private val explainableAiEngine: ExplainableAiEngine
) {
    suspend operator fun invoke(input: CropAnalysisInput, farmerLanguage: String = "kn"): Result<CropDiagnosisFullReport> {
        val result = cropDoctorRepository.diagnoseCrop(input)
        return if (result.isSuccess) {
            val analysis = result.getOrThrow()
            val diagnosis = cropDoctorRepository.getDetailedDiagnosis(input.crop, analysis)
            val facts = explainableAiEngine.generateExplanation(input, analysis)
            val counterfactual = explainableAiEngine.getCounterfactualSummary(input.crop, farmerLanguage)

            // Save record to local Room database for offline history
            cropDoctorRepository.saveScanRecord(analysis, input.imagePath, diagnosis)

            Result.success(
                CropDiagnosisFullReport(
                    analysisResult = analysis,
                    detailedDiagnosis = diagnosis,
                    explainabilityFacts = facts,
                    counterfactualAdvice = counterfactual
                )
            )
        } else {
            Result.failure(result.exceptionOrNull() ?: Exception("Unknown AI diagnostic error"))
        }
    }
}

class GetFarmOverviewUseCase @Inject constructor(
    private val farmRepository: FarmRepository
) {
    fun getFarms(): Flow<List<Farm>> = farmRepository.getFarms()
    fun getFields(farmId: String): Flow<List<Field>> = farmRepository.getFieldsForFarm(farmId)
}

class GetIrrigationAdvisoryUseCase @Inject constructor(
    private val irrigationRepository: IrrigationRepository
) {
    suspend operator fun invoke(fieldId: String, crop: CropType, stage: CropStage): IrrigationAdvisory {
        return irrigationRepository.getIrrigationAdvisory(fieldId, crop, stage)
    }

    fun observe(fieldId: String): Flow<IrrigationAdvisory?> {
        return irrigationRepository.observeAdvisory(fieldId)
    }
}

class GetPestForecastUseCase @Inject constructor(
    private val pestRepository: PestForecastRepository
) {
    suspend operator fun invoke(crop: CropType): List<PestForecast> {
        return pestRepository.getPestForecast(crop)
    }
}

class GetMarketIntelligenceUseCase @Inject constructor(
    private val marketRepository: MarketRepository
) {
    operator fun invoke(crop: CropType): Flow<List<MandiPriceInfo>> {
        return marketRepository.getMandiPrices(crop)
    }
}

class VoiceAssistantUseCase @Inject constructor() {
    data class VoiceResponse(
        val recognizedQuery: String,
        val replyText: LocalizedText,
        val audioUtterance: String,
        val suggestedActionRoute: String? = null
    )

    fun processQuery(query: String, crop: CropType, language: String): VoiceResponse {
        val clean = query.lowercase().trim()

        return when {
            clean.contains("ಬೆಂಕಿ ರೋಗ") || clean.contains("blast") || clean.contains("ರೋಗ") || clean.contains("disease") -> {
                VoiceResponse(
                    recognizedQuery = query,
                    replyText = LocalizedText(
                        en = "Finger millet blast risk is High today. Please spray Tricyclazole @ 0.6g/L or apply Pseudomonas bio-fungicide.",
                        kn = "ಇಂದು ರಾಗಿ ಬೆಂಕಿ ರೋಗದ ಸಾಧ್ಯತೆ ಹೆಚ್ಚಾಗಿದೆ. ಮುಂಜಾನೆ ಟ್ರೈಕೋಸೈಕ್ಲಾಜೋಲ್ (0.6 ಗ್ರಾಂ/ಲೀ) ಅಥವಾ ಸ್ಯೂಡೋಮೊನಾಸ್ ಜೈವಿಕ ಔಷಧ ಸಿಂಪಡಿಸಿ.",
                        hi = "रागी ब्लास्ट का खतरा अधिक है। कृपया ट्राइसाइक्लाजोल 0.6 ग्राम/लीटर का छिड़काव करें।"
                    ),
                    audioUtterance = "ಇಂದು ರಾಗಿ ಬೆಂಕಿ ರೋಗದ ಸಾಧ್ಯತೆ ಹೆಚ್ಚಾಗಿದೆ. ಮುಂಜಾನೆ ಟ್ರೈಕೋಸೈಕ್ಲಾಜೋಲ್ ಸಿಂಪಡಿಸಿ.",
                    suggestedActionRoute = "scan"
                )
            }
            clean.contains("ನೀರು") || clean.contains("water") || clean.contains("irrigation") || clean.contains("ಪಂಪ್") -> {
                VoiceResponse(
                    recognizedQuery = query,
                    replyText = LocalizedText(
                        en = "Soil moisture is at 38%. Run your 5HP pump tomorrow morning for 1 hour 45 minutes.",
                        kn = "ಮಣ್ಣಿನಲ್ಲಿ ತೇವಾಂಶ 38% ಕ್ಕೆ ಇಳಿದಿದೆ. ನಾಳೆ ಬೆಳಗ್ಗೆ 6 ಗಂಟೆಗೆ 5HP ಪಂಪ್ ಅನ್ನು 1 ಗಂಟೆ 45 ನಿಮಿಷ ಚಲಾಯಿಸಿ.",
                        hi = "मिट्टी की नमी 38% है। कल सुबह 5HP पंप 1 घंटा 45 मिनट चलाएं।"
                    ),
                    audioUtterance = "ಮಣ್ಣಿನಲ್ಲಿ ತೇವಾಂಶ 38% ಕ್ಕೆ ಇಳಿದಿದೆ. ನಾಳೆ ಬೆಳಗ್ಗೆ ಪಂಪ್ ಚಲಾಯಿಸಿ.",
                    suggestedActionRoute = "irrigation"
                )
            }
            clean.contains("ಬೆಲೆ") || clean.contains("rate") || clean.contains("price") || clean.contains("ಮಂಡಿ") -> {
                VoiceResponse(
                    recognizedQuery = query,
                    replyText = LocalizedText(
                        en = "Mandya APMC is offering ₹3,720/quintal for Ragi today (+₹80 higher). Recommended to sell now.",
                        kn = "ಮಂಡ್ಯ ಮಾರುಕಟ್ಟೆಯಲ್ಲಿ ಇಂದು ರಾಗಿ ದರ ಕ್ವಿಂಟಾಲ್‌ಗೆ ₹3,720 ಇದೆ (₹80 ಏರಿಕೆ). ಮಾರಾಟ ಮಾಡಲು ಸೂಕ್ತ ಸಮಯ.",
                        hi = "मंड्या मंडी में आज रागी का भाव ₹3,720 प्रति क्विंटल है (+₹80 बढ़ोतरी)।"
                    ),
                    audioUtterance = "ಮಂಡ್ಯ ಮಾರುಕಟ್ಟೆಯಲ್ಲಿ ಇಂದು ರಾಗಿ ದರ ಕ್ವಿಂಟಾಲ್‌ಗೆ 3720 ರೂಪಾಯಿ ಇದೆ.",
                    suggestedActionRoute = "market"
                )
            }
            else -> {
                VoiceResponse(
                    recognizedQuery = query,
                    replyText = LocalizedText(
                        en = "I can assist you with Crop Diseases, Smart Irrigation, Pest Forecast, and Mandi Rates. Tap camera to diagnose any leaf photo.",
                        kn = "ನಾನು ಬೆಳೆ ರೋಗ ಪತ್ತೆ, ನೀರಾವರಿ ಸಲಹೆ, ಕೀಟ ಮುನ್ಸೂಚನೆ ಮತ್ತು ಮಂಡಿ ಬೆಲೆಗಳ ಬಗ್ಗೆ ಮಾಹಿತಿ ನೀಡಬಲ್ಲೆ. ಫೋಟೋ ತೆಗೆಯಲು ಕ್ಯಾಮೆರಾ ಒತ್ತಿ.",
                        hi = "मैं फसल रोग, सिंचाई, कीट पूर्वानुमान और मंडी भाव में मदद कर सकता हूँ।"
                    ),
                    audioUtterance = "ನಾನು ಬೆಳೆ ರೋಗ, ನೀರಾವರಿ ಮತ್ತು ಮಂಡಿ ಬೆಲೆಗಳ ಬಗ್ಗೆ ಮಾಹಿತಿ ನೀಡಬಲ್ಲೆ.",
                    suggestedActionRoute = "home"
                )
            }
        }
    }
}
