package com.example.movein.presentation.screens.home

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.movein.presentation.AppState
import com.example.movein.presentation.JourneyLog
import com.example.movein.presentation.MentalTheme
import com.example.movein.presentation.components.BentoCard
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

enum class IntegratedMood(
    val label: String,
    val emoji: String,
    val ambienceTitle: String,
    val ambienceDescription: String,
    val appState: AppState
) {
    BOSAN(
        label = "Bosan",
        emoji = "😐",
        ambienceTitle = "Butuh Variasi.",
        ambienceDescription = "Kamu butuh aktivitas kecil yang bikin suasana terasa baru.",
        appState = AppState.NEUTRAL
    ),
    SEDIH(
        label = "Sedih",
        emoji = "😔",
        ambienceTitle = "Butuh Ditemani.",
        ambienceDescription = "Tidak apa-apa pelan dulu. Pilih aktivitas yang terasa lembut.",
        appState = AppState.OVERWHELMED
    ),
    CAPEK(
        label = "Capek",
        emoji = "🥱",
        ambienceTitle = "Butuh Istirahat.",
        ambienceDescription = "Energi kamu sedang rendah. Fokus ke recovery kecil.",
        appState = AppState.RECOVERING
    ),
    SEMANGAT(
        label = "Semangat",
        emoji = "🔥",
        ambienceTitle = "Siap Bergerak.",
        ambienceDescription = "Energi kamu bagus. Arahkan ke tugas kecil yang jelas.",
        appState = AppState.NEUTRAL
    ),
    GABUT(
        label = "Gabut",
        emoji = "🫠",
        ambienceTitle = "Butuh Arah.",
        ambienceDescription = "Daripada scroll tanpa tujuan, coba satu aktivitas ringan.",
        appState = AppState.NEUTRAL
    ),
    STRESS(
        label = "Stress",
        emoji = "😵‍💫",
        ambienceTitle = "Butuh Tenang.",
        ambienceDescription = "Kepala sedang penuh. Pecah semuanya jadi satu langkah kecil.",
        appState = AppState.OVERWHELMED
    )
}

data class IntegratedActivity(
    val title: String,
    val description: String,
    val category: String
)

private val activitiesByMood = mapOf(
    IntegratedMood.BOSAN to listOf(
        IntegratedActivity(
            title = "Cari satu lagu baru",
            description = "Dengarkan lagu yang belum pernah kamu dengar untuk mengganti mood.",
            category = "Hiburan"
        ),
        IntegratedActivity(
            title = "Jalan santai 10 menit",
            description = "Keluar sebentar dan lihat sekitar tanpa buru-buru.",
            category = "Kesehatan"
        ),
        IntegratedActivity(
            title = "Rapikan satu sudut meja",
            description = "Pilih satu area kecil saja agar tidak terasa berat.",
            category = "Produktif"
        )
    ),
    IntegratedMood.SEDIH to listOf(
        IntegratedActivity(
            title = "Tulis perasaan 3 menit",
            description = "Tulis tanpa diedit. Tujuannya bukan bagus, tapi lega.",
            category = "Self-improvement"
        ),
        IntegratedActivity(
            title = "Minum air hangat",
            description = "Ambil jeda kecil dan beri tubuh rasa nyaman.",
            category = "Kesehatan"
        ),
        IntegratedActivity(
            title = "Chat teman dekat",
            description = "Cukup bilang kamu butuh ditemani sebentar.",
            category = "Sosial"
        )
    ),
    IntegratedMood.CAPEK to listOf(
        IntegratedActivity(
            title = "Power nap 15 menit",
            description = "Set timer sebentar agar tubuh punya waktu reset.",
            category = "Santai"
        ),
        IntegratedActivity(
            title = "Stretching ringan",
            description = "Regangkan bahu, leher, tangan, dan punggung pelan-pelan.",
            category = "Kesehatan"
        ),
        IntegratedActivity(
            title = "Kurangi layar 10 menit",
            description = "Letakkan HP sebentar dan biarkan mata istirahat.",
            category = "Self-improvement"
        )
    ),
    IntegratedMood.SEMANGAT to listOf(
        IntegratedActivity(
            title = "Selesaikan 1 tiny task",
            description = "Pilih tugas kecil yang bisa selesai kurang dari 10 menit.",
            category = "Produktif"
        ),
        IntegratedActivity(
            title = "Buat mini to-do list",
            description = "Tulis 3 target kecil agar energimu lebih terarah.",
            category = "Produktif"
        ),
        IntegratedActivity(
            title = "Belajar skill mikro",
            description = "Baca atau tonton satu materi pendek yang berguna.",
            category = "Self-improvement"
        )
    ),
    IntegratedMood.GABUT to listOf(
        IntegratedActivity(
            title = "Coba resep sederhana",
            description = "Buat minuman atau makanan kecil dari bahan yang ada.",
            category = "Hiburan"
        ),
        IntegratedActivity(
            title = "Bereskan galeri HP",
            description = "Hapus 20 foto tidak penting agar storage terasa lega.",
            category = "Produktif"
        ),
        IntegratedActivity(
            title = "Mini challenge 5 menit",
            description = "Tantang diri menyelesaikan satu hal kecil.",
            category = "Santai"
        )
    ),
    IntegratedMood.STRESS to listOf(
        IntegratedActivity(
            title = "Tarik napas 4-4-4",
            description = "Tarik 4 detik, tahan 4 detik, hembuskan 4 detik.",
            category = "Kesehatan"
        ),
        IntegratedActivity(
            title = "Brain dump",
            description = "Keluarkan semua isi kepala ke catatan agar tidak menumpuk.",
            category = "Self-improvement"
        ),
        IntegratedActivity(
            title = "Pecah tugas jadi 1 langkah",
            description = "Jangan pikirkan semuanya. Pilih satu langkah pertama saja.",
            category = "Produktif"
        )
    )
)

private val fallbackQuotes = listOf(
    "Progress kecil tetap progress.",
    "Tidak semua hari harus produktif maksimal.",
    "Mulai dari satu langkah kecil dulu.",
    "Istirahat bukan gagal. Itu bagian dari proses.",
    "Yang penting kamu tetap balik bergerak."
)

@Composable
fun MoveInIntegratedDashboard(
    theme: MentalTheme,
    isLight: Boolean,
    addJourneyLog: (JourneyLog) -> Unit,
    addMomentum: (Int) -> Unit,
    setAppState: (AppState) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMoodIndex by rememberSaveable { mutableStateOf(0) }
    var randomActivityIndex by rememberSaveable { mutableStateOf(0) }
    var quoteIndex by rememberSaveable { mutableStateOf(0) }
    var showAmbience by rememberSaveable { mutableStateOf(false) }

    var weeklyResetKey by rememberSaveable { mutableStateOf(getWeekKey()) }
    var activityCountMap by remember {
        mutableStateOf<Map<String, Int>>(emptyMap())
    }

    val currentWeekKey = getWeekKey()

    LaunchedEffect(currentWeekKey) {
        if (weeklyResetKey != currentWeekKey) {
            weeklyResetKey = currentWeekKey
            activityCountMap = emptyMap()
        }
    }

    val moods = IntegratedMood.values().toList()
    val selectedMood = moods[selectedMoodIndex]
    val activities = activitiesByMood[selectedMood].orEmpty()

    val currentRandomActivity = activities.getOrElse(randomActivityIndex) {
        activities.first()
    }

    val favoriteActivityTitle = activityCountMap.maxByOrNull { it.value }?.key
    val favoriteActivity = activitiesByMood.values
        .flatten()
        .firstOrNull { it.title == favoriteActivityTitle }

    LaunchedEffect(selectedMoodIndex) {
        randomActivityIndex = 0

        while (true) {
            delay(4000)

            if (activities.isNotEmpty()) {
                randomActivityIndex = if (activities.size > 1) {
                    activities.indices
                        .filter { it != randomActivityIndex }
                        .random()
                } else {
                    0
                }
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(theme.gap)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(theme.gap),
            verticalAlignment = Alignment.Top
        ) {
            MoodAmbienceSquareCard(
                theme = theme,
                isLight = isLight,
                selectedMoodIndex = selectedMoodIndex,
                showAmbience = showAmbience,
                onMoodSelected = { index ->
                    selectedMoodIndex = index
                    setAppState(moods[index].appState)
                    showAmbience = true
                },
                onBackToMood = {
                    showAmbience = false
                },
                modifier = Modifier
                    .weight(1f)
                    .height(250.dp)
            )

            RandomActivitySquareCard(
                theme = theme,
                isLight = isLight,
                activity = currentRandomActivity,
                onDone = {
                    activityCountMap = activityCountMap.toMutableMap().apply {
                        this[currentRandomActivity.title] =
                            (this[currentRandomActivity.title] ?: 0) + 1
                    }

                    addMomentum(5)

                    addJourneyLog(
                        JourneyLog(
                            time = "Baru saja",
                            mood = selectedMood.label,
                            task = currentRandomActivity.title,
                            result = "Random Activity Done",
                            type = "activity",
                            appState = selectedMood.appState,
                            color = if (isLight) theme.accentLight else theme.accentDark,
                            bgColor = if (isLight) {
                                theme.accentLight.copy(alpha = 0.10f)
                            } else {
                                theme.accentDark.copy(alpha = 0.10f)
                            }
                        )
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .height(250.dp)
            )
        }

        FavoriteWeeklyCard(
            theme = theme,
            isLight = isLight,
            favoriteActivity = favoriteActivity,
            favoriteCount = favoriteActivityTitle?.let { activityCountMap[it] } ?: 0,
            weeklyResetKey = weeklyResetKey
        )

        KtorQuoteCard(
            theme = theme,
            isLight = isLight,
            quote = fallbackQuotes[quoteIndex],
            onRefreshQuote = {
                quoteIndex = fallbackQuotes.indices
                    .filter { it != quoteIndex }
                    .random()
            }
        )
    }
}

@Composable
private fun MoodAmbienceSquareCard(
    theme: MentalTheme,
    isLight: Boolean,
    selectedMoodIndex: Int,
    showAmbience: Boolean,
    onMoodSelected: (Int) -> Unit,
    onBackToMood: () -> Unit,
    modifier: Modifier = Modifier
) {
    val moods = IntegratedMood.values().toList()
    val selectedMood = moods[selectedMoodIndex]

    BentoCard(
        theme = theme,
        isLight = isLight,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.GraphicEq,
                contentDescription = null,
                tint = if (isLight) theme.accentLight else theme.accentDark,
                modifier = Modifier.size(13.dp)
            )

            Spacer(modifier = Modifier.width(5.dp))

            Text(
                text = "Mood Ambience",
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (isLight) theme.accentLight else theme.accentDark,
                modifier = Modifier.weight(1f)
            )

            if (showAmbience) {
                IconButton(
                    onClick = onBackToMood,
                    modifier = Modifier.size(22.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = Color(0xFFA3A3A3),
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Crossfade(
            targetState = showAmbience,
            animationSpec = tween(durationMillis = 450),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { ambienceVisible ->
            if (ambienceVisible) {
                AmbienceContent(
                    theme = theme,
                    isLight = isLight,
                    selectedMood = selectedMood,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                MoodSliderContent(
                    moods = moods,
                    selectedMoodIndex = selectedMoodIndex,
                    theme = theme,
                    isLight = isLight,
                    onMoodSelected = onMoodSelected,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun MoodSliderContent(
    moods: List<IntegratedMood>,
    selectedMoodIndex: Int,
    theme: MentalTheme,
    isLight: Boolean,
    onMoodSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(142.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            itemsIndexed(moods) { index, mood ->
                val selected = index == selectedMoodIndex

                MoodSliderItem(
                    mood = mood,
                    selected = selected,
                    theme = theme,
                    isLight = isLight,
                    onClick = {
                        onMoodSelected(index)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Geser kanan/kiri.",
            fontSize = 7.sp,
            lineHeight = 9.sp,
            color = if (isLight) Color(0xFF737373) else Color(0xFFA3A3A3),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun MoodSliderItem(
    mood: IntegratedMood,
    selected: Boolean,
    theme: MentalTheme,
    isLight: Boolean,
    onClick: () -> Unit
) {
    val itemWidth = if (selected) 86.dp else 72.dp
    val itemHeight = if (selected) 130.dp else 116.dp

    Column(
        modifier = Modifier
            .width(itemWidth)
            .height(itemHeight)
            .clip(RoundedCornerShape(24.dp))
            .background(
                if (selected) {
                    if (isLight) {
                        theme.accentLight.copy(alpha = 0.14f)
                    } else {
                        theme.accentDark.copy(alpha = 0.18f)
                    }
                } else {
                    if (isLight) {
                        Color.Black.copy(alpha = 0.04f)
                    } else {
                        Color.White.copy(alpha = 0.06f)
                    }
                }
            )
            .border(
                width = 1.dp,
                color = if (selected) {
                    if (isLight) {
                        theme.accentLight.copy(alpha = 0.45f)
                    } else {
                        theme.accentDark.copy(alpha = 0.45f)
                    }
                } else {
                    Color.Transparent
                },
                shape = RoundedCornerShape(24.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = mood.emoji,
            fontSize = if (selected) 30.sp else 25.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = mood.label,
            fontSize = if (selected) 12.sp else 10.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = if (selected) {
                if (isLight) theme.accentLight else theme.accentDark
            } else {
                if (isLight) Color(0xFF737373) else Color(0xFFA3A3A3)
            }
        )
    }
}

@Composable
private fun AmbienceContent(
    theme: MentalTheme,
    isLight: Boolean,
    selectedMood: IntegratedMood,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(
                if (isLight) {
                    Color.White.copy(alpha = 0.78f)
                } else {
                    Color.Black.copy(alpha = 0.24f)
                }
            )
            .border(
                width = 1.dp,
                color = if (isLight) {
                    theme.accentLight.copy(alpha = 0.22f)
                } else {
                    theme.accentDark.copy(alpha = 0.18f)
                },
                shape = RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 14.dp, vertical = 14.dp)
    ) {
        Column {
            Text(
                text = selectedMood.ambienceTitle,
                fontSize = 19.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = if (isLight) Color(0xFF171717) else Color.White
            )

            Spacer(modifier = Modifier.height(9.dp))

            Text(
                text = selectedMood.label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (isLight) theme.accentLight else theme.accentDark
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = selectedMood.ambienceDescription,
                fontSize = 9.sp,
                lineHeight = 12.sp,
                maxLines = 6,
                overflow = TextOverflow.Ellipsis,
                color = if (isLight) Color(0xFF525252) else Color(0xFFA3A3A3)
            )
        }
    }
}

@Composable
private fun RandomActivitySquareCard(
    theme: MentalTheme,
    isLight: Boolean,
    activity: IntegratedActivity,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    BentoCard(
        theme = theme,
        isLight = isLight,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(26.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        if (isLight) Color.Black.copy(alpha = 0.05f)
                        else Color.White.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Stars,
                    contentDescription = null,
                    tint = if (isLight) theme.accentLight else theme.accentDark,
                    modifier = Modifier.size(12.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Random Activity",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (isLight) theme.accentLight else theme.accentDark
                )

            }

            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = Color(0xFFA3A3A3),
                modifier = Modifier.size(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(5.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(22.dp))
                .background(
                    if (isLight) {
                        Color.White.copy(alpha = 0.78f)
                    } else {
                        Color.Black.copy(alpha = 0.24f)
                    }
                )
                .border(
                    width = 1.dp,
                    color = if (isLight) {
                        Color.Black.copy(alpha = 0.06f)
                    } else {
                        Color.White.copy(alpha = 0.08f)
                    },
                    shape = RoundedCornerShape(22.dp)
                )
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Column {
                Text(
                    text = activity.category,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (isLight) theme.accentLight else theme.accentDark
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = activity.title,
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = if (isLight) Color(0xFF171717) else Color.White
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = activity.description,
                    fontSize = 8.sp,
                    lineHeight = 11.sp,
                    maxLines = 6,
                    overflow = TextOverflow.Ellipsis,
                    color = if (isLight) Color(0xFF525252) else Color(0xFFA3A3A3)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Button(
            onClick = onDone,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isLight) {
                    Color(0xFF171717)
                } else {
                    Color.White
                },
                contentColor = if (isLight) {
                    Color.White
                } else {
                    Color.Black
                }
            ),
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(12.dp)
            )

            Spacer(modifier = Modifier.width(5.dp))

            Text(
                text = "Kerjakan",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun FavoriteWeeklyCard(
    theme: MentalTheme,
    isLight: Boolean,
    favoriteActivity: IntegratedActivity?,
    favoriteCount: Int,
    weeklyResetKey: String
) {
    BentoCard(
        theme = theme,
        isLight = isLight,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = Color(0xFFF43F5E),
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Favorite Activity Mingguan",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isLight) theme.accentLight else theme.accentDark
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (favoriteActivity == null) {
            Text(
                text = "Belum ada aktivitas favorit.",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isLight) Color(0xFF171717) else Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Kerjakan random activity beberapa kali. Aktivitas yang paling sering kamu kerjakan akan muncul di sini.",
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = if (isLight) Color(0xFF525252) else Color(0xFFA3A3A3)
            )
        } else {
            Text(
                text = favoriteActivity.category,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isLight) theme.accentLight else theme.accentDark
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = favoriteActivity.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (isLight) Color(0xFF171717) else Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = favoriteActivity.description,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = if (isLight) Color(0xFF525252) else Color(0xFFA3A3A3)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Dikerjakan $favoriteCount kali minggu ini.",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF10B981)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Reset mingguan: $weeklyResetKey",
            fontSize = 11.sp,
            color = if (isLight) Color(0xFF737373) else Color(0xFFA3A3A3)
        )
    }
}

@Composable
private fun KtorQuoteCard(
    theme: MentalTheme,
    isLight: Boolean,
    quote: String,
    onRefreshQuote: () -> Unit
) {
    BentoCard(
        theme = theme,
        isLight = isLight,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.FormatQuote,
                    contentDescription = null,
                    tint = if (isLight) Color(0xFF737373) else Color(0xFFA3A3A3),
                    modifier = Modifier.size(14.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "Ktor Quote",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isLight) Color(0xFF737373) else Color(0xFFA3A3A3)
                )
            }

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        if (isLight) Color.Black.copy(alpha = 0.06f)
                        else Color.White.copy(alpha = 0.08f)
                    )
                    .clickable { onRefreshQuote() }
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = if (isLight) Color.Black else Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "\"$quote\"",
            fontSize = 16.sp,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Medium,
            lineHeight = 22.sp,
            color = if (isLight) Color(0xFF262626) else Color.White.copy(alpha = 0.9f)
        )
    }
}

private fun getWeekKey(): String {
    val date = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date

    val weekOfMonth = ((date.dayOfMonth - 1) / 7) + 1

    return "${date.year}-${date.monthNumber}-W$weekOfMonth"
}