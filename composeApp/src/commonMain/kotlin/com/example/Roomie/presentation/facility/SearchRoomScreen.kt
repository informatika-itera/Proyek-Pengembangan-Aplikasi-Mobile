package com.example.Roomie.presentation.facility

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.Roomie.domain.model.Room
import com.example.Roomie.domain.model.RoomStatus
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchRoomScreen(
    onBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    viewModel: SearchRoomViewModel = koinViewModel()
) {
    val query by viewModel.searchQuery.collectAsState()
    val results by viewModel.searchResults.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        value = query,
                        onValueChange = viewModel::onQueryChange,
                        placeholder = { Text("Ketik nomor ruangan (misal: 101)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                            unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                            focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                            unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                        ),
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (query.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onQueryChange("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Hapus")
                                }
                            }
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        if (results.isEmpty() && query.isNotEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Ruangan tidak ditemukan")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(results) { room ->
                    SearchResultItem(
                        room = room,
                        onClick = { onNavigateToDetail(room.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchResultItem(room: Room, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        ListItem(
            headlineContent = { Text("Ruangan ${room.name}") },
            supportingContent = { Text("Lantai ${room.floor} - GKU 2") },
            trailingContent = {
                val color = when(room.status) {
                    RoomStatus.AVAILABLE -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
                    RoomStatus.BOOKED -> androidx.compose.ui.graphics.Color(0xFFF44336)
                    RoomStatus.MAINTENANCE -> androidx.compose.ui.graphics.Color(0xFFFFEB3B)
                }
                Surface(
                    color = color.copy(alpha = 0.2f),
                    shape = MaterialTheme.shapes.extraSmall
                ) {
                    Text(
                        text = room.status.name,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = color
                    )
                }
            }
        )
    }
}
