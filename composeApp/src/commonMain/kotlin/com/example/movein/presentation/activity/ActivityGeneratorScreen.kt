package com.example.movein.presentation.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.movein.presentation.components.*
import com.example.movein.presentation.theme.MoveInTheme
import com.example.movein.presentation.theme.NoteAITheme

private data class ActivityRecommendation(
    val title: String,
    val category: String,
    val duration: String,
    val description: String,
    val points: Int,
    val emoji: String
)

private val activityMap = mapOf(
    "bored" to listOf(
        ActivityRecommendation("Mini Walk 10 Menit", "Kesehatan", "10 m", "Keluar sebentar, lihat langit, lalu jalan santai.", 10, "🚶"),
        ActivityRecommendation("Rapikan Satu Sudut", "Produktif", "7 m", "Pilih satu sudut meja atau kamar saja.", 12, "🧹")
    ),
    "sad" to listOf(
        ActivityRecommendation("Minum Air Hangat", "Self-care", "5 m", "Ambil minuman hangat, duduk sebentar.", 10, "☕"),
        ActivityRecommendation("Tulis 3 Kalimat Jujur", "Journaling", "8 m", "Tulis apa yang kamu rasakan tanpa dirapikan.", 15, "📝")
    ),
    "tired" to listOf(
        ActivityRecommendation("Power Nap Singkat", "Recovery", "15 m", "Pasang timer, rebahkan badan sebentar.", 12, "😴"),
        ActivityRecommendation("Stretch Bahu dan Leher", "Kesehatan", "5 m", "Putar bahu pelan, tarik napas, lemaskan leher.", 10, "🧘")
    ),
    "excited" to listOf(
        ActivityRecommendation("Sprint Tugas 25 Menit", "Produktif", "25 m", "Kerjakan satu tugas tanpa distraksi.", 25, "🚀"),
        ActivityRecommendation("Workout Mini", "Kesehatan", "12 m", "Lakukan gerakan ringan untuk energi positif.", 18, "🏃")
    ),
    "gabut" to listOf(
        ActivityRecommendation("Random Skill 15 Menit", "Self-improvement", "15 m", "Cari satu tutorial singkat apa saja.", 15, "🧠"),
        ActivityRecommendation("Eksperimen Foto", "Kreatif", "15 m", "Ambil 5 foto benda biasa dengan angle unik.", 15, "📷")
    ),
    "stress" to listOf(
        ActivityRecommendation("Breathing Reset", "Recovery", "3 m", "Tarik napas 4 hitungan, tahan 2, buang 6.", 12, "🌬️"),
        ActivityRecommendation("Jauhkan Layar Sebentar", "Detox", "10 m", "Letakkan HP/laptop, berdiri, minum air.", 14, "📵")
    )
)

@Composable
fun ActivityGeneratorScreen(
    moodId: String,
    userName: String,
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit
) {
    val mood = moveInMoods.firstOrNull { it.id == moodId } ?: moveInMoods.first()
    val activities = activityMap[mood.id].orEmpty().ifEmpty { activityMap.values.flatten() }
    var selectedActivity by remember(mood.id) { mutableStateOf(activities.random()) }

    NoteAITheme(darkTheme = true) {
        MoveInScaffold {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MoveInTheme.colors.textPrimary)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(onClick = onLogout) {
                            Text("Logout", color = MoveInTheme.colors.errorRed)
                        }
                    }
                }

                item {
                    MoveInPill(
                        text = "${mood.emoji} ${mood.title} Space",
                        selected = true,
                        color = mood.accent.copy(alpha = 0.2f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Rekomendasi buat kamu",
                        style = MoveInTheme.typography.displayLarge.copy(color = MoveInTheme.colors.textPrimary)
                    )
                    Text(
                        text = "Untuk $userName, mulai dari aktivitas kecil dulu. Nggak harus sempurna.",
                        style = MoveInTheme.typography.bodyLarge.copy(color = MoveInTheme.colors.textSecondary)
                    )
                }

                item {
                    MoveInCard(
                        borderColor = mood.accent.copy(alpha = 0.3f),
                        contentPadding = 24.dp
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(mood.accent.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(selectedActivity.emoji, fontSize = 32.sp)
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            selectedActivity.category.uppercase(),
                            style = MoveInTheme.typography.labelSmall.copy(color = mood.accent, letterSpacing = 1.sp)
                        )
                        Text(
                            selectedActivity.title,
                            style = MoveInTheme.typography.displayMedium.copy(color = MoveInTheme.colors.textPrimary)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            selectedActivity.description,
                            style = MoveInTheme.typography.bodyMedium.copy(color = MoveInTheme.colors.textSecondary)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            MoveInPill(text = selectedActivity.duration, selected = false)
                            MoveInPill(text = "+${selectedActivity.points} Momentum", selected = false)
                        }
                    }
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        MoveInPrimaryButton(
                            text = "Acak Lagi",
                            onClick = { selectedActivity = activities.random() },
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Pilih Mood Lain", color = MoveInTheme.colors.textSecondary)
                        }
                    }
                }
            }
        }
    }
}
