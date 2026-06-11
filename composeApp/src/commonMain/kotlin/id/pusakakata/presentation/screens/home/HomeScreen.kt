package id.pusakakata.presentation.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.pusakakata.presentation.components.EmptyState
import id.pusakakata.presentation.components.LoadingIndicator
import id.pusakakata.presentation.components.ErrorMessage
import id.pusakakata.presentation.components.ItemCard
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
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
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
                        Icon(Icons.Outlined.Settings, "Pengaturan", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                ),
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddWord,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(20.dp),
                icon = { Icon(Icons.Default.Add, "Tambah") },
                text = { Text("Tambah Kata", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
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
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Cari kata unik Nusantara...", style = MaterialTheme.typography.bodyMedium) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.primary) },
            trailingIcon = {
                if (isSearching) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else if (query.isNotEmpty()) {
                    IconButton(
                        onClick = onAiSearch,
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Icon(Icons.Default.AutoAwesome, "Tanya AI", tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(18.dp))
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            singleLine = true
        )
    }
}

@Composable
fun TokenBadge(tokens: Long) {
    Surface(
        modifier = Modifier
            .padding(end = 8.dp)
            .clip(RoundedCornerShape(16.dp)),
        color = MaterialTheme.colorScheme.secondaryContainer,
        onClick = {}
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$tokens", 
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(Modifier.width(6.dp))
            Text("🪙", fontSize = 16.sp)
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
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ActionBanner(onQuiz, onGacha)
        }
        
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Koleksi Pusaka",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "${words.size} Kata",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
        
        items(words, key = { it.id }) { word ->
            ItemCard(
                word = word,
                onClick = { onWordClick(word.id) },
                onDelete = { onDelete(word.id) },
                onToggleFavorite = { onToggleFavorite(word.id) }
            )
        }
        
        item { Spacer(modifier = Modifier.height(100.dp)) }
    }
}

@Composable
fun ActionBanner(onQuiz: () -> Unit, onGacha: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BannerButton(
            title = "Uji Pengetahuan",
            subtitle = "Asah ingatan & dapatkan token koin",
            icon = Icons.Default.Quiz,
            gradient = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)),
            onClick = onQuiz
        )
        BannerButton(
            title = "Galeri Mitologi",
            subtitle = "Panggil sosok legenda Nusantara",
            icon = Icons.Default.Casino,
            gradient = listOf(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)),
            onClick = onGacha
        )
    }
}

@Composable
fun BannerButton(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    gradient: List<Color>,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .shadow(8.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() },
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(Brush.linearGradient(gradient))
                .fillMaxSize()
        ) {
            Icon(
                icon, null,
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 25.dp, y = 25.dp),
                tint = Color.White.copy(alpha = 0.15f)
            )
            
            Row(
                modifier = Modifier.padding(24.dp).fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, null, tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                }
                
                Spacer(Modifier.width(20.dp))
                
                Column {
                    Text(title, color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                    Text(subtitle, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
                
                Spacer(Modifier.weight(1f))
                
                Icon(Icons.Default.ChevronRight, null, tint = Color.White.copy(alpha = 0.7f))
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
        icon = { Icon(Icons.Default.AutoAwesome, null, tint = MaterialTheme.colorScheme.secondary) },
        title = {
            Text("Analisis Pusaka AI", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
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
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    Text(
                        word.category.uppercase(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                
                Text(
                    word.definition,
                    style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 26.sp),
                    textAlign = TextAlign.Justify
                )
                
                if (word.example.isNotEmpty()) {
                    Spacer(Modifier.height(24.dp))
                    Text(
                        "Contoh Penggunaan:",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(top = 8.dp).fillMaxWidth()
                    ) {
                        Text(
                            "\"${word.example}\"",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            ),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 24.dp)
            ) {
                Text("Tutup", fontWeight = FontWeight.Bold)
            }
        }
    )
}
