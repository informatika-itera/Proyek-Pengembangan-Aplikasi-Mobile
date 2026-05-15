package com.example.mybawanggacha.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object MBGMainRailKey {
    const val Home = "home"
    const val AnimeList = "anime_list"
}

@Stable
data class MBGSideRailItem(
    val key: String,
    val label: String,
    val icon: ImageVector? = null
)

fun animeMainRailItems(): List<MBGSideRailItem> = listOf(
    MBGSideRailItem(
        key = MBGMainRailKey.Home,
        label = "Home",
        icon = Icons.Default.Home
    ),
    MBGSideRailItem(
        key = MBGMainRailKey.AnimeList,
        label = "Anime List",
        icon = Icons.Default.Menu
    )
)

@Composable
fun MBGSideRailScaffold(
    selectedRailKey: String,
    onRailItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    railItems: List<MBGSideRailItem> = animeMainRailItems(),
    railWidth: Dp = 96.dp,
    topAction: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        MBGVerticalRail(
            selectedKey = selectedRailKey,
            items = railItems,
            onItemClick = onRailItemClick,
            topAction = topAction,
            modifier = Modifier.width(railWidth)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            content()
        }
    }
}

@Composable
fun MBGVerticalRail(
    selectedKey: String,
    items: List<MBGSideRailItem>,
    onItemClick: (String) -> Unit,
    topAction: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        topAction()

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            items.forEachIndexed { index, item ->
                MBGVerticalRailItem(
                    item = item,
                    selected = item.key == selectedKey,
                    onClick = { onItemClick(item.key) }
                )

                if (index != items.lastIndex) {
                    Spacer(modifier = Modifier.height(52.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
fun MBGVerticalRailItem(
    item: MBGSideRailItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeColor = MaterialTheme.colorScheme.onBackground
    val inactiveColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.58f)
    val labelColor = if (selected) activeColor else inactiveColor

    Column(
        modifier = modifier
            .width(72.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item.icon?.let { imageVector ->
            Icon(
                imageVector = imageVector,
                contentDescription = item.label,
                tint = labelColor,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        Text(
            text = item.label,
            style = MaterialTheme.typography.titleMedium,
            color = labelColor,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.rotate(-90f)
        )
    }
}

@Composable
fun MBGRailSettingsButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    MBGRailIconButton(
        icon = Icons.Default.Settings,
        contentDescription = "Settings",
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
fun MBGRailBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    MBGRailIconButton(
        icon = Icons.AutoMirrored.Filled.ArrowBack,
        contentDescription = "Kembali",
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
fun MBGRailIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Transparent
) {
    Surface(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        shape = CircleShape,
        color = containerColor,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}
