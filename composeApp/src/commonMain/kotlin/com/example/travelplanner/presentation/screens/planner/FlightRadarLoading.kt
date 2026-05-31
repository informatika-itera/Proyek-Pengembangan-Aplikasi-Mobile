package com.example.travelplanner.presentation.screens.planner

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travelplanner.core.util.LocalStrings
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI

@Composable
fun FlightRadarLoading(
    modifier: Modifier = Modifier
) {
    val s = LocalStrings.current
    val isIndonesian = remember(s) { s.cancel == "Batal" }

    // 1. Dynamic AI stage texts (Idea 2)
    val loadingStages = remember(isIndonesian) {
        if (isIndonesian) {
            listOf(
                "🔍 Menganalisis rute & jadwal keberangkatan...",
                "🏨 Mencari penginapan terbaik sesuai budget...",
                "🍲 Mengumpulkan kuliner legendaris setempat...",
                "🗺️ Menyusun jadwal rute aktivitas harian...",
                "✨ Menyempurnakan detail itinerary dengan AI..."
            )
        } else {
            listOf(
                "🔍 Analyzing route & departure schedules...",
                "🏨 Finding the best accommodations for you...",
                "🍲 Gathering local legendary culinary hotspots...",
                "🗺️ Mapping out daily optimized activity paths...",
                "✨ Finalizing ultimate travel itinerary details..."
            )
        }
    }

    var currentStageIndex by remember { mutableStateOf(0) }

    LaunchedEffect(loadingStages) {
        while (true) {
            delay(2800)
            currentStageIndex = (currentStageIndex + 1) % loadingStages.size
        }
    }

    // 2. Animations for the Flight Radar (Idea 1)
    val transition = rememberInfiniteTransition(label = "radar")
    
    // Rotating radar sweep
    val sweepRotation by transition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(4500, easing = LinearEasing)),
        label = "sweep"
    )

    // Rotating airplane
    val airplaneRotation by transition.animateFloat(
        initialValue = 360f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing)),
        label = "airplane"
    )

    // Fading detected blips (landmarks)
    val blipPulse1 by transition.animateFloat(
        initialValue = 0.1f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "blip1"
    )
    val blipPulse2 by transition.animateFloat(
        initialValue = 0.9f, targetValue = 0.2f,
        animationSpec = infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "blip2"
    )

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .padding(24.dp)
            .shadow(16.dp, RoundedCornerShape(24.dp))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            // Radar Visual Box
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(100))
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFF0F1A24), Color(0xFF070B0F))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cx = size.width / 2
                    val cy = size.height / 2
                    val maxRadius = minOf(size.width, size.height) / 2 * 0.92f

                    // Draw concentric radar grids
                    drawCircle(
                        color = Color(0xFF00B4D8).copy(alpha = 0.12f),
                        radius = maxRadius,
                        center = Offset(cx, cy),
                        style = Stroke(width = 1.dp.toPx())
                    )
                    drawCircle(
                        color = Color(0xFF00B4D8).copy(alpha = 0.10f),
                        radius = maxRadius * 0.66f,
                        center = Offset(cx, cy),
                        style = Stroke(width = 1.dp.toPx())
                    )
                    drawCircle(
                        color = Color(0xFF00B4D8).copy(alpha = 0.08f),
                        radius = maxRadius * 0.33f,
                        center = Offset(cx, cy),
                        style = Stroke(width = 1.dp.toPx())
                    )

                    // Draw Crosshairs
                    drawLine(
                        color = Color(0xFF00B4D8).copy(alpha = 0.15f),
                        start = Offset(cx - maxRadius, cy),
                        end = Offset(cx + maxRadius, cy),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
                    )
                    drawLine(
                        color = Color(0xFF00B4D8).copy(alpha = 0.15f),
                        start = Offset(cx, cy - maxRadius),
                        end = Offset(cx, cy + maxRadius),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
                    )

                    // Draw detected blips (Landmarks / Hotels) pulsing
                    // Blip 1: Top-Right
                    drawCircle(
                        color = Color(0xFF52B788).copy(alpha = blipPulse1),
                        radius = 5.dp.toPx(),
                        center = Offset(cx + maxRadius * 0.45f, cy - maxRadius * 0.35f)
                    )
                    drawCircle(
                        color = Color(0xFF52B788).copy(alpha = blipPulse1 * 0.3f),
                        radius = 12.dp.toPx() * blipPulse1,
                        center = Offset(cx + maxRadius * 0.45f, cy - maxRadius * 0.35f),
                        style = Stroke(width = 1.5.dp.toPx())
                    )

                    // Blip 2: Bottom-Left
                    drawCircle(
                        color = Color(0xFF52B788).copy(alpha = blipPulse2),
                        radius = 4.dp.toPx(),
                        center = Offset(cx - maxRadius * 0.50f, cy + maxRadius * 0.40f)
                    )
                    drawCircle(
                        color = Color(0xFF52B788).copy(alpha = blipPulse2 * 0.3f),
                        radius = 10.dp.toPx() * blipPulse2,
                        center = Offset(cx - maxRadius * 0.50f, cy + maxRadius * 0.40f),
                        style = Stroke(width = 1.5.dp.toPx())
                    )

                    // Draw rotating radar sweep line with a subtle sector sweep gradient
                    rotate(sweepRotation, pivot = Offset(cx, cy)) {
                        drawArc(
                            brush = Brush.sweepGradient(
                                0.75f to Color(0xFF00B4D8).copy(alpha = 0.30f),
                                1.0f to Color(0xFF00B4D8).copy(alpha = 0.0f)
                            ),
                            startAngle = 0f,
                            sweepAngle = 90f,
                            useCenter = true,
                            size = Size(maxRadius * 2, maxRadius * 2),
                            topLeft = Offset(cx - maxRadius, cy - maxRadius)
                        )
                        drawLine(
                            color = Color(0xFF00B4D8).copy(alpha = 0.7f),
                            start = Offset(cx, cy),
                            end = Offset(cx + maxRadius, cy),
                            strokeWidth = 2.dp.toPx()
                        )
                    }

                    // Draw plane flight path trail (Idea 1)
                    drawArc(
                        brush = Brush.sweepGradient(
                            0f to Color(0xFF0077B6).copy(alpha = 0.9f),
                            0.6f to Color(0xFF0077B6).copy(alpha = 0.05f),
                            1f to Color.Transparent,
                            center = Offset(cx, cy)
                        ),
                        startAngle = airplaneRotation - 200f,
                        sweepAngle = 200f,
                        useCenter = false,
                        size = Size(maxRadius * 1.5f, maxRadius * 1.5f),
                        topLeft = Offset(cx - maxRadius * 0.75f, cy - maxRadius * 0.75f),
                        style = Stroke(
                            width = 2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f))
                        )
                    )

                    // Draw airplane vector moving and rotating dynamically
                    val planeRad = maxRadius * 0.75f
                    val angleRad = (airplaneRotation * PI / 180.0).toFloat()
                    val px = cx + cos(angleRad) * planeRad
                    val py = cy + sin(angleRad) * planeRad

                    // Rotate the draw scope to face the airplane direction
                    // Direction is perpendicular to the radius (tangent)
                    val tangentAngle = airplaneRotation + 180f
                    rotate(tangentAngle, pivot = Offset(px, py)) {
                        val scale = 0.9f
                        val planePath = Path().apply {
                            moveTo(px + 0f * scale, py - 12f * scale)
                            lineTo(px + 3f * scale, py - 4f * scale)
                            lineTo(px + 12f * scale, py + 2f * scale)
                            lineTo(px + 3f * scale, py + 2f * scale)
                            lineTo(px + 4f * scale, py + 8f * scale)
                            lineTo(px + 8f * scale, py + 10f * scale)
                            lineTo(px + 0f * scale, py + 9f * scale)
                            lineTo(px - 8f * scale, py + 10f * scale)
                            lineTo(px - 4f * scale, py + 8f * scale)
                            lineTo(px - 3f * scale, py + 2f * scale)
                            lineTo(px - 12f * scale, py + 2f * scale)
                            lineTo(px - 3f * scale, py - 4f * scale)
                            close()
                        }
                        drawPath(planePath, Color(0xFF00E5FF))
                        drawCircle(Color(0xFF00E5FF).copy(alpha = 0.5f), radius = 5.dp.toPx(), center = Offset(px, py))
                    }
                }
            }

            // Text section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = s.aiGenerating,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                // Animated Stage Text (Idea 2)
                AnimatedContent(
                    targetState = loadingStages[currentStageIndex],
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(400)) + slideInVertically(
                            animationSpec = tween(400),
                            initialOffsetY = { it / 2 })
                        ).togetherWith(
                            fadeOut(animationSpec = tween(300)) + slideOutVertically(
                                animationSpec = tween(300),
                                targetOffsetY = { -it / 2 })
                        )
                    },
                    label = "stageText"
                ) { stageText ->
                    Text(
                        text = stageText,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().height(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = s.aiGeneratingBody,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
