package com.krushiedge.presentation.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krushiedge.domain.model.CropAnalysisInput
import com.krushiedge.domain.model.CropStage
import com.krushiedge.domain.model.CropType
import com.krushiedge.domain.usecase.AnalyzeCropUseCase
import com.krushiedge.domain.usecase.CropDiagnosisFullReport
import com.krushiedge.util.KrushiTtsManager
import com.krushiedge.util.NetworkMonitor
import com.krushiedge.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ScanUiState(
    val isScanning: Boolean = false,
    val selectedCrop: CropType = CropType.RAGI,
    val selectedStage: CropStage = CropStage.FLOWERING,
    val currentImageUri: String? = null,
    val diagnosisReport: CropDiagnosisFullReport? = null,
    val isResultDialogVisible: Boolean = false,
    val activeTab: TreatmentTab = TreatmentTab.CHEMICAL,
    val isAudioPlaying: Boolean = false,
    val language: String = "kn",
    val error: String? = null
)

enum class TreatmentTab {
    CHEMICAL,
    ORGANIC,
    EXPLANATION,
    PREVENTION
}

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val analyzeCropUseCase: AnalyzeCropUseCase,
    private val networkMonitor: NetworkMonitor,
    private val sessionManager: SessionManager,
    private val ttsManager: KrushiTtsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScanUiState(language = sessionManager.getLanguage()))
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    init {
        observeLanguage()
        observeTts()
    }

    private fun observeLanguage() {
        viewModelScope.launch {
            sessionManager.currentLanguage.collect { lang ->
                _uiState.value = _uiState.value.copy(language = lang)
            }
        }
    }

    private fun observeTts() {
        viewModelScope.launch {
            ttsManager.isSpeaking.collect { speaking ->
                _uiState.value = _uiState.value.copy(isAudioPlaying = speaking)
            }
        }
    }

    fun onCropSelected(crop: CropType) {
        _uiState.value = _uiState.value.copy(selectedCrop = crop)
    }

    fun onStageSelected(stage: CropStage) {
        _uiState.value = _uiState.value.copy(selectedStage = stage)
    }

    fun onTabSelected(tab: TreatmentTab) {
        _uiState.value = _uiState.value.copy(activeTab = tab)
    }

    fun triggerScan(imagePath: String? = null, rawBytes: ByteArray? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isScanning = true, error = null)

            val input = CropAnalysisInput(
                crop = _uiState.value.selectedCrop,
                cropStage = _uiState.value.selectedStage,
                fieldId = "f1",
                gridId = "g5",
                imageData = rawBytes,
                imagePath = imagePath,
                temperature = 31.0,
                humidity = 78.0,
                rainfall = 0.0,
                soilMoisture = 38.0,
                ndvi = 0.52
            )

            val result = analyzeCropUseCase(input, _uiState.value.language)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isScanning = false,
                    diagnosisReport = result.getOrThrow(),
                    isResultDialogVisible = true
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isScanning = false,
                    error = result.exceptionOrNull()?.message ?: "Diagnosis failed"
                )
            }
        }
    }

    fun dismissResult() {
        ttsManager.stop()
        _uiState.value = _uiState.value.copy(isResultDialogVisible = false)
    }

    fun toggleAudio() {
        if (_uiState.value.isAudioPlaying) {
            ttsManager.stop()
        } else {
            val report = _uiState.value.diagnosisReport ?: return
            val lang = _uiState.value.language
            val speech = if (lang == "kn") {
                "ಕಂಡುಬಂದ ರೋಗ: ${report.diagnosis.diseaseNameKn}. ತೀವ್ರತೆ: ${report.diagnosis.severity}. " +
                "ಸಾವಯವ ಪರಿಹಾರ: ${report.diagnosis.organicCureKn.take(150)}. " +
                "ರಾಸಾಯನಿಕ ಪರಿಹಾರ: ${report.diagnosis.chemicalCureKn.take(150)}"
            } else if (lang == "hi") {
                "पहचानी गई बीमारी: ${report.diagnosis.diseaseNameEn}. " +
                "जैविक उपचार: ${report.diagnosis.organicCureEn.take(150)}. " +
                "रासायनिक उपचार: ${report.diagnosis.chemicalCureEn.take(150)}"
            } else {
                "Detected Disease: ${report.diagnosis.diseaseNameEn}. Severity: ${report.diagnosis.severity}. " +
                "Organic remedy: ${report.diagnosis.organicCureEn.take(150)}. " +
                "Chemical remedy: ${report.diagnosis.chemicalCureEn.take(150)}"
            }
            ttsManager.speak(speech, lang)
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.stop()
    }
}
