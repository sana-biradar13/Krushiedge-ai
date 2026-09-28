package com.krushiedge.presentation.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.krushiedge.R

private val GreenDark = Color(0xFF1B5E20)
private val GreenMid = Color(0xFF2E7D32)
private val GreenLight = Color(0xFF69F0AE)
private val Indigo = Color(0xFF1A237E)

@Composable
fun SignUpScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onSignUpSuccess: (language: String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val screenState by viewModel.screenState.collectAsState()
    val formState by viewModel.formState.collectAsState()
    var isVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) { isVisible = true }

    LaunchedEffect(screenState) {
        if (screenState is AuthScreenState.Success) {
            onSignUpSuccess((screenState as AuthScreenState.Success).language)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(GreenDark, GreenMid, Color(0xFF388E3C), Indigo)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { it / 3 }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Agriculture,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
                Text(
                    text = "KrushiEdge AI",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "ಖಾತೆ ತೆರೆಯಿರಿ  •  Create Account  •  खाता बनाएं",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(4.dp))

                // ── Form Card ──────────────────────────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f))
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Name
                        KrushiField(
                            value = formState.name,
                            onValueChange = viewModel::onNameChange,
                            label = "ಹೆಸರು / Name / नाम",
                            leadingIcon = Icons.Default.Person,
                            imeAction = ImeAction.Next,
                            onIme = { focusManager.moveFocus(FocusDirection.Down) }
                        )

                        // Phone or Email
                        KrushiField(
                            value = formState.identifier,
                            onValueChange = viewModel::onIdentifierChange,
                            label = "ಫೋನ್ / Email / फोन",
                            leadingIcon = Icons.Default.Phone,
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next,
                            onIme = { focusManager.moveFocus(FocusDirection.Down) }
                        )

                        // Password
                        KrushiPasswordField(
                            value = formState.password,
                            onValueChange = viewModel::onPasswordChange,
                            isVisible = formState.isPasswordVisible,
                            onToggleVisibility = viewModel::togglePasswordVisibility,
                            label = "ಪಾಸ್‌ವರ್ಡ್ / Password / पासवर्ड",
                            imeAction = ImeAction.Done,
                            onIme = { focusManager.clearFocus() }
                        )

                        // Language chips
                        Text(
                            text = "ಭಾಷೆ ಆಯ್ಕೆ / Language / भाषा",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("kn" to "ಕನ್ನಡ", "hi" to "हिन्दी", "en" to "English").forEach { (code, label) ->
                                val selected = formState.selectedLanguage == code
                                FilterChip(
                                    selected = selected,
                                    onClick = { viewModel.onLanguageChange(code) },
                                    label = { Text(label, fontSize = 12.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = GreenLight,
                                        selectedLabelColor = GreenDark
                                    )
                                )
                            }
                        }

                        // Error
                        if (screenState is AuthScreenState.Error) {
                            Text(
                                text = (screenState as AuthScreenState.Error).message,
                                color = Color(0xFFFF5252),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Sign Up Button
                        Button(
                            onClick = { viewModel.signUp() },
                            enabled = screenState !is AuthScreenState.Loading,
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GreenLight,
                                contentColor = GreenDark,
                                disabledContainerColor = Color.White.copy(alpha = 0.2f)
                            )
                        ) {
                            if (screenState is AuthScreenState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = GreenDark,
                                    strokeWidth = 2.5.dp
                                )
                            } else {
                                Text(
                                    "ಖಾತೆ ತೆರೆಯಿರಿ  •  Sign Up",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }

                // Navigate to Login
                TextButton(onClick = onNavigateToLogin) {
                    Text(
                        text = "ಈಗಾಗಲೇ ಖಾತೆ ಇದೆಯೇ? ಲಾಗಿನ್ ಮಾಡಿ  •  Already have an account? Login",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }

                Text(
                    text = "ನಿಮ್ಮ ಡೇಟಾ ಸಾಧನದಲ್ಲಿ ಸುರಕ್ಷಿತವಾಗಿ ಉಳಿಸಲಾಗುತ್ತದೆ",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.45f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ── Shared Field Composables ─────────────────────────────────────────────────

@Composable
private fun KrushiField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onIme: () -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp) },
        leadingIcon = { Icon(leadingIcon, contentDescription = null, tint = Color.White.copy(alpha = 0.75f)) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = GreenLight,
            unfocusedBorderColor = Color.White.copy(alpha = 0.35f),
            cursorColor = GreenLight
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = KeyboardActions(onAny = { onIme() }),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun KrushiPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit,
    label: String,
    imeAction: ImeAction = ImeAction.Done,
    onIme: () -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp) },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White.copy(alpha = 0.75f)) },
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(
                    imageVector = if (isVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (isVisible) "Hide" else "Show",
                    tint = Color.White.copy(alpha = 0.75f)
                )
            }
        },
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = GreenLight,
            unfocusedBorderColor = Color.White.copy(alpha = 0.35f),
            cursorColor = GreenLight
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = imeAction),
        keyboardActions = KeyboardActions(onAny = { onIme() }),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}
