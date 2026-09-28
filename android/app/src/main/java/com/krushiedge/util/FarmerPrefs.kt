package com.krushiedge.util

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Stores farmer name entered during onboarding.
 * Persists across app launches using SharedPreferences.
 */
@Singleton
class FarmerPrefs @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("krushi_farmer_prefs", Context.MODE_PRIVATE)

    var farmerName: String
        get() = prefs.getString(KEY_FARMER_NAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_FARMER_NAME, value).apply()

    val isOnboardingComplete: Boolean
        get() = farmerName.isNotBlank()

    fun clear() = prefs.edit().clear().apply()

    companion object {
        private const val KEY_FARMER_NAME = "farmer_name"
    }
}
