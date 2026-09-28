package com.krushiedge

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.krushiedge.presentation.navigation.KrushiAppNavHost
import com.krushiedge.presentation.theme.KrushiEdgeTheme
import com.krushiedge.util.LocaleHelper
import com.krushiedge.util.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val currentLang by sessionManager.currentLanguage.collectAsState()
            val locale = LocaleHelper.getLocale(currentLang)

            val configuration = Configuration(LocalConfiguration.current).apply {
                setLocale(locale)
                setLayoutDirection(locale)
            }

            LocaleHelper.setLocale(LocalContext.current, currentLang)

            CompositionLocalProvider(LocalConfiguration provides configuration) {
                KrushiEdgeTheme {
                    KrushiAppNavHost(sessionManager = sessionManager)
                }
            }
        }
    }
}
