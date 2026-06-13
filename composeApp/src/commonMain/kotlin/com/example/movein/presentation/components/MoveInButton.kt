package com.example.movein.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

import com.example.movein.presentation.theme.MoveInTheme

@Composable
fun MoveInPrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MoveInTheme.colors.primary,
            contentColor = Color.White,
            disabledContainerColor = MoveInTheme.colors.surfaceSecondary.copy(alpha = 0.5f),
            disabledContentColor = MoveInTheme.colors.textMuted
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Text(
            text = text,
            style = MoveInTheme.typography.labelLarge
        )
    }
}

@Composable
fun MoveInSecondaryButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        border = BorderStroke(1.dp, MoveInTheme.colors.borderSubtle),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
    ) {
        Text(
            text = text,
            style = MoveInTheme.typography.labelLarge.copy(color = MoveInTheme.colors.textSecondary)
        )
    }
}

@Composable
fun MoveInTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(label, style = MoveInTheme.typography.bodyMedium)
        },
        singleLine = true,
        visualTransformation = if (isPassword) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MoveInTheme.colors.textPrimary,
            unfocusedTextColor = MoveInTheme.colors.textPrimary,
            focusedLabelColor = MoveInTheme.colors.primary,
            unfocusedLabelColor = MoveInTheme.colors.textMuted,
            focusedBorderColor = MoveInTheme.colors.primary,
            unfocusedBorderColor = MoveInTheme.colors.borderSubtle,
            cursorColor = MoveInTheme.colors.primary,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(18.dp),
        modifier = modifier.fillMaxWidth()
    )
}
