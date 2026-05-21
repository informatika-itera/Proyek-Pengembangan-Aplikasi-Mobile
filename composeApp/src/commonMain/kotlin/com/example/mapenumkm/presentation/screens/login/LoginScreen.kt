package com.example.mapenumkm.presentation.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource

// Import Res biasanya otomatis dari Android Studio.
// Jika error, tekan Alt + Enter pada Res lalu pilih import yang tersedia.
import mapenumkm.composeapp.generated.resources.Res
import mapenumkm.composeapp.generated.resources.logo_mapen

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val greenPrimary = Color(0xFF16A34A)
    val greenDark = Color(0xFF15803D)
    val grayText = Color(0xFF64748B)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.White,
                        Color(0xFFF8FFF9),
                        Color(0xFFDCFCE7)
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(26.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Image(
                        painter = painterResource(Res.drawable.logo_mapen),
                        contentDescription = "Logo MaPen UMKM",
                        modifier = Modifier
                            .size(105.dp),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "MaPen UMKM",
                        color = greenPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Manajemen Penjualan UMKM",
                        color = grayText,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Masuk untuk mulai kelola usahamu",
                        color = grayText,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            errorMessage = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Email atau No. HP")
                        },
                        placeholder = {
                            Text("Masukkan email atau nomor HP")
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Kata Sandi")
                        },
                        placeholder = {
                            Text("Masukkan kata sandi")
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Lupa kata sandi?",
                        color = greenPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.End)
                    )

                    if (errorMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            when {
                                email.isBlank() -> {
                                    errorMessage = "Email atau No. HP wajib diisi"
                                }

                                password.isBlank() -> {
                                    errorMessage = "Kata sandi wajib diisi"
                                }

                                password.length < 6 -> {
                                    errorMessage = "Kata sandi minimal 6 karakter"
                                }

                                else -> {
                                    onLoginSuccess()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = greenPrimary
                        )
                    ) {
                        Text(
                            text = "Masuk",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Belum punya akun? ",
                            color = grayText,
                            fontSize = 13.sp
                        )

                        Text(
                            text = "Daftar",
                            color = greenDark,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    Text(
                        text = "Aplikasi pintar untuk kelola penjualan UMKM",
                        color = grayText,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "MaPen UMKM - Partner Cerdas UMKM",
                color = grayText,
                fontSize = 12.sp
            )
        }
    }
}