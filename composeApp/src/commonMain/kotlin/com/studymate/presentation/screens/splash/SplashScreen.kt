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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studymate.presentation.theme.BackgroundDark
import com.studymate.presentation.theme.PrimaryLight
import com.studymate.Res
import com.studymate.app_logo
import com.studymate.app_logo
import com.studymate.logo_if_itera
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

@Composable
fun SplashScreen(onNavigateToHome: () -> Unit) {
    var showITLogo by remember { mutableStateOf(true) }
    
    val logoOffsetY = remember { Animatable(-1000f) }
    val dolphinAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Phase 1: Show IT Itera Logo (Immediately Wide)
        delay(1500) // Show the IT logo (Wide) for a bit
        showITLogo = false
        
        // Phase 2: Show Dolphin Animation
        dolphinAlpha.snapTo(1f)
        logoOffsetY.animateTo(
            targetValue = 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        delay(200)
        textAlpha.animateTo(1f, tween(1000))
        delay(1500)
        onNavigateToHome()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (showITLogo) Color.White else BackgroundDark),
        contentAlignment = Alignment.Center
    ) {
        if (showITLogo) {
            // Teknik Informatika Logo - Full Width (No Fade-in, just be there)
            Image(
                painter = painterResource(Res.drawable.logo_if_itera),
                contentDescription = "IF Itera Logo",
                modifier = Modifier
                    .fillMaxWidth(0.85f),
                contentScale = ContentScale.Fit
            )
        } else {
            // Dolphin StudyMate Animation
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.app_logo),
                    contentDescription = "StudyMate Logo",
                    modifier = Modifier
                        .size(180.dp)
                        .alpha(dolphinAlpha.value)
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
}
