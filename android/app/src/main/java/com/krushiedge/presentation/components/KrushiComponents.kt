package com.krushiedge.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krushiedge.domain.model.AiMode
import com.krushiedge.domain.model.ConnectivityStatus
import com.krushiedge.domain.model.RiskLevel
import com.krushiedge.presentation.theme.KrushiGreenPrimary
import com.krushiedge.presentation.theme.RiskInspect
import com.krushiedge.presentation.theme.RiskMonitor
import com.krushiedge.presentation.theme.RiskNormal
import com.krushiedge.presentation.theme.RiskUrgent

@Composable
fun KrushiTopBar(
    title: String,
    subtitle: String? = null,
    connectivity: ConnectivityStatus = ConnectivityStatus.ONLINE,
    pendingSyncCount: Int = 0,
    currentLanguage: String = "kn",
    onLanguageClick: () -> Unit = {},
    onVoiceClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = KrushiGreenPrimary
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ConnectivityPill(status = connectivity, pendingSyncCount = pendingSyncCount)
                }
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Language selector chip
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onLanguageClick() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Language",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = when (currentLanguage.lowercase()) {
                                "kn", "kannada" -> "ಕನ್ನಡ"
                                "hi", "hindi" -> "हिन्दी"
                                else -> "ENG"
                            },
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Voice Assistant icon button
                IconButton(
                    onClick = onVoiceClick,
                    modifier = Modifier
                        .size(38.dp)
                        .background(KrushiGreenPrimary.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Assistant",
                        tint = KrushiGreenPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ConnectivityPill(
    status: ConnectivityStatus,
    pendingSyncCount: Int = 0
) {
    val (bgColor, textColor, icon, label) = when (status) {
        ConnectivityStatus.ONLINE -> Quad(
            Color(0xFFE8F5E9),
            Color(0xFF2E7D32),
            Icons.Default.CloudDone,
            "Online"
        )
        ConnectivityStatus.OFFLINE -> Quad(
            Color(0xFFFFF3E0),
            Color(0xFFE65100),
            Icons.Default.WifiOff,
            if (pendingSyncCount > 0) "Offline ($pendingSyncCount)" else "Offline AI"
        )
        ConnectivityStatus.SYNCING -> Quad(
            Color(0xFFE3F2FD),
            Color(0xFF1565C0),
            Icons.Default.Bolt,
            "Syncing..."
        )
        else -> Quad(
            Color(0xFFF5F5F5),
            Color(0xFF616161),
            Icons.Default.CloudOff,
            "Standby"
        )
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bgColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = textColor,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
        }
    }
}

@Composable
fun RiskLevelBadge(risk: RiskLevel, modifier: Modifier = Modifier) {
    val (bgColor, textColor, icon, label) = when (risk) {
        RiskLevel.NORMAL -> Quad(
            Color(0xFFE8F5E9),
            RiskNormal,
            Icons.Default.CheckCircle,
            "ಸುರಕ್ಷಿತ (Normal)"
        )
        RiskLevel.MONITOR -> Quad(
            Color(0xFFFFF9C4),
            RiskMonitor,
            Icons.Default.Info,
            "ಗಮನಿಸಿ (Monitor)"
        )
        RiskLevel.INSPECT -> Quad(
            Color(0xFFFFE0B2),
            RiskInspect,
            Icons.Default.Warning,
            "ಪರಿಶೀಲಿಸಿ (Inspect)"
        )
        RiskLevel.URGENT -> Quad(
            Color(0xFFFFCDD2),
            RiskUrgent,
            Icons.Default.Warning,
            "ತುರ್ತು (Urgent)"
        )
        RiskLevel.INSUFFICIENT -> Quad(
            Color(0xFFF5F5F5),
            Color.Gray,
            Icons.Default.Info,
            "ಮಾಹಿತಿ ಕೊರತೆ"
        )
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = bgColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Composable
fun AiModeBadge(mode: AiMode, modifier: Modifier = Modifier) {
    val label = when (mode) {
        AiMode.LOCAL -> "⚡ ಆನ್-ಡಿವೈಸ್ ಸ್ಥಳೀಯ AI (Offline)"
        AiMode.REMOTE -> "☁️ ಕ್ಲೌಡ್ ಕೃಷಿ AI (Gemini 2.0)"
        AiMode.HYBRID -> "⚡+☁️ ಹೈಬ್ರಿಡ್ AI (Edge fallback)"
        AiMode.OFFLINE -> "📦 ಆಫ್‌ಲೈನ್ ಕ್ಯಾಶ್ (Saved Rule)"
    }
    val badgeColor = when (mode) {
        AiMode.LOCAL -> Color(0xFF00796B)
        AiMode.REMOTE -> Color(0xFF1565C0)
        AiMode.HYBRID -> Color(0xFF6A1B9A)
        AiMode.OFFLINE -> Color(0xFFE65100)
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = badgeColor.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.3f))
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = badgeColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String? = null,
    icon: ImageVector,
    iconTint: Color = KrushiGreenPrimary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun AudioPlayerBar(
    isPlaying: Boolean,
    label: String = "ಧ್ವನಿ ವಿವರಣೆ ಕೇಳಿ (Listen Audio in Kannada)",
    onTogglePlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = KrushiGreenPrimary.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = onTogglePlay,
                    modifier = Modifier
                        .size(40.dp)
                        .background(KrushiGreenPrimary, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = "Play/Stop",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isPlaying) "ಪ್ಲೇ ಆಗುತ್ತಿದೆ..." else "ಸ್ಪಷ್ಟ ಕನ್ನಡದಲ್ಲಿ ಕೇಳಲು ಒತ್ತಿ",
                        style = MaterialTheme.typography.bodySmall,
                        color = KrushiGreenPrimary
                    )
                }
            }
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
