package com.example.foodsaver.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodsaver.core.utility.formatQuantity
import com.example.foodsaver.domain.model.FoodItem
import com.example.foodsaver.domain.model.FoodStatus
import com.example.foodsaver.presentation.theme.*

@Composable
fun StandardPageHeader(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableFoodItem(
    item: FoodItem,
    onDelete: (Long) -> Unit,
    onClick: (Long) -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onDelete(item.id)
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
                    else -> Color.Transparent
                }
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .background(color, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Hapus",
                        modifier = Modifier.padding(end = 16.dp),
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        },
        content = {
            FoodItemCard(
                item = item,
                onClick = { onClick(item.id) },
                modifier = Modifier.testTag("food_card_${item.id}")
            )
        }
    )
}

@Composable
fun FoodItemCard(
    item: FoodItem,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val status = item.getStatus()
    val isDark = isSystemInDarkTheme()
    val isHighlighted = status != FoodStatus.SAFE
    
    val statusColor = when (status) {
        FoodStatus.SAFE -> if (isDark) SafeTextDark else SafeTextLight
        FoodStatus.NEAR_EXPIRY -> if (isDark) WarningTextDark else WarningTextLight
        else -> if (isDark) ExpiredTextDark else ExpiredTextLight
    }

    val cardBgColor = if (isHighlighted) {
        statusColor.copy(alpha = 0.1f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBgColor,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isHighlighted) 0.dp else 2.dp),
        border = if (isHighlighted) BorderStroke(1.dp, statusColor.copy(alpha = 0.3f)) else null,
        onClick = { onClick?.invoke() }
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(12.dp),
                color = statusColor.copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = getEmojiForCategory(item.category),
                        fontSize = 28.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusBadge(status = status)
                }
                
                Text(
                    text = "${item.category} • ${item.quantity.formatQuantity(item.unit)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Simpan: ${item.storageLocation}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = item.getStatusLabel(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }
        }
    }
}

@Composable
fun StatusBadge(status: FoodStatus) {
    val isDark = isSystemInDarkTheme()
    val (text, color) = when (status) {
        FoodStatus.SAFE -> "Aman" to if (isDark) SafeTextDark else SafeTextLight
        FoodStatus.NEAR_EXPIRY -> "Segera" to if (isDark) WarningTextDark else WarningTextLight
        FoodStatus.EXPIRED -> "Kadaluwarsa" to if (isDark) ExpiredTextDark else ExpiredTextLight
        FoodStatus.EXPIRED_TODAY -> "Hari Ini" to if (isDark) ExpiredTextDark else ExpiredTextLight
    }

    Surface(
        color = color.copy(alpha = 0.2f),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = color,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
fun UrgentReminderBanner(reminders: List<FoodItem>, onFoodClick: (Long) -> Unit) {
    if (reminders.isEmpty()) return
    
    val mostUrgent = reminders.first()
    val remainingDays = mostUrgent.getDaysRemaining()
    
    val isDark = isSystemInDarkTheme()
    val finalBgColor = if (isDark) {
        when {
            remainingDays <= 0 -> ExpiredBgDark
            else -> WarningBgDark
        }
    } else {
        when {
            remainingDays <= 0 -> ExpiredBgLight
            else -> WarningBgLight
        }
    }
    
    val finalTextColor = if (isDark) {
        when {
            remainingDays <= 0 -> ExpiredTextDark
            else -> WarningTextDark
        }
    } else {
        when {
            remainingDays <= 0 -> ExpiredTextLight
            else -> WarningTextLight
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("banner_urgent")
            .clickable { onFoodClick(mostUrgent.id) }
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = finalBgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Outlined.NotificationsActive, 
                contentDescription = null, 
                tint = finalTextColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = when {
                        remainingDays == 0 -> "Yah, Terakhir Hari Ini!"
                        remainingDays < 0 -> "Waduh, Sudah Lewat!"
                        else -> "Ayo Segera Dimasak!"
                    },
                    fontWeight = FontWeight.ExtraBold,
                    color = finalTextColor,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = when {
                        remainingDays == 1 -> "${mostUrgent.name} segera habis besok. Yuk olah sekarang agar tidak terbuang."
                        remainingDays == 0 -> "${mostUrgent.name} terakhir hari ini. Cek kondisinya ya."
                        else -> "${mostUrgent.name} ${mostUrgent.getStatusLabel()}."
                    },
                    color = finalTextColor,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }
            if (reminders.size > 1) {
                Surface(
                    color = finalTextColor.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "+${reminders.size - 1}", 
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = finalTextColor,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
fun InventorySummarySection(
    totalCount: Int,
    safeCount: Int,
    nearlyExpiredCount: Int,
    expiredCount: Int
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SummaryItem(
                label = "Total",
                count = totalCount.toString(),
                color = MaterialTheme.colorScheme.onSurface,
                icon = Icons.Outlined.Inventory,
                modifier = Modifier.weight(1f).testTag("summary_total")
            )
            SummaryItem(
                label = "Aman",
                count = safeCount.toString(),
                color = MaterialTheme.colorScheme.primary,
                icon = Icons.Outlined.CheckCircle,
                modifier = Modifier.weight(1f).testTag("summary_safe")
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SummaryItem(
                label = "Segera",
                count = nearlyExpiredCount.toString(),
                color = MaterialTheme.colorScheme.secondary,
                icon = Icons.Outlined.Warning,
                modifier = Modifier.weight(1f).testTag("summary_nearly")
            )
            SummaryItem(
                label = "Kadaluwarsa",
                count = expiredCount.toString(),
                color = MaterialTheme.colorScheme.error,
                icon = Icons.Outlined.ErrorOutline,
                modifier = Modifier.weight(1f).testTag("summary_expired")
            )
        }
    }
}

@Composable
fun SummaryItem(label: String, count: String, color: Color, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = count, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = color)
                Text(
                    text = label, 
                    style = MaterialTheme.typography.labelSmall, 
                    color = MaterialTheme.colorScheme.onSurfaceVariant, 
                    maxLines = 1,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun PriorityCard(item: FoodItem, onClick: () -> Unit) {
    val status = item.getStatus()
    val isDark = isSystemInDarkTheme()
    
    val statusColor = when (status) {
        FoodStatus.EXPIRED, FoodStatus.EXPIRED_TODAY -> if (isDark) ExpiredTextDark else ExpiredTextLight
        else -> if (isDark) WarningTextDark else WarningTextLight
    }
    val bgColor = when (status) {
        FoodStatus.EXPIRED, FoodStatus.EXPIRED_TODAY -> if (isDark) ExpiredBgDark else ExpiredBgLight
        else -> if (isDark) WarningBgDark else WarningBgLight
    }

    Card(
        modifier = Modifier
            .width(180.dp)
            .testTag("priority_card_${item.id}")
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = if (isDark) 0.1f else 0.4f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(getEmojiForCategory(item.category), fontSize = 18.sp)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    item.category, 
                    style = MaterialTheme.typography.labelSmall, 
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                item.name, 
                fontWeight = FontWeight.ExtraBold, 
                maxLines = 1, 
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDark) TextMainDark else TextMainLight
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                item.getStatusLabel(), 
                style = MaterialTheme.typography.labelSmall, 
                color = statusColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun RecipeShortcutCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("card_cook_from_stock")
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Outlined.RestaurantMenu,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Masak dari Stok",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    "Bikin resep dari bahan yang ada di rumah.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun EmptyFoodState(
    message: String = "Belum ada makanan tersimpan",
    description: String = "Yuk mulai catat stok makananmu biar FoodSaver bisa ingatkan kalau ada yang mau kadaluwarsa.",
    onActionClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .testTag("empty_state"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(120.dp),
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.Inventory2,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            message,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onActionClick,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
            modifier = Modifier.testTag("btn_empty_add_food")
        ) {
            Text("Mulai Tambah Makanan", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp).testTag("error_state"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.ErrorOutline, 
            contentDescription = null, 
            modifier = Modifier.size(48.dp), 
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message, 
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry, 
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.testTag("btn_retry_home")
        ) {
            Text("Coba Lagi Ya", fontWeight = FontWeight.Bold)
        }
    }
}

fun getEmojiForCategory(category: String): String {
    return when (category) {
        "Sayur" -> "🥬"
        "Buah" -> "🍎"
        "Daging" -> "🥩"
        "Susu & Telur" -> "🥛"
        "Minuman" -> "🥤"
        "Camilan" -> "🍿"
        "Bumbu" -> "🧂"
        else -> "🍴"
    }
}
