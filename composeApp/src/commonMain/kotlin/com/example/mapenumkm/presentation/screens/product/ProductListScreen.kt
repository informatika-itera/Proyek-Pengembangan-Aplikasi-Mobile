package com.example.mapenumkm.presentation.screens.product

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.mapenumkm.domain.model.Note
import com.example.mapenumkm.domain.model.NoteCategory
import com.example.mapenumkm.domain.usecase.NoteSortBy
import com.example.mapenumkm.presentation.components.LoadingIndicator
import com.example.mapenumkm.presentation.screens.home.DashboardBottomNavigation
import com.example.mapenumkm.presentation.screens.home.HomeUiState
import com.example.mapenumkm.presentation.screens.home.HomeViewModel
import mapenumkm.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    onNavigateToAddProduct: () -> Unit,
    onNavigateToEditProduct: (Long) -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToReport: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    val categories = listOf("Semua") + NoteCategory.entries.map { it.displayName }
    var selectedCategoryIndex by remember { mutableStateOf(0) }
    
    var showSortSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Produk",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF16A34A)
                        )
                    )
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToAddProduct,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(40.dp)
                            .background(Color(0xFF16A34A), CircleShape)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Tambah", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            DashboardBottomNavigation(
                selectedItem = 1,
                onDashboardClick = onNavigateToDashboard,
                onProdukClick = {},
                onTransaksiClick = {},
                onRiwayatClick = onNavigateToHistory,
                onLaporanClick = onNavigateToReport
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            // Search Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { 
                        searchQuery = it
                        viewModel.onSearchQueryChange(it)
                    },
                    placeholder = { Text("Cari produk...", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                    trailingIcon = { 
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { 
                                searchQuery = ""
                                viewModel.clearSearch()
                            }) {
                                Icon(Icons.Default.Close, contentDescription = null)
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                        focusedBorderColor = Color(0xFF16A34A),
                        unfocusedContainerColor = Color(0xFFF9FAFB),
                        focusedContainerColor = Color(0xFFF9FAFB)
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .clickable { showSortSheet = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList, 
                        contentDescription = "Urutkan", 
                        tint = if (uiState is HomeUiState.Success && (uiState as HomeUiState.Success).sortBy != NoteSortBy.UPDATED_DESC) 
                            Color(0xFF16A34A) else Color.Gray
                    )
                }
            }

            // Categories Tab
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(categories.size) { index ->
                    val isSelected = selectedCategoryIndex == index
                    Column(
                        modifier = Modifier.clickable { 
                            selectedCategoryIndex = index
                            val category = if (index == 0) null else NoteCategory.entries[index - 1]
                            viewModel.onCategorySelected(category)
                        },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = categories[index],
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color(0xFF16A34A) else Color.Gray
                            )
                        )
                        if (isSelected) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(24.dp)
                                    .height(2.dp)
                                    .background(Color(0xFF16A34A))
                            )
                        }
                    }
                }
            }
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

            // Product List
            when (val state = uiState) {
                is HomeUiState.Loading -> LoadingIndicator()
                is HomeUiState.Success -> {
                    if (state.notes.isEmpty()) {
                        EmptyProductState(query = state.query)
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 20.dp)
                        ) {
                            items(state.notes) { product ->
                                ProductManageItem(
                                    product = product
                                ) { onNavigateToEditProduct(product.id) }
                            }
                        }
                    }
                }
                is HomeUiState.Empty -> {
                    EmptyProductState(query = state.query)
                }
                is HomeUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.message, color = Color.Red)
                    }
                }
            }
        }

        // Sort Bottom Sheet
        if (showSortSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSortSheet = false },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 40.dp)
                ) {
                    Text(
                        text = "Urutkan Berdasarkan",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                    )
                    
                    val currentSort = (uiState as? HomeUiState.Success)?.sortBy ?: NoteSortBy.UPDATED_DESC
                    
                    NoteSortBy.entries.forEach { sortBy ->
                        val isSelected = currentSort == sortBy
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.onSortByChanged(sortBy)
                                    showSortSheet = false
                                }
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = sortBy.displayName,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = if (isSelected) Color(0xFF16A34A) else Color.Black,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF16A34A))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyProductState(query: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.LightGray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (query.isEmpty()) "Belum ada produk" else "Produk tidak ditemukan",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = if (query.isEmpty()) "Mulai tambahkan produk jualan Anda" else "Coba cari dengan kata kunci lain",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@Composable
fun ProductManageItem(
    product: Note,
    onEditClick: () -> Unit
) {
    val imageRes = when {
        product.title.contains("nasi goreng", ignoreCase = true) -> Res.drawable.nasi_goreng
        product.title.contains("Es teler", ignoreCase = true) -> Res.drawable.Es_teler
        product.title.contains("Es teh", ignoreCase = true) -> Res.drawable.Es_teh
        else -> null
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF3F4F6)),
                contentAlignment = Alignment.Center
            ) {
                if (product.imageUri != null) {
                    AsyncImage(
                        model = product.imageUri,
                        contentDescription = product.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else if (imageRes != null) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(imageRes),
                        contentDescription = product.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = when(product.category) {
                            NoteCategory.DRINK -> Icons.Default.LocalCafe
                            NoteCategory.FOOD -> Icons.Default.Fastfood
                            else -> Icons.Default.Image
                        },
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = product.category.displayName,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF16A34A)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Rp ${product.price.toInt()}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray, modifier = Modifier.size(20.dp))
                }
                Text(
                    text = "Stok ${product.stock}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (product.stock <= 5) Color.Red else Color.Black,
                        fontWeight = if (product.stock <= 5) FontWeight.Bold else FontWeight.Normal
                    )
                )
            }
        }
        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
    }
}
