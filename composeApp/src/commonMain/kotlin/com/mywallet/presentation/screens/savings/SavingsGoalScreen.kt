package com.mywallet.presentation.screens.savings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mywallet.domain.model.SavingsGoal
import com.mywallet.utils.formatCurrency
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingsGoalScreen(
    viewModel: SavingsGoalViewModel = koinViewModel()
) {
    val goals by viewModel.goals.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Target Tabungan",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = 28.sp
                        )
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Target")
            }
        }
    ) { padding ->
        if (goals.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Flag, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                    Text("Belum ada target tabungan", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(goals) { goal ->
                    SavingsGoalItem(
                        goal = goal,
                        onDelete = { viewModel.deleteGoal(goal.id) },
                        onUpdateAmount = { viewModel.updateCurrentAmount(goal.id, it) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddGoalDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, target, category, color ->
                viewModel.addGoal(title, target, category, color, null)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun SavingsGoalItem(
    goal: SavingsGoal,
    onDelete: () -> Unit,
    onUpdateAmount: (Double) -> Unit
) {
    var showUpdateDialog by remember { mutableStateOf(false) }
    val progress = (goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f)
    val color = try { Color(goal.color.removePrefix("#").toLong(16) or 0xFF000000) } catch(e: Exception) { MaterialTheme.colorScheme.primary }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { showUpdateDialog = true },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(color.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getCategoryIcon(goal.category),
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(goal.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(goal.category, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Terkumpul: Rp ${formatCurrency(goal.currentAmount)}", style = MaterialTheme.typography.bodyMedium)
                Text("Target: Rp ${formatCurrency(goal.targetAmount)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                color = color,
                trackColor = color.copy(alpha = 0.2f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "${(progress * 100).toInt()}% Tercapai",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }

    if (showUpdateDialog) {
        var amountText by remember { mutableStateOf(goal.currentAmount.toString()) }
        AlertDialog(
            onDismissRequest = { showUpdateDialog = false },
            title = { Text("Update Tabungan") },
            text = {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Jumlah Tabungan Saat Ini") },
                    prefix = { Text("Rp ") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onUpdateAmount(amountText.toDoubleOrNull() ?: goal.currentAmount)
                    showUpdateDialog = false
                }) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUpdateDialog = false }) { Text("Batal") }
            }
        )
    }
}

@Composable
fun AddGoalDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("") }
    
    val categories = listOf(
        SavingsCategory("Elektronik", Icons.Default.Laptop, "#448AFF"),
        SavingsCategory("Liburan", Icons.Default.Flight, "#FFC107"),
        SavingsCategory("Pendidikan", Icons.Default.School, "#9C27B0"),
        SavingsCategory("Kendaraan", Icons.Default.DirectionsCar, "#FF5252"),
        SavingsCategory("Darurat", Icons.Default.NewReleases, "#F44336"),
        SavingsCategory("Rumah", Icons.Default.Home, "#4CAF50"),
        SavingsCategory("Lainnya", Icons.Default.Category, "#9E9E9E")
    )
    
    var selectedCategory by remember { mutableStateOf(categories[0]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Target Tabungan Baru") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Nama Target (misal: Beli Laptop)") })
                OutlinedTextField(value = target, onValueChange = { target = it }, label = { Text("Target Nominal") }, prefix = { Text("Rp ") })
                
                Text("Pilih Jenis", style = MaterialTheme.typography.labelMedium)
                
                // Horizontal list of categories
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.height(150.dp).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(categories) { category ->
                            val isSelected = selectedCategory == category
                            val color = Color(category.color.removePrefix("#").toLong(16) or 0xFF000000)
                            
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedCategory = category },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) color.copy(alpha = 0.1f) else Color.Transparent,
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, color) else null
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = category.icon,
                                        contentDescription = null,
                                        tint = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = category.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (isSelected) color else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (title.isNotEmpty() && target.toDoubleOrNull() != null) {
                    onConfirm(title, target.toDoubleOrNull()!!, selectedCategory.name, selectedCategory.color)
                }
            }) { Text("Buat") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

data class SavingsCategory(
    val name: String,
    val icon: ImageVector,
    val color: String
)

fun getCategoryIcon(category: String): ImageVector {
    return when(category) {
        "Elektronik" -> Icons.Default.Laptop
        "Liburan" -> Icons.Default.Flight
        "Pendidikan" -> Icons.Default.School
        "Kendaraan" -> Icons.Default.DirectionsCar
        "Darurat" -> Icons.Default.NewReleases
        "Rumah" -> Icons.Default.Home
        else -> Icons.Default.Category
    }
}
