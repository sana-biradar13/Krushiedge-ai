package com.krushiedge.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krushiedge.data.repository.WeatherRepository
import com.krushiedge.domain.model.ConnectivityStatus
import com.krushiedge.domain.model.CropStage
import com.krushiedge.domain.model.CropType
import com.krushiedge.domain.model.Farm
import com.krushiedge.domain.model.Field
import com.krushiedge.domain.model.RiskLevel
import com.krushiedge.domain.usecase.GetFarmOverviewUseCase
import com.krushiedge.util.FarmerPrefs
import com.krushiedge.util.KrushiTtsManager
import com.krushiedge.util.NetworkMonitor
import com.krushiedge.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val currentFarm: Farm? = null,
    val fields: List<Field> = emptyList(),
    val connectivity: ConnectivityStatus = ConnectivityStatus.ONLINE,
    val currentLanguage: String = "kn",
    val todayWeatherTemp: Double = 29.5,
    val todayHumidity: Int = 68,
    val rainForecastPct: Int = 15,
    val windSpeedKmH: Double = 9.2,
    val weatherCondition: String = "ಭಾಗಶಃ ಮೋಡ (Partly Cloudy)",
    val isWeatherLoading: Boolean = false,
    val sprayAdvisory: String = "ಸಿಂಪರಣೆಗೆ ಸೂಕ್ತ ವಾತಾವರಣವಿದೆ",
    val overallFarmRisk: RiskLevel = RiskLevel.OPTIMAL,
    val activeAlertCount: Int = 1,
    val pendingSyncItems: Int = 0,
    val isTtsSpeaking: Boolean = false,
    val farmerDisplayName: String = ""
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val farmOverviewUseCase: GetFarmOverviewUseCase,
    private val weatherRepository: WeatherRepository,
    private val networkMonitor: NetworkMonitor,
    private val farmerPrefs: FarmerPrefs,
    private val sessionManager: SessionManager,
    private val ttsManager: KrushiTtsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState(
            currentLanguage = sessionManager.getLanguage(),
            farmerDisplayName = sessionManager.getUserName().ifBlank { farmerPrefs.farmerName }
        )
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
        observeNetwork()
        observeWeather()
        observeLanguage()
        observeTts()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            farmOverviewUseCase.getFarms().collect { farms ->
                val enteredName = sessionManager.getUserName().ifBlank { farmerPrefs.farmerName.trim() }
                val displayName = if (enteredName.isNotBlank()) {
                    "$enteredName ಅವರ ಹೊಲ ($enteredName's Farm)"
                } else {
                    "ರೈತ ಮಿತ್ರ ಹೊಲ (My Farm)"
                }

                val baseFarm = farms.firstOrNull() ?: Farm(
                    id = "farm_01",
                    name = displayName,
                    village = "ಮಂಡ್ಯ (Mandya)",
                    crop = CropType.RAGI,
                    areaAcres = 3.5,
                    cropStage = CropStage.FLOWERING
                )
                val primaryFarm = baseFarm.copy(name = displayName)
                _uiState.value = _uiState.value.copy(
                    currentFarm = primaryFarm,
                    farmerDisplayName = enteredName
                )

                farmOverviewUseCase.getFields(primaryFarm.id).collect { fields ->
                    _uiState.value = _uiState.value.copy(fields = fields)
                }
            }
        }
    }

    private fun observeWeather() {
        viewModelScope.launch {
            // Load initial cached or remote weather
            try {
                _uiState.value = _uiState.value.copy(isWeatherLoading = true)
                val weather = weatherRepository.getCurrentWeather()
                _uiState.value = _uiState.value.copy(
                    todayWeatherTemp = weather.temperatureC,
                    todayHumidity = weather.humidityPct,
                    rainForecastPct = weather.rainProbabilityPct,
                    windSpeedKmH = weather.windSpeedKmH,
                    weatherCondition = weather.conditionText,
                    isWeatherLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isWeatherLoading = false)
            }

            // Observe continuous weather changes
            weatherRepository.observeWeather().collect { weather ->
                if (weather != null) {
                    _uiState.value = _uiState.value.copy(
                        todayWeatherTemp = weather.temperatureC,
                        todayHumidity = weather.humidityPct,
                        rainForecastPct = weather.rainProbabilityPct,
                        windSpeedKmH = weather.windSpeedKmH,
                        weatherCondition = weather.conditionText
                    )
                }
            }
        }
    }

    fun refreshWeather() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isWeatherLoading = true)
            weatherRepository.refreshWeather()
            _uiState.value = _uiState.value.copy(isWeatherLoading = false)
        }
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkMonitor.status.collect { status ->
                _uiState.value = _uiState.value.copy(connectivity = status)
            }
        }
    }

    private fun observeLanguage() {
        viewModelScope.launch {
            sessionManager.currentLanguage.collect { lang ->
                _uiState.value = _uiState.value.copy(currentLanguage = lang)
            }
        }
    }

    private fun observeTts() {
        viewModelScope.launch {
            ttsManager.isSpeaking.collect { isSpeaking ->
                _uiState.value = _uiState.value.copy(isTtsSpeaking = isSpeaking)
            }
        }
    }

    fun toggleLanguage() {
        val nextLang = when (_uiState.value.currentLanguage) {
            "kn" -> "hi"
            "hi" -> "en"
            else -> "kn"
        }
        sessionManager.setLanguage(nextLang)
    }

    fun readAdvisoryAloud() {
        if (_uiState.value.isTtsSpeaking) {
            ttsManager.stop()
        } else {
            val state = _uiState.value
            val farmName = state.currentFarm?.name ?: "ಕೃಷಿ ಹೊಲ"
            val textToSpeak = if (state.currentLanguage == "kn") {
                "ನಮಸ್ಕಾರ ${state.farmerDisplayName}. ಇಂದಿನ ಹವಾಮಾನ: ಉಷ್ಣಾಂಶ ${state.todayWeatherTemp.toInt()} ಡಿಗ್ರಿ ಸೆಲ್ಸಿಯಸ್. ತೇವಾಂಶ ${state.todayHumidity} ಶೇಕಡಾ. ಮಳೆ ಸಂಭವ ${state.rainForecastPct} ಶೇಕಡಾ. ಗಾಳಿಯ ವೇಗ ಗಂಟೆಗೆ ${state.windSpeedKmH.toInt()} ಕಿಲೋಮೀಟರ್. ನಿಮ್ಮ ಬೆಳೆ ರಾಗಿ ಹೂಬಿಡುವ ಹಂತದಲ್ಲಿದೆ. ಇಂದು ಕೀಟನಾಶಕ ಸಿಂಪರಣೆಗೆ ಉತ್ತಮ ವಾತಾವರಣವಿದೆ."
            } else if (state.currentLanguage == "hi") {
                "नमस्ते ${state.farmerDisplayName}. आज का मौसम: तापमान ${state.todayWeatherTemp.toInt()} डिग्री सेल्सियस। आर्द्रता ${state.todayHumidity} प्रतिशत। बारिश की संभावना ${state.rainForecastPct} प्रतिशत। आपकी रागी की फसल फूल आने की अवस्था में है।"
            } else {
                "Hello ${state.farmerDisplayName}. Today's weather: Temperature ${state.todayWeatherTemp.toInt()} degrees Celsius. Humidity ${state.todayHumidity} percent. Rain probability ${state.rainForecastPct} percent. Wind speed ${state.windSpeedKmH.toInt()} kilometers per hour. Your Ragi crop is at flowering stage. Safe conditions for field operations."
            }
            ttsManager.speak(textToSpeak, state.currentLanguage)
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.stop()
    }
}
