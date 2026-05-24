package com.example.noteai.presentation.mood

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noteai.presentation.components.MoodCard
import com.example.noteai.presentation.components.MoveInBentoCard
import com.example.noteai.presentation.components.MoveInScreenFrame
import com.example.noteai.presentation.components.moveInMoods

@Composable
fun MoodSelectionScreen(
    userName: String,
    onMoodSelected: (String) -> Unit,
    onLogout: () -> Unit
) {
    MoveInScreenFrame(
        accent = Color(0xFF22D3EE),
        background = Color(0xFF050505),
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Halo, $userName",
                    color = Color.White.copy(alpha = 0.72f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "Mood kamu hari ini apa?",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 34.sp
                )
            }

            TextButton(onClick = onLogout) {
                Text(
                    text = "Logout",
                    color = Color.White.copy(alpha = 0.62f)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Pilih satu mental space. MoveIn akan kasih rekomendasi aktivitas kecil yang sesuai.",
            color = Color.White.copy(alpha = 0.62f),
            fontSize = 14.sp,
            lineHeight = 21.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        moveInMoods.chunked(2).forEach { rowMoods ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowMoods.forEach { mood ->
                    MoodCard(
                        mood = mood,
                        onClick = {
                            onMoodSelected(mood.id)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                if (rowMoods.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        Spacer(modifier = Modifier.height(22.dp))

        MoveInBentoCard {
            Text(
                text = "Sprint 2 Core Flow",
                color = Color(0xFF22D3EE),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Login → pilih mood → dapat aktivitas random → acak lagi.",
                color = Color.White.copy(alpha = 0.72f),
                fontSize = 13.sp,
                lineHeight = 20.sp
            )
        }
    }
}