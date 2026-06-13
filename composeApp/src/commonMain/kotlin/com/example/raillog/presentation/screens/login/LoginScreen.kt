package com.example.raillog.presentation.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.raillog.presentation.components.FormField
import com.example.raillog.presentation.components.PrimaryButton
import com.example.raillog.presentation.theme.RailLogColors
import com.example.raillog.presentation.theme.Spacing
import org.koin.compose.viewmodel.koinViewModel

// ── Login ─────────────────────────────────────────────────────────────────────

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            onLoginSuccess(uiState.role)
            viewModel.resetState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RailLogColors.Background)
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(Spacing.pagePadding)
                .statusBarsPadding()
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Spacer(Modifier.height(Spacing.xxl))

            // Header
            Text(
                "Selamat datang",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = RailLogColors.TextPrimary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Masuk untuk melanjutkan ke sistem",
                style = MaterialTheme.typography.bodyMedium,
                color = RailLogColors.TextSecondary
            )

            Spacer(Modifier.height(Spacing.xl))

            // Fields
            FormField(
                value = username,
                onValueChange = { username = it },
                label = "Username",
                placeholder = "Masukkan username",
                leadingIcon = Icons.Default.Person
            )

            Spacer(Modifier.height(Spacing.md))

            FormField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                placeholder = "Masukkan password",
                leadingIcon = Icons.Default.Lock,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            null,
                            tint = RailLogColors.TextTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            )

            // Error
            if (uiState.error != null) {
                Spacer(Modifier.height(Spacing.sm))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(RailLogColors.Danger50, RoundedCornerShape(8.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.ErrorOutline, null,
                        tint = RailLogColors.Danger600, modifier = Modifier.size(16.dp))
                    Text(uiState.error!!, style = MaterialTheme.typography.bodySmall,
                        color = RailLogColors.Danger600)
                }
            }

            Spacer(Modifier.height(Spacing.xl))

            PrimaryButton(
                text = "Masuk",
                onClick = { viewModel.login(username, password) },
                loading = uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(Spacing.lg))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Staff baru?", style = MaterialTheme.typography.bodySmall,
                    color = RailLogColors.TextSecondary)
                Spacer(Modifier.width(4.dp))
                TextButton(
                    onClick = onNavigateToRegister,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Daftar di sini",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = RailLogColors.PrimaryAction)
                }
            }

            // Hint akun default
            Spacer(Modifier.height(Spacing.xxl))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RailLogColors.Neutral50, RoundedCornerShape(10.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("Akun demo:", style = MaterialTheme.typography.labelSmall,
                    color = RailLogColors.TextTertiary, fontWeight = FontWeight.Medium)
                Text("Admin → admin / raillog123",
                    style = MaterialTheme.typography.labelSmall, color = RailLogColors.TextTertiary)
                Text("Staff → operator / raillog123",
                    style = MaterialTheme.typography.labelSmall, color = RailLogColors.TextTertiary)
            }
        }
    }
}
