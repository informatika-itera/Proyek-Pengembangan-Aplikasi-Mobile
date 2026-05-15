package com.example.mybawanggacha.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mybawanggacha.data.remote.dto.AnimeEntry

@Composable
fun AnimeHorizontalSection(
    title: String,
    anime: List<AnimeEntry>,
    onViewAllClick: () -> Unit,
    onAnimeClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    SectionHeader(
        title = title,
        onViewAllClick = onViewAllClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(
            items = anime,
            key = { it.mal_id }
        ) { item ->
            AnimePosterCard(
                title = item.title,
                imageUrl = item.images.jpg.large_image_url,
                onClick = { onAnimeClick(item.mal_id) }
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier,
    actionText: String = "→"
) {
    Row(
        modifier = modifier.padding(bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = actionText,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable(onClick = onViewAllClick)
        )
    }
}
