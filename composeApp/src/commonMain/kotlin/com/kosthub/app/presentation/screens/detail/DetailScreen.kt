package com.kosthub.app.presentation.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.kosthub.app.domain.model.Kost
import com.kosthub.app.presentation.components.EmptyState
import com.kosthub.app.presentation.components.ErrorState
import com.kosthub.app.presentation.components.LoadingState
import com.kosthub.app.presentation.state.UiState

@Composable
fun DetailScreen(
    kostId: Long,
    uiState: UiState<List<Kost>>,
    onBack: () -> Unit,
    onToggleFavorite: (Kost) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Back Button & Screen Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Kembali"
                )
            }
            Text(
                text = "Detail Kost",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        when (uiState) {
            is UiState.Loading -> LoadingState()
            is UiState.Error -> ErrorState(message = uiState.message)
            is UiState.Empty -> EmptyState(text = "Belum ada data kost")
            is UiState.Success -> {
                val kost = uiState.data.firstOrNull { it.id == kostId }
                if (kost == null) {
                    EmptyState(text = "Kost tidak ditemukan")
                } else {
                    DetailContent(kost = kost, onToggleFavorite = onToggleFavorite)
                }
            }
        }
    }
}

@Composable
private fun DetailContent(
    kost: Kost,
    onToggleFavorite: (Kost) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // Premium AsyncImage from Placehold.co
        val encodedName = kost.namaKos.replace(" ", "+")
        val imageUrl = "https://placehold.co/600x400/4f378b/ffffff?text=$encodedName"
        AsyncImage(
            model = imageUrl,
            contentDescription = kost.namaKos,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Title and Favorite Toggle Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = kost.namaKos,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { onToggleFavorite(kost) }) {
                Icon(
                    imageVector = if (kost.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorit",
                    tint = if (kost.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Location Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${formatJarakKm(kost.jarakKm)} km dari Kampus",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Price Section
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = formatHargaTahunan(kost.hargaTahunan),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = " / tahun",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Specs Grid
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SpecCard(
                label = "Tipe Kost",
                value = kost.tipeKos,
                icon = Icons.Outlined.Group,
                modifier = Modifier.weight(1f)
            )
            SpecCard(
                label = "Kamar Mandi",
                value = "Mandi ${kost.kamarMandi}",
                icon = Icons.Outlined.Bathtub,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Facilities Section
        Text(
            text = "Fasilitas Kost",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        val facilities = getFacilityItems(kost)
        val availableFacilities = facilities.filter { it.isAvailable }

        if (availableFacilities.isEmpty()) {
            Text(
                text = "Tidak ada fasilitas spesifik",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            // Group into grid rows of 2 columns
            availableFacilities.chunked(2).forEach { rowItems ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    rowItems.forEach { item ->
                        FacilityItemRow(
                            label = item.label,
                            icon = item.icon,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowItems.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Contact Section
        Text(
            text = "Kontak Pengelola",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Pemilik Kost",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = kost.nomorTelepon ?: "Tidak ada nomor telepon",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                kost.nomorTelepon?.let { phone ->
                    if (phone.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                // Dialer action is not platform-agnostic, we can use a callback or standard indicator
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Phone,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Hubungi Pemilik")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SpecCard(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun FacilityItemRow(
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private data class FacilityItem(
    val label: String,
    val icon: ImageVector,
    val isAvailable: Boolean
)

private fun getFacilityItems(kost: Kost): List<FacilityItem> {
    return listOf(
        FacilityItem("WiFi", Icons.Outlined.Wifi, kost.wifi == "Ada"),
        FacilityItem(
            if (kost.fasilitasPendingin == "AC") "AC" else "Kipas Angin",
            Icons.Outlined.AcUnit,
            kost.fasilitasPendingin != "Tidak ada"
        ),
        FacilityItem("Kasur", Icons.Outlined.Bed, kost.furniturKasur == "Ada"),
        FacilityItem("Lemari", Icons.Outlined.MeetingRoom, kost.furniturLemari == "Ada"),
        FacilityItem("Meja Belajar", Icons.Outlined.TableRestaurant, kost.furniturMejaBelajar == "Ada"),
        FacilityItem("Laundry", Icons.Outlined.LocalLaundryService, kost.areaLaundry == "Ada"),
        FacilityItem("Dapur", Icons.Outlined.Kitchen, kost.areaDapur == "Ada"),
        FacilityItem("CCTV", Icons.Outlined.Security, kost.keamananCctv == "Ada")
    )
}

private fun formatJarakKm(km: Double): String {
    return km.toString().replace(".", ",")
}

private fun formatHargaTahunan(value: Long): String {
    val digits = value.toString()
    val grouped = digits.reversed().chunked(3).joinToString(".").reversed()
    return "Rp$grouped"
}
