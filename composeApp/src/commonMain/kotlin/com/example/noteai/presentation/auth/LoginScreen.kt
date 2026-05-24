package com.example.noteai.presentation.auth

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
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
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    MoveInScreenFrame(
        accent = Color(0xFF22D3EE),
        background = Color(0xFF050505),
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "MoveIn.",
            color = Color.White,
            fontSize = 34.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = "Masuk dulu, lalu pilih mood kamu hari ini.",
            color = Color.White.copy(alpha = 0.68f),
            fontSize = 14.sp,
            lineHeight = 21.sp
        )

        Spacer(modifier = Modifier.height(36.dp))

        MoveInBentoCard {
            Text(
                text = "Login",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(18.dp))

            MoveInTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email atau username",
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(12.dp))

            MoveInTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                isPassword = true
            )

            Spacer(modifier = Modifier.height(22.dp))

            MoveInPrimaryButton(
                text = "Masuk ke MoveIn",
                accent = Color(0xFF22D3EE),
                enabled = email.isNotBlank() && password.length >= 4,
                onClick = {
                    val userName = email
                        .substringBefore("@")
                        .replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase() else it.toString()
                        }

                    onLoginSuccess(userName)
                }
            )

            TextButton(
                onClick = onNavigateToRegister
            ) {
                Text(
                    text = "Belum punya akun? Register",
                    color = Color.White.copy(alpha = 0.75f)
                )
            }
        }
    }
}