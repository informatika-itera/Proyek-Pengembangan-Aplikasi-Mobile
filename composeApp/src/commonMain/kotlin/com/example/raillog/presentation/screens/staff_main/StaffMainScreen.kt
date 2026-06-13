package com.example.raillog.presentation.screens.staff_main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.raillog.domain.model.DraftItem
import com.example.raillog.domain.model.SupplyItem
import com.example.raillog.presentation.components.*
import com.example.raillog.presentation.theme.RailLogColors
import com.example.raillog.presentation.theme.Spacing
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

private fun getGreeting(): String {
    val h = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour
    return when (h) {
        in 5..10 -> "Selamat pagi"
        in 11..14 -> "Selamat siang"
        in 15..18 -> "Selamat sore"
        else -> "Selamat malam"
    }
}

private fun formatTs(millis: Long): String {
    if (millis <= 0L) return "Baru saja"
    return try {
        val dt = Instant.fromEpochMilliseconds(millis)
            .toLocalDateTime(TimeZone.currentSystemDefault())
        "${dt.dayOfMonth.toString().padStart(2,'0')}/${dt.monthNumber.toString().padStart(2,'0')}/${dt.year}"
    } catch (e: Exception) { "-" }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffMainScreen(
    viewModel: StaffMainViewModel = koinViewModel(),
    onNavigateToNewRequisition: () -> Unit,
    onNavigateToResumeDraft: (String) -> Unit = {},
    onNavigateToAIAssistant: () -> Unit,
    onLogout: () -> Unit
) {
    val userRole  by viewModel.activeUserRole.collectAsState()
    val allItems  by viewModel.allSupplyItems.collectAsState()
    val allDrafts by viewModel.allDrafts.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf("Beranda", "Pengajuan", "Gudang", "Riwayat")
    val tabIcons = listOf(
        Icons.Default.Home,
        Icons.AutoMirrored.Filled.Assignment,
        Icons.Default.Inventory,
        Icons.Default.History
    )

    Scaffold(
        containerColor = RailLogColors.Background,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(RailLogColors.PrimaryAction)
                                .clickable { selectedTab = 0 },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Train, null,
                                tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                        Spacer(Modifier.width(10.dp))
                        Text("RailLog", fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp, color = RailLogColors.TextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToAIAssistant) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = "Asisten AI",
                            tint = RailLogColors.PrimaryAction,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    var showMenu by remember { mutableStateOf(false) }
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.AccountCircle, null,
                            tint = RailLogColors.TextSecondary, modifier = Modifier.size(26.dp))
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier
                            .background(RailLogColors.Surface)
                            .border(1.dp, RailLogColors.BorderDefault, RoundedCornerShape(10.dp))
                    ) {
                        // Multi-account logic is complex, for now, just show a placeholder
                        DropdownMenuItem(
                            text = {
                                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                    Text("Staff Logistik",
                                        fontWeight = FontWeight.Medium,
                                        color = RailLogColors.TextPrimary)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(100.dp))
                                            .background(RailLogColors.Brand50)
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(userRole, fontSize = 11.sp,
                                            color = RailLogColors.PrimaryAction,
                                            fontWeight = FontWeight.Medium)
                                    }
                                }
                            },
                            onClick = { showMenu = false }
                        )
                        HorizontalDivider(color = RailLogColors.BorderSubtle)
                        DropdownMenuItem(
                            text = {
                                Text("Keluar", color = RailLogColors.Danger600,
                                    fontWeight = FontWeight.Medium)
                            },
                            leadingIcon = {
                                Icon(Icons.AutoMirrored.Filled.Logout, null,
                                    tint = RailLogColors.Danger600, modifier = Modifier.size(16.dp))
                            },
                            onClick = { showMenu = false; onLogout() }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RailLogColors.Surface,
                )
            )
        },
        floatingActionButton = {
            if (selectedTab == 1) {
                FloatingActionButton(
                    onClick = onNavigateToNewRequisition,
                    containerColor = RailLogColors.PrimaryAction,
                    contentColor   = Color.White,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.size(52.dp)
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(22.dp))
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = RailLogColors.Surface,
                tonalElevation = 0.dp,
                modifier = Modifier.border(
                    width = 1.dp,
                    color = RailLogColors.BorderSubtle,
                    shape = RoundedCornerShape(0.dp)
                )
            ) {
                tabs.forEachIndexed { i, label ->
                    NavigationBarItem(
                        selected = selectedTab == i,
                        onClick  = { selectedTab = i },
                        icon = { Icon(tabIcons[i], null, modifier = Modifier.size(20.dp)) },
                        label = {
                            Text(label, fontSize = 10.sp,
                                fontWeight = if (selectedTab == i) FontWeight.SemiBold
                                else FontWeight.Normal)
                        },
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
                0 -> StaffHomeTab(userRole, allItems, onNavigateToNewRequisition)
                1 -> StaffRequestsTab(viewModel, allDrafts, onNavigateToResumeDraft)
                2 -> StaffInventoryTab(viewModel)
                3 -> StaffHistoryTab(viewModel)
            }
        }
    }
}

// ── Home tab ──────────────────────────────────────────────────────────────────

@Composable
fun StaffHomeTab(
    userRole: String,
    allItems: List<SupplyItem>,
    onStart: () -> Unit
) {
    val pending  = allItems.count { it.status.name == "PENDING" }
    val critical = allItems.count { it.priority.name == "CRITICAL" || it.priority.name == "HIGH" }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Spacing.pagePadding),
        verticalArrangement = Arrangement.spacedBy(Spacing.sectionGap)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(getGreeting(), style = MaterialTheme.typography.bodySmall,
                    color = RailLogColors.TextSecondary)
                Text("Selamat bekerja",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold, color = RailLogColors.TextPrimary)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape)
                        .background(if (pending > 0) RailLogColors.Warning600 else RailLogColors.Success600))
                    Text(
                        if (pending > 0) "$pending permintaan menunggu verifikasi"
                        else "Semua permintaan terverifikasi",
                        style = MaterialTheme.typography.bodySmall,
                        color = RailLogColors.TextSecondary
                    )
                }
            }
        }

        // Metrics row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.itemGap)
            ) {
                MetricCard("Inventaris", allItems.size.toString(),
                    Icons.Default.Inventory2, RailLogColors.PrimaryAction,
                    modifier = Modifier.weight(1f))
                MetricCard("Kritis", critical.toString(),
                    Icons.Default.Warning, RailLogColors.Danger600,
                    modifier = Modifier.weight(1f))
                MetricCard("Pending", pending.toString(),
                    Icons.Default.Schedule, RailLogColors.Warning600,
                    modifier = Modifier.weight(1f))
            }
        }

        // Quick action card
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(RailLogColors.PrimaryAction)
                    .clickable(onClick = onStart)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Buat pengajuan baru",
                        fontWeight = FontWeight.SemiBold, color = Color.White, fontSize = 15.sp)
                    Spacer(Modifier.height(2.dp))
                    Text("Requisition wizard 5 langkah",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.75f))
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }

        // Recent activity
        item {
            SectionHeader("Aktivitas terbaru")
        }

        if (allItems.isEmpty()) {
            item {
                EmptyState(Icons.Default.Inbox,
                    "Belum ada aktivitas",
                    "Buat pengajuan pertama Anda")
            }
        } else {
            items(allItems.reversed().take(5)) { item ->
                SupplyItemRow(item)
            }
        }

        item { Spacer(Modifier.height(Spacing.lg)) }
    }
}

@Composable
private fun SupplyItemRow(item: SupplyItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(RailLogColors.Surface)
            .border(1.dp, RailLogColors.BorderDefault, RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium, color = RailLogColors.TextPrimary,
                maxLines = 1)
            Spacer(Modifier.height(2.dp))
            Text(item.partCode, style = MaterialTheme.typography.labelSmall,
                color = RailLogColors.TextTertiary)
        }
        StatusBadge(item.status)
    }
}

// ── Requests tab ──────────────────────────────────────────────────────────────

@Composable
fun StaffRequestsTab(
    viewModel: StaffMainViewModel,
    allDrafts: List<DraftItem>,
    onResume: (String) -> Unit
) {
    var subTab by remember { mutableIntStateOf(0) }
    val labels = listOf("Semua", "Draf", "Pending", "Selesai")
    val query  by viewModel.searchQuery.collectAsState()
    val items  by viewModel.filteredRequestItems.collectAsState()

    val display = when (subTab) {
        2 -> items.filter { it.status.name == "PENDING" }
        3 -> items.filter { it.status.name == "VERIFIED" || it.status.name == "REJECTED" }
        else -> items
    }

    Column(modifier = Modifier.fillMaxSize().background(RailLogColors.Background)) {
        Column(modifier = Modifier.padding(horizontal = Spacing.pagePadding)) {
            Spacer(Modifier.height(Spacing.md))
            PageHeader("Pengajuan", "Daftar semua permintaan material")
            Spacer(Modifier.height(Spacing.md))
            RailLogSearchField(query, { viewModel.updateSearchQuery(it) }, "Cari nama atau kode...")
            Spacer(Modifier.height(Spacing.sm))
        }

        ScrollableTabRow(
            selectedTabIndex = subTab,
            containerColor   = RailLogColors.Background,
            edgePadding = Spacing.pagePadding,
            indicator = { tabPositions ->
                SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[subTab]),
                    color = RailLogColors.PrimaryAction
                )
            },
            divider = { HorizontalDivider(color = RailLogColors.BorderSubtle) }
        ) {
            labels.forEachIndexed { i, title ->
                Tab(
                    selected = subTab == i,
                    onClick  = { subTab = i },
                    text = {
                        Text(title,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (subTab == i) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (subTab == i) RailLogColors.PrimaryAction else RailLogColors.TextSecondary)
                    }
                )
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(Spacing.pagePadding),
            verticalArrangement = Arrangement.spacedBy(Spacing.itemGap)
        ) {
            if (subTab == 0 || subTab == 1) {
                items(allDrafts) { DraftCard(it, { onResume(it.draftId) }) }
            }
            if (subTab != 1) {
                items(display) { RequestCard(it) }
            }
            if (display.isEmpty() && (subTab != 1 || allDrafts.isEmpty())) {
                item {
                    EmptyState(Icons.Default.Inbox, "Tidak ditemukan",
                        "Belum ada data yang sesuai")
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun RequestCard(item: SupplyItem) {
    SurfaceCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.partCode, style = MaterialTheme.typography.labelSmall,
                    color = RailLogColors.TextTertiary)
                Spacer(Modifier.height(2.dp))
                Text(item.name, style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium, color = RailLogColors.TextPrimary)
                Spacer(Modifier.height(4.dp))
                Text("${item.quantity} ${item.unit}",
                    style = MaterialTheme.typography.bodySmall,
                    color = RailLogColors.TextSecondary)
            }
            Column(horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)) {
                StatusBadge(item.status)
                Text(formatTs(item.createdAt.toEpochMilliseconds()),
                    style = MaterialTheme.typography.labelSmall, color = RailLogColors.TextTertiary)
            }
        }
    }
}

@Composable
private fun DraftCard(draft: DraftItem, onClick: () -> Unit) {
    SurfaceCard(onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(RailLogColors.Warning50)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text("Draf", fontSize = 11.sp,
                        color = RailLogColors.Warning600, fontWeight = FontWeight.Medium)
                }
                Spacer(Modifier.height(6.dp))
                Text(draft.projectTitle.ifEmpty { "Pengajuan belum selesai" },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium, color = RailLogColors.TextPrimary)
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { draft.currentStep / 5f },
                    modifier = Modifier.fillMaxWidth().height(3.dp).clip(RoundedCornerShape(2.dp)),
                    color = RailLogColors.PrimaryAction,
                    trackColor = RailLogColors.Neutral200
                )
                Spacer(Modifier.height(4.dp))
                Text("Langkah ${draft.currentStep} dari 5",
                    style = MaterialTheme.typography.labelSmall, color = RailLogColors.TextTertiary)
            }
            Spacer(Modifier.width(12.dp))
            Icon(Icons.Default.ChevronRight, null,
                tint = RailLogColors.TextTertiary, modifier = Modifier.size(18.dp))
        }
    }
}

// ── Inventory tab ─────────────────────────────────────────────────────────────

@Composable
fun StaffInventoryTab(viewModel: StaffMainViewModel) {
    val cats = listOf("All", "Infrastructure", "Bogie", "Propulsion", "Braking", "Tools")
    val query       by viewModel.searchQuery.collectAsState()
    val selectedCat by viewModel.inventoryCategory.collectAsState()
    val items       by viewModel.filteredInventoryItems.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(RailLogColors.Background)) {
        Column(modifier = Modifier.padding(horizontal = Spacing.pagePadding)) {
            Spacer(Modifier.height(Spacing.md))
            PageHeader("Gudang", "${items.size} item tersedia")
            Spacer(Modifier.height(Spacing.md))
            RailLogSearchField(query, { viewModel.updateSearchQuery(it) }, "Cari komponen...")
            Spacer(Modifier.height(Spacing.sm))
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = Spacing.pagePadding),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(cats) { cat ->
                val selected = selectedCat == cat
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(
                            if (selected) RailLogColors.PrimaryAction else RailLogColors.Surface
                        )
                        .border(
                            1.dp,
                            if (selected) RailLogColors.PrimaryAction else RailLogColors.BorderDefault,
                            RoundedCornerShape(100.dp)
                        )
                        .clickable { viewModel.updateInventoryCategory(cat) }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        cat,
                        fontSize = 12.sp,
                        fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                        color = if (selected) Color.White else RailLogColors.TextSecondary
                    )
                }
            }
        }

        Spacer(Modifier.height(Spacing.sm))

        LazyColumn(
            contentPadding = PaddingValues(Spacing.pagePadding),
            verticalArrangement = Arrangement.spacedBy(Spacing.itemGap)
        ) {
            if (items.isEmpty()) {
                item {
                    EmptyState(Icons.Default.Inventory2, "Tidak ditemukan",
                        "Coba ubah filter atau kata kunci pencarian")
                }
            } else {
                items(items) { InventoryItemCard(it) }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun InventoryItemCard(item: SupplyItem) {
    SurfaceCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium, color = RailLogColors.TextPrimary)
                Spacer(Modifier.height(2.dp))
                Text(item.partCode, style = MaterialTheme.typography.labelSmall,
                    color = RailLogColors.TextTertiary)
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(RailLogColors.Neutral100)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(item.category.name, fontSize = 11.sp, color = RailLogColors.TextSecondary)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${item.quantity}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold, color = RailLogColors.TextPrimary)
                Text(item.unit, style = MaterialTheme.typography.labelSmall,
                    color = RailLogColors.TextTertiary)
            }
        }
    }
}

// ── History tab ───────────────────────────────────────────────────────────────

@Composable
fun StaffHistoryTab(viewModel: StaffMainViewModel) {
    val filters = listOf("Semua", "Pending", "Terverifikasi", "Ditolak")
    val query  by viewModel.historySearchQuery.collectAsState()
    val filter by viewModel.historyFilter.collectAsState()
    val items  by viewModel.filteredHistoryItems.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(RailLogColors.Background)) {
        Column(modifier = Modifier.padding(horizontal = Spacing.pagePadding)) {
            Spacer(Modifier.height(Spacing.md))
            PageHeader("Riwayat audit", "${items.size} entri")
            Spacer(Modifier.height(Spacing.md))
            RailLogSearchField(query, { viewModel.updateHistorySearchQuery(it) }, "Cari kode proyek...")
            Spacer(Modifier.height(Spacing.sm))
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = Spacing.pagePadding),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(filters) { f ->
                val isSelected = filter.contains(f, true) ||
                        (f == "Semua" && filter == "All Requests")
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(if (isSelected) RailLogColors.PrimaryAction else RailLogColors.Surface)
                        .border(1.dp,
                            if (isSelected) RailLogColors.PrimaryAction else RailLogColors.BorderDefault,
                            RoundedCornerShape(100.dp))
                        .clickable {
                            viewModel.updateHistoryFilter(if (f == "Semua") "All Requests" else f)
                        }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(f, fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                        color = if (isSelected) Color.White else RailLogColors.TextSecondary)
                }
            }
        }

        Spacer(Modifier.height(Spacing.sm))

        LazyColumn(
            contentPadding = PaddingValues(Spacing.pagePadding),
            verticalArrangement = Arrangement.spacedBy(Spacing.itemGap)
        ) {
            if (items.isEmpty()) {
                item { EmptyState(Icons.Default.History, "Belum ada riwayat", "Mulai buat pengajuan baru") }
            } else {
                items(items) { RequestCard(it) }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}