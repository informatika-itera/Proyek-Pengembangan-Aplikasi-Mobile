package com.example.raillog.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.raillog.domain.model.Priority
import com.example.raillog.domain.model.SupplyStatus
import com.example.raillog.domain.model.VerificationStatus
import com.example.raillog.presentation.theme.RailLogColors
import com.example.raillog.presentation.theme.Spacing

// ── Page scaffold helper ────────────────────────────────────────────────────

@Composable
fun PageHeader(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = RailLogColors.TextPrimary
        )
        if (subtitle != null) {
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = RailLogColors.TextSecondary
            )
        }
    }
}

// ── Card ────────────────────────────────────────────────────────────────────

@Composable
fun SurfaceCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val base = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(RailLogColors.Surface)
        .border(1.dp, RailLogColors.BorderDefault, RoundedCornerShape(12.dp))
        .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
        .padding(Spacing.cardPadding)
        .then(modifier)

    Column(modifier = base, content = content)
}

// ── Divider ─────────────────────────────────────────────────────────────────

@Composable
fun SubtleDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier,
        thickness = 1.dp,
        color = RailLogColors.BorderSubtle
    )
}

// ── Search field ────────────────────────────────────────────────────────────

@Composable
fun RailLogSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(placeholder, style = MaterialTheme.typography.bodyMedium,
                color = RailLogColors.TextTertiary)
        },
        leadingIcon = {
            Icon(Icons.Default.Search, null,
                tint = RailLogColors.TextTertiary, modifier = Modifier.size(18.dp))
        },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, null,
                        tint = RailLogColors.TextTertiary, modifier = Modifier.size(16.dp))
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = RailLogColors.PrimaryAction,
            unfocusedBorderColor = RailLogColors.BorderDefault,
            focusedContainerColor   = RailLogColors.Surface,
            unfocusedContainerColor = RailLogColors.Surface,
            focusedTextColor     = RailLogColors.TextPrimary,
            unfocusedTextColor   = RailLogColors.TextPrimary
        )
    )
}

// ── Status badge ────────────────────────────────────────────────────────────

@Composable
fun StatusBadge(status: SupplyStatus) {
    val (bg, text) = when (status) {
        SupplyStatus.VERIFIED   -> RailLogColors.Success100 to RailLogColors.Success600
        SupplyStatus.PENDING    -> RailLogColors.Warning100 to RailLogColors.Warning600
        SupplyStatus.REJECTED   -> RailLogColors.Danger100  to RailLogColors.Danger600
        SupplyStatus.IN_TRANSIT -> RailLogColors.Info100    to RailLogColors.Info600
        SupplyStatus.RECEIVED   -> RailLogColors.Brand100   to RailLogColors.Brand600
    }
    BadgePill(label = status.displayName, bg = bg, textColor = text)
}

@Composable
fun PriorityBadge(priority: Priority) {
    val (bg, text) = when (priority) {
        Priority.CRITICAL -> RailLogColors.Danger100  to RailLogColors.Danger600
        Priority.HIGH     -> RailLogColors.Warning100 to RailLogColors.Warning600
        Priority.NORMAL   -> RailLogColors.Neutral100 to RailLogColors.Neutral600
        Priority.LOW      -> RailLogColors.Success100 to RailLogColors.Success600
    }
    BadgePill(label = priority.displayName, bg = bg, textColor = text)
}

@Composable
fun VerificationBadge(status: VerificationStatus) {
    val (bg, text) = when (status) {
        VerificationStatus.APPROVED    -> RailLogColors.Success100 to RailLogColors.Success600
        VerificationStatus.FLAGGED     -> RailLogColors.Danger100  to RailLogColors.Danger600
        VerificationStatus.AI_REVIEWED -> RailLogColors.AISurface  to RailLogColors.AIText
        VerificationStatus.UNVERIFIED  -> RailLogColors.Neutral100 to RailLogColors.Neutral600
    }
    BadgePill(label = status.displayName, bg = bg, textColor = text)
}

@Composable
private fun BadgePill(label: String, bg: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

// ── Icon button with label ───────────────────────────────────────────────────

@Composable
fun IconTextButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = RailLogColors.PrimaryAction
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(16.dp))
        Text(label, style = MaterialTheme.typography.labelMedium, color = tint)
    }
}

// ── Metric card ──────────────────────────────────────────────────────────────

@Composable
fun MetricCard(
    label: String,
    value: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(RailLogColors.Surface)
            .border(1.dp, RailLogColors.BorderDefault, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(iconTint.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.height(12.dp))
        Text(value, style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold, color = RailLogColors.TextPrimary)
        Spacer(Modifier.height(2.dp))
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = RailLogColors.TextSecondary)
    }
}

// ── Section header ───────────────────────────────────────────────────────────

@Composable
fun SectionHeader(
    title: String,
    action: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = RailLogColors.TextPrimary
        )
        action?.invoke()
    }
}

// ── Empty state ──────────────────────────────────────────────────────────────

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(RailLogColors.Neutral100),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = RailLogColors.TextTertiary, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.height(Spacing.md))
        Text(title, style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium, color = RailLogColors.TextPrimary)
        Spacer(Modifier.height(4.dp))
        Text(message, style = MaterialTheme.typography.bodySmall,
            color = RailLogColors.TextSecondary)
    }
}

// ── Loading ───────────────────────────────────────────────────────────────────

@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            color = RailLogColors.PrimaryAction,
            strokeWidth = 2.dp,
            modifier = Modifier.size(28.dp)
        )
    }
}

// ── Primary button ────────────────────────────────────────────────────────────

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    loading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        enabled = enabled && !loading,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = RailLogColors.PrimaryAction,
            contentColor   = RailLogColors.White,
            disabledContainerColor = RailLogColors.Neutral200,
            disabledContentColor   = RailLogColors.Neutral400
        )
    ) {
        if (loading) {
            CircularProgressIndicator(
                color = RailLogColors.White,
                strokeWidth = 2.dp,
                modifier = Modifier.size(18.dp)
            )
        } else {
            if (icon != null) {
                Icon(icon, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
            }
            Text(text, fontWeight = FontWeight.Medium)
        }
    }
}

// ── Secondary button ──────────────────────────────────────────────────────────

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, RailLogColors.BorderDefault),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = RailLogColors.TextPrimary
        )
    ) {
        Text(text, fontWeight = FontWeight.Medium)
    }
}

// ── Form text field ───────────────────────────────────────────────────────────

@Composable
fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isError: Boolean = false,
    errorMessage: String? = null,
    readOnly: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (isError) RailLogColors.Danger600 else RailLogColors.TextSecondary,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            readOnly = readOnly,
            singleLine = singleLine,
            isError = isError,
            placeholder = {
                Text(placeholder, color = RailLogColors.TextTertiary,
                    style = MaterialTheme.typography.bodyMedium)
            },
            leadingIcon = leadingIcon?.let { icon ->
                { Icon(icon, null, tint = RailLogColors.TextTertiary, modifier = Modifier.size(18.dp)) }
            },
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor     = RailLogColors.PrimaryAction,
                unfocusedBorderColor   = RailLogColors.BorderDefault,
                errorBorderColor       = RailLogColors.Danger600,
                focusedContainerColor  = RailLogColors.Surface,
                unfocusedContainerColor = RailLogColors.Surface,
                errorContainerColor    = RailLogColors.Danger50,
                focusedTextColor       = RailLogColors.TextPrimary,
                unfocusedTextColor     = RailLogColors.TextPrimary
            )
        )
        if (isError && errorMessage != null) {
            Text(
                errorMessage,
                style = MaterialTheme.typography.labelSmall,
                color = RailLogColors.Danger600,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}
