package com.krushiedge.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val titleKn: String,
    val titleEn: String,
    val icon: ImageVector
) {
    data object Home : Screen("home", "ಮುಖಪುಟ", "Home", Icons.Default.Home)
    data object Scan : Screen("scan", "ಬೆಳೆ ಡಾಕ್ಟರ್", "Crop Doctor", Icons.Default.CameraAlt)
    data object Irrigation : Screen("irrigation", "ನೀರಾವರಿ", "Irrigation", Icons.Default.Opacity)
    data object PestForecast : Screen("pest", "ಕೀಟ ಮುನ್ಸೂಚನೆ", "Pest Risk", Icons.Default.BugReport)
    data object Market : Screen("market", "ಮಂಡಿ ಬೆಲೆ", "Mandi Rates", Icons.Default.Storefront)
    data object FieldDetail : Screen("field_detail", "ಹೊಲದ ವಲಯ", "Field Zoning", Icons.Default.Agriculture)
    data object VoiceAssistant : Screen("voice", "AI ಧ್ವನಿ ಸಹಾಯಕ", "Voice AI", Icons.Default.RecordVoiceOver)
    data object Settings : Screen("settings", "ಸೆಟ್ಟಿಂಗ್ಸ್", "Settings", Icons.Default.Settings)
    data object Login : Screen("login", "ಲಾಗಿನ್", "Login", Icons.Default.Agriculture)
    data object SignUp : Screen("signup", "ಸೈನ್ ಅಪ್", "Sign Up", Icons.Default.Agriculture)
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Scan,
    Screen.Irrigation,
    Screen.PestForecast,
    Screen.Market
)
