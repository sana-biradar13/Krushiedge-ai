package com.krushiedge.presentation.farm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krushiedge.domain.model.Field
import com.krushiedge.domain.model.FieldGrid
import com.krushiedge.domain.repository.FarmRepository
import com.krushiedge.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FieldUiState(
    val field: Field? = null,
    val selectedGrid: FieldGrid? = null,
    val language: String = "kn"
)

@HiltViewModel
class FieldViewModel @Inject constructor(
    private val farmRepository: FarmRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(FieldUiState(language = sessionManager.getLanguage()))
    val uiState: StateFlow<FieldUiState> = _uiState.asStateFlow()

    init {
        observeLanguage()
        loadField()
    }

    private fun observeLanguage() {
        viewModelScope.launch {
            sessionManager.currentLanguage.collect { lang ->
                _uiState.value = _uiState.value.copy(language = lang)
            }
        }
    }

    private fun loadField() {
        viewModelScope.launch {
            val f = farmRepository.getFieldById("f1")
            _uiState.value = _uiState.value.copy(
                field = f,
                selectedGrid = f?.gridZones?.find { it.gridId == "g5" }
            )
        }
    }

    fun onGridSelected(grid: FieldGrid) {
        _uiState.value = _uiState.value.copy(selectedGrid = grid)
    }
}
