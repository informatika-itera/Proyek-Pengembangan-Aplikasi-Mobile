package com.example.movein.presentation.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.movein.presentation.AppState
import com.example.movein.presentation.JourneyLog
import com.example.movein.presentation.MentalTheme
import com.example.movein.presentation.components.BentoCard
import kotlin.math.roundToInt

enum class CozyGameCategory(
    val label: String,
    val emoji: String
) {
    ORGANIZING("Organizing", "🗂️"),
    CLEANING("Cleaning", "🧽"),
    COLOR_VISUAL("Color Visual", "🎨"),
    BUILDING("Building", "🧱"),
    ASMR_CRAFT("ASMR Craft", "✨")
}

enum class CozyGameMechanic(
    val label: String
) {
    TAP_TO_CLEAR("Swipe Clean"),
    SORT_AND_MATCH("Sort & Match"),
    HOLD_TO_FILL("Hold to Fill"),
    GRID_TAP("Pop Grid"),
    PATTERN_FLOW("Pattern Flow")
}

data class CozyGameItem(
    val id: Int,
    val title: String,
    val description: String,
    val category: CozyGameCategory,
    val mechanic: CozyGameMechanic,
    val reward: Int,
    val emoji: String
)

private data class CozyGameSpec(
    val instruction: String,
    val boardTitle: String,
    val primaryEmoji: String,
    val secondaryEmojis: List<String>,
    val gridCells: Int,
    val totalSteps: Int,
    val progressIncrement: Int,
    val palette: List<Color>
)

private val cozyGameDataset = listOf(
    CozyGameItem(1, "Desk Organizer", "Rapikan benda kecil di meja virtual.", CozyGameCategory.ORGANIZING, CozyGameMechanic.SORT_AND_MATCH, 8, "🖊️"),
    CozyGameItem(2, "Shelf Sort", "Susun barang rak berdasarkan warna dan ukuran.", CozyGameCategory.ORGANIZING, CozyGameMechanic.SORT_AND_MATCH, 8, "📚"),
    CozyGameItem(3, "Bag Packing", "Masukkan item ke tas dengan urutan rapi.", CozyGameCategory.ORGANIZING, CozyGameMechanic.GRID_TAP, 8, "🎒"),
    CozyGameItem(4, "Cable Tidy", "Urai kabel kusut menjadi jalur bersih.", CozyGameCategory.ORGANIZING, CozyGameMechanic.PATTERN_FLOW, 9, "🔌"),
    CozyGameItem(5, "Fridge Arrange", "Susun isi kulkas agar terlihat satisfying.", CozyGameCategory.ORGANIZING, CozyGameMechanic.GRID_TAP, 9, "🧊"),
    CozyGameItem(6, "Drawer Reset", "Rapikan laci kecil satu per satu.", CozyGameCategory.ORGANIZING, CozyGameMechanic.TAP_TO_CLEAR, 8, "🗃️"),

    CozyGameItem(7, "Window Wipe", "Bersihkan kaca sampai bening.", CozyGameCategory.CLEANING, CozyGameMechanic.TAP_TO_CLEAR, 8, "🪟"),
    CozyGameItem(8, "Soap Bubble Clean", "Pecahkan busa sabun yang menenangkan.", CozyGameCategory.CLEANING, CozyGameMechanic.GRID_TAP, 8, "🫧"),
    CozyGameItem(9, "Dust Sweep", "Sapu debu kecil dari permukaan.", CozyGameCategory.CLEANING, CozyGameMechanic.PATTERN_FLOW, 9, "🧹"),
    CozyGameItem(10, "Mug Wash", "Bersihkan noda kecil di gelas.", CozyGameCategory.CLEANING, CozyGameMechanic.TAP_TO_CLEAR, 8, "☕"),
    CozyGameItem(11, "Plant Care", "Bersihkan daun dan beri air secukupnya.", CozyGameCategory.CLEANING, CozyGameMechanic.HOLD_TO_FILL, 10, "🪴"),
    CozyGameItem(12, "Keyboard Clean", "Bersihkan sela keyboard satu-satu.", CozyGameCategory.CLEANING, CozyGameMechanic.GRID_TAP, 9, "⌨️"),

    CozyGameItem(13, "Color Dots", "Tap titik warna yang muncul bergantian.", CozyGameCategory.COLOR_VISUAL, CozyGameMechanic.GRID_TAP, 8, "🔵"),
    CozyGameItem(14, "Gradient Flow", "Isi bar warna sampai penuh.", CozyGameCategory.COLOR_VISUAL, CozyGameMechanic.HOLD_TO_FILL, 8, "🌈"),
    CozyGameItem(15, "Paint Match", "Pilih warna yang cocok dengan mood.", CozyGameCategory.COLOR_VISUAL, CozyGameMechanic.SORT_AND_MATCH, 8, "🖌️"),
    CozyGameItem(16, "Light Switch", "Nyalakan lampu kecil dalam pola lembut.", CozyGameCategory.COLOR_VISUAL, CozyGameMechanic.PATTERN_FLOW, 9, "💡"),
    CozyGameItem(17, "Soft Pixel", "Isi pixel kosong menjadi gambar sederhana.", CozyGameCategory.COLOR_VISUAL, CozyGameMechanic.GRID_TAP, 9, "🟦"),
    CozyGameItem(18, "Color Breathing", "Ikuti ritme warna yang pelan.", CozyGameCategory.COLOR_VISUAL, CozyGameMechanic.HOLD_TO_FILL, 10, "🫧"),

    CozyGameItem(19, "Tiny House", "Bangun rumah kecil dari blok sederhana.", CozyGameCategory.BUILDING, CozyGameMechanic.GRID_TAP, 10, "🏠"),
    CozyGameItem(20, "Stone Stack", "Susun batu kecil sampai stabil.", CozyGameCategory.BUILDING, CozyGameMechanic.PATTERN_FLOW, 9, "🪨"),
    CozyGameItem(21, "Cozy Room", "Letakkan furniture kecil di ruangan.", CozyGameCategory.BUILDING, CozyGameMechanic.SORT_AND_MATCH, 10, "🛋️"),
    CozyGameItem(22, "Garden Path", "Buat jalur taman yang rapi.", CozyGameCategory.BUILDING, CozyGameMechanic.PATTERN_FLOW, 9, "🌿"),
    CozyGameItem(23, "Block Balance", "Tap blok dengan urutan tepat.", CozyGameCategory.BUILDING, CozyGameMechanic.GRID_TAP, 9, "🧱"),
    CozyGameItem(24, "Mini Bridge", "Lengkapi jembatan kecil.", CozyGameCategory.BUILDING, CozyGameMechanic.HOLD_TO_FILL, 10, "🌉"),

    CozyGameItem(25, "Clay Press", "Tekan tanah liat sampai bentuknya halus.", CozyGameCategory.ASMR_CRAFT, CozyGameMechanic.HOLD_TO_FILL, 10, "🟤"),
    CozyGameItem(26, "Paper Fold", "Lipat kertas virtual secara bertahap.", CozyGameCategory.ASMR_CRAFT, CozyGameMechanic.TAP_TO_CLEAR, 9, "📄"),
    CozyGameItem(27, "Bead String", "Susun manik-manik sesuai pola.", CozyGameCategory.ASMR_CRAFT, CozyGameMechanic.SORT_AND_MATCH, 10, "📿"),
    CozyGameItem(28, "Candle Pour", "Isi lilin secara perlahan.", CozyGameCategory.ASMR_CRAFT, CozyGameMechanic.HOLD_TO_FILL, 10, "🕯️"),
    CozyGameItem(29, "Sticker Peel", "Lepas sticker dengan gerakan lembut.", CozyGameCategory.ASMR_CRAFT, CozyGameMechanic.TAP_TO_CLEAR, 9, "🏷️"),
    CozyGameItem(30, "Sand Pattern", "Buat pola pasir yang calming.", CozyGameCategory.ASMR_CRAFT, CozyGameMechanic.PATTERN_FLOW, 10, "🏖️")
)

@Composable
fun CozyGameHubSection(
    theme: MentalTheme,
    isLight: Boolean,
    addJourneyLog: (JourneyLog) -> Unit,
    addMomentum: (Int) -> Unit,
    setAppState: (AppState) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedGame by remember { mutableStateOf<CozyGameItem?>(null) }

    CozyGamePicker(
        theme = theme,
        isLight = isLight,
        onGameSelected = { game ->
            selectedGame = game
        },
        modifier = modifier
    )

    selectedGame?.let { game ->
        Dialog(
            onDismissRequest = {
                selectedGame = null
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            CozyGameFullScreen(
                game = game,
                theme = theme,
                isLight = isLight,
                onBack = {
                    selectedGame = null
                },
                onComplete = { completedGame ->
                    addMomentum(completedGame.reward)
                    setAppState(AppState.RECOVERING)

                    addJourneyLog(
                        JourneyLog(
                            time = "Baru saja",
                            mood = "Detox",
                            task = completedGame.title,
                            result = "Mini Game Completed +${completedGame.reward}",
                            type = "mini_game",
                            appState = AppState.RECOVERING,
                            color = if (isLight) theme.accentLight else theme.accentDark,
                            bgColor = if (isLight) {
                                theme.accentLight.copy(alpha = 0.10f)
                            } else {
                                theme.accentDark.copy(alpha = 0.10f)
                            }
                        )
                    )

                    selectedGame = null
                }
            )
        }
    }
}

@Composable
private fun CozyGamePicker(
    theme: MentalTheme,
    isLight: Boolean,
    onGameSelected: (CozyGameItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var search by rememberSaveable { mutableStateOf("") }
    var selectedCategoryName by rememberSaveable { mutableStateOf("ALL") }

    val filteredGames = cozyGameDataset.filter { game ->
        val matchSearch = game.title.contains(search, ignoreCase = true) ||
                game.description.contains(search, ignoreCase = true) ||
                game.category.label.contains(search, ignoreCase = true)

        val matchCategory = selectedCategoryName == "ALL" ||
                game.category.name == selectedCategoryName

        matchSearch && matchCategory
    }

    BentoCard(
        theme = theme,
        isLight = isLight,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Extension,
                contentDescription = null,
                tint = if (isLight) theme.accentLight else theme.accentDark,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Satisfying Game Hub",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isLight) Color(0xFF171717) else Color.White
                )

                Text(
                    text = "30 mini game cozy dengan visual playable.",
                    fontSize = 11.sp,
                    color = if (isLight) Color(0xFF737373) else Color(0xFFA3A3A3)
                )
            }

            Text(
                text = "${filteredGames.size}/30",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isLight) theme.accentLight else theme.accentDark
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            placeholder = {
                Text(
                    text = "Cari game...",
                    fontSize = 12.sp,
                    color = if (isLight) Color(0xFF737373) else Color(0xFFA3A3A3)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = if (isLight) Color(0xFF737373) else Color(0xFFA3A3A3),
                    modifier = Modifier.size(18.dp)
                )
            },
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = if (isLight) {
                    Color.Black.copy(alpha = 0.04f)
                } else {
                    Color.White.copy(alpha = 0.06f)
                },
                unfocusedContainerColor = if (isLight) {
                    Color.Black.copy(alpha = 0.04f)
                } else {
                    Color.White.copy(alpha = 0.06f)
                },
                focusedBorderColor = if (isLight) {
                    theme.accentLight.copy(alpha = 0.35f)
                } else {
                    theme.accentDark.copy(alpha = 0.35f)
                },
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = if (isLight) Color(0xFF171717) else Color.White,
                unfocusedTextColor = if (isLight) Color(0xFF171717) else Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            item {
                CategoryChip(
                    text = "All",
                    selected = selectedCategoryName == "ALL",
                    theme = theme,
                    isLight = isLight,
                    onClick = {
                        selectedCategoryName = "ALL"
                    }
                )
            }

            itemsIndexed(CozyGameCategory.values().toList()) { _, category ->
                CategoryChip(
                    text = "${category.emoji} ${category.label}",
                    selected = selectedCategoryName == category.name,
                    theme = theme,
                    isLight = isLight,
                    onClick = {
                        selectedCategoryName = category.name
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            filteredGames.chunked(2).forEach { rowGames ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowGames.forEach { game ->
                        CozyGameCard(
                            game = game,
                            theme = theme,
                            isLight = isLight,
                            onClick = {
                                onGameSelected(game)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (rowGames.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryChip(
    text: String,
    selected: Boolean,
    theme: MentalTheme,
    isLight: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(
                if (selected) {
                    if (isLight) theme.accentLight.copy(alpha = 0.14f)
                    else theme.accentDark.copy(alpha = 0.18f)
                } else {
                    if (isLight) Color.Black.copy(alpha = 0.05f)
                    else Color.White.copy(alpha = 0.07f)
                }
            )
            .border(
                width = 1.dp,
                color = if (selected) {
                    if (isLight) theme.accentLight.copy(alpha = 0.35f)
                    else theme.accentDark.copy(alpha = 0.35f)
                } else {
                    Color.Transparent
                },
                shape = RoundedCornerShape(999.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) {
                if (isLight) theme.accentLight else theme.accentDark
            } else {
                if (isLight) Color(0xFF525252) else Color(0xFFA3A3A3)
            }
        )
    }
}

@Composable
private fun CozyGameCard(
    game: CozyGameItem,
    theme: MentalTheme,
    isLight: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spec = remember(game.id) { createGameSpec(game) }
    val cardBg = spec.palette.firstOrNull() ?: if (isLight) Color.White else Color.Black

    Box(
        modifier = modifier
            .height(166.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.verticalGradient(
                    colors = if (isLight) {
                        listOf(cardBg.copy(alpha = 0.24f), Color.White.copy(alpha = 0.86f))
                    } else {
                        listOf(cardBg.copy(alpha = 0.22f), Color.Black.copy(alpha = 0.28f))
                    }
                )
            )
            .border(
                width = 1.dp,
                color = if (isLight) theme.accentLight.copy(alpha = 0.14f)
                else theme.accentDark.copy(alpha = 0.12f),
                shape = RoundedCornerShape(26.dp)
            )
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Canvas(
            modifier = Modifier.matchParentSize()
        ) {
            drawCircle(
                color = cardBg.copy(alpha = 0.18f),
                radius = size.width * 0.34f,
                center = Offset(size.width * 0.88f, size.height * 0.14f)
            )
            drawCircle(
                color = Color.White.copy(alpha = if (isLight) 0.30f else 0.08f),
                radius = size.width * 0.18f,
                center = Offset(size.width * 0.12f, size.height * 0.90f)
            )
        }

        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                MiniAssetBadge(
                    emoji = game.emoji,
                    bg = cardBg,
                    isLight = isLight
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = "#${game.id}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isLight) theme.accentLight else theme.accentDark
                    )

                    Text(
                        text = game.mechanic.label,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isLight) Color(0xFF737373) else Color(0xFFA3A3A3),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = game.title,
                fontSize = 14.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = if (isLight) Color(0xFF171717) else Color.White
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = game.category.label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isLight) theme.accentLight else theme.accentDark
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = if (isLight) Color(0xFF737373) else Color(0xFFA3A3A3),
                    modifier = Modifier.size(14.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "Play",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isLight) Color(0xFF737373) else Color(0xFFA3A3A3)
                )
            }
        }
    }
}

@Composable
private fun MiniAssetBadge(
    emoji: String,
    bg: Color,
    isLight: Boolean
) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(
                        Color.White.copy(alpha = if (isLight) 0.95f else 0.18f),
                        bg.copy(alpha = 0.38f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = bg.copy(alpha = 0.28f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            fontSize = 22.sp
        )
    }
}

@Composable
private fun CozyGameFullScreen(
    game: CozyGameItem,
    theme: MentalTheme,
    isLight: Boolean,
    onBack: () -> Unit,
    onComplete: (CozyGameItem) -> Unit
) {
    val spec = remember(game.id) { createGameSpec(game) }

    var tappedCells by remember(game.id) { mutableStateOf<Set<Int>>(emptySet()) }
    var flowStep by rememberSaveable(game.id) { mutableIntStateOf(0) }
    var fillProgress by rememberSaveable(game.id) { mutableIntStateOf(0) }
    var scratchProgress by rememberSaveable(game.id) { mutableIntStateOf(0) }
    var selectedSortItem by remember(game.id) { mutableStateOf<Int?>(null) }
    var placedSortItems by remember(game.id) { mutableStateOf<Map<Int, Int>>(emptyMap()) }
    var completionEffect by remember(game.id) { mutableStateOf(false) }

    val progress = when (game.mechanic) {
        CozyGameMechanic.TAP_TO_CLEAR -> scratchProgress
        CozyGameMechanic.GRID_TAP -> ((tappedCells.size / spec.totalSteps.toFloat()) * 100).roundToInt().coerceIn(0, 100)
        CozyGameMechanic.SORT_AND_MATCH -> ((placedSortItems.size / spec.totalSteps.toFloat()) * 100).roundToInt().coerceIn(0, 100)
        CozyGameMechanic.PATTERN_FLOW -> ((flowStep / spec.totalSteps.toFloat()) * 100).roundToInt().coerceIn(0, 100)
        CozyGameMechanic.HOLD_TO_FILL -> fillProgress
    }

    LaunchedEffect(progress) {
        if (progress >= 100) {
            completionEffect = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isLight) theme.bgLight else theme.bgDark)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(20.dp)
    ) {
        GameHeaderBar(
            game = game,
            theme = theme,
            isLight = isLight,
            onBack = onBack
        )

        Spacer(modifier = Modifier.height(18.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(36.dp))
                .background(
                    Brush.verticalGradient(
                        colors = if (isLight) {
                            listOf(
                                spec.palette[0].copy(alpha = 0.20f),
                                Color.White.copy(alpha = 0.94f),
                                spec.palette[1].copy(alpha = 0.14f)
                            )
                        } else {
                            listOf(
                                spec.palette[0].copy(alpha = 0.20f),
                                Color.Black.copy(alpha = 0.34f),
                                spec.palette[1].copy(alpha = 0.16f)
                            )
                        }
                    )
                )
                .border(
                    width = 1.dp,
                    color = if (isLight) theme.accentLight.copy(alpha = 0.16f)
                    else theme.accentDark.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(36.dp)
                )
                .padding(20.dp)
        ) {
            Canvas(
                modifier = Modifier.matchParentSize()
            ) {
                drawCircle(
                    color = spec.palette[2].copy(alpha = 0.12f),
                    radius = size.width * 0.38f,
                    center = Offset(size.width * 0.92f, size.height * 0.04f)
                )
                drawCircle(
                    color = Color.White.copy(alpha = if (isLight) 0.30f else 0.06f),
                    radius = size.width * 0.25f,
                    center = Offset(size.width * 0.08f, size.height * 0.96f)
                )
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GameHeroTitle(
                    game = game,
                    spec = spec,
                    isLight = isLight
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    when (game.mechanic) {
                        CozyGameMechanic.TAP_TO_CLEAR -> {
                            ScratchCleanFinalBoard(
                                game = game,
                                spec = spec,
                                theme = theme,
                                isLight = isLight,
                                progress = scratchProgress,
                                onScratch = {
                                    scratchProgress = (scratchProgress + spec.progressIncrement).coerceAtMost(100)
                                }
                            )
                        }

                        CozyGameMechanic.GRID_TAP -> {
                            PopGridFinalBoard(
                                game = game,
                                spec = spec,
                                theme = theme,
                                isLight = isLight,
                                tappedCells = tappedCells,
                                onTapCell = { index ->
                                    tappedCells = tappedCells + index
                                }
                            )
                        }

                        CozyGameMechanic.SORT_AND_MATCH -> {
                            SortMatchFinalBoard(
                                game = game,
                                spec = spec,
                                theme = theme,
                                isLight = isLight,
                                selectedSortItem = selectedSortItem,
                                placedSortItems = placedSortItems,
                                onSelectSortItem = { index ->
                                    selectedSortItem = index
                                },
                                onPlaceSortItem = { slot ->
                                    val selected = selectedSortItem
                                    if (selected != null && selected !in placedSortItems.keys) {
                                        val correctSlot = selected % 3
                                        if (slot == correctSlot) {
                                            placedSortItems = placedSortItems + (selected to slot)
                                            selectedSortItem = null
                                        }
                                    }
                                }
                            )
                        }

                        CozyGameMechanic.PATTERN_FLOW -> {
                            PatternFlowFinalBoard(
                                game = game,
                                spec = spec,
                                theme = theme,
                                isLight = isLight,
                                flowStep = flowStep,
                                onFlowTap = { index ->
                                    if (index == flowStep) {
                                        flowStep = (flowStep + 1).coerceAtMost(spec.totalSteps)
                                    }
                                }
                            )
                        }

                        CozyGameMechanic.HOLD_TO_FILL -> {
                            HoldFillFinalBoard(
                                game = game,
                                spec = spec,
                                theme = theme,
                                isLight = isLight,
                                fillProgress = fillProgress,
                                onFill = {
                                    fillProgress = (fillProgress + spec.progressIncrement).coerceAtMost(100)
                                }
                            )
                        }
                    }

                    this@Column.AnimatedVisibility(
                        visible = completionEffect,
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        CompletionOverlay(
                            theme = theme,
                            isLight = isLight
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                ProgressBar(
                    progress = progress,
                    theme = theme,
                    isLight = isLight
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "$progress%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isLight) theme.accentLight else theme.accentDark
                )

                Spacer(modifier = Modifier.height(12.dp))

                ResetGameButton(
                    isLight = isLight,
                    onReset = {
                        tappedCells = emptySet()
                        flowStep = 0
                        fillProgress = 0
                        scratchProgress = 0
                        selectedSortItem = null
                        placedSortItems = emptyMap()
                        completionEffect = false
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = {
                if (progress >= 100) {
                    onComplete(game)
                }
            },
            enabled = progress >= 100,
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (progress >= 100) {
                    Color(0xFF10B981)
                } else {
                    if (isLight) Color.Black.copy(alpha = 0.08f)
                    else Color.White.copy(alpha = 0.10f)
                },
                disabledContainerColor = if (isLight) {
                    Color.Black.copy(alpha = 0.08f)
                } else {
                    Color.White.copy(alpha = 0.10f)
                },
                contentColor = Color.White,
                disabledContentColor = if (isLight) Color(0xFF737373) else Color(0xFFA3A3A3)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = if (progress >= 100) {
                    "Selesai & Ambil Momentum"
                } else {
                    "Selesaikan Mini Game"
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun GameHeaderBar(
    game: CozyGameItem,
    theme: MentalTheme,
    isLight: Boolean,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(
                    if (isLight) Color.Black.copy(alpha = 0.06f)
                    else Color.White.copy(alpha = 0.08f)
                )
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                tint = if (isLight) Color.Black else Color.White
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = game.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (isLight) Color(0xFF171717) else Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${game.category.emoji} ${game.category.label} • ${game.mechanic.label}",
                fontSize = 11.sp,
                color = if (isLight) Color(0xFF737373) else Color(0xFFA3A3A3),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Stars,
                contentDescription = null,
                tint = if (isLight) theme.accentLight else theme.accentDark,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "+${game.reward}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isLight) theme.accentLight else theme.accentDark
            )
        }
    }
}

@Composable
private fun GameHeroTitle(
    game: CozyGameItem,
    spec: CozyGameSpec,
    isLight: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(82.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color.White.copy(alpha = if (isLight) 0.95f else 0.20f),
                            spec.palette[0].copy(alpha = 0.38f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = spec.palette[0].copy(alpha = 0.34f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = game.emoji,
                fontSize = 42.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = spec.boardTitle,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = if (isLight) Color(0xFF171717) else Color.White,
            textAlign = TextAlign.Center
        )

        Text(
            text = spec.instruction,
            fontSize = 12.sp,
            lineHeight = 17.sp,
            color = if (isLight) Color(0xFF737373) else Color(0xFFA3A3A3),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

@Composable
private fun ScratchCleanFinalBoard(
    game: CozyGameItem,
    spec: CozyGameSpec,
    theme: MentalTheme,
    isLight: Boolean,
    progress: Int,
    onScratch: () -> Unit
) {
    val dirtAlpha by animateFloatAsState(
        targetValue = 1f - progress / 100f,
        animationSpec = tween(260),
        label = "scratchDirtAlpha"
    )

    val cleanScale by animateFloatAsState(
        targetValue = if (progress >= 100) 1.08f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "cleanScale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(290.dp)
            .clip(RoundedCornerShape(34.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        spec.palette[0].copy(alpha = if (isLight) 0.40f else 0.28f),
                        if (isLight) Color.White else Color(0xFF111827)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = if (isLight) theme.accentLight.copy(alpha = 0.22f)
                else theme.accentDark.copy(alpha = 0.22f),
                shape = RoundedCornerShape(34.dp)
            )
            .pointerInput(game.id) {
                detectDragGestures(
                    onDrag = { change, _ ->
                        change.consume()
                        if (progress < 100) {
                            onScratch()
                        }
                    }
                )
            }
            .clickable {
                if (progress < 100) {
                    onScratch()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            drawRoundRect(
                color = Color.White.copy(alpha = if (isLight) 0.26f else 0.08f),
                topLeft = Offset(size.width * 0.12f, size.height * 0.12f),
                size = Size(size.width * 0.76f, size.height * 0.70f),
                cornerRadius = CornerRadius(42f, 42f)
            )

            for (i in 0 until 34) {
                val x = size.width * (((i * 37) % 100) / 100f)
                val y = size.height * (((i * 61) % 100) / 100f)
                val radius = 10f + (i % 5) * 5f

                drawCircle(
                    color = Color(0xFF7A5C45).copy(alpha = 0.30f * dirtAlpha),
                    radius = radius,
                    center = Offset(x, y)
                )
            }

            if (progress >= 100) {
                drawLine(
                    color = Color.White.copy(alpha = 0.85f),
                    start = Offset(size.width * 0.18f, size.height * 0.25f),
                    end = Offset(size.width * 0.82f, size.height * 0.12f),
                    strokeWidth = 9f,
                    cap = StrokeCap.Round
                )

                drawLine(
                    color = Color.White.copy(alpha = 0.60f),
                    start = Offset(size.width * 0.22f, size.height * 0.56f),
                    end = Offset(size.width * 0.70f, size.height * 0.42f),
                    strokeWidth = 6f,
                    cap = StrokeCap.Round
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.scale(cleanScale)
        ) {
            Text(
                text = if (progress >= 100) "✨" else spec.primaryEmoji,
                fontSize = 58.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (progress >= 100) "Kinclong!" else "Swipe / tap untuk membersihkan",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isLight) Color(0xFF171717) else Color.White
            )
        }
    }
}

@Composable
private fun PopGridFinalBoard(
    game: CozyGameItem,
    spec: CozyGameSpec,
    theme: MentalTheme,
    isLight: Boolean,
    tappedCells: Set<Int>,
    onTapCell: (Int) -> Unit
) {
    val columns = if (spec.gridCells == 16) 4 else 3
    val rows = (spec.gridCells + columns - 1) / columns
    val cellOuter = if (spec.gridCells == 16) 66.dp else 76.dp
    val cellInner = if (spec.gridCells == 16) 52.dp else 60.dp

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        repeat(rows) { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                repeat(columns) { col ->
                    val index = row * columns + col

                    if (index < spec.gridCells) {
                        val pressed = index in tappedCells
                        val animatedSize by animateDpAsState(
                            targetValue = if (pressed) cellInner - 15.dp else cellInner,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            label = "popSize$index"
                        )

                        val alpha by animateFloatAsState(
                            targetValue = if (pressed) 0.78f else 1f,
                            animationSpec = tween(150),
                            label = "popAlpha$index"
                        )

                        Box(
                            modifier = Modifier
                                .size(cellOuter)
                                .clip(RoundedCornerShape(22.dp))
                                .background(
                                    if (pressed) Color(0xFF10B981).copy(alpha = 0.18f)
                                    else if (isLight) Color.White.copy(alpha = 0.70f)
                                    else Color.White.copy(alpha = 0.08f)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (pressed) Color(0xFF10B981).copy(alpha = 0.36f)
                                    else spec.palette[0].copy(alpha = 0.18f),
                                    shape = RoundedCornerShape(22.dp)
                                )
                                .clickable {
                                    if (!pressed) {
                                        onTapCell(index)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(
                                modifier = Modifier.matchParentSize()
                            ) {
                                drawCircle(
                                    color = spec.palette[index % spec.palette.size].copy(alpha = if (pressed) 0.10f else 0.20f),
                                    radius = size.minDimension * 0.40f,
                                    center = Offset(size.width * 0.5f, size.height * 0.5f)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(animatedSize)
                                    .alpha(alpha)
                                    .clip(CircleShape)
                                    .background(
                                        if (pressed) {
                                            SolidColor(Color(0xFF10B981).copy(alpha = 0.35f))
                                        } else {
                                            Brush.radialGradient(
                                                listOf(
                                                    Color.White.copy(alpha = if (isLight) 0.92f else 0.16f),
                                                    spec.palette[index % spec.palette.size].copy(alpha = 0.45f)
                                                )
                                            )
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (pressed) "✓" else spec.secondaryEmojis[index % spec.secondaryEmojis.size],
                                    fontSize = if (pressed) 18.sp else 24.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SortMatchFinalBoard(
    game: CozyGameItem,
    spec: CozyGameSpec,
    theme: MentalTheme,
    isLight: Boolean,
    selectedSortItem: Int?,
    placedSortItems: Map<Int, Int>,
    onSelectSortItem: (Int) -> Unit,
    onPlaceSortItem: (Int) -> Unit
) {
    val itemIcons = remember(game.id) {
        val base = spec.secondaryEmojis
        listOf(
            base[0 % base.size],
            base[1 % base.size],
            base[2 % base.size],
            base[0 % base.size],
            base[1 % base.size],
            base[2 % base.size]
        )
    }

    val slotIcons = itemIcons.take(3)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            slotIcons.forEachIndexed { slot, icon ->
                val count = placedSortItems.values.count { it == slot }
                val full = count >= 2

                val slotScale by animateFloatAsState(
                    targetValue = if (full) 1.05f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "slotScale$slot"
                )

                Box(
                    modifier = Modifier
                        .size(82.dp)
                        .scale(slotScale)
                        .clip(RoundedCornerShape(26.dp))
                        .background(
                            if (full) Color(0xFF10B981).copy(alpha = 0.20f)
                            else if (isLight) Color.White.copy(alpha = 0.76f)
                            else Color.White.copy(alpha = 0.08f)
                        )
                        .border(
                            width = 1.dp,
                            color = if (full) Color(0xFF10B981).copy(alpha = 0.42f)
                            else spec.palette[slot % spec.palette.size].copy(alpha = 0.30f),
                            shape = RoundedCornerShape(26.dp)
                        )
                        .clickable {
                            onPlaceSortItem(slot)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = icon,
                            fontSize = 27.sp
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = "$count/2",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLight) theme.accentLight else theme.accentDark
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemIcons.chunked(3).forEachIndexed { rowIndex, rowItems ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowItems.forEachIndexed { colIndex, icon ->
                        val index = rowIndex * 3 + colIndex
                        val placed = index in placedSortItems.keys
                        val selected = selectedSortItem == index

                        val scale by animateFloatAsState(
                            targetValue = when {
                                placed -> 0.84f
                                selected -> 1.10f
                                else -> 1f
                            },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            label = "sortItemScale$index"
                        )

                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .scale(scale)
                                .clip(RoundedCornerShape(22.dp))
                                .background(
                                    when {
                                        placed -> Color(0xFF10B981).copy(alpha = 0.18f)
                                        selected -> if (isLight) theme.accentLight.copy(alpha = 0.22f)
                                        else theme.accentDark.copy(alpha = 0.26f)
                                        else -> if (isLight) Color.White.copy(alpha = 0.75f)
                                        else Color.White.copy(alpha = 0.08f)
                                    }
                                )
                                .border(
                                    width = 1.dp,
                                    color = when {
                                        selected -> if (isLight) theme.accentLight else theme.accentDark
                                        placed -> Color(0xFF10B981).copy(alpha = 0.44f)
                                        else -> Color.Transparent
                                    },
                                    shape = RoundedCornerShape(22.dp)
                                )
                                .clickable {
                                    if (!placed) {
                                        onSelectSortItem(index)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (placed) "✓" else icon,
                                fontSize = 27.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PatternFlowFinalBoard(
    game: CozyGameItem,
    spec: CozyGameSpec,
    theme: MentalTheme,
    isLight: Boolean,
    flowStep: Int,
    onFlowTap: (Int) -> Unit
) {
    val path = remember(game.id) {
        when (game.id) {
            4 -> listOf("🔌", "〰️", "〰️", "✨", "🔋", "✨", "〰️", "〰️", "✅")
            9 -> listOf("🧹", "·", "·", "✨", "·", "✨", "·", "·", "✅")
            16 -> listOf("💡", "✨", "✨", "🌙", "✨", "🌙", "✨", "✨", "✅")
            20 -> listOf("🪨", "🪨", "🪨", "✨", "🪨", "✨", "🪨", "🪨", "✅")
            22 -> listOf("🌿", "🪨", "🌿", "🪨", "✨", "🪨", "🌿", "🪨", "✅")
            30 -> listOf("🏖️", "〰️", "〰️", "✨", "〰️", "✨", "〰️", "〰️", "✅")
            else -> listOf("🌱", "🌿", "🍃", "✨", game.emoji, "✨", "🍃", "🌿", "✅")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(292.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(292.dp)
                .padding(34.dp)
        ) {
            val points = listOf(
                Offset(size.width * 0.15f, size.height * 0.18f),
                Offset(size.width * 0.50f, size.height * 0.18f),
                Offset(size.width * 0.85f, size.height * 0.18f),
                Offset(size.width * 0.85f, size.height * 0.50f),
                Offset(size.width * 0.50f, size.height * 0.50f),
                Offset(size.width * 0.15f, size.height * 0.50f),
                Offset(size.width * 0.15f, size.height * 0.82f),
                Offset(size.width * 0.50f, size.height * 0.82f),
                Offset(size.width * 0.85f, size.height * 0.82f)
            )

            for (i in 0 until points.lastIndex) {
                drawLine(
                    color = if (i < flowStep) {
                        Color(0xFF10B981).copy(alpha = 0.58f)
                    } else {
                        spec.palette[0].copy(alpha = 0.20f)
                    },
                    start = points[i],
                    end = points[i + 1],
                    strokeWidth = 9f,
                    cap = StrokeCap.Round
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            repeat(3) { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    repeat(3) { col ->
                        val index = row * 3 + col
                        val done = index < flowStep
                        val active = index == flowStep

                        val scale by animateFloatAsState(
                            targetValue = when {
                                active -> 1.12f
                                done -> 0.94f
                                else -> 1f
                            },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            label = "flowScale$index"
                        )

                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .scale(scale)
                                .clip(RoundedCornerShape(24.dp))
                                .background(
                                    when {
                                        done -> Color(0xFF10B981).copy(alpha = 0.24f)
                                        active -> if (isLight) theme.accentLight.copy(alpha = 0.26f)
                                        else theme.accentDark.copy(alpha = 0.30f)
                                        else -> if (isLight) Color.White.copy(alpha = 0.80f)
                                        else Color.White.copy(alpha = 0.08f)
                                    }
                                )
                                .border(
                                    width = 1.dp,
                                    color = when {
                                        active -> if (isLight) theme.accentLight else theme.accentDark
                                        done -> Color(0xFF10B981).copy(alpha = 0.42f)
                                        else -> Color.Transparent
                                    },
                                    shape = RoundedCornerShape(24.dp)
                                )
                                .clickable {
                                    onFlowTap(index)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (done) "✓" else path[index],
                                fontSize = 25.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HoldFillFinalBoard(
    game: CozyGameItem,
    spec: CozyGameSpec,
    theme: MentalTheme,
    isLight: Boolean,
    fillProgress: Int,
    onFill: () -> Unit
) {
    val progressFloat by animateFloatAsState(
        targetValue = fillProgress / 100f,
        animationSpec = tween(280),
        label = "fillProgress"
    )

    val innerSize by animateDpAsState(
        targetValue = (82 + fillProgress).dp.coerceAtMost(186.dp),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "fillSize"
    )

    Box(
        modifier = Modifier
            .size(248.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(
                        if (isLight) Color.White.copy(alpha = 0.88f) else Color.White.copy(alpha = 0.08f),
                        spec.palette[1].copy(alpha = 0.18f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = if (isLight) theme.accentLight.copy(alpha = 0.22f)
                else theme.accentDark.copy(alpha = 0.26f),
                shape = CircleShape
            )
            .clickable {
                if (fillProgress < 100) {
                    onFill()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            drawArc(
                color = Color(0xFF10B981).copy(alpha = 0.64f),
                startAngle = -90f,
                sweepAngle = progressFloat * 360f,
                useCenter = false,
                style = Stroke(
                    width = 18f,
                    cap = StrokeCap.Round
                )
            )

            drawCircle(
                color = spec.palette[0].copy(alpha = 0.12f),
                radius = size.minDimension * 0.33f,
                center = center
            )
        }

        Box(
            modifier = Modifier
                .size(innerSize)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color.White.copy(alpha = if (isLight) 0.85f else 0.18f),
                            spec.palette[0].copy(alpha = 0.40f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = spec.primaryEmoji,
                    fontSize = 50.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "$fillProgress%",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isLight) theme.accentLight else theme.accentDark
                )
            }
        }
    }
}

@Composable
private fun CompletionOverlay(
    theme: MentalTheme,
    isLight: Boolean
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(30.dp))
            .background(
                if (isLight) Color.White.copy(alpha = 0.92f)
                else Color.Black.copy(alpha = 0.72f)
            )
            .border(
                width = 1.dp,
                color = Color(0xFF10B981).copy(alpha = 0.40f),
                shape = RoundedCornerShape(30.dp)
            )
            .padding(horizontal = 26.dp, vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "✨",
                fontSize = 42.sp
            )

            Text(
                text = "Perfect!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = if (isLight) Color(0xFF171717) else Color.White
            )

            Text(
                text = "Mini game selesai",
                fontSize = 12.sp,
                color = if (isLight) theme.accentLight else theme.accentDark
            )
        }
    }
}

@Composable
private fun ResetGameButton(
    isLight: Boolean,
    onReset: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(
                if (isLight) Color.Black.copy(alpha = 0.06f)
                else Color.White.copy(alpha = 0.08f)
            )
            .clickable { onReset() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = if (isLight) Color(0xFF525252) else Color(0xFFA3A3A3),
                modifier = Modifier.size(14.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "Reset Game",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isLight) Color(0xFF525252) else Color(0xFFA3A3A3)
            )
        }
    }
}

@Composable
private fun ProgressBar(
    progress: Int,
    theme: MentalTheme,
    isLight: Boolean
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress / 100f,
        animationSpec = tween(300),
        label = "gameProgress"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(14.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(
                if (isLight) Color.Black.copy(alpha = 0.07f)
                else Color.White.copy(alpha = 0.08f)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .fillMaxHeight()
                .clip(RoundedCornerShape(999.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            if (isLight) theme.accentLight else theme.accentDark,
                            Color(0xFF10B981)
                        )
                    )
                )
        )
    }
}

private fun createGameSpec(game: CozyGameItem): CozyGameSpec {
    val softBlue = Color(0xFF93C5FD)
    val softPink = Color(0xFFF9A8D4)
    val softGreen = Color(0xFF86EFAC)
    val softYellow = Color(0xFFFDE68A)
    val softPurple = Color(0xFFC4B5FD)
    val softOrange = Color(0xFFFDBA74)

    fun base(
        instruction: String,
        boardTitle: String,
        primary: String = game.emoji,
        items: List<String> = listOf(game.emoji, "✨", "🌿"),
        cells: Int = 9,
        steps: Int = 9,
        inc: Int = 10,
        palette: List<Color> = listOf(softBlue, softPink, softGreen)
    ): CozyGameSpec {
        return CozyGameSpec(
            instruction = instruction,
            boardTitle = boardTitle,
            primaryEmoji = primary,
            secondaryEmojis = items,
            gridCells = cells,
            totalSteps = steps,
            progressIncrement = inc,
            palette = palette
        )
    }

    return when (game.id) {
        1 -> base("Pilih alat tulis, lalu masukkan ke wadah yang cocok.", "Organize the Desk", "🖊️", listOf("🖊️", "📎", "📌"), 9, 6, 10, listOf(softBlue, softYellow, softPurple))
        2 -> base("Cocokkan buku merah, biru, dan hijau ke raknya.", "Sort the Shelf", "📚", listOf("📕", "📘", "📗"), 9, 6, 10, listOf(softPurple, softBlue, softGreen))
        3 -> base("Tap semua slot tas sampai barang tersusun penuh.", "Pack the Bag", "🎒", listOf("👕", "🎧", "📱", "🧴"), 16, 16, 8, listOf(softOrange, softBlue, softPink))
        4 -> base("Ikuti jalur kabel yang menyala dari awal sampai akhir.", "Cable Flow", "🔌", listOf("🔌", "〰️", "🔋"), 9, 9, 10, listOf(softPurple, softBlue, softYellow))
        5 -> base("Tap isi kulkas sampai semua panel terisi rapi.", "Fridge Grid", "🧊", listOf("🥛", "🍎", "🥦", "🧊"), 16, 16, 8, listOf(softBlue, softGreen, softPink))
        6 -> base("Swipe area laci untuk menghilangkan debu dan clutter.", "Drawer Clean", "🗃️", listOf("🗃️"), 9, 100, 6, listOf(softYellow, softOrange, softBlue))

        7 -> base("Swipe kaca sampai noda menghilang dan muncul kilau.", "Window Wipe", "🪟", listOf("🪟"), 9, 100, 5, listOf(softBlue, Color(0xFFBAE6FD), softPurple))
        8 -> base("Pop semua bubble sabun sampai habis.", "Bubble Pop", "🫧", listOf("🫧", "○", "◌"), 16, 16, 8, listOf(softBlue, softPink, softPurple))
        9 -> base("Ikuti jalur sapuan debu yang menyala.", "Dust Flow", "🧹", listOf("🧹", "·", "✨"), 9, 9, 10, listOf(softYellow, softOrange, softGreen))
        10 -> base("Swipe noda mug sampai permukaan bersih.", "Mug Wash", "☕", listOf("☕"), 9, 100, 6, listOf(softOrange, softYellow, softBlue))
        11 -> base("Tap perlahan untuk mengisi air tanaman.", "Plant Care", "🪴", listOf("💧", "🪴"), 9, 100, 8, listOf(softGreen, softBlue, softYellow))
        12 -> base("Tap semua tombol keyboard sampai bersih.", "Keyboard Clean", "⌨️", listOf("⌨️", "⌘", "⌫"), 16, 16, 8, listOf(softPurple, softBlue, softGreen))

        13 -> base("Tap semua dot warna untuk menyalakan palet.", "Color Dots", "🔵", listOf("🔴", "🟠", "🟡", "🟢", "🔵", "🟣"), 16, 16, 8, listOf(softPink, softYellow, softBlue))
        14 -> base("Tap perlahan sampai gradasi warna penuh.", "Gradient Flow", "🌈", listOf("🌈"), 9, 100, 8, listOf(softPink, softYellow, softBlue))
        15 -> base("Cocokkan kuas, cat, dan air ke paletnya.", "Paint Match", "🖌️", listOf("🖌️", "🎨", "💧"), 9, 6, 10, listOf(softPink, softPurple, softBlue))
        16 -> base("Tekan cahaya sesuai urutan untuk menyalakan pola.", "Light Path", "💡", listOf("💡", "✨", "🌙"), 9, 9, 10, listOf(softYellow, softPurple, softBlue))
        17 -> base("Isi semua pixel untuk membentuk pola soft.", "Soft Pixel", "🟦", listOf("🟦", "🟪", "🟩", "🟨"), 16, 16, 8, listOf(softBlue, softPurple, softGreen))
        18 -> base("Tap mengikuti ritme napas warna sampai penuh.", "Color Breathing", "🫧", listOf("🫧"), 9, 100, 7, listOf(softPurple, softBlue, softPink))

        19 -> base("Tap blok rumah sampai bangunan mini selesai.", "Tiny House Build", "🏠", listOf("🧱", "🏠", "🪟"), 16, 16, 8, listOf(softOrange, softYellow, softGreen))
        20 -> base("Ikuti urutan batu dari bawah ke atas.", "Stone Stack", "🪨", listOf("🪨", "✨"), 9, 9, 10, listOf(Color(0xFFD6D3D1), softGreen, softBlue))
        21 -> base("Cocokkan sofa, kursi, dan tanaman ke zona ruangan.", "Cozy Room", "🛋️", listOf("🛋️", "🪑", "🪴"), 9, 6, 10, listOf(softGreen, softYellow, softPurple))
        22 -> base("Ikuti jalur taman sampai setapak selesai.", "Garden Path", "🌿", listOf("🌿", "🪨", "✨"), 9, 9, 10, listOf(softGreen, softYellow, softBlue))
        23 -> base("Tap semua blok sampai balance board penuh.", "Block Balance", "🧱", listOf("🧱", "□", "■"), 16, 16, 8, listOf(softOrange, softYellow, softPurple))
        24 -> base("Tap untuk membangun jembatan secara perlahan.", "Mini Bridge", "🌉", listOf("🌉"), 9, 100, 8, listOf(softBlue, softPurple, softOrange))

        25 -> base("Tekan clay sampai permukaannya halus.", "Clay Press", "🟤", listOf("🟤"), 9, 100, 7, listOf(Color(0xFFD6A77A), softOrange, softYellow))
        26 -> base("Swipe kertas sampai lipatan terlihat rapi.", "Paper Fold", "📄", listOf("📄"), 9, 100, 8, listOf(Color(0xFFE5E7EB), softBlue, softPurple))
        27 -> base("Cocokkan manik merah, kuning, dan biru ke talinya.", "Bead String", "📿", listOf("🔴", "🟡", "🔵"), 9, 6, 10, listOf(softPink, softYellow, softBlue))
        28 -> base("Tap sampai cairan lilin memenuhi cetakan.", "Candle Pour", "🕯️", listOf("🕯️"), 9, 100, 8, listOf(softYellow, softOrange, softPink))
        29 -> base("Swipe sticker sampai terlepas mulus.", "Sticker Peel", "🏷️", listOf("🏷️"), 9, 100, 5, listOf(softPink, softYellow, softBlue))
        30 -> base("Ikuti pola pasir sampai garis calming selesai.", "Sand Pattern", "🏖️", listOf("🏖️", "〰️", "✨"), 9, 9, 10, listOf(Color(0xFFFCD34D), softBlue, softGreen))

        else -> base(game.description, game.title)
    }
}