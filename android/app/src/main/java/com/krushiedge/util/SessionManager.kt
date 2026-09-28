package com.krushiedge.util

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("krushi_auth_session", Context.MODE_PRIVATE)

    private val _isLoggedIn = MutableStateFlow(isUserLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentLanguage = MutableStateFlow(getLanguage())
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    fun isUserLoggedIn(): Boolean {
        return !getUserId().isNullOrBlank() && !getAuthToken().isNullOrBlank()
    }

    fun saveSession(userId: String, name: String, identifier: String, language: String, token: String) {
        prefs.edit()
            .putString(KEY_USER_ID, userId)
            .putString(KEY_USER_NAME, name)
            .putString(KEY_IDENTIFIER, identifier)
            .putString(KEY_LANGUAGE, language)
            .putString(KEY_AUTH_TOKEN, token)
            .putLong(KEY_LOGIN_TIME, System.currentTimeMillis())
            .apply()
        _isLoggedIn.value = true
        _currentLanguage.value = language
    }

    fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)

    fun getUserName(): String = prefs.getString(KEY_USER_NAME, "") ?: ""

    fun getIdentifier(): String = prefs.getString(KEY_IDENTIFIER, "") ?: ""

    fun getLanguage(): String = prefs.getString(KEY_LANGUAGE, "kn") ?: "kn"

    fun setLanguage(language: String) {
        prefs.edit().putString(KEY_LANGUAGE, language).apply()
        _currentLanguage.value = language
    }

    fun getAuthToken(): String? = prefs.getString(KEY_AUTH_TOKEN, null)

    fun clearSession() {
        val lang = getLanguage()
        prefs.edit().clear().apply()
        // Retain language choice across logout
        setLanguage(lang)
        _isLoggedIn.value = false
    }

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_IDENTIFIER = "identifier"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_LOGIN_TIME = "login_time"
    }
}
