package com.example.tabungin.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tabungin.domain.model.Setoran
import com.example.tabungin.domain.model.Target
import androidx.compose.material3.*
import androidx.compose.runtime.*


fun formatRupiah(amount: Double): String {
    val formatted = amount.toLong().toString()
        .reversed().chunked(3).joinToString(".").reversed()
    return "Rp $formatted"
}


fun parseHexColor(hex: String): Color {
    return try {
        val clean = hex.removePrefix("#")
        val r = clean.substring(0, 2).toInt(16)
        val g = clean.substring(2, 4).toInt(16)
        val b = clean.substring(4, 6).toInt(16)
        Color(r, g, b)
    } catch (e: Exception) {
        Color(0xFF4CAF50)
    }
}

// ── SummaryCard ───────────────────────────────────────────────

@Composable
fun SummaryCard(
    totalTerkumpul: Double,
    totalTarget: Double,
    jumlahTarget: Int
) {
    val progres = if (totalTarget <= 0) 0f
    else (totalTerkumpul / totalTarget).toFloat().coerceIn(0f, 1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Ringkasan Tabungan",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(4.dp))
            Text(
                formatRupiah(totalTerkumpul),
                style      = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color      = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress   = { progres },
                modifier   = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${(progres * 100).toInt()}% tercapai",
                    style = MaterialTheme.typography.bodySmall)
                Text("$jumlahTarget target aktif",
                    style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

// ── TargetCard ────────────────────────────────────────────────

@Composable
fun TargetCard(
    target: Target,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier  = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier          = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier         = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(parseHexColor(target.warna).copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Text(target.icon, style = MaterialTheme.typography.titleLarge)
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(target.nama,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(2.dp))
                Text(
                    "${formatRupiah(target.terkumpul)} / ${formatRupiah(target.targetAmount)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress   = { target.progres },
                    modifier   = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    color      = parseHexColor(target.warna)
                )
                Spacer(Modifier.height(2.dp))
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${(target.progres * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = parseHexColor(target.warna))
                    Text("⏰ ${target.deadline}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(Icons.Default.Delete, "Hapus",
                    tint = MaterialTheme.colorScheme.error)
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title  = { Text("Hapus Target?") },
            text   = { Text("\"${target.nama}\" dan semua setorannya akan dihapus permanen.") },
            confirmButton = {
                TextButton(
                    onClick = { showDeleteDialog = false; onDelete() },
                    colors  = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) { Text("Hapus") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            }
        )
    }
}

// ── ProgressCard ─────────────────────────────────────────────

@Composable
fun ProgressCard(target: Target) {
    val accentColor = parseHexColor(target.warna)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(target.icon, style = MaterialTheme.typography.displaySmall)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(target.nama,
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold)
                    Text("⏰ Deadline: ${target.deadline}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer)
                }
            }

            Spacer(Modifier.height(16.dp))
            LinearProgressIndicator(
                progress   = { target.progres },
                modifier   = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)),
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                color      = accentColor
            )
            Spacer(Modifier.height(12.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Terkumpul", style = MaterialTheme.typography.labelSmall)
                    Text(formatRupiah(target.terkumpul),
                        style      = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color      = accentColor)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Progres", style = MaterialTheme.typography.labelSmall)
                    Text("${(target.progres * 100).toInt()}%",
                        style      = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Sisa", style = MaterialTheme.typography.labelSmall)
                    Text(formatRupiah(target.sisaTabungan),
                        style      = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color      = if (target.tercapai) accentColor
                        else MaterialTheme.colorScheme.error)
                }
            }

            if (target.tercapai) {
                Spacer(Modifier.height(12.dp))
                Card(colors = CardDefaults.cardColors(containerColor = accentColor.copy(alpha = 0.15f))) {
                    Text(
                        "🎉 Target tercapai! Selamat menabung!",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        style    = MaterialTheme.typography.bodyMedium,
                        color    = accentColor
                    )
                }
            }
        }
    }
}

// ── SetoranItem ───────────────────────────────────────────────

@Composable
fun SetoranItem(
    setoran: Setoran,
    showTargetNama: Boolean = false,
    onDelete: () -> Unit,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
    ) {
        Row(
            modifier          = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier         = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.tertiaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text("💰", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                if (showTargetNama && setoran.targetNama.isNotEmpty()) {
                    Text(
                        setoran.targetNama,
                        style      = MaterialTheme.typography.labelSmall,
                        color      = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    formatRupiah(setoran.amount),
                    style      = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                if (setoran.catatan.isNotBlank()) {
                    Text(setoran.catatan,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(setoran.tanggal,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Delete hanya tampil di DetailScreen (bukan Riwayat)
            if (onClick == null) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Hapus setoran",
                        tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
