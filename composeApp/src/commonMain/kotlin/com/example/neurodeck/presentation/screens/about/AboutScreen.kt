package com.example.neurodeck.presentation.screens.about

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.neurodeck.presentation.components.SectionTitle
import com.example.neurodeck.presentation.components.StickyNoteBadge
import com.example.neurodeck.presentation.navigation.AppTopBar
import neurodeck.composeapp.generated.resources.Res
import neurodeck.composeapp.generated.resources.neurodeck_logo
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Tentang NeuroDeck",
                canNavigateBack = true,
                onNavigationClick = onBack,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // ════════════════════════════════════════════════════════════════
            // BRAND HEADER — Logo dalam white circle + nama + version badge
            // ════════════════════════════════════════════════════════════════
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // White circle container — supaya logo dengan background putih
                // blend dengan dark mode tanpa edge artifact.
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(androidx.compose.ui.graphics.Color.White),
                    contentAlignment = Alignment.Center,
                ) {
                    // Logo image — PNG dari composeResources
                    Image(
                        painter = painterResource(Res.drawable.neurodeck_logo),
                        contentDescription = "NeuroDeck Logo",
                        modifier = Modifier.size(120.dp),
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "NeuroDeck",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Version badge (sticky note style)
                StickyNoteBadge(
                    text = APP_VERSION,
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary,
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = APP_TAGLINE,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }

            // ════════════════════════════════════════════════════════════════
            // TIM PENGEMBANG
            // ════════════════════════════════════════════════════════════════
            SectionTitle(text = "Tim Pengembang")
            OutlinedCard(
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(14.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    InfoRow(label = "Mata Kuliah", value = "IF25-22017 — Pengembangan Aplikasi Mobile")
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoRow(label = "Institusi", value = "Institut Teknologi Sumatera (ITERA)")
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoRow(
                        label = "Anggota Tim",
                        value = "• Muhammad Fajri Firdaus — NIM 123140050\n• Nadya Shafwa Yusuf — NIM 123140167",
                    )
                }
            }

            // ════════════════════════════════════════════════════════════════
            // TECH STACK
            // ════════════════════════════════════════════════════════════════
            SectionTitle(text = "Tech Stack")
            OutlinedCard(
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(14.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = TECH_STACK,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            // ════════════════════════════════════════════════════════════════
            // REPOSITORY (highlight card — tertiary container biar stand out)
            // ════════════════════════════════════════════════════════════════
            SectionTitle(text = "Repository")
            OutlinedCard(
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(14.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "GitHub Repository",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = GITHUB_REPOSITORY,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                    Text(
                        text = "GitHub Branch",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = GITHUB_BRANCH,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ════════════════════════════════════════════════════════════════
            // FOOTER
            // ════════════════════════════════════════════════════════════════
            Text(
                text = "Dibuat dengan ❤️ untuk pembelajaran adaptif",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Helper Composable: label-value pair row, label small caps + value normal.
 * Dipakai berulang di "Tim Pengembang" section.
 */
@Composable
private fun InfoRow(label: String, value: String) {
    Column {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

private const val APP_VERSION = "v0.2.0 · Sprint 2 Build"
private const val APP_TAGLINE =
    "Aplikasi flashcard cerdas dengan SM-2 spaced repetition + AI Tutor."
private const val GITHUB_BRANCH = "project/123140050-123140167-NeuroDeck"

private const val GITHUB_REPOSITORY = "https://github.com/fajrifirdaus/Proyek-Pengembangan-Aplikasi-Mobile.git"
private const val TECH_STACK =
    "• Kotlin Multiplatform 2.0.21\n" +
            "• Compose Multiplatform 1.7.0\n" +
            "• Clean Architecture + MVVM\n" +
            "• SQLDelight 2.0.2 (local storage)\n" +
            "• Ktor 3.0.1 (Gemini API)\n" +
            "• Koin 4.0.0 (DI)\n" +
            "• DataStore 1.1.1 (preferences)\n" +
            "• Algoritma SuperMemo 2 (Wozniak 1988)"