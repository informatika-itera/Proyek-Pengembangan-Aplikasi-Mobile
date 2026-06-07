package id.pusakakata.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            LargeTopAppBar(
                title = { 
                    Column {
                        Text(
                            "Pusaka Kata", 
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            "Eksplorasi Kosakata Nusantara",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                },
                actions = {
                    TokenBadge(tokens)
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, "Pengaturan", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddWord,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(Icons.Default.Add, "Tambah")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Enhanced Search & AI Bar
            SearchSection(
                query = searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                isSearching = isSearching,
                onAiSearch = { viewModel.executeSearch { word -> showAiResult = word } }
            )

            AnimatedVisibility(visible = searchError != null) {
                Text(
                    searchError ?: "", 
                    color = MaterialTheme.colorScheme.error, 
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (val state = uiState) {
                    is HomeUiState.Loading -> LoadingIndicator()
                    is HomeUiState.Empty -> EmptyState(message = "Mulailah dengan mencari kata baru!")
                    is HomeUiState.Error -> ErrorMessage(message = state.message)
                    is HomeUiState.Success -> {
                        WordList(
                            words = state.words,
                            onWordClick = onWordClick,
                            onDelete = viewModel::deleteWord,
                            onToggleFavorite = viewModel::toggleFavorite,
                            onQuiz = onNavigateToQuiz,
                            onGacha = onNavigateToGacha
                        )
                    }
                }
            }
        }
        
        if (showAiResult != null) {
            AiResultDialog(
                word = showAiResult!!,
                onDismiss = { showAiResult = null }
            )
        }
    }
}

@Composable
fun SearchSection(
    query: String,
    onQueryChange: (String) -> Unit,
    isSearching: Boolean,
    onAiSearch: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Cari kata atau tanya AI...") },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.primary) },
            trailingIcon = {
                if (isSearching) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else if (query.isNotEmpty()) {
                    IconButton(onClick = onAiSearch) {
                        Icon(Icons.Default.AutoAwesome, "Tanya AI", tint = MaterialTheme.colorScheme.secondary)
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )
    }
}

@Composable
fun TokenBadge(tokens: Long) {
    Surface(
        modifier = Modifier
            .padding(end = 16.dp)
            .clip(RoundedCornerShape(12.dp)),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$tokens", 
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(Modifier.width(4.dp))
            Text("🪙", fontSize = 14.sp)
        }
    }
}

@Composable
fun WordList(
    words: List<Word>,
    onWordClick: (String) -> Unit,
    onDelete: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onQuiz: () -> Unit,
    onGacha: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ActionBanner(onQuiz, onGacha)
        }
        
        item {
            Text(
                "Koleksi Anda",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        items(words, key = { it.id }) { word ->
            ItemCard(
                word = word,
                onClick = { onWordClick(word.id) },
                onDelete = { onDelete(word.id) },
                onToggleFavorite = { onToggleFavorite(word.id) }
            )
        }
        
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun ActionBanner(onQuiz: () -> Unit, onGacha: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BannerButton(
            title = "Mulai Kuis",
            subtitle = "Dapatkan Token",
            icon = Icons.Default.Quiz,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f),
            onClick = onQuiz
        )
        BannerButton(
            title = "Pusaka Gacha",
            subtitle = "Koleksi Kartu",
            icon = Icons.Default.Casino,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.weight(1f),
            onClick = onGacha
        )
    }
}

@Composable
fun BannerButton(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(20.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Subtle Background decoration
            Icon(
                icon, null,
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 10.dp, y = 10.dp),
                tint = Color.White.copy(alpha = 0.15f)
            )
            
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterStart)
            ) {
                Icon(icon, null, tint = Color.White, modifier = Modifier.size(24.dp))
                Spacer(Modifier.height(8.dp))
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(subtitle, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun AiResultDialog(word: Word, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(32.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, null, tint = MaterialTheme.colorScheme.secondary)
                Spacer(Modifier.width(12.dp))
                Text("Analisis AI", style = MaterialTheme.typography.headlineSmall)
            }
        },
        text = {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Text(
                    word.term,
                    style = MaterialTheme.typography.displaySmall.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Black
                    )
                )
                
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    Text(
                        word.category.uppercase(),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                
                Text(
                    word.definition,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Justify
                )
                
                if (word.example.isNotEmpty()) {
                    Spacer(Modifier.height(20.dp))
                    Text(
                        "Contoh Penggunaan:",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        "\"${word.example}\"",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup", fontWeight = FontWeight.Bold)
            }
        }
    )
}
