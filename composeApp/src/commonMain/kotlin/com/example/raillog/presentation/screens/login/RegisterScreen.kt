package com.example.raillog.presentation.screens.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.raillog.data.local.datastore.UserPreferences
import com.example.raillog.presentation.theme.RailLogColors
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import androidx.compose.material.icons.automirrored.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    userPreferences: UserPreferences = koinInject()
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var name by remember { mutableStateOf("") }
    var employeeId by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var errorMsg by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = RailLogColors.TextPrimary,
        unfocusedTextColor = RailLogColors.TextPrimary,
        focusedLabelColor = RailLogColors.PrimaryAction,
        unfocusedLabelColor = RailLogColors.TextPrimary,
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        focusedBorderColor = RailLogColors.PrimaryAction,
        unfocusedBorderColor = RailLogColors.BorderSubtle
    )

    if (isSuccess) {
        AlertDialog(
            onDismissRequest = { },
            containerColor = Color.White,
            title = { Text("Registrasi Berhasil", color = RailLogColors.PrimaryAction, fontWeight = FontWeight.ExtraBold) },
            text = { Text("Akun Anda telah terdaftar secara resmi. Silakan Login dengan NIP dan Password Anda.", color = RailLogColors.TextPrimary, fontWeight = FontWeight.Bold) },
            confirmButton = {
                Button(
                    onClick = onNavigateBack,
                    colors = ButtonDefaults.buttonColors(containerColor = RailLogColors.PrimaryAction, contentColor = Color.White)
                ) { Text("KE HALAMAN LOGIN", fontWeight = FontWeight.ExtraBold, color = Color.White) }
            }
        )
    }

    Scaffold(
        containerColor = RailLogColors.Neutral100,
        topBar = {
            TopAppBar(
                title = { Text("Pendaftaran Akun Resmi", fontWeight = FontWeight.ExtraBold, color = RailLogColors.PrimaryAction) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = RailLogColors.PrimaryAction) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RailLogColors.Neutral100)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Otoritas Logistik", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = RailLogColors.PrimaryAction)
            Text("Masukkan identitas valid sesuai data kepegawaian.", color = RailLogColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Nama Lengkap (Sesuai ID)", fontWeight = FontWeight.Bold) }, 
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = textFieldColors
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = employeeId, onValueChange = { employeeId = it.uppercase() },
                label = { Text("NIP / ID Karyawan", fontWeight = FontWeight.Bold) }, 
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                placeholder = { Text("Contoh: RLN-12345", color = Color.Gray) },
                colors = textFieldColors
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = phone, onValueChange = { phone = it },
                label = { Text("Nomor WhatsApp/HP", fontWeight = FontWeight.Bold) }, 
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                placeholder = { Text("0812xxxxxxxx", color = Color.Gray) },
                colors = textFieldColors
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = username, onValueChange = { username = it },
                label = { Text("Username Akun", fontWeight = FontWeight.Bold) }, 
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = textFieldColors
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password, onValueChange = { password = it },
                label = { Text("Password", fontWeight = FontWeight.Bold) }, 
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null, tint = Color.Black)
                    }
                },
                colors = textFieldColors
            )

            if (errorMsg != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = RailLogColors.Danger50),
                    border = BorderStroke(2.dp, RailLogColors.Danger600)
                ) {
                    Text(errorMsg!!, color = RailLogColors.Danger600, modifier = Modifier.padding(12.dp), fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    val nipRegex = Regex("^RLN-[A-Z0-9]+$")
                    val phoneRegex = Regex("^08[0-9]{9,13}$")

                    when {
                        name.isBlank() || username.isBlank() || password.isBlank() || employeeId.isBlank() || phone.isBlank() -> {
                            errorMsg = "Semua kolom identitas wajib diisi!"
                        }
                        !nipRegex.matches(employeeId) -> {
                            errorMsg = "Format NIP tidak valid! Gunakan format: RLN-[KODE] (Contoh: RLN-001)"
                        }
                        !phoneRegex.matches(phone) -> {
                            errorMsg = "Nomor WhatsApp tidak valid! Gunakan format Indonesia (Contoh: 08123456789)"
                        }
                        password.length < 6 -> {
                            errorMsg = "Password terlalu pendek! Minimal 6 karakter."
                        }
                        else -> {
                            coroutineScope.launch {
                                userPreferences.registerStaff(name, username, password, employeeId, phone)
                                isSuccess = true
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RailLogColors.PrimaryAction,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("DAFTAR SEKARANG", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            }
            
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
