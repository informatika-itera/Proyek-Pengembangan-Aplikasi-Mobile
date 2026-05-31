package com.example.pantaujompo.presentation.screens.settings

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pantaujompo.core.util.AppStrings
import com.example.pantaujompo.data.local.datastore.UserPreferences
import com.example.pantaujompo.presentation.theme.DarkBackground
import com.example.pantaujompo.presentation.theme.MeshBackground
import com.example.pantaujompo.presentation.theme.glassCard
import org.koin.compose.koinInject

@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit,
    userPreferences: UserPreferences = koinInject()
) {
    val language by userPreferences.language.collectAsState(initial = "id")
    fun str(key: String): String = AppStrings.get(key, language)

    val isDark = MaterialTheme.colorScheme.background == DarkBackground
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    val accentColor = MaterialTheme.colorScheme.primary
    val surfaceColor = if (isDark) Color(0xFF151515) else MaterialTheme.colorScheme.surface

    val infiniteTransition = rememberInfiniteTransition()
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(animation = tween(2000, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse)
    )

    MeshBackground(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // ======== HEADER ========
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(surfaceColor)
                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                        .clickable { onNavigateBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ArrowBack, null, tint = textPrimary, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(str("tentang_aplikasi"), color = textPrimary, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                    Text("Pantau Jompo Mobile", color = accentColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ======== HERO SECTION (GLOWING APP LOGO / TITLE) ========
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassCard(shape = RoundedCornerShape(28.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    com.example.pantaujompo.presentation.components.AppLogo(
                        logoSize = 72.dp,
                        iconSize = 36.dp,
                        textSize = 22.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Versi 1.0.0 (Premium Editon)", color = textSecondary, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ======== JOKE GEN Z JOMPO CARD ========
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassCard(shape = RoundedCornerShape(24.dp))
                    .border(1.dp, Color(0xFF00BCD4).copy(0.2f), RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFF00BCD4).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Info, null, tint = Color(0xFF00BCD4), modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(str("apa_itu_pantau_jompo"), color = textPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = str("apa_itu_pantau_jompo_desc"),
                        color = textPrimary,
                        fontSize = 13.sp,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Justify
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ======== TECH STACK CARD ========
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassCard(shape = RoundedCornerShape(24.dp))
                    .border(1.dp, Color(0xFF00E676).copy(0.2f), RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFF00E676).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Code, null, tint = Color(0xFF00E676), modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(str("teknologi_di_balik_layar"), color = textPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    val techStack = listOf(
                        "Kotlin Multiplatform Mobile (KMM)" to "Satu kode untuk Android & iOS.",
                        "Jetpack Compose Multiplatform UI" to "UI modern dan reaktif.",
                        "Gemini 3.1 Flash API" to "AI Advisor & Scanner canggih.",
                        "OSMDroid & Map Tiles" to "Tracking GPS real-time.",
                        "Room Database" to "Penyimpanan lokal yang cepat.",
                        "Koin" to "Dependency Injection ringan."
                    )
                    
                    techStack.forEach { (tech, desc) ->
                        Row(modifier = Modifier.padding(bottom = 8.dp)) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(16.dp).padding(top = 2.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(tech, color = textPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text(desc, color = textSecondary, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ======== DEVELOPER / KONTRIBUTOR CARD ========
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassCard(shape = RoundedCornerShape(24.dp))
                    .border(1.dp, Color(0xFFFF9100).copy(0.2f), RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFFFF9100).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Group, null, tint = Color(0xFFFF9100), modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(str("tim_pengembang"), color = textPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Dev 1
                    DevCard(
                        name = "Muhammad Piela Nugraha",
                        nim = "123140200",
                        role = "Teknik Informatika",
                        offsetY = floatAnim,
                        gradientColors = listOf(Color(0xFFFF9100), Color(0xFFFF3D00)),
                        textColor = textPrimary,
                        textSecondaryColor = textSecondary
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Dev 2
                    DevCard(
                        name = "Pradana Figo Ariasyah",
                        nim = "123140063",
                        role = "Teknik Informatika",
                        offsetY = -floatAnim,
                        gradientColors = listOf(Color(0xFF00BCD4), Color(0xFF00E676)),
                        textColor = textPrimary,
                        textSecondaryColor = textSecondary
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.EmojiEvents, null, tint = Color(0xFFFFC107), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(str("pengembang_kelas_rb"), color = textSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun DevCard(name: String, nim: String, role: String, offsetY: Float, gradientColors: List<Color>, textColor: Color, textSecondaryColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(listOf(Color.White.copy(0.05f), Color.Transparent)))
            .border(1.dp, Color.White.copy(0.1f), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // 3D Avatar representation
            Box(
                modifier = Modifier
                    .offset(y = offsetY.dp)
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(gradientColors))
                    .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(name, color = textColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(role, color = textSecondaryColor, fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(gradientColors[0].copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(nim, color = gradientColors[0], fontWeight = FontWeight.ExtraBold, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}
