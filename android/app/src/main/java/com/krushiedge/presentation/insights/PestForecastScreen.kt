package com.krushiedge.presentation.insights

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.krushiedge.domain.model.CropType
import com.krushiedge.domain.model.RiskLevel
import com.krushiedge.presentation.components.AudioPlayerBar
import com.krushiedge.presentation.components.KrushiTopBar
import com.krushiedge.presentation.components.RiskLevelBadge
import com.krushiedge.presentation.theme.KrushiGreenDark
import com.krushiedge.presentation.theme.KrushiGreenPrimary

@Composable
fun PestForecastScreen(
    viewModel: PestForecastViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val isKn = state.language == "kn"

    Scaffold(
        topBar = {
            KrushiTopBar(
                title = if (isKn) "ಕೀಟ ಹಾವಳಿ ಮುನ್ಸೂಚನೆ" else "Pest Outbreak Forecast",
                subtitle = if (isKn) "7 ದಿನಗಳ ಮುನ್ಸೂಚನೆ & ಸಮಗ್ರ ನಿರ್ವಹಣೆ" else "7-Day Risk & IPM Solutions",
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
                    label = if (isKn) "ಕೀಟ ಹಾವಳಿ ಎಚ್ಚರಿಕೆ ಕನ್ನಡದಲ್ಲಿ ಆಲಿಸಿ" else "Listen Pest Advisory in Kannada",
                    onTogglePlay = { viewModel.toggleTts() }
                )
            }

            // Crop Selector Chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listOf(CropType.RAGI, CropType.COTTON, CropType.GROUNDNUT, CropType.TOMATO)) { crop ->
                        val selected = state.selectedCrop == crop
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (selected) KrushiGreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { viewModel.onCropChanged(crop) }
                        ) {
                            Text(
                                text = when (crop) {
                                    CropType.RAGI -> "ರಾಗಿ (Ragi)"
                                    CropType.COTTON -> "ಹತ್ತಿ (Cotton)"
                                    CropType.GROUNDNUT -> "ಕಡಲೆಕಾಯಿ"
                                    else -> "ಟೊಮ್ಯಾಟೊ"
                                },
                                fontSize = 12.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Pest Forecast Cards
            items(state.forecasts) { pest ->
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
                            Column {
                                Text(
                                    text = if (isKn) pest.pestNameKn else pest.pestNameEn,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = KrushiGreenDark
                                    )
                                )
                                Text(
                                    text = pest.scientificName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            RiskLevelBadge(risk = pest.currentRisk)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isKn) "7 ದಿನಗಳ ಅಪಾಯ ಪ್ರಕ್ಷೇಪಣ (7-Day Risk Trend):" else "7-Day Risk Projection:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // 7-day risk mini-chart
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            pest.riskTrend7Days.forEach { day ->
                                val dayColor = when (day.riskLevel) {
                                    RiskLevel.URGENT -> Color(0xFFE53935)
                                    RiskLevel.INSPECT -> Color(0xFFFB8C00)
                                    RiskLevel.MONITOR -> Color(0xFFFDD835)
                                    else -> Color(0xFF43A047)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "ದಿನ ${day.dayOffset}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(dayColor, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${(day.triggerProbability * 100).toInt()}%",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Immediate IPM Advice Callout
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFE8F5E9)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = KrushiGreenPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (isKn) "ತಕ್ಷಣದ ಜೈವಿಕ ನಿಯಂತ್ರಣ ಕ್ರಮ:" else "Immediate Bio-control Action:",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF1B5E20)
                                    )
                                    Text(
                                        text = pest.immediateMonitoringAdvice.get(state.language),
                                        fontSize = 12.sp,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                            }
                        }

                        if (pest.pheromoneTrapRecommended) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isKn) "⚡ ಎಕರೆಗೆ ${pest.trapCountPerAcre} ಮೋಹಕ ಬಲೆಗಳನ್ನು (Pheromone Traps) ಅಳವಡಿಸಿ."
                                else "⚡ Install ${pest.trapCountPerAcre} pheromone traps per acre.",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFE65100)
                            )
                        }
                    }
                }
            }
        }
    }
}
