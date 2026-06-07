package com.example.noteai.presentation.mood

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noteai.presentation.components.*
import com.example.noteai.presentation.theme.MoveInTheme
import com.example.noteai.presentation.theme.NoteAITheme

@Composable
fun MoodSelectionScreen(
    userName: String,
    onMoodSelected: (String) -> Unit,
    onLogout: () -> Unit
) {
    NoteAITheme(darkTheme = true) {
        MoveInScaffold {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Halo, $userName",
                                style = MoveInTheme.typography.labelSmall.copy(color = MoveInTheme.colors.textSecondary)
                            )
                            Text(
                                text = "Mood kamu hari ini apa?",
                                style = MoveInTheme.typography.displayLarge.copy(color = MoveInTheme.colors.textPrimary)
                            )
                        }
                        TextButton(onClick = onLogout) {
                            Text("Logout", color = MoveInTheme.colors.errorRed)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Pilih satu mental space. MoveIn akan kasih rekomendasi aktivitas kecil yang sesuai.",
                        style = MoveInTheme.typography.bodyMedium.copy(color = MoveInTheme.colors.textMuted)
                    )
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        moveInMoods.chunked(2).forEach { rowMoods ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                rowMoods.forEach { mood ->
                                    MoodCard(
                                        mood = mood,
                                        onClick = { onMoodSelected(mood.id) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (rowMoods.size == 1) Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                item {
                    MoveInCard(backgroundColor = MoveInTheme.colors.surfaceSecondary.copy(alpha = 0.5f)) {
                        Text(
                            text = "Mental Recovery Companion",
                            style = MoveInTheme.typography.labelSmall.copy(color = MoveInTheme.colors.accentBlue)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Login → pilih mood → dapat aktivitas random → acak lagi.",
                            style = MoveInTheme.typography.bodyMedium.copy(color = MoveInTheme.colors.textSecondary)
                        )
                    }
                }
            }
        }
    }
}
