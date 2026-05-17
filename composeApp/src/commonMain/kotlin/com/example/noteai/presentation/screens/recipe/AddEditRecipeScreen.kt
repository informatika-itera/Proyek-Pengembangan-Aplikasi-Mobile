package com.example.noteai.presentation.screens.recipe

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.noteai.domain.model.Recipe
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditRecipeScreen(
    recipeId: Long? = null,
    onNavigateBack: () -> Unit,
    viewModel: RecipeViewModel = koinViewModel()
) {
    val recipes by viewModel.recipes.collectAsState()
    val existingRecipe = remember(recipeId, recipes) {
        recipes.find { it.id == recipeId }
    }

    var title by remember { mutableStateOf(existingRecipe?.title ?: "") }
    var ingredients by remember { mutableStateOf(existingRecipe?.ingredients ?: "") }
    var instructions by remember { mutableStateOf(existingRecipe?.instructions ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (recipeId == null) "Tambah Resep" else "Edit Resep") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.saveRecipe(
                            Recipe(
                                id = recipeId ?: 0L,
                                title = title,
                                ingredients = ingredients,
                                instructions = instructions,
                                isFavorite = existingRecipe?.isFavorite ?: false,
                                isAiGenerated = existingRecipe?.isAiGenerated ?: false
                            )
                        )
                        onNavigateBack()
                    }) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Judul Resep") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = ingredients,
                onValueChange = { ingredients = it },
                label = { Text("Bahan-bahan") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            OutlinedTextField(
                value = instructions,
                onValueChange = { instructions = it },
                label = { Text("Instruksi Memasak") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 5
            )
        }
    }
}
