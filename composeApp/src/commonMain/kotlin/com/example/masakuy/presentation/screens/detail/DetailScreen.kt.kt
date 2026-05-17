package com.example.masakuy.presentation.screens.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.masakuy.core.util.formatCurrency
import com.example.masakuy.theme.OrangeMain
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    recipeId: String,
    viewModel: DetailViewModel = koinViewModel(),
    onBackClick: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value

    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.recipe?.name ?: "Detail Resep") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite(recipeId) }) {
                        Icon(
                            imageVector = if (uiState.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (uiState.isFavorite) OrangeMain else androidx.compose.ui.graphics.Color.Gray
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Text("Error: ${uiState.error}", modifier = Modifier.padding(innerPadding))
        } else if (uiState.recipe != null) {
            val recipe = uiState.recipe!!

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    AsyncImage(
                        model = recipe.image,
                        contentDescription = recipe.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    Text(
                        text = recipe.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item {
                    Row {
                        Text("Estimasi Biaya: ", fontWeight = FontWeight.Bold)
                        Text(formatCurrency(recipe.estimatedCost), color = OrangeMain)
                    }
                    Row {
                        Text("Waktu: ", fontWeight = FontWeight.Bold)
                        Text("${recipe.estimatedTime} menit")
                    }
                    Row {
                        Text("Tingkat Kesulitan: ", fontWeight = FontWeight.Bold)
                        Text(recipe.difficulty)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    Text(
                        text = "Bahan-bahan",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(recipe.ingredients.size) { index ->
                    val ingredient = recipe.ingredients[index]
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text("• ${ingredient.name} ${ingredient.quantity}")
                        Text(
                            formatCurrency(ingredient.estimatedPrice),
                            fontSize = 12.sp,
                            color = androidx.compose.ui.graphics.Color.Gray
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Cara Membuat",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(recipe.instructions.size) { index ->
                    Text(
                        "${index + 1}. ${recipe.instructions[index]}",
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.toggleFavorite(recipeId) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OrangeMain
                        )
                    ) {
                        Text(
                            if (uiState.isFavorite) "Hapus dari Favorit" else "Simpan ke Favorit",
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}