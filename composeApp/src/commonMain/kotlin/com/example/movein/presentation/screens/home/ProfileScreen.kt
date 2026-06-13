package com.example.movein.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen(
    isLight: Boolean,
    setIsLight: (Boolean) -> Unit,
    userName: String,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditing by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf(userName) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar Section
        Box(modifier = Modifier.size(96.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(if (isLight) Color(0xFFF3F4F6) else Color(0xFF262626))
                    .border(4.dp, if (isLight) Color.White else Color(0xFF171717), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🥸", fontSize = 40.sp)
            }
            
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 8.dp, y = 4.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (isLight) Color(0xFF171717) else Color.White)
                    .clickable { isEditing = !isEditing },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                    contentDescription = null,
                    tint = if (isLight) Color.White else Color.Black,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isEditing) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.width(200.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = if (isLight) Color.White else Color(0xFF171717),
                    unfocusedContainerColor = if (isLight) Color.White else Color(0xFF171717),
                    focusedBorderColor = if (isLight) Color(0xFFD1D5DB) else Color.White.copy(alpha = 0.2f),
                    unfocusedBorderColor = if (isLight) Color(0xFFE5E7EB) else Color.White.copy(alpha = 0.1f)
                )
            )
        } else {
            Text(
                text = name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (isLight) Color(0xFF171717) else Color.White
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Menu List
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Theme Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isLight) Color.White else Color(0xFF171717).copy(alpha = 0.5f))
                    .border(1.dp, if (isLight) Color(0xFFE5E7EB) else Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isLight) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = null,
                        tint = if (isLight) Color(0xFF737373) else Color(0xFFA3A3A3),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "App Theme",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isLight) Color(0xFF374151) else Color(0xFFD1D5DB)
                    )
                }
                
                Switch(
                    checked = isLight,
                    onCheckedChange = { setIsLight(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF3B82F6),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFF404040)
                    )
                )
            }

            // Settings Item
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isLight) Color.White else Color(0xFF171717).copy(alpha = 0.5f))
                    .border(1.dp, if (isLight) Color(0xFFE5E7EB) else Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                    .clickable { }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Settings,
                    null,
                    tint = if (isLight) Color(0xFF737373) else Color(0xFFA3A3A3),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Pengaturan Akun",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isLight) Color(0xFF374151) else Color(0xFFD1D5DB)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Logout Item
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isLight) Color(0xFFFEF2F2) else Color(0xFF450A0A).copy(alpha = 0.2f))
                    .border(1.dp, if (isLight) Color(0xFFFEE2E2) else Color(0xFF7F1D1D).copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .clickable { onLogout() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Logout, null, tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text("Keluar (Logout)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
            }
        }
    }
}
