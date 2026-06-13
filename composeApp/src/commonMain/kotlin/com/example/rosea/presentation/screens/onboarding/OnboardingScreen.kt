package com.example.rosea.presentation.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.rosea.data.local.datastore.UserPreferences
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun OnboardingScreen(
    onNavigateToHome: () -> Unit,
    userPreferences: UserPreferences = koinInject()
) {
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF5F5),
                        Color(0xFFFFE4E1)
                    )
                )
            )
    ) {
        // Decorative Makeup Background Elements
        Box(modifier = Modifier.fillMaxSize()) {
            Surface(
                modifier = Modifier
                    .size(240.dp)
                    .offset(x = (-40).dp, y = 60.dp)
                    .clip(RoundedCornerShape(40.dp)),
                color = Color.White.copy(alpha = 0.3f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🎨", fontSize = 120.sp, modifier = Modifier.offset(x = 20.dp))
                }
            }

            Text(
                "💄", 
                fontSize = 80.sp, 
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 100.dp, end = 40.dp)
                    .offset(y = 20.dp)
            )
            
            Text(
                "🖌️", 
                fontSize = 50.sp, 
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 200.dp, start = 60.dp)
            )
        }

        // Professional Glass Card
        Surface(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .height(340.dp),
            shape = RoundedCornerShape(40.dp),
            color = Color.White.copy(alpha = 0.6f),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White.copy(alpha = 0.8f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1.3f)) {
                    Surface(
                        color = Color(0xFFC2185B).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "PREMIUM COSMETICS",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFC2185B),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "Rosea",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF880E4F),
                            letterSpacing = (-1).sp
                        )
                    )
                    Text(
                        text = "Beauty Lab",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC2185B)
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Temukan koleksi makeup eksklusif yang dirancang untuk menonjolkan kecantikan unik Anda.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF4A148C),
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Surface(
                    modifier = Modifier
                        .size(120.dp, 190.dp)
                        .padding(start = 8.dp),
                    shape = RoundedCornerShape(28.dp),
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    AsyncImage(
                        model = "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?auto=format&fit=crop&q=80&w=400",
                        contentDescription = "Cosmetics",
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        // Action Area
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp, start = 32.dp, end = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.size(24.dp, 6.dp).clip(CircleShape).background(Color(0xFFC2185B)))
                Box(Modifier.size(6.dp, 6.dp).clip(CircleShape).background(Color(0xFFC2185B).copy(alpha = 0.3f)))
                Box(Modifier.size(6.dp, 6.dp).clip(CircleShape).background(Color(0xFFC2185B).copy(alpha = 0.3f)))
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            Button(
                onClick = {
                    scope.launch {
                        userPreferences.setOnboardingCompleted()
                        onNavigateToHome()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFAD1457),
                                    Color(0xFFD81B60)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Mulai Sekarang",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        ),
                        color = Color.White
                    )
                }
            }
        }
    }
}
