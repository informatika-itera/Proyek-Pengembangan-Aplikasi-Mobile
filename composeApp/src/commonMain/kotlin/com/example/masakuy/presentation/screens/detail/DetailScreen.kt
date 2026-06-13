package com.example.masakuy.presentation.screens.detail

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SoupKitchen
import androidx.compose.material.icons.filled.RamenDining
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.masakuy.theme.OrangeMain
import org.koin.compose.viewmodel.koinViewModel

private fun formatRp(amount: Int): String {
    val s = amount.toString().reversed()
    val chunks = s.chunked(3).joinToString(".").reversed()
    return "Rp$chunks"
}

// Pesan loading yang berganti-ganti agar tidak terasa lama
private val loadingMessages = listOf(
    "Menghubungi dapur AI... ??",
    "Mencari resep terbaik buat kamu...",
    "Menghitung bahan dan estimasi harga...",
    "Hampir selesai, sabar ya!"
)

private fun recipeIcon(name: String): androidx.compose.ui.graphics.vector.ImageVector {
    val n = name.lowercase()
    return when {
        n.contains("soto") || n.contains("sup") || n.contains("sayur") -> Icons.Default.SoupKitchen
        n.contains("mie") || n.contains("bakso") -> Icons.Default.RamenDining
        n.contains("roti") || n.contains("kue") -> Icons.Default.BakeryDining
        else -> Icons.Default.Restaurant
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    recipeId: String,
    recipeName: String,
    budget: Int,
    viewModel: DetailViewModel = koinViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId, recipeName, budget)
    }

    val recipe = uiState.recipe

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(recipe?.name ?: recipeName, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    recipe?.let {
                        IconButton(onClick = { viewModel.toggleFavorite(it.id, !it.isFavorite) }) {
                            Icon(
                                imageVector = if (it.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (it.isFavorite) OrangeMain else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                DetailLoadingState(modifier = Modifier.padding(innerPadding))
            }

            uiState.error != null -> {
                DetailErrorState(
                    errorMessage = uiState.error!!,
                    modifier = Modifier.padding(innerPadding),
                    onRetry = { viewModel.loadRecipe(recipeId, recipeName, budget) }
                )
            }

            recipe != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Hero banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(recipeIcon(recipe.name), contentDescription = null, modifier = Modifier.size(72.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(recipe.name, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                        Spacer(Modifier.height(16.dp))

                        // Chips info
                        Row {
                            Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(12.dp)) {
                                Text(
                                    "${formatRp(recipe.estimatedCost)}",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    color = OrangeMain, fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(12.dp)) {
                                Text(
                                    "${recipe.estimatedTime} menit",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(12.dp)) {
                                Text(
                                    "${recipe.difficulty}",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(Modifier.height(26.dp))

                        // Bahan-bahan
                        Text("Bahan-bahan", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(14.dp))
                        if (recipe.ingredients.isEmpty()) {
                            Text("Tidak ada data bahan.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                        } else {
                            recipe.ingredients.forEach { bahan ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        bahan.name, color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 14.sp, modifier = Modifier.weight(1f)
                                    )
                                    if (bahan.estimatedPrice > 0)
                                        Text(formatRp(bahan.estimatedPrice), color = OrangeMain, fontSize = 14.sp)
                                }
                            }
                        }

                        Spacer(Modifier.height(26.dp))

                        // Cara Membuat
                        Text("Cara Membuat", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(14.dp))
                        if (recipe.instructions.isEmpty()) {
                            Text("Tidak ada instruksi.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                        } else {
                            recipe.instructions.forEachIndexed { index, step ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(OrangeMain, RoundedCornerShape(14.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "${index + 1}",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                    Spacer(Modifier.width(10.dp))
                                    Text(
                                        step, color = MaterialTheme.colorScheme.onBackground,
                                        fontSize = 14.sp, modifier = Modifier.weight(1f)
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                            }
                        }

                        Spacer(Modifier.height(26.dp))

                        // Tombol Favorit
                        Button(
                            onClick = { viewModel.toggleFavorite(recipe.id, !recipe.isFavorite) },
                            modifier = Modifier.fillMaxWidth().height(58.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (recipe.isFavorite) Color(0xFFEF4444) else OrangeMain
                            )
                        ) {
                            Text(
                                text = if (recipe.isFavorite) "Hapus dari Favorit" else "Simpan ke Favorit",
                                color = Color.White, fontWeight = FontWeight.Bold,
                                fontSize = 17.sp, textAlign = TextAlign.Center
                            )
                        }
                        Spacer(Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Loading State — animasi pulsing + pesan berganti tiap 2.5 detik
// ---------------------------------------------------------------------------
@Composable
private fun DetailLoadingState(modifier: Modifier = Modifier) {
    var messageIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(2500)
            messageIndex = (messageIndex + 1) % loadingMessages.size
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 1f, label = "alpha",
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                color = OrangeMain,
                modifier = Modifier.size(52.dp),
                strokeWidth = 4.dp
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = loadingMessages[messageIndex],
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .alpha(alpha)
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Error State — pesan friendly + tombol coba lagi
// ---------------------------------------------------------------------------
@Composable
private fun DetailErrorState(
    errorMessage: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Tentukan pesan & emoji berdasarkan jenis error
    val isRateLimit = errorMessage.contains("429") ||
            errorMessage.contains("quota") ||
            errorMessage.contains("rate") ||
            errorMessage.contains("banyak")

    val isNetwork = errorMessage.contains("terhubung") ||
            errorMessage.contains("network") ||
            errorMessage.contains("timeout") ||
            errorMessage.contains("SocketTimeout")

    val (emojiIcon, title, subtitle) = when {
        isRateLimit -> Triple(
            Icons.Default.HourglassEmpty,
            "AI-nya lagi sibuk!",
            "Terlalu banyak permintaan sekarang. Tunggu sebentar lalu coba lagi."
        )
        isNetwork -> Triple(
            Icons.Default.WifiOff,
            "Koneksi bermasalah",
            "Periksa koneksi internet kamu, lalu coba lagi."
        )
        else -> Triple(
            Icons.Default.ErrorOutline,
            "Gagal memuat resep",
            "Terjadi kesalahan saat mengambil detail resep. Coba lagi ya!"
        )
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Icon(emojiIcon, contentDescription = null, modifier = Modifier.size(56.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))
            Text(
                title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(8.dp))
            Text(
                subtitle,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )
            Spacer(Modifier.height(28.dp))
            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangeMain),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("Coba Lagi", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}





















