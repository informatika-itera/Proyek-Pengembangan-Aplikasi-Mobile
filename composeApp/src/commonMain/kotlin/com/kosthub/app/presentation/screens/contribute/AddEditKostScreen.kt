package com.kosthub.app.presentation.screens.contribute

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kosthub.app.domain.model.Kost
import com.kosthub.app.presentation.components.EmptyState
import com.kosthub.app.presentation.components.ErrorState
import com.kosthub.app.presentation.components.LoadingState
import com.kosthub.app.presentation.state.OperationState
import com.kosthub.app.presentation.state.UiState
import com.kosthub.app.presentation.viewmodel.ProfileViewModel
import kotlinx.coroutines.delay

@Composable
fun AddEditKostScreen(
    title: String,
    kostId: Long?,
    uiState: UiState<List<Kost>>,
    profileViewModel: ProfileViewModel,
    operationState: OperationState,
    onClearOperation: () -> Unit,
    onSave: (Kost) -> Unit,
    onBack: () -> Unit
) {
    val profileState by profileViewModel.uiState.collectAsState()
    val contributorId = (profileState as? UiState.Success)?.data?.id ?: 1L

    val existingKost = when {
        kostId == null -> null
        uiState is UiState.Success -> uiState.data.firstOrNull { it.id == kostId }
        else -> null
    }

    var namaKos by remember { mutableStateOf("") }
    var nomorTelepon by remember { mutableStateOf("") }
    var daerah by remember { mutableStateOf("") }
    var jarakKm by remember { mutableStateOf("") }
    var hargaTahunan by remember { mutableStateOf("") }
    var tipeKos by remember { mutableStateOf("") }
    var kamarMandi by remember { mutableStateOf("") }
    var wifi by remember { mutableStateOf("") }
    var furniturKasur by remember { mutableStateOf("") }
    var furniturLemari by remember { mutableStateOf("") }
    var furniturMejaBelajar by remember { mutableStateOf("") }
    var fasilitasPendingin by remember { mutableStateOf("") }
    var areaLaundry by remember { mutableStateOf("") }
    var areaDapur by remember { mutableStateOf("") }
    var keamananCctv by remember { mutableStateOf("") }

    LaunchedEffect(existingKost) {
        if (existingKost != null) {
            namaKos = existingKost.namaKos
            nomorTelepon = existingKost.nomorTelepon.orEmpty()
            daerah = existingKost.daerah
            jarakKm = formatJarakKm(existingKost.jarakKm)
            hargaTahunan = existingKost.hargaTahunan.toString()
            tipeKos = existingKost.tipeKos
            kamarMandi = existingKost.kamarMandi
            wifi = existingKost.wifi
            furniturKasur = existingKost.furniturKasur
            furniturLemari = existingKost.furniturLemari
            furniturMejaBelajar = existingKost.furniturMejaBelajar
            fasilitasPendingin = existingKost.fasilitasPendingin
            areaLaundry = existingKost.areaLaundry
            areaDapur = existingKost.areaDapur
            keamananCctv = existingKost.keamananCctv
        }
    }

    LaunchedEffect(operationState) {
        if (operationState is OperationState.Success) {
            delay(1200)
            onClearOperation()
            onBack()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text(text = title, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        if (kostId != null && uiState is UiState.Loading) {
            LoadingState()
            return
        }
        if (kostId != null && uiState is UiState.Error) {
            ErrorState(message = uiState.message)
            return
        }
        if (kostId != null && uiState is UiState.Success && existingKost == null) {
            EmptyState(text = "Kost tidak ditemukan")
            return
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Informasi Umum
            Text(
                text = "Informasi Umum",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )
            FormField(label = "Nama Kost", value = namaKos, onValueChange = { namaKos = it })
            FormField(label = "Nomor Telepon", value = nomorTelepon, onValueChange = { nomorTelepon = it })
            SelectField(
                label = "Tipe Kos",
                value = tipeKos,
                options = listOf("Laki-laki", "Perempuan", "Campur"),
                onSelect = { tipeKos = it }
            )
            FormField(label = "Harga Tahunan (angka)", value = hargaTahunan, onValueChange = { hargaTahunan = it })

            // Lokasi
            Text(
                text = "Lokasi",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )
            SelectField(
                label = "Daerah",
                value = daerah,
                options = listOf("Belwis", "Airan", "Korpri"),
                onSelect = { daerah = it }
            )
            FormField(label = "Jarak ke Kampus (km)", value = jarakKm, onValueChange = { jarakKm = it })

            // Fasilitas Kamar
            Text(
                text = "Fasilitas Kamar",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )
            SelectField(
                label = "Kamar Mandi",
                value = kamarMandi,
                options = listOf("Dalam", "Luar"),
                onSelect = { kamarMandi = it }
            )
            SelectField(
                label = "Wifi",
                value = wifi,
                options = listOf("Ada", "Tidak Ada"),
                onSelect = { wifi = it }
            )
            SelectField(
                label = "Furnitur Kasur",
                value = furniturKasur,
                options = listOf("Ada", "Tidak Ada"),
                onSelect = { furniturKasur = it }
            )
            SelectField(
                label = "Furnitur Lemari",
                value = furniturLemari,
                options = listOf("Ada", "Tidak Ada"),
                onSelect = { furniturLemari = it }
            )
            SelectField(
                label = "Furnitur Meja Belajar",
                value = furniturMejaBelajar,
                options = listOf("Ada", "Tidak Ada"),
                onSelect = { furniturMejaBelajar = it }
            )
            SelectField(
                label = "Pendingin",
                value = fasilitasPendingin,
                options = listOf("AC", "Kipas Angin", "Tidak ada"),
                onSelect = { fasilitasPendingin = it }
            )

            // Fasilitas Umum & Keamanan
            Text(
                text = "Fasilitas Umum & Keamanan",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )
            SelectField(
                label = "Area Laundry",
                value = areaLaundry,
                options = listOf("Ada", "Tidak Ada"),
                onSelect = { areaLaundry = it }
            )
            SelectField(
                label = "Area Dapur",
                value = areaDapur,
                options = listOf("Ada", "Tidak Ada"),
                onSelect = { areaDapur = it }
            )
            SelectField(
                label = "Keamanan CCTV",
                value = keamananCctv,
                options = listOf("Ada", "Tidak Ada"),
                onSelect = { keamananCctv = it }
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        val isLoading = operationState is OperationState.Loading
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    val kost = buildKostFromForm(
                        existingKost = existingKost,
                        contributorId = contributorId,
                        namaKos = namaKos,
                        nomorTelepon = nomorTelepon,
                        daerah = daerah,
                        jarakKm = jarakKm,
                        hargaTahunan = hargaTahunan,
                        tipeKos = tipeKos,
                        kamarMandi = kamarMandi,
                        wifi = wifi,
                        furniturKasur = furniturKasur,
                        furniturLemari = furniturLemari,
                        furniturMejaBelajar = furniturMejaBelajar,
                        fasilitasPendingin = fasilitasPendingin,
                        areaLaundry = areaLaundry,
                        areaDapur = areaDapur,
                        keamananCctv = keamananCctv
                    )
                    onSave(kost)
                },
                enabled = !isLoading
            ) {
                Text(text = if (kostId == null) "Simpan" else "Update")
            }
            Button(onClick = onBack, enabled = !isLoading) {
                Text(text = "Batal")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        when (operationState) {
            is OperationState.Loading -> Text(
                text = "Memproses...",
                style = MaterialTheme.typography.bodySmall
            )
            is OperationState.Success -> Text(
                text = operationState.message,
                style = MaterialTheme.typography.bodySmall
            )
            is OperationState.Error -> Text(
                text = operationState.message,
                style = MaterialTheme.typography.bodySmall
            )
            OperationState.Idle -> Unit
        }
    }
}

@Composable
private fun FormField(label: String, value: String, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        modifier = Modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectField(
    label: String,
    value: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(text = label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun buildKostFromForm(
    existingKost: Kost?,
    contributorId: Long,
    namaKos: String,
    nomorTelepon: String,
    daerah: String,
    jarakKm: String,
    hargaTahunan: String,
    tipeKos: String,
    kamarMandi: String,
    wifi: String,
    furniturKasur: String,
    furniturLemari: String,
    furniturMejaBelajar: String,
    fasilitasPendingin: String,
    areaLaundry: String,
    areaDapur: String,
    keamananCctv: String
): Kost {
    return Kost(
        id = existingKost?.id ?: 0,
        contributorId = existingKost?.contributorId ?: contributorId,
        namaKos = namaKos,
        nomorTelepon = nomorTelepon.ifBlank { null },
        daerah = daerah,
        jarakKm = parseJarakKm(jarakKm),
        hargaTahunan = parseHargaTahunan(hargaTahunan),
        tipeKos = tipeKos,
        kamarMandi = kamarMandi,
        wifi = wifi,
        furniturKasur = furniturKasur,
        furniturLemari = furniturLemari,
        furniturMejaBelajar = furniturMejaBelajar,
        fasilitasPendingin = fasilitasPendingin,
        areaLaundry = areaLaundry,
        areaDapur = areaDapur,
        keamananCctv = keamananCctv,
        isFavorite = existingKost?.isFavorite ?: false
    )
}

private fun parseJarakKm(value: String): Double {
    return value.trim().replace(",", ".").toDoubleOrNull() ?: 0.0
}

private fun parseHargaTahunan(value: String): Long {
    return value.trim().replace(".", "").toLongOrNull() ?: 0L
}

private fun formatJarakKm(km: Double): String {
    return km.toString().replace(".", ",")
}
