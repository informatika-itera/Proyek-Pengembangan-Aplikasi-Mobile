package com.example.travelplanner.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travelplanner.core.util.LocalStrings
import kotlinx.coroutines.delay

/**
 * Animated in-app offline/online notification banner.
 *
 * - When offline: red banner slides in from top, stays permanently
 * - When back online: green banner shows briefly for 2 seconds then auto-hides
 */
@Composable
fun ConnectivityBanner(isOnline: Boolean, modifier: Modifier = Modifier) {
    val s = LocalStrings.current
    // Track whether we've ever seen an offline state (to show "back online" message)
    var wasOffline by remember { mutableStateOf(false) }
    var showOnlineConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(isOnline) {
        if (!isOnline) {
            wasOffline = true
            showOnlineConfirmation = false
        } else if (wasOffline) {
            // Back online — show success banner briefly
            showOnlineConfirmation = true
            delay(2500)
            showOnlineConfirmation = false
        }
    }

    val showOffline = !isOnline
    val showOnline  = isOnline && showOnlineConfirmation

    Column(modifier = modifier) {
        // ── OFFLINE BANNER ──────────────────────────────────────────────
        AnimatedVisibility(
            visible = showOffline,
            enter = slideInVertically(tween(350)) { -it } + fadeIn(tween(350)),
            exit  = slideOutVertically(tween(300)) { -it } + fadeOut(tween(300))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFB71C1C))
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        Icons.Default.WifiOff,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Column {
                        Text(
                            text = s.noInternetConnection,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 13.sp
                        )
                        Text(
                            text = s.someFeaturesUnavailable,
                            color = Color.White.copy(alpha = 0.85f),
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // ── ONLINE CONFIRMATION BANNER ──────────────────────────────────
        AnimatedVisibility(
            visible = showOnline,
            enter = slideInVertically(tween(350)) { -it } + fadeIn(tween(350)),
            exit  = slideOutVertically(tween(300)) { -it } + fadeOut(tween(300))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1B5E20))
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        Icons.Default.Wifi,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = s.internetRestored,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
