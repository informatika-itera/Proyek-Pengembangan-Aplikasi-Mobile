package com.example.foodsaver.presentation.screens.recipe

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.foodsaver.domain.model.RecipeRecommendation
import com.example.foodsaver.presentation.theme.*
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeRecommendationScreen(
    ingredientIds: List<Long>,
    manualIngredients: List<String>,
    prioritizeExpired: Boolean,
    preference: String,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: CookFromStockViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    var showConfirmDialog by remember { mutableStateOf(false) }

    val recommendation = when (val resState = state.recommendationState) {
        is RecommendationUiState.Success -> resState.recommendation
        is RecommendationUiState.Fallback -> resState.recommendation
        else -> null
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Gunakan Bahan?") },
            text = { Text("Apakah semua bahan dari inventaris yang digunakan dalam resep ini ingin ditandai sebagai sudah dikonsumsi?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.markIngredientsAsUsed(ingredientIds) {
                            showConfirmDialog = false
                            scope.launch {
                                snackbarHostState.showSnackbar("Bahan berhasil ditandai sebagai habis")
                            }
                            onNavigateToHome()
                        }
                    },
                    modifier = Modifier.testTag("btn_confirm_use_ingredients")
                ) {
                    Text("Ya, Sudah Digunakan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.testTag("recipe_recommendation_screen"),
        topBar = {
            TopAppBar(
                title = { Text("Rekomendasi Resep", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    recommendation?.let { recipe ->
                        IconButton(onClick = {
                            val textToCopy = buildString {
                                appendLine(recipe.title)
                                appendLine("\nBahan Utama:")
                                recipe.usedIngredients.forEach { appendLine("- $it") }
                                if (recipe.optionalIngredients.isNotEmpty()) {
                                    appendLine("\nBahan Tambahan:")
                                    recipe.optionalIngredients.forEach { appendLine("- $it") }
                                }
                                appendLine("\nLangkah Memasak:")
                                recipe.steps.forEachIndexed { i, s -> appendLine("${i + 1}. $s") }
                            }
                            clipboardManager.setText(AnnotatedString(textToCopy))
                            scope.launch { snackbarHostState.showSnackbar("Resep disalin ke papan klip") }
                        }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Salin")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            recommendation?.let {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Button(
                        onClick = { showConfirmDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(56.dp)
                            .testTag("btn_mark_used"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Tandai Bahan Sudah Digunakan", fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).testTag("recipe_result_card")) {
            when (val recState = state.recommendationState) {
                is RecommendationUiState.Loading, RecommendationUiState.Idle -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "FoodSaver sedang mencari resep yang cocok...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Mohon tunggu sebentar.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
                is RecommendationUiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(32.dp).testTag("empty_state"),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            recState.message,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.height(24.dp))
                        Button(onClick = onNavigateBack) {
                            Text("Kembali Pilih Bahan")
                        }
                    }
                }
                RecommendationUiState.Empty -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(32.dp).testTag("empty_state"),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.RestaurantMenu, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Wah, kami belum menemukan resep yang cocok.",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Coba ganti kombinasi bahan atau tambahkan bahan lain.",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(24.dp))
                        Button(onClick = onNavigateBack) {
                            Text("Kembali Pilih Bahan")
                        }
                    }
                }
                is RecommendationUiState.Success, is RecommendationUiState.Fallback -> {
                    val recipe = if (recState is RecommendationUiState.Success) recState.recommendation else (recState as RecommendationUiState.Fallback).recommendation
                    val isFallback = recState is RecommendationUiState.Fallback

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (isFallback) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "FoodSaver menampilkan rekomendasi lokal berdasarkan bahan yang kamu pilih.",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth().testTag("recipe_header_card"),
                            shape = RoundedCornerShape(28.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (recipe.imageUrl != null) {
                                    AsyncImage(
                                        model = recipe.imageUrl,
                                        contentDescription = recipe.title,
                                        modifier = Modifier
                                            .size(160.dp)
                                            .clip(RoundedCornerShape(24.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Surface(
                                        modifier = Modifier.size(80.dp),
                                        shape = RoundedCornerShape(20.dp),
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                Icons.Default.Restaurant,
                                                contentDescription = null,
                                                modifier = Modifier.size(40.dp),
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Text(
                                    text = recipe.title,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.testTag("txt_recipe_title")
                                )
                                
                                if (recipe.description.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = recipe.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Center
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    RecipeInfoBadge(Icons.Default.Timer, "${recipe.cookingTimeMinutes} m")
                                    RecipeInfoBadge(Icons.Default.BarChart, recipe.difficulty)
                                }
                            }
                        }

                        SectionTitle("Bahan Digunakan")
                        recipe.usedIngredients.forEach { ingredient ->
                            IngredientRow(ingredient, isUsed = true)
                        }

                        SectionTitle("Bahan Tambahan")
                        if (recipe.optionalIngredients.isNotEmpty()) {
                            recipe.optionalIngredients.forEach { ingredient ->
                                IngredientRow(ingredient, isUsed = false)
                            }
                        } else {
                            Text(
                                "Tidak ada bahan tambahan khusus.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        SectionTitle("Langkah Memasak")
                        recipe.steps.forEachIndexed { index, step ->
                            StepRow(index + 1, step)
                        }

                        recipe.warningMessage?.let { warning ->
                            SectionTitle("Catatan FoodSaver")
                            val isError = warning.contains("kedaluwarsa") && warning.contains("Peringatan")
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isError) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                                )
                            ) {
                                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        if (isError) Icons.Default.Warning else Icons.Default.Info,
                                        contentDescription = null,
                                        tint = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        warning,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeInfoBadge(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(6.dp))
        Text(text, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
fun IngredientRow(name: String, isUsed: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            if (isUsed) Icons.Default.CheckCircle else Icons.Default.AddCircleOutline,
            contentDescription = null,
            tint = if (isUsed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(12.dp))
        Text(name, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun StepRow(number: Int, text: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
        Surface(
            modifier = Modifier.size(28.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(number.toString(), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 22.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
