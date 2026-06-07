package com.example.synesthesia.presentation.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.synesthesia.domain.model.Note
import com.example.synesthesia.domain.model.EmotionSystem
import com.example.synesthesia.domain.model.EmotionCategory
import com.example.synesthesia.presentation.theme.BrightYellow
import com.example.synesthesia.presentation.theme.RoyalBlue
import com.example.synesthesia.presentation.theme.SpaceBlack
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ConstellationCanvas(
    notes: List<Note>,
    onNoteClick: (Long) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    val isAstronomy = MaterialTheme.colorScheme.background == SpaceBlack
    var offset by remember { mutableStateOf(Offset.Zero) }
    var scale by remember { mutableStateOf(1f) }
    var selectedNoteId by remember { mutableStateOf<Long?>(null) }
    var clickedHubCategory by remember { mutableStateOf<EmotionCategory?>(null) }
    val haptic = LocalHapticFeedback.current

    // Positions of individual note nodes
    val notePositions = remember(notes) {
        mutableMapOf<Long, Offset>()
    }

    // Positions of Hubs for click detection and popup anchoring
    val hubPositions = remember(notes) { mutableStateMapOf<String, Offset>() }

    val infiniteTransition = rememberInfiniteTransition()
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * kotlin.math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val twinkleAnim by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { centroid, pan, zoom, _ ->
                    val newScale = (scale * zoom).coerceIn(0.2f, 5f)
                    val actualZoom = newScale / scale
                    // Adjust offset to zoom around centroid
                    offset = (centroid - (centroid - offset) * actualZoom) + pan
                    scale = newScale
                }
            }
            .pointerInput(notes) {
                detectTapGestures { tapOffset ->
                    val adjustedTap = (tapOffset - offset) / scale
                    var found = false

                    // Check Hubs first (priority)
                    hubPositions.forEach { entry ->
                        val id = entry.key
                        val pos = entry.value
                        val dx = adjustedTap.x - pos.x
                        val dy = adjustedTap.y - pos.y
                        if (sqrt(dx * dx + dy * dy) <= 60f) {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            clickedHubCategory = EmotionSystem.categories.find { it.id == id }
                            selectedNoteId = null
                            found = true
                        }
                    }

                    if (!found) {
                        notePositions.forEach { entry ->
                            val id = entry.key
                            val pos = entry.value
                            val dx = adjustedTap.x - pos.x
                            val dy = adjustedTap.y - pos.y
                            if (sqrt(dx * dx + dy * dy) <= 40f) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                selectedNoteId = id
                                onNoteClick(id)
                                clickedHubCategory = null
                                found = true
                            }
                        }
                    }

                    if (!found) {
                        selectedNoteId = null
                        clickedHubCategory = null
                    }
                }
            }
    ) {
        val centerX = constraints.maxWidth / 2f
        val centerY = constraints.maxHeight / 2f

        // Shooting Star Animation State
        var lastNoteCount by remember { mutableStateOf(notes.size) }
        val shootingStarAnim = remember { Animatable(0f) }
        var shootingStarStart by remember { mutableStateOf(Offset.Zero) }
        var shootingStarTarget by remember { mutableStateOf(Offset.Zero) }

        LaunchedEffect(notes.size) {
            if (notes.size > lastNoteCount) {
                // Pick a random edge for start
                val side = (0..3).random()
                shootingStarStart = when(side) {
                    0 -> Offset(Random.nextFloat() * constraints.maxWidth.toFloat(), -100f)
                    1 -> Offset(constraints.maxWidth.toFloat() + 100f, Random.nextFloat() * constraints.maxHeight.toFloat())
                    2 -> Offset(Random.nextFloat() * constraints.maxWidth.toFloat(), constraints.maxHeight.toFloat() + 100f)
                    else -> Offset(-100f, Random.nextFloat() * constraints.maxHeight.toFloat())
                }
                // Target is approximately the center or a random hub
                shootingStarTarget = Offset(centerX, centerY)
                
                shootingStarAnim.snapTo(0f)
                shootingStarAnim.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(1200, easing = LinearOutSlowInEasing)
                )
            }
            lastNoteCount = notes.size
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y,
                    transformOrigin = TransformOrigin(0f, 0f)
                )
        ) {
            val starColor = if (isAstronomy) Color.White else Color(0xFFB39DDB) // Pastel Purple

            // Background stars
            repeat(100) { i ->
                drawCircle(
                    color = starColor.copy(alpha = 0.15f * twinkleAnim),
                    radius = 1f + (i % 2),
                    center = Offset(
                        x = (i * 777f % size.width),
                        y = (i * 333f % size.height)
                    )
                )
            }

            // Shooting Star Effect
            if (shootingStarAnim.value > 0f && shootingStarAnim.value < 1f) {
                val progress = shootingStarAnim.value
                val currentX = shootingStarStart.x + (shootingStarTarget.x - shootingStarStart.x) * progress
                val currentY = shootingStarStart.y + (shootingStarTarget.y - shootingStarStart.y) * progress
                val currentPos = Offset(currentX, currentY)
                
                drawCircle(
                    color = Color.White,
                    radius = 3.dp.toPx(),
                    center = currentPos
                )
                
                // Simple Trail
                val trailProgress = (progress - 0.2f).coerceAtLeast(0f)
                val trailX = shootingStarStart.x + (shootingStarTarget.x - shootingStarStart.x) * trailProgress
                val trailY = shootingStarStart.y + (shootingStarTarget.y - shootingStarStart.y) * trailProgress
                
                drawLine(
                    color = Color.White.copy(alpha = 0.4f * (1f - progress)),
                    start = Offset(trailX, trailY),
                    end = currentPos,
                    strokeWidth = 2.dp.toPx()
                )
            }

            // Define Hub positions (Diamond layout)
            val hubDistance = 800f
            val hubs = EmotionSystem.categories.mapIndexed { index, category ->
                val angle = (index.toFloat() / EmotionSystem.categories.size) * 2f * kotlin.math.PI.toFloat()
                val hubPos = Offset(
                    centerX + cos(angle) * hubDistance,
                    centerY + sin(angle) * hubDistance
                )
                category to hubPos
            }

            // Draw Hubs and Notes
            hubs.forEachIndexed { hubIndex, (category, hubBasePos) ->
                val hubColor = parseHexColor(category.color)
                val hubCurrentPos = Offset(
                    hubBasePos.x + sin(floatAnim + hubIndex) * 15f,
                    hubBasePos.y + cos(floatAnim * 0.5f + hubIndex) * 15f
                )
                hubPositions[category.id] = hubCurrentPos

                // 1. Draw Hub Glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(hubColor.copy(alpha = 0.4f), Color.Transparent),
                        center = hubCurrentPos,
                        radius = 150f
                    ),
                    radius = 150f,
                    center = hubCurrentPos
                )

                // 2. Draw Hub Core
                drawCircle(
                    color = Color.White.copy(alpha = 0.8f),
                    radius = 20f,
                    center = hubCurrentPos
                )
                drawCircle(
                    color = hubColor,
                    radius = 20f,
                    center = hubCurrentPos,
                    style = Stroke(width = 4f)
                )

                // 3. Draw Notes belonging to this Hub
                // Notes are grouped by their main category name stored in artToken
                val notesInCategory = notes.filter { it.artToken == category.name }
                notesInCategory.forEachIndexed { noteIndex, note ->
                    val noteAngle = (noteIndex.toFloat() / (notesInCategory.size.coerceAtLeast(1))) * 2f * kotlin.math.PI.toFloat()
                    val noteRadius = 200f + (noteIndex * 20f % 100f)
                    val noteBasePos = Offset(
                        hubCurrentPos.x + cos(noteAngle) * noteRadius,
                        hubCurrentPos.y + sin(noteAngle) * noteRadius
                    )

                    val noteCurrentPos = Offset(
                        noteBasePos.x + sin(floatAnim * 1.2f + noteIndex) * 10f,
                        noteBasePos.y + cos(floatAnim * 0.8f + noteIndex) * 10f
                    )
                    notePositions[note.id] = noteCurrentPos

                    // Draw connection to Hub
                    drawLine(
                        color = hubColor.copy(alpha = 0.3f),
                        start = hubCurrentPos,
                        end = noteCurrentPos,
                        strokeWidth = 1.dp.toPx()
                    )

                    // Draw Note Dot
                    drawCircle(
                        color = hubColor,
                        radius = 8f,
                        center = noteCurrentPos
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.6f * twinkleAnim),
                        radius = 12f,
                        center = noteCurrentPos,
                        style = Stroke(width = 2f)
                    )

                    // Draw Selection Ring
                    if (selectedNoteId == note.id) {
                        drawCircle(
                            color = BrightYellow,
                            radius = 24f,
                            center = noteCurrentPos,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                }
            }
        }

        // Anchored Emotion Hub Popup
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offset.x
                    translationY = offset.y
                    transformOrigin = TransformOrigin(0f, 0f)
                }
        ) {
            // Shared Element Placeholders (for transition)
            notes.forEach { note ->
                val pos = notePositions[note.id] ?: Offset.Zero
                with(sharedTransitionScope) {
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(pos.x.roundToInt() - 20, pos.y.roundToInt() - 20) }
                            .size(40.dp)
                            .sharedElement(
                                rememberSharedContentState(key = "note-${note.id}"),
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                    )
                }
            }

            clickedHubCategory?.let { category ->
                val hubPos = hubPositions[category.id] ?: Offset.Zero
                val hubColor = parseHexColor(category.color)

                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                (hubPos.x - 190f).roundToInt(),
                                (hubPos.y - 160f).roundToInt()
                            )
                        }
                        .wrapContentSize(Alignment.BottomCenter)
                ) {
                    AnimatedVisibility(
                        visible = true, // State controlled by outer null check
                        enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom),
                        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(IntrinsicSize.Max)
                        ) {
                            // The Label Bubble
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.5f))
                                    .border(1.dp, hubColor, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = category.name,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    textAlign = TextAlign.Center,
                                    fontSize = 12.sp,
                                    modifier = Modifier.widthIn(max = 120.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun parseHexColor(hex: String?): Color {
    if (hex == null || !hex.startsWith("#")) return Color.Gray
    return try {
        Color(hex.removePrefix("#").toLong(16) or 0xFF000000)
    } catch (_: Exception) {
        Color.Gray
    }
}
