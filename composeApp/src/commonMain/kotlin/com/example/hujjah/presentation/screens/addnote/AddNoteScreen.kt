package com.example.hujjah.presentation.screens.addnote

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hujjah.presentation.components.LoadingIndicator
import com.example.hujjah.domain.repository.hujjah.HujjahRepository
import com.example.hujjah.domain.model.islamic.SurahItem
import com.example.hujjah.domain.model.islamic.HadithBookItem
import com.example.hujjah.presentation.theme.LocalHujjahColors
import org.koin.compose.viewmodel.koinViewModel
import org.koin.compose.koinInject
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.heightIn
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(
    noteId: Long?,
    initialContent: String? = null,
    onNavigateBack: () -> Unit,
    onNavigateToAI: (String) -> Unit,
    viewModel: AddNoteViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = LocalHujjahColors.current
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Rujukan Dalil local state
    var hasInitializedRef by remember { mutableStateOf(false) }
    var referenceType by remember { mutableStateOf("NONE") } // "NONE", "QURAN", "HADITH"
    var refSource by remember { mutableStateOf("") }
    var refNumber by remember { mutableStateOf("") }
    var refArabic by remember { mutableStateOf("") }
    var refTranslation by remember { mutableStateOf("") }
    var cleanContentText by remember { mutableStateOf("") }

    val hujjahRepository = koinInject<HujjahRepository>()
    val coroutineScope = rememberCoroutineScope()

    // Dynamic reference count sync
    var surahList by remember { mutableStateOf<List<SurahItem>>(emptyList()) }
    var hadithBookList by remember { mutableStateOf<List<HadithBookItem>>(emptyList()) }
    var showManualInputDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        hujjahRepository.getSurahs(false).collect { list ->
            surahList = list
        }
    }
    LaunchedEffect(Unit) {
        hujjahRepository.getHadithBooks(false).collect { list ->
            hadithBookList = list
        }
    }

    val selectedSurahItem = remember(refSource, surahList) {
        surahList.find { it.name.equals(refSource, ignoreCase = true) }
    }
    val selectedHadithBookItem = remember(refSource, hadithBookList) {
        hadithBookList.find { it.name.equals(refSource, ignoreCase = true) || it.id.equals(refSource, ignoreCase = true) }
    }
    val maxLimit = remember(selectedSurahItem, selectedHadithBookItem, referenceType, refSource) {
        if (referenceType == "QURAN") {
            selectedSurahItem?.numberOfVerses ?: 286
        } else if (referenceType == "HADITH") {
            selectedHadithBookItem?.totalHadith ?: 7563
        } else {
            1
        }
    }

    val currentVal = remember(refNumber, maxLimit) {
        refNumber.toIntOrNull()?.coerceIn(1, maxLimit) ?: 1
    }

    // Auto-select first surah/book if refSource is blank
    LaunchedEffect(referenceType) {
        if (referenceType == "QURAN" && (refSource.isBlank() || !quranSurahs.any { it.second.equals(refSource, ignoreCase = true) })) {
            refSource = "Al-Fatihah"
            refNumber = "1"
        } else if (referenceType == "HADITH" && (refSource.isBlank() || !hadithBooks.any { it.second.equals(refSource, ignoreCase = true) || it.first.equals(refSource, ignoreCase = true) })) {
            refSource = "Shahih Bukhari"
            refNumber = "1"
        }
    }

    LaunchedEffect(noteId, initialContent) {
        viewModel.initializeNote(noteId, initialContent)
    }
    
    // Parse rujukan ketika data selesai dimuat
    LaunchedEffect(uiState.isLoading, uiState.content) {
        if (!uiState.isLoading && uiState.content.isNotEmpty() && !hasInitializedRef) {
            val regex = Regex("""\n\n\[Rujukan:\s*(Quran|Hadits)\s*(\|\|\||\|)\s*(.*?)\s*\]""")
            val match = regex.find(uiState.content)
            if (match != null) {
                val fullTag = match.value
                cleanContentText = uiState.content.replace(fullTag, "")
                val tagContent = fullTag.trim().removeSurrounding("[Rujukan:", "]").trim()
                val parts = if (tagContent.contains("|||")) {
                    tagContent.split("|||").map { it.trim() }
                } else {
                    tagContent.split("|").map { it.trim() }
                }
                
                if (parts.size >= 3) {
                    referenceType = if (parts[0] == "Quran") "QURAN" else "HADITH"
                    refSource = parts[1]
                    refNumber = parts[2]
                    if (parts.size >= 5) {
                        refArabic = parts[3]
                        refTranslation = parts[4]
                    }
                }
            } else {
                cleanContentText = uiState.content
                referenceType = "NONE"
            }
            hasInitializedRef = true
        } else if (!uiState.isLoading && uiState.content.isEmpty() && !hasInitializedRef) {
            cleanContentText = ""
            hasInitializedRef = true
        }
    }

    // Load isi rujukan secara asinkron
    LaunchedEffect(referenceType, refSource, refNumber) {
        if (referenceType != "NONE" && refSource.isNotBlank() && refNumber.isNotBlank()) {
            val num = refNumber.toIntOrNull()
            if (num != null) {
                if (referenceType == "QURAN") {
                    val surahPair = quranSurahs.find { it.second.equals(refSource, ignoreCase = true) }
                    if (surahPair != null) {
                        try {
                            val verses = hujjahRepository.getSurahDetail(surahPair.first, surahPair.second, false).first()
                            val verse = verses.find { it.number == num }
                            if (verse != null) {
                                refArabic = verse.arabic
                                refTranslation = verse.translation
                            } else {
                                refArabic = ""
                                refTranslation = ""
                            }
                        } catch (e: Exception) {
                            refArabic = ""
                            refTranslation = ""
                        }
                    }
                } else if (referenceType == "HADITH") {
                    val bookPair = hadithBooks.find { it.second.equals(refSource, ignoreCase = true) || it.first.equals(refSource, ignoreCase = true) }
                    if (bookPair != null) {
                        try {
                            val hadiths = hujjahRepository.getHadithRange(bookPair.first, num, num, false).first()
                            val hadith = hadiths.firstOrNull()
                            if (hadith != null) {
                                refArabic = hadith.arab
                                refTranslation = hadith.translation
                            } else {
                                refArabic = ""
                                refTranslation = ""
                            }
                        } catch (e: Exception) {
                            refArabic = ""
                            refTranslation = ""
                        }
                    }
                }
            }
        } else {
            refArabic = ""
            refTranslation = ""
        }
    }
    
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AddNoteEvent.NoteSaved -> onNavigateBack()
                is AddNoteEvent.Error -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }
    
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (uiState.isEditMode) "Edit Catatan" else "Catatan Baru",
                        fontWeight = FontWeight.Bold,
                        color = colors.goldHighlight
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Kembali",
                            tint = colors.goldHighlight
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onNavigateToAI(uiState.content) },
                        enabled = uiState.content.isNotBlank()
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AutoAwesome, 
                            contentDescription = "AI Assistant",
                            tint = if (uiState.content.isNotBlank()) colors.goldHighlight else colors.goldHighlight.copy(alpha = 0.4f)
                        )
                    }
                    
                    IconButton(
                        onClick = {
                            val finalContent = buildString {
                                append(cleanContentText.trim())
                                if (referenceType != "NONE" && refSource.isNotBlank() && refNumber.isNotBlank()) {
                                    val typeLabel = if (referenceType == "QURAN") "Quran" else "Hadits"
                                    append("\n\n[Rujukan: $typeLabel ||| ${refSource.trim()} ||| ${refNumber.trim()} ||| ${refArabic.trim()} ||| ${refTranslation.trim()}]")
                                }
                            }
                            viewModel.onContentChange(finalContent)
                            viewModel.saveNote()
                        },
                        enabled = (cleanContentText.isNotBlank() || uiState.title.isNotBlank()) && !uiState.isSaving
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check, 
                            contentDescription = "Simpan",
                            tint = if ((cleanContentText.isNotBlank() || uiState.title.isNotBlank()) && !uiState.isSaving) colors.goldHighlight else colors.goldHighlight.copy(alpha = 0.4f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            LoadingIndicator()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = viewModel::onTitleChange,
                    label = { Text("Judul") },
                    placeholder = { Text("Masukkan judul...") },
                    singleLine = true,
                    isError = uiState.titleError != null,
                    supportingText = uiState.titleError?.let { { Text(it) } },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.goldHighlight,
                        unfocusedBorderColor = if (colors.isDarkTheme) Color(0xFF1E1E1E) else Color(0xFFE5E5E5),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = cleanContentText,
                    onValueChange = {
                        cleanContentText = it
                        viewModel.onContentChange(it)
                    },
                    label = { Text("Konten") },
                    placeholder = { Text("Tulis catatan di sini...") },
                    minLines = 8,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.goldHighlight,
                        unfocusedBorderColor = if (colors.isDarkTheme) Color(0xFF1E1E1E) else Color(0xFFE5E5E5),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Form Rujukan Dalil Apple Premium
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (colors.isDarkTheme) Color.Black else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    border = BorderStroke(0.5.dp, colors.goldHighlight.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "🔗 Hubungkan Rujukan Dalil",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.goldHighlight
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Selector Rujukan (NONE, QURAN, HADITH)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("NONE" to "Tanpa Rujukan", "QURAN" to "Al-Qur'an", "HADITH" to "Hadits").forEach { (type, label) ->
                                val isSelected = referenceType == type
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { referenceType = type },
                                    label = { Text(label, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = colors.goldHighlight,
                                        selectedLabelColor = MaterialTheme.colorScheme.background
                                    )
                                )
                            }
                        }
                        
                        if (referenceType != "NONE") {
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // 1. Selector Surah / Book (Dropdown)
                            var dropdownExpanded by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = dropdownExpanded,
                                onExpandedChange = { dropdownExpanded = it },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = refSource,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text(if (referenceType == "QURAN") "Nama Surah" else "Nama Perawi/Kitab") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = colors.goldHighlight,
                                        unfocusedBorderColor = if (colors.isDarkTheme) Color(0xFF1E1E1E) else Color(0xFFE5E5E5),
                                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                )
                                
                                ExposedDropdownMenu(
                                    expanded = dropdownExpanded,
                                    onDismissRequest = { dropdownExpanded = false },
                                    modifier = Modifier.heightIn(max = 250.dp)
                                ) {
                                    if (referenceType == "QURAN") {
                                        quranSurahs.forEach { (_, name) ->
                                            DropdownMenuItem(
                                                text = { Text(name) },
                                                onClick = {
                                                    refSource = name
                                                    dropdownExpanded = false
                                                    refNumber = "1"
                                                }
                                            )
                                        }
                                    } else {
                                        hadithBooks.forEach { (_, name) ->
                                            DropdownMenuItem(
                                                text = { Text(name) },
                                                onClick = {
                                                    refSource = name
                                                    dropdownExpanded = false
                                                    refNumber = "1"
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // 2. Slider Controls for Verse/Hadith Number
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (referenceType == "QURAN") "Ayat pilihan:" else "Hadits nomor:",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                    
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.clickable { showManualInputDialog = true }
                                    ) {
                                        Text(
                                            text = "$currentVal / $maxLimit",
                                            fontWeight = FontWeight.Bold,
                                            color = colors.goldHighlight,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = " ✏️",
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = {
                                            val newVal = (currentVal - 1).coerceAtLeast(1)
                                            refNumber = newVal.toString()
                                        },
                                        enabled = currentVal > 1
                                    ) {
                                        Text("-", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = colors.goldHighlight)
                                    }
                                    
                                    Slider(
                                        value = currentVal.toFloat(),
                                        onValueChange = { floatVal ->
                                            refNumber = floatVal.toInt().toString()
                                        },
                                        valueRange = 1f..maxLimit.toFloat(),
                                        colors = SliderDefaults.colors(
                                            thumbColor = colors.goldHighlight,
                                            activeTrackColor = colors.goldHighlight,
                                            inactiveTrackColor = colors.goldHighlight.copy(alpha = 0.2f)
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                    
                                    IconButton(
                                        onClick = {
                                            val newVal = (currentVal + 1).coerceAtMost(maxLimit)
                                            refNumber = newVal.toString()
                                        },
                                        enabled = currentVal < maxLimit
                                    ) {
                                        Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = colors.goldHighlight)
                                    }
                                }
                            }
                            
                            // Live Preview Kutipan Rujukan
                            if (refArabic.isNotBlank()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = colors.goldHighlight.copy(alpha = 0.05f)
                                    ),
                                    border = BorderStroke(0.5.dp, colors.goldHighlight.copy(alpha = 0.3f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "Kutipan Rujukan:",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = colors.goldHighlight,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = refArabic,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium,
                                            textAlign = TextAlign.End,
                                            lineHeight = 24.sp,
                                            modifier = Modifier.fillMaxWidth(),
                                            color = if (colors.isDarkTheme) Color.White else colors.islamicGreen
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "\"$refTranslation\"",
                                            fontSize = 12.sp,
                                            fontStyle = FontStyle.Italic,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = uiState.category,
                    onValueChange = viewModel::onCategoryChange,
                    label = { Text("Kategori (Opsional)") },
                    placeholder = { Text("Ketik kategori baru...") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.goldHighlight,
                        unfocusedBorderColor = if (colors.isDarkTheme) Color(0xFF1E1E1E) else Color(0xFFE5E5E5),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    if (showManualInputDialog) {
        var textInput by remember(refNumber) { mutableStateOf(refNumber) }
        AlertDialog(
            onDismissRequest = { showManualInputDialog = false },
            title = { Text("Masukkan Nomor Secara Manual") },
            text = {
                Column {
                    Text("Masukkan angka antara 1 dan $maxLimit:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it.filter { char -> char.isDigit() } },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.goldHighlight
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val typedVal = textInput.toIntOrNull()
                        if (typedVal != null) {
                            refNumber = typedVal.coerceIn(1, maxLimit).toString()
                        }
                        showManualInputDialog = false
                    }
                ) {
                    Text("OK", color = colors.goldHighlight)
                }
            },
            dismissButton = {
                TextButton(onClick = { showManualInputDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}


private val quranSurahs = listOf(
    Pair(1, "Al-Fatihah"), Pair(2, "Al-Baqarah"), Pair(3, "Ali 'Imran"), Pair(4, "An-Nisa'"),
    Pair(5, "Al-Ma'idah"), Pair(6, "Al-An'am"), Pair(7, "Al-A'raf"), Pair(8, "Al-Anfal"),
    Pair(9, "At-Taubah"), Pair(10, "Yunus"), Pair(11, "Hud"), Pair(12, "Yusuf"),
    Pair(13, "Ar-Ra'd"), Pair(14, "Ibrahim"), Pair(15, "Al-Hijr"), Pair(16, "An-Nahl"),
    Pair(17, "Al-Isra'"), Pair(18, "Al-Kahf"), Pair(19, "Maryam"), Pair(20, "Ta Ha"),
    Pair(21, "Al-Anbiya'"), Pair(22, "Al-Hajj"), Pair(23, "Al-Mu'minun"), Pair(24, "An-Nur"),
    Pair(25, "Al-Furqan"), Pair(26, "Asy-Syu'ara'"), Pair(27, "An-Naml"), Pair(28, "Al-Qashash"),
    Pair(29, "Al-'Ankabut"), Pair(30, "Ar-Rum"), Pair(31, "Luqman"), Pair(32, "As-Sajdah"),
    Pair(33, "Al-Ahzab"), Pair(34, "Saba'"), Pair(35, "Fathir"), Pair(36, "Ya Sin"),
    Pair(37, "As-Saffat"), Pair(38, "Sad"), Pair(39, "Az-Zumar"), Pair(40, "Ghafir"),
    Pair(41, "Fushshilat"), Pair(42, "Asy-Syura"), Pair(43, "Az-Zukhruf"), Pair(44, "Ad-Dukhan"),
    Pair(45, "Al-Jatsiyah"), Pair(46, "Al-Ahqaf"), Pair(47, "Muhammad"), Pair(48, "Al-Fath"),
    Pair(49, "Al-Hujurat"), Pair(50, "Qaf"), Pair(51, "Adz-Dzariyat"), Pair(52, "Ath-Thur"),
    Pair(53, "An-Najm"), Pair(54, "Al-Qamar"), Pair(55, "Ar-Rahman"), Pair(56, "Al-Waqi'ah"),
    Pair(57, "Al-Hadid"), Pair(58, "Al-Mujadilah"), Pair(59, "Al-Hasyr"), Pair(60, "Al-Mumtahanah"),
    Pair(61, "As-Saff"), Pair(62, "Al-Jumu'ah"), Pair(63, "Al-Munafiqun"), Pair(64, "At-Taghabun"),
    Pair(65, "Ath-Thalaq"), Pair(66, "At-Tahrim"), Pair(67, "Al-Mulk"), Pair(68, "Al-Qalam"),
    Pair(69, "Al-Haqqah"), Pair(70, "Al-Ma'arij"), Pair(71, "Nuh"), Pair(72, "Al-Jinn"),
    Pair(73, "Al-Muzzammil"), Pair(74, "Al-Muddatstsir"), Pair(75, "Al-Qiyamah"), Pair(76, "Al-Insan"),
    Pair(77, "Al-Mursalat"), Pair(78, "An-Naba'"), Pair(79, "An-Nazi'at"), Pair(80, "'Abasa"),
    Pair(81, "At-Takwir"), Pair(82, "Al-Infitar"), Pair(83, "Al-Muthaffifin"), Pair(84, "Al-Insiqaq"),
    Pair(85, "Al-Buruj"), Pair(86, "Ath-Thariq"), Pair(87, "Al-A'la"), Pair(88, "Al-Ghasyiyah"),
    Pair(89, "Al-Fajr"), Pair(90, "Al-Balad"), Pair(91, "Asy-Syams"), Pair(92, "Al-Lail"),
    Pair(93, "Ad-Duha"), Pair(94, "Al-Insyirah"), Pair(95, "At-Tin"), Pair(96, "Al-'Alaq"),
    Pair(97, "Al-Qadr"), Pair(98, "Al-Bayyinah"), Pair(99, "Az-Zalzalah"), Pair(100, "Al-'Adiyat"),
    Pair(101, "Al-Qari'ah"), Pair(102, "At-Takatsur"), Pair(103, "Al-'Asr"), Pair(104, "Al-Humazah"),
    Pair(105, "Al-Fil"), Pair(106, "Quraisy"), Pair(107, "Al-Ma'un"), Pair(108, "Al-Kautsar"),
    Pair(109, "Al-Kafirun"), Pair(110, "An-Nasr"), Pair(111, "Al-Lahab"), Pair(112, "Al-Ikhlas"),
    Pair(113, "Al-Falaq"), Pair(114, "An-Nas")
)

private val hadithBooks = listOf(
    Pair("bukhari", "Shahih Bukhari"),
    Pair("muslim", "Shahih Muslim"),
    Pair("abu-daud", "Sunan Abu Daud"),
    Pair("tirmidzi", "Sunan Tirmidzi"),
    Pair("nasai", "Sunan Nasai"),
    Pair("ibnu-majah", "Sunan Ibnu Majah"),
    Pair("ahmad", "Musnad Ahmad"),
    Pair("malik", "Muwatta Malik"),
    Pair("darimi", "Sunan Darimi")
)
