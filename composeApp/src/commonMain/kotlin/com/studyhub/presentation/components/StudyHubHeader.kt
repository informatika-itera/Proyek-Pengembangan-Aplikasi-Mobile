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
import com.studyhub.presentation.theme.GoldenSuedeDark
import com.studyhub.presentation.theme.GoldenSuedeLight

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
        colors = listOf(GoldenSuedeDark, GoldenSuedeLight),
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset.Infinite
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(headerBrush)
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 12.dp)
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
                                style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White
                        )
                        if (subtitle != null) {
                            subtitle()
                        }
                    }
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        content = actions
                    )
                }
                
                if (content != null) {
                    Spacer(Modifier.height(20.dp))
                    content()
                }
            }
        }
        
        // Canvas Curve
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
