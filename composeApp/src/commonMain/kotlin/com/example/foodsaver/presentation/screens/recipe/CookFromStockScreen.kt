package com.example.foodsaver.presentation.screens.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodsaver.domain.model.FoodItem
import com.example.foodsaver.domain.model.FoodStatus
import com.example.foodsaver.presentation.components.getEmojiForCategory
import com.example.foodsaver.presentation.theme.*
import com.example.foodsaver.core.util.formatQuantity
import org.koin.compose.viewmodel.koinViewModel

enum class RecipeInputMode {
    INVENTORY, MANUAL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CookFromStockScreen(
    onNavigateBack: () -> Unit,
    onNavigateToResult: (List<Long>, List<String>, Boolean, String) -> Unit,
    onAddFoodClick: () -> Unit = {},
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
        modifier = Modifier.testTag("recipe_screen"),
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Masak dari Stok", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                        Text("Buat resep dari bahan yang tersedia.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
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
                            viewModel.addManualIngredient("") 
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp)
                        .testTag("recipe_generate_button"),
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
            TabRow(
                selectedTabIndex = selectedMode.ordinal,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = { HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)) }
            ) {
                Tab(
                    selected = selectedMode == RecipeInputMode.INVENTORY,
                    onClick = { selectedMode = RecipeInputMode.INVENTORY },
                    text = { Text("Inventaris", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("recipe_inventory_tab")
                )
                Tab(
                    selected = selectedMode == RecipeInputMode.MANUAL,
                    onClick = { selectedMode = RecipeInputMode.MANUAL },
                    text = { Text("Input Manual", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("recipe_manual_tab")
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
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Punya bahan seadanya?",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    "Masukkan bahan seperti telur, nasi, bakso, atau sayur. FoodSaver akan bantu kasih ide masakan.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    item {
                        Text(
                            if (selectedMode == RecipeInputMode.INVENTORY) "Pilih bahan dari inventaris kamu" else "Masukkan bahan secara manual",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    if (selectedMode == RecipeInputMode.INVENTORY) {
                        if (state.ingredients.isEmpty()) {
                            item { EmptyIngredientsState(onAddFoodClick) }
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
                                modifier = Modifier.fillMaxWidth().testTag("manual_ingredient_input"),
                                placeholder = { Text("Contoh: telur, nasi, bakso") },
                                label = { Text("Tambah Bahan") },
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            viewModel.addManualIngredient(manualInput)
                                            manualInput = ""
                                            focusManager.clearFocus()
                                        },
                                        modifier = Modifier.testTag("btn_add_manual")
                                    ) {
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
                                Text("Prioritaskan bahan hampir kedaluwarsa", style = MaterialTheme.typography.bodyMedium)
                                Text("Berikan resep menggunakan bahan paling mendesak.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = state.prioritizeExpired,
                                onCheckedChange = { viewModel.setPrioritizeExpired(it) },
                                modifier = Modifier.testTag("switch_prioritize")
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
                                    modifier = Modifier.weight(1f).testTag("chip_pref_$pref")
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(80.dp)) 
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
    val isDark = isSystemInDarkTheme()
    val statusColor = when (status) {
        FoodStatus.SAFE -> if (isDark) SafeTextDark else SafeTextLight
        FoodStatus.NEAR_EXPIRY -> if (isDark) WarningTextDark else WarningTextLight
        else -> if (isDark) ExpiredTextDark else ExpiredTextLight
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .testTag("ingredient_card_${item.id}"),
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
                        item.quantity.formatQuantity(item.unit),
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
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("checkbox_${item.id}")
            )
        }
    }
}

@Composable
fun EmptyIngredientsState(onAddFoodClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp).testTag("empty_state"),
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
            "Belum ada bahan di inventaris",
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
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onAddFoodClick,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.testTag("btn_empty_add_food")
        ) {
            Text("Tambah Makanan", fontWeight = FontWeight.Bold)
        }
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
