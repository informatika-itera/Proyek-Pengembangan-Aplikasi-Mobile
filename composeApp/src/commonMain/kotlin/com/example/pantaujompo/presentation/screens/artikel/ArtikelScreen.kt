package com.example.pantaujompo.presentation.screens.artikel

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import org.koin.compose.viewmodel.koinViewModel
import com.example.pantaujompo.presentation.theme.*
import com.example.pantaujompo.data.remote.api.NewsArticleDto

@Composable
fun ArtikelScreen(
    onNavigateBack: () -> Unit = {},
    onArticleDetailToggled: (Boolean) -> Unit = {}, // callback to show/hide bottom bar
    viewModel: ArtikelViewModel = koinViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val daftarArtikel by viewModel.artikelList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val userPreferences: com.example.pantaujompo.data.local.datastore.UserPreferences = org.koin.compose.koinInject()
    val language by userPreferences.language.collectAsState(initial = "id")
    fun str(key: String) = com.example.pantaujompo.core.util.AppStrings.get(key, language)

    // State untuk menyimpan artikel yang lagi dibaca bray
    var artikelDipilih by remember { mutableStateOf<NewsArticleDto?>(null) }

    // 🔥 JIKA USER LAGI NYEKREK / KLIK ARTIKEL, TAMPILIN LAYAR BACA PREMIUM 🔥
    if (artikelDipilih != null) {
        LaunchedEffect(Unit) { onArticleDetailToggled(true) }
        LayarBacaDetail(artikel = artikelDipilih!!) {
            artikelDipilih = null // Pas diklik back, balik ke list awal bray
            onArticleDetailToggled(false)
        }
    } else {
        LaunchedEffect(Unit) { onArticleDetailToggled(false) }

        // TAMPILAN UTAMA LIST LIST ARTIKEL
        MeshBackground(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(48.dp))

                // ==================== PREMIUM HEADER ====================
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val isDark = MaterialTheme.colorScheme.background == DarkBackground
                    val textPrimary = MaterialTheme.colorScheme.onBackground
                    
                    Text(
                        text = str("artikel"), // Assuming "artikel" is "Literasi Kesehatan" or "Artikel"
                        color = textPrimary,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                // ==================== SEARCH BAR INSTANT SYNCHRONIZED ====================
                val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
                val textPrimaryColor = MaterialTheme.colorScheme.onBackground
                val accentColor = MaterialTheme.colorScheme.primary

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onQueryChanged(it) },
                    placeholder = { Text(if (language == "en") "Search health topics..." else "Cari topik kesehatan...", color = textSecondary, fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = accentColor) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onQueryChanged("") }) { 
                                Icon(Icons.Default.Close, contentDescription = null, tint = textSecondary)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedTextColor = textPrimaryColor,
                        unfocusedTextColor = textPrimaryColor,
                        cursorColor = accentColor
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ==================== DAFTAR ARTIKEL ====================
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = accentColor)
                    }
                } else if (daftarArtikel.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                        Text(if (language == "en") "No articles found." else "Artikel tidak ditemukan.", color = textSecondary)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        items(daftarArtikel) { artikel ->
                            ArtikelModernCard(artikel, language) {
                                artikelDipilih = artikel // Deteksi klik kartu buat ngebaca bray!
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== KOMPONEN KARTU ARTIKEL BERGAYA MAJALAH ====================
@Composable
fun ArtikelModernCard(artikel: NewsArticleDto, language: String, onCardClick: () -> Unit) {
    val tanggalFormat = artikel.publishedAt?.split("T")?.get(0) ?: (if(language == "en") "Latest" else "Terbaru")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .glassCard(shape = RoundedCornerShape(24.dp))
            .clickable { onCardClick() }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                AsyncImage(
                    model = artikel.urlToImage,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color(0xFF151515)), startY = 150f)))
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(text = artikel.title ?: "", color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 2, lineHeight = 22.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(artikel.source.name ?: "News", color = Color.Gray, fontSize = 12.sp)
                    Text(" • ", color = Color.Gray, fontSize = 12.sp)
                    Text(tanggalFormat, color = Color.Gray, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onCardClick() }, 
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if(language == "en") "Read Article" else "Baca Artikel", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

// ==================== LAYAR BACA PREMIUM DETAIL ====================
@Composable
fun LayarBacaDetail(artikel: NewsArticleDto, onBackClick: () -> Unit) {
    androidx.activity.compose.BackHandler {
        onBackClick()
    }

    val userPreferences: com.example.pantaujompo.data.local.datastore.UserPreferences = org.koin.compose.koinInject()
    val language by userPreferences.language.collectAsState(initial = "id")

    val scrollState = rememberScrollState()
    val tanggalFormat = artikel.publishedAt?.split("T")?.get(0) ?: (if(language=="en") "Latest" else "Terbaru")

    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
    val isDarkDetail = MaterialTheme.colorScheme.background == DarkBackground
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Scrollable Content
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState)
        ) {
            // Header Image
            Box(modifier = Modifier.fillMaxWidth().height(440.dp)) {
                AsyncImage(
                    model = artikel.urlToImage,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Gradient fade at the bottom of the image for smooth transition
                Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color(0x66000000)), startY = 200f)))
            }

            // Overlapping Card Content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-40).dp)
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 24.dp, vertical = 32.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Label sumber artikel dengan warna tema
                        Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.primary.copy(0.15f)).padding(horizontal = 10.dp, vertical = 6.dp)) {
                            Text(artikel.source.name?.uppercase() ?: "NEWS", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(tanggalFormat, color = textSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Text(text = artikel.title ?: "", color = textPrimary, fontSize = 26.sp, fontWeight = FontWeight.Black, lineHeight = 34.sp)

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(24.dp))

                    // Snippet Preview Berita
                    Text(
                        text = artikel.description ?: "",
                        color = textSecondary,
                        fontSize = 16.sp,
                        lineHeight = 26.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val kontenBersih = artikel.content?.substringBefore("[+") ?: ""
                    Text(
                        text = kontenBersih,
                        color = textPrimary.copy(alpha = 0.8f),
                        fontSize = 15.sp,
                        lineHeight = 26.sp
                    )

                    Spacer(modifier = Modifier.height(140.dp)) // Extra space so content isn't hidden behind the sticky button
                }
            }
        }

        // Sticky Top Back Button
        Box(
            modifier = Modifier
                .padding(top = 48.dp, start = 20.dp)
                .size(44.dp)
                .glassCard(shape = CircleShape)
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = if (isDarkDetail) Color.White else Color.Black
            )
        }

        // Sticky Bottom Read More Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color.Transparent, MaterialTheme.colorScheme.background.copy(alpha = 0.8f), MaterialTheme.colorScheme.background)))
                .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 48.dp) // Added 48.dp bottom padding for Navbar
        ) {
            Button(
                onClick = {
                    artikel.url?.let { uriHandler.openUri(it) }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = if(language == "en") "READ FULL ARTICLE ON WEBSITE" else "BACA SELENGKAPNYA DI WEBSITE",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}