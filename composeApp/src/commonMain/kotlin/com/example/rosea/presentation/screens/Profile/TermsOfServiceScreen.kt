package com.example.rosea.presentation.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsOfServiceScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Terms of Service", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Text(
                "Ketentuan Layanan ROSÉA",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Terakhir diperbarui: 12 Juni 2024",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            TermsSection(
                title = "1. Penerimaan Ketentuan",
                content = "Dengan mengunduh atau menggunakan aplikasi ROSÉA, Anda setuju untuk terikat oleh Ketentuan Layanan ini. Jika Anda tidak menyetujui bagian mana pun dari ketentuan ini, Anda tidak diperbolehkan menggunakan layanan kami."
            )

            TermsSection(
                title = "2. Penggunaan Akun",
                content = "Anda bertanggung jawab untuk menjaga kerahasiaan informasi akun dan kata sandi Anda. Anda menyetujui untuk bertanggung jawab atas semua aktivitas yang terjadi di bawah akun Anda."
            )

            TermsSection(
                title = "3. Transaksi dan Pembayaran",
                content = "Seluruh harga produk yang tertera adalah dalam Rupiah (IDR). Kami berhak mengubah harga sewaktu-waktu tanpa pemberitahuan sebelumnya. Pembayaran harus dilakukan melalui kanal resmi yang disediakan di dalam aplikasi."
            )

            TermsSection(
                title = "4. Kebijakan Pengembalian",
                content = "Pengembalian barang hanya diterima jika produk yang diterima rusak atau tidak sesuai dengan pesanan, dengan menyertakan video unboxing sebagai bukti yang sah."
            )

            TermsSection(
                title = "5. Batasan Tanggung Jawab",
                content = "ROSÉA tidak bertanggung jawab atas segala kerugian yang timbul dari penggunaan atau ketidakmampuan menggunakan aplikasi ini, atau atas segala perubahan yang dilakukan pada aplikasi."
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun TermsSection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 20.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 22.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
