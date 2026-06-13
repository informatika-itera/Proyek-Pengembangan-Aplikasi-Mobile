package com.example.raillog.presentation.screens.welcome

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.raillog.core.util.RequestNotificationPermission
import com.example.raillog.data.local.datastore.UserPreferences
import com.example.raillog.presentation.theme.RailLogColors
import com.example.raillog.presentation.theme.Spacing
import org.koin.compose.koinInject

@Composable
fun WelcomeScreen(
    onNavigateToLogin: () -> Unit,
    onAutoLogin: (String) -> Unit = {},
    userPreferences: UserPreferences = koinInject()
) {
    RequestNotificationPermission()

    val savedRole by userPreferences.userRole.collectAsState(initial = "")

    LaunchedEffect(savedRole) {
        if (savedRole.isNotEmpty()) onAutoLogin(savedRole)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RailLogColors.Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.pagePadding)
                .statusBarsPadding()
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top — logo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = Spacing.sm)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(RailLogColors.PrimaryAction),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Train,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    "RailLog",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = RailLogColors.TextPrimary
                )
            }

            // Center — headline
            Column {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(RailLogColors.Brand50)
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Text(
                        "Logistik Perkeretaapian",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = RailLogColors.PrimaryAction
                    )
                }

                Spacer(Modifier.height(Spacing.md))

                Text(
                    "Kelola rantai pasok\nkereta api dengan\npresisi.",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 42.sp,
                    color = RailLogColors.TextPrimary
                )

                Spacer(Modifier.height(Spacing.md))

                Text(
                    "Pemantauan real-time, verifikasi dokumen\nberbasis AI, dan manajemen inventaris\ndalam satu platform.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = RailLogColors.TextSecondary,
                    lineHeight = 22.sp
                )
            }

            // Bottom — CTA
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                Button(
                    onClick = onNavigateToLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RailLogColors.PrimaryAction,
                        contentColor   = Color.White
                    )
                ) {
                    Text("Masuk ke Sistem", fontWeight = FontWeight.Medium, fontSize = 15.sp)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(18.dp))
                }

                Text(
                    "v1.0.0 · Institut Teknologi Sumatera",
                    style = MaterialTheme.typography.labelSmall,
                    color = RailLogColors.TextTertiary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}