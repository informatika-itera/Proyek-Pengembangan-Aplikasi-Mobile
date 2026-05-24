package com.example.neurodeck.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// ════════════════════════════════════════════════════════════════════════════
// Dialogs.kt — commonMain
//
// Sprint 2 — Prioritas 3b (Reusable Components, minimal scope)
//
// File ini menambahkan 2 reusable component yang dipakai di multiple
// screens (P3d Decks delete, P3e Profile reset data, P4 Stats period filter):
//
//   1. ConfirmDialog  — Material 3 AlertDialog untuk konfirmasi destructive
//                       actions (delete, reset). Optional "double-confirm"
//                       via [isDestructive] yang mengubah warna tombol confirm
//                       jadi error red.
//
//   2. SectionTitle   — Heading kecil untuk membagi konten dalam screen
//                       (e.g. "Aksi Cepat", "Pengaturan", "Data Management").
//
// Existing components (LoadingIndicator, ErrorMessage, EmptyState) sudah
// ada di CommonComponents.kt dari Sprint 1 — tidak perlu duplicate.
// ════════════════════════════════════════════════════════════════════════════

/**
 * Generic confirmation dialog Material 3.
 *
 * Pakai untuk destructive actions yang perlu konfirmasi user:
 *   - Delete deck / delete card
 *   - Reset all data (di Profile tab)
 *   - Clear chat history (di AI Chat tab)
 *   - Logout (kalau ada nanti)
 *
 * Pattern usage (di parent composable):
 *
 *   var showDialog by remember { mutableStateOf(false) }
 *
 *   IconButton(onClick = { showDialog = true }) {
 *       Icon(Icons.Default.Delete, contentDescription = "Hapus")
 *   }
 *
 *   if (showDialog) {
 *       ConfirmDialog(
 *           title = "Hapus Deck?",
 *           message = "Semua kartu di deck ini akan ikut terhapus permanen.",
 *           confirmLabel = "Hapus",
 *           isDestructive = true,
 *           onConfirm = {
 *               viewModel.deleteDeck()
 *               showDialog = false
 *           },
 *           onDismiss = { showDialog = false },
 *       )
 *   }
 *
 * @param title          Judul dialog (bold, biasanya 1 baris).
 * @param message        Pesan deskripsi (1-3 baris). Boleh markdown-style \n
 *                       untuk multi-line.
 * @param confirmLabel   Label tombol confirm. Default "OK".
 *                       Pakai action verb yang spesifik: "Hapus", "Reset",
 *                       "Logout" — bukan "OK" yang ambigu.
 * @param dismissLabel   Label tombol cancel. Default "Batal".
 * @param isDestructive  Kalau true, tombol confirm pakai warna error (red)
 *                       sebagai visual warning. Pakai true untuk delete/reset.
 * @param onConfirm      Lambda yang dijalankan saat user tap confirm.
 *                       PENTING: caller harus juga close dialog di sini
 *                       (set showDialog = false), karena dialog tidak
 *                       auto-close setelah confirm — supaya caller bisa
 *                       optional show loading sebelum dismiss.
 * @param onDismiss      Lambda dijalankan saat user tap dismiss/cancel/back/
 *                       tap outside. Biasanya set showDialog = false.
 */
@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmLabel: String = "OK",
    dismissLabel: String = "Batal",
    isDestructive: Boolean = false,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = confirmLabel,
                    // Destructive action = red text untuk visual warning.
                    // M3 guideline: hindari pakai red button background di
                    // dialog (terlalu visually heavy), cukup colored text.
                    color = if (isDestructive) {
                        MaterialTheme.colorScheme.error
                    } else {
                        Color.Unspecified  // default = primary
                    },
                    fontWeight = FontWeight.SemiBold,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = dismissLabel)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
    )
}

/**
 * Heading kecil untuk membagi sections dalam screen.
 *
 * Pattern usage:
 *
 *   Column {
 *       SectionTitle(text = "Aksi Cepat")
 *       QuickActionRow(...)
 *
 *       SectionTitle(text = "Continue Learning")
 *       LazyColumn { items(decks) { ... } }
 *   }
 *
 * Styling: titleMedium + semibold + primary color — distinct enough untuk
 * jadi section divider, tapi tidak overwhelming.
 *
 * @param text       Teks section heading.
 * @param modifier   Optional modifier (default fillMaxWidth + padding).
 */
@Composable
fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    )
}

/**
 * Spacer vertikal dengan ukuran standard. Helper untuk hindari boilerplate
 * `Spacer(modifier = Modifier.height(16.dp))` di banyak tempat.
 *
 * Pakai standard 8dp grid (8/16/24/32) untuk konsistensi.
 */
@Composable
fun VerticalSpacer(height: Int = 16) {
    Spacer(modifier = Modifier.height(height.dp))
}