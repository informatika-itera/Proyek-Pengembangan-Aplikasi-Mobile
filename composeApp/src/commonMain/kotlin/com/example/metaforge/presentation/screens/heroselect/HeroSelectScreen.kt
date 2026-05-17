package com.example.metaforge.presentation.screens.heroselect

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items as lazyRowItems
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.metaforge.domain.model.HeroRole
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeroSelectScreen(
    slotIndex: Int, isAlly: Boolean, isBan: Boolean = false,
    onNavigateBack: () -> Unit,
    viewModel: HeroSelectViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if(isBan) "Ban Hero" else "Select Hero Slot #${slotIndex + 1}") },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is HeroSelectUiState.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            is HeroSelectUiState.Error -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(text = state.message, color = MaterialTheme.colorScheme.error) }
            is HeroSelectUiState.Ready -> {
                Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                    LazyRow(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item { FilterChip(selected = state.selectedRole == null, onClick = { viewModel.filterByRole(null) }, label = { Text("All Lanes") }) }
                        lazyRowItems(HeroRole.entries) { role ->
                            FilterChip(selected = state.selectedRole == role, onClick = { viewModel.filterByRole(role) }, label = { Text(role.displayName) })
                        }
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3), modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.filteredHeroes) { hero ->
                            val isAlreadyPicked = state.pickedHeroNames.contains(hero.name)
                            Box(
                                modifier = Modifier.aspectRatio(1f).clip(RoundedCornerShape(12.dp))
                                    .background(if (isAlreadyPicked) Color.DarkGray.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface)
                                    .border(1.dp, if (isAlreadyPicked) Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                    .clickable(enabled = !isAlreadyPicked) {
                                        viewModel.pickHero(slotIndex, isAlly, isBan, hero)
                                        onNavigateBack()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).background(if (isAlreadyPicked) Color.Black else Color.Gray))
                                    Spacer(Modifier.height(4.dp))
                                    Text(hero.name, style = MaterialTheme.typography.bodySmall, color = if (isAlreadyPicked) Color.Gray else Color.White)
                                    if (isAlreadyPicked) Text("Picked", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}