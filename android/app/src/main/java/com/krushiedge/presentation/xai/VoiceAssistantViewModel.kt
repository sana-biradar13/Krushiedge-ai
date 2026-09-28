package com.krushiedge.presentation.xai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krushiedge.domain.model.CropType
import com.krushiedge.domain.usecase.VoiceAssistantUseCase
import com.krushiedge.util.FarmerPrefs
import com.krushiedge.util.KrushiTtsManager
import com.krushiedge.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VoiceUiMessage(
    val sender: String, // "FARMER" or "KRUSHI_AI"
    val text: String,
    val audioUtterance: String? = null,
    val actionRoute: String? = null
)

data class VoiceAssistantUiState(
    val isListening: Boolean = false,
    val isSpeaking: Boolean = false,
    val language: String = "kn",
    val messages: List<VoiceUiMessage> = emptyList()
)

@HiltViewModel
class VoiceAssistantViewModel @Inject constructor(
    private val voiceUseCase: VoiceAssistantUseCase,
    private val farmerPrefs: FarmerPrefs,
    private val sessionManager: SessionManager,
    private val ttsManager: KrushiTtsManager
) : ViewModel() {

    private val farmerName: String
        get() = sessionManager.getUserName().ifBlank { farmerPrefs.farmerName.trim().ifBlank { "ರೈತ ಮಿತ್ರರೇ" } }

    private val _uiState = MutableStateFlow(
        VoiceAssistantUiState(
            language = sessionManager.getLanguage(),
            messages = listOf(
                VoiceUiMessage(
                    sender = "KRUSHI_AI",
                    text = "ನಮಸ್ಕಾರ ${sessionManager.getUserName().ifBlank { farmerPrefs.farmerName.trim().ifBlank { "ರೈತ ಮಿತ್ರರೇ" } }} ಅವರೇ! ನಿಮ್ಮ ಹೊಲದ ರೋಗಗಳು, ನೀರಾವರಿ ಅಥವಾ ಮಂಡಿ ಬೆಲೆಗಳ ಬಗ್ಗೆ ಏನಾದರೂ ಕೇಳಿ.",
                    audioUtterance = "ನಮಸ್ಕಾರ ${sessionManager.getUserName().ifBlank { farmerPrefs.farmerName.trim().ifBlank { "ರೈತ ಮಿತ್ರರೇ" } }} ಅವರೇ! ನಿಮ್ಮ ಹೊಲದ ಬಗ್ಗೆ ಏನಾದರೂ ಕೇಳಿ."
                )
            )
        )
    )
    val uiState: StateFlow<VoiceAssistantUiState> = _uiState.asStateFlow()

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
                _uiState.value = _uiState.value.copy(isSpeaking = speaking)
            }
        }
    }

    fun onStartListening() {
        _uiState.value = _uiState.value.copy(isListening = true)
    }

    fun submitQuery(userPrompt: String) {
        viewModelScope.launch {
            val updated = _uiState.value.messages.toMutableList()
            updated.add(VoiceUiMessage(sender = "FARMER", text = userPrompt))

            val response = voiceUseCase.processQuery(userPrompt, CropType.RAGI, _uiState.value.language)
            val replyText = response.replyText.get(_uiState.value.language)
            val utterance = response.audioUtterance ?: replyText

            updated.add(
                VoiceUiMessage(
                    sender = "KRUSHI_AI",
                    text = replyText,
                    audioUtterance = utterance,
                    actionRoute = response.suggestedActionRoute
                )
            )

            _uiState.value = _uiState.value.copy(
                isListening = false,
                messages = updated
            )

            // Speak out response in selected language
            ttsManager.speak(utterance, _uiState.value.language)
        }
    }

    fun speakMessage(message: VoiceUiMessage) {
        val textToSpeak = message.audioUtterance ?: message.text
        ttsManager.speak(textToSpeak, _uiState.value.language)
    }

    fun stopSpeaking() {
        ttsManager.stop()
        _uiState.value = _uiState.value.copy(isSpeaking = false)
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.stop()
    }
}
