package com.krushiedge.presentation.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.krushiedge.presentation.components.AiModeBadge
import com.krushiedge.presentation.components.AudioPlayerBar
import com.krushiedge.presentation.components.KrushiTopBar
import com.krushiedge.presentation.components.RiskLevelBadge
import com.krushiedge.presentation.theme.KrushiGreenDark
import com.krushiedge.presentation.theme.KrushiGreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    viewModel: ScanViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val isKn = state.language == "kn"
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        topBar = {
            KrushiTopBar(
                title = if (isKn) "ಬೆಳೆ ಡಾಕ್ಟರ್ AI" else "Crop Doctor AI",
                subtitle = if (isKn) "ರೋಗ ಪತ್ತೆ ಕ್ಯಾಮೆರಾ" else "Disease Diagnostic Camera",
                currentLanguage = state.language
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.Black)
        ) {
            // Camera Viewfinder View
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Crop Selection Pills
                Column {
                    Text(
                        text = if (isKn) "ಬೆಳೆ ಆಯ್ಕೆಮಾಡಿ:" else "Select Crop:",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(listOf(CropType.RAGI, CropType.GROUNDNUT, CropType.COTTON, CropType.TOMATO, CropType.RICE)) { crop ->
                            val selected = state.selectedCrop == crop
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (selected) KrushiGreenPrimary else Color.DarkGray.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable { viewModel.onCropSelected(crop) }
                            ) {
                                Text(
                                    text = when (crop) {
                                        CropType.RAGI -> "ರಾಗಿ (Ragi)"
                                        CropType.GROUNDNUT -> "ಕಡಲೆಕಾಯಿ (Groundnut)"
                                        CropType.COTTON -> "ಹತ್ತಿ (Cotton)"
                                        CropType.TOMATO -> "ಟೊಮ್ಯಾಟೊ (Tomato)"
                                        CropType.RICE -> "ಭತ್ತ (Rice)"
                                        else -> crop.code
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                // Center Reticle / Bounding Box for Leaf targeting
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .border(2.dp, KrushiGreenPrimary, RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Camera,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isKn) "ಬಾಧಿತ ಎಲೆಯನ್ನು ಈ ಚೌಕಟ್ಟಿನಲ್ಲಿ ಹಿಡಿಯಿರಿ" else "Align infected leaf inside frame",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (isKn) "ಉತ್ತಮ ಬೆಳಕಿನಲ್ಲಿ ಸ್ಪಷ್ಟ ಫೋಟೋ ತೆಗೆಯಿರಿ" else "Ensure good sunlight, no shadow",
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                    }
                }

                // Bottom Camera Control Bar
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AiModeBadge(mode = com.krushiedge.domain.model.AiMode.LOCAL)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Gallery button
                        IconButton(
                            onClick = { viewModel.triggerScan() },
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.DarkGray, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = "Gallery",
                                tint = Color.White
                            )
                        }

                        // Shutter Trigger Button
                        Surface(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .clickable {
                                    if (!state.isScanning) viewModel.triggerScan()
                                },
                            shape = CircleShape,
                            color = KrushiGreenPrimary,
                            border = androidx.compose.foundation.BorderStroke(4.dp, Color.White)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (state.isScanning) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(36.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Camera,
                                        contentDescription = "Capture",
                                        tint = Color.White,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }
                        }

                        // Flash Toggle Button
                        IconButton(
                            onClick = { },
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.DarkGray, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = "Flash",
                                tint = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        // ── Comprehensive Diagnosis Bottom Sheet Report ──
        if (state.isResultDialogVisible && state.diagnosisReport != null) {
            val report = state.diagnosisReport!!
            val diag = report.detailedDiagnosis

            ModalBottomSheet(
                onDismissRequest = { viewModel.dismissResult() },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header with confidence and close button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isKn) diag.diseaseNameKn else diag.diseaseNameEn,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = KrushiGreenDark
                                )
                            )
                            Text(
                                text = if (isKn) diag.diseaseNameEn else diag.diseaseNameKn,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        RiskLevelBadge(risk = RiskLevel.URGENT)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Confidence Score Pill & Model Mode
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFE8F5E9)
                        ) {
                            Text(
                                text = "ನಿಖರತೆ (Confidence): ${(report.analysisResult.confidence * 100).toInt()}%",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        AiModeBadge(mode = report.analysisResult.aiMode)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Audio explanation player
                    AudioPlayerBar(
                        isPlaying = state.isAudioPlaying,
                        label = if (isKn) "ಚಿಕಿತ್ಸಾ ವಿಧಾನವನ್ನು ಕನ್ನಡದಲ್ಲಿ ಆಲಿಸಿ" else "Listen Treatment Plan in Kannada",
                        onTogglePlay = { viewModel.toggleAudio() }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Symptoms & Causes
                    Text(
                        text = if (isKn) "ರೋಗದ ಲಕ್ಷಣಗಳು:" else "Symptoms:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = diag.symptomsSummary.get(state.language),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Treatment Options Tabs (Chemical vs Organic vs Explanation)
                    TabRow(
                        selectedTabIndex = state.activeTab.ordinal,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ) {
                        Tab(
                            selected = state.activeTab == TreatmentTab.CHEMICAL,
                            onClick = { viewModel.onTabSelected(TreatmentTab.CHEMICAL) },
                            text = { Text(if (isKn) "ರಾಸಾಯನಿಕ" else "Chemical", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = state.activeTab == TreatmentTab.ORGANIC,
                            onClick = { viewModel.onTabSelected(TreatmentTab.ORGANIC) },
                            text = { Text(if (isKn) "ಜೈವಿಕ / ಸಾವಯವ" else "Organic", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = state.activeTab == TreatmentTab.EXPLANATION,
                            onClick = { viewModel.onTabSelected(TreatmentTab.EXPLANATION) },
                            text = { Text(if (isKn) "AI ಕಾರಣ (XAI)" else "Why AI Flagged", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    when (state.activeTab) {
                        TreatmentTab.CHEMICAL -> {
                            diag.treatmentPlan.chemicalOptions.forEach { chemical ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = chemical.activeIngredient,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                text = "₹${chemical.estimatedCostPerAcreInr.toInt()}/ಎಕರೆ",
                                                fontWeight = FontWeight.Bold,
                                                color = KrushiGreenPrimary
                                            )
                                        }
                                        Text(
                                            text = "ವ್ಯಾಪಾರ ನಾಮ (Brands): ${chemical.tradeNames.joinToString(", ")}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "ಪ್ರಮಾಣ (Dosage): ${chemical.dosagePerLiterWater} (${chemical.sprayVolumePerAcreLiters}L ನೀರು/ಎಕರೆ)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                        TreatmentTab.ORGANIC -> {
                            diag.treatmentPlan.organicOptions.forEach { organic ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9).copy(alpha = 0.6f))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = organic.name.get(state.language),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Color(0xFF1B5E20)
                                        )
                                        Text(
                                            text = organic.recipeOrSource.get(state.language),
                                            fontSize = 12.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "ಪ್ರಮಾಣ: ${organic.dosagePerAcre} • ವೆಚ್ಚ: ₹${organic.estimatedCostPerAcreInr.toInt()}/ಎಕರೆ",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF2E7D32)
                                        )
                                    }
                                }
                            }
                        }
                        TreatmentTab.EXPLANATION -> {
                            report.explainabilityFacts.forEach { fact ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = KrushiGreenPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "${fact.title.get(state.language)} (${fact.evidenceWeightPct.toInt()}%)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = fact.description.get(state.language),
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFFF8E1)
                            ) {
                                Text(
                                    text = report.counterfactualAdvice,
                                    fontSize = 12.sp,
                                    color = Color(0xFFF57F17),
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                        else -> {}
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.dismissResult() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = KrushiGreenPrimary)
                    ) {
                        Text(if (isKn) "ಮುಕ್ತಾಯ / ರೆಕಾರ್ಡ್ ಉಳಿಸಲಾಗಿದೆ" else "Done (Saved to History)", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
