package com.kosthub.app.presentation.screens.contribute

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kosthub.app.domain.model.Kost
import com.kosthub.app.presentation.components.EmptyState
import com.kosthub.app.presentation.components.ErrorState
import com.kosthub.app.presentation.components.KostManageCard
import com.kosthub.app.presentation.components.LoadingState
import com.kosthub.app.presentation.state.UiState
import com.kosthub.app.presentation.viewmodel.ProfileViewModel

@Composable
fun ContributeScreen(
    uiState: UiState<List<Kost>>,
    profileViewModel: ProfileViewModel,
    onNavigateAdd: () -> Unit,
    onNavigateEdit: (Long) -> Unit,
    onNavigateDelete: (Long) -> Unit
) {
    val profileState by profileViewModel.uiState.collectAsState()
    val contributorId = (profileState as? UiState.Success)?.data?.id ?: 1L
    val contributorName = (profileState as? UiState.Success)?.data?.name ?: "Kontributor"
    val contributorEmail = (profileState as? UiState.Success)?.data?.email ?: ""

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Kontribusi Kost", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Kontributor: $contributorName ${if (contributorEmail.isBlank()) "" else "($contributorEmail)"}",
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onNavigateAdd) {
            Text(text = "Tambah Kost")
        }
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            when (uiState) {
                is UiState.Loading -> item { LoadingState() }
                is UiState.Error -> item { ErrorState(message = uiState.message) }
                is UiState.Empty -> item { EmptyState(text = "Belum ada kost yang kamu tambahkan") }
                is UiState.Success -> {
                    val myItems = uiState.data.filter { it.contributorId == contributorId }
                    if (myItems.isEmpty()) {
                        item { EmptyState(text = "Belum ada kost yang kamu tambahkan") }
                    } else {
                        items(myItems) { kost ->
                            KostManageCard(
                                kost = kost,
                                onEdit = { onNavigateEdit(it.id) },
                                onDelete = onNavigateDelete
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun buildKostFromForm(
    editingKost: Kost?,
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
        id = editingKost?.id ?: 0,
        contributorId = contributorId,
        namaKos = namaKos,
        nomorTelepon = nomorTelepon.ifBlank { null },
        daerah = daerah,
        jarakKm = parseJarakKm(jarakKm),
        hargaTahunan = hargaTahunan.toLongOrNull() ?: 0L,
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
        isFavorite = editingKost?.isFavorite ?: false
    )
}

private fun parseJarakKm(value: String): Double {
    return value.trim().replace(",", ".").toDoubleOrNull() ?: 0.0
}

private fun formatJarakKm(km: Double): String {
    return km.toString().replace(".", ",")
}

private fun clearForm(
    onClearEditing: () -> Unit,
    onNamaKos: (String) -> Unit,
    onNomorTelepon: (String) -> Unit,
    onDaerah: (String) -> Unit,
    onJarakKm: (String) -> Unit,
    onHargaTahunan: (String) -> Unit,
    onTipeKos: (String) -> Unit,
    onKamarMandi: (String) -> Unit,
    onWifi: (String) -> Unit,
    onFurniturKasur: (String) -> Unit,
    onFurniturLemari: (String) -> Unit,
    onFurniturMejaBelajar: (String) -> Unit,
    onFasilitasPendingin: (String) -> Unit,
    onAreaLaundry: (String) -> Unit,
    onAreaDapur: (String) -> Unit,
    onKeamananCctv: (String) -> Unit
) {
    onClearEditing()
    onNamaKos("")
    onNomorTelepon("")
    onDaerah("")
    onJarakKm("")
    onHargaTahunan("")
    onTipeKos("")
    onKamarMandi("")
    onWifi("")
    onFurniturKasur("")
    onFurniturLemari("")
    onFurniturMejaBelajar("")
    onFasilitasPendingin("")
    onAreaLaundry("")
    onAreaDapur("")
    onKeamananCctv("")
}
