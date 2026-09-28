package com.krushiedge.presentation.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krushiedge.domain.model.CropType
import com.krushiedge.domain.model.PestForecast
import com.krushiedge.domain.usecase.GetPestForecastUseCase
import com.krushiedge.util.KrushiTtsManager
import com.krushiedge.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PestUiState(
    val selectedCrop: CropType = CropType.RAGI,
    val forecasts: List<PestForecast> = emptyList(),
    val language: String = "kn",
    val isTtsActive: Boolean = false
)

@HiltViewModel
class PestForecastViewModel @Inject constructor(
    private val pestUseCase: GetPestForecastUseCase,
    private val sessionManager: SessionManager,
    private val ttsManager: KrushiTtsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PestUiState(language = sessionManager.getLanguage()))
    val uiState: StateFlow<PestUiState> = _uiState.asStateFlow()

    init {
        observeLanguage()
        observeTts()
        loadPests()
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

    private fun loadPests() {
        viewModelScope.launch {
            val list = pestUseCase(_uiState.value.selectedCrop)
            _uiState.value = _uiState.value.copy(forecasts = list)
        }
    }

    fun onCropChanged(crop: CropType) {
        _uiState.value = _uiState.value.copy(selectedCrop = crop)
        loadPests()
    }

    fun toggleTts() {
        if (_uiState.value.isTtsActive) {
            ttsManager.stop()
        } else {
            val forecasts = _uiState.value.forecasts
            val lang = _uiState.value.language
            val text = if (lang == "kn") {
                val pestNames = forecasts.joinToString(", ") { it.pestNameKn }
                "ಕೀಟ ಮುನ್ಸೂಚನೆ ವರದಿ: ಪ್ರಸ್ತುತ ಹವಾಮಾನದಲ್ಲಿ ${pestNames} ಕೀಟಗಳ ಬಾಧೆ ಹೆಚ್ಚಾಗುವ ಸಾಧ್ಯತೆ ಇದೆ. ತಕ್ಷಣ ಹೊಲವನ್ನು ಪರಿಶೀಲಿಸಿ."
            } else if (lang == "hi") {
                val pestNames = forecasts.joinToString(", ") { it.pestNameEn }
                "कीट पूर्वानुमान रिपोर्ट: वर्तमान मौसम में ${pestNames} का प्रकोप बढ़ने की संभावना है। कृपया खेत का निरीक्षण करें।"
            } else {
                val pestNames = forecasts.joinToString(", ") { it.pestNameEn }
                "Pest forecast alert: High risk of ${pestNames} due to current humidity levels. Please inspect your field."
            }
            ttsManager.speak(text, lang)
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.stop()
    }
}
