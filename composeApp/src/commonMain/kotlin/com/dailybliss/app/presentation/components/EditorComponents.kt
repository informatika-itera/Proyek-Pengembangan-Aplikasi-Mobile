package com.dailybliss.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage

@Composable
fun TextBlockItem(
    text: String,
    onTextChange: (String) -> Unit,
    onRemove: () -> Unit,
    onEnterPressed: () -> Unit,
    onAttachMedia: () -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.Top
    ) {
        IconButton(
            onClick = onAttachMedia,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Attach Media",
                tint = Color.LightGray.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(4.dp))

        BasicTextField(
            value = text,
            onValueChange = {
                if (it.endsWith("\n")) {
                    onEnterPressed()
                } else {
                    onTextChange(it)
                }
            },
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 4.dp)
                .focusRequester(focusRequester)
                .onKeyEvent { keyEvent ->
                    if (keyEvent.type == KeyEventType.KeyDown && 
                        keyEvent.key == Key.Backspace && 
                        text.isEmpty()) {
                        onRemove()
                        true
                    } else {
                        false
                    }
                },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = Color.DarkGray,
                lineHeight = 26.sp
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { innerTextField ->
                if (text.isEmpty()) {
                    Text(
                        text = "Tulis cerita...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.LightGray.copy(alpha = 0.5f)
                    )
                }
                innerTextField()
            }
        )

        IconButton(
            onClick = onRemove,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Remove Block",
                tint = Color.LightGray.copy(alpha = 0.4f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun ImageBlockItem(
    url: String,
    showAddBlockButton: Boolean,
    onRemove: () -> Unit,
    onAddBlockBelow: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(start = 36.dp, end = 36.dp)) {
            Box(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 500.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Fit
                )
                
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .size(28.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Remove Image",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            if (showAddBlockButton) {
                TextButton(
                    onClick = onAddBlockBelow,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.LightGray)
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Tambah blok baru di sini", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}
