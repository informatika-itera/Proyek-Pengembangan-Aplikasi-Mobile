package com.example.movein.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.movein.presentation.getGrowthPhase

@Composable
fun GrowthScreen(
    momentum: Int,
    setMomentum: (Int) -> Unit,
    isLight: Boolean,
    modifier: Modifier = Modifier
) {
    var isDead by rememberSaveable { mutableStateOf(false) }
    val growth = getGrowthPhase(momentum)

    val treeEmoji = when {
        isDead -> "🥀"
        momentum == 0 -> "🌰"
        momentum < 50 -> "🌱"
        momentum < 150 -> "🌿"
        momentum < 300 -> "🌳"
        else -> "🌸"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Spa, null, tint = Color(0xFF10B981), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Growth",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isLight) Color(0xFF171717) else Color.White
                )
            }
            Text(
                text = "Fase perjalanan pemulihanmu.",
                fontSize = 14.sp,
                color = if (isLight) Color(0xFF737373) else Color(0xFFA3A3A3)
            )
        }

        // Tree Hero Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(if (isLight) Color.White else Color(0xFF171717).copy(alpha = 0.4f))
                .border(1.dp, if (isLight) Color(0xFFE5E7EB) else Color.White.copy(alpha = 0.05f), RoundedCornerShape(32.dp))
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            // Background Glow
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .blur(50.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                (if (isDead) Color(0xFFEF4444) else growth.color).copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = treeEmoji, fontSize = 72.sp)
                    Column(
                        modifier = Modifier.align(Alignment.BottomCenter).offset(y = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = momentum.toString(), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = if (isLight) Color(0xFF171717) else Color.White)
                        Text(text = "MOMENTUM", fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = Color(0xFF737373))
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = if (isDead) "Fase: Layu" else "Phase: ${growth.phase}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDead) Color(0xFFF87171) else growth.color
                )
                
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isDead) "Pohon telah mati, namun kamu selalu bisa menanam ulang harapan baru." else growth.desc,
                    fontSize = 12.sp,
                    color = if (isLight) Color(0xFF525252) else Color(0xFFA3A3A3),
                    modifier = Modifier.padding(horizontal = 24.dp),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (isDead) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF10B981).copy(alpha = 0.1f))
                            .border(1.dp, Color(0xFF10B981).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .clickable {
                                isDead = false
                                setMomentum(0)
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Refresh, null, tint = Color(0xFF34D399), modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Tanam Ulang", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF34D399))
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFEF4444).copy(alpha = 0.1f))
                            .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .clickable { isDead = true }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("Biarkan Mati", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF87171))
                    }
                }
            }
        }

        // Archive Card
        Column {
            Text("Personal Archive", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (isLight) Color(0xFF171717) else Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isLight) Color(0xFFEFF6FF) else Color(0xFF1E3A8A).copy(alpha = 0.2f))
                    .border(1.dp, if (isLight) Color(0xFFDBEAFE) else Color(0xFF1E3A8A).copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Icon(Icons.Default.FormatQuote, null, modifier = Modifier.size(16.dp), tint = if (isLight) Color(0xFF3B82F6) else Color(0xFF60A5FA))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"3 hari lalu kamu merasa Overwhelmed. Hari ini kamu berhasil mengumpulkan $momentum momentum. Proses tidak mengkhianati hasil.\"",
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 20.sp,
                        color = if (isLight) Color(0xFF374151) else Color(0xFFDBEAFE)
                    )
                }
            }
        }

        // Badges Grid
        Column {
            Text("Symbolic Memories", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (isLight) Color(0xFF171717) else Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            val badges = listOf(
                BadgeData("First Breath", "Selesaikan Zen Breathing", momentum < 5, Icons.Default.Air),
                BadgeData("Let It Go", "Bakar uneg-uneg (Yapping)", momentum < 10, Icons.Default.Whatshot),
                BadgeData("Soft Reset", "Selesaikan Tiny Win", momentum < 20, Icons.Default.Coffee),
                BadgeData("Touch Grass", "Capai fase Breathing", momentum < 50, Icons.Default.Spa)
            )
            
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                for (i in badges.indices step 2) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        BadgeItem(badges[i], isLight, modifier = Modifier.weight(1f))
                        if (i + 1 < badges.size) {
                            BadgeItem(badges[i + 1], isLight, modifier = Modifier.weight(1f))
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

data class BadgeData(val title: String, val desc: String, val locked: Boolean, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Composable
private fun BadgeItem(badge: BadgeData, isLight: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (badge.locked) {
                    if (isLight) Color(0xFFF9FAFB) else Color(0xFF171717).copy(alpha = 0.3f)
                } else {
                    if (isLight) Color.White else Color(0xFF171717).copy(alpha = 0.4f)
                }
            )
            .border(
                width = 1.dp,
                color = if (isLight) Color(0xFFE5E7EB) else Color.White.copy(alpha = if (badge.locked) 0.05f else 0.1f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (badge.locked) {
                            Color(0xFF262626)
                        } else {
                            if (isLight) Color(0xFFECFDF5) else Color(0xFF064E3B)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = badge.icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp).let { if (badge.locked) it.alpha(0.5f) else it },
                    tint = if (badge.locked) Color(0xFF737373) else (if (isLight) Color(0xFF10B981) else Color(0xFF34D399))
                )
            }
            
            Column {
                Text(text = badge.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isLight) Color(0xFF171717) else Color.White)
                Text(text = badge.desc, fontSize = 9.sp, color = Color(0xFF737373))
            }
        }
        
        if (badge.locked) {
            Icon(
                Icons.Default.Lock,
                null,
                modifier = Modifier.size(12.dp).align(Alignment.TopEnd),
                tint = Color(0xFFA3A3A3)
            )
        }
    }
}
