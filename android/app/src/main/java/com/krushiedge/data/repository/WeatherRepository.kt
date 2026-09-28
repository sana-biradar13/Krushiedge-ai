package com.krushiedge.data.repository

import com.krushiedge.data.local.dao.WeatherDao
import com.krushiedge.data.local.entity.WeatherCacheEntity
import com.krushiedge.data.remote.WeatherApiClient
import com.krushiedge.data.remote.WeatherData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val weatherDao: WeatherDao,
    private val weatherApiClient: WeatherApiClient
) {
    fun observeWeather(): Flow<WeatherCacheEntity?> {
        return weatherDao.observeWeather("primary_weather")
    }

    suspend fun getCurrentWeather(lat: Double = 12.5226, lon: Double = 76.8976): WeatherCacheEntity {
        val cached = weatherDao.getWeather("primary_weather")
        val isCacheFresh = cached != null && (System.currentTimeMillis() - cached.lastUpdatedMillis < 30 * 60 * 1000)

        if (isCacheFresh && cached != null) {
            return cached
        }

        val remoteResult = weatherApiClient.fetchWeather(lat, lon)
        if (remoteResult.isSuccess) {
            val data = remoteResult.getOrThrow()
            val entity = WeatherCacheEntity(
                id = "primary_weather",
                latitude = data.latitude,
                longitude = data.longitude,
                temperatureC = data.temperatureC,
                humidityPct = data.humidityPct,
                precipitationMm = data.precipitationMm,
                rainProbabilityPct = data.rainProbabilityPct,
                windSpeedKmH = data.windSpeedKmH,
                weatherCode = data.weatherCode,
                conditionText = "${data.conditionTextKn} | ${data.conditionTextEn}",
                hourlyJson = data.hourlyJson,
                dailyJson = data.dailyJson,
                lastUpdatedMillis = System.currentTimeMillis()
            )
            weatherDao.insertWeather(entity)
            return entity
        }

        // Return cached or default baseline
        return cached ?: WeatherCacheEntity(
            id = "primary_weather",
            latitude = lat,
            longitude = lon,
            temperatureC = 29.5,
            humidityPct = 65,
            precipitationMm = 0.0,
            rainProbabilityPct = 15,
            windSpeedKmH = 9.2,
            weatherCode = 1,
            conditionText = "ಭಾಗಶಃ ಮೋಡ (Partly Cloudy)",
            hourlyJson = "{}",
            dailyJson = "{}",
            lastUpdatedMillis = System.currentTimeMillis()
        )
    }

    suspend fun refreshWeather(lat: Double = 12.5226, lon: Double = 76.8976): Result<WeatherData> {
        val remoteResult = weatherApiClient.fetchWeather(lat, lon)
        if (remoteResult.isSuccess) {
            val data = remoteResult.getOrThrow()
            val entity = WeatherCacheEntity(
                id = "primary_weather",
                latitude = data.latitude,
                longitude = data.longitude,
                temperatureC = data.temperatureC,
                humidityPct = data.humidityPct,
                precipitationMm = data.precipitationMm,
                rainProbabilityPct = data.rainProbabilityPct,
                windSpeedKmH = data.windSpeedKmH,
                weatherCode = data.weatherCode,
                conditionText = "${data.conditionTextKn} | ${data.conditionTextEn}",
                hourlyJson = data.hourlyJson,
                dailyJson = data.dailyJson,
                lastUpdatedMillis = System.currentTimeMillis()
            )
            weatherDao.insertWeather(entity)
        }
        return remoteResult
    }
}
