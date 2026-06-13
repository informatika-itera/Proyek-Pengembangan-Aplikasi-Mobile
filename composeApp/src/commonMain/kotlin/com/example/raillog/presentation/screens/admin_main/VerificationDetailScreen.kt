package com.example.raillog.presentation.screens.admin_main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.raillog.presentation.components.*
import com.example.raillog.presentation.theme.RailLogColors
import com.example.raillog.presentation.theme.Spacing
import org.koin.compose.viewmodel.koinViewModel

// ── Helper: Terjemahkan error mentah dari Gemini API ke pesan yang ramah ──────

private fun parseAiErrorMessage(rawError: String?): Triple<String, String, Boolean> {
    if (rawError == null) return Triple(
        "Gagal menghubungi layanan AI.",
        "Periksa koneksi internet Anda dan coba lagi.",
        false
    )

    return when {
        // Quota harian habis atau rate limit per menit
        rawError.contains("quota", ignoreCase = true) ||
                rawError.contains("RESOURCE_EXHAUSTED", ignoreCase = true) -> {
            // Coba ambil angka detik retry dari pesan, contoh: "Please retry in 57.85 seconds."
            val retrySeconds = Regex("""retry in ([\d.]+) second""")
                .find(rawError)?.groupValues?.get(1)?.toDoubleOrNull()?.toInt()

            val detail = if (retrySeconds != null) {
                "Batas penggunaan AI tercapai. Coba lagi dalam ±$retrySeconds detik."
            } else {
                "Batas penggunaan harian AI telah tercapai. Coba lagi besok atau gunakan API key baru."
            }
            Triple("Batas kuota Gemini API tercapai", detail, retrySeconds != null)
        }

        // Masalah autentikasi / API key tidak valid
        rawError.contains("API_KEY", ignoreCase = true) ||
                rawError.contains("INVALID_ARGUMENT", ignoreCase = true) ||
                rawError.contains("401", ignoreCase = true) -> Triple(
            "API key tidak valid",
            "Periksa konfigurasi GEMINI_API_KEY di local.properties.",
            false
        )

        // Koneksi / timeout
        rawError.contains("timeout", ignoreCase = true) ||
                rawError.contains("ConnectException", ignoreCase = true) ||
                rawError.contains("SocketException", ignoreCase = true) -> Triple(
            "Koneksi terputus",
            "Tidak dapat menjangkau server AI. Periksa koneksi internet Anda.",
            false
        )

        // Server error sisi Gemini
        rawError.contains("503", ignoreCase = true) ||
                rawError.contains("500", ignoreCase = true) ||
                rawError.contains("UNAVAILABLE", ignoreCase = true) -> Triple(
            "Layanan AI sedang gangguan",
            "Server Gemini sedang tidak tersedia. Coba beberapa saat lagi.",
            false
        )

        // Fallback generic
        else -> Triple(
            "Gagal menghubungi layanan AI",
            "Terjadi kesalahan tak terduga. Tekan tombol refresh untuk mencoba lagi.",
            false
        )
    }
}

// ── Verification Detail ───────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationDetailScreen(
    requisitionId: Long,
    onNavigateBack: () -> Unit,
    viewModel: VerificationDetailViewModel = koinViewModel()
) {
    LaunchedEffect(requisitionId) { viewModel.loadItem(requisitionId) }

    val item         by viewModel.selectedItem.collectAsState()
    val aiValidation by viewModel.aiValidation.collectAsState()

    if (item == null) { LoadingIndicator(); return }

    val currentItem = item!!

    Scaffold(
        containerColor = RailLogColors.Background,
        topBar = {
            TopAppBar(
                title = {
                    Text("Detail verifikasi", fontWeight = FontWeight.Medium,
                        color = RailLogColors.TextPrimary)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null,
                            tint = RailLogColors.TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RailLogColors.Surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(Spacing.pagePadding),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            // Header card
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(RailLogColors.PrimaryAction)
                        .padding(16.dp)
                ) {
                    Text("REQ #${currentItem.id}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f))
                    Spacer(Modifier.height(4.dp))
                    Text(currentItem.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold, color = Color.White)
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(currentItem.status.name, fontSize = 11.sp,
                                color = Color.White, fontWeight = FontWeight.Medium)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(currentItem.priority.name, fontSize = 11.sp,
                                color = Color.White, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            // Data fields
            item {
                SurfaceCard {
                    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                        Text("Data teknis & proyek",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium, color = RailLogColors.TextTertiary)
                        Spacer(Modifier.height(Spacing.md))
                        DetailField("Kode proyek", currentItem.partCode, currentItem.partCode.isNotBlank())
                        SubtleDivider(modifier = Modifier.padding(vertical = 10.dp))
                        DetailField("Komponen", currentItem.name, valid = true)
                        SubtleDivider(modifier = Modifier.padding(vertical = 10.dp))
                        DetailField("Kuantitas", "${currentItem.quantity} ${currentItem.unit}",
                            currentItem.quantity > 0)
                        SubtleDivider(modifier = Modifier.padding(vertical = 10.dp))
                        DetailField("Supplier", currentItem.supplier, currentItem.supplier.isNotBlank())
                        SubtleDivider(modifier = Modifier.padding(vertical = 10.dp))
                        DetailField("Catatan",
                            currentItem.notes.ifBlank { "Tidak ada catatan" }, valid = true,
                            isLast = true)
                    }
                }
            }

            // AI panel
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.AutoAwesome, null,
                                tint = RailLogColors.AIText, modifier = Modifier.size(16.dp))
                            Text("Analisis AI",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium, color = RailLogColors.TextPrimary)
                        }
                        if (aiValidation.status != AIValidationStatus.LOADING) {
                            IconButton(
                                onClick = { viewModel.retryAiValidation() },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Refresh, null,
                                    tint = RailLogColors.TextSecondary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    AIValidationPanel(aiValidation)
                }
            }

            // Actions
            item {
                Row(modifier = Modifier.padding(top = Spacing.sm),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.itemGap)) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f).height(48.dp),
                        onClick  = { viewModel.rejectItem { onNavigateBack() } },
                        shape    = RoundedCornerShape(10.dp),
                        border   = androidx.compose.foundation.BorderStroke(1.dp, RailLogColors.Danger600),
                        colors   = ButtonDefaults.outlinedButtonColors(contentColor = RailLogColors.Danger600)
                    ) {
                        Text("Tolak", fontWeight = FontWeight.Medium)
                    }
                    Button(
                        modifier = Modifier.weight(1f).height(48.dp),
                        onClick  = { viewModel.verifyItem { onNavigateBack() } },
                        shape    = RoundedCornerShape(10.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = RailLogColors.Success600)
                    ) {
                        Text("Setujui", fontWeight = FontWeight.Medium)
                    }
                }
            }

            item { Spacer(Modifier.height(Spacing.xl)) }
        }
    }
}

@Composable
private fun DetailField(label: String, value: String, valid: Boolean, isLast: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = RailLogColors.TextTertiary)
            Spacer(Modifier.height(2.dp))
            Text(value, style = MaterialTheme.typography.bodyMedium,
                color = RailLogColors.TextPrimary)
        }
        if (!valid) {
            Icon(Icons.Default.Warning, null,
                tint = RailLogColors.Warning600, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun AIValidationPanel(aiValidation: AIValidationState) {
    when (aiValidation.status) {

        // ── Loading ───────────────────────────────────────────────────────────
        AIValidationStatus.LOADING -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(RailLogColors.AISurface)
                    .border(1.dp, RailLogColors.AIBorder, RoundedCornerShape(10.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = RailLogColors.AIText
                )
                Text("Gemini AI sedang menganalisis...",
                    style = MaterialTheme.typography.bodySmall, color = RailLogColors.AIText)
            }
        }

        // ── Success ───────────────────────────────────────────────────────────
        AIValidationStatus.SUCCESS -> {
            val res = aiValidation.result ?: ""
            val (bg, border, textColor) = when {
                res.contains("TIDAK VALID", true) ->
                    Triple(RailLogColors.Danger50, RailLogColors.Danger600, RailLogColors.Danger600)
                res.contains("PERLU REVIEW", true) ->
                    Triple(RailLogColors.Warning50, RailLogColors.Warning600, RailLogColors.Warning600)
                else ->
                    Triple(RailLogColors.Success50, RailLogColors.Success600, RailLogColors.Success600)
            }
            val statusLabel = when {
                res.contains("TIDAK VALID", true)  -> "Tidak valid"
                res.contains("PERLU REVIEW", true) -> "Perlu review"
                else -> "Valid"
            }
            AnimatedVisibility(visible = true, enter = fadeIn() + slideInVertically { it / 2 }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(bg)
                        .border(1.dp, border, RoundedCornerShape(10.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.AutoAwesome, null,
                            tint = textColor, modifier = Modifier.size(14.dp))
                        Text(statusLabel, fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold, color = textColor)
                    }
                    Text(res, style = MaterialTheme.typography.bodySmall,
                        color = RailLogColors.TextPrimary, lineHeight = 20.sp)
                }
            }
        }

        // ── Error ─────────────────────────────────────────────────────────────
        AIValidationStatus.ERROR -> {
            // Terjemahkan error teknis menjadi pesan yang ramah untuk pengguna.
            // parseAiErrorMessage mengembalikan Triple(judul, detail, canRetrySoon).
            val (title, detail, canRetryInMoment) = parseAiErrorMessage(aiValidation.errorMessage)

            AnimatedVisibility(visible = true, enter = fadeIn() + slideInVertically { it / 2 }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(RailLogColors.Warning50)
                        .border(1.dp, RailLogColors.Warning600, RoundedCornerShape(10.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Baris judul error
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            if (canRetryInMoment) Icons.Default.HourglassBottom
                            else Icons.Default.CloudOff,
                            contentDescription = null,
                            tint = RailLogColors.Warning600,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = RailLogColors.Warning600
                        )
                    }

                    // Pesan detail yang ramah
                    Text(
                        detail,
                        style = MaterialTheme.typography.bodySmall,
                        color = RailLogColors.TextSecondary,
                        lineHeight = 18.sp
                    )

                    // Hint untuk tombol refresh
                    HorizontalDivider(
                        color = RailLogColors.Warning600.copy(alpha = 0.2f),
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Refresh, null,
                            tint = RailLogColors.TextTertiary,
                            modifier = Modifier.size(11.dp))
                        Text(
                            "Tekan tombol refresh di atas untuk mencoba lagi",
                            style = MaterialTheme.typography.labelSmall,
                            color = RailLogColors.TextTertiary
                        )
                    }
                }
            }
        }

        else -> {}
    }
}