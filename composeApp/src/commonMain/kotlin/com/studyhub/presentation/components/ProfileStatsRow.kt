package com.studyhub.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.studyhub.presentation.theme.Spacing

@Composable
fun ProfileStatsRow(
    overdueCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.normal),
        horizontalArrangement = Arrangement.spacedBy(Spacing.normal)
    ) {
        QuickStatCard(
            title = "Terlambat",
            value = overdueCount.toString(),
            subtitle = "tugas",
            icon = Icons.Default.Warning,
            modifier = Modifier.weight(1f),
            containerColor = if (overdueCount > 0) MaterialTheme.colorScheme.errorContainer 
                             else MaterialTheme.colorScheme.surfaceVariant
        )
        // You can add more stats here if needed, matching the prompt's simplicity
    }
}
