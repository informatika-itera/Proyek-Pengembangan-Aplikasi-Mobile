package com.example.sholatyuk.presentation.screens.kajian

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.sholatyuk.domain.model.KajianCategory
import com.example.sholatyuk.domain.model.KajianNote
import com.example.sholatyuk.presentation.theme.*
import com.example.sholatyuk.presentation.screens.profile.ProfileViewModel
import kotlinx.datetime.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KajianScreen(
    onNavigateBack: () -> Unit,
    viewModel: KajianViewModel = koinViewModel(),
    profileViewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isLightModeEnabled by profileViewModel.isLightModeEnabled.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }
    var noteToEdit by remember { mutableStateOf<KajianNote?>(null) }
    var noteToDelete by remember { mutableStateOf<KajianNote?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = if (isLightModeEnabled) DeepBlue else AccentYellow,
                contentColor = if (isLightModeEnabled) Color.White else DeepBlue,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Catatan")
            }
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = if (isLightModeEnabled) {
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFFE0F2F1), Color(0xFFF5F5F5)),
                            startY = 0f,
                            endY = 1200f
                        )
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(DarkTeal, DeepBlue),
                            startY = 0f,
                            endY = 1200f
                        )
                    }
                )
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header (Consistent with Home, Prayer, Doa, and IslamAI screens)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, start = 20.dp, end = 20.dp, bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background((if (isLightModeEnabled) DeepBlue else AccentYellow).copy(alpha = 0.1f))
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = if (isLightModeEnabled) DeepBlue else AccentYellow
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column {
                        Text(
                            text = "CATATAN",
                            color = (if (isLightModeEnabled) Color.Black else TextWhite).copy(alpha = 0.8f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Kajian Islam",
                            color = if (isLightModeEnabled) Color.Black else TextWhite,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Search Bar
                SearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = viewModel::onSearchQueryChange,
                    isLightMode = isLightModeEnabled
                )

                // Category Stats
                CategoryStatsRow(
                    notes = uiState.notes,
                    selectedCategory = uiState.selectedCategory,
                    onCategoryClick = viewModel::onCategorySelect,
                    isLightMode = isLightModeEnabled
                )

                // Notes Grid
                if (uiState.notes.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "Belum ada catatan kajian.", 
                            color = (if (isLightModeEnabled) Color.Black else TextWhite).copy(alpha = 0.5f)
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.notes, key = { it.id }) { note ->
                            KajianNoteCard(
                                note = note,
                                onEdit = { noteToEdit = it },
                                onDelete = { noteToDelete = it },
                                isLightMode = isLightModeEnabled
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialogs
    if (showAddDialog) {
        AddEditNoteDialog(
            isLightMode = isLightModeEnabled,
            onDismiss = { showAddDialog = false },
            onSave = { judul, ustadz, tanggal, kategori, isi ->
                viewModel.addNote(judul, ustadz, tanggal, kategori, isi)
                showAddDialog = false
            }
        )
    }

    noteToEdit?.let { note ->
        AddEditNoteDialog(
            note = note,
            isLightMode = isLightModeEnabled,
            onDismiss = { noteToEdit = null },
            onSave = { judul, ustadz, tanggal, kategori, isi ->
                viewModel.updateNote(note.copy(judul = judul, ustadz = ustadz, tanggal = tanggal, kategori = kategori, isi = isi))
                noteToEdit = null
            }
        )
    }

    noteToDelete?.let { note ->
        AlertDialog(
            onDismissRequest = { noteToDelete = null },
            title = { Text("Hapus Catatan") },
            text = { Text("Apakah Anda yakin ingin menghapus catatan '${note.judul}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteNote(note.id)
                        noteToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Hapus", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { noteToDelete = null }) {
                    Text("Batal", color = (if (isLightModeEnabled) Color.Black else TextWhite).copy(alpha = 0.6f))
                }
            },
            containerColor = if (isLightModeEnabled) Color.White else CardBackground,
            titleContentColor = if (isLightModeEnabled) Color.Black else TextWhite,
            textContentColor = (if (isLightModeEnabled) Color.Black else TextWhite).copy(alpha = 0.8f)
        )
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit, isLightMode: Boolean) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp)),
        placeholder = { Text("Cari judul atau ustadz...", color = Color.Gray) },
        leadingIcon = { 
            Icon(
                Icons.Default.Search, 
                contentDescription = null, 
                tint = if (isLightMode) DeepBlue else AccentYellow
            ) 
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = if (isLightMode) Color.White else CardBackground,
            unfocusedContainerColor = if (isLightMode) Color.White else CardBackground,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = if (isLightMode) Color.Black else TextWhite,
            unfocusedTextColor = if (isLightMode) Color.Black else TextWhite
        ),
        singleLine = true
    )
}

@Composable
fun CategoryStatsRow(
    notes: List<KajianNote>,
    selectedCategory: KajianCategory?,
    onCategoryClick: (KajianCategory?) -> Unit,
    isLightMode: Boolean
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            CategoryChip(
                name = "Semua",
                count = notes.size,
                isSelected = selectedCategory == null,
                onClick = { onCategoryClick(null) },
                isLightMode = isLightMode
            )
        }
        items(KajianCategory.entries) { category ->
            val count = notes.count { it.kategori == category }
            CategoryChip(
                name = category.displayName,
                count = count,
                isSelected = selectedCategory == category,
                onClick = { onCategoryClick(category) },
                isLightMode = isLightMode
            )
        }
    }
}

@Composable
fun CategoryChip(name: String, count: Int, isSelected: Boolean, onClick: () -> Unit, isLightMode: Boolean) {
    Card(
        onClick = onClick,
        modifier = Modifier.padding(vertical = 4.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) (if (isLightMode) DeepBlue else AccentYellow) else (if (isLightMode) Color.White else CardBackground)
        ),
        border = if (!isSelected) BorderStroke(1.dp, (if (isLightMode) Color.Black else Color.White).copy(alpha = 0.1f)) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = if (isLightMode && !isSelected) 2.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                color = if (isSelected) (if (isLightMode) Color.White else DeepBlue) else (if (isLightMode) Color.Black else TextWhite),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (isSelected) (if (isLightMode) Color.White.copy(alpha = 0.2f) else DeepBlue.copy(alpha = 0.2f)) else (if (isLightMode) DeepBlue else AccentYellow).copy(alpha = 0.2f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = count.toString(),
                    color = if (isSelected) (if (isLightMode) Color.White else DeepBlue) else (if (isLightMode) DeepBlue else AccentYellow),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
fun KajianNoteCard(
    note: KajianNote,
    onEdit: (KajianNote) -> Unit,
    onDelete: (KajianNote) -> Unit,
    isLightMode: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLightMode) Color.White else CardBackground
        ),
        border = BorderStroke(1.dp, (if (isLightMode) Color.Black else Color.White).copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isLightMode) 2.dp else 0.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(note.kategori.icon, fontSize = 24.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = note.judul,
                        color = if (isLightMode) Color.Black else TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = note.ustadz,
                        color = if (isLightMode) DeepBlue else AccentYellow,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = note.isi,
                color = (if (isLightMode) Color.Black else TextWhite).copy(alpha = 0.7f),
                fontSize = 11.sp,
                maxLines = 2,
                lineHeight = 16.sp,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = note.tanggal,
                    color = (if (isLightMode) Color.Black else TextWhite).copy(alpha = 0.4f),
                    fontSize = 10.sp
                )
                Row {
                    IconButton(onClick = { onEdit(note) }, modifier = Modifier.size(24.dp)) {
                        Icon(
                            Icons.Default.Edit, 
                            contentDescription = "Edit", 
                            tint = if (isLightMode) DeepBlue else AccentYellow, 
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(onClick = { onDelete(note) }, modifier = Modifier.size(24.dp)) {
                        Icon(
                            Icons.Default.Delete, 
                            contentDescription = "Hapus", 
                            tint = Color.Red.copy(alpha = 0.7f), 
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteDialog(
    note: KajianNote? = null,
    isLightMode: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String, String, KajianCategory, String) -> Unit
) {
    var judul by remember { mutableStateOf(note?.judul ?: "") }
    var ustadz by remember { mutableStateOf(note?.ustadz ?: "") }
    var tanggal by remember { mutableStateOf(note?.tanggal ?: "") }
    var kategori by remember { mutableStateOf(note?.kategori ?: KajianCategory.AQIDAH) }
    var isi by remember { mutableStateOf(note?.isi ?: "") }
    var expanded by remember { mutableStateOf(false) }
    
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        KajianDatePickerDialog(
            initialDate = tanggal,
            onDismiss = { showDatePicker = false },
            onDateSelected = { 
                tanggal = it
                showDatePicker = false
            },
            isLightMode = isLightMode
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = { if (judul.isNotBlank()) onSave(judul, ustadz, tanggal, kategori, isi) },
                colors = ButtonDefaults.buttonColors(containerColor = if (isLightMode) DeepBlue else AccentYellow),
                enabled = judul.isNotBlank()
            ) {
                Text(
                    "Simpan", 
                    color = if (isLightMode) Color.White else DeepBlue, 
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Batal", 
                    color = (if (isLightMode) Color.Black else TextWhite).copy(alpha = 0.6f)
                )
            }
        },
        title = {
            Text(
                text = if (note == null) "Tambah Catatan" else "Edit Catatan",
                color = if (isLightMode) Color.Black else TextWhite,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NoteTextField(value = judul, onValueChange = { judul = it }, label = "Judul Kajian", isLightMode = isLightMode)
                NoteTextField(value = ustadz, onValueChange = { ustadz = it }, label = "Ustadz/Pemateri", isLightMode = isLightMode)
                
                // Date Picker Field (Interactive)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                ) {
                    NoteTextField(
                        value = tanggal,
                        onValueChange = { },
                        label = "Tanggal (DD/MM/YYYY)",
                        isLightMode = isLightMode,
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = if (isLightMode) DeepBlue else AccentYellow
                            )
                        }
                    )
                    // Cover the field to ensure click works everywhere
                    Box(modifier = Modifier.matchParentSize().clickable { showDatePicker = true })
                }
                
                // Category Dropdown
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedCard(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.outlinedCardColors(containerColor = if (isLightMode) Color.White else DeepBlue),
                        border = BorderStroke(1.dp, (if (isLightMode) DeepBlue else AccentYellow).copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "${kategori.icon} ${kategori.displayName}", 
                                color = if (isLightMode) Color.Black else TextWhite
                            )
                            Icon(
                                Icons.Default.ArrowDropDown, 
                                contentDescription = null, 
                                tint = if (isLightMode) DeepBlue else AccentYellow
                            )
                        }
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(if (isLightMode) Color.White else CardBackground).fillMaxWidth(0.7f)
                    ) {
                        KajianCategory.entries.forEach { cat ->
                            DropdownMenuItem(
                                text = { 
                                    Text(
                                        "${cat.icon} ${cat.displayName}", 
                                        color = if (isLightMode) Color.Black else TextWhite
                                    ) 
                                },
                                onClick = {
                                    kategori = cat
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                NoteTextField(value = isi, onValueChange = { isi = it }, label = "Isi Catatan", singleLine = false, minLines = 3, isLightMode = isLightMode)
            }
        },
        containerColor = if (isLightMode) Color.White else CardBackground
    )
}

@Composable
fun NoteTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    singleLine: Boolean = true,
    minLines: Int = 1,
    isLightMode: Boolean,
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { 
            Text(
                label, 
                color = (if (isLightMode) DeepBlue else AccentYellow).copy(alpha = 0.6f)
            ) 
        },
        modifier = modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = if (isLightMode) Color.Black else TextWhite,
            unfocusedTextColor = if (isLightMode) Color.Black else TextWhite,
            focusedBorderColor = if (isLightMode) DeepBlue else AccentYellow,
            unfocusedBorderColor = (if (isLightMode) DeepBlue else AccentYellow).copy(alpha = 0.3f),
            cursorColor = if (isLightMode) DeepBlue else AccentYellow,
            disabledTextColor = if (isLightMode) Color.Black else TextWhite,
            disabledBorderColor = (if (isLightMode) DeepBlue else AccentYellow).copy(alpha = 0.3f),
            disabledLabelColor = (if (isLightMode) DeepBlue else AccentYellow).copy(alpha = 0.6f),
            disabledTrailingIconColor = if (isLightMode) DeepBlue else AccentYellow
        ),
        singleLine = singleLine,
        minLines = minLines,
        shape = RoundedCornerShape(8.dp),
        readOnly = readOnly,
        trailingIcon = trailingIcon,
        enabled = !readOnly
    )
}

@Composable
fun KajianDatePickerDialog(
    initialDate: String,
    onDismiss: () -> Unit,
    onDateSelected: (String) -> Unit,
    isLightMode: Boolean
) {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    
    val parsedDate = remember(initialDate) {
        try {
            if (initialDate.isNotBlank()) {
                val parts = initialDate.split("/")
                LocalDate(parts[2].toInt(), parts[1].toInt(), parts[0].toInt())
            } else today
        } catch (e: Exception) {
            today
        }
    }

    var currentMonth by remember { mutableStateOf(parsedDate.month) }
    var currentYear by remember { mutableStateOf(parsedDate.year) }
    
    val monthsId = listOf(
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    )
    val daysId = listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab")
    
    val bgColor = if (isLightMode) Color.White else Color(0xFF1E2A2A)
    val accentColor = if (isLightMode) DeepBlue else Color(0xFFC9A84C)
    val textColor = if (isLightMode) Color.Black else TextWhite

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = bgColor,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header: Bulan Tahun
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        if (currentMonth == Month.JANUARY) {
                            currentMonth = Month.DECEMBER
                            currentYear -= 1
                        } else {
                            currentMonth = Month.entries[currentMonth.ordinal - 1]
                        }
                    }) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = accentColor)
                    }
                    
                    Text(
                        text = "${monthsId[currentMonth.ordinal]} $currentYear",
                        color = textColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    
                    IconButton(onClick = {
                        if (currentMonth == Month.DECEMBER) {
                            currentMonth = Month.JANUARY
                            currentYear += 1
                        } else {
                            currentMonth = Month.entries[currentMonth.ordinal + 1]
                        }
                    }) {
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = accentColor)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Day Names
                Row(modifier = Modifier.fillMaxWidth()) {
                    daysId.forEach { day ->
                        Text(
                            text = day,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            color = textColor.copy(alpha = 0.5f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Calendar Grid
                val firstDayOfMonth = LocalDate(currentYear, currentMonth, 1)
                val daysInMonth = when (currentMonth) {
                    Month.FEBRUARY -> if ((currentYear % 4 == 0 && currentYear % 100 != 0) || (currentYear % 400 == 0)) 29 else 28
                    Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
                    else -> 31
                }
                val firstDayOfWeek = if (firstDayOfMonth.dayOfWeek == DayOfWeek.SUNDAY) 0 else firstDayOfMonth.dayOfWeek.isoDayNumber
                
                val calendarDays = mutableListOf<LocalDate?>()
                for (i in 0 until firstDayOfWeek) {
                    calendarDays.add(null)
                }
                for (i in 1..daysInMonth) {
                    calendarDays.add(LocalDate(currentYear, currentMonth, i))
                }
                
                val weeks = calendarDays.chunked(7)
                weeks.forEach { week ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        week.forEach { date ->
                            val isSelected = date != null && date == parsedDate
                            val isToday = date != null && date == today
                            
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isSelected -> accentColor
                                            isToday -> accentColor.copy(alpha = 0.2f)
                                            else -> Color.Transparent
                                        }
                                    )
                                    .border(
                                        if (isToday && !isSelected) BorderStroke(1.dp, accentColor) else BorderStroke(0.dp, Color.Transparent),
                                        CircleShape
                                    )
                                    .clickable(enabled = date != null) {
                                        date?.let {
                                            onDateSelected("${it.dayOfMonth.toString().padStart(2, '0')}/${it.monthNumber.toString().padStart(2, '0')}/${it.year}")
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (date != null) {
                                    Text(
                                        text = date.dayOfMonth.toString(),
                                        color = when {
                                            isSelected -> if (isLightMode) Color.White else DeepBlue
                                            isToday -> accentColor
                                            else -> textColor
                                        },
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                        // Fill empty cells at the end of the last week
                        if (week.size < 7) {
                            for (i in 0 until (7 - week.size)) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Footer Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = {
                        onDateSelected("${today.dayOfMonth.toString().padStart(2, '0')}/${today.monthNumber.toString().padStart(2, '0')}/${today.year}")
                    }) {
                        Text("Hari Ini", color = accentColor, fontWeight = FontWeight.Bold)
                    }
                    
                    TextButton(onClick = onDismiss) {
                        Text("Batal", color = textColor.copy(alpha = 0.6f))
                    }
                }
            }
        }
    }
}
