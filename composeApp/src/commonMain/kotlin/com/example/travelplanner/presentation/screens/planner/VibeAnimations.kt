package com.example.travelplanner.presentation.screens.planner

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.math.*

// ══════════════════════════════════════════════════════════════════════
//  ANIMATED CANVAS SCENES — satu per vibe
//  Semua menggunakan InfiniteTransition + Canvas, zero dependencies
// ══════════════════════════════════════════════════════════════════════

@Composable
fun AlamAnimatedScene(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "alam")
    val cloudX by transition.animateFloat(
        initialValue = -80f, targetValue = 120f,
        animationSpec = infiniteRepeatable(tween(7000, easing = LinearEasing), RepeatMode.Restart),
        label = "cloud"
    )
    val cloudX2 by transition.animateFloat(
        initialValue = 60f, targetValue = 220f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing), RepeatMode.Restart),
        label = "cloud2"
    )
    val treeSway by transition.animateFloat(
        initialValue = -2f, targetValue = 2f,
        animationSpec = infiniteRepeatable(tween(2500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "sway"
    )
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width; val h = size.height
        // Sky gradient
        drawRect(
            Brush.verticalGradient(listOf(Color(0xFF1B3A5C), Color(0xFF2D6A4F), Color(0xFF52B788))),
            size = size
        )
        // Clouds
        drawCloud(cloudX / 100f * w, h * 0.15f, w * 0.25f)
        drawCloud(cloudX2 / 100f * w, h * 0.22f, w * 0.18f)
        // Mountain silhouettes (back → front)
        val mountainPath = Path().apply {
            moveTo(0f, h)
            lineTo(0f, h * 0.55f)
            lineTo(w * 0.22f, h * 0.30f)
            lineTo(w * 0.40f, h * 0.50f)
            lineTo(w * 0.55f, h * 0.25f)
            lineTo(w * 0.70f, h * 0.45f)
            lineTo(w * 0.85f, h * 0.32f)
            lineTo(w, h * 0.48f)
            lineTo(w, h)
            close()
        }
        drawPath(mountainPath, Color(0xFF1B4332))
        // Snow cap
        val snowPath = Path().apply {
            moveTo(w * 0.55f, h * 0.25f)
            lineTo(w * 0.48f, h * 0.38f)
            lineTo(w * 0.62f, h * 0.38f)
            close()
        }
        drawPath(snowPath, Color(0xFFF0EDE5).copy(alpha = 0.85f))
        // Trees swaying
        listOf(0.08f, 0.18f, 0.30f, 0.75f, 0.88f).forEachIndexed { i, x ->
            drawPineTree(w * x, h * 0.72f, h * 0.20f, treeSway * (if (i % 2 == 0) 1f else -1f))
        }
        // Ground
        drawRect(Color(0xFF1B4332), topLeft = Offset(0f, h * 0.80f), size = Size(w, h * 0.20f))
    }
}

@Composable
fun PantaiAnimatedScene(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "pantai")
    val wave1Phase by transition.animateFloat(
        initialValue = 0f, targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(2200, easing = LinearEasing)),
        label = "w1"
    )
    val wave2Phase by transition.animateFloat(
        initialValue = PI.toFloat(), targetValue = 3 * PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(3100, easing = LinearEasing)),
        label = "w2"
    )
    val wave3Phase by transition.animateFloat(
        initialValue = PI.toFloat() / 2, targetValue = 2.5f * PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(1800, easing = LinearEasing)),
        label = "w3"
    )
    val sunPulse by transition.animateFloat(
        initialValue = 0.9f, targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "sun"
    )
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width; val h = size.height
        // Sky
        drawRect(Brush.verticalGradient(listOf(Color(0xFF1B3A5C), Color(0xFF0096C7), Color(0xFF48CAE4))), size = size)
        // Sun with glow
        val sunCx = w * 0.75f; val sunCy = h * 0.22f
        drawCircle(Color(0xFFFFBE0B).copy(alpha = 0.25f), radius = 28f * sunPulse, center = Offset(sunCx, sunCy))
        drawCircle(Color(0xFFFFBE0B).copy(alpha = 0.50f), radius = 20f * sunPulse, center = Offset(sunCx, sunCy))
        drawCircle(Color(0xFFFFF3C4), radius = 14f * sunPulse, center = Offset(sunCx, sunCy))
        // Ocean body
        drawRect(Color(0xFF0077B6).copy(alpha = 0.85f), topLeft = Offset(0f, h * 0.45f), size = Size(w, h * 0.55f))
        // Wave layers (back to front, progressively brighter)
        drawWave(w, h, baseY = h * 0.46f, phase = wave1Phase, amplitude = h * 0.030f,
            color = Color(0xFF0096C7).copy(alpha = 0.7f), segments = 16)
        drawWave(w, h, baseY = h * 0.52f, phase = wave2Phase, amplitude = h * 0.025f,
            color = Color(0xFF48CAE4).copy(alpha = 0.65f), segments = 14)
        drawWave(w, h, baseY = h * 0.60f, phase = wave3Phase, amplitude = h * 0.018f,
            color = Color(0xFFADE8F4).copy(alpha = 0.55f), segments = 12)
        // Foam line
        drawWave(w, h, baseY = h * 0.67f, phase = wave1Phase * 1.3f, amplitude = h * 0.010f,
            color = Color.White.copy(alpha = 0.40f), segments = 20)
        // Sandy beach
        drawRect(Brush.verticalGradient(
            listOf(Color(0xFFE9C46A), Color(0xFFDBA05A)),
            startY = h * 0.82f, endY = h
        ), topLeft = Offset(0f, h * 0.82f), size = Size(w, h * 0.18f))
    }
}

@Composable
fun KulinerAnimatedScene(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "kuliner")
    // 6 bubble particles per animasi
    val bubbleProgress = (0..5).map { i ->
        transition.animateFloat(
            initialValue = 0f, targetValue = 1f,
            animationSpec = infiniteRepeatable(
                tween(1600 + i * 280, easing = LinearEasing),
                RepeatMode.Restart,
                initialStartOffset = StartOffset(i * 260)
            ),
            label = "b$i"
        )
    }
    val steamWave by transition.animateFloat(
        initialValue = 0f, targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing)),
        label = "steam"
    )
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width; val h = size.height
        drawRect(Brush.verticalGradient(listOf(Color(0xFF7B1D1D), Color(0xFFB22222), Color(0xFFE63946))), size = size)
        // Bowl silhouette
        val bowlCx = w * 0.50f; val bowlY = h * 0.62f
        val bowlPath = Path().apply {
            moveTo(bowlCx - w * 0.32f, bowlY)
            cubicTo(bowlCx - w * 0.32f, bowlY + h * 0.25f,
                    bowlCx + w * 0.32f, bowlY + h * 0.25f,
                    bowlCx + w * 0.32f, bowlY)
            close()
        }
        drawPath(bowlPath, Color(0xFFFF6B6B).copy(alpha = 0.40f))
        drawPath(bowlPath, Color(0xFFFFF0E6).copy(alpha = 0.20f), style = Stroke(3f))
        // Steam curls rising
        for (s in 0..2) {
            val sx = bowlCx + (s - 1) * w * 0.16f
            val steamPath = Path().apply {
                moveTo(sx, bowlY - h * 0.02f)
                for (seg in 1..8) {
                    val yy = bowlY - seg * h * 0.055f
                    val xx = sx + sin(steamWave + seg * 0.9f + s * 2.1f) * w * 0.07f
                    lineTo(xx, yy)
                }
            }
            drawPath(steamPath, Color.White.copy(alpha = 0.22f), style = Stroke(3.5f, cap = StrokeCap.Round))
        }
        // Rising bubble particles
        val bubbleXPos = listOf(0.25f, 0.40f, 0.55f, 0.65f, 0.38f, 0.70f)
        bubbleProgress.forEachIndexed { i, prog ->
            val p = prog.value
            val bx = w * bubbleXPos[i] + sin(p * PI.toFloat() * 2 + i) * w * 0.04f
            val by = bowlY - p * h * 0.55f
            val alpha = (1f - p) * 0.70f
            val radius = (4f + i * 2f) * (1f - p * 0.5f)
            drawCircle(Color.White.copy(alpha = alpha), radius = radius, center = Offset(bx, by))
        }
    }
}

@Composable
fun SejarahAnimatedScene(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "sejarah")
    val rotate by transition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(18000, easing = LinearEasing)),
        label = "rot"
    )
    val innerRotate by transition.animateFloat(
        initialValue = 360f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(12000, easing = LinearEasing)),
        label = "irot"
    )
    val pulse by transition.animateFloat(
        initialValue = 0.92f, targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width; val h = size.height
        drawRect(Brush.verticalGradient(listOf(Color(0xFF2C1503), Color(0xFF7B3F00), Color(0xFFE07A5F))), size = size)
        // Temple pillars silhouette
        val pY = h * 0.50f; val pW = w * 0.09f; val pH = h * 0.50f
        listOf(0.12f, 0.28f, 0.72f, 0.88f).forEach { px ->
            drawRect(Color(0xFF3D1A00).copy(alpha = 0.6f),
                topLeft = Offset(w * px - pW / 2, pY), size = Size(pW, pH))
            // Capital top
            drawRect(Color(0xFF3D1A00).copy(alpha = 0.6f),
                topLeft = Offset(w * px - pW * 0.7f, pY - h * 0.03f), size = Size(pW * 1.4f, h * 0.03f))
        }
        // Pediment / roof
        val roofPath = Path().apply {
            moveTo(w * 0.05f, pY)
            lineTo(w * 0.50f, h * 0.28f)
            lineTo(w * 0.95f, pY)
            close()
        }
        drawPath(roofPath, Color(0xFF3D1A00).copy(alpha = 0.55f))
        // Rotating mandala
        val cx = w * 0.50f; val cy = h * 0.38f
        rotate(rotate, pivot = Offset(cx, cy)) {
            val r = minOf(w, h) * 0.22f * pulse
            for (k in 0..11) {
                val angle = k * 30f * (PI / 180f)
                val ex = cx + cos(angle) * r; val ey = cy + sin(angle.toFloat()) * r
                drawLine(Color(0xFFB8893A).copy(alpha = 0.60f), Offset(cx, cy), Offset(ex.toFloat(), ey), 1.5f)
                drawCircle(Color(0xFFB8893A).copy(alpha = 0.50f), 4f, Offset(ex.toFloat(), ey))
            }
            drawCircle(Color.Transparent, r * 0.55f, Offset(cx, cy),
                style = Stroke(1.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f))))
        }
        rotate(innerRotate, pivot = Offset(cx, cy)) {
            val r2 = minOf(w, h) * 0.12f * pulse
            for (k in 0..7) {
                val angle = k * 45f * (PI / 180f)
                val ex = cx + cos(angle) * r2; val ey = cy + sin(angle.toFloat()) * r2
                drawCircle(Color(0xFFF2CC8F).copy(alpha = 0.70f), 3f, Offset(ex.toFloat(), ey))
            }
            drawCircle(Color(0xFFF2CC8F).copy(alpha = 0.80f), 6f, Offset(cx, cy))
        }
    }
}

@Composable
fun SantaiAnimatedScene(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "santai")
    val sunRotate by transition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(12000, easing = LinearEasing)),
        label = "sun_rot"
    )
    val glowPulse by transition.animateFloat(
        initialValue = 0.85f, targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(2800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow"
    )
    val horizonShift by transition.animateFloat(
        initialValue = -1f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(5000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "horizon"
    )
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width; val h = size.height
        val cx = w * 0.50f; val cy = h * 0.38f
        // Sky
        drawRect(Brush.verticalGradient(listOf(Color(0xFF1B3A5C), Color(0xFF6B2D0E), Color(0xFFFF6B35), Color(0xFFFFBE0B))), size = size)
        // Sun outer glow rings
        drawCircle(Color(0xFFFFBE0B).copy(alpha = 0.10f * glowPulse), radius = 62f * glowPulse, center = Offset(cx, cy))
        drawCircle(Color(0xFFFFBE0B).copy(alpha = 0.20f * glowPulse), radius = 46f * glowPulse, center = Offset(cx, cy))
        // Rotating sun rays
        rotate(sunRotate, pivot = Offset(cx, cy)) {
            for (k in 0..11) {
                val angle = k * 30.0 * PI / 180.0
                val inner = 34f; val outer = 50f + (k % 3) * 6f
                val sx = cx + cos(angle) * inner; val sy = cy + sin(angle) * inner
                val ex = cx + cos(angle) * outer; val ey = cy + sin(angle) * outer
                drawLine(Color(0xFFFFBE0B).copy(alpha = 0.75f),
                    Offset(sx.toFloat(), sy.toFloat()), Offset(ex.toFloat(), ey.toFloat()), 2.5f)
            }
        }
        // Sun disc
        drawCircle(Color(0xFFFFF3C4), radius = 30f, center = Offset(cx, cy))
        drawCircle(Color(0xFFFFBE0B), radius = 26f, center = Offset(cx, cy))
        // Horizon sea — gentle shimmer
        val seaTop = h * 0.60f + horizonShift * 3f
        drawRect(Brush.verticalGradient(
            listOf(Color(0xFFE63946).copy(alpha = 0.7f), Color(0xFF0096C7).copy(alpha = 0.85f)),
            startY = seaTop, endY = h
        ), topLeft = Offset(0f, seaTop), size = Size(w, h - seaTop))
        // Reflection streak of sun on water
        val refPath = Path().apply {
            moveTo(cx - w * 0.06f, seaTop)
            lineTo(cx - w * 0.18f, h)
            lineTo(cx + w * 0.18f, h)
            lineTo(cx + w * 0.06f, seaTop)
            close()
        }
        drawPath(refPath, Brush.verticalGradient(
            listOf(Color(0xFFFFBE0B).copy(alpha = 0.55f), Color.Transparent),
            startY = seaTop, endY = h
        ))
        // Palm tree silhouette
        val palmX = w * 0.85f; val palmBase = h * 0.90f; val palmH = h * 0.35f
        val trunkPath = Path().apply {
            moveTo(palmX - 5f, palmBase)
            quadraticTo(palmX - 15f, palmBase - palmH * 0.5f, palmX - 20f, palmBase - palmH)
            lineTo(palmX - 12f, palmBase - palmH)
            quadraticTo(palmX - 8f, palmBase - palmH * 0.5f, palmX + 2f, palmBase)
            close()
        }
        drawPath(trunkPath, Color(0xFF1B3A5C).copy(alpha = 0.7f))
        // Palm leaves
        val leafTip = Offset(palmX - 20f, palmBase - palmH)
        listOf(-70f, -50f, -30f, -10f, 10f, 30f).forEach { angle ->
            val rad = angle * PI / 180.0
            val ex = leafTip.x + cos(rad) * w * 0.22f
            val ey = leafTip.y + sin(rad) * h * 0.15f
            drawLine(Color(0xFF1B3A5C).copy(alpha = 0.65f), leafTip,
                Offset(ex.toFloat(), ey.toFloat()), 3.5f)
        }
    }
}

@Composable
fun PetualanganAnimatedScene(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "petualangan")
    val starTwinkle by transition.animateFloat(
        initialValue = 0f, targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing)),
        label = "twinkle"
    )
    val lightningProgress by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            keyframes {
                durationMillis = 4000
                0f at 0 using LinearEasing
                1f at 300 using FastOutSlowInEasing
                1f at 600 using LinearEasing
                0f at 900 using LinearEasing
                0f at 4000 using LinearEasing
            }
        ),
        label = "lightning"
    )
    val particleProgress = (0..8).map { i ->
        transition.animateFloat(
            initialValue = 0f, targetValue = 1f,
            animationSpec = infiniteRepeatable(
                tween(1200 + i * 180, easing = LinearEasing),
                RepeatMode.Restart,
                initialStartOffset = StartOffset(i * 130)
            ),
            label = "p$i"
        )
    }
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width; val h = size.height
        drawRect(Brush.verticalGradient(listOf(Color(0xFF0D0221), Color(0xFF2D0A4E), Color(0xFF6A0572))), size = size)
        // Stars background
        val starSeeds = listOf(0.15f to 0.08f, 0.35f to 0.14f, 0.60f to 0.06f, 0.80f to 0.18f,
            0.25f to 0.25f, 0.70f to 0.28f, 0.90f to 0.10f, 0.05f to 0.20f, 0.50f to 0.30f)
        starSeeds.forEachIndexed { i, (sx, sy) ->
            val twinkAlpha = 0.5f + 0.5f * sin(starTwinkle + i * 0.8f).toFloat()
            drawCircle(Color.White.copy(alpha = twinkAlpha * 0.85f),
                radius = 2.5f + i % 3f, center = Offset(w * sx, h * sy))
        }
        // Mountain ridge (jagged peaks)
        val peakPath = Path().apply {
            moveTo(0f, h)
            lineTo(0f, h * 0.58f)
            lineTo(w * 0.10f, h * 0.48f)
            lineTo(w * 0.18f, h * 0.56f)
            lineTo(w * 0.28f, h * 0.35f)  // main peak
            lineTo(w * 0.36f, h * 0.52f)
            lineTo(w * 0.45f, h * 0.42f)
            lineTo(w * 0.52f, h * 0.55f)
            lineTo(w * 0.62f, h * 0.32f)  // secondary peak
            lineTo(w * 0.70f, h * 0.50f)
            lineTo(w * 0.78f, h * 0.44f)
            lineTo(w * 0.88f, h * 0.55f)
            lineTo(w, h * 0.50f)
            lineTo(w, h)
            close()
        }
        drawPath(peakPath, Color(0xFF1A0535))
        // Lightning bolt
        if (lightningProgress > 0.01f) {
            val lx = w * 0.62f; val ly = h * 0.05f
            val boltPath = Path().apply {
                moveTo(lx, ly)
                lineTo(lx - 12f, ly + h * 0.12f)
                lineTo(lx - 4f, ly + h * 0.12f)
                lineTo(lx - 18f, ly + h * 0.26f)
                lineTo(lx - 6f, ly + h * 0.26f)
                lineTo(lx - 22f, ly + h * 0.38f)
            }
            val la = lightningProgress
            drawPath(boltPath, Color(0xFFFFBE0B).copy(alpha = la * 0.9f), style = Stroke(3f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            // Glow around bolt
            drawCircle(Color(0xFFFFBE0B).copy(alpha = la * 0.25f), radius = 30f, center = Offset(lx - 10f, ly + h * 0.18f))
        }
        // Energy particles streaking diagonally
        val pXBase = listOf(0.15f,0.30f,0.45f,0.60f,0.20f,0.70f,0.35f,0.55f,0.80f)
        particleProgress.forEachIndexed { i, prog ->
            val p = prog.value
            val px = (pXBase[i] + p * 0.3f) * w
            val py = (0.40f + p * 0.55f) * h
            val alpha = (1f - p) * 0.8f
            drawCircle(Color(0xFFAB47BC).copy(alpha = alpha), radius = 3f + i % 3f, center = Offset(px, py))
            // Trailing streak
            drawLine(Color(0xFFE040FB).copy(alpha = alpha * 0.5f),
                Offset(px - p * 15f, py - p * 10f), Offset(px, py), 1.5f)
        }
    }
}

// ══════════════════════════════════════════════════════════════════════
//  HELPER DRAW FUNCTIONS
// ══════════════════════════════════════════════════════════════════════

private fun DrawScope.drawWave(
    w: Float, h: Float, baseY: Float, phase: Float, amplitude: Float,
    color: Color, segments: Int
) {
    val path = Path()
    val step = w / segments
    path.moveTo(0f, baseY + amplitude * sin(phase))
    for (i in 1..segments) {
        val x = i * step
        val y = baseY + amplitude * sin(phase + i * 2 * PI.toFloat() / segments)
        path.lineTo(x, y)
    }
    path.lineTo(w, h); path.lineTo(0f, h); path.close()
    drawPath(path, color)
}

private fun DrawScope.drawCloud(cx: Float, cy: Float, size: Float) {
    val alpha = 0.35f
    drawCircle(Color.White.copy(alpha), size * 0.38f, Offset(cx, cy))
    drawCircle(Color.White.copy(alpha), size * 0.28f, Offset(cx - size * 0.30f, cy + size * 0.08f))
    drawCircle(Color.White.copy(alpha), size * 0.25f, Offset(cx + size * 0.28f, cy + size * 0.10f))
}

private fun DrawScope.drawPineTree(cx: Float, baseY: Float, treeH: Float, sway: Float) {
    rotate(sway, pivot = Offset(cx, baseY)) {
        val trunkW = treeH * 0.08f
        drawRect(Color(0xFF1B3A2A).copy(alpha = 0.8f),
            topLeft = Offset(cx - trunkW / 2, baseY - treeH * 0.22f),
            size = Size(trunkW, treeH * 0.22f))
        listOf(0f, 0.22f, 0.45f).forEachIndexed { i, offset ->
            val layerW = treeH * (0.55f - i * 0.12f)
            val layerH = treeH * (0.35f - i * 0.05f)
            val layerY = baseY - treeH * (0.22f + offset)
            val treePath = Path().apply {
                moveTo(cx, layerY - layerH)
                lineTo(cx - layerW / 2, layerY)
                lineTo(cx + layerW / 2, layerY)
                close()
            }
            drawPath(treePath, Color(0xFF1B4332).copy(alpha = 0.85f - i * 0.1f))
        }
    }
}
