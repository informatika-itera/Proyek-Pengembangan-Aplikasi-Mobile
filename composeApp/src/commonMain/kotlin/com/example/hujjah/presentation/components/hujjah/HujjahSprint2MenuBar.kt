package com.example.hujjah.presentation.components.hujjah

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hujjah.presentation.theme.LocalHujjahColors

enum class HujjahMenuItem {
    HOME,
    LENS,
    QURAN,
    HADITH,
    PROFILE
}

@Composable
fun HujjahSprint2MenuBar(
    currentItem: HujjahMenuItem,
    onNavigateToHome: () -> Unit,
    onNavigateToLens: () -> Unit,
    onNavigateToQuran: () -> Unit,
    onNavigateToHadith: () -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalHujjahColors.current

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = if (colors.isDarkTheme) Color.Black else MaterialTheme.colorScheme.surface,
        shadowElevation = 16.dp,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HujjahTabButton(
                label = "Beranda",
                icon = Icons.Default.Home,
                selected = currentItem == HujjahMenuItem.HOME,
                onClick = onNavigateToHome,
                colors = colors
            )
            HujjahTabButton(
                label = "Lens",
                icon = Icons.Default.Forum,
                selected = currentItem == HujjahMenuItem.LENS,
                onClick = onNavigateToLens,
                colors = colors
            )
            HujjahTabButton(
                label = "Al-Qur'an",
                icon = Icons.Default.Book,
                selected = currentItem == HujjahMenuItem.QURAN,
                onClick = onNavigateToQuran,
                colors = colors
            )
            HujjahTabButton(
                label = "Hadits",
                icon = Icons.Default.MenuBook,
                selected = currentItem == HujjahMenuItem.HADITH,
                onClick = onNavigateToHadith,
                colors = colors
            )
            HujjahTabButton(
                label = "Profil",
                icon = Icons.Default.Person,
                selected = currentItem == HujjahMenuItem.PROFILE,
                onClick = onNavigateToProfile,
                colors = colors
            )
        }
    }
}

@Composable
private fun HujjahTabButton(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    colors: com.example.hujjah.presentation.theme.HujjahColors
) {
    val activeColor = colors.goldHighlight
    val inactiveColor = if (colors.isDarkTheme) Color.White.copy(alpha = 0.5f) else colors.islamicGreen.copy(alpha = 0.5f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp, horizontal = 12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) activeColor else inactiveColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) activeColor else inactiveColor,
            textAlign = TextAlign.Center
        )
    }
}
