package com.example.Roomie.presentation.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.example.Roomie.presentation.AppViewModel
import org.koin.compose.viewmodel.koinViewModel

data class OnboardingPage(
    val title: String,
    val description: String,
    val color: Color
)

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    viewModel: AppViewModel = koinViewModel()
) {
    val pages = listOf(
        OnboardingPage(
            "Cek Ruangan Cepat",
            "Pantau ketersediaan seluruh gedung di ITERA secara real-time dari genggaman Anda.",
            MaterialTheme.colorScheme.primary
        ),
        OnboardingPage(
            "Lapor Fasilitas Rusak",
            "Ada AC mati atau lampu putus? Lapor melalui Roomie dan pantau progres perbaikannya.",
            MaterialTheme.colorScheme.secondary
        ),
        OnboardingPage(
            "E-Permit Digital",
            "Pinjam ruangan jadi lebih mudah dengan surat izin digital yang siap ditunjukkan ke petugas.",
            MaterialTheme.colorScheme.tertiary
        )
    )

    var currentPage by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Simple Animated Content
        AnimatedContent(
            targetState = currentPage,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            }
        ) { pageIndex ->
            val page = pages[pageIndex]
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    modifier = Modifier.size(200.dp),
                    shape = CircleShape,
                    color = page.color.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "🏙️", // Placeholder for actual illustration
                            fontSize = 80.sp
                        )
                    }
                }
                Spacer(Modifier.height(40.dp))
                Text(
                    text = page.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = page.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            }
        }

        Spacer(Modifier.height(64.dp))

        // Indicators
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            pages.forEachIndexed { index, _ ->
                Box(
                    modifier = Modifier
                        .size(if (currentPage == index) 24.dp else 8.dp, 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (currentPage == index) MaterialTheme.colorScheme.primary 
                            else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                )
            }
        }

        Spacer(Modifier.height(48.dp))

        Button(
            onClick = {
                if (currentPage < pages.size - 1) {
                    currentPage++
                } else {
                    viewModel.completeOnboarding()
                    onFinished()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = if (currentPage == pages.size - 1) "MULAI SEKARANG" else "LANJUT",
                fontWeight = FontWeight.Bold
            )
            if (currentPage < pages.size - 1) {
                Spacer(Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
            }
        }
    }
}
