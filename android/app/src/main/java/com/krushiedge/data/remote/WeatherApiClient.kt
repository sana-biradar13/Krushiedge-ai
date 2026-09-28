package com.krushiedge.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

data class WeatherData(
    val latitude: Double,
    val longitude: Double,
    val temperatureC: Double,
    val humidityPct: Int,
    val precipitationMm: Double,
    val rainProbabilityPct: Int,
    val windSpeedKmH: Double,
    val weatherCode: Int,
    val conditionTextKn: String,
    val conditionTextEn: String,
    val conditionTextHi: String,
    val isSafeToSpray: Boolean,
    val sprayAdvisoryKn: String,
    val sprayAdvisoryEn: String,
    val hourlyJson: String,
    val dailyJson: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Singleton
class WeatherApiClient @Inject constructor() {

    suspend fun fetchWeather(
        lat: Double = 12.5226, // Default Mandya, Karnataka coordinates
        lon: Double = 76.8976
    ): Result<WeatherData> = withContext(Dispatchers.IO) {
        try {
            val urlString = "https://api.open-meteo.com/v1/forecast?" +
                    "latitude=$lat&longitude=$lon" +
                    "&current=temperature_2m,relative_humidity_2m,precipitation,weather_code,wind_speed_10m" +
                    "&hourly=temperature_2m,relative_humidity_2m,precipitation_probability,weather_code" +
                    "&daily=weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max,precipitation_sum" +
                    "&timezone=Asia%2FKolkata"

            val url = URL(urlString)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 8000
                readTimeout = 8000
                setRequestProperty("Accept", "application/json")
                setRequestProperty("User-Agent", "KrushiEdgeAI-Android/2.0")
            }

            if (connection.responseCode == 200) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val responseText = reader.use { it.readText() }
                connection.disconnect()

                val json = JSONObject(responseText)
                val current = json.getJSONObject("current")
                val daily = json.optJSONObject("daily")
                val hourly = json.optJSONObject("hourly")

                val temp = current.optDouble("temperature_2m", 28.5)
                val humidity = current.optInt("relative_humidity_2m", 68)
                val precipitation = current.optDouble("precipitation", 0.0)
                val weatherCode = current.optInt("weather_code", 0)
                val windSpeed = current.optDouble("wind_speed_10m", 8.5)

                // Get max rain prob for today from daily array if available
                val rainProbMax = if (daily != null && daily.has("precipitation_probability_max")) {
                    val arr = daily.getJSONArray("precipitation_probability_max")
                    if (arr.length() > 0) arr.optInt(0, 10) else 10
                } else 10

                val conditionKn = mapWeatherCodeToKn(weatherCode)
                val conditionEn = mapWeatherCodeToEn(weatherCode)
                val conditionHi = mapWeatherCodeToHi(weatherCode)

                // Agricultural spray safety logic
                val isSafe = windSpeed < 15.0 && rainProbMax < 35 && precipitation < 1.0
                val sprayKn = if (isSafe) {
                    "ಸಿಂಪರಣೆಗೆ ಸೂಕ್ತ ವಾತಾವರಣವಿದೆ (ಗಾಳಿಯ ವೇಗ: ${windSpeed.toInt()} km/h, ಮಳೆ ಸಂಭವ: $rainProbMax%)"
                } else {
                    "ಸಿಂಪರಣೆಯನ್ನು ಮುಂದೂಡಿ (ಹೆಚ್ಚು ಗಾಳಿ ಅಥವಾ ಮಳೆಯ ಸಾಧ್ಯತೆ ಇದೆ)"
                }
                val sprayEn = if (isSafe) {
                    "Safe for chemical/organic spraying (Wind: ${windSpeed.toInt()} km/h, Rain chance: $rainProbMax%)"
                } else {
                    "Avoid spraying today (High wind or risk of rain wash-off)"
                }

                val weatherData = WeatherData(
                    latitude = lat,
                    longitude = lon,
                    temperatureC = temp,
                    humidityPct = humidity,
                    precipitationMm = precipitation,
                    rainProbabilityPct = rainProbMax,
                    windSpeedKmH = windSpeed,
                    weatherCode = weatherCode,
                    conditionTextKn = conditionKn,
                    conditionTextEn = conditionEn,
                    conditionTextHi = conditionHi,
                    isSafeToSpray = isSafe,
                    sprayAdvisoryKn = sprayKn,
                    sprayAdvisoryEn = sprayEn,
                    hourlyJson = hourly?.toString() ?: "{}",
                    dailyJson = daily?.toString() ?: "{}",
                    timestamp = System.currentTimeMillis()
                )
                Result.success(weatherData)
            } else {
                Result.failure(Exception("HTTP ${connection.responseCode}: ${connection.responseMessage}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapWeatherCodeToKn(code: Int): String = when (code) {
        0 -> "ಶುಭ್ರ ಆಕಾಶ (Clear Sky)"
        1, 2 -> "ಭಾಗಶಃ ಮೋಡ (Partly Cloudy)"
        3 -> "ಮೋಡ ಕವಿದ ವಾತಾವರಣ (Overcast)"
        45, 48 -> "ದಟ್ಟ ಮಂಜು (Foggy)"
        51, 53, 55 -> "ತುಂತುರು ಮಳೆ (Drizzle)"
        61, 63, 65 -> "ಸಾಧಾರಣ ಮಳೆ (Rain)"
        71, 73, 75 -> "ಹಿಮಪಾತ (Snow)"
        80, 81, 82 -> "ಭಾರಿ ಮಳೆ (Heavy Showers)"
        95, 96, 99 -> "ಗುಡುಗು ಸಿಡಿಲಿನ ಮಳೆ (Thunderstorm)"
        else -> "ಸಾಧಾರಣ ವಾತಾವರಣ (Fair Weather)"
    }

    private fun mapWeatherCodeToEn(code: Int): String = when (code) {
        0 -> "Clear Sky"
        1, 2 -> "Partly Cloudy"
        3 -> "Overcast"
        45, 48 -> "Foggy"
        51, 53, 55 -> "Drizzle"
        61, 63, 65 -> "Rain"
        71, 73, 75 -> "Snow"
        80, 81, 82 -> "Heavy Rain"
        95, 96, 99 -> "Thunderstorm"
        else -> "Fair Weather"
    }

    private fun mapWeatherCodeToHi(code: Int): String = when (code) {
        0 -> "साफ मौसम (Clear Sky)"
        1, 2 -> "हल्के बादल (Partly Cloudy)"
        3 -> "घने बादल (Overcast)"
        45, 48 -> "कोहरा (Foggy)"
        51, 53, 55 -> "बूंदाबांदी (Drizzle)"
        61, 63, 65 -> "बारिश (Rain)"
        71, 73, 75 -> "बर्फबारी (Snow)"
        80, 81, 82 -> "भारी बारिश (Heavy Rain)"
        95, 96, 99 -> "गरज के साथ बारिश (Thunderstorm)"
        else -> "सामान्य मौसम (Fair Weather)"
    }
}
