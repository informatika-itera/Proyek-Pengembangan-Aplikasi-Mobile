package id.pusakakata.presentation.screens.addedit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.pusakakata.presentation.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    viewModel: AddEditViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (state.wordId == null) "Tambah Pusaka Baru" else "Perbarui Pusaka",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {
            if (state.canSave) {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.saveWord() },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    icon = { Icon(Icons.Default.Check, "Simpan") },
                    text = { Text("Simpan Kata") }
                )
            }
        }
    ) { padding ->
        if (state.isLoading) {
            LoadingIndicator()
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                if (state.error != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Text(
                            state.error!!, 
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Identitas Kata",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = state.term,
                            onValueChange = { viewModel.onTermChange(it) },
                            label = { Text("Masukkan Kosakata") },
                            placeholder = { Text("Contoh: Sasmita...") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            leadingIcon = { Icon(Icons.Default.MenuBook, contentDescription = null) },
                            isError = state.term.isBlank() && state.term.isNotEmpty()
                        )
                        
                        IconButton(
                            onClick = { viewModel.searchOnline() },
                            modifier = Modifier.padding(top = 8.dp),
                            enabled = state.term.isNotBlank()
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "Cari Online")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text("Pilih Kategori", style = MaterialTheme.typography.labelLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Umum", "Sastra", "Arkais").forEach { cat ->
                            FilterChip(
                                selected = state.category == cat,
                                onClick = { viewModel.onCategoryChange(cat) },
                                label = { Text(cat) },
                                leadingIcon = if (state.category == cat) {
                                    { Icon(Icons.Default.Done, contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize)) }
                                } else null,
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Makna & Penjelasan",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )

                        TextButton(
                            onClick = { viewModel.searchOnline() },
                            enabled = state.term.isNotBlank() && !state.isLoading,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Tanya AI ✨", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                    
                    OutlinedTextField(
                        value = state.definition,
                        onValueChange = { viewModel.onDefinitionChange(it) },
                        label = { Text("Definisi Lengkap") },
                        placeholder = { Text("Jelaskan makna kata ini atau cari online...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { 
                            Icon(Icons.Default.HistoryEdu, contentDescription = null)
                        },
                        isError = state.definition.isBlank() && state.definition.isNotEmpty()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Contoh Penggunaan",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    
                    OutlinedTextField(
                        value = state.example,
                        onValueChange = { viewModel.onExampleChange(it) },
                        label = { Text("Contoh Kalimat") },
                        placeholder = { Text("AI akan membantu membuatkan contoh...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { 
                            Icon(Icons.Default.AutoStories, contentDescription = null)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
