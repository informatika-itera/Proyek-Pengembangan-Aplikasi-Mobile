package com.example.rosea.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoucherScreen(onNavigateBack: () -> Unit) {
    val vouchers = listOf(
        VoucherData("ROSEA50", "Diskon 50% hingga Rp 50.000", "Berlaku s/d 31 Des 2024"),
        VoucherData("FREEONGKIR", "Gratis Ongkir Min. Belanja Rp 0", "Berlaku s/d 30 Nov 2024"),
        VoucherData("BEAUTY20", "Potongan Langsung Rp 20.000", "Khusus kategori Skincare")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Vouchers", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(vouchers) { voucher ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.ConfirmationNumber,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(voucher.code, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                            Text(voucher.desc, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(voucher.expiry, fontSize = 12.sp, color = Color.Gray)
                        }
                        Button(
                            onClick = { },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("Pakai", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

data class VoucherData(val code: String, val desc: String, val expiry: String)
