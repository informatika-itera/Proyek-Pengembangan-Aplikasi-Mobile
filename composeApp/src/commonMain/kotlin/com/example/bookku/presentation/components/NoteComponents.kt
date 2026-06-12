package com.example.bookku.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.bookku.domain.model.Book
import com.example.bookku.domain.model.BookRating

@Composable
fun NoteCard(
    book: Book,
    onClick: () -> Unit,
    onPinClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
    isOwner: Boolean = false, // Parameter kunci untuk keamanan
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = Color(book.color.hexValue),
        label = "card_bg"
    )
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.height(140.dp).fillMaxWidth()) {
            AsyncImage(
                model = book.coverUrl.ifBlank { "https://via.placeholder.com/150x200?text=No+Cover" },
                contentDescription = null,
                modifier = Modifier.width(100.dp).fillMaxHeight().clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.weight(1f).padding(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(book.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        if (book.author.isNotBlank()) {
                            Text(book.author, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    
                    // Pin HANYA untuk pemilik
                    if (isOwner && onPinClick != null) {
                        IconButton(onClick = onPinClick, modifier = Modifier.size(24.dp)) {
                            Icon(
                                imageVector = if (book.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                                contentDescription = null,
                                tint = if (book.isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
                
                Text(book.preview, style = MaterialTheme.typography.bodySmall, maxLines = 2, modifier = Modifier.weight(1f).padding(top = 4.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    CategoryBadge(category = book.category.displayName)
                    
                    // Hapus HANYA untuk pemilik
                    if (isOwner && onDeleteClick != null) {
                        IconButton(onClick = onDeleteClick, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryBadge(category: String) {
    Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)) {
        Text(category, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
    }
}

@Composable
fun LoadingIndicator() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
}

@Composable
fun EmptyState(title: String, message: String, modifier: Modifier = Modifier, icon: @Composable (() -> Unit)? = null) {
    Column(modifier = modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        icon?.invoke()
        Spacer(modifier = Modifier.height(16.dp))
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(message, style = MaterialTheme.typography.bodyMedium, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}

@Composable
fun ErrorState(message: String, onRetry: (() -> Unit)? = null) {
    Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Oops!", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.error)
        Text(message, style = MaterialTheme.typography.bodyMedium, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        if (onRetry != null) {
            Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) { Text("Coba Lagi") }
        }
    }
}

@Composable
fun ColorPickerRow(selectedColor: BookRating, onColorSelected: (BookRating) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        BookRating.entries.forEach { color ->
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(color.hexValue))
                    .clickable { onColorSelected(color) }
                    .then(if (color == selectedColor) Modifier.background(Color.Black.copy(alpha = 0.2f), CircleShape) else Modifier),
                contentAlignment = Alignment.Center
            ) {
                if (color == selectedColor) Icon(Icons.Default.Check, null, tint = if (color == BookRating.DEFAULT) Color.Black else Color.White, modifier = Modifier.size(16.dp))
            }
        }
    }
}
