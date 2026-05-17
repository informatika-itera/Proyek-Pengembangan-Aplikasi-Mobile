package com.example.metaforge.presentation.screens.draft

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DraftScreen(
    isUserFirstPick: Boolean,
    userPickPosition: Int, // Menangkap parameter posisi pick user
    viewModel: DraftViewModel,
    onNavigateToHeroSelect: (Int, Boolean, Boolean) -> Unit,
    onNavigateToCounter: () -> Unit,
    onNavigateToSynergy: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DRAFT ARENA", color = Color.White, fontWeight = FontWeight.Black, letterSpacing = 2.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is DraftUiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }
            is DraftUiState.Error -> Box(Modifier.fillMaxSize(), Alignment.Center) { Text(state.message, color = MaterialTheme.colorScheme.error) }
            is DraftUiState.Ready -> {
                Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)) {

                    // INDIKATOR GILIRAN & POSISI USER AKTIF
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = state.turnMessage,
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "YOUR ASSIGNED SLOT: PICK #$userPickPosition",
                                color = Color(0xFFFFD700), // Warna Emas
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }

                    if (state.isUserTurn && state.recommendation != null) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(Modifier.width(8.dp))
                                    Text("SYSTEM RECOMMENDATION", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black)
                                }
                                Spacer(Modifier.height(12.dp))
                                Text("${state.recommendation.heroName} • ${state.recommendation.role}", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(4.dp))
                                Text(state.recommendation.reason, color = Color.LightGray, style = MaterialTheme.typography.bodySmall)

                                if (state.recommendation.warning != null) {
                                    Spacer(Modifier.height(8.dp))
                                    Row(verticalAlignment = Alignment.Top) {
                                        Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text(state.recommendation.warning, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                        }
                    }

                    Text("BAN PHASE (Fill 10 Bans to unlock Picks)", color = Color.Gray, fontWeight = FontWeight.Black, style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            for (i in 0 until 5) {
                                val hero = state.draftState.allyBans.getOrNull(i)
                                BanSlotItem(hero?.name, true, { onNavigateToHeroSelect(i, true, true) }) { viewModel.removeHero(i, true, true) }
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            for (i in 0 until 5) {
                                val hero = state.draftState.enemyBans.getOrNull(i)
                                BanSlotItem(hero?.name, false, { onNavigateToHeroSelect(i, false, true) }) { viewModel.removeHero(i, false, true) }
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("BLUE TEAM", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                        Text("RED TEAM", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                    }
                    Spacer(Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            for (i in 0 until 5) {
                                val hero = state.draftState.allySlots.getOrNull(i)
                                val isUnlocked = state.draftState.isPickUnlocked(i, true, isUserFirstPick)
                                val isUserSlot = (i == userPickPosition - 1) // Deteksi apakah ini slot pengguna

                                InteractiveHeroSlot(hero?.name, hero?.role?.displayName, true, isUnlocked, isUserSlot, { if (isUnlocked) onNavigateToHeroSelect(i, true, false) }) { viewModel.removeHero(i, true, false) }
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            for (i in 0 until 5) {
                                val hero = state.draftState.enemySlots.getOrNull(i)
                                val isUnlocked = state.draftState.isPickUnlocked(i, false, isUserFirstPick)

                                InteractiveHeroSlot(hero?.name, hero?.role?.displayName, false, isUnlocked, false, { if (isUnlocked) onNavigateToHeroSelect(i, false, false) }) { viewModel.removeHero(i, false, false) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.BanSlotItem(heroName: String?, isAlly: Boolean, onClick: () -> Unit, onDelete: () -> Unit) {
    val tintColor = if (isAlly) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(6.dp))
            .background(if (heroName != null) Color.Black.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface)
            .border(1.dp, tintColor.copy(alpha = 0.8f), RoundedCornerShape(6.dp))
            .clickable(enabled = heroName == null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (heroName != null) {
            Box(modifier = Modifier.fillMaxSize().padding(2.dp), contentAlignment = Alignment.TopEnd) {
                IconButton(onClick = onDelete, modifier = Modifier.size(16.dp)) {
                    Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(12.dp))
                }
            }
            Text(
                text = heroName.take(3).uppercase(),
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Clip
            )
        } else {
            Box(modifier = Modifier.fillMaxSize(0.3f).background(tintColor.copy(alpha = 0.3f)))
        }
    }
}

@Composable
fun InteractiveHeroSlot(
    heroName: String?,
    role: String?,
    isAlly: Boolean,
    isUnlocked: Boolean,
    isUserSlot: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val teamColor = if (isAlly) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
    val highlightColor = if (isUserSlot) Color(0xFFFFD700) else teamColor // Warna Emas Jika Slot User
    val gradientColors = if (heroName != null) listOf(teamColor.copy(alpha = 0.3f), MaterialTheme.colorScheme.surface) else listOf(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.surface)
    val borderColor = if (isUserSlot) highlightColor else if (isUnlocked && heroName == null) teamColor else MaterialTheme.colorScheme.surfaceVariant

    Box(
        modifier = Modifier.fillMaxWidth().height(68.dp).padding(vertical = 4.dp).clip(RoundedCornerShape(10.dp))
            .background(Brush.horizontalGradient(gradientColors))
            .border(if (isUserSlot || (isUnlocked && heroName == null)) 2.dp else 1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable(enabled = heroName == null && isUnlocked) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (heroName != null) {
            Row(modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(heroName, color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                        if (isUserSlot) {
                            Spacer(Modifier.width(6.dp))
                            Text("(YOU)", color = highlightColor, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(role ?: "", color = Color.LightGray, style = MaterialTheme.typography.labelSmall)
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) { Icon(Icons.Default.Close, null, tint = teamColor) }
            }
        } else if (isUnlocked) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("SELECT HERO", color = teamColor, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                if (isUserSlot) Text("(YOUR SLOT)", color = highlightColor, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("AWAITING TURN", color = Color.DarkGray, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                if (isUserSlot) Text("(YOUR SLOT)", color = Color.Gray, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}