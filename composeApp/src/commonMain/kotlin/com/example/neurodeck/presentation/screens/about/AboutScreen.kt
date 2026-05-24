package com.example.neurodeck.presentation.screens.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.neurodeck.presentation.components.SectionTitle
import com.example.neurodeck.presentation.navigation.AppTopBar

/**
 * About screen — info aplikasi NeuroDeck, team, repository.
 *
 * Static content untuk Sprint 2. Tidak ada ViewModel — pure composable
 * dengan hardcoded data. Sprint 4+ bisa di-extend kalau perlu (changelog
 * dinamis, dll).
 *
 * @param onBack  Pop back stack.
 */
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // App branding header
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "🧠",
                    style = MaterialTheme.typography.displayLarge,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "NeuroDeck",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = APP_VERSION,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = APP_TAGLINE,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            SectionTitle(text = "Tim Pengembang")
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                shape = RoundedCornerShape(12.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Mata Kuliah",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "IF25-22017 — Pengembangan Aplikasi Mobile",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Institusi",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "Institut Teknologi Sumatera (ITERA)",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Anggota Tim",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "• Dev A — NIM 123140050\n• Dev B — NIM 123140167",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            SectionTitle(text = "Tech Stack")
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                shape = RoundedCornerShape(12.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = TECH_STACK,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            SectionTitle(text = "Repository")
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                ),
                shape = RoundedCornerShape(12.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "GitHub",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                    Text(
                        text = GITHUB_BRANCH,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "URL handler tersedia di Sprint 4 (platform-specific).",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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

private const val APP_VERSION = "Versi 0.2.0 (Sprint 2 Build)"
private const val APP_TAGLINE =
    "Aplikasi flashcard cerdas dengan SM-2 spaced repetition + AI Tutor."
private const val GITHUB_BRANCH =
    "project/123140050-123140167-NeuroDeck"
private const val TECH_STACK =
    "• Kotlin Multiplatform 2.0.21\n" +
            "• Compose Multiplatform 1.7.0\n" +
            "• Clean Architecture + MVVM\n" +
            "• SQLDelight 2.0.2 (local storage)\n" +
            "• Ktor 3.0.1 (Gemini API)\n" +
            "• Koin 4.0.0 (DI)\n" +
            "• DataStore 1.1.1 (preferences)\n" +
            "• Algoritma SuperMemo 2 (Wozniak 1988)"