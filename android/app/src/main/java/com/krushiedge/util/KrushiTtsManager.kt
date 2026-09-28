package com.krushiedge.util

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KrushiTtsManager @Inject constructor(
    @ApplicationContext private val context: Context
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var pendingText: String? = null
    private var pendingLang: String? = null

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    init {
        initializeTts()
    }

    private fun initializeTts() {
        // Try Google TTS engine first for best regional language (Kannada/Hindi) support
        try {
            tts = TextToSpeech(context, this, "com.google.android.tts")
        } catch (e: Exception) {
            tts = TextToSpeech(context, this)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                }
            })

            // Execute pending speech if requested before init finished
            pendingText?.let { text ->
                val lang = pendingLang ?: "kn"
                pendingText = null
                pendingLang = null
                speak(text, lang)
            }
        }
    }

    fun speak(text: String, languageCode: String = "kn") {
        if (!isInitialized || tts == null) {
            pendingText = text
            pendingLang = languageCode
            return
        }

        val locale = when (languageCode.lowercase()) {
            "kn" -> Locale("kn", "IN")
            "hi" -> Locale("hi", "IN")
            "en" -> Locale("en", "IN")
            else -> Locale("kn", "IN")
        }

        val langResult = tts?.setLanguage(locale)
        if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
            // If regional voice not installed, try English with fallback
            tts?.setLanguage(Locale.ENGLISH)
        }

        // Clean markdown tags and formatting
        val cleanText = text
            .replace(Regex("\\*\\*|\\*|#|_|`"), "")
            .replace(Regex("http\\S+"), "")
            .replace(Regex("[•➔✓•]"), " ")
            .trim()

        if (cleanText.isNotBlank()) {
            val params = Bundle().apply {
                putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC)
                putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
            }
            val utteranceId = "krushi_tts_${System.currentTimeMillis()}"
            tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
        }
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
