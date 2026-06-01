package com.mywallet.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mywallet.presentation.components.AppLogoIcon

@Composable
fun LoginScreen(
    onAuthenticated: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // Menggunakan latar putih
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            AppLogoIcon(
                modifier = Modifier.size(150.dp),
                useOuterRing = true // Mengaktifkan ring yang sekarang sudah biru tua
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                "MyWallet",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                color = Color(0xFF001F3F) // DarkNavy
            )
            
            Text(
                "Aplikasi Terkunci",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF001F3F).copy(alpha = 0.6f)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Button(
                onClick = onAuthenticated,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = CircleShape
            ) {
                Icon(Icons.Default.Fingerprint, contentDescription = null)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Buka dengan Biometrik", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
