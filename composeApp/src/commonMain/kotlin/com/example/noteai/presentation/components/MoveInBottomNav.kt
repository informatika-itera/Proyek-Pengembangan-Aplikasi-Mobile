package com.example.noteai.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun MoveInBottomNav(
    activeTab: String,
    onTabSelected: (String) -> Unit,
    isLight: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(CircleShape)
            .background(
                if (isLight) Color.White.copy(alpha = 0.9f) else Color(0xFF181818).copy(alpha = 0.9f)
            )
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavIcon(
                id = "home",
                icon = Icons.Default.Home,
                isActive = activeTab == "home",
                isLight = isLight,
                onClick = { onTabSelected("home") }
            )
            NavIcon(
                id = "journey",
                icon = Icons.Default.History, // Activity equivalent
                isActive = activeTab == "journey",
                isLight = isLight,
                onClick = { onTabSelected("journey") }
            )

            // Center Button (Detox)
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .offset(y = (-20).dp)
                    .clip(CircleShape)
                    .background(
                        if (activeTab == "detox") {
                            if (isLight) Color(0xFF3B82F6) else Color.White.copy(alpha = 0.2f)
                        } else {
                            if (isLight) Color(0xFFF3F4F6) else Color(0xFF262626)
                        }
                    )
                    .clickable { onTabSelected("detox") },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Air, // Wind equivalent
                    contentDescription = null,
                    tint = if (activeTab == "detox") Color.White else (if (isLight) Color(0xFF525252) else Color.White),
                    modifier = Modifier.size(22.dp)
                )
            }

            NavIcon(
                id = "quests",
                icon = Icons.Default.Spa, // Sprout equivalent
                isActive = activeTab == "quests",
                isLight = isLight,
                onClick = { onTabSelected("quests") }
            )
            NavIcon(
                id = "profile",
                icon = Icons.Default.Person, // User equivalent
                isActive = activeTab == "profile",
                isLight = isLight,
                onClick = { onTabSelected("profile") }
            )
        }
    }
}

@Composable
private fun NavIcon(
    id: String,
    icon: ImageVector,
    isActive: Boolean,
    isLight: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(56.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isActive) {
                if (isLight) Color(0xFF171717) else Color.White
            } else {
                if (isLight) Color(0xFFA3A3A3) else Color(0xFF737373)
            },
            modifier = Modifier.size(20.dp)
        )
    }
}
