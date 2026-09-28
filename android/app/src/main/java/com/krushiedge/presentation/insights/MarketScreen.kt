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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
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
import com.krushiedge.domain.model.MandiRecommendation
import com.krushiedge.domain.model.PriceTrendDirection
import com.krushiedge.presentation.components.KrushiTopBar
import com.krushiedge.presentation.theme.KrushiGreenDark
import com.krushiedge.presentation.theme.KrushiGreenPrimary

@Composable
fun MarketScreen(
    viewModel: MarketViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val isKn = state.language == "kn"

    Scaffold(
        topBar = {
            KrushiTopBar(
                title = if (isKn) "APMC ಮಂಡಿ ಮಾರುಕಟ್ಟೆ ದರಗಳು" else "APMC Mandi Intelligence",
                subtitle = if (isKn) "ನೈಜ ಬೆಲೆ & ಸಾರಿಗೆ ವೆಚ್ಚದ ನಂತರದ ಲಾಭ" else "Real Rates & Net Profit after Transport",
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
            // Crop Filter Chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listOf(CropType.RAGI, CropType.GROUNDNUT, CropType.COTTON, CropType.TOMATO)) { crop ->
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
                                    CropType.GROUNDNUT -> "ಕಡಲೆಕಾಯಿ"
                                    CropType.COTTON -> "ಹತ್ತಿ"
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

            // Net Profit Revenue Estimator Card
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
                            Text(
                                text = if (isKn) "ಅಂದಾಜು ಒಟ್ಟು ಆದಾಯ (35 ಕ್ವಿಂಟಾಲ್)" else "Estimated Total Revenue (35 Qtl)",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = null,
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "₹1,28,625",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = if (isKn) "ಮಂಡ್ಯ ಮಂಡಿಯಲ್ಲಿ ಮಾರಾಟ ಮಾಡಿದರೆ (ಸಾರಿಗೆ ವೆಚ್ಚ ಕಳೆದು)"
                            else "At Mandya Mandi after deducting ₹1,575 transport fee",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            item {
                Text(
                    text = if (isKn) "ಸಮೀಪದ APMC ಮಾರುಕಟ್ಟೆಗಳು" else "Nearby APMC Mandis",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            // Mandi Price Cards List
            items(state.prices) { mandi ->
                val isBest = mandi.recommendation == MandiRecommendation.SELL_NOW_HIGHEST_NET

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBest) Color(0xFFF1F8E9) else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isBest) 4.dp else 2.dp),
                    border = if (isBest) androidx.compose.foundation.BorderStroke(1.5.dp, KrushiGreenPrimary) else null
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = mandi.mandiName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    if (isBest) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = KrushiGreenPrimary
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Star,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(10.dp)
                                                )
                                                Spacer(modifier = Modifier.width(2.dp))
                                                Text(
                                                    text = if (isKn) "ಅತ್ಯುತ್ತಮ ಲಾಭ" else "Best Net",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }
                                }
                                Text(
                                    text = "ದೂರ: ${mandi.distanceKm.toInt()} km • ${mandi.variety}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Price Change pill
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (mandi.priceChange24hInr >= 0) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    tint = if (mandi.priceChange24hInr >= 0) Color(0xFF2E7D32) else Color(0xFFC62828),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "${if (mandi.priceChange24hInr >= 0) "+" else ""}₹${mandi.priceChange24hInr.toInt()}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (mandi.priceChange24hInr >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = if (isKn) "ಮಾರುಕಟ್ಟೆ ಬೆಲೆ" else "Mandi Rate",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "₹${mandi.modalPricePerQuintalInr.toInt()} / ಕ್ವಿಂ",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column {
                                Text(
                                    text = if (isKn) "ಸಾರಿಗೆ ವೆಚ್ಚ" else "Transport Fee",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "-₹${mandi.estimatedTransportCostPerQuintalInr.toInt()}",
                                    fontSize = 14.sp,
                                    color = Color(0xFFC62828)
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (isKn) "ನಿವ್ವಳ ಲಾಭ (Net Price)" else "Effective Net",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = KrushiGreenPrimary
                                )
                                Text(
                                    text = "₹${mandi.netEffectivePricePerQuintalInr.toInt()} / ಕ್ವಿಂ",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = KrushiGreenPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
