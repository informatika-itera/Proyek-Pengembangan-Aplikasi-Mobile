package com.example.fitgen.presentation.screens.nutrition

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fitgen.core.util.rememberCameraLauncher
import com.example.fitgen.domain.usecase.AnalyzeFoodNutritionUseCase
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun ScannerScreen(
    analyzeFoodNutritionUseCase: AnalyzeFoodNutritionUseCase = koinInject()
) {
    val maroon = Color(0xFF800000)
    val darkBlack = Color(0xFF121212)
    val pinkAccent = Color(0xFFFFB6C1)

    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var nutritionResult by remember { mutableStateOf<String?>(null) }
    var lastImageBytes by remember { mutableStateOf<ByteArray?>(null) }

    val processImageScanning: (ByteArray?) -> Unit = { imageBytes ->
        if (imageBytes != null) {
            lastImageBytes = imageBytes
            isLoading = true
            isError = false
            nutritionResult = null

            scope.launch {
                val result = analyzeFoodNutritionUseCase(imageBytes)
                result.onSuccess { data ->
                    nutritionResult = """
                        🔥 Kalori: ${data.kalori} kcal
                        🥩 Protein: ${data.protein} g
                        🍚 Karbohidrat: ${data.karbohidrat} g
                        🥑 Lemak: ${data.lemak} g
                    """.trimIndent()
                }.onFailure {
                    isError = true
                    nutritionResult = "Gagal mendeteksi makanan. Foto kemungkinan buram atau tidak dikenali oleh Gemini AI."
                }
                isLoading = false
            }
        }
    }

    val cameraLauncher = rememberCameraLauncher { imageBytes ->
        processImageScanning(imageBytes)
    }

    Box(modifier = Modifier.fillMaxSize().background(darkBlack)) {

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = maroon)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Gemini AI sedang menganalisis makananmu...", color = Color.White)
            } else if (nutritionResult != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isError) MaterialTheme.colorScheme.errorContainer else Color(0xFF1E1E1E)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(16.dp).fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isError) "⚠️ Edge Case Terdeteksi" else "✨ Hasil Analisis AI",
                            color = if (isError) MaterialTheme.colorScheme.onErrorContainer else pinkAccent,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = nutritionResult!!,
                            color = if (isError) MaterialTheme.colorScheme.onErrorContainer else Color.White,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )

                        // Skenario tombol Coba Lagi jika terjadi error / fallback
                        if (isError) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { cameraLauncher() },
                                colors = ButtonDefaults.buttonColors(containerColor = maroon)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Retry")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Ambil Foto Ulang", color = Color.White)
                            }
                        }
                    }
                }
            } else {
                Text(
                    "Arahkan kamera ke porsi makanmu untuk memindai nutrisi",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }

        if (!isLoading) {
            FloatingActionButton(
                onClick = { cameraLauncher() },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(32.dp)
                    .size(72.dp),
                shape = CircleShape,
                containerColor = maroon,
                contentColor = Color.White
            ) {
                Icon(
                    Icons.Filled.CameraAlt,
                    contentDescription = "Ambil Foto",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}