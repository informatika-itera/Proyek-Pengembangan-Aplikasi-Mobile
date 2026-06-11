package com.example.foodsaver.presentation.screens.recipe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.foodsaver.domain.model.Recipe
import com.example.foodsaver.presentation.components.ErrorState
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(
    initialIngredients: List<String> = emptyList(),
    onRecipeClick: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: RecipeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val filters = remember { listOf("Pedas", "Murah", "Cepat Dibuat", "Sehat", "Dessert") }

    LaunchedEffect(initialIngredients) {
        if (initialIngredients.isNotEmpty()) {
            viewModel.findRecipesByIngredients(initialIngredients)
        }
    }

    Scaffold(
        modifier = Modifier.testTag("recipe_list_screen"),
        topBar = {
            RecipeListTopBar(onBackClick = onNavigateBack)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            RecipeSearchBar(
                query = state.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange
            )

            FilterChipRow(
                filters = filters,
                activeFilter = state.activeFilter,
                onFilterSelect = viewModel::onFilterSelect
            )

            if (state.selectedIngredients.isNotEmpty() && state.searchQuery.isEmpty()) {
                IngredientsUsageInfo(ingredients = state.selectedIngredients)
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    state.isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    state.error != null -> {
                        ErrorState(
                            message = "Waduh, resepnya gagal dimuat nih. Coba lagi ya!",
                            onRetry = { viewModel.findRecipesByIngredients(initialIngredients) }
                        )
                    }
                    state.filteredRecipes.isEmpty() -> {
                        EmptyRecipeState()
                    }
                    else -> {
                        RecipeListContent(
                            recipes = state.filteredRecipes,
                            onRecipeClick = onRecipeClick
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipeListTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text("Inspirasi Masak", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )
}

@Composable
private fun RecipeSearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("recipe_search_field"),
        placeholder = { Text("Lagi pengen masak apa hari ini?") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        shape = RoundedCornerShape(24.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        ),
        singleLine = true
    )
}

@Composable
private fun FilterChipRow(
    filters: List<String>,
    activeFilter: String?,
    onFilterSelect: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { filter ->
            FilterChip(
                selected = activeFilter == filter,
                onClick = { onFilterSelect(filter) },
                label = { Text(filter) },
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
private fun IngredientsUsageInfo(ingredients: List<String>) {
    Text(
        text = "Bisa dimasak pakai: ${ingredients.joinToString(", ")}",
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun RecipeListContent(
    recipes: List<Recipe>,
    onRecipeClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().testTag("recipe_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(recipes) { recipe ->
            RecipeListItem(
                recipe = recipe,
                onClick = { onRecipeClick(recipe.id) }
            )
        }
    }
}

@Composable
private fun RecipeListItem(recipe: Recipe, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("recipe_item_${recipe.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = recipe.imageUrl,
                contentDescription = recipe.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                recipe.category?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (recipe.matchPercentage > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "Cocok ${recipe.matchPercentage}% dengan stokmu",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyRecipeState() {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Resepnya belum ketemu nih.", fontWeight = FontWeight.Bold)
        Text(
            "Coba cari dengan kata kunci lain yuk!",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
