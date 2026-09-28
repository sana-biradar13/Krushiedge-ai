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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

private val LGreenDark = Color(0xFF1B5E20)
private val LGreenMid = Color(0xFF2E7D32)
private val LGreenLight = Color(0xFF69F0AE)
private val LIndigo = Color(0xFF1A237E)

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: (language: String) -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    val screenState by viewModel.screenState.collectAsState()
    val formState by viewModel.formState.collectAsState()
    var isVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) { isVisible = true }

    LaunchedEffect(screenState) {
        if (screenState is AuthScreenState.Success) {
            onLoginSuccess((screenState as AuthScreenState.Success).language)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(LGreenDark, LGreenMid, Color(0xFF388E3C), LIndigo)
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
                    modifier = Modifier.size(72.dp)
                )
                Text(
                    text = "KrushiEdge AI",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "ಲಾಗಿನ್  •  Login  •  लॉगिन",
                    fontSize = 14.sp,
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
                        // Phone / Email
                        OutlinedTextField(
                            value = formState.identifier,
                            onValueChange = viewModel::onIdentifierChange,
                            label = { Text("ಫೋನ್ / Email / फोन", color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp) },
                            leadingIcon = { Icon(Icons.Default.Phone, null, tint = Color.White.copy(alpha = 0.75f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = LGreenLight,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.35f),
                                cursorColor = LGreenLight
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Password
                        OutlinedTextField(
                            value = formState.password,
                            onValueChange = viewModel::onPasswordChange,
                            label = { Text("ಪಾಸ್‌ವರ್ಡ್ / Password / पासवर्ड", color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp) },
                            leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color.White.copy(alpha = 0.75f)) },
                            trailingIcon = {
                                IconButton(onClick = viewModel::togglePasswordVisibility) {
                                    Icon(
                                        if (formState.isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        null, tint = Color.White.copy(alpha = 0.75f)
                                    )
                                }
                            },
                            visualTransformation = if (formState.isPasswordVisible)
                                androidx.compose.ui.text.input.VisualTransformation.None
                            else
                                androidx.compose.ui.text.input.PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = LGreenLight,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.35f),
                                cursorColor = LGreenLight
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(); viewModel.login() }),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Error banner
                        if (screenState is AuthScreenState.Error) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFB71C1C).copy(alpha = 0.25f)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Warning, null, tint = Color(0xFFFF5252), modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = (screenState as AuthScreenState.Error).message,
                                        color = Color(0xFFFF5252),
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        // Login Button
                        Button(
                            onClick = { viewModel.login() },
                            enabled = screenState !is AuthScreenState.Loading,
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LGreenLight,
                                contentColor = LGreenDark,
                                disabledContainerColor = Color.White.copy(alpha = 0.2f)
                            )
                        ) {
                            if (screenState is AuthScreenState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = LGreenDark,
                                    strokeWidth = 2.5.dp
                                )
                            } else {
                                Icon(Icons.Default.Login, null, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("ಲಾಗಿನ್  •  Login", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }
                }

                // Navigate to Sign Up
                TextButton(onClick = {
                    viewModel.clearError()
                    onNavigateToSignUp()
                }) {
                    Text(
                        text = "ಹೊಸ ಖಾತೆ ಬೇಕೇ? ಸೈನ್ ಅಪ್ ಮಾಡಿ  •  New here? Create Account",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
