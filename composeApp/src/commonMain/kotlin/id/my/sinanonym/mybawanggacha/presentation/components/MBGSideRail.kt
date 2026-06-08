package id.my.sinanonym.mybawanggacha.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    const val Search = "search"
    const val MyLibrary = "my_library"
    const val Gacha = "gacha"
    const val AnimeList = "anime_list"
    const val MangaList = "manga_list"
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
        key = MBGMainRailKey.MyLibrary,
        label = "My Library",
        icon = Icons.Default.CollectionsBookmark
    ),
    MBGSideRailItem(
        key = MBGMainRailKey.Gacha,
        label = "Gacha",
        icon = Icons.Default.Star
    ),
    MBGSideRailItem(
        key = MBGMainRailKey.AnimeList,
        label = "Anime List",
        icon = Icons.Default.SmartDisplay
    ),
    MBGSideRailItem(
        key = MBGMainRailKey.MangaList,
        label = "Manga List",
        icon = Icons.Default.MenuBook
    ),
    MBGSideRailItem(
        key = MBGMainRailKey.Search,
        label = "Search",
        icon = Icons.Default.Search
    ),
)

@Composable
fun MBGSideRailScaffold(
    selectedRailKey: String,
    onRailItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    railItems: List<MBGSideRailItem> = animeMainRailItems(),
    railWidth: Dp = 64.dp,
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
            MBGRailContentHost(
                selectedRailKey = selectedRailKey,
                orderedRailKeys = railItems.map { it.key },
                content = content
            )
        }
    }
}

@Composable
private fun MBGRailContentHost(
    selectedRailKey: String,
    orderedRailKeys: List<String>,
    content: @Composable () -> Unit
) {
    val initialDirection = remember(selectedRailKey) {
        MBGRailContentTransitionController.consume(targetKey = selectedRailKey)
    }
    var initialContentVisible by remember(selectedRailKey, initialDirection) {
        mutableStateOf(initialDirection == null)
    }

    LaunchedEffect(selectedRailKey, initialDirection) {
        if (initialDirection != null) {
            initialContentVisible = true
        }
    }

    if (initialDirection != null) {
        AnimatedVisibility(
            visible = initialContentVisible,
            enter = railContentEnterTransition(initialDirection),
            exit = ExitTransition.None
        ) {
            content()
        }
    } else {
        AnimatedContent(
            targetState = selectedRailKey,
            transitionSpec = {
                val direction = railContentSlideDirection(
                    fromKey = initialState,
                    toKey = targetState,
                    orderedKeys = orderedRailKeys
                )

                val enter = direction?.let(::railContentEnterTransition)
                    ?: fadeIn(animationSpec = tween(RAIL_CONTENT_FADE_DURATION_MS))
                val exit = fadeOut(animationSpec = tween(RAIL_CONTENT_FADE_DURATION_MS))

                enter togetherWith exit
            },
            label = "rail_content_transition"
        ) {
            content()
        }
    }
}

private object MBGRailContentTransitionController {
    private var pendingTransition: MBGRailPendingTransition? = null

    fun prepare(
        fromKey: String,
        toKey: String,
        orderedKeys: List<String>
    ) {
        if (fromKey == toKey) return

        val direction = railContentSlideDirection(
            fromKey = fromKey,
            toKey = toKey,
            orderedKeys = orderedKeys
        ) ?: return

        pendingTransition = MBGRailPendingTransition(
            targetKey = toKey,
            direction = direction
        )
    }

    fun consume(targetKey: String): MBGRailContentSlideDirection? {
        val pending = pendingTransition ?: return null
        if (pending.targetKey != targetKey) return null

        pendingTransition = null
        return pending.direction
    }
}

private data class MBGRailPendingTransition(
    val targetKey: String,
    val direction: MBGRailContentSlideDirection
)

private enum class MBGRailContentSlideDirection {
    FromTop,
    FromBottom
}

private fun railContentSlideDirection(
    fromKey: String,
    toKey: String,
    orderedKeys: List<String>
): MBGRailContentSlideDirection? {
    val fromIndex = orderedKeys.indexOf(fromKey)
    val toIndex = orderedKeys.indexOf(toKey)

    if (fromIndex < 0 || toIndex < 0 || fromIndex == toIndex) return null

    return if (toIndex > fromIndex) {
        MBGRailContentSlideDirection.FromBottom
    } else {
        MBGRailContentSlideDirection.FromTop
    }
}

private fun railContentEnterTransition(
    direction: MBGRailContentSlideDirection
): EnterTransition {
    return slideInVertically(
        animationSpec = tween(RAIL_CONTENT_SLIDE_DURATION_MS),
        initialOffsetY = { fullHeight ->
            when (direction) {
                MBGRailContentSlideDirection.FromBottom -> fullHeight
                MBGRailContentSlideDirection.FromTop -> -fullHeight
            }
        }
    ) + fadeIn(animationSpec = tween(RAIL_CONTENT_SLIDE_DURATION_MS))
}

private const val RAIL_CONTENT_SLIDE_DURATION_MS = 280
private const val RAIL_CONTENT_FADE_DURATION_MS = 90

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
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        topAction()

        LazyColumn(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(22.dp, Alignment.CenterVertically)
        ) {
            itemsIndexed(
                items = items,
                key = { _, item -> item.key }
            ) { _, item ->
                MBGVerticalRailItem(
                    item = item,
                    selected = item.key == selectedKey,
                    onClick = {
                        MBGRailContentTransitionController.prepare(
                            fromKey = selectedKey,
                            toKey = item.key,
                            orderedKeys = items.map { railItem -> railItem.key }
                        )
                        onItemClick(item.key)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))
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

    Box(
        modifier = modifier
            .width(42.dp)
            .height(136.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .rotate(-90f)
                .requiredWidth(128.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            item.icon?.let { imageVector ->
                Icon(
                    imageVector = imageVector,
                    contentDescription = item.label,
                    tint = labelColor,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))
            }

            Text(
                text = item.label,
                style = MaterialTheme.typography.labelLarge,
                color = labelColor,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                maxLines = 1,
                softWrap = false,
                textAlign = TextAlign.Center
            )
        }
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
        modifier = modifier.padding(top = 12.dp)
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
            .size(40.dp)
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
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
