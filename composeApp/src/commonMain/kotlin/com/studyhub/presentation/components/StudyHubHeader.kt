package com.studyhub.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studyhub.presentation.theme.Spacing

@Composable
fun StudyHubHeader(
    title: String,
    subtitle: @Composable (ColumnScope.() -> Unit)? = null,
    dateText: String? = null,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (ColumnScope.() -> Unit)? = null
) {
    val headerBrush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary // Mixed for brand gradient feel
        ),
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset.Infinite
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(headerBrush)
                .statusBarsPadding()
                .padding(horizontal = Spacing.normal)
                .padding(top = Spacing.normal, bottom = Spacing.small)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        if (dateText != null) {
                            Text(
                                text = dateText,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                            )
                        }
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        if (subtitle != null) {
                            subtitle()
                        }
                    }
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                        verticalAlignment = Alignment.CenterVertically,
                        content = actions
                    )
                }
                
                if (content != null) {
                    Spacer(Modifier.height(Spacing.normal))
                    content()
                }
            }
        }
        
        // Canvas Curve to blend with background
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
        ) {
            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                quadraticTo(
                    size.width / 2f, size.height * 2f,
                    0f, 0f
                )
                close()
            }
            drawPath(path, brush = headerBrush)
        }
    }
}
