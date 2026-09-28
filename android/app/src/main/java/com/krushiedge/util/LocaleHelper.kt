package com.krushiedge.util

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {
    fun setLocale(context: Context, languageCode: String): Context {
        val locale = when (languageCode.lowercase()) {
            "kn" -> Locale("kn", "IN")
            "hi" -> Locale("hi", "IN")
            "en" -> Locale("en", "IN")
            else -> Locale("kn", "IN")
        }
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        @Suppress("DEPRECATION")
        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        return context.createConfigurationContext(config)
    }

    fun getLocale(languageCode: String): Locale {
        return when (languageCode.lowercase()) {
            "kn" -> Locale("kn", "IN")
            "hi" -> Locale("hi", "IN")
            "en" -> Locale("en", "IN")
            else -> Locale("kn", "IN")
        }
    }
}
