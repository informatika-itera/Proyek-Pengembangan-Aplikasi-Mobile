package id.pusakakata.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import id.pusakakata.ui.components.EmptyState
import id.pusakakata.ui.components.LoadingIndicator
import id.pusakakata.ui.components.ErrorMessage
import id.pusakakata.ui.components.ItemCard
import id.pusakakata.domain.model.Word

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onAddWord: () -> Unit,
    onWordClick: (String) -> Unit,
    onNavigateToGacha: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToQuiz: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val searchError by viewModel.searchError.collectAsState()
    val tokens by viewModel.tokens.collectAsState()
    
    var showAiResult by remember { mutableStateOf<Word?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pusaka Kata", style = MaterialTheme.typography.headlineMedium) },
                actions = {
                    Badge(containerColor = MaterialTheme.colorScheme.tertiary, modifier = Modifier.padding(end = 8.dp)) {
                        Text("$tokens 🪙", modifier = Modifier.padding(4.dp))
                    }
                    IconButton(onClick = onNavigateToSettings) { Icon(Icons.Default.Settings, contentDescription = "Pengaturan") }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddWord, // Kembali bisa Tambah Manual (Requirement Offline)
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                icon = { Icon(Icons.Default.Add, "Tambah") },
                text = { Text("Input Manual") }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Search & AI Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                placeholder = { Text("Cari lokal atau tanya AI...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (isSearching) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { 
                            viewModel.executeSearch { word -> showAiResult = word } 
                        }) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "Tanya AI", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            if (searchError != null) {
                Text(searchError!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(horizontal = 16.dp), style = MaterialTheme.typography.bodySmall)
            }

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (val state = uiState) {
                    is HomeUiState.Loading -> LoadingIndicator()
                    is HomeUiState.Empty -> EmptyState(message = "Pusaka masih kosong. Gunakan Input Manual atau Tanya AI di atas!")
                    is HomeUiState.Error -> ErrorMessage(message = state.message)
                    is HomeUiState.Success -> {
                        val words = state.words
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 80.dp, start = 16.dp, end = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(onClick = onNavigateToQuiz, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer), shape = RoundedCornerShape(12.dp)) {
                                        Icon(Icons.Default.Quiz, contentDescription = null); Spacer(Modifier.width(8.dp)); Text("Mulai Kuis")
                                    }
                                    Button(onClick = onNavigateToGacha, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                                        Icon(Icons.Default.Casino, contentDescription = null); Spacer(Modifier.width(8.dp)); Text("Gacha")
                                    }
                                }
                            }
                            items(words, key = { it.id }) { word ->
                                ItemCard(word = word, onClick = { onWordClick(word.id) }, onDelete = { viewModel.deleteWord(word.id) })
                            }
                        }
                    }
                }
            }
        }
        
        // Pop-up AI Summary (Requirement Baru)
        if (showAiResult != null) {
            AlertDialog(
                onDismissRequest = { showAiResult = null },
                title = { 
                    Column {
                        Text(
                            text = showAiResult!!.category,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text("Makna dari AI Pusaka ✨") 
                    }
                },
                text = {
                    Column {
                        Text(showAiResult!!.term, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(8.dp))
                        Text(showAiResult!!.definition, textAlign = TextAlign.Justify)
                        
                        if (showAiResult!!.example.isNotBlank()) {
                            Spacer(Modifier.height(16.dp))
                            Text("Contoh Pusaka:", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                            Text("\"${showAiResult!!.example}\"", style = MaterialTheme.typography.bodyMedium, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                        }

                        Spacer(Modifier.height(16.dp))
                        Text("Tersimpan otomatis di riwayat beranda.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAiResult = null }) { Text("Siap!") }
                }
            )
        }
    }
}
