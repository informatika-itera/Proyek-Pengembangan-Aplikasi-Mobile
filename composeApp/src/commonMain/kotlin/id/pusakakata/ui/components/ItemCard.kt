package id.pusakakata.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.pusakakata.domain.model.Word

@Composable
fun ItemCard(
    word: Word,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        ListItem(
            headlineContent = { 
                Text(word.term, style = MaterialTheme.typography.titleLarge) 
            },
            supportingContent = { 
                Text(
                    word.definition, 
                    maxLines = 2,
                    style = MaterialTheme.typography.bodyMedium 
                ) 
            },
            overlineContent = {
                Text(word.category, style = MaterialTheme.typography.labelSmall)
            },
            trailingContent = {
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete, 
                        contentDescription = "Hapus",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            },
            colors = ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
        )
    }
}
