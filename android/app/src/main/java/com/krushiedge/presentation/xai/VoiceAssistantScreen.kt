package com.krushiedge.presentation.xai

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.VolumeUp
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
import com.krushiedge.presentation.components.KrushiTopBar
import com.krushiedge.presentation.theme.KrushiGreenDark
import com.krushiedge.presentation.theme.KrushiGreenPrimary

@Composable
fun VoiceAssistantScreen(
    viewModel: VoiceAssistantViewModel = hiltViewModel(),
    onNavigateToRoute: (String) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val isKn = state.language == "kn"

    Scaffold(
        topBar = {
            KrushiTopBar(
                title = if (isKn) "AI ಧ್ವನಿ ಸಹಾಯಕ" else "Voice AI Assistant",
                subtitle = if (isKn) "ಕನ್ನಡ ಧ್ವನಿ ಸಂಭಾಷಣೆ" else "Kannada Conversational AI",
                currentLanguage = state.language
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Chat Conversation History
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.messages) { msg ->
                    val isAi = msg.sender == "KRUSHI_AI"

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isAi) Arrangement.Start else Arrangement.End
                    ) {
                        Card(
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (isAi) 2.dp else 16.dp,
                                bottomEnd = if (isAi) 16.dp else 2.dp
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isAi) MaterialTheme.colorScheme.surface else KrushiGreenPrimary
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                if (isAi) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VolumeUp,
                                            contentDescription = null,
                                            tint = KrushiGreenPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Krushi AI",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = KrushiGreenPrimary
                                        )
                                    }
                                }

                                Text(
                                    text = msg.text,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                    color = if (isAi) MaterialTheme.colorScheme.onSurface else Color.White
                                )

                                if (msg.actionRoute != null && msg.actionRoute != "home") {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = KrushiGreenPrimary.copy(alpha = 0.12f),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { onNavigateToRoute(msg.actionRoute) }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = if (isKn) "ವಿಭಾಗ ತೆರೆಯಿರಿ ➔" else "Open Section ➔",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
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

            // Quick Example Prompts
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = if (isKn) "ಉದಾಹರಣೆ ಪ್ರಶ್ನೆಗಳು (Quick Voice Prompts):" else "Sample Prompts:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val prompt1 = "ರಾಗಿ ಬೆಂಕಿ ರೋಗಕ್ಕೆ ಮದ್ದು ಏನು?"
                    val prompt2 = "ಇಂದು ನೀರು ಕೊಡಬೇಕಾ?"

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { viewModel.submitQuery(prompt1) }
                    ) {
                        Text(
                            text = "🌿 ಬೆಂಕಿ ರೋಗ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { viewModel.submitQuery(prompt2) }
                    ) {
                        Text(
                            text = "💧 ನೀರಾವರಿ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { viewModel.submitQuery("ಮಂಡ್ಯ ಮಂಡಿ ರಾಗಿ ಬೆಲೆ ಎಷ್ಟು?") }
                    ) {
                        Text(
                            text = "💰 ಮಂಡಿ ಬೆಲೆ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }

            // Big Push-to-Talk Mic Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .clickable {
                            viewModel.submitQuery("ರಾಗಿ ಬೆಂಕಿ ರೋಗಕ್ಕೆ ಸೂಕ್ತ ಔಷಧ ತಿಳಿಸಿ")
                        },
                    shape = CircleShape,
                    color = KrushiGreenDark,
                    shadowElevation = 6.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Speak",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        }
    }
}
