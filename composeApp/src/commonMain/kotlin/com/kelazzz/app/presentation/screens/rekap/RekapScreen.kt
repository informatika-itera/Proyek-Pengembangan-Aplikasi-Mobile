package com.kelazzz.app.presentation.screens.rekap

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kelazzz.app.domain.model.AttendanceSummary
import com.kelazzz.app.domain.model.Kelas
import com.kelazzz.app.domain.model.RiskLevel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RekapScreen(
    viewModel: RekapViewModel = koinViewModel(),
    onKelasClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Menangani pesan error atau sukses sync menggunakan Snackbar
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.syncSuccess) {
        if (uiState.syncSuccess) {
            snackbarHostState.showSnackbar("Data presensi berhasil disinkronkan!")
            viewModel.clearSyncSuccess()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.syncPresensi() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                if (uiState.isSyncing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.CloudSync,
                        contentDescription = "Sinkronisasi data presensi"
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Header Ringkas & Bar Sinkronisasi Indikator
                if (uiState.isSyncing) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Kolom Pencarian yang Elegan
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = viewModel::onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    placeholder = { Text("Cari kelas atau mata kuliah...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search"
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Menangani State Tampilan List
                when {
                    uiState.isLoading && uiState.kelasList.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    uiState.filteredKelasList.isEmpty() -> {
                        EmptyStateView(
                            searchQuery = uiState.searchQuery,
                            onSyncClick = { viewModel.syncPresensi() }
                        )
                    }

                    else -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 80.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                items = uiState.filteredKelasList,
                                key = { it.kelas.kodeKelas }
                            ) { uiModel ->
                                KelasCardItem(
                                    uiModel = uiModel,
                                    onClick = { 
                                        viewModel.selectKelas(uiModel.kelas)
                                        onKelasClick(uiModel.kelas.kodeKelas) 
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Menampilkan Bottom Sheet detail presensi jika selectedKelas != null
    if (uiState.selectedKelas != null) {
        val selectedKelas = uiState.selectedKelas!!
        ModalBottomSheet(
            onDismissRequest = { viewModel.dismissKelasDetail() },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            KelasPresensiDetailSheet(
                kelas = selectedKelas,
                isLoading = uiState.isDetailLoading,
                presensiList = uiState.presensiDetailList,
                onDismiss = { viewModel.dismissKelasDetail() }
            )
        }
    }
}

@Composable
private fun KelasCardItem(
    uiModel: KelasUiModel,
    onClick: () -> Unit
) {
    val kelas = uiModel.kelas
    val summary = uiModel.summary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Row Atas: Kode MK & Badge Kelas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = kelas.kodeMk,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Kelas ${kelas.namaKelas}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Judul Mata Kuliah
            Text(
                text = kelas.namaMk,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Dosen
            if (kelas.namaDosenList.isNotBlank()) {
                Text(
                    text = kelas.namaDosenList,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Section: Status Kehadiran Minimalis
            if (summary != null) {
                val alphaCount = summary.totalAlpha
                val statusText: String?
                val statusColor: Color
                
                when {
                    alphaCount <= 2 -> {
                         statusText = null
                         statusColor = Color.Transparent
                    }
                    alphaCount == 3 -> {
                         statusText = "Alpha: 3/3 (Batas Maks)"
                         statusColor = Color(0xFFEF6C00) // Oranye
                    }
                    else -> {
                         statusText = "Alpha: $alphaCount/3 (Bahaya)"
                         statusColor = Color(0xFFC62828) // Merah
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (statusText != null) Arrangement.SpaceBetween else Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (statusText != null) {
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }
                    
                    Text(
                        text = "Kehadiran: ${summary.persentaseKehadiran.toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Presensi belum tersinkron. Tekan tombol sinkronisasi untuk memperbarui data.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyStateView(
    searchQuery: String,
    onSyncClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (searchQuery.isEmpty()) Icons.Default.Book else Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (searchQuery.isEmpty()) "Belum ada data presensi" else "Mata kuliah tidak ditemukan",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (searchQuery.isEmpty()) {
                "Data lokal kosong. Silakan sinkronisasikan kelas dan presensi dari akun Pocket ITERA Anda."
            } else {
                "Tidak ada mata kuliah yang cocok dengan pencarian \"$searchQuery\"."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        if (searchQuery.isEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onSyncClick,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sinkronisasi Presensi")
            }
        }
    }
}

@Composable
private fun KelasPresensiDetailSheet(
    kelas: Kelas,
    isLoading: Boolean,
    presensiList: List<com.kelazzz.app.domain.model.Presensi>,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = kelas.namaMk,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${kelas.kodeMk} • Kelas ${kelas.namaKelas}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
            IconButton(onClick = onDismiss) {
                Icon(imageVector = Icons.Default.Clear, contentDescription = "Tutup")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Dosen Pengampu
        Text(
            text = "Dosen: ${kelas.namaDosenList.ifBlank { "Tidak ada nama dosen" }}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Loading Indicator
        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Summary Card
        val validMeetings = presensiList.filter { it.status != com.kelazzz.app.domain.model.StatusPresensi.BELUM_MULAI }
        val totalHadir = validMeetings.count { it.status == com.kelazzz.app.domain.model.StatusPresensi.HADIR }
        val totalAlpha = validMeetings.count { it.status == com.kelazzz.app.domain.model.StatusPresensi.ALPHA }
        val totalPertemuan = validMeetings.size

        val percentage = if (totalPertemuan > 0) (totalHadir.toFloat() / totalPertemuan * 100).toInt() else 100
        
        val performanceColor: Color
        val statusText: String
        val warningMessage: String
        
        when {
            totalAlpha <= 2 -> {
                performanceColor = Color(0xFF2E7D32) // Hijau
                statusText = "Kehadiran Aman"
                warningMessage = "Sisa jatah alpha Anda: ${3 - totalAlpha} kali."
            }
            totalAlpha == 3 -> {
                performanceColor = Color(0xFFEF6C00) // Oranye
                statusText = "Batas Maksimal!"
                warningMessage = "Jatah alpha habis (3/3). Hati-hati jangan sampai absen lagi."
            }
            else -> {
                performanceColor = Color(0xFFC62828) // Merah
                statusText = "Terancam Tidak Bisa UAS"
                warningMessage = "Alpha: $totalAlpha kali (Melebihi batas maksimal 3 kali)."
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = performanceColor.copy(alpha = 0.08f)),
            border = BorderStroke(1.dp, performanceColor.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = performanceColor
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Hadir: $totalHadir | Alpha: $totalAlpha | Terlaksana: $totalPertemuan",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "$percentage%",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = performanceColor
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // List Pertemuan 1-16
        Text(
            text = "Daftar Pertemuan (1-16)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (presensiList.isEmpty() && !isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Data presensi tidak tersedia offline.\nHarap sambungkan ke internet untuk melakukan sinkronisasi.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Urutkan pertemuan berdasarkan nomor 1 sampai 16
                val sortedList = presensiList.sortedBy { it.pertemuan }
                
                items(sortedList) { presensi ->
                    PertemuanItemRow(presensi = presensi)
                }
            }
        }
    }
}

@Composable
private fun PertemuanItemRow(
    presensi: com.kelazzz.app.domain.model.Presensi
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Pertemuan ${presensi.pertemuan}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                val formattedDate = if (presensi.tanggal.isNotBlank()) {
                    presensi.tanggal
                } else {
                    "Jadwal belum ditentukan"
                }
                
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Badge Status Kehadiran
            val badgeColor: Color
            val badgeTextColor: Color
            val badgeText: String
            
            when (presensi.status) {
                com.kelazzz.app.domain.model.StatusPresensi.HADIR -> {
                    badgeColor = Color(0xFFE8F5E9)
                    badgeTextColor = Color(0xFF2E7D32)
                    badgeText = "Hadir"
                }
                com.kelazzz.app.domain.model.StatusPresensi.ALPHA -> {
                    badgeColor = Color(0xFFFFEBEE)
                    badgeTextColor = Color(0xFFC62828)
                    badgeText = "Alpha"
                }
                com.kelazzz.app.domain.model.StatusPresensi.BELUM_MULAI -> {
                    badgeColor = MaterialTheme.colorScheme.surfaceVariant
                    badgeTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    badgeText = "Belum Ada"
                }
                else -> {
                    badgeColor = MaterialTheme.colorScheme.surfaceVariant
                    badgeTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    badgeText = "-"
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(badgeColor)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = badgeText,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = badgeTextColor
                )
            }
        }
    }
}
