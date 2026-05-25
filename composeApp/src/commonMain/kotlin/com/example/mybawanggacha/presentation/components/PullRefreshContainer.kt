package com.example.mybawanggacha.presentation.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullRefreshContainer(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val pullRefreshState = rememberPullToRefreshState()
    val pullFraction = pullRefreshState.distanceFraction.coerceIn(0f, 1.25f)
    val shouldDecorate = enabled && (pullFraction > 0.02f || isRefreshing)

    val contentOffset by animateDpAsState(
        targetValue = when {
            !enabled -> 0.dp
            isRefreshing -> 16.dp
            else -> (pullFraction * 42f).dp
        },
        label = "pull_refresh_content_offset"
    )
    val contentTopCorner by animateDpAsState(
        targetValue = if (shouldDecorate) 38.dp else 0.dp,
        label = "pull_refresh_content_corner"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.22f))
    ) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                if (enabled && !isRefreshing) {
                    onRefresh()
                }
            },
            modifier = Modifier.fillMaxSize(),
            state = pullRefreshState,
            indicator = {
                PullRefreshCrescentBar(
                    state = pullRefreshState,
                    isRefreshing = isRefreshing,
                    enabled = enabled,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .zIndex(0f)
                )
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = contentOffset)
                    .zIndex(1f)
                    .clip(
                        RoundedCornerShape(
                            topStart = contentTopCorner,
                            topEnd = contentTopCorner
                        )
                    )
                    .background(MaterialTheme.colorScheme.background)
            ) {
                content()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PullRefreshCrescentBar(
    state: PullToRefreshState,
    isRefreshing: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    if (!enabled) return

    val pullFraction = state.distanceFraction.coerceIn(0f, 1.25f)
    val visible = pullFraction > 0.02f || isRefreshing
    if (!visible) return

    val visualProgress = if (isRefreshing) 1f else pullFraction.coerceIn(0f, 1f)
    val alpha = if (isRefreshing) 1f else (pullFraction * 1.25f).coerceIn(0f, 1f)
    val label = when {
        isRefreshing -> "Refreshing..."
        pullFraction >= 1f -> "Lepas untuk refresh"
        else -> "Tarik untuk refresh"
    }
    val icon = if (isRefreshing) Icons.Default.Refresh else Icons.Default.KeyboardArrowDown

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .graphicsLayer {
                this.alpha = alpha
                translationY = -14f * (1f - alpha)
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 26.dp, top = 12.dp, end = 26.dp, bottom = 22.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.size(20.dp)
            )

            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .width(72.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.16f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(visualProgress.coerceIn(0.08f, 1f))
                        .clip(RoundedCornerShape(999.dp))
                        .background(MaterialTheme.colorScheme.tertiary)
                )
            }
        }
    }
}