package com.krushiedge.util

import com.krushiedge.domain.model.CropStage
import com.krushiedge.domain.model.CropType
import com.krushiedge.domain.model.SoilType
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Agronomic Decision Engine & Bio-physical Mathematics Calculator.
 *
 * Implements standard agricultural engineering equations:
 * 1. FAO-56 Penman-Monteith Evapotranspiration (ET0)
 * 2. Crop water requirement (ETc = Kc * ET0)
 * 3. Irrigation pump run time computation
 * 4. Growing Degree Days (GDD) for phenology tracking
 * 5. Soil moisture depletion fractions
 */
@Singleton
class AgronomyCalculator @Inject constructor() {

    /**
     * Approximate FAO-56 Penman-Monteith Evapotranspiration (mm/day).
     */
    fun calculateET0(
        temperatureMaxC: Double,
        temperatureMinC: Double,
        humidityAvgPct: Double,
        windSpeedKmH: Double,
        solarRadiationMjM2: Double = 19.5
    ): Double {
        val tempMean = (temperatureMaxC + temperatureMinC) / 2.0
        val windSpeedMs = windSpeedKmH / 3.6

        // Saturation vapor pressure (es)
        val esTmax = 0.6108 * exp((17.27 * temperatureMaxC) / (temperatureMaxC + 237.3))
        val esTmin = 0.6108 * exp((17.27 * temperatureMinC) / (temperatureMinC + 237.3))
        val es = (esTmax + esTmin) / 2.0

        // Actual vapor pressure (ea)
        val ea = es * (humidityAvgPct / 100.0)

        // Slope of vapor pressure curve (Delta)
        val delta = (4098.0 * (0.6108 * exp((17.27 * tempMean) / (tempMean + 237.3)))) /
                ((tempMean + 237.3) * (tempMean + 237.3))

        // Psychrometric constant (gamma) at ~600m MSL (average Deccan plateau elevation)
        val gamma = 0.063

        // Radiation terms
        val rn = 0.77 * solarRadiationMjM2
        val g = 0.0 // Soil heat flux for daily calculation

        val numerator = 0.408 * delta * (rn - g) + gamma * (900.0 / (tempMean + 273.0)) * windSpeedMs * (es - ea)
        val denominator = delta + gamma * (1.0 + 0.34 * windSpeedMs)

        return max(1.5, min(9.0, numerator / denominator))
    }

    /**
     * Get FAO Crop Coefficient (Kc) by stage.
     */
    fun getCropCoefficientKc(crop: CropType, stage: CropStage): Double {
        return when (crop) {
            CropType.RAGI -> when (stage) {
                CropStage.SOWING, CropStage.GERMINATION -> 0.35
                CropStage.VEGETATIVE -> 0.75
                CropStage.FLOWERING, CropStage.GRAIN_FILLING -> 1.15
                CropStage.MATURITY, CropStage.HARVEST -> 0.60
            }
            CropType.GROUNDNUT -> when (stage) {
                CropStage.SOWING, CropStage.GERMINATION -> 0.40
                CropStage.VEGETATIVE -> 0.80
                CropStage.FLOWERING, CropStage.GRAIN_FILLING -> 1.10
                CropStage.MATURITY, CropStage.HARVEST -> 0.65
            }
            CropType.COTTON -> when (stage) {
                CropStage.SOWING, CropStage.GERMINATION -> 0.35
                CropStage.VEGETATIVE -> 0.75
                CropStage.FLOWERING, CropStage.GRAIN_FILLING -> 1.20
                CropStage.MATURITY, CropStage.HARVEST -> 0.70
            }
            CropType.TOMATO -> when (stage) {
                CropStage.SOWING, CropStage.GERMINATION -> 0.45
                CropStage.VEGETATIVE -> 0.85
                CropStage.FLOWERING, CropStage.GRAIN_FILLING -> 1.25
                CropStage.MATURITY, CropStage.HARVEST -> 0.80
            }
            CropType.RICE -> when (stage) {
                CropStage.SOWING, CropStage.GERMINATION -> 1.10
                CropStage.VEGETATIVE -> 1.20
                CropStage.FLOWERING, CropStage.GRAIN_FILLING -> 1.35
                CropStage.MATURITY, CropStage.HARVEST -> 0.95
            }
            else -> 0.80
        }
    }

    /**
     * Calculate Daily Crop Water Need ETc (mm/day).
     */
    fun calculateCropWaterNeed(crop: CropType, stage: CropStage, et0: Double): Double {
        val kc = getCropCoefficientKc(crop, stage)
        return et0 * kc
    }

    /**
     * Compute pump run time hours based on irrigation requirement, area, and pump HP.
     * 1 Acre-mm = ~10,000 liters.
     * 5 HP pump delivers approximately ~30,000 liters per hour (drip/sprinkler efficiency included).
     */
    fun calculatePumpRunHours(
        requiredWaterMm: Double,
        areaAcres: Double,
        pumpHp: Double = 5.0,
        irrigationEfficiency: Double = 0.85
    ): Double {
        if (requiredWaterMm <= 0.0 || areaAcres <= 0.0) return 0.0
        val totalLitersNeeded = requiredWaterMm * 10000.0 * areaAcres / irrigationEfficiency
        val pumpLitersPerHour = pumpHp * 6000.0 // approx 6000 L/hr per HP for typical submersible
        return max(0.25, totalLitersNeeded / pumpLitersPerHour)
    }

    /**
     * Growing Degree Days (GDD) = max(0, ((Tmax + Tmin) / 2) - Tbase).
     */
    fun calculateGDD(tempMaxC: Double, tempMinC: Double, baseTempC: Double = 10.0): Double {
        val avg = (tempMaxC + tempMinC) / 2.0
        return max(0.0, avg - baseTempC)
    }
}
