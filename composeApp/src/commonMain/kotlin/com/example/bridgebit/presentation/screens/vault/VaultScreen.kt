package com.example.bridgebit.presentation.screens.vault

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bridgebit.presentation.components.TranslationCard
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun VaultScreen(
    onNavigateToDetail: (Long) -> Unit,
    viewModel: VaultViewModel = koinViewModel()
) {
    val groupedPhrases by viewModel.groupedVaultPhrases.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Phrase Vault Categories") }) }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)
        ) {
            if (groupedPhrases.isEmpty()) {
                com.example.bridgebit.presentation.components.AnimatedEmptyState(
                    title = "Vault Kosong",
                    subtitle = "Belum ada frasa yang disimpan ke Vault. Simpan frasa favoritmu untuk dilihat kembali!",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {

                    groupedPhrases.forEach { (category, phrases) ->
                        stickyHeader {
                            Surface(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                color = MaterialTheme.colorScheme.background
                            ) {
                                Text(
                                    text = category.uppercase(),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }

                        items(items = phrases, key = { it.id }) { item ->
                            TranslationCard(
                                translation = item,
                                onClick = { onNavigateToDetail(item.id) },
                                onVaultClick = { viewModel.unvaultTranslation(item.id) },
                                onDeleteClick = { viewModel.deleteTranslation(item.id) },
                                modifier = Modifier.padding(bottom = 8.dp).animateItem()
                            )
                        }
                    }
                }
            }
        }
    }
}