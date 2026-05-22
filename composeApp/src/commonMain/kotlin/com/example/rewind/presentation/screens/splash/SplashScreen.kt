package com.example.rewind.presentation.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rewind.presentation.theme.BackgroundDark
import com.example.rewind.presentation.theme.GoldAmber
import com.example.rewind.presentation.theme.SurfaceDark
import com.example.rewind.presentation.theme.TextSecondary
import com.example.rewind.presentation.theme.TextWarm
import com.example.rewind.presentation.theme.TheaterRed
import com.example.rewind.presentation.theme.VelvetRed
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val logoScale = remember { Animatable(0.4f) }
    val logoAlpha = remember { Animatable(0f) }
    val titleAlpha = remember { Animatable(0f) }
    val titleOffset = remember { Animatable(20f) }
    val dividerAlpha = remember { Animatable(0f) }
    val dividerScale = remember { Animatable(0f) }
    val taglineAlpha = remember { Animatable(0f) }
    val bottomAlpha = remember { Animatable(0f) }
    val glowAlpha = remember { Animatable(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "ambient")

    val topGlowPulse by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "topGlow"
    )

    val bottomGlowPulse by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bottomGlow"
    )

    val logoBorderPulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoBorder"
    )

    LaunchedEffect(Unit) {
        glowAlpha.animateTo(1f, animationSpec = tween(500))
        logoScale.animateTo(1f, animationSpec = tween(900, easing = FastOutSlowInEasing))
        logoAlpha.animateTo(1f, animationSpec = tween(700))
        delay(150)
        titleOffset.animateTo(0f, animationSpec = tween(600, easing = FastOutSlowInEasing))
        titleAlpha.animateTo(1f, animationSpec = tween(600))
        delay(200)
        dividerScale.animateTo(1f, animationSpec = tween(500, easing = FastOutSlowInEasing))
        dividerAlpha.animateTo(1f, animationSpec = tween(400))
        delay(150)
        taglineAlpha.animateTo(1f, animationSpec = tween(700))
        delay(200)
        bottomAlpha.animateTo(1f, animationSpec = tween(500))
        delay(1200)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to BackgroundDark,
                        0.4f to SurfaceDark,
                        0.7f to BackgroundDark,
                        1.0f to BackgroundDark
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(400.dp)
                .offset(y = (-80).dp, x = 20.dp)
                .blur(90.dp)
                .alpha(glowAlpha.value * topGlowPulse)
                .background(
                    Brush.radialGradient(
                        colorStops = arrayOf(
                            0.0f to GoldAmber.copy(alpha = 0.22f),
                            0.5f to TheaterRed.copy(alpha = 0.12f),
                            1.0f to Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(350.dp)
                .offset(y = 160.dp, x = (-50).dp)
                .blur(80.dp)
                .alpha(glowAlpha.value * bottomGlowPulse)
                .background(
                    Brush.radialGradient(
                        colorStops = arrayOf(
                            0.0f to VelvetRed.copy(alpha = 0.35f),
                            0.6f to TheaterRed.copy(alpha = 0.1f),
                            1.0f to Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(y = 80.dp, x = 130.dp)
                .blur(50.dp)
                .alpha(glowAlpha.value * topGlowPulse * 0.5f)
                .background(
                    Brush.radialGradient(
                        colors = listOf(GoldAmber.copy(alpha = 0.18f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .scale(logoScale.value)
                    .alpha(logoAlpha.value),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(148.dp)
                        .blur(30.dp)
                        .alpha(logoBorderPulse * 0.8f)
                        .background(
                            Brush.radialGradient(
                                colorStops = arrayOf(
                                    0.0f to GoldAmber.copy(alpha = 0.55f),
                                    0.5f to TheaterRed.copy(alpha = 0.25f),
                                    1.0f to Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )
                Box(
                    modifier = Modifier
                        .size(112.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .background(
                            Brush.linearGradient(
                                colorStops = arrayOf(
                                    0.0f to SurfaceDark,
                                    0.4f to VelvetRed,
                                    1.0f to TheaterRed
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        GoldAmber.copy(alpha = 0.08f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    Text("🎬", fontSize = 46.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .alpha(titleAlpha.value)
                    .offset(y = titleOffset.value.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "REWIND",
                    color = GoldAmber,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 10.sp
                )
                Text(
                    text = "Film & Series Tracker",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 3.sp
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier
                    .alpha(dividerAlpha.value)
                    .scale(scaleX = dividerScale.value, scaleY = 1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color.Transparent, GoldAmber.copy(alpha = 0.5f))
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .background(GoldAmber.copy(alpha = 0.8f), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(GoldAmber.copy(alpha = 0.5f), Color.Transparent)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "\"karena setiap tontonan layak untuk dikenang\"",
                color = TextWarm.copy(alpha = 0.5f),
                fontSize = 11.sp,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
                lineHeight = 17.sp,
                modifier = Modifier
                    .alpha(taglineAlpha.value)
                    .padding(horizontal = 48.dp)
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
                .alpha(bottomAlpha.value),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(TheaterRed.copy(alpha = 0.6f), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(GoldAmber.copy(alpha = 0.8f), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(TheaterRed.copy(alpha = 0.6f), CircleShape)
                )
            }
            Text(
                text = "by Rewind Team",
                color = TextSecondary.copy(alpha = 0.35f),
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}