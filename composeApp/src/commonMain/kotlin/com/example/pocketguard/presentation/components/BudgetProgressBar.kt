package com.example.pocketguard.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BudgetProgressBar(
    totalExpense: Double,
    budgetLimit: Double,
    monthLabel : String,
    onEditClick: () -> Unit
) {
    // Menghindari pembagian dengan nol
    val percentage = if (budgetLimit > 0) (totalExpense / budgetLimit) else 0.0
    val progress = percentage.coerceIn(0.0, 1.0).toFloat()

    // Logika perubahan warna psikologis
    val progressColor = when {
        percentage >= 0.9 -> Color(0xFFE53935) // Merah (Bahaya: >90%)
        percentage >= 0.7 -> Color(0xFFFFA000) // Oranye (Peringatan: >70%)
        else -> Color(0xFF43A047)              // Hijau (Aman)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Batas Pengeluaran ($monthLabel)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(onClick = onEditClick, modifier = Modifier.size(24.dp)) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit Anggaran",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar Visual
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                color = progressColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Terpakai: Rp ${formatRupiahKmp(totalExpense)}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (budgetLimit > 0) "Batas: Rp ${formatRupiahKmp(budgetLimit)}" else "Batas: Belum diatur",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/* =====================================================================
 * MODERN SET BUDGET DIALOG (UI DIPERBARUI)
 * ===================================================================== */
@Composable
fun SetBudgetDialog(
    currentBudget: Double,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    // Inisialisasi state dengan nilai yang sudah diformat jika ada
    var inputValue by remember {
        val initial = if (currentBudget > 0) currentBudget.toLong().toString() else ""
        mutableStateOf(formatRupiahInput(initial))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp), // Sudut lebih membulat dan modern
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text("Atur Batas Pengeluaran", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        text = {
            Column {
                Text(
                    text = "Tentukan batas maksimal pengeluaran Anda bulan ini agar kantong tetap aman.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = inputValue,
                    onValueChange = { newValue ->
                        // 1. Bersihkan semua karakter selain angka
                        val rawNumber = newValue.replace(Regex("\\D"), "")
                        // 2. Format ulang menjadi string bertitik otomatis
                        inputValue = formatRupiahInput(rawNumber)
                    },
                    label = { Text("Nominal") },
                    prefix = { Text("Rp ", fontWeight = FontWeight.SemiBold) }, // Tulisan Rp Permanen
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2E7D32),
                        focusedLabelColor = Color(0xFF2E7D32)
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Buang titik saat menekan simpan agar konversi ke Double aman
                    val rawNumber = inputValue.replace(Regex("\\D"), "")
                    val budget = rawNumber.toDoubleOrNull() ?: 0.0
                    onSave(budget)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Simpan", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF2E7D32))
            ) {
                Text("Batal", fontWeight = FontWeight.Medium)
            }
        }
    )
}

/* =====================================================================
 * FORMATTER UTILS (AMAN UNTUK KMP)
 * ===================================================================== */

/**
 * Format untuk Progress Bar UI (Contoh: 1.500.000)
 */
private fun formatRupiahKmp(amount: Double): String {
    val longAmount = amount.toLong()
    val stringAmount = longAmount.toString()
    val reversedString = stringAmount.reversed()
    val stringBuilder = StringBuilder()

    for (i in reversedString.indices) {
        if (i > 0 && i % 3 == 0) {
            stringBuilder.append('.')
        }
        stringBuilder.append(reversedString[i])
    }

    return stringBuilder.reverse().toString()
}

/**
 * Format untuk kotak input teks otomatis (Contoh ketik "15" jadi "15", ketik "1500" jadi "1.500")
 */
private fun formatRupiahInput(rawDigitString: String): String {
    if (rawDigitString.isEmpty()) return ""
    val reversed = rawDigitString.reversed()
    val builder = StringBuilder()
    for (i in reversed.indices) {
        if (i > 0 && i % 3 == 0) {
            builder.append('.')
        }
        builder.append(reversed[i])
    }
    return builder.reverse().toString()
}