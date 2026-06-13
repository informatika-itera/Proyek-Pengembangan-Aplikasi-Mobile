package com.example.masakuy.presentation.screens.recommendation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.masakuy.domain.model.Recipe
import com.example.masakuy.theme.OrangeMain
import org.koin.compose.viewmodel.koinViewModel

private val dummyRecipes = listOf(
    Recipe(id="1", name="Nasi Telur Kecap",    image="", estimatedCost=11000, estimatedTime=10, difficulty="Mudah", isFavorite=false),
    Recipe(id="2", name="Mi Sayur Telur",       image="", estimatedCost=13500, estimatedTime=15, difficulty="Mudah", isFavorite=false),
    Recipe(id="3", name="Tahu Cabe Garam",      image="", estimatedCost=12000, estimatedTime=15, difficulty="Mudah", isFavorite=false),
    Recipe(id="4", name="Tempe Goreng Bumbu",   image="", estimatedCost=8000,  estimatedTime=10, difficulty="Mudah", isFavorite=false),
    Recipe(id="5", name="Sayur Bayam Bening",   image="", estimatedCost=9000,  estimatedTime=20, difficulty="Mudah", isFavorite=false),
)

private val recipeEmojis = listOf("🍳","🍜","🌶️","🍱","🥗","🍲","🫕","🥙")

private fun formatRp(amount: Int): String {
    val s = amount.toString().reversed()
    val chunks = s.chunked(3).joinToString(".").reversed()
    return "Rp$chunks"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationScreen(
    budget: Int,
    viewModel: RecommendationViewModel = koinViewModel(),
    onRecipeClick: (String, String) -> Unit,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) { viewModel.getRecommendations(budget) }

    val displayRecipes = if (uiState.recipes.isNotEmpty()) uiState.recipes else dummyRecipes
    val isShowingDummy = uiState.recipes.isEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Rekomendasi Budget", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Normal)
                        Text(formatRp(budget), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = OrangeMain)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = { Text("✨", fontSize = 20.sp, modifier = Modifier.padding(end = 16.dp)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                if (uiState.isLoading) {
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant).padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(color = OrangeMain, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(12.dp))
                        Text("Meminta rekomendasi dari AI...", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else if (!isShowingDummy) {
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1B3A2D)).padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("✨", fontSize = 16.sp)
                        Spacer(Modifier.width(10.dp))
                        Text("${displayRecipes.size} resep ditemukan sesuai budget kamu!", fontSize = 13.sp, color = Color(0xFF86EFAC))
                    }
                }
            }

            if (uiState.error != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("⚠️ ${uiState.error}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onErrorContainer)
                            Spacer(Modifier.height(10.dp))
                            Button(
                                onClick = { viewModel.getRecommendations(budget) },
                                colors = ButtonDefaults.buttonColors(containerColor = OrangeMain),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) { Text("Coba Lagi", color = Color.White, fontWeight = FontWeight.Bold) }
                        }
                    }
                }
            }

            if (isShowingDummy && !uiState.isLoading) {
                item {
                    Button(
                        onClick = { viewModel.getRecommendations(budget) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangeMain)
                    ) { Text("✨ Muat Rekomendasi AI", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White) }
                }
            }

            itemsIndexed(displayRecipes) { index, recipe ->
                RecipeRecommendationCard(
                    recipe = recipe,
                    index = index,
                    onClick = { onRecipeClick(recipe.id, recipe.name) }
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B3A2D))
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                        Text("💡", fontSize = 20.sp)
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("Tips Hemat", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF86EFAC))
                            Spacer(Modifier.height(4.dp))
                            Text("Sesuaikan porsi dan bahan dengan stok yang kamu punya di rumah!", fontSize = 12.sp, color = Color(0xFF86EFAC), lineHeight = 18.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecipeRecommendationCard(recipe: Recipe, index: Int, onClick: () -> Unit) {
    val emoji = recipeEmojis[index % recipeEmojis.size]
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(32.dp).clip(CircleShape).background(OrangeMain),
                contentAlignment = Alignment.Center
            ) {
                Text("${index + 1}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(Modifier.width(12.dp))
            Box(
                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 30.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(recipe.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(4.dp))
                Text("💰 ${formatRp(recipe.estimatedCost)}", fontSize = 12.sp, color = OrangeMain)
                Text("⏱ ${recipe.estimatedTime} menit", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { onClick() },
                    modifier = Modifier.height(32.dp),
                    shape = RoundedCornerShape(9.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, OrangeMain)
                ) { Text("Lihat Detail", fontSize = 12.sp, color = OrangeMain, fontWeight = FontWeight.Bold) }
            }
        }
    }
}