package com.studymate.presentation.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studymate.presentation.theme.BackgroundDark
import com.studymate.presentation.theme.PrimaryLight
import com.studymate.Res
import com.studymate.app_logo
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

@Composable
fun SplashScreen(onNavigateToHome: () -> Unit) {
    val logoOffsetY = remember { Animatable(-1000f) } // Mulai dari jauh di atas
    val textAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // 1. Animasi Logo jatuh dari atas ke bawah dengan efek pantulan (bounce)
        logoOffsetY.animateTo(
            targetValue = 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        // 2. Jeda sebentar setelah logo mendarat
        delay(200)
        // 3. Teks "StudyMate" muncul perlahan
        textAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200)
        )
        // 4. Jeda sebelum pindah ke Home
        delay(1500)
        onNavigateToHome()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.app_logo),
                contentDescription = "StudyMate Logo",
                modifier = Modifier
                    .size(180.dp)
                    .graphicsLayer {
                        translationY = logoOffsetY.value
                    }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(textAlpha.value)
            ) {
                Text(
                    text = "StudyMate",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Black,
                    color = PrimaryLight,
                    letterSpacing = 4.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Belajar Cerdas, Raih Prestasi 🎓",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
