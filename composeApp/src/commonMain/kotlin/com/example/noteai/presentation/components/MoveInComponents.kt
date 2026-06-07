package com.example.noteai.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noteai.presentation.theme.MoveInTheme

@Composable
fun MoveInSectionTitle(
    title: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MoveInTheme.colors.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Column {
            Text(
                text = title,
                style = MoveInTheme.typography.headlineMedium.copy(
                    color = MoveInTheme.colors.textPrimary
                )
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MoveInTheme.typography.bodyMedium.copy(
                        color = MoveInTheme.colors.textSecondary
                    )
                )
            }
        }
    }
}

@Composable
fun MoveInPill(
    text: String,
    selected: Boolean,
    color: Color = MoveInTheme.colors.primary,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        color = if (selected) color else MoveInTheme.colors.surfaceSecondary,
        contentColor = if (selected) Color.White else MoveInTheme.colors.textSecondary
    ) {
        Text(
            text = text,
            style = MoveInTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun MoveInIconCircle(
    icon: ImageVector,
    selected: Boolean,
    size: Dp = 44.dp,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(if (selected) MoveInTheme.colors.primary else MoveInTheme.colors.surfaceSecondary)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) Color.White else MoveInTheme.colors.textSecondary,
            modifier = Modifier.size(size * 0.5f)
        )
    }
}

@Composable
fun MoveInPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    small: Boolean = false,
    danger: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (danger) MoveInTheme.colors.errorRed else MoveInTheme.colors.primary,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(if (small) 12.dp else 16.dp),
        contentPadding = if (small) PaddingValues(horizontal = 16.dp, vertical = 8.dp) 
                         else PaddingValues(horizontal = 24.dp, vertical = 12.dp)
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(if (small) 16.dp else 20.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = if (small) MoveInTheme.typography.labelSmall else MoveInTheme.typography.labelLarge
        )
    }
}

@Composable
fun MoveInSegmentedControl(
    options: List<String>,
    selectedOption: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(MoveInTheme.colors.surfaceSecondary)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        options.forEach { option ->
            val isSelected = option == selectedOption
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) MoveInTheme.colors.surfacePrimary else Color.Transparent)
                    .clickable { onSelected(option) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    style = MoveInTheme.typography.labelSmall.copy(
                        color = if (isSelected) MoveInTheme.colors.textPrimary else MoveInTheme.colors.textMuted
                    )
                )
            }
        }
    }
}
