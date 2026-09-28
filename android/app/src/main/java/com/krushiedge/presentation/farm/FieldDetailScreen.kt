package com.krushiedge.presentation.farm

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
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
import com.krushiedge.domain.model.FieldGrid
import com.krushiedge.domain.model.RiskLevel
import com.krushiedge.presentation.components.KrushiTopBar
import com.krushiedge.presentation.components.RiskLevelBadge
import com.krushiedge.presentation.theme.KrushiGreenDark
import com.krushiedge.presentation.theme.KrushiGreenPrimary
import com.krushiedge.presentation.theme.RiskInspect
import com.krushiedge.presentation.theme.RiskMonitor
import com.krushiedge.presentation.theme.RiskNormal
import com.krushiedge.presentation.theme.RiskUrgent

@Composable
fun FieldDetailScreen(
    viewModel: FieldViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val isKn = state.language == "kn"
    val field = state.field
    val selectedGrid = state.selectedGrid

    Scaffold(
        topBar = {
            KrushiTopBar(
                title = if (isKn) "ಹೊಲದ ವಲಯ ನಕ್ಷೆ" else "Field Zoning Heatmap",
                subtitle = "ಉತ್ತರ ಬ್ಲಾಕ್ (3.5 ಎಕರೆ) • 3x3 Micro-grids",
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
            // Interactive 3x3 Grid Map Container
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isKn) "ಹೊಲದ ವಲಯಗಳನ್ನು ಸ್ಪರ್ಶಿಸಿ ಪರಿಶೀಲಿಸಿ" else "Tap a sector grid to inspect evidence",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // 3x3 Grid Visualizer
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            for (r in 0..2) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    for (c in 0..2) {
                                        val gridItem = field?.gridZones?.find { it.row == r && it.col == c }
                                        val isSelected = selectedGrid?.gridId == gridItem?.gridId
                                        val gridColor = when (gridItem?.risk) {
                                            RiskLevel.URGENT -> Color(0xFFFFCDD2)
                                            RiskLevel.INSPECT -> Color(0xFFFFE0B2)
                                            RiskLevel.MONITOR -> Color(0xFFFFF9C4)
                                            else -> Color(0xFFC8E6C9)
                                        }
                                        val borderColor = if (isSelected) KrushiGreenDark else Color.Transparent

                                        Box(
                                            modifier = Modifier
                                                .size(90.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(gridColor)
                                                .border(if (isSelected) 3.dp else 1.dp, borderColor, RoundedCornerShape(10.dp))
                                                .clickable {
                                                    if (gridItem != null) viewModel.onGridSelected(gridItem)
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = "ವಲಯ ${gridItem?.gridId?.uppercase() ?: ""}",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp,
                                                    color = Color.Black.copy(alpha = 0.8f)
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = when (gridItem?.risk) {
                                                        RiskLevel.URGENT -> "ತುರ್ತು"
                                                        RiskLevel.INSPECT -> "ಪರಿಶೀಲಿಸಿ"
                                                        RiskLevel.MONITOR -> "ಗಮನಿಸಿ"
                                                        else -> "ಸಾಮಾನ್ಯ"
                                                    },
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = when (gridItem?.risk) {
                                                        RiskLevel.URGENT -> RiskUrgent
                                                        RiskLevel.INSPECT -> RiskInspect
                                                        RiskLevel.MONITOR -> RiskMonitor
                                                        else -> RiskNormal
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Selected Grid Evidence Details
            if (selectedGrid != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "ವಲಯ ${selectedGrid.gridId.uppercase()} ವಿವರ (Sector Details)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "ಅಕ್ಷಾಂಶ: ${selectedGrid.centerLat} • ರೇಖಾಂಶ: ${selectedGrid.centerLon}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                RiskLevelBadge(risk = selectedGrid.risk)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = if (isKn) "ಸಂಗ್ರಹಿಸಿದ ಪುರಾವೆಗಳು (Multi-modal Evidence):" else "Evidence Sources:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            selectedGrid.evidence.forEach { ev ->
                                Row(
                                    modifier = Modifier.padding(vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = KrushiGreenPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = when (ev) {
                                            com.krushiedge.domain.model.EvidenceType.CROP_PHOTO -> "ಕ್ಯಾಮರಾ ಫೋಟೋದಲ್ಲಿ ಶಿಲೀಂಧ್ರ ಕಲೆ ಪತ್ತೆ"
                                            com.krushiedge.domain.model.EvidenceType.WEATHER -> "78% ಹೆಚ್ಚಿನ ಗಾಳಿಯ ತೇವಾಂಶ ದಾಖಲೆ"
                                            com.krushiedge.domain.model.EvidenceType.SOIL_MOISTURE -> "ಮಣ್ಣಿನ ತೇವಾಂಶ 38% ಕ್ಕೆ ಇಳಿಕೆ"
                                            com.krushiedge.domain.model.EvidenceType.VEGETATION_TREND -> "NDVI ಹಸಿರುಮಟ್ಟದಲ್ಲಿ 12% ಕುಸಿತ"
                                            else -> ev.code
                                        },
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
