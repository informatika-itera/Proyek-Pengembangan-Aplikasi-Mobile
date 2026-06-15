package com.studymate.presentation.screens.calendar

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studymate.domain.model.Reminder
import com.studymate.domain.repository.CalendarEvent
import com.studymate.presentation.theme.*
import kotlinx.datetime.*
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val reminders by viewModel.reminders.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0: Weekly, 1: Monthly
    var showTypeSelection by remember { mutableStateOf(false) }
    var showAddEventDialog by remember { mutableStateOf(false) }
    var showAddReminderDialog by remember { mutableStateOf(false) }

    var selectedDetailItem by remember { mutableStateOf<Any?>(null) }

    // Collapsing Calendar Logic for Monthly view
    val calendarHeight = 280.dp
    val calendarHeightPx = with(LocalDensity.current) { calendarHeight.toPx() }
    val collapsedHeight = 60.dp
    val collapsedHeightPx = with(LocalDensity.current) { collapsedHeight.toPx() }
    
    var calendarOffsetHeightPx by remember { mutableStateOf(0f) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (selectedTab == 0) return Offset.Zero // No collapse in weekly
                val delta = available.y
                val newOffset = calendarOffsetHeightPx + delta
                calendarOffsetHeightPx = newOffset.coerceIn(-(calendarHeightPx - collapsedHeightPx), 0f)
                return Offset.Zero
            }
        }
    }

    if (showTypeSelection) {
        AlertDialog(
            onDismissRequest = { showTypeSelection = false },
            title = { Text("Pilih Jenis Baru") },
            text = { Text("Apa yang ingin Anda tambahkan ke planner?") },
            confirmButton = {
                Button(onClick = { 
                    showAddEventDialog = true 
                    showTypeSelection = false
                }) { Text("Agenda") }
            },
            dismissButton = {
                Button(onClick = { 
                    showAddReminderDialog = true 
                    showTypeSelection = false
                }) { Text("Pengingat") }
            }
        )
    }

    if (showAddEventDialog) {
        AddEventDialog(
            selectedDate = uiState.selectedDate,
            onDismiss = { showAddEventDialog = false },
            onSave = { title, desc, start, end ->
                viewModel.addEvent(title, desc, start, end)
                showAddEventDialog = false
            }
        )
    }

    if (showAddReminderDialog) {
        AddReminderDialog(
            initialDate = uiState.selectedDate,
            onDismiss = { showAddReminderDialog = false },
            onSave = { title, desc, due ->
                viewModel.addReminder(title, desc, due)
                showAddReminderDialog = false
            }
        )
    }

    if (selectedDetailItem != null) {
        DetailDialog(
            item = selectedDetailItem!!,
            onDismiss = { selectedDetailItem = null },
            onDelete = {
                val item = selectedDetailItem
                when (item) {
                    is CalendarEvent -> item.id?.let { viewModel.deleteEvent(it) }
                    is Reminder -> viewModel.deleteReminder(item.id)
                }
                selectedDetailItem = null
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Surface(
                modifier = Modifier.shadow(4.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column {
                    CenterAlignedTopAppBar(
                        title = { Text("Planner", fontWeight = FontWeight.Black) }
                    )
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        divider = {},
                        indicator = { tabPositions ->
                            if (selectedTab < tabPositions.size) {
                                Box(
                                    Modifier
                                        .tabIndicatorOffset(tabPositions[selectedTab])
                                        .height(3.dp)
                                        .padding(horizontal = 40.dp)
                                        .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                                        .background(PrimaryLight)
                                )
                            }
                        }
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { 
                                selectedTab = 0 
                                calendarOffsetHeightPx = 0f
                            },
                            selectedContentColor = PrimaryLight,
                            unselectedContentColor = Color.Gray
                        ) {
                            Text("Weekly", modifier = Modifier.padding(14.dp), fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium)
                        }
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            selectedContentColor = PrimaryLight,
                            unselectedContentColor = Color.Gray
                        ) {
                            Text("Monthly", modifier = Modifier.padding(14.dp), fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium)
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showTypeSelection = true },
                containerColor = ActionFABLight,
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding())
                .nestedScroll(nestedScrollConnection)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Calendar Header (Month Year + Nav)
                CalendarHeaderSection(
                    selectedDate = uiState.selectedDate,
                    onPrev = { viewModel.navigatePrev(selectedTab == 1) },
                    onNext = { viewModel.navigateNext(selectedTab == 1) }
                )

                // Calendar Content Area
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (selectedTab == 0) {
                        CalendarGridSection(
                            selectedDate = uiState.selectedDate,
                            reminders = reminders,
                            events = uiState.events,
                            onDateSelected = { viewModel.onDateSelected(it) }
                        )
                    } else {
                        // Monthly grid with collapsing logic
                        Box(modifier = Modifier
                            .height(calendarHeight + (calendarOffsetHeightPx / with(LocalDensity.current) { 1.dp.toPx() }).dp)
                            .clip(RectangleShape)
                        ) {
                            MonthlyGridSection(
                                selectedDate = uiState.selectedDate,
                                reminders = reminders,
                                events = uiState.events,
                                onDateSelected = { viewModel.onDateSelected(it) },
                                isCollapsed = calendarOffsetHeightPx < -100f
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Daily Agenda List Header
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Agenda Hari Ini",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    val dailyEvents = uiState.events.filter { 
                        Instant.fromEpochMilliseconds(it.startTime).toLocalDateTime(TimeZone.currentSystemDefault()).date == uiState.selectedDate
                    }
                    val dailyReminders = reminders.filter { 
                        Instant.fromEpochMilliseconds(it.dueDate).toLocalDateTime(TimeZone.currentSystemDefault()).date == uiState.selectedDate
                    }
                    val count = dailyEvents.size + dailyReminders.size
                    Surface(color = PrimaryLight.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                        Text(
                            "$count Agenda",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = PrimaryLight,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Scrollable List
                TimelineListSection(
                    events = uiState.events.filter { 
                        Instant.fromEpochMilliseconds(it.startTime).toLocalDateTime(TimeZone.currentSystemDefault()).date == uiState.selectedDate
                    },
                    reminders = reminders.filter { 
                        Instant.fromEpochMilliseconds(it.dueDate).toLocalDateTime(TimeZone.currentSystemDefault()).date == uiState.selectedDate
                    },
                    onItemClick = { selectedDetailItem = it }
                )
            }
        }
    }
}

@Composable
fun CalendarHeaderSection(selectedDate: LocalDate, onPrev: () -> Unit, onNext: () -> Unit) {
    val monthName = remember(selectedDate) {
        selectedDate.month.name.lowercase().replaceFirstChar { it.uppercase() }
    }
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("$monthName ${selectedDate.year}", fontWeight = FontWeight.Black, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground)
            Text("Aktivitas Terjadwal", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Row(modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))) {
            IconButton(onClick = onPrev) { Icon(Icons.Default.ChevronLeft, null, modifier = Modifier.size(20.dp)) }
            IconButton(onClick = onNext) { Icon(Icons.Default.ChevronRight, null, modifier = Modifier.size(20.dp)) }
        }
    }
}

@Composable
fun CalendarGridSection(selectedDate: LocalDate, reminders: List<Reminder>, events: List<CalendarEvent>, onDateSelected: (LocalDate) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
        val days = listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab")
        val startOfWeek = selectedDate.minus((selectedDate.dayOfWeek.ordinal + 1) % 7, DateTimeUnit.DAY)
        repeat(7) { i ->
            val date = startOfWeek.plus(i, DateTimeUnit.DAY)
            val isSelected = date == selectedDate
            val isToday = date == Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            val hasReminder = reminders.any { Instant.fromEpochMilliseconds(it.dueDate).toLocalDateTime(TimeZone.currentSystemDefault()).date == date }
            val hasHoliday = events.any { it.isHoliday && Instant.fromEpochMilliseconds(it.startTime).toLocalDateTime(TimeZone.currentSystemDefault()).date == date }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(48.dp).clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) PrimaryLight else if (isToday) PrimaryLight.copy(alpha = 0.1f) else Color.Transparent)
                    .clickable { onDateSelected(date) }.padding(vertical = 12.dp)
            ) {
                Text(days[i], color = if (isSelected) Color.White else if (hasHoliday) Color(0xFF4CAF50) else Color.Gray, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Box(contentAlignment = Alignment.Center) {
                    Text(date.dayOfMonth.toString(), fontWeight = FontWeight.Black, fontSize = 16.sp, color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface)
                    if (hasReminder && !isSelected) Box(modifier = Modifier.align(Alignment.BottomCenter).offset(y = 8.dp).size(4.dp).clip(CircleShape).background(ActionFABLight))
                }
            }
        }
    }
}

@Composable
fun MonthlyGridSection(selectedDate: LocalDate, reminders: List<Reminder>, events: List<CalendarEvent>, onDateSelected: (LocalDate) -> Unit, isCollapsed: Boolean) {
    if (isCollapsed) {
        CalendarGridSection(selectedDate, reminders, events, onDateSelected)
    } else {
        val firstDay = LocalDate(selectedDate.year, selectedDate.month, 1)
        val daysInMonth = selectedDate.month.number.let { m -> if (m == 2) (if (selectedDate.year % 4 == 0) 29 else 28) else if (m in listOf(4,6,9,11)) 30 else 31 }
        val startPadding = (firstDay.dayOfWeek.ordinal + 1) % 7
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("M", "S", "S", "R", "K", "J", "S").forEach { Text(it, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold) }
            }
            Spacer(modifier = Modifier.height(8.dp))
            LazyVerticalGrid(columns = GridCells.Fixed(7), modifier = Modifier.height(240.dp)) {
                items(List(startPadding) { null } + List(daysInMonth) { it + 1 }) { day ->
                    if (day != null) {
                        val date = LocalDate(selectedDate.year, selectedDate.month, day)
                        val isSelected = date == selectedDate
                        val isToday = date == today
                        val hasReminder = reminders.any { Instant.fromEpochMilliseconds(it.dueDate).toLocalDateTime(TimeZone.currentSystemDefault()).date == date }
                        val hasHoliday = events.any { it.isHoliday && Instant.fromEpochMilliseconds(it.startTime).toLocalDateTime(TimeZone.currentSystemDefault()).date == date }
                        Box(modifier = Modifier.aspectRatio(1f).clip(RoundedCornerShape(8.dp)).background(if (isSelected) PrimaryLight else if (isToday) PrimaryLight.copy(alpha = 0.1f) else Color.Transparent).clickable { onDateSelected(date) }, contentAlignment = Alignment.Center) {
                            Text(day.toString(), fontSize = 14.sp, fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal, color = if (isSelected) Color.White else if (hasHoliday) Color(0xFF4CAF50) else if (isToday) PrimaryLight else MaterialTheme.colorScheme.onSurface)
                            if (hasReminder && !isSelected) Box(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 2.dp).size(4.dp).clip(CircleShape).background(ActionFABLight))
                        }
                    } else Box(modifier = Modifier.aspectRatio(1f))
                }
            }
        }
    }
}

@Composable
fun TimelineListSection(events: List<CalendarEvent>, reminders: List<Reminder>, onItemClick: (Any) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items(reminders) { reminder ->
            Surface(modifier = Modifier.fillMaxWidth().clickable { onItemClick(reminder) }, shape = RoundedCornerShape(20.dp), color = ActionFABLight.copy(alpha = 0.1f), border = BorderStroke(1.dp, ActionFABLight.copy(alpha = 0.2f))) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.NotificationsActive, null, tint = ActionFABLight, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        val timeStr = remember(reminder.dueDate) {
                            val dt = Instant.fromEpochMilliseconds(reminder.dueDate).toLocalDateTime(TimeZone.currentSystemDefault())
                            "${dt.hour.toString().padStart(2, '0')}:${dt.minute.toString().padStart(2, '0')}"
                        }
                        Text(reminder.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                        Text("Batas Waktu: $timeStr", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
            }
        }
        items(events) { event ->
            val startTimeStr = remember(event.startTime) {
                val dt = Instant.fromEpochMilliseconds(event.startTime).toLocalDateTime(TimeZone.currentSystemDefault())
                "${dt.hour.toString().padStart(2, '0')}:${dt.minute.toString().padStart(2, '0')}"
            }
            val accentColor = if (event.isHoliday) Color(0xFF4CAF50) else Color(0xFF4361EE)
            Surface(modifier = Modifier.fillMaxWidth().clickable { onItemClick(event) }, shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, accentColor.copy(alpha = 0.2f))) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccessTime, null, tint = accentColor, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(event.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                        Text("$startTimeStr • ${if (event.isHoliday) "Libur Nasional" else "Agenda"}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun DetailDialog(item: Any, onDismiss: () -> Unit, onDelete: () -> Unit) {
    val title = when (item) { is CalendarEvent -> item.title; is Reminder -> item.title; else -> "" }
    val desc = when (item) { is CalendarEvent -> item.description; is Reminder -> item.description; else -> null } ?: "Tidak ada keterangan"
    val time = when (item) {
        is CalendarEvent -> { val dt = Instant.fromEpochMilliseconds(item.startTime).toLocalDateTime(TimeZone.currentSystemDefault()); "${dt.dayOfMonth} ${dt.month.name} ${dt.year}, ${dt.hour}:${dt.minute.toString().padStart(2, '0')}" }
        is Reminder -> { val dt = Instant.fromEpochMilliseconds(item.dueDate).toLocalDateTime(TimeZone.currentSystemDefault()); "${dt.dayOfMonth} ${dt.month.name} ${dt.year}, ${dt.hour}:${dt.minute.toString().padStart(2, '0')}" }
        else -> ""
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Black) },
        text = { Column { Text(time, style = MaterialTheme.typography.labelMedium, color = PrimaryLight); Spacer(modifier = Modifier.height(16.dp)); Text(desc) } },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Tutup") } },
        dismissButton = { TextButton(onClick = onDelete, colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)) { Text("Hapus") } }
    )
}

@Composable
fun AddEventDialog(selectedDate: LocalDate, onDismiss: () -> Unit, onSave: (String, String?, Long, Long) -> Unit) {
    var title by remember { mutableStateOf("") }; var desc by remember { mutableStateOf("") }; var h by remember { mutableStateOf("09") }; var m by remember { mutableStateOf("00") }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Tambah Agenda") }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Judul") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth())
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Jam Mulai:"); OutlinedTextField(value = h, onValueChange = { h = it }, modifier = Modifier.width(60.dp)); Text(":"); OutlinedTextField(value = m, onValueChange = { m = it }, modifier = Modifier.width(60.dp))
            }
        }
    }, confirmButton = { Button(onClick = { val dt = LocalDateTime(selectedDate.year, selectedDate.month, selectedDate.dayOfMonth, h.toIntOrNull() ?: 9, m.toIntOrNull() ?: 0); val epoch = dt.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(); onSave(title, desc.ifBlank { null }, epoch, epoch + 3600000) }, enabled = title.isNotBlank()) { Text("Simpan") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderDialog(initialDate: LocalDate, onDismiss: () -> Unit, onSave: (String, String?, Long) -> Unit) {
    var title by remember { mutableStateOf("") }; var desc by remember { mutableStateOf("") }; var date by remember { mutableStateOf(initialDate) }; var h by remember { mutableStateOf("09") }; var m by remember { mutableStateOf("00") }; var showDP by remember { mutableStateOf(false) }
    val dpState = rememberDatePickerState(initialSelectedDateMillis = date.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds())
    if (showDP) { DatePickerDialog(onDismissRequest = { showDP = false }, confirmButton = { TextButton(onClick = { dpState.selectedDateMillis?.let { date = Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.UTC).date }; showDP = false }) { Text("Pilih") } }) { DatePicker(state = dpState) } }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Buat Pengingat") }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Judul") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Keterangan") }, modifier = Modifier.fillMaxWidth())
            Row(modifier = Modifier.fillMaxWidth().clickable { showDP = true }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarMonth, null, tint = PrimaryLight); Spacer(modifier = Modifier.width(12.dp)); Column { Text("Tanggal:", style = MaterialTheme.typography.labelSmall); Text("${date.dayOfMonth} ${date.month.name} ${date.year}", fontWeight = FontWeight.Bold) }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccessTime, null, tint = PrimaryLight); Spacer(modifier = Modifier.width(12.dp)); Text("Jam:"); Spacer(modifier = Modifier.width(8.dp)); OutlinedTextField(value = h, onValueChange = { h = it }, modifier = Modifier.width(60.dp)); Text(":", modifier = Modifier.padding(horizontal = 4.dp)); OutlinedTextField(value = m, onValueChange = { m = it }, modifier = Modifier.width(60.dp))
            }
        }
    }, confirmButton = { Button(onClick = { val dt = LocalDateTime(date.year, date.month, date.dayOfMonth, h.toIntOrNull() ?: 9, m.toIntOrNull() ?: 0); onSave(title, desc.ifBlank { null }, dt.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()) }, enabled = title.isNotBlank()) { Text("Pasang") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } })
}

private fun LocalDate.atStartOfDayIn(zone: TimeZone): Instant = LocalDateTime(this, LocalTime(0,0)).toInstant(zone)
