package com.example.foodsaver.presentation.screens.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodsaver.domain.model.FoodItem
import com.example.foodsaver.domain.model.FoodStatus
import com.example.foodsaver.presentation.components.getEmojiForCategory
import com.example.foodsaver.presentation.theme.*
import org.koin.compose.viewmodel.koinViewModel

enum class RecipeInputMode {
    INVENTORY, MANUAL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CookFromStockScreen(
    onNavigateBack: () -> Unit,
    onNavigateToResult: (List<Long>, List<String>, Boolean, String) -> Unit,
    viewModel: CookFromStockViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedMode by remember { mutableStateOf(RecipeInputMode.INVENTORY) }
    var manualInput by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Masak dari Stok", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp) },
                navigationIcon = {
                    if (onNavigateBack != {}) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.resetIngredients() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Button(
                    onClick = {
                        if (state.selectedIngredientIds.isNotEmpty() || state.manualIngredients.isNotEmpty()) {
                            onNavigateToResult(
                                state.selectedIngredientIds.toList(),
                                state.manualIngredients,
                                state.prioritizeExpired,
                                state.preference
                            )
                        } else {
                            viewModel.addManualIngredient("") // Trigger "Pilih atau masukkan minimal satu bahan"
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.RestaurantMenu, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Buat Rekomendasi Resep", fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Mode Selector
            TabRow(
                selectedTabIndex = selectedMode.ordinal,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = {}
            ) {
                Tab(
                    selected = selectedMode == RecipeInputMode.INVENTORY,
                    onClick = { selectedMode = RecipeInputMode.INVENTORY },
                    text = { Text("Inventory", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedMode == RecipeInputMode.MANUAL,
                    onClick = { selectedMode = RecipeInputMode.MANUAL },
                    text = { Text("Input Manual", fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            if (selectedMode == RecipeInputMode.INVENTORY) "Pilih bahan dari inventory kamu" else "Masukkan bahan secara manual",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            "Pilih bahan atau masukkan sendiri untuk mendapatkan resep.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (selectedMode == RecipeInputMode.INVENTORY) {
                        if (state.ingredients.isEmpty()) {
                            item {
                                EmptyIngredientsState()
                            }
                        } else {
                            items(state.ingredients, key = { it.id }) { item ->
                                IngredientSelectableCard(
                                    item = item,
                                    isSelected = state.selectedIngredientIds.contains(item.id),
                                    onToggle = { viewModel.toggleIngredientSelection(item.id) }
                                )
                            }
                        }
                    } else {
                        item {
                            OutlinedTextField(
                                value = manualInput,
                                onValueChange = { manualInput = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Contoh: telur, nasi, bakso") },
                                label = { Text("Tambah Bahan") },
                                trailingIcon = {
                                    IconButton(onClick = {
                                        viewModel.addManualIngredient(manualInput)
                                        manualInput = ""
                                        focusManager.clearFocus()
                                    }) {
                                        Icon(Icons.Default.Add, contentDescription = "Tambah")
                                    }
                                },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = {
                                    viewModel.addManualIngredient(manualInput)
                                    manualInput = ""
                                    focusManager.clearFocus()
                                }),
                                shape = RoundedCornerShape(12.dp)
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            if (state.manualIngredients.isNotEmpty()) {
                                Text("Bahan Manual:", style = MaterialTheme.typography.labelLarge)
                                Spacer(modifier = Modifier.height(8.dp))
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    state.manualIngredients.forEach { ingredient ->
                                        InputChip(
                                            selected = false,
                                            onClick = {},
                                            label = { Text(ingredient) },
                                            trailingIcon = {
                                                Icon(
                                                    Icons.Default.Close,
                                                    contentDescription = "Hapus",
                                                    modifier = Modifier.size(16.dp).clickable {
                                                        viewModel.removeManualIngredient(ingredient)
                                                    }
                                                )
                                            },
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "Belum ada bahan manual.\nKetik bahan di atas dan tekan tombol +",
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            "Preferensi Masak",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Prioritaskan bahan hampir expired", style = MaterialTheme.typography.bodyMedium)
                                Text("Berikan resep menggunakan bahan paling urgent.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = state.prioritizeExpired,
                                onCheckedChange = { viewModel.setPrioritizeExpired(it) }
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text("Gaya Resep:", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val preferences = listOf("Cepat", "Praktis", "Sehat")
                            preferences.forEach { pref ->
                                FilterChip(
                                    selected = state.preference == pref,
                                    onClick = { viewModel.setPreference(pref) },
                                    label = { Text(pref) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(80.dp)) // Extra space for bottom bar
                    }
                }
            }
        }
    }
}

@Composable
fun IngredientSelectableCard(
    item: FoodItem,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    val status = item.getStatus()
    val statusColor = when (status) {
        FoodStatus.SAFE -> SafeTextLight
        FoodStatus.NEAR_EXPIRY -> WarningTextLight
        else -> ExpiredTextLight
    }

    // Formatting quantity: 12.0 -> 12, 12.5 -> 12.5
    val quantityText = if (item.quantity % 1.0 == 0.0) {
        item.quantity.toInt().toString()
    } else {
        item.quantity.toString()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) 
                             else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 0.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(statusColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(getEmojiForCategory(item.category), fontSize = 24.sp)
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "$quantityText ${item.unit}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        item.getStatusLabel(),
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
fun EmptyIngredientsState() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Belum ada bahan di inventory",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            "Tambahkan makanan terlebih dahulu atau gunakan input manual.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement
    ) {
        content()
    }
}
