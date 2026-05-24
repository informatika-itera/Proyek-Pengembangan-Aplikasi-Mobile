package com.example.rosea.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // 👈 Tambahan Import Panah
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 👇 Parameter onNavigateBack ditambahkan di sini
@Composable
fun ProfileScreen(onNavigateBack: () -> Unit) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .verticalScroll(scrollState)
    ) {
        // 👇 Parameter dilempar ke ProfileHeader
        ProfileHeader(onNavigateBack = onNavigateBack)
        Spacer(modifier = Modifier.height(16.dp))
        OrderActivitySection()
        Spacer(modifier = Modifier.height(16.dp))
        MenuSection()
        Spacer(modifier = Modifier.height(32.dp))
    }
}

// 👇 Parameter ditangkap oleh Header untuk menjalankan tombol
@Composable
private fun ProfileHeader(onNavigateBack: () -> Unit) {
    // Bagian Header dengan warna dominan (misal warna Primary ROSÉA)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            )
            .padding(start = 16.dp, end = 24.dp, top = 32.dp, bottom = 32.dp)
    ) {
        Column {
            // 👇 Ini tombol panah kembalinya
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                // Placeholder Foto Profil
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Avatar",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Info Pengguna
                Column {
                    Text(
                        text = "Roséanne Park",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // Badge Membership ala Sociolla
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "💎 ROSÉA VIP Member",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderActivitySection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Aktivitas Pesanan",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OrderStatusItem(icon = Icons.Default.ShoppingCart, label = "Belum Bayar")
                OrderStatusItem(icon = Icons.Default.Build, label = "Dikemas")
                OrderStatusItem(icon = Icons.Default.Send, label = "Dikirim")
                OrderStatusItem(icon = Icons.Default.Star, label = "Penilaian")
            }
        }
    }
}

@Composable
private fun OrderStatusItem(icon: ImageVector, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { /* TODO: Navigasi ke daftar pesanan */ }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
private fun MenuSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            MenuItem(icon = Icons.Default.AccountCircle, title = "Profil Kecantikan")
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            MenuItem(icon = Icons.Default.LocationOn, title = "Daftar Alamat")
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            MenuItem(icon = Icons.Default.Favorite, title = "Wishlist")
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            MenuItem(icon = Icons.Default.Settings, title = "Pengaturan Akun")
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            // Tombol Logout dengan gaya berbeda
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* TODO: Aksi Logout */ }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Keluar",
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Keluar",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun MenuItem(icon: ImageVector, title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: Navigasi ke menu spesifik */ }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Pergi",
            tint = Color.LightGray
        )
    }
}