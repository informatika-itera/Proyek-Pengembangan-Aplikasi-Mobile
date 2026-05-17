package com.example.metaforge.presentation.screens.heroselect

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeroListScreen(
    onNavigateToHeroInfo: (Int, String, String) -> Unit,
    viewModel: HeroSelectViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TIER LIST & ENCYCLOPEDIA", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is HeroSelectUiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
            is HeroSelectUiState.Ready -> {
                val groupedHeroes = state.allHeroes.groupBy {
                    when (it.name) {
                        "Fanny", "Mathilda", "Joy", "Nolan" -> "SS Tier (Ban Priority)"
                        "Chou", "Khufra", "Beatrix", "Novaria", "Valentina", "Ling" -> "S Tier (Pick Priority)"
                        else -> "A Tier (Situational)"
                    }
                }

                val tierOrder = listOf("SS Tier (Ban Priority)", "S Tier (Pick Priority)", "A Tier (Situational)")

                LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
                    tierOrder.forEach { tier ->
                        val heroesInTier = groupedHeroes[tier]
                        if (!heroesInTier.isNullOrEmpty()) {
                            item {
                                Text(tier, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 12.dp))
                            }
                            items(heroesInTier) { hero ->
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onNavigateToHeroInfo(hero.id, hero.name, hero.role.displayName) },
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).background(Color.DarkGray))
                                        Spacer(Modifier.width(16.dp))
                                        Column {
                                            Text(hero.name, fontWeight = FontWeight.Bold, color = Color.White)
                                            Text(hero.role.displayName, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else -> {}
        }
    }
}