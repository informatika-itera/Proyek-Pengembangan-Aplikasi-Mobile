package com.example.travelplanner.presentation.screens.about

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import org.jetbrains.compose.resources.painterResource
import noteai.composeapp.generated.resources.Res
import noteai.composeapp.generated.resources.icon_travel_planner
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travelplanner.core.util.LocalStrings

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val s = LocalStrings.current
    val uriHandler = LocalUriHandler.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (s.isEnglish) "About App" else "Tentang Aplikasi",
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.3.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (s.isEnglish) "Back" else "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── APP LOGO HERO ──────────────────────────────────────────
            Surface(
                modifier = Modifier
                    .size(120.dp)
                    .shadow(8.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Image(
                    painter = painterResource(Res.drawable.icon_travel_planner),
                    contentDescription = "App Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "AI Travel Planner",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = "v1.1.0",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }

            // ── APP DESCRIPTION ────────────────────────────────────────
            Surface(
                modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (s.isEnglish) "App Overview" else "Deskripsi Aplikasi",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (s.isEnglish) {
                            "AI Travel Planner is a smart travel assistant designed to simplify your journey planning. Powered by Gemini AI, the app generates custom daily itineraries, maps flight & lodging search parameters, and tracks your travel budget in real-time with automated scanning."
                        } else {
                            "AI Travel Planner adalah asisten perjalanan cerdas yang dirancang untuk mempermudah perancangan liburan Anda. Didukung oleh teknologi Gemini AI, aplikasi ini dapat menyusun rencana aktivitas harian secara otomatis, memetakan detail pencarian tiket/hotel, serta melacak anggaran belanja perjalanan Anda dengan scan/ketik otomatis secara real-time."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp
                    )
                }
            }

            // ── KEY FEATURES ───────────────────────────────────────────
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = if (s.isEnglish) "Key Features" else "Fitur Utama",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 4.dp)
                )

                AboutFeatureRow(
                    icon = Icons.Default.AutoAwesome,
                    title = if (s.isEnglish) "AI Itinerary Generator" else "Perencana Itinerary AI",
                    subtitle = if (s.isEnglish) {
                        "Generate custom daily activities instantly based on your destination and chosen vibe."
                    } else {
                        "Susun rencana harian instan berdasarkan kota tujuan dan nuansa perjalanan pilihanmu."
                    }
                )

                AboutFeatureRow(
                    icon = Icons.Default.PieChart,
                    title = if (s.isEnglish) "Interactive Finance Charts" else "Ringkasan Keuangan Interaktif",
                    subtitle = if (s.isEnglish) {
                        "Track expenses and visualize spending categories with premium interactive Donut charts."
                    } else {
                        "Catat pengeluaran trip dan pantau kategori belanja dengan grafik Donut yang interaktif."
                    }
                )

                AboutFeatureRow(
                    icon = Icons.Default.CloudOff,
                    title = if (s.isEnglish) "Offline Local Storage" else "Penyimpanan Lokal Offline",
                    subtitle = if (s.isEnglish) {
                        "Access your saved itineraries and expenses securely, even without an internet connection."
                    } else {
                        "Akses kembali data itinerary dan catatan keuangan dengan aman meski sedang offline."
                    }
                )

                AboutFeatureRow(
                    icon = Icons.Default.Security,
                    title = if (s.isEnglish) "Bilingual Localization" else "Dukungan Dua Bahasa",
                    subtitle = if (s.isEnglish) {
                        "Switch dynamically between English and Indonesian configurations across the app."
                    } else {
                        "Navigasi aplikasi secara fleksibel dalam Bahasa Indonesia dan Bahasa Inggris."
                    }
                )
            }

            // ── TECH STACK ─────────────────────────────────────────────
            Surface(
                modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = if (s.isEnglish) "Technology Stack" else "Teknologi Aplikasi",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TechChip("Kotlin Multiplatform")
                        TechChip("Compose Multiplatform")
                        TechChip("SQLDelight DB")
                        TechChip("Gemini AI API")
                        TechChip("Koin DI")
                    }
                }
            }

            // ── FOOTER & COPYRIGHT ─────────────────────────────────────
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (s.isEnglish) "Developed by" else "Dikembangkan oleh",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
                
                Text(
                    text = "Taufik Hidayat & Romadhon Santoso",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    onClick = {
                        try {
                            uriHandler.openUri("mailto:taufik.hidayatnst02@gmail.com?subject=Travel%20Planner%20Feedback")
                        } catch (e: Exception) {
                            // Silence fallback
                        }
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "taufik.hidayatnst02@gmail.com",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
                
                Text(
                    text = "© 2026 AI Travel Planner.\nAll rights reserved.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun AboutFeatureRow(icon: ImageVector, title: String, subtitle: String) {
    Surface(
        modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun TechChip(text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable FlowRowScope.() -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement,
        content = content
    )
}
