package com.example.masakuy.presentation.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.masakuy.presentation.components.BudgetOption
import com.example.masakuy.presentation.components.BudgetSelector
import com.example.masakuy.presentation.components.RecipeCard
import com.example.masakuy.theme.OrangeMain
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onRecommendationClick: (Int) -> Unit,
    onRecipeClick: (String) -> Unit,
    onFavoriteClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Masakuy") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Hai, mau makan apa hari ini?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Budget kamu:",
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            BudgetSelector(
                options = listOf(
                    BudgetOption("Rp10K", 10000),
                    BudgetOption("Rp15K", 15000),
                    BudgetOption("Rp20K", 20000)
                ),
                selectedBudget = uiState.selectedBudget,
                onBudgetSelected = { budget ->
                    viewModel.selectBudget(budget)
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.selectedBudget != null) {
                Button(
                    onClick = { onRecommendationClick(uiState.selectedBudget!!) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrangeMain
                    )
                ) {
                    Text("Cari Rekomendasi", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Rekomendasi Populer",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(16.dp)
                )
            } else if (uiState.error != null) {
                Text("Error: ${uiState.error}")
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.recipes.size) { index ->
                        RecipeCard(
                            recipe = uiState.recipes[index],
                            onClick = { onRecipeClick(uiState.recipes[index].id) }
                        )
                    }
                }
            }
        }
    }
}