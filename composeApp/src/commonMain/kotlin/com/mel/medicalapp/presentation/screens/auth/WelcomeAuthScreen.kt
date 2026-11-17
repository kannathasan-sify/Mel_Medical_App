package com.mel.medicalapp.presentation.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mel.medicalapp.core.auth.GoogleAuthUser
import com.mel.medicalapp.core.auth.rememberGoogleAuthHandler
import com.mel.medicalapp.core.config.AppConfig
import com.mel.medicalapp.presentation.theme.MelColors
import kotlinx.coroutines.launch

enum class AuthMode {
    SIGN_IN,
    SIGN_UP,
    FORGOT_PASSWORD
}

@Composable
fun WelcomeAuthScreen(
    serverClientId: String = AppConfig.GOOGLE_SERVER_CLIENT_ID,
    onGoogleSignInSuccess: (GoogleAuthUser) -> Unit,
    onAppleSignInClicked: () -> Unit,
    onEmailPasswordSubmitted: (mode: AuthMode, email: String, pass: String) -> Unit
) {
    var authMode by remember { mutableStateOf(AuthMode.SIGN_IN) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var mfaChoice by remember { mutableStateOf("phone") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isAuthenticating by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    // Native Google Credential Manager Bottom Sheet Handler
    val googleAuthHandler = rememberGoogleAuthHandler(
        serverClientId = serverClientId,
        onSuccess = { googleUser ->
            isAuthenticating = false
            onGoogleSignInSuccess(googleUser)
        },
        onError = { err ->
            isAuthenticating = false
            errorMessage = err
        }
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MelColors.SurfaceBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header / Brand Hero Area (Matches "Welcome to Ask MEL" sketch)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Brand Emblem / Logo (Stethoscope curled around muscle arm)
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(MelColors.ApricotLight, CircleShape)
                        .border(2.dp, MelColors.Apricot, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "💪🩺",
                        fontSize = 36.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Ask MEL",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = MelColors.DeepBlue
                )

                Text(
                    text = "your MEDICAL EMPOWERMENT LIAISON",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = MelColors.AquaGreen
                )

                Text(
                    text = "empowering you in real time",
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                    color = MelColors.CoralPink
                )
            }

            // Auth Card Form Container (Apricot border accent from sketch)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MelColors.CardSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.5.dp, MelColors.Apricot.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Mode Selector Tabs (Sign In vs Sign Up)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MelColors.SurfaceBackground, RoundedCornerShape(12.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TabButton(
                            text = "Sign In",
                            isSelected = authMode == AuthMode.SIGN_IN,
                            onClick = { authMode = AuthMode.SIGN_IN }
                        )
                        TabButton(
                            text = "Sign Up",
                            isSelected = authMode == AuthMode.SIGN_UP,
                            onClick = { authMode = AuthMode.SIGN_UP }
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage ?: "",
                            color = MelColors.Error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    // Native Google Bottom View / Sheet Trigger Button
                    Button(
                        onClick = {
                            isAuthenticating = true
                            errorMessage = null
                            coroutineScope.launch {
                                googleAuthHandler.signInWithGoogle()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        border = BorderStroke(1.5.dp, MelColors.BorderSubtle),
                        enabled = !isAuthenticating
                    ) {
                        if (isAuthenticating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MelColors.SkyBlue,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("G ", fontWeight = FontWeight.Black, color = Color(0xFF4285F4), fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (authMode == AuthMode.SIGN_UP) "Sign up with Google (Native)" else "Sign in with Google (Native)",
                                    color = MelColors.TextPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = MelColors.BorderSubtle)
                        Text(
                            text = " or with email ",
                            fontSize = 12.sp,
                            color = MelColors.TextMuted,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = MelColors.BorderSubtle)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Email input
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email address") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Password input
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (authMode == AuthMode.SIGN_IN) {
                        // Forgot Password Link
                        Text(
                            text = "Forgot password?",
                            fontSize = 13.sp,
                            color = MelColors.SkyBlue,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .align(Alignment.End)
                                .clickable { authMode = AuthMode.FORGOT_PASSWORD }
                                .padding(vertical = 8.dp)
                        )
                    } else if (authMode == AuthMode.SIGN_UP) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Confirm password") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Multi-Factor Authentication (MFA):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MelColors.TextSecondary,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = mfaChoice == "phone",
                                onClick = { mfaChoice = "phone" }
                            )
                            Text("Phone Code", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(16.dp))
                            RadioButton(
                                selected = mfaChoice == "facial",
                                onClick = { mfaChoice = "facial" }
                            )
                            Text("Face Scan (Biometric)", fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Primary Submit Button
                    Button(
                        onClick = { onEmailPasswordSubmitted(authMode, email, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MelColors.AquaGreen)
                    ) {
                        Text(
                            text = when (authMode) {
                                AuthMode.SIGN_IN -> "Sign In"
                                AuthMode.SIGN_UP -> "Create Account"
                                AuthMode.FORGOT_PASSWORD -> "Send Reset Link"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            // Bottom Legal Footer
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "By signing in, you agree to our Privacy & Health Data Policy.",
                    fontSize = 11.sp,
                    color = MelColors.TextMuted,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Medical consultation recordings require separate explicit consent.",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MelColors.SkyBlue,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .background(
                if (isSelected) MelColors.CardSurface else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 24.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) MelColors.DeepBlue else MelColors.TextSecondary,
            fontSize = 14.sp
        )
    }
}
