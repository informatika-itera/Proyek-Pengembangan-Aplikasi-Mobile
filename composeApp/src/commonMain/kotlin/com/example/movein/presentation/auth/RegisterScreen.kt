package com.example.movein.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.movein.presentation.components.*
import com.example.movein.presentation.theme.MoveInTheme
import com.example.movein.presentation.theme.NoteAITheme

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onRegisterSuccess: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    val registerState by viewModel.registerState

    LaunchedEffect(registerState) {
        when (registerState) {
            is RegisterResultState.Success -> {
                val rawName = (registerState as RegisterResultState.Success).username
                val userName = rawName
                    .substringBefore("@")
                    .ifBlank { "Mahasiswa" }
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

                viewModel.resetState()
                onRegisterSuccess(userName)
            }
            is RegisterResultState.Error -> {
                error = (registerState as RegisterResultState.Error).message
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
                TextButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Text(
                        text = "← Kembali",
                        style = MoveInTheme.typography.labelLarge.copy(color = MoveInTheme.colors.textMuted)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Buat akun",
                    style = MoveInTheme.typography.displayLarge.copy(
                        fontSize = 32.sp,
                        color = MoveInTheme.colors.textPrimary
                    )
                )

                Text(
                    text = "Mulai langkah kecil pemulihanmu hari ini.",
                    style = MoveInTheme.typography.bodyMedium.copy(
                        color = MoveInTheme.colors.textSecondary
                    )
                )

                Spacer(modifier = Modifier.height(40.dp))

                MoveInCard(
                    contentPadding = 24.dp
                ) {
                    MoveInTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Nama panggilan"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    MoveInTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        keyboardType = KeyboardType.Email
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    MoveInTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password (min 4 karakter)",
                        isPassword = true
                    )

                    if (error != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = error.orEmpty(),
                            style = MoveInTheme.typography.labelSmall.copy(color = MoveInTheme.colors.errorRed)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    MoveInPrimaryButton(
                        text = if (registerState is RegisterResultState.Loading) "Mendaftarkan..." else "Register dan Mulai",
                        onClick = {
                            error = when {
                                name.isBlank() -> "Nama belum diisi."
                                email.isBlank() -> "Email belum diisi."
                                password.length < 4 -> "Password minimal 4 karakter."
                                else -> null
                            }

                            if (error == null) {
                                // Menggunakan email sebagai username untuk konsistensi dengan login
                                viewModel.register(email, password)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = registerState !is RegisterResultState.Loading
                    )
                }
            }
        }
    }
}
