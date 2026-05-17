package com.example.masakuy.presentation.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.masakuy.theme.OrangeMain

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🍳 Masakuy",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = OrangeMain
        )

        Text(
            text = "Cari ide makan sesuai budgetmu",
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 8.dp)
        )

        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Email") },
            modifier = Modifier.padding(top = 32.dp)
        )

        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password") },
            modifier = Modifier.padding(top = 16.dp)
        )

        Button(
            onClick = { onLoginSuccess() },
            modifier = Modifier.padding(top = 32.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = OrangeMain
            )
        ) {
            Text("Masuk", fontSize = 16.sp)
        }

        Text(
            text = "Daftar",
            modifier = Modifier.padding(top = 16.dp),
            fontSize = 14.sp,
            color = OrangeMain
        )
    }
}