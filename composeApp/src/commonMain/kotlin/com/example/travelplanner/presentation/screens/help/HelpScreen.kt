package com.example.travelplanner.presentation.screens.help

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travelplanner.core.util.LocalStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val s = LocalStrings.current
    val uriHandler = LocalUriHandler.current

    val faqItems = remember(s.isEnglish) {
        if (s.isEnglish) {
            listOf(
                FaqItem(
                    category = "General",
                    question = "What is AI Travel Planner?",
                    answer = "AI Travel Planner is a smart travel assistant app that helps you design custom itineraries and track your travel expenses using artificial intelligence."
                ),
                FaqItem(
                    category = "General",
                    question = "Is this application free?",
                    answer = "Yes! All core features, including itinerary generation and expense tracking, are completely free to use."
                ),
                FaqItem(
                    category = "AI Planner",
                    question = "How do I create a new itinerary?",
                    answer = "Go to the Home tab, click \"Plan New Trip\", fill in your destination, dates, budget preset, and trip vibe, and click \"Generate Itinerary\". The Gemini AI will automatically plan your days in seconds."
                ),
                FaqItem(
                    category = "AI Planner",
                    question = "Why does the destination image show a default landscape?",
                    answer = "The app attempts to load real-time photos of your destination from Wikipedia or Unsplash. If the search fails or your internet connection is slow, it displays a high-quality default landscape background to keep the interface looking premium."
                ),
                FaqItem(
                    category = "Finance",
                    question = "How does the AI Expense Scan/Write feature work?",
                    answer = "On the trip expenses page, you can write free-form text (e.g., \"dinner pasta 120k transport 40k\") or paste receipt text. The AI parses the details and populates items, category splits, and costs automatically."
                ),
                FaqItem(
                    category = "Finance",
                    question = "How do I view the total expenses of completed trips?",
                    answer = "Go to the Profile tab and select \"Finance Summary\". You will see a list of all your trips, complete with interactive Donut Charts showing the budget proportions spent on Food, Transport, Lodging, etc."
                ),
                FaqItem(
                    category = "Troubleshooting",
                    question = "Why does the trip generation fail (Error)?",
                    answer = "Trip generation requires an active internet connection to communicate with the Gemini AI service. Ensure you are connected to the internet. If it continues to fail, you can click the \"Retry\" button."
                )
            )
        } else {
            listOf(
                FaqItem(
                    category = "Umum",
                    question = "Apa itu AI Travel Planner?",
                    answer = "AI Travel Planner adalah aplikasi asisten perjalanan cerdas yang membantu Anda merancang rencana perjalanan (itinerary) kustom dan melacak pengeluaran perjalanan Anda menggunakan kecerdasan buatan."
                ),
                FaqItem(
                    category = "Umum",
                    question = "Apakah aplikasi ini gratis?",
                    answer = "Ya! Semua fitur utama, termasuk pembuatan rencana perjalanan AI dan pencatatan pengeluaran, dapat digunakan gratis sepenuhnya."
                ),
                FaqItem(
                    category = "Perencana AI",
                    question = "Bagaimana cara membuat rencana perjalanan baru?",
                    answer = "Masuk ke tab Beranda, klik \"Rencanakan Perjalanan Baru\", isi kota tujuan, tanggal, pilihan anggaran (Budget), serta Vibe perjalanan Anda, lalu tekan \"Buat Perjalanan\". AI Gemini akan merancang aktivitas harian Anda dalam hitungan detik."
                ),
                FaqItem(
                    category = "Perencana AI",
                    question = "Mengapa gambar tujuan hanya menampilkan pemandangan default?",
                    answer = "Aplikasi mencoba mencari foto nyata kota tujuan dari Wikipedia atau Unsplash. Jika pencarian tidak membuahkan hasil atau koneksi internet lambat, aplikasi otomatis menampilkan gambar pemandangan default berkualitas tinggi agar antarmuka tetap estetik."
                ),
                FaqItem(
                    category = "Keuangan",
                    question = "Bagaimana cara kerja fitur Scan/Ketik AI di pencatat pengeluaran?",
                    answer = "Di halaman pengeluaran perjalanan, Anda dapat menulis teks bebas (contoh: \"makan malam pasta 120rb transportasi 40rb\") atau menempelkan teks struk belanjaan. AI akan otomatis mengurai nama pengeluaran, kategori, dan biayanya."
                ),
                FaqItem(
                    category = "Keuangan",
                    question = "Bagaimana cara melihat total pengeluaran per perjalanan?",
                    answer = "Masuk ke tab Profil dan pilih \"Ringkasan Keuangan\". Anda dapat melihat semua perjalanan yang telah selesai beserta grafik Donut Chart interaktif yang membagi pengeluaran per kategori."
                ),
                FaqItem(
                    category = "Masalah Umum",
                    question = "Mengapa pembuatan rencana perjalanan gagal (Error)?",
                    answer = "Pembuatan itinerary membutuhkan koneksi internet aktif untuk berkomunikasi dengan kecerdasan buatan Gemini. Pastikan koneksi internet Anda lancar. Jika gagal, silakan tekan tombol \"Coba Lagi\"."
                )
            )
        }
    }

    val categories = remember(s.isEnglish) {
        if (s.isEnglish) {
            listOf("All", "General", "AI Planner", "Finance", "Troubleshooting")
        } else {
            listOf("Semua", "Umum", "Perencana AI", "Keuangan", "Masalah Umum")
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(categories.first()) }
    var expandedIndex by remember { mutableStateOf(-1) }

    val filteredFaq = remember(searchQuery, selectedCategory, faqItems) {
        faqItems.filter { item ->
            val matchesCategory = selectedCategory == categories.first() || item.category.lowercase() == selectedCategory.lowercase()
            val matchesSearch = item.question.contains(searchQuery, ignoreCase = true) || item.answer.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (s.isEnglish) "Help & FAQ" else "Bantuan & FAQ",
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── SEARCH BAR ──────────────────────────────────────────────
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(if (s.isEnglish) "Search FAQ..." else "Cari bantuan...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            // ── CATEGORIES ──────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = selectedCategory == category
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .clickable {
                                selectedCategory = category
                                expandedIndex = -1 // Reset accordion on filter
                            },
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(50),
                        border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    ) {
                        Text(
                            text = category,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            // ── ACCORDION FAQ LIST ──────────────────────────────────────
            if (filteredFaq.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (s.isEnglish) "No results found." else "Tidak ada FAQ yang cocok.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                filteredFaq.forEachIndexed { index, item ->
                    val isExpanded = expandedIndex == index
                    val rotationState by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f)

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        onClick = { expandedIndex = if (isExpanded) -1 else index }
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.question,
                                    modifier = Modifier.weight(0.9f),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Icon(
                                    imageVector = Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .rotate(rotationState)
                                        .size(20.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            AnimatedVisibility(
                                visible = isExpanded,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Text(
                                    text = item.answer,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 18.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── CONTACT SUPPORT CARD ────────────────────────────────────
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(
                    modifier = Modifier.background(
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                            )
                        )
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (s.isEnglish) "Need More Help?" else "Butuh Bantuan Lain?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = if (s.isEnglish) {
                                "Contact the developer team directly for support, feedback, or custom feature requests."
                            } else {
                                "Hubungi tim developer secara langsung untuk bantuan, umpan balik, atau pengajuan fitur khusus."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )

                        Spacer(Modifier.height(4.dp))

                        Button(
                            onClick = {
                                try {
                                    uriHandler.openUri("mailto:taufik.hidayatnst02@gmail.com?subject=Travel%20Planner%20Support")
                                } catch (e: Exception) {
                                    // Fallback if no mail app configured
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = if (s.isEnglish) "Send Email" else "Kirim Email",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "taufik.hidayatnst02@gmail.com",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

data class FaqItem(
    val category: String,
    val question: String,
    val answer: String
)
