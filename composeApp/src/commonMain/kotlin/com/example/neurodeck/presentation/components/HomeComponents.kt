package com.example.neurodeck.presentation.screens.home.components

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.neurodeck.domain.model.Deck

// ════════════════════════════════════════════════════════════════════════════
// HomeComponents.kt — commonMain
//
// Sprint 2 — Prioritas 3c.3 (Home Tab Components)
//
// Semua sub-components untuk Home Screen di satu file (vs. 5 file terpisah).
// Pattern ini dipakai juga di CommonComponents.kt — file lokasi dekat dengan
// parent screen, mudah cari & maintain.
//
// Components:
//   - GreetingCard      — Header big greeting + nama user
//   - StatMiniCard      — Small stat card untuk 3 angka (Due/Streak/Today)
//   - QuickActionButton — 2 tombol prominent (New Deck, Study Now)
//   - RecentDeckItem    — Compact deck card untuk "Continue Learning" list
//   - TipsCard          — Card dengan tips of the day
// ════════════════════════════════════════════════════════════════════════════

/**
 * Header greeting card — big & welcoming.
 *
 * Layout:
 *   "Selamat Pagi 👋"
 *   "<userName>"
 *
 * Pakai primaryContainer sebagai background untuk visual prominence,
 * dan onPrimaryContainer untuk text supaya contrast cukup di Light & Dark.
 */
@Composable
fun GreetingCard(
    greeting: String,
    userName: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "$greeting 👋",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

/**
 * Mini stat card — small box dengan icon + nilai + label.
 *
 * Dipakai dalam Row 3 columns untuk display Due/Streak/Today di atas.
 *
 * @param icon       Material icon untuk visual cue.
 * @param value      Angka utama (e.g. "12", "5 hari", "23 kartu").
 * @param label      Caption di bawah angka (e.g. "Due Cards", "Streak").
 * @param accentColor Optional warna icon — supaya ada visual variety:
 *                   merah untuk Due (urgency), oranye untuk Streak (fire),
 *                   hijau untuk Reviewed Today (success).
 */
@Composable
fun StatMiniCard(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    accentColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = RoundedCornerShape(16.dp),
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
                modifier = Modifier.size(28.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/**
 * Helper row untuk 3 mini stat cards. Weight 1f each = equal width.
 */
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
 * Quick action buttons row — 2 tombol prominent: "Deck Baru" + "Belajar Sekarang".
 *
 * Yang pertama (Deck Baru) = OutlinedButton (less emphasis, secondary action).
 * Yang kedua (Belajar Sekarang) = filled Button (primary action — Study CTA).
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
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Deck Baru")
        }
        Button(
            onClick = onStudyNow,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
            ),
        ) {
            Icon(
                imageVector = Icons.Outlined.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Belajar Sekarang")
        }
    }
}

/**
 * Recent deck item — compact card untuk Continue Learning list.
 *
 * Tampil sebagai Card horizontal: icon kiri, title + card count kanan.
 * Click → navigate ke deck detail / card list.
 */
@Composable
fun RecentDeckItem(
    deck: Deck,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Icon container — circle dengan style icon di tengah.
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Style,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp),
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
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = deck.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${deck.cardCount} kartu",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/**
 * Tips of the day card — small info card di paling bawah Home.
 *
 * Style: tertiaryContainer background, supaya visually berbeda dengan
 * mini stats (surfaceVariant) dan greeting (primaryContainer).
 */
@Composable
fun TipsCard(
    tip: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        ),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "TIPS HARI INI",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = tip,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
            )
        }
    }
}