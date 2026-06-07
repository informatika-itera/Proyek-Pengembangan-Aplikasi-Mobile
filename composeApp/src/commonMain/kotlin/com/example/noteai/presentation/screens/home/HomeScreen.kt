package com.example.noteai.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noteai.presentation.AppState
import com.example.noteai.presentation.JourneyLog
import com.example.noteai.presentation.MentalTheme
import com.example.noteai.presentation.components.AiValidationDialog
import com.example.noteai.presentation.components.BentoCard

private fun getDynamicAiResponse(text: String): String {
    val lowerText = text.lowercase()

    return when {
        lowerText.contains("tugas") ||
                lowerText.contains("kerja") ||
                lowerText.contains("dosen") -> {
            "Tugas yang numpuk memang bikin napas terasa sesak. Valid kalau kamu capek. Tinggalkan layar 5 menit, lalu mulai dari satu langkah kecil."
        }

        lowerText.contains("sepi") ||
                lowerText.contains("sendiri") ||
                lowerText.contains("sedih") -> {
            "Kadang rasa sepi datang tiba-tiba dan terasa berat. Tidak apa-apa merasa begitu. Coba tarik napas pelan dan lakukan satu hal kecil yang bikin nyaman."
        }

        lowerText.contains("bingung") ||
                lowerText.contains("overthinking") ||
                lowerText.contains("pusing") -> {
            "Terlalu banyak isi kepala memang bikin bising. Yuk pause sebentar. Tidak semua harus dijawab malam ini."
        }

        lowerText.contains("capek") ||
                lowerText.contains("lelah") -> {
            "Kalau tubuhmu capek, istirahat bukan malas. Ambil jeda kecil dulu, lalu lanjut pelan-pelan."
        }

        else -> {
            "Aku dengar keluh kesahmu. Tidak apa-apa merasa seperti ini. Validasi emosimu, tarik napas pelan-pelan, kamu sudah bertahan dengan baik hari ini."
        }
    }
}

@Composable
fun HomeScreen(
    theme: MentalTheme,
    isLight: Boolean,
    addJourneyLog: (JourneyLog) -> Unit,
    addMomentum: (Int) -> Unit,
    appState: AppState,
    setAppState: (AppState) -> Unit,
    modifier: Modifier = Modifier
) {
    var yapText by rememberSaveable { mutableStateOf("") }
    var isBurning by remember { mutableStateOf(false) }
    var aiModalOpen by remember { mutableStateOf(false) }
    var aiMessage by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(theme.gap)
    ) {
        // Mental Space Badge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        if (isLight) {
                            theme.accentLight.copy(alpha = 0.1f)
                        } else {
                            theme.accentDark.copy(alpha = 0.1f)
                        }
                    )
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "MENTAL SPACE: ${theme.name}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = if (isLight) theme.accentLight else theme.accentDark
                )
            }
        }

        // YAPPING SPACE
        BentoCard(
            theme = theme,
            isLight = isLight,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(
                                if (isLight) Color.Black.copy(alpha = 0.05f)
                                else Color.White.copy(alpha = 0.05f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Whatshot,
                            contentDescription = null,
                            tint = if (isLight) theme.accentLight else theme.accentDark,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Yapping Space",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isLight) theme.accentLight else theme.accentDark
                    )
                }

                Text(
                    text = "${yapText.length}/250",
                    fontSize = 10.sp,
                    color = if (isLight) Color(0xFFA3A3A3) else Color(0xFF737373)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp)
            ) {
                if (isBurning) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Whatshot,
                            contentDescription = null,
                            tint = Color(0xFFF97316),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                } else {
                    OutlinedTextField(
                        value = yapText,
                        onValueChange = {
                            if (it.length <= 250) {
                                yapText = it
                            }
                        },
                        placeholder = {
                            Text(
                                text = "Ruangan ini aman. Tulis sepuasnya (max 250 karakter). Aplikasi akan bereaksi sesuai curhatanmu...",
                                fontSize = 14.sp,
                                color = Color(0xFF737373)
                            )
                        },
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = if (isLight) {
                                Color.Black.copy(alpha = 0.05f)
                            } else {
                                Color.Black.copy(alpha = 0.2f)
                            },
                            unfocusedContainerColor = if (isLight) {
                                Color.Black.copy(alpha = 0.05f)
                            } else {
                                Color.Black.copy(alpha = 0.2f)
                            },
                            focusedBorderColor = if (isLight) theme.accentLight else theme.accentDark,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = if (isLight) Color(0xFF262626) else Color.White,
                            unfocusedTextColor = if (isLight) Color(0xFF262626) else Color.White
                        )
                    )

                    if (yapText.isNotBlank()) {
                        IconButton(
                            onClick = {
                                isBurning = true

                                val response = getDynamicAiResponse(yapText)

                                setAppState(AppState.OVERWHELMED)

                                addJourneyLog(
                                    JourneyLog(
                                        time = "Baru saja",
                                        mood = "Overwhelmed",
                                        task = "Yapping Space",
                                        result = "Divalidasi AI",
                                        type = "yapping",
                                        appState = AppState.OVERWHELMED,
                                        color = Color(0xFFF87171),
                                        bgColor = Color(0xFFF87171).copy(alpha = 0.1f)
                                    )
                                )

                                addMomentum(3)

                                aiMessage = response
                                yapText = ""
                                isBurning = false
                                aiModalOpen = true
                            },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isLight) Color.Black.copy(alpha = 0.1f)
                                    else Color.White.copy(alpha = 0.1f)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = null,
                                tint = if (isLight) theme.accentLight else theme.accentDark,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        // DASHBOARD FITUR UTAMA
        MoveInIntegratedDashboard(
            theme = theme,
            isLight = isLight,
            addJourneyLog = addJourneyLog,
            addMomentum = addMomentum,
            setAppState = setAppState,
            modifier = Modifier.fillMaxWidth()
        )
    }

    if (aiModalOpen) {
        AiValidationDialog(
            message = aiMessage,
            theme = theme,
            isLight = isLight,
            onDismiss = {
                aiModalOpen = false
            }
        )
    }
}
