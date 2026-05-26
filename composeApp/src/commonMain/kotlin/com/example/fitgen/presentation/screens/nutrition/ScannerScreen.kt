package com.example.fitgen.presentation.screens.nutrition

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    var nutritionResult by remember { mutableStateOf<String?>(null) }

    val cameraLauncher = rememberCameraLauncher { imageBytes ->
        if (imageBytes != null) {
            isLoading = true
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
                    nutritionResult = "Gagal mendeteksi makanan. Coba foto ulang dengan pencahayaan yang terang."
                }
                isLoading = false
            }
        }
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
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(16.dp).fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "✨ Hasil Analisis AI",
                            color = pinkAccent,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            nutritionResult!!,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            } else {
                Text(
                    "Arahkan kamera ke porsi makanmu untuk memindai nutrisi",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

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