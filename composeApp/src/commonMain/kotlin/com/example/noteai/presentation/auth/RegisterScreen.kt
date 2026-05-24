package com.example.noteai.presentation.auth

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noteai.presentation.components.MoveInBentoCard
import com.example.noteai.presentation.components.MoveInPrimaryButton
import com.example.noteai.presentation.components.MoveInScreenFrame
import com.example.noteai.presentation.components.MoveInTextField

@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onRegisterSuccess: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    MoveInScreenFrame(
        accent = Color(0xFF22D3EE),
        background = Color(0xFF050505),
        modifier = Modifier.fillMaxSize()
    ) {
        TextButton(
            onClick = onNavigateBack
        ) {
            Text(
                text = "← Kembali",
                color = Color.White.copy(alpha = 0.75f)
            )
        }

        Text(
            text = "Buat akun",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = "Akun ini masih mock lokal untuk kebutuhan Sprint 2.",
            color = Color.White.copy(alpha = 0.68f),
            fontSize = 14.sp,
            lineHeight = 21.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        MoveInBentoCard {
            MoveInTextField(
                value = name,
                onValueChange = { name = it },
                label = "Nama panggilan"
            )

            Spacer(modifier = Modifier.height(12.dp))

            MoveInTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(12.dp))

            MoveInTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password minimal 4 karakter",
                isPassword = true
            )

            if (error != null) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = error.orEmpty(),
                    color = Color(0xFFFB7185),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            MoveInPrimaryButton(
                text = "Register dan Mulai",
                accent = Color(0xFF22D3EE),
                onClick = {
                    error = when {
                        name.isBlank() -> "Nama belum diisi."
                        email.isBlank() -> "Email belum diisi."
                        password.length < 4 -> "Password minimal 4 karakter."
                        else -> null
                    }

                    if (error == null) {
                        onRegisterSuccess(name)
                    }
                }
            )
        }
    }
}