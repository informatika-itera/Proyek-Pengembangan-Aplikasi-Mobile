package com.example.noteai.presentation.components.hujjah

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

enum class HujjahMenuItem {
    LENS,
    RESULT,
    DETAIL,
    BOOKMARK
}

@Composable
fun HujjahSprint2MenuBar(
    currentItem: HujjahMenuItem,
    onNavigateToLens: () -> Unit,
    onNavigateToResult: () -> Unit,
    onNavigateToDetail: () -> Unit,
    onNavigateToBookmarks: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = HujjahUiColors.White,
        shadowElevation = 8.dp,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HujjahMenuButton(
                text = "Lens",
                selected = currentItem == HujjahMenuItem.LENS,
                onClick = onNavigateToLens,
                modifier = Modifier.weight(1f)
            )
            HujjahMenuButton(
                text = "Hasil",
                selected = currentItem == HujjahMenuItem.RESULT,
                onClick = onNavigateToResult,
                modifier = Modifier.weight(1f)
            )
            HujjahMenuButton(
                text = "Detail",
                selected = currentItem == HujjahMenuItem.DETAIL,
                onClick = onNavigateToDetail,
                modifier = Modifier.weight(1f)
            )
            HujjahMenuButton(
                text = "Simpan",
                selected = currentItem == HujjahMenuItem.BOOKMARK,
                onClick = onNavigateToBookmarks,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun HujjahMenuButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = if (selected) HujjahUiColors.White else HujjahUiColors.PrimaryDark,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        maxLines = 1,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) HujjahUiColors.Primary else HujjahUiColors.Mint)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp)
    )
}
