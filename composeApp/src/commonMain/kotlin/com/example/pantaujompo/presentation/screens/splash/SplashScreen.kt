package com.example.pantaujompo.presentation.screens.splash

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import com.example.pantaujompo.presentation.components.AppLogo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.pantaujompo.utils.SoundManager
import androidx.compose.ui.graphics.graphicsLayer
import org.koin.compose.koinInject
import com.example.pantaujompo.data.local.datastore.UserPreferences
import com.example.pantaujompo.core.util.AppStrings
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun SplashScreen(
    hasCompletedProfile: Boolean?,
    onNavigateToHome: () -> Unit,
    onNavigateToProfileSetup: () -> Unit
) {
    var showOnboarding by remember { mutableStateOf(false) }
    
    // Animation States
    val laptopScale = remember { Animatable(0f) }
    val laptopRotation = remember { Animatable(0f) }
    
    val portalScale = remember { Animatable(0f) }
    val portalRotation = remember { Animatable(0f) }
    
    val runnerScale = remember { Animatable(0f) }
    val runnerRotation = remember { Animatable(-180f) } // Spin out from the portal
    val overallAlpha = remember { Animatable(1f) }

    val soundManager = remember { SoundManager() }

    LaunchedEffect(key1 = hasCompletedProfile) {
        // Laptop appears
        laptopScale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        delay(600)

        // Black Hole Portal Opens
        launch { portalRotation.animateTo(360f, tween(1000, easing = LinearEasing)) }
        portalScale.animateTo(1f, tween(300))
        
        soundManager.playSplashSound()
        
        // Sucked into portal! (Spiraling wildly, shrinking to 0)
        launch { 
            laptopRotation.animateTo(1080f, tween(800, easing = CubicBezierEasing(0.4f, 0.0f, 1f, 1f))) 
        }
        laptopScale.animateTo(0f, tween(800, easing = CubicBezierEasing(0.4f, 0.0f, 1f, 1f)))
        
        // Portal shrinks and vanishes
        portalScale.animateTo(0f, tween(150))
        
        delay(100) // Silent void

        // Runner Emerges by spinning out!
        launch { runnerRotation.animateTo(0f, tween(600, easing = FastOutSlowInEasing)) }
        runnerScale.animateTo(1.5f, tween(600, easing = FastOutSlowInEasing))
        delay(700) // Hold runner proudly

        // Fade smoothly to black instead of aggressively zooming (mudah ngehitam)
        if (hasCompletedProfile != null) {
            overallAlpha.animateTo(0f, tween(600, easing = LinearEasing))
            if (hasCompletedProfile == true) {
                onNavigateToHome()
            } else {
                showOnboarding = true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF050505), Color(0xFF101010)))),
        contentAlignment = Alignment.Center
    ) {
        if (!showOnboarding) {
            Box(modifier = Modifier.fillMaxSize().alpha(overallAlpha.value), contentAlignment = Alignment.Center) {
                // The Black Hole Portal (Spinning Gradient)
                Box(modifier = Modifier
                    .size(160.dp)
                    .scale(portalScale.value)
                    .graphicsLayer { rotationZ = portalRotation.value }
                    .clip(CircleShape)
                    .background(Brush.sweepGradient(listOf(Color.Black, Color(0xFF00FFCC).copy(alpha=0.8f), Color(0xFF00B3FF).copy(alpha=0.5f), Color.Black)))
                )
                
                // Pure Black center for the hole
                Box(modifier = Modifier
                    .size(120.dp)
                    .scale(portalScale.value)
                    .clip(CircleShape)
                    .background(Color.Black)
                )

                // Laptop (Mode Jompo)
                if (laptopScale.value > 0f) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.graphicsLayer {
                            rotationZ = laptopRotation.value
                            scaleX = laptopScale.value
                            scaleY = laptopScale.value
                        }
                    ) {
                        Icon(Icons.Default.Computer, "Mode Jompo", tint = Color.LightGray, modifier = Modifier.size(80.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Mode Jompo...", color = Color.LightGray, fontSize = 18.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    }
                }

                // Emergent Runner (Pantau Jompo)
                if (runnerScale.value > 0f) {
                    Box(
                        modifier = Modifier
                            .graphicsLayer { rotationZ = runnerRotation.value }
                            .scale(runnerScale.value), 
                        contentAlignment = Alignment.Center
                    ) {
                        Box(modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                            .background(Brush.radialGradient(listOf(Color(0xFF00E676).copy(alpha = 0.6f), Color.Transparent)))
                        )
                        AppLogo(showSubtitle = true)
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showOnboarding,
            enter = fadeIn(tween(400)), // Fade in gently since we just zoomed in
            exit = fadeOut()
        ) {
            OnboardingPager(onFinish = onNavigateToProfileSetup)
        }
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun OnboardingPager(onFinish: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()
    val userPreferences: UserPreferences = koinInject()

    val language by userPreferences.language.collectAsState(initial = "id")
    fun str(key: String): String = AppStrings.get(key, language)
    
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF080808))) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            when (page) {
                0 -> OnboardingPage(
                    icons = listOf(Icons.Default.DirectionsRun, Icons.Default.DirectionsBike, Icons.Default.Timer),
                    title = str("onboarding_1_title"),
                    desc = str("onboarding_1_desc"),
                    color = Color(0xFF00E676)
                )
                1 -> OnboardingPage(
                    icons = listOf(Icons.Default.Restaurant, Icons.Default.CameraAlt, Icons.Default.MonitorWeight),
                    title = str("onboarding_2_title"),
                    desc = str("onboarding_2_desc"),
                    color = Color(0xFF00BCD4) // NeonCyan
                )
                2 -> OnboardingPage(
                    icons = listOf(Icons.Default.AutoAwesome, Icons.Default.Favorite, Icons.Default.TrendingUp),
                    title = str("onboarding_3_title"),
                    desc = str("onboarding_3_desc"),
                    color = Color(0xFFFF4081) // NeonPurple
                )
            }
        }

        // Bottom section
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xFF080808).copy(alpha = 0.95f), Color(0xFF000000))))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Dot indicators
            Row(
                Modifier.wrapContentHeight().fillMaxWidth().padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(3) { iteration ->
                    val color = if (pagerState.currentPage == iteration) Color(0xFF00E676) else Color.DarkGray
                    val width = if (pagerState.currentPage == iteration) 24.dp else 8.dp
                    Box(modifier = Modifier.padding(horizontal = 4.dp).clip(CircleShape).background(color).height(8.dp).width(width))
                }
            }

            // Glass Button with Interaction
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val buttonScale by animateFloatAsState(
                targetValue = if (isPressed) 0.95f else 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            )
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .scale(buttonScale)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFF00E676).copy(alpha = 0.8f), Color(0xFF00BCD4).copy(alpha = 0.6f))))
                    .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            ) {
                Button(
                    onClick = {
                        if (pagerState.currentPage < 2) {
                            coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        } else {
                            onFinish()
                        }
                    },
                    interactionSource = interactionSource,
                    modifier = Modifier.fillMaxSize(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = if (pagerState.currentPage == 2) str("mulai_setup_profil") else str("selanjutnya"),
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingPage(icons: List<ImageVector>, title: String, desc: String, color: Color) {
    // Infinite floating animation
    val infiniteTransition = rememberInfiniteTransition()
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(animation = tween(1500, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .align(Alignment.TopCenter)
                .background(Brush.radialGradient(listOf(color.copy(alpha = 0.15f), Color.Transparent), radius = 600f))
        )
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp).padding(bottom = 120.dp)
        ) {
            // Floating Cluster
            Box(
                modifier = Modifier.size(240.dp).offset(y = floatAnim.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(color.copy(alpha=0.2f), color.copy(alpha=0.05f))))
                        .border(2.dp, color.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icons[0], contentDescription = null, tint = color, modifier = Modifier.size(72.dp))
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = 16.dp, y = 32.dp)
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E1E1E).copy(alpha=0.8f))
                        .border(1.dp, color.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icons[1], contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-16).dp, y = (-32).dp)
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E1E1E).copy(alpha=0.8f))
                        .border(1.dp, color.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icons[2], contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                }
            }
            Spacer(modifier = Modifier.height(48.dp))
            Text(text = title, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center, lineHeight = 36.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = desc, color = Color.LightGray, fontSize = 16.sp, textAlign = TextAlign.Center, lineHeight = 24.sp)
        }
    }
}