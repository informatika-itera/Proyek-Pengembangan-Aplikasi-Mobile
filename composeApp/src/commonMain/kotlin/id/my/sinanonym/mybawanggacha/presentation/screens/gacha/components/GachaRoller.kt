package id.my.sinanonym.mybawanggacha.presentation.screens.gacha.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import id.my.sinanonym.mybawanggacha.domain.gacha.model.GachaHistoryEntry
import id.my.sinanonym.mybawanggacha.domain.gacha.model.GachaResultItem
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

private const val RollerCardCount = 9
private const val RollerVisibleAngle = 82f
private const val RollerAngleStep = 24f

@Composable
internal fun GachaRoller(
    anchorItem: GachaResultItem?,
    history: List<GachaHistoryEntry>,
    canSkip: Boolean,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val items = remember(anchorItem, history) {
        buildRollingItems(
            anchorItem = anchorItem,
            history = history
        )
    }
    val density = LocalDensity.current
    val radiusPx = with(density) { 132.dp.toPx() }
    val yLiftPx = with(density) { 10.dp.toPx() }
    val cameraDistance = 18f * density.density
    val infiniteTransition = rememberInfiniteTransition(label = "gacha_roller")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1180,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "gacha_roller_phase"
    )
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.72f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 620,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gacha_roller_pulse"
    )

    Box(modifier = modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.28f),
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(17.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Rolling...",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(156.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(126.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f + 0.22f * pulse))
                )

                items.forEachIndexed { index, item ->
                    val angle = normalizeRollerAngle(
                        value = (index - items.lastIndex / 2f) * RollerAngleStep - phase
                    )
                    val isVisible = abs(angle) <= RollerVisibleAngle
                    if (isVisible) {
                        val radians = angle.toDouble() * PI / 180.0
                        val depth = ((cos(radians) + 1.0) / 2.0).toFloat()
                        val side = sin(radians).toFloat()
                        val scale = 0.66f + 0.34f * depth
                        val alpha = 0.30f + 0.70f * depth

                        RollingPreviewCard(
                            item = item,
                            modifier = Modifier
                                .zIndex(depth)
                                .graphicsLayer {
                                    translationX = side * radiusPx
                                    translationY = -depth * yLiftPx
                                    scaleX = scale
                                    scaleY = scale
                                    rotationY = -side * 32f
                                    shadowElevation = 6f + 10f * depth
                                    this.alpha = alpha
                                    this.cameraDistance = cameraDistance
                                }
                        )
                    }
                }
            }

                Text(
                    text = "Selecting",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (canSkip) {
            TextButton(
                onClick = onSkip,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp, end = 8.dp)
            ) {
                Text(
                    text = "Skip animation",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun RollingPreviewCard(
    item: GachaResultItem?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.size(width = 82.dp, height = 116.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            if (item?.imageUrl.isNullOrBlank()) {
                PlaceholderRollerCard(item = item)
            } else {
                AsyncImage(
                    model = item?.imageUrl,
                    contentDescription = item?.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(116.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
private fun PlaceholderRollerCard(item: GachaResultItem?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(116.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f))
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            modifier = Modifier.size(22.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(7.dp))
        Text(
            text = item?.mediaType?.label ?: "MBG",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

private fun buildRollingItems(
    anchorItem: GachaResultItem?,
    history: List<GachaHistoryEntry>
): List<GachaResultItem?> {
    val seeded = buildList {
        anchorItem?.let(::add)
        history.forEach { entry -> add(entry.item) }
    }.distinctBy { item -> "${item.mediaType}:${item.malId}" }

    return if (seeded.isEmpty()) {
        List(RollerCardCount) { null }
    } else {
        List(RollerCardCount) { index -> seeded[index % seeded.size] }
    }
}

private fun normalizeRollerAngle(value: Float): Float {
    var angle = value % 360f
    if (angle < -180f) angle += 360f
    if (angle > 180f) angle -= 360f
    return angle
}
