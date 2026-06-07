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

/**
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
                    color = if (isDestructive) {
                        MaterialTheme.colorScheme.error
                    } else {
                        Color.Unspecified
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
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    )
}

@Composable
fun VerticalSpacer(height: Int = 16) {
    Spacer(modifier = Modifier.height(height.dp))
}
