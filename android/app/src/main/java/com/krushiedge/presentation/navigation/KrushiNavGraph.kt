package com.krushiedge.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.krushiedge.presentation.auth.AuthViewModel
import com.krushiedge.presentation.auth.LoginScreen
import com.krushiedge.presentation.auth.SignUpScreen
import com.krushiedge.presentation.camera.ScanScreen
import com.krushiedge.presentation.farm.FieldDetailScreen
import com.krushiedge.presentation.home.HomeScreen
import com.krushiedge.presentation.insights.IrrigationScreen
import com.krushiedge.presentation.insights.MarketScreen
import com.krushiedge.presentation.insights.PestForecastScreen
import com.krushiedge.presentation.settings.SettingsScreen
import com.krushiedge.presentation.xai.VoiceAssistantScreen
import com.krushiedge.util.SessionManager
import javax.inject.Inject

@Composable
fun KrushiAppNavHost(
    navController: NavHostController = rememberNavController(),
    sessionManager: SessionManager? = null
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Start at Home if session is active, else at Login
    val startDestination = if (sessionManager?.isUserLoggedIn() == true) {
        Screen.Home.route
    } else {
        Screen.Login.route
    }

    // Auth screens and Scan don't show bottom nav
    val authRoutes = setOf(Screen.Login.route, Screen.SignUp.route, Screen.Scan.route)

    Scaffold(
        bottomBar = {
            if (currentRoute !in authRoutes) {
                KrushiBottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            // ── Auth Flow ──────────────────────────────────────────────────
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = { _ ->
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToSignUp = {
                        navController.navigate(Screen.SignUp.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Screen.SignUp.route) {
                SignUpScreen(
                    onSignUpSuccess = { _ ->
                        navController.navigate(Screen.Home.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.SignUp.route) { inclusive = true }
                        }
                    }
                )
            }

            // ── Main App Flow ──────────────────────────────────────────────
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToScan = { navController.navigate(Screen.Scan.route) },
                    onNavigateToIrrigation = { navController.navigate(Screen.Irrigation.route) },
                    onNavigateToPest = { navController.navigate(Screen.PestForecast.route) },
                    onNavigateToMarket = { navController.navigate(Screen.Market.route) },
                    onNavigateToFieldDetail = { navController.navigate(Screen.FieldDetail.route) },
                    onNavigateToVoice = { navController.navigate(Screen.VoiceAssistant.route) }
                )
            }

            composable(Screen.Scan.route) {
                ScanScreen(onNavigateBack = { navController.popBackStack() })
            }

            composable(Screen.Irrigation.route) { IrrigationScreen() }

            composable(Screen.PestForecast.route) { PestForecastScreen() }

            composable(Screen.Market.route) { MarketScreen() }

            composable(Screen.FieldDetail.route) {
                FieldDetailScreen(onNavigateBack = { navController.popBackStack() })
            }

            composable(Screen.VoiceAssistant.route) {
                VoiceAssistantScreen(
                    onNavigateToRoute = { route -> navController.navigate(route) }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onSwitchAccount = {
                        sessionManager?.clearSession()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

