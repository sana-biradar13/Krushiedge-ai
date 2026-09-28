package com.krushiedge.presentation.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krushiedge.domain.model.CropStage
import com.krushiedge.domain.model.CropType
import com.krushiedge.domain.model.IrrigationAdvisory
import com.krushiedge.domain.usecase.GetIrrigationAdvisoryUseCase
import com.krushiedge.util.KrushiTtsManager
import com.krushiedge.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IrrigationUiState(
    val isLoading: Boolean = false,
    val advisory: IrrigationAdvisory? = null,
    val selectedMotorHp: Double = 5.0,
    val language: String = "kn",
    val isTtsActive: Boolean = false
)

@HiltViewModel
class IrrigationViewModel @Inject constructor(
    private val irrigationUseCase: GetIrrigationAdvisoryUseCase,
    private val sessionManager: SessionManager,
    private val ttsManager: KrushiTtsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(IrrigationUiState(language = sessionManager.getLanguage()))
    val uiState: StateFlow<IrrigationUiState> = _uiState.asStateFlow()

    init {
        observeLanguage()
        observeTts()
        loadAdvisory()
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
                _uiState.value = _uiState.value.copy(isTtsActive = speaking)
            }
        }
    }

    private fun loadAdvisory() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val advisory = irrigationUseCase("f1", CropType.RAGI, CropStage.FLOWERING)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                advisory = advisory
            )
        }
    }

    fun setMotorHp(hp: Double) {
        _uiState.value = _uiState.value.copy(selectedMotorHp = hp)
    }

    fun toggleTts() {
        if (_uiState.value.isTtsActive) {
            ttsManager.stop()
        } else {
            val adv = _uiState.value.advisory ?: return
            val lang = _uiState.value.language
            val text = if (lang == "kn") {
                "ಸ್ಮಾರ್ಟ್ ನೀರಾವರಿ ಸಲಹೆ: ನಿಮ್ಮ ಬೆಳೆಗೆ ${adv.waterRequirementLiters.toInt()} ಲೀಟರ್ ನೀರು ಅಗತ್ಯವಿದೆ. ನಿಮ್ಮ ${_uiState.value.selectedMotorHp.toInt()} ಹೆಚ್‌ಪಿ ಪಂಪ್ ಅನ್ನು ${adv.recommendedPumpRunHours} ಗಂಟೆಗಳ ಕಾಲ ಚಲಾಯಿಸಲು ಶಿಫಾರಸು ಮಾಡಲಾಗಿದೆ."
            } else if (lang == "hi") {
                "स्मार्ट सिंचाई सलाह: आपकी फसल को ${adv.waterRequirementLiters.toInt()} लीटर पानी की आवश्यकता है। मोटर को ${adv.recommendedPumpRunHours} घंटे चलाने की सलाह दी जाती है।"
            } else {
                "Smart Irrigation Advisory: Crop requires ${adv.waterRequirementLiters.toInt()} liters of water. Recommended pump run time is ${adv.recommendedPumpRunHours} hours."
            }
            ttsManager.speak(text, lang)
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.stop()
    }
}
