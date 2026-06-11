package com.studymate.presentation.screens.calendar

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studymate.domain.model.Reminder
import com.studymate.domain.repository.CalendarEvent
import com.studymate.presentation.theme.*
import kotlinx.datetime.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val reminders by viewModel.reminders.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    var showTypeSelection by remember { mutableStateOf(false) }
    var showAddEventDialog by remember { mutableStateOf(false) }
    var showAddReminderDialog by remember { mutableStateOf(false) }

    var selectedDetailItem by remember { mutableStateOf<Any?>(null) }

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
                when (selectedDetailItem) {
                    is CalendarEvent -> (selectedDetailItem as CalendarEvent).id?.let { viewModel.deleteEvent(it) }
                    is Reminder -> viewModel.deleteReminder((selectedDetailItem as Reminder).id)
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
                            onClick = { selectedTab = 0 },
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
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            CalendarHeaderSection(
                selectedDate = uiState.selectedDate,
                onPrev = { viewModel.navigatePrev(selectedTab == 1) },
                onNext = { viewModel.navigateNext(selectedTab == 1) }
            )
            
            if (selectedTab == 0) {
                CalendarGridSection(
                    selectedDate = uiState.selectedDate,
                    reminders = reminders,
                    events = uiState.events,
                    onDateSelected = { viewModel.onDateSelected(it) }
                )
            } else {
                MonthlyGridSection(
                    selectedDate = uiState.selectedDate,
                    reminders = reminders,
                    events = uiState.events,
                    onDateSelected = { viewModel.onDateSelected(it) }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            val dailyEvents = uiState.events.filter { 
                Instant.fromEpochMilliseconds(it.startTime).toLocalDateTime(TimeZone.currentSystemDefault()).date == uiState.selectedDate
            }
            val dailyReminders = reminders.filter { 
                Instant.fromEpochMilliseconds(it.dueDate).toLocalDateTime(TimeZone.currentSystemDefault()).date == uiState.selectedDate
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Agenda Hari Ini",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black
                )
                Surface(
                    color = PrimaryLight.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    val count = dailyEvents.size + dailyReminders.size
                    Text(
                        "$count Agenda",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = PrimaryLight,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            TimelineListSection(
                events = dailyEvents,
                reminders = dailyReminders,
                onItemClick = { selectedDetailItem = it }
            )
        }
    }
}

@Composable
fun CalendarHeaderSection(
    selectedDate: LocalDate,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    val monthName = remember(selectedDate) {
        selectedDate.month.name.lowercase().replaceFirstChar { it.uppercase() }
    }
    Row(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "$monthName ${selectedDate.year}", 
                fontWeight = FontWeight.Black, 
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "Aktivitas Terjadwal", 
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            IconButton(onClick = onPrev) { Icon(Icons.Default.ChevronLeft, null, modifier = Modifier.size(20.dp)) }
            IconButton(onClick = onNext) { Icon(Icons.Default.ChevronRight, null, modifier = Modifier.size(20.dp)) }
        }
    }
}

@Composable
fun CalendarGridSection(
    selectedDate: LocalDate,
    reminders: List<Reminder>,
    events: List<CalendarEvent>,
    onDateSelected: (LocalDate) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val days = listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab")
        // Start week with Sunday on the left
        val startOfWeek = selectedDate.minus((selectedDate.dayOfWeek.ordinal + 1) % 7, DateTimeUnit.DAY)
        
        repeat(7) { i ->
            val date = startOfWeek.plus(i, DateTimeUnit.DAY)
            val isSelected = date == selectedDate
            val isToday = date == Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            
            val hasReminder = reminders.any { 
                Instant.fromEpochMilliseconds(it.dueDate).toLocalDateTime(TimeZone.currentSystemDefault()).date == date 
            }
            val hasHoliday = events.any { 
                it.isHoliday && Instant.fromEpochMilliseconds(it.startTime).toLocalDateTime(TimeZone.currentSystemDefault()).date == date 
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .then(
                        if (isSelected) {
                            Modifier.background(
                                brush = Brush.verticalGradient(colors = listOf(PrimaryLight, SecondaryLight))
                            ).shadow(8.dp, RoundedCornerShape(16.dp))
                        } else if (isToday) {
                            Modifier.background(PrimaryLight.copy(alpha = 0.1f))
                        } else {
                            Modifier.background(Color.Transparent)
                        }
                    )
                    .clickable { onDateSelected(date) }
                    .padding(vertical = 12.dp)
            ) {
                Text(
                    days[i], 
                    color = if (isSelected) Color.White else if (hasHoliday) Color(0xFF4CAF50) else Color.Gray, 
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        date.dayOfMonth.toString(), 
                        fontWeight = FontWeight.Black, 
                        fontSize = 16.sp,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                    if (hasReminder && !isSelected) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = 8.dp)
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(ActionFABLight)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MonthlyGridSection(
    selectedDate: LocalDate,
    reminders: List<Reminder>,
    events: List<CalendarEvent>,
    onDateSelected: (LocalDate) -> Unit
) {
    val firstDayOfMonth = LocalDate(selectedDate.year, selectedDate.month, 1)
    val daysInMonth = selectedDate.month.number.let { month ->
        if (month == 2) {
            if (selectedDate.year % 4 == 0 && (selectedDate.year % 100 != 0 || selectedDate.year % 400 == 0)) 29 else 28
        } else if (month in listOf(4, 6, 9, 11)) 30 else 31
    }
    
    val startPadding = (firstDayOfMonth.dayOfWeek.ordinal + 1) % 7
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf("M", "S", "S", "R", "K", "J", "S").forEach { 
                Text(it, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.height(240.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(List(startPadding) { null } + List(daysInMonth) { it + 1 }) { day ->
                if (day != null) {
                    val date = LocalDate(selectedDate.year, selectedDate.month, day)
                    val isSelected = date == selectedDate
                    val isToday = date == today
                    
                    val hasReminder = reminders.any { 
                        Instant.fromEpochMilliseconds(it.dueDate).toLocalDateTime(TimeZone.currentSystemDefault()).date == date 
                    }
                    val hasHoliday = events.any { 
                        it.isHoliday && Instant.fromEpochMilliseconds(it.startTime).toLocalDateTime(TimeZone.currentSystemDefault()).date == date 
                    }

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when {
                                    isSelected -> PrimaryLight
                                    isToday -> PrimaryLight.copy(alpha = 0.1f)
                                    else -> Color.Transparent
                                }
                            )
                            .clickable { onDateSelected(date) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            day.toString(),
                            fontSize = 14.sp,
                            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                            color = when {
                                isSelected -> Color.White
                                hasHoliday -> Color(0xFF4CAF50)
                                isToday -> PrimaryLight
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                        if (hasReminder && !isSelected) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 2.dp)
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(ActionFABLight)
                            )
                        }
                    }
                } else {
                    Box(modifier = Modifier.aspectRatio(1f))
                }
            }
        }
    }
}

@Composable
fun TimelineListSection(
    events: List<CalendarEvent>,
    reminders: List<Reminder>,
    onItemClick: (Any) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(reminders) { reminder ->
            Row(modifier = Modifier.fillMaxWidth().clickable { onItemClick(reminder) }, verticalAlignment = Alignment.Top) {
                Column(horizontalAlignment = Alignment.End, modifier = Modifier.width(50.dp)) {
                    Text("Pengingat", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, color = ActionFABLight)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Surface(
                    modifier = Modifier.weight(1f).shadow(2.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    color = ActionFABLight.copy(alpha = 0.05f),
                    border = BorderStroke(1.dp, ActionFABLight.copy(alpha = 0.2f))
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NotificationsActive, null, tint = ActionFABLight)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            val timeStr = remember(reminder.dueDate) {
                                val dt = Instant.fromEpochMilliseconds(reminder.dueDate).toLocalDateTime(TimeZone.currentSystemDefault())
                                "${dt.hour.toString().padStart(2, '0')}:${dt.minute.toString().padStart(2, '0')}"
                            }
                            Text(reminder.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text("Waktu: $timeStr", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                    }
                }
            }
        }
        
        items(events) { event ->
            val startTimeStr = remember(event.startTime) {
                val instant = Instant.fromEpochMilliseconds(event.startTime)
                val dt = instant.toLocalDateTime(TimeZone.currentSystemDefault())
                "${dt.hour.toString().padStart(2, '0')}:${dt.minute.toString().padStart(2, '0')}"
            }
            val color = remember(event.color, event.isHoliday) {
                if (event.isHoliday) Color(0xFF4CAF50)
                else try {
                    Color(event.color?.removePrefix("#")?.toLong(16) ?: 0xFF4CC9F0)
                } catch (_: Exception) {
                    Color(0xFF4CC9F0)
                }
            }

            Row(modifier = Modifier.fillMaxWidth().clickable { onItemClick(event) }, verticalAlignment = Alignment.Top) {
                Column(horizontalAlignment = Alignment.End, modifier = Modifier.width(50.dp)) {
                    Text(startTimeStr, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text(if (event.isHoliday) "Libur" else "Agenda", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Surface(
                    modifier = Modifier.weight(1f).shadow(2.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.width(4.dp).height(40.dp).clip(CircleShape).background(color))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(event.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            event.description?.let {
                                Text(it, style = MaterialTheme.typography.bodySmall, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailDialog(
    item: Any,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    val title = when (item) {
        is CalendarEvent -> item.title
        is Reminder -> item.title
        else -> ""
    }
    val description = when (item) {
        is CalendarEvent -> item.description ?: "Tidak ada keterangan"
        is Reminder -> item.description ?: "Tidak ada keterangan"
        else -> ""
    }
    val timeStr = when (item) {
        is CalendarEvent -> {
            val dt = Instant.fromEpochMilliseconds(item.startTime).toLocalDateTime(TimeZone.currentSystemDefault())
            "${dt.dayOfMonth} ${dt.month.name} ${dt.year}, ${dt.hour}:${dt.minute.toString().padStart(2, '0')}"
        }
        is Reminder -> {
            val dt = Instant.fromEpochMilliseconds(item.dueDate).toLocalDateTime(TimeZone.currentSystemDefault())
            "${dt.dayOfMonth} ${dt.month.name} ${dt.year}, ${dt.hour}:${dt.minute.toString().padStart(2, '0')}"
        }
        else -> ""
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Black) },
        text = {
            Column {
                Text(timeStr, style = MaterialTheme.typography.labelMedium, color = PrimaryLight)
                Spacer(modifier = Modifier.height(16.dp))
                Text(description)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Tutup") }
        },
        dismissButton = {
            TextButton(onClick = onDelete, colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)) {
                Text("Hapus")
            }
        }
    )
}

@Composable
fun AddEventDialog(
    selectedDate: LocalDate,
    onDismiss: () -> Unit,
    onSave: (String, String?, Long, Long) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startHour by remember { mutableStateOf("09") }
    var startMinute by remember { mutableStateOf("00") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Agenda") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul Agenda") },
                    placeholder = { Text("Misal: Kuliah Pemrograman") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Deskripsi (Opsional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Jam Mulai:", style = MaterialTheme.typography.bodyMedium)
                    OutlinedTextField(
                        value = startHour,
                        onValueChange = { if (it.length <= 2) startHour = it },
                        modifier = Modifier.width(60.dp),
                        singleLine = true
                    )
                    Text(":")
                    OutlinedTextField(
                        value = startMinute,
                        onValueChange = { if (it.length <= 2) startMinute = it },
                        modifier = Modifier.width(60.dp),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val hour = startHour.toIntOrNull() ?: 9
                    val minute = startMinute.toIntOrNull() ?: 0
                    val startDateTime = LocalDateTime(selectedDate.year, selectedDate.month, selectedDate.dayOfMonth, hour, minute)
                    val startEpoch = startDateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                    onSave(title, description.ifBlank { null }, startEpoch, startEpoch + 3600000)
                },
                enabled = title.isNotBlank()
            ) { Text("Simpan") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderDialog(
    initialDate: LocalDate,
    onDismiss: () -> Unit,
    onSave: (String, String?, Long) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(initialDate) }
    var startHour by remember { mutableStateOf("09") }
    var startMinute by remember { mutableStateOf("00") }
    
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.toInstant(LocalTime(0, 0), TimeZone.currentSystemDefault()).toEpochMilliseconds()
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        selectedDate = Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.UTC).date
                    }
                    showDatePicker = false
                }) { Text("Pilih") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Batal") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Buat Pengingat") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul Pengingat") },
                    placeholder = { Text("Misal: Deadline Tugas") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Keterangan") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CalendarMonth, null, tint = PrimaryLight)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Tanggal:", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text(
                            "${selectedDate.dayOfMonth} ${selectedDate.month.name} ${selectedDate.year}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AccessTime, null, tint = PrimaryLight)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Jam:", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = startHour,
                        onValueChange = { if (it.length <= 2) startHour = it },
                        modifier = Modifier.width(60.dp),
                        singleLine = true
                    )
                    Text(":", modifier = Modifier.padding(horizontal = 4.dp))
                    OutlinedTextField(
                        value = startMinute,
                        onValueChange = { if (it.length <= 2) startMinute = it },
                        modifier = Modifier.width(60.dp),
                        singleLine = true
                    )
                }
                
                Text(
                    "Notifikasi akan dikirim 3 hari, 2 hari, dan 1 hari sebelum deadline.",
                    style = MaterialTheme.typography.labelSmall,
                    color = ActionFABLight
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val hour = startHour.toIntOrNull() ?: 9
                    val minute = startMinute.toIntOrNull() ?: 0
                    val dueDateTime = LocalDateTime(selectedDate.year, selectedDate.month, selectedDate.dayOfMonth, hour, minute)
                    val dueEpoch = dueDateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                    onSave(title, description.ifBlank { null }, dueEpoch)
                },
                enabled = title.isNotBlank()
            ) { Text("Pasang Pengingat") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}

private fun LocalDate.toInstant(time: LocalTime, zone: TimeZone): Instant {
    return LocalDateTime(this, time).toInstant(zone)
}
