package id.pusakakata.presentation.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.pusakakata.core.util.rememberShareManager
import id.pusakakata.presentation.components.LoadingIndicator
import id.pusakakata.presentation.components.ErrorMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    onDelete: (String) -> Unit,
    onToggleFavorite: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val shareManager = rememberShareManager()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    if (uiState is DetailUiState.Success) {
                        val word = (uiState as DetailUiState.Success).word
                        IconButton(onClick = { onToggleFavorite(word.id) }) {
                            Icon(
                                imageVector = if (word.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorit",
                                tint = if (word.isFavorite) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { onEdit(word.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { onDelete(word.id); onBack() }) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is DetailUiState.Loading -> LoadingIndicator()
                is DetailUiState.Error -> ErrorMessage(message = state.message)
                is DetailUiState.Success -> {
                    val word = state.word
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = word.category.uppercase(),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = word.term,
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Black
                        )
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Text(
                            text = "Definisi",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = word.definition,
                            style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 28.sp),
                            textAlign = TextAlign.Justify
                        )
                        
                        if (word.example.isNotBlank()) {
                            Spacer(modifier = Modifier.height(40.dp))
                            
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Column(modifier = Modifier.padding(24.dp)) {
                                    Text(
                                        text = "Contoh Kalimat",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(Modifier.height(12.dp))
                                    Text(
                                        text = "\"${word.example}\"",
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontStyle = FontStyle.Italic,
                                            lineHeight = 24.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(48.dp))
                        
                        // Action area
                        Button(
                            onClick = { 
                                val textToShare = "Pusaka Kata: ${word.term}\n\n" +
                                        "Definisi: ${word.definition}\n\n" +
                                        "Kategori: ${word.category}\n" +
                                        (if (word.example.isNotBlank()) "\nContoh: \"${word.example}\"" else "")
                                shareManager.shareText(textToShare)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Icon(Icons.Default.Share, null)
                            Spacer(Modifier.width(12.dp))
                            Text("Bagikan Kosakata", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
