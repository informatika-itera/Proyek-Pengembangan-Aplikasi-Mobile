package com.example.noteai.presentation.activity

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noteai.presentation.components.InfoPill
import com.example.noteai.presentation.components.MoveInPrimaryButton
import com.example.noteai.presentation.components.MoveInScreenFrame
import com.example.noteai.presentation.components.MoveInSecondaryButton
import com.example.noteai.presentation.components.moveInMoods

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
        ActivityRecommendation(
            title = "Mini Walk 10 Menit",
            category = "Kesehatan",
            duration = "10 menit",
            description = "Keluar sebentar, lihat langit, lalu jalan santai tanpa target. Cukup bikin badan bergerak.",
            points = 10,
            emoji = "🚶"
        ),
        ActivityRecommendation(
            title = "Rapikan Satu Sudut",
            category = "Produktif",
            duration = "7 menit",
            description = "Pilih satu sudut meja atau kamar. Jangan semua. Satu area kecil saja sudah cukup.",
            points = 12,
            emoji = "🧹"
        ),
        ActivityRecommendation(
            title = "Cari Lagu Baru",
            category = "Hiburan",
            duration = "5 menit",
            description = "Putar satu lagu random dari genre yang jarang kamu dengar. Biarkan mood ikut berubah.",
            points = 8,
            emoji = "🎧"
        )
    ),
    "sad" to listOf(
        ActivityRecommendation(
            title = "Minum Air Hangat",
            category = "Self-care",
            duration = "5 menit",
            description = "Ambil minuman hangat, duduk sebentar, dan jangan paksa diri untuk langsung baik-baik saja.",
            points = 10,
            emoji = "☕"
        ),
        ActivityRecommendation(
            title = "Tulis 3 Kalimat Jujur",
            category = "Journaling",
            duration = "8 menit",
            description = "Tulis apa yang kamu rasakan tanpa dirapikan. Tidak perlu bagus, yang penting keluar.",
            points = 15,
            emoji = "📝"
        ),
        ActivityRecommendation(
            title = "Chat Satu Teman Aman",
            category = "Sosial",
            duration = "10 menit",
            description = "Kirim pesan sederhana ke orang yang bikin kamu merasa aman. Tidak perlu cerita panjang dulu.",
            points = 15,
            emoji = "💬"
        )
    ),
    "tired" to listOf(
        ActivityRecommendation(
            title = "Power Nap Singkat",
            category = "Recovery",
            duration = "15 menit",
            description = "Pasang timer, rebahkan badan, dan izinkan diri berhenti sebentar tanpa rasa bersalah.",
            points = 12,
            emoji = "😴"
        ),
        ActivityRecommendation(
            title = "Stretch Bahu dan Leher",
            category = "Kesehatan",
            duration = "5 menit",
            description = "Putar bahu pelan, tarik napas, lalu lemaskan leher. Aktivitas kecil untuk reset badan.",
            points = 10,
            emoji = "🧘"
        ),
        ActivityRecommendation(
            title = "Mode Low Power",
            category = "Santai",
            duration = "20 menit",
            description = "Pilih satu aktivitas ringan: mandi, makan, atau rebahan sadar. Jangan multitasking dulu.",
            points = 8,
            emoji = "🔋"
        )
    ),
    "excited" to listOf(
        ActivityRecommendation(
            title = "Sprint Tugas 25 Menit",
            category = "Produktif",
            duration = "25 menit",
            description = "Pilih satu tugas paling dekat deadline. Kerjakan 25 menit tanpa buka aplikasi lain.",
            points = 25,
            emoji = "🚀"
        ),
        ActivityRecommendation(
            title = "Brain Dump Ide",
            category = "Self-improvement",
            duration = "10 menit",
            description = "Tulis semua ide yang muncul. Jangan disaring dulu. Nanti baru pilih yang paling realistis.",
            points = 18,
            emoji = "💡"
        ),
        ActivityRecommendation(
            title = "Workout Mini",
            category = "Kesehatan",
            duration = "12 menit",
            description = "Lakukan gerakan ringan. Fokusnya bukan berat, tapi menjaga energi tetap positif.",
            points = 18,
            emoji = "🏃"
        )
    ),
    "gabut" to listOf(
        ActivityRecommendation(
            title = "Random Skill 15 Menit",
            category = "Self-improvement",
            duration = "15 menit",
            description = "Cari satu tutorial singkat: desain, foto, coding, masak, atau bahasa baru. Coba sedikit saja.",
            points = 15,
            emoji = "🧠"
        ),
        ActivityRecommendation(
            title = "Eksperimen Foto",
            category = "Kreatif",
            duration = "15 menit",
            description = "Ambil 5 foto benda biasa di sekitarmu dengan angle yang lebih niat.",
            points = 15,
            emoji = "📷"
        ),
        ActivityRecommendation(
            title = "Declutter Galeri",
            category = "Produktif",
            duration = "10 menit",
            description = "Hapus screenshot atau foto blur yang tidak penting. Bikin ruang digital terasa lega.",
            points = 10,
            emoji = "🗂️"
        )
    ),
    "stress" to listOf(
        ActivityRecommendation(
            title = "Breathing Reset",
            category = "Recovery",
            duration = "3 menit",
            description = "Tarik napas 4 hitungan, tahan 2, buang 6. Ulangi sampai pikiran sedikit melambat.",
            points = 12,
            emoji = "🌬️"
        ),
        ActivityRecommendation(
            title = "Pecah Masalah Jadi 1 Langkah",
            category = "Produktif",
            duration = "8 menit",
            description = "Tulis masalahmu, lalu pilih satu langkah terkecil yang bisa dilakukan sekarang.",
            points = 18,
            emoji = "🧩"
        ),
        ActivityRecommendation(
            title = "Jauhkan Layar Sebentar",
            category = "Detox",
            duration = "10 menit",
            description = "Letakkan HP/laptop, berdiri, minum air, lalu kembali saat kepala lebih tenang.",
            points = 14,
            emoji = "📵"
        )
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
    val activities = activityMap[mood.id].orEmpty().ifEmpty {
        activityMap.values.flatten()
    }

    var selectedActivity by remember(mood.id) {
        mutableStateOf(activities.random())
    }

    MoveInScreenFrame(
        accent = mood.accent,
        background = mood.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(onClick = onNavigateBack) {
                Text(
                    text = "← Mood",
                    color = Color.White.copy(alpha = 0.75f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            TextButton(onClick = onLogout) {
                Text(
                    text = "Logout",
                    color = Color.White.copy(alpha = 0.55f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            color = mood.accent.copy(alpha = 0.16f),
            shape = RoundedCornerShape(999.dp),
            border = BorderStroke(1.dp, mood.accent.copy(alpha = 0.35f))
        ) {
            Text(
                text = "${mood.emoji} ${mood.title} Space",
                color = mood.accent,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Rekomendasi buat kamu",
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 36.sp
        )

        Text(
            text = "Untuk $userName, mulai dari aktivitas kecil dulu. Nggak harus sempurna.",
            color = Color.White.copy(alpha = 0.64f),
            fontSize = 14.sp,
            lineHeight = 21.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        Card(
            shape = RoundedCornerShape(34.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.10f)
            ),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.10f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(78.dp)
                        .clip(CircleShape)
                        .background(mood.accent.copy(alpha = 0.18f))
                        .border(
                            width = 1.dp,
                            color = mood.accent.copy(alpha = 0.35f),
                            shape = CircleShape
                        )
                ) {
                    Text(
                        text = selectedActivity.emoji,
                        fontSize = 36.sp
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                Text(
                    text = selectedActivity.category.uppercase(),
                    color = mood.accent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.2.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = selectedActivity.title,
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 31.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = selectedActivity.description,
                    color = Color.White.copy(alpha = 0.72f),
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(22.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    InfoPill(
                        text = selectedActivity.duration,
                        accent = mood.accent
                    )

                    InfoPill(
                        text = "+${selectedActivity.points} Momentum",
                        accent = mood.accent
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        MoveInPrimaryButton(
            text = "Acak Lagi",
            accent = mood.accent,
            onClick = {
                val nextActivities = activities.filter { it != selectedActivity }
                selectedActivity = nextActivities.ifEmpty { activities }.random()
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        MoveInSecondaryButton(
            text = "Pilih Mood Lain",
            onClick = onNavigateBack
        )
    }
}