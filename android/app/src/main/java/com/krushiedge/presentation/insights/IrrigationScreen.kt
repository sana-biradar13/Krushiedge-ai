package com.krushiedge.presentation.insights

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.krushiedge.domain.model.AiMode
import com.krushiedge.presentation.components.AiModeBadge
import com.krushiedge.presentation.components.AudioPlayerBar
import com.krushiedge.presentation.components.KrushiTopBar
import com.krushiedge.presentation.components.StatCard
import com.krushiedge.presentation.theme.KrushiGreenDark
import com.krushiedge.presentation.theme.KrushiGreenPrimary

@Composable
fun IrrigationScreen(
    viewModel: IrrigationViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val isKn = state.language == "kn"
    val adv = state.advisory

    Scaffold(
        topBar = {
            KrushiTopBar(
                title = if (isKn) "ಸ್ಮಾರ್ಟ್ ನೀರಾವರಿ ಸಲಹೆ" else "Smart Irrigation Advisory",
                subtitle = if (isKn) "ಬಯೋ-ಫಿಸಿಕಲ್ ನೀರಿನ ಲೆಕ್ಕಾಚಾರ" else "Bio-physical Water Demand",
                currentLanguage = state.language
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
            // Audio Explanation
            item {
                AudioPlayerBar(
                    isPlaying = state.isTtsActive,
                    label = if (isKn) "ನೀರಾವರಿ ಸಮಯವನ್ನು ಕನ್ನಡದಲ್ಲಿ ಆಲಿಸಿ" else "Listen Irrigation Timing in Kannada",
                    onTogglePlay = { viewModel.toggleTts() }
                )
            }

            // Hero Moisture Gauge Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isKn) "ಮಣ್ಣಿನ ಪ್ರಸ್ತುತ ತೇವಾಂಶ" else "Current Soil Moisture",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFFF3E0)
                            ) {
                                Text(
                                    text = if (isKn) "ನೀರುಣಿಸುವ ಅಗತ್ಯವಿದೆ" else "Irrigation Needed",
                                    color = Color(0xFFE65100),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Large Percentage Display & Progress Bar
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "${adv?.currentSoilMoisturePct?.toInt() ?: 38}%",
                                fontSize = 42.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFE65100)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "/ ಗುರಿ (Target): ${adv?.targetSoilMoisturePct?.toInt() ?: 65}%",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { ((adv?.currentSoilMoisturePct ?: 38.0) / 100.0).toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = Color(0xFFE65100),
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = adv?.reasoning?.get(state.language)
                                ?: "ಮಣ್ಣಿನಲ್ಲಿ ತೇವಾಂಶ ಕಡಿಮೆಯಾಗಿದ್ದು, ನಾಳೆ ಮುಂಜಾನೆ ಪಂಪ್ ಚಲಾಯಿಸಿ.",
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Recommended Pump Schedule Action Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = KrushiGreenDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isKn) "ಶಿಫಾರಸು ಮಾಡಿದ ಪಂಪ್ ಚಾಲನೆ ಸಮಯ" else "Recommended Pump Schedule",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            AiModeBadge(mode = AiMode.LOCAL)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = if (isKn) "5 HP ಮೋಟಾರ್ ಅವಧಿ" else "5 HP Run Time",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "${adv?.pumpRunHours ?: 1.8} ಗಂಟೆ (1h 48m)",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (isKn) "ಉತ್ತಮ ಸಮಯ (Best Window)" else "Best Window",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "ನಾಳೆ ಬೆಳಗ್ಗೆ 06:00 - 08:00",
                                    color = Color(0xFFFFD54F),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Savings,
                                    contentDescription = null,
                                    tint = Color(0xFFFFD54F),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isKn) "ಬೆಳಗಿನ ಸಮಯದಲ್ಲಿ ನೀರುಣಿಸುವುದರಿಂದ 28.5% ಆವಿಯಾಗುವಿಕೆ ತಡೆಯಬಹುದು"
                                    else "Morning irrigation saves 28.5% water from midday evaporation loss.",
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // Agronomic Physics Telemetry Grid
            item {
                Text(
                    text = if (isKn) "ಕೃಷಿ ತಾಂತ್ರಿಕ ಸೂಚಿಗಳು (FAO-56)" else "Agronomic Parameters (FAO-56)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "ET0 ಆವಿಯಾಗುವಿಕೆ",
                        value = "${String.format("%.1f", adv?.referenceEvapotranspirationMmDay ?: 5.2)} mm/ದಿನ",
                        subtitle = "Penman-Monteith",
                        icon = Icons.Default.WaterDrop,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "ಬೆಳೆ ಗುಣಾಂಕ (Kc)",
                        value = "${adv?.cropCoefficientKc ?: 1.15}",
                        subtitle = "ಹೂವಾಡುವ ಹಂತ",
                        icon = Icons.Default.Opacity,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
