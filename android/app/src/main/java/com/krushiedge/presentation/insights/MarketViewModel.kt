package com.krushiedge.presentation.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krushiedge.domain.model.CropType
import com.krushiedge.domain.model.MandiPriceInfo
import com.krushiedge.domain.usecase.GetMarketIntelligenceUseCase
import com.krushiedge.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MarketUiState(
    val selectedCrop: CropType = CropType.RAGI,
    val prices: List<MandiPriceInfo> = emptyList(),
    val language: String = "kn",
    val estimatedYieldQuintals: Double = 35.0
)

@HiltViewModel
class MarketViewModel @Inject constructor(
    private val marketUseCase: GetMarketIntelligenceUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MarketUiState(language = sessionManager.getLanguage()))
    val uiState: StateFlow<MarketUiState> = _uiState.asStateFlow()

    init {
        observeLanguage()
        loadPrices()
    }

    private fun observeLanguage() {
        viewModelScope.launch {
            sessionManager.currentLanguage.collect { lang ->
                _uiState.value = _uiState.value.copy(language = lang)
            }
        }
    }

    private fun loadPrices() {
        viewModelScope.launch {
            marketUseCase(_uiState.value.selectedCrop).collect { list ->
                _uiState.value = _uiState.value.copy(prices = list)
            }
        }
    }

    fun onCropChanged(crop: CropType) {
        _uiState.value = _uiState.value.copy(selectedCrop = crop)
        loadPrices()
    }

    fun onYieldChanged(quintals: Double) {
        _uiState.value = _uiState.value.copy(estimatedYieldQuintals = quintals)
    }
}
