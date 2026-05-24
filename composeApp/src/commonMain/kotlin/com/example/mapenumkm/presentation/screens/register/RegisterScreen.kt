package com.example.mapenumkm.presentation.screens.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: RegisterViewModel = koinViewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val state by viewModel.registerState.collectAsState()

    LaunchedEffect(state) {
        if (state is RegisterState.Success) {
            onRegisterSuccess()
            viewModel.resetState()
        } else if (state is RegisterState.Error) {
            errorMessage = (state as RegisterState.Error).message
        }
    }

    val greenPrimary = Color(0xFF16A34A)
    val greenDark = Color(0xFF15803D)
    val grayText = Color(0xFF64748B)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Akun", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.White, Color(0xFFF8FFF9), Color(0xFFDCFCE7))
                    )
                )
                .padding(paddingValues)
                .padding(24.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Buat Akun Baru",
                    color = greenPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Lengkapi data di bawah untuk mendaftar",
                    color = grayText,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; errorMessage = "" },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nama Lengkap") },
                    placeholder = { Text("Masukkan nama lengkap") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    enabled = state !is RegisterState.Loading
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; errorMessage = "" },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Email") },
                    placeholder = { Text("Masukkan alamat email") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    enabled = state !is RegisterState.Loading
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it; errorMessage = "" },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nomor HP") },
                    placeholder = { Text("Contoh: 08123456789") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    enabled = state !is RegisterState.Loading
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errorMessage = "" },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Kata Sandi") },
                    placeholder = { Text("Minimal 8 karakter (Huruf & Angka)") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    enabled = state !is RegisterState.Loading,
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it; errorMessage = "" },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Konfirmasi Kata Sandi") },
                    placeholder = { Text("Ulangi kata sandi") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    enabled = state !is RegisterState.Loading
                )

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        when {
                            name.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank() -> {
                                errorMessage = "Semua bidang wajib diisi"
                            }
                            !email.contains("@") -> {
                                errorMessage = "Format email tidak valid"
                            }
                            password.length < 8 -> {
                                errorMessage = "Kata sandi minimal 8 karakter"
                            }
                            !password.any { it.isDigit() } || !password.any { it.isLetter() } -> {
                                errorMessage = "Kata sandi harus kombinasi huruf dan angka"
                            }
                            password != confirmPassword -> {
                                errorMessage = "Konfirmasi kata sandi tidak cocok"
                            }
                            else -> viewModel.register(name, email, phone, password)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = greenPrimary),
                    enabled = state !is RegisterState.Loading
                ) {
                    if (state is RegisterState.Loading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Daftar Sekarang", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.clickable { onNavigateBack() },
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("Sudah punya akun? ", color = grayText, fontSize = 14.sp)
                    Text("Masuk", color = greenDark, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}
