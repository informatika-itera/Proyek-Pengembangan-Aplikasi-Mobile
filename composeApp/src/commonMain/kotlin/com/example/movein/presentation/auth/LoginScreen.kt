package com.example.movein.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.movein.presentation.components.*
import com.example.movein.presentation.theme.MoveInTheme
import com.example.movein.presentation.theme.NoteAITheme

@Composable
fun LoginScreen(
    viewModel: AuthViewModel, // SUNTIKAN 1: Masukkan ViewModel sebagai parameter
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    // SUNTIKAN 2: Pantau status login dari database
    val loginState by viewModel.loginState

    // SUNTIKAN 3: Tangani efek ketika database sukses menemukan user
    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginResultState.Success -> {
                val rawName = (loginState as LoginResultState.Success).username
                val userName = rawName
                    .substringBefore("@")
                    .ifBlank { "Mahasiswa" }
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

                viewModel.resetState()
                onLoginSuccess(userName)
            }
            is LoginResultState.Error -> {
                error = (loginState as LoginResultState.Error).message
                viewModel.resetState()
            }
            else -> {}
        }
    }

    NoteAITheme(darkTheme = true) {
        MoveInScaffold {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "movein.",
                    style = MoveInTheme.typography.displayLarge.copy(
                        fontSize = 34.sp,
                        color = MoveInTheme.colors.textPrimary
                    )
                )

                Text(
                    text = "Your Anti-Toxic Productivity Space",
                    style = MoveInTheme.typography.bodyMedium.copy(
                        color = MoveInTheme.colors.textSecondary
                    )
                )

                Spacer(modifier = Modifier.height(48.dp))

                MoveInCard(
                    contentPadding = 24.dp
                ) {
                    Text(
                        text = "Masuk dulu, lalu pilih mood kamu hari ini.",
                        style = MoveInTheme.typography.bodyLarge.copy(
                            color = MoveInTheme.colors.textPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    MoveInTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            error = null
                        },
                        label = "Email atau username",
                        keyboardType = KeyboardType.Email
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    MoveInTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            error = null
                        },
                        label = "Password",
                        isPassword = true
                    )

                    if (error != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = error.orEmpty(),
                            style = MoveInTheme.typography.labelSmall.copy(
                                color = MoveInTheme.colors.errorRed
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // SUNTIKAN 4: Ganti logic onClick untuk menembak fungsi kueri di ViewModel
                    MoveInPrimaryButton(
                        text = if (loginState is LoginResultState.Loading) "Memeriksa..." else "Masuk ke movein",
                        onClick = {
                            error = when {
                                email.isBlank() -> "Email atau username belum diisi."
                                password.isBlank() -> "Password belum diisi."
                                password.length < 4 -> "Password minimal 4 karakter."
                                else -> null
                            }

                            if (error == null) {
                                viewModel.login(email, password)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = loginState !is LoginResultState.Loading
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = onNavigateToRegister,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = "Belum punya akun? Register",
                            style = MoveInTheme.typography.labelLarge.copy(
                                color = MoveInTheme.colors.accentBlue
                            )
                        )
                    }
                }
            }
        }
    }
}