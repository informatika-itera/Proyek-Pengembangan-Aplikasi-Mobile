package com.example.raillog.presentation.screens.admin_main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.raillog.domain.model.SupplyItem
import com.example.raillog.presentation.components.*
import com.example.raillog.presentation.theme.RailLogColors
import com.example.raillog.presentation.theme.Spacing
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

private fun formatAdminTs(millis: Long): String {
    if (millis <= 0L) return ""
    return try {
        val dt = Instant.fromEpochMilliseconds(millis)
            .toLocalDateTime(TimeZone.currentSystemDefault())
        "${dt.dayOfMonth.toString().padStart(2, '0')}/${dt.monthNumber.toString().padStart(2, '0')}"
    } catch (e: Exception) { "" }
}

// ── Admin Main ────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMainScreen(
    viewModel: AdminMainViewModel = koinViewModel(),
    onNavigateToVerificationDetail: (Long) -> Unit,
    onNavigateToAIAssistant: () -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val allItems by viewModel.allItems.collectAsState()

    val tabs = listOf("Inventaris", "Verifikasi", "Operasional")
    val tabIcons = listOf(
        Icons.Default.Inventory,
        Icons.AutoMirrored.Filled.FactCheck,
        Icons.Default.QueryStats
    )

    Scaffold(
        containerColor = RailLogColors.Background,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(30.dp).clip(RoundedCornerShape(6.dp))
                                .background(RailLogColors.PrimaryAction),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AdminPanelSettings, null,
                                tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("RailLog Admin", fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp, color = RailLogColors.TextPrimary)
                            Text("Panel kendali", style = MaterialTheme.typography.labelSmall,
                                color = RailLogColors.TextTertiary)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToAIAssistant) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = "Asisten AI",
                            tint = RailLogColors.PrimaryAction,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, null,
                            tint = RailLogColors.Danger600, modifier = Modifier.size(20.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RailLogColors.Surface)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = RailLogColors.Surface,
                tonalElevation = 0.dp,
                modifier = Modifier.border(1.dp, RailLogColors.BorderSubtle, RoundedCornerShape(0.dp))
            ) {
                tabs.forEachIndexed { i, label ->
                    NavigationBarItem(
                        selected = selectedTab == i,
                        onClick  = { selectedTab = i },
                        icon = { Icon(tabIcons[i], null, modifier = Modifier.size(20.dp)) },
                        label = { Text(label, fontSize = 10.sp,
                            fontWeight = if (selectedTab == i) FontWeight.SemiBold
                            else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor   = RailLogColors.PrimaryAction,
                            selectedTextColor   = RailLogColors.PrimaryAction,
                            unselectedIconColor = RailLogColors.TextTertiary,
                            unselectedTextColor = RailLogColors.TextTertiary,
                            indicatorColor      = RailLogColors.Brand50
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (selectedTab) {
                0 -> AdminInventoryTab(allItems)
                1 -> AdminVerificationTab(viewModel, onNavigateToVerificationDetail)
                2 -> AdminOperationsTab(viewModel)
            }
        }
    }
}

// ── Inventory tab ──────────────────────────────────────────────────────────────

@Composable
fun AdminInventoryTab(allItems: List<SupplyItem>) {
    val criticalItems = allItems.filter { it.quantity < 15 }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(RailLogColors.Background),
        contentPadding = PaddingValues(Spacing.pagePadding),
        verticalArrangement = Arrangement.spacedBy(Spacing.sectionGap)
    ) {
        item {
            PageHeader("Inventaris gudang", "Kapasitas & stok kritis")
        }

        item {
            SurfaceCard {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    Text("Kapasitas per subsistem",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium, color = RailLogColors.TextSecondary)
                    ProgressRow("Infrastruktur", 0.75f)
                    ProgressRow("Sarana kereta", 0.45f)
                    ProgressRow("Elektronik", 0.12f)
                }
            }
        }

        item {
            SectionHeader("Stok kritis (< 15 unit)")
        }

        if (criticalItems.isEmpty()) {
            item {
                EmptyState(Icons.Default.CheckCircle,
                    "Stok aman", "Semua komponen dalam kondisi optimal")
            }
        } else {
            items(criticalItems) { item ->
                SurfaceCard {
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.name, style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium, color = RailLogColors.TextPrimary)
                            Text("SKU: ${item.partCode}",
                                style = MaterialTheme.typography.labelSmall,
                                color = RailLogColors.TextTertiary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("${item.quantity}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = RailLogColors.Danger600)
                            Text("unit", style = MaterialTheme.typography.labelSmall,
                                color = RailLogColors.TextTertiary)
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(Spacing.lg)) }
    }
}

@Composable
private fun ProgressRow(label: String, progress: Float) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodySmall,
                color = RailLogColors.TextSecondary)
            Text("${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = if (progress < 0.2f) RailLogColors.Danger600 else RailLogColors.TextPrimary)
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
            color = if (progress < 0.2f) RailLogColors.Danger600 else RailLogColors.PrimaryAction,
            trackColor = RailLogColors.Neutral100
        )
    }
}

// ── Verification tab ──────────────────────────────────────────────────────────

@Composable
fun AdminVerificationTab(
    viewModel: AdminMainViewModel,
    onDetail: (Long) -> Unit
) {
    val query by viewModel.searchQuery.collectAsState()
    val items by viewModel.filteredPendingItems.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(RailLogColors.Background)) {
        Column(modifier = Modifier.padding(horizontal = Spacing.pagePadding)) {
            Spacer(Modifier.height(Spacing.md))
            PageHeader("Antrean verifikasi", "${items.size} pengajuan menunggu")
            Spacer(Modifier.height(Spacing.md))
            RailLogSearchField(query, { viewModel.updateSearchQuery(it) },
                "Cari berdasarkan nama...")
            Spacer(Modifier.height(Spacing.sm))
        }

        HorizontalDivider(color = RailLogColors.BorderSubtle)

        LazyColumn(
            contentPadding = PaddingValues(Spacing.pagePadding),
            verticalArrangement = Arrangement.spacedBy(Spacing.itemGap)
        ) {
            if (items.isEmpty()) {
                item {
                    EmptyState(Icons.AutoMirrored.Filled.FactCheck,
                        "Tidak ada antrean", "Semua pengajuan telah diproses")
                }
            } else {
                items(items) { item ->
                    VerificationQueueCard(item, { onDetail(item.id) })
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun VerificationQueueCard(item: SupplyItem, onClick: () -> Unit) {
    SurfaceCard(onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("REQ-${item.id}",
                    style = MaterialTheme.typography.labelSmall,
                    color = RailLogColors.TextTertiary)
                Spacer(Modifier.height(2.dp))
                Text(item.name, style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium, color = RailLogColors.TextPrimary)
                Spacer(Modifier.height(4.dp))
                Text("${item.quantity} unit",
                    style = MaterialTheme.typography.bodySmall, color = RailLogColors.TextSecondary)
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(RailLogColors.AISurface)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.AutoAwesome, null,
                                tint = RailLogColors.AIText,
                                modifier = Modifier.size(12.dp))
                            Text("Analisis AI tersedia", fontSize = 11.sp,
                                color = RailLogColors.AIText, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
            Column(horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(formatAdminTs(item.createdAt.toEpochMilliseconds()),
                    style = MaterialTheme.typography.labelSmall, color = RailLogColors.TextTertiary)
                PriorityBadge(item.priority)
                Spacer(Modifier.height(4.dp))
                Icon(Icons.Default.ChevronRight, null,
                    tint = RailLogColors.TextTertiary, modifier = Modifier.size(16.dp))
            }
        }
    }
}

// ── Operations tab ────────────────────────────────────────────────────────────

@Composable
fun AdminOperationsTab(viewModel: AdminMainViewModel) {
    val allItems         by viewModel.allItems.collectAsState()
    val pendingCount     by viewModel.pendingRequisitions.collectAsState()
    val criticalPending  by viewModel.criticalPendingCount.collectAsState()
    val aiConfidence     by viewModel.averageAiConfidence.collectAsState()
    val verifiedCount    = allItems.count { it.status.name == "VERIFIED" }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(RailLogColors.Background),
        contentPadding = PaddingValues(Spacing.pagePadding),
        verticalArrangement = Arrangement.spacedBy(Spacing.sectionGap)
    ) {
        item { PageHeader("Operasional", "Ringkasan sistem logistik") }

        item {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.itemGap)) {
                MetricCard("Terverifikasi", verifiedCount.toString(),
                    Icons.Default.CheckCircle, RailLogColors.Success600,
                    modifier = Modifier.weight(1f))
                MetricCard("Pending", pendingCount.size.toString(),
                    Icons.Default.Schedule,
                    if (pendingCount.isNotEmpty()) RailLogColors.Warning600 else RailLogColors.Success600,
                    modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(Spacing.itemGap))
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.itemGap)) {
                MetricCard("Kritis pending", criticalPending.toString(),
                    Icons.Default.Warning,
                    if (criticalPending > 0) RailLogColors.Danger600 else RailLogColors.Success600,
                    modifier = Modifier.weight(1f))
                MetricCard("AI Confidence", "$aiConfidence%",
                    Icons.Default.AutoAwesome, RailLogColors.AIText,
                    modifier = Modifier.weight(1f))
            }
        }

        item {
            SectionHeader("Integritas sistem")
            Spacer(Modifier.height(Spacing.itemGap))
            SurfaceCard {
                Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                    SystemStatusRow("Audit engine DB", "Sinkron", RailLogColors.Success600)
                    SubtleDivider(modifier = Modifier.padding(vertical = 10.dp))
                    SystemStatusRow("Gemini AI Core", "Aktif", RailLogColors.Success600)
                    SubtleDivider(modifier = Modifier.padding(vertical = 10.dp))
                    SystemStatusRow("Pusat notifikasi", "Siaga", RailLogColors.Success600)
                }
            }
        }

        item { Spacer(Modifier.height(Spacing.lg)) }
    }
}

@Composable
private fun SystemStatusRow(label: String, status: String, color: Color) {
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = RailLogColors.TextPrimary)
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(color))
            Text(status, style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium, color = color)
        }
    }
}