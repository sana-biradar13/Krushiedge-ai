package com.krushiedge.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krushiedge.data.repository.AuthRepository
import com.krushiedge.data.repository.AuthResult
import com.krushiedge.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── Auth State ────────────────────────────────────────────────────────────────

sealed class AuthScreenState {
    data object Idle : AuthScreenState()
    data object Loading : AuthScreenState()
    data class Success(val userName: String, val language: String) : AuthScreenState()
    data class Error(val message: String) : AuthScreenState()
}

data class AuthFormState(
    val name: String = "",
    val identifier: String = "",
    val password: String = "",
    val selectedLanguage: String = "kn",
    val isPasswordVisible: Boolean = false
)

// ── ViewModel ─────────────────────────────────────────────────────────────────

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _screenState = MutableStateFlow<AuthScreenState>(AuthScreenState.Idle)
    val screenState: StateFlow<AuthScreenState> = _screenState.asStateFlow()

    private val _formState = MutableStateFlow(AuthFormState())
    val formState: StateFlow<AuthFormState> = _formState.asStateFlow()

    // Called once to check persisted session on cold start
    fun checkExistingSession(): Boolean = authRepository.isLoggedIn()

    fun onNameChange(value: String) {
        _formState.value = _formState.value.copy(name = value)
    }

    fun onIdentifierChange(value: String) {
        _formState.value = _formState.value.copy(identifier = value)
    }

    fun onPasswordChange(value: String) {
        _formState.value = _formState.value.copy(password = value)
    }

    fun onLanguageChange(lang: String) {
        _formState.value = _formState.value.copy(selectedLanguage = lang)
    }

    fun togglePasswordVisibility() {
        _formState.value = _formState.value.copy(
            isPasswordVisible = !_formState.value.isPasswordVisible
        )
    }

    fun signUp() {
        val form = _formState.value
        _screenState.value = AuthScreenState.Loading
        viewModelScope.launch {
            val result = authRepository.signUp(
                name = form.name,
                identifier = form.identifier,
                rawPassword = form.password,
                language = form.selectedLanguage
            )
            _screenState.value = when (result) {
                is AuthResult.Success -> AuthScreenState.Success(result.name, result.language)
                is AuthResult.Failure -> AuthScreenState.Error(result.message)
            }
        }
    }

    fun login() {
        val form = _formState.value
        _screenState.value = AuthScreenState.Loading
        viewModelScope.launch {
            val result = authRepository.login(
                identifier = form.identifier,
                rawPassword = form.password
            )
            _screenState.value = when (result) {
                is AuthResult.Success -> AuthScreenState.Success(result.name, result.language)
                is AuthResult.Failure -> AuthScreenState.Error(result.message)
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _screenState.value = AuthScreenState.Idle
        _formState.value = AuthFormState()
    }

    fun clearError() {
        _screenState.value = AuthScreenState.Idle
    }

    fun getStoredLanguage(): String = sessionManager.getLanguage()
}
