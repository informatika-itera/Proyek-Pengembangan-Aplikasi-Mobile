package com.example.noteai.presentation.screens.recipe

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.noteai.domain.model.Recipe
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeScreen(
    onRecipeClick: (Long) -> Unit,
    onAddRecipeClick: () -> Unit,
    viewModel: RecipeViewModel = koinViewModel()
) {
    val recipes by viewModel.recipes.collectAsState()
    val showFavoritesOnly by viewModel.showFavoritesOnly.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Buku Resep", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddRecipeClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Resep")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(selectedTabIndex = if (showFavoritesOnly) 1 else 0) {
                Tab(
                    selected = !showFavoritesOnly,
                    onClick = { viewModel.setShowFavoritesOnly(false) },
                    text = { Text("Semua Resep") }
                )
                Tab(
                    selected = showFavoritesOnly,
                    onClick = { viewModel.setShowFavoritesOnly(true) },
                    text = { Text("Favorit") }
                )
            }

            if (recipes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (showFavoritesOnly) "Belum ada resep favorit." else "Belum ada resep. Yuk buat resep baru!",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(recipes, key = { it.id }) { recipe ->
                        RecipeCard(
                            recipe = recipe,
                            onCardClick = { onRecipeClick(recipe.id) },
                            onFavoriteClick = { viewModel.toggleFavorite(recipe.id) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeCard(
    recipe: Recipe,
    onCardClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    ElevatedCard(
        onClick = onCardClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        imageVector = if (recipe.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorit",
                        tint = if (recipe.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = recipe.instructions,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (recipe.isAiGenerated) {
                Spacer(modifier = Modifier.height(12.dp))
                SuggestionChip(
                    onClick = {},
                    label = { 
                        Text("AI Generated", style = MaterialTheme.typography.labelSmall) 
                    },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    )
                )
            }
        }
    }
}
