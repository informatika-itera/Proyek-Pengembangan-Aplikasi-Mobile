package id.my.sinanonym.mybawanggacha.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MediaDetailActionMenu(
    isInLibrary: Boolean,
    onOpenAi: () -> Unit,
    onOpenLibrary: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (expanded) {
                SmallFloatingActionButton(
                    onClick = {
                        expanded = false
                        onOpenAi()
                    },
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "AI Assistant"
                    )
                }

                SmallFloatingActionButton(
                    onClick = {
                        expanded = false
                        onOpenLibrary()
                    },
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Icon(
                        imageVector = if (isInLibrary) Icons.Default.Edit else Icons.Default.Add,
                        contentDescription = if (isInLibrary) {
                            "Edit My Library"
                        } else {
                            "Tambah ke My Library"
                        }
                    )
                }
            }

            FloatingActionButton(
                onClick = { expanded = !expanded },
                shape = CircleShape
            ) {
                Icon(
                    imageVector = if (expanded) Icons.Default.Close else Icons.Default.MoreVert,
                    contentDescription = if (expanded) "Tutup menu aksi" else "Buka menu aksi"
                )
            }
        }
    }
}
