package com.example.foodsaver.presentation.screens.recipe.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.foodsaver.domain.model.MealType
import com.example.foodsaver.domain.model.Recipe
import kotlinx.datetime.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: String,
    onNavigateBack: () -> Unit,
    viewModel: RecipeDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showMealPlanDialog by remember { mutableStateOf(false) }

    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId)
    }

    LaunchedEffect(state.isMealPlanSaved) {
        if (state.isMealPlanSaved) {
            snackbarHostState.showSnackbar("Sip! Resep sudah masuk ke jadwal masakmu.")
            viewModel.resetMealPlanStatus()
        }
    }

    if (showMealPlanDialog) {
        MealPlanSelectionDialog(
            onDismiss = { showMealPlanDialog = false },
            onConfirm = { date, type ->
                viewModel.addToMealPlan(date, type)
                showMealPlanDialog = false
            }
        )
    }

    Scaffold(
        modifier = Modifier.testTag("recipe_detail_screen"),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            RecipeDetailTopBar(
                isFavorite = state.recipe?.isFavorite == true,
                onBackClick = onNavigateBack,
                onMealPlanClick = { showMealPlanDialog = true },
                onFavoriteClick = viewModel::toggleFavorite,
                showActions = state.recipe != null
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.error != null -> {
                    Text(
                        text = "Aduh, detail resepnya nggak mau muncul. Coba lagi ya!",
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                state.recipe != null -> {
                    RecipeDetailContent(recipe = state.recipe!!)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipeDetailTopBar(
    isFavorite: Boolean,
    onBackClick: () -> Unit,
    onMealPlanClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    showActions: Boolean
) {
    TopAppBar(
        title = { Text("Detail Resep", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
            }
        },
        actions = {
            if (showActions) {
                IconButton(onClick = onMealPlanClick) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = "Tambah ke Jadwal Masak", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Simpan Favorit",
                        tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )
}

@Composable
private fun RecipeDetailContent(recipe: Recipe) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        AsyncImage(
            model = recipe.imageUrl,
            contentDescription = recipe.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = recipe.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )
            
            RecipeTags(category = recipe.category, area = recipe.area)

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)

            RecipeIngredientsSection(ingredients = recipe.ingredients)

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.outlineVariant)

            RecipeInstructionsSection(instructions = recipe.instructions)
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun RecipeTags(category: String?, area: String?) {
    Row(
        modifier = Modifier.padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        category?.let {
            AssistChip(
                onClick = {},
                label = { Text(it) },
                shape = RoundedCornerShape(12.dp)
            )
        }
        area?.let {
            AssistChip(
                onClick = {},
                label = { Text(it) },
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
private fun RecipeIngredientsSection(ingredients: List<com.example.foodsaver.domain.model.IngredientAmount>) {
    Text(
        text = "Bahan yang Dibutuhkan",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.padding(bottom = 12.dp, top = 8.dp)
    )

    ingredients.forEach { ingredient ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = ingredient.name, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = ingredient.amount,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun RecipeInstructionsSection(instructions: String?) {
    Text(
        text = "Cara Memasak",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.padding(bottom = 12.dp)
    )

    Text(
        text = instructions ?: "Instruksinya belum tersedia nih.",
        style = MaterialTheme.typography.bodyMedium,
        lineHeight = 24.sp
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlanSelectionDialog(
    onDismiss: () -> Unit,
    onConfirm: (LocalDate, MealType) -> Unit
) {
    var selectedDate by remember { mutableStateOf(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date) }
    var selectedType by remember { mutableStateOf(MealType.LUNCH) }
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        selectedDate = Instant.fromEpochMilliseconds(it)
                            .toLocalDateTime(TimeZone.currentSystemDefault()).date
                    }
                    showDatePicker = false
                }) { Text("Pilih", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Batal") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Atur Jadwal Masak", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Kapan kamu mau masak menu ini?", style = MaterialTheme.typography.bodyMedium)
                
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                    val monthIndo = when(selectedDate.month) {
                        Month.JANUARY -> "Januari"
                        Month.FEBRUARY -> "Februari"
                        Month.MARCH -> "Maret"
                        Month.APRIL -> "April"
                        Month.MAY -> "Mei"
                        Month.JUNE -> "Juni"
                        Month.JULY -> "Juli"
                        Month.AUGUST -> "Agustus"
                        Month.SEPTEMBER -> "September"
                        Month.OCTOBER -> "Oktober"
                        Month.NOVEMBER -> "November"
                        Month.DECEMBER -> "Desember"
                        else -> selectedDate.month.name
                    }
                    val label = when (selectedDate) {
                        today -> "Hari Ini"
                        today.plus(1, DateTimeUnit.DAY) -> "Besok"
                        else -> "${selectedDate.dayOfMonth} $monthIndo"
                    }
                    Text("Tanggal: $label", fontWeight = FontWeight.Bold)
                }

                Text("Untuk waktu makan apa?", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MealType.entries.forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { 
                                Text(when(type) {
                                    MealType.BREAKFAST -> "Sarapan"
                                    MealType.LUNCH -> "Siang"
                                    MealType.DINNER -> "Malam"
                                })
                            },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedDate, selectedType) },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Simpan Jadwal", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Nggak Jadi")
            }
        }
    )
}
