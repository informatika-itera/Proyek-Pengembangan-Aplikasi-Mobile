package com.example.rosea.presentation.screens.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportScreen(onNavigateBack: () -> Unit) {
    val faqs = listOf(
        "Bagaimana cara melacak pesanan saya?" to "Anda dapat melihat status pengiriman di menu 'History' pada halaman profil Anda.",
        "Berapa lama waktu pengiriman?" to "Waktu pengiriman biasanya memakan waktu 2-5 hari kerja tergantung lokasi pengiriman dan jasa kurir yang dipilih.",
        "Apakah produk ROSÉA aman untuk kulit sensitif?" to "Ya, seluruh produk kami telah melalui uji klinis dan menggunakan bahan alami yang ramah untuk segala jenis kulit, termasuk kulit sensitif.",
        "Cara menggunakan voucher diskon?" to "Anda dapat memasukkan kode voucher pada halaman checkout di kolom 'Punya kode promo?' sebelum melakukan pembayaran."
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help & Support", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Paling Sering Ditanyakan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(4.dp))
            }

            items(faqs) { (question, answer) ->
                FAQItem(question, answer)
            }

            item {
                Spacer(Modifier.height(24.dp))
                ContactUsSection()
            }
        }
    }
}

@Composable
private fun FAQItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (expanded) 180f else 0f)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 0.5.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (expanded) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        onClick = { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.AutoMirrored.Outlined.HelpOutline,
                    null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = question,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (expanded) FontWeight.Bold else FontWeight.SemiBold
                    ),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.Default.ExpandMore,
                    null,
                    modifier = Modifier.rotate(rotation),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            }
            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(Modifier.height(12.dp))
                    Divider(color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = answer,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactUsSection() {
    Column {
        Text(
            "Hubungi Kami",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            "Tim kami siap membantu kendala Anda 24/7",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ContactCard(
                icon = Icons.Outlined.ChatBubbleOutline,
                label = "Live Chat",
                subLabel = "Balas Cepat",
                modifier = Modifier.weight(1f),
                color = Color(0xFFE3F2FD),
                iconColor = Color(0xFF1E88E5)
            )
            ContactCard(
                icon = Icons.Outlined.Email,
                label = "Email",
                subLabel = "24 Jam Kerja",
                modifier = Modifier.weight(1f),
                color = Color(0xFFFFF3E0),
                iconColor = Color(0xFFFB8C00)
            )
        }
        
        Spacer(Modifier.height(12.dp))
        
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { },
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 0.5.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = CircleShape,
                    color = Color(0xFFE8F5E9)
                ) {
                    Icon(
                        Icons.Outlined.Call,
                        null,
                        modifier = Modifier.padding(10.dp),
                        tint = Color(0xFF43A047)
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Call Center", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("0800-1-ROSEA (Bebas Pulsa)", fontSize = 12.sp, color = Color.Gray)
                }
                Icon(
                    Icons.AutoMirrored.Outlined.Chat,
                    null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
            }
        }
    }
}

@Composable
private fun ContactCard(
    icon: ImageVector,
    label: String,
    subLabel: String,
    color: Color,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable { },
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 0.5.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = color
            ) {
                Icon(icon, null, modifier = Modifier.padding(10.dp), tint = iconColor)
            }
            Spacer(Modifier.height(16.dp))
            Text(label, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(subLabel, fontSize = 11.sp, color = Color.Gray)
        }
    }
}
