package com.example.raillog.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.raillog.presentation.theme.RailLogColors

@Composable
fun RailLogSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        placeholder = {
            Text(placeholder)
        },
        leadingIcon = {
            Icon(Icons.Default.Search, null)
        },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(
                    onClick = {
                        onValueChange("")
                    }
                ) {
                    Icon(Icons.Default.Close, null)
                }
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = RailLogColors.PrimaryAction,
            unfocusedBorderColor = Color.Gray
        ),
        singleLine = true
    )
}