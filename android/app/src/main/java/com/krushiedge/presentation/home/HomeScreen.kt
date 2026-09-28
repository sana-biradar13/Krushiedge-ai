package com.krushiedge.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.krushiedge.domain.model.AiMode
import com.krushiedge.domain.model.RiskLevel
import com.krushiedge.presentation.components.AiModeBadge
import com.krushiedge.presentation.components.AudioPlayerBar
import com.krushiedge.presentation.components.KrushiTopBar
import com.krushiedge.presentation.components.RiskLevelBadge
import com.krushiedge.presentation.components.StatCard
import com.krushiedge.presentation.theme.KrushiGreenDark
import com.krushiedge.presentation.theme.KrushiGreenPrimary
import com.krushiedge.presentation.theme.RiskUrgent

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToScan: () -> Unit,
    onNavigateToIrrigation: () -> Unit,
    onNavigateToPest: () -> Unit,
    onNavigateToMarket: () -> Unit,
    onNavigateToFieldDetail: () -> Unit,
    onNavigateToVoice: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val isKn = state.currentLanguage == "kn"

    Scaffold(
        topBar = {
            KrushiTopBar(
                title = "KRUSHIEDGE AI",
                subtitle = "ನಿಮ್ಮ ಹೊಲದ AI ಸಹಾಯಕ (Mandya)",
                connectivity = state.connectivity,
                pendingSyncCount = state.pendingSyncItems,
                currentLanguage = state.currentLanguage,
                onLanguageClick = { viewModel.toggleLanguage() },
                onVoiceClick = onNavigateToVoice
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Audio Explanation Banner ──
            item {
                AudioPlayerBar(
                    isPlaying = state.isTtsSpeaking,
                    label = if (isKn) "ಇಂದಿನ ಬೆಳೆ ಸಲಹೆ ಆಲಿಸಿ (ಕನ್ನಡ)" else if (state.currentLanguage == "hi") "आज की कृषि सलाह सुनें (हिंदी)" else "Listen Today's Crop Advisory",
                    onTogglePlay = { viewModel.readAdvisoryAloud() }
                )
            }

            // ── Hero Farm Health Status Card ──
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = state.currentFarm?.name ?: "ನನ್ನ ಹೊಲ (My Farm)",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "ರಾಗಿ (Finger Millet) • 3.5 ಎಕರೆ • ಹೂವಾಡುವ ಹಂತ",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            RiskLevelBadge(risk = state.overallFarmRisk)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // AI Alert Callout
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFFEBEE),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFCDD2))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Alert",
                                    tint = RiskUrgent,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isKn) "ಬ್ಲಾಸ್ಟ್ ಶಿಲೀಂಧ್ರ ಎಚ್ಚರಿಕೆ (ವಲಯ B)" else "Blast Fungal Alert in Sector B",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFFB71C1C)
                                    )
                                    Text(
                                        text = if (isKn) "ತೇವಾಂಶ 72% ಹೆಚ್ಚಿರುವುದರಿಂದ ತ್ವರಿತ ಪರಿಶೀಲನೆ ಅಗತ್ಯ" else "High humidity triggers 68% risk; inspect immediately.",
                                        fontSize = 12.sp,
                                        color = Color(0xFFC62828)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            AiModeBadge(mode = AiMode.HYBRID)
                            Text(
                                text = "ವಲಯ ನಕ್ಷೆ ನೋಡಿ ➔",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = KrushiGreenPrimary,
                                modifier = Modifier.clickable { onNavigateToFieldDetail() }
                            )
                        }
                    }
                }
            }

            // ── Quick Action Hero: Scan with Crop Doctor ──
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onNavigateToScan() },
                    shape = RoundedCornerShape(16.dp),
                    color = KrushiGreenDark,
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Scan",
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isKn) "ಬೆಳೆ ಡಾಕ್ಟರ್ AI ಸ್ಕ್ಯಾನ್" else "Scan Leaf with AI Doctor",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = if (isKn) "ಫೋಟೋ ತೆಗೆದು ರೋಗ ಪತ್ತೆ ಹಚ್ಚಿ (ಇಂಟರ್ನೆಟ್ ಇಲ್ಲದಿದ್ದರೂ)" else "Instant diagnosis works 100% offline",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Open",
                            tint = Color.White
                        )
                    }
                }
            }

            // ── Weather & Microclimate Telemetry Grid ──
            item {
                Text(
                    text = if (isKn) "ಇಂದಿನ ಹವಾಮಾನ ಮತ್ತು ಮಣ್ಣಿನ ಸ್ಥಿತಿ" else "Today's Microclimate & Soil",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = if (isKn) "ತಾಪಮಾನ" else "Temp",
                        value = "${state.todayWeatherTemp.toInt()}°C",
                        subtitle = "ಬಿಸಿಲು",
                        icon = Icons.Default.Thermostat,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = if (isKn) "ಗಾಳಿಯ ತೇವಾಂಶ" else "Humidity",
                        value = "${state.todayHumidity}%",
                        subtitle = "ಹೆಚ್ಚಿನ ಮಟ್ಟ",
                        icon = Icons.Default.WaterDrop,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = if (isKn) "ಮಳೆ ಸಾಧ್ಯತೆ" else "Rain",
                        value = "${state.rainForecastPct}%",
                        subtitle = "ಮಳೆ ಇಲ್ಲ",
                        icon = Icons.Default.Cloud,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ── Key Agri Modules Grid ──
            item {
                Text(
                    text = if (isKn) "ಸ್ಮಾರ್ಟ್ ಕೃಷಿ ಸೇವೆಗಳು" else "Smart Agricultural Modules",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Smart Irrigation Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToIrrigation() },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFFE1F5FE), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Opacity,
                                    contentDescription = "Irrigation",
                                    tint = Color(0xFF0288D1),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isKn) "ನೀರಾವರಿ ಸಲಹೆ" else "Irrigation",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = if (isKn) "ನಾಳೆ 1.8 ಗಂ ಪಂಪ್" else "Run pump 1.8h",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Pest Risk Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToPest() },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFFFFF3E0), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BugReport,
                                    contentDescription = "Pest",
                                    tint = Color(0xFFF57C00),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isKn) "ಕೀಟ ಮುನ್ಸೂಚನೆ" else "Pest Outbreak",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = if (isKn) "ಲದ್ದಿ ಹುಳು ಎಚ್ಚರಿಕೆ" else "Armyworm risk",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // APMC Mandi Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToMarket() },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Storefront,
                                    contentDescription = "Market",
                                    tint = Color(0xFF388E3C),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isKn) "ಮಂಡಿ ಬೆಲೆ" else "Mandi Rate",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "₹3,720 / ಕ್ವಿಂ",
                                fontSize = 11.sp,
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
