package com.example.neurodeck.presentation.screens.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.neurodeck.domain.model.Deck
import com.example.neurodeck.presentation.components.StickyNoteBadge

// ════════════════════════════════════════════════════════════════════════════
// HomeComponents.kt — REFACTORED to Vivid Logic style (Sprint 2 UI polish)
//
// Perubahan dari versi sebelumnya:
//   - GreetingCard: pakai big bold headline (display style) + sticky badge streak
//   - StatMiniCard: OutlinedCard dengan black border (Vivid Logic signature)
//   - QuickActionsRow: inverted black button + filled purple
//   - RecentDeckItem: OutlinedCard dengan colored left accent strip
//   - TipsCard: tetap tertiary container, lebih flat
// ════════════════════════════════════════════════════════════════════════════

/**
 * GreetingCard — big bold headline "Morning, Brainiac!" style.
 *
 * Layout:
 *   "Selamat Pagi 👋"               <-- caption
 *   "<userName>"                    <-- big bold display
 *
 * Tidak pakai Card container — biarkan flat di background, lebih airy.
 */
@Composable
fun GreetingCard(
    greeting: String,
    userName: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Text(
            text = userName,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$greeting 👋 — siap rangkai memori baru?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/**
 * Mini stat card — outlined dengan black border (Vivid Logic style).
 *
 * @param accentColor Warna icon — variasi visual untuk Due/Streak/Today.
 */
@Composable
fun StatMiniCard(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    accentColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
) {
    OutlinedCard(
        modifier = modifier,
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(14.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun StatsRow(
    dueCount: Int,
    streakDays: Int,
    reviewedToday: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        StatMiniCard(
            icon = Icons.Outlined.Timer,
            value = dueCount.toString(),
            label = "Due Cards",
            accentColor = MaterialTheme.colorScheme.error,
            modifier = Modifier.weight(1f),
        )
        StatMiniCard(
            icon = Icons.Outlined.LocalFireDepartment,
            value = "$streakDays hari",
            label = "Streak",
            accentColor = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.weight(1f),
        )
        StatMiniCard(
            icon = Icons.Outlined.School,
            value = reviewedToday.toString(),
            label = "Hari Ini",
            accentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f),
        )
    }
}

/**
 * Quick action buttons — INVERTED (black) untuk "Belajar Sekarang" dan
 * OUTLINED untuk "Deck Baru". Sesuai pattern Stitch button system.
 */
@Composable
fun QuickActionsRow(
    onCreateDeck: () -> Unit,
    onStudyNow: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedButton(
            onClick = onCreateDeck,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 14.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shape = RoundedCornerShape(10.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Deck Baru",
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
            )
        }
        // Filled primary button = main CTA "Belajar Sekarang"
        Button(
            onClick = onStudyNow,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
            shape = RoundedCornerShape(10.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Belajar Sekarang",
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

/**
 * Recent deck item — OutlinedCard dengan colored accent strip di kiri.
 * Mimicking Stitch library card style.
 */
@Composable
fun RecentDeckItem(
    deck: Deck,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Left accent strip (purple)
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(70.dp)
                    .clip(RoundedCornerShape(topStart = 2.dp, bottomStart = 2.dp))
                    .background(MaterialTheme.colorScheme.primary),
            )

            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Icon container — square outlined
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Style,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp),
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = deck.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                    )
                    if (deck.description.isNotBlank()) {
                        Text(
                            text = deck.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${deck.cardCount} kartu",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

/**
 * Tips card — sticky-note look (yellow tertiary) untuk tip of the day.
 */
@Composable
fun TipsCard(
    tip: String,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(14.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            StickyNoteBadge(text = "TIPS HARI INI")
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = tip,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
            )
        }
    }
}

// (End of file)