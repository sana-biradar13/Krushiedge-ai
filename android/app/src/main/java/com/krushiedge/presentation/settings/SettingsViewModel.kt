package com.krushiedge.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krushiedge.domain.model.AiMode
import com.krushiedge.domain.repository.OfflineSyncRepository
import com.krushiedge.util.FarmerPrefs
import com.krushiedge.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val farmerName: String = "",
    val selectedLanguage: String = "kn",
    val preferredAiMode: AiMode = AiMode.HYBRID,
    val isDownloadingModel: Boolean = false,
    val localModelVersion: String = "v2.4-lite (42 MB)",
    val isModelDownloaded: Boolean = true,
    val pendingSyncCount: Int = 0,
    val syncStatusMessage: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val syncRepository: OfflineSyncRepository,
    private val farmerPrefs: FarmerPrefs,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            farmerName = sessionManager.getUserName().ifBlank { farmerPrefs.farmerName },
            selectedLanguage = sessionManager.getLanguage()
        )
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observeSync()
    }

    fun updateFarmerName(newName: String) {
        farmerPrefs.farmerName = newName
        val currentUserId = sessionManager.getUserId() ?: "u_default"
        val currentIdent = sessionManager.getIdentifier()
        val currentLang = sessionManager.getLanguage()
        val currentToken = sessionManager.getAuthToken() ?: "token_default"
        sessionManager.saveSession(currentUserId, newName, currentIdent, currentLang, currentToken)
        _uiState.value = _uiState.value.copy(farmerName = newName)
    }

    private fun observeSync() {
        viewModelScope.launch {
            syncRepository.getPendingSyncCount().collect { count ->
                _uiState.value = _uiState.value.copy(pendingSyncCount = count)
            }
        }
    }

    fun setLanguage(lang: String) {
        sessionManager.setLanguage(lang)
        _uiState.value = _uiState.value.copy(selectedLanguage = lang)
    }

    fun setAiMode(mode: AiMode) {
        _uiState.value = _uiState.value.copy(preferredAiMode = mode)
    }

    fun syncNow() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(syncStatusMessage = "Syncing with cloud...")
            val res = syncRepository.syncPendingItems()
            if (res.isSuccess) {
                _uiState.value = _uiState.value.copy(syncStatusMessage = "Sync complete (${res.getOrThrow()} items uploaded).")
            } else {
                _uiState.value = _uiState.value.copy(syncStatusMessage = "Sync failed: ${res.exceptionOrNull()?.message}")
            }
        }
    }
}
