package com.example.travelplanner.presentation.screens.planner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travelplanner.core.util.LocalStrings
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

// ══════════════════════════════════════════════════════════════════════
//  HELPERS
// ══════════════════════════════════════════════════════════════════════

private fun indonesianMonthShort(month: Int) = when (month) {
    1 -> "Jan"; 2 -> "Feb"; 3 -> "Mar"; 4 -> "Apr"
    5 -> "Mei"; 6 -> "Jun"; 7 -> "Jul"; 8 -> "Agt"
    9 -> "Sep"; 10 -> "Okt"; 11 -> "Nov"; else -> "Des"
}

private fun epochMillisToDateString(millis: Long): String {
    val instant = Instant.fromEpochMilliseconds(millis)
    val d = instant.toLocalDateTime(TimeZone.UTC)
    return "${d.dayOfMonth} ${indonesianMonthShort(d.monthNumber)} ${d.year}"
}

private fun formatRupiahInput(raw: String): String {
    val digits = raw.filter { it.isDigit() }
    if (digits.isEmpty()) return ""
    val number = digits.toLongOrNull() ?: return digits
    val str = number.toString()
    return buildString {
        str.reversed().forEachIndexed { i, c ->
            if (i > 0 && i % 3 == 0) append('.')
            append(c)
        }
    }.reversed()
}

// ══════════════════════════════════════════════════════════════════════
//  DATA
// ══════════════════════════════════════════════════════════════════════

data class VibeOption(
    val label: String,
    val subtitle: String,
    val animationScene: @Composable (Modifier) -> Unit
)

// vibeOptions and budgetPresets are now built inside the composable using LocalStrings
// to support bilingual labels (see getVibeOptions / getBudgetPresets helpers below)

data class BudgetPreset(val key: String, val label: String, val range: String)

// ══════════════════════════════════════════════════════════════════════
//  SCREEN
// ══════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateTripScreen(
    onNavigateBack: () -> Unit,
    onGenerateSuccess: (String) -> Unit,
    viewModel: GenerateTripViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val s = LocalStrings.current

    // Build locale-aware option lists inside the composable
    val vibeOptions = remember(s) {
        listOf(
            VibeOption(s.vibeNature,    s.vibeNatureSub)    { m -> AlamAnimatedScene(m) },
            VibeOption(s.vibeBeach,     s.vibeBeachSub)     { m -> PantaiAnimatedScene(m) },
            VibeOption(s.vibeCulinary,  s.vibeCulinarySub)  { m -> KulinerAnimatedScene(m) },
            VibeOption(s.vibeHistory,   s.vibeHistorySub)   { m -> SejarahAnimatedScene(m) },
            VibeOption(s.vibeRelax,     s.vibeRelaxSub)     { m -> SantaiAnimatedScene(m) },
            VibeOption(s.vibeAdventure, s.vibeAdventureSub) { m -> PetualanganAnimatedScene(m) },
            VibeOption(s.vibeFormal,    s.vibeFormalSub)    { m -> FormalAnimatedScene(m) },
            VibeOption(s.vibeRomantic,  s.vibeRomanticSub)  { m -> RomanticAnimatedScene(m) }
        )
    }
    val budgetPresets = remember(s) {
        listOf(
            BudgetPreset("hemat",  s.budgetSavings, "< Rp 500.000"),
            BudgetPreset("sedang", s.budgetMedium,  "Rp 500K – 2Jt"),
            BudgetPreset("nyaman", s.budgetComfort, "Rp 2Jt – 5Jt"),
            BudgetPreset("mewah",  s.budgetLuxury,  "> Rp 5.000.000")
        )
    }

    var departureCity     by rememberSaveable { mutableStateOf("") }
    var destination       by rememberSaveable { mutableStateOf("") }
    var startDate         by rememberSaveable { mutableStateOf("") }
    var endDate           by rememberSaveable { mutableStateOf("") }
    var selectedBudgetKey by rememberSaveable { mutableStateOf("") }
    var customBudgetRaw   by rememberSaveable { mutableStateOf("") }
    var numberOfTravelers by rememberSaveable { mutableStateOf("1") }
    var selectedVibe      by rememberSaveable { mutableStateOf("") }
    var specialNotes      by rememberSaveable { mutableStateOf("") }

    val budgetValue = remember(selectedBudgetKey, customBudgetRaw) {
        when {
            selectedBudgetKey.isNotBlank() ->
                budgetPresets.find { it.key == selectedBudgetKey }?.range ?: ""
            customBudgetRaw.isNotBlank() ->
                "Rp ${formatRupiahInput(customBudgetRaw)}"
            else -> ""
        }
    }

    val isFormValid = departureCity.isNotBlank() && destination.isNotBlank()
        && startDate.isNotBlank() && endDate.isNotBlank()
        && selectedVibe.isNotBlank() && !uiState.isLoading

    LaunchedEffect(uiState.successTripId) {
        uiState.successTripId?.let { tripId ->
            onGenerateSuccess(tripId)
            viewModel.resetState()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = { Text(s.planTripTitle, fontWeight = FontWeight.SemiBold, letterSpacing = 0.3.sp) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = s.back)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            },
            bottomBar = {
                Surface(tonalElevation = 4.dp, color = MaterialTheme.colorScheme.surface) {
                    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                        Button(
                            onClick = {
                                viewModel.generateTrip(
                                    departureCity = departureCity,
                                    destination = destination,
                                    startDate = startDate,
                                    endDate = endDate,
                                    duration = "$startDate – $endDate",
                                    vibe = selectedVibe,
                                    specialNotes = specialNotes,
                                    language = if (s.isEnglish) "English" else "Indonesian",
                                    errDestEmpty = s.destinationEmptyError,
                                    errVibeEmpty = s.vibeEmptyError,
                                    errAiFormat = s.aiFormatError,
                                    errNetwork = s.networkError,
                                    errAiGeneral = s.aiGeneralError
                                )
                            },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            enabled = isFormValid,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(s.generateButton, fontWeight = FontWeight.SemiBold, letterSpacing = 0.3.sp)
                        }
                        if (!isFormValid && !uiState.isLoading) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(s.fillAllFields,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                // ── SECTION 1: RUTE ────────────────────────────────────
                FormSection(icon = Icons.Default.AirplanemodeActive, title = s.sectionRoute) {
                    ElegantTextField(
                        value = departureCity,
                        onValueChange = { departureCity = it },
                        label = s.departureCity,
                        placeholder = s.departureCityPlaceholder,
                        icon = Icons.Default.FlightTakeoff,
                        isRequired = true,
                        capitalization = KeyboardCapitalization.Words
                    )
                    ElegantTextField(
                        value = destination,
                        onValueChange = { destination = it },
                        label = s.destinationCity,
                        placeholder = s.destinationCityPlaceholder,
                        icon = Icons.Default.LocationOn,
                        isRequired = true,
                        capitalization = KeyboardCapitalization.Words
                    )
                }

                // ── SECTION 2: TANGGAL ─────────────────────────────────
                FormSection(icon = Icons.Default.CalendarMonth, title = s.sectionSchedule) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        DatePickerField(
                            value = startDate,
                            onValueChange = { startDate = it },
                            label = s.depart,
                            icon = Icons.Default.FlightTakeoff,
                            isRequired = true,
                            modifier = Modifier.weight(1f)
                        )
                        DatePickerField(
                            value = endDate,
                            onValueChange = { endDate = it },
                            label = s.returnDate,
                            icon = Icons.Default.FlightLand,
                            isRequired = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // ── SECTION 3: DETAIL ──────────────────────────────────
                FormSection(icon = Icons.Default.Group, title = s.sectionGroup) {
                    ElegantTextField(
                        value = numberOfTravelers,
                        onValueChange = { if (it.all(Char::isDigit) && it.length <= 2) numberOfTravelers = it },
                        label = s.numberOfPeople, placeholder = "2",
                        icon = Icons.Default.People, keyboardType = KeyboardType.Number
                    )
                }

                // ── SECTION 4: BUDGET ──────────────────────────────────
                FormSection(icon = Icons.Default.Wallet, title = s.sectionBudget,
                    subtitle = s.sectionBudgetSubtitle) {
                    BudgetRangeSelector(
                        selectedKey = selectedBudgetKey,
                        customBudgetRaw = customBudgetRaw,
                        presets = budgetPresets,
                        onPresetSelected = { key ->
                            selectedBudgetKey = key
                            customBudgetRaw = ""
                        },
                        onCustomBudgetChange = { raw ->
                            customBudgetRaw = raw
                            selectedBudgetKey = ""
                        }
                    )
                }

                // ── SECTION 5: VIBE ────────────────────────────────────
                FormSection(icon = Icons.Default.Explore, title = s.sectionVibe,
                    subtitle = s.sectionVibeSubtitle) {
                    vibeOptions.chunked(2).forEach { rowItems ->
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()) {
                            rowItems.forEach { vibe ->
                                AnimatedVibeCard(
                                    vibe = vibe,
                                    isSelected = selectedVibe == vibe.label,
                                    onClick = { selectedVibe = vibe.label },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (rowItems.size < 2) Spacer(Modifier.weight(1f))
                        }
                    }
                }

                // ── SECTION 6: CATATAN ─────────────────────────────────
                FormSection(icon = Icons.Default.EditNote, title = s.sectionNotes,
                    subtitle = s.sectionNotesSubtitle) {
                    OutlinedTextField(
                        value = specialNotes, onValueChange = { specialNotes = it },
                        placeholder = {
                            Text(s.notesPlaceholder,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                        },
                        modifier = Modifier.fillMaxWidth().height(96.dp), maxLines = 4,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    )
                }

                uiState.errorMessage?.let { error ->
                    Surface(shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null,
                                tint = MaterialTheme.colorScheme.error)
                            Text(error, style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // Loading overlay
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.70f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                FlightRadarLoading(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                )
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════
//  DATE PICKER FIELD
// ══════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    isRequired: Boolean = false,
    modifier: Modifier = Modifier
) {
    val s = LocalStrings.current
    var showPicker by remember { mutableStateOf(false) }
    val pickerState = rememberDatePickerState()

    Box(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = {
                Text(if (isRequired) "$label *" else label,
                    style = MaterialTheme.typography.labelMedium)
            },
            placeholder = {
                Text(s.pickDate, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f))
            },
            leadingIcon = {
                Icon(icon, contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f),
                    modifier = Modifier.size(18.dp))
            },
            trailingIcon = {
                Icon(Icons.Default.CalendarMonth, contentDescription = s.pickDate,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f),
                    modifier = Modifier.size(18.dp))
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                disabledTextColor = MaterialTheme.colorScheme.onSurface
            )
        )
        Box(modifier = Modifier.matchParentSize().clickable { showPicker = true })
    }

    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { millis ->
                        onValueChange(epochMillisToDateString(millis))
                    }
                    showPicker = false
                }) { Text(s.pickDateAction, fontWeight = FontWeight.SemiBold) }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text(s.cancel) }
            }
        ) {
            DatePicker(
                state = pickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                    todayDateBorderColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

// ══════════════════════════════════════════════════════════════════════
//  BUDGET RANGE SELECTOR
// ══════════════════════════════════════════════════════════════════════

@Composable
fun BudgetRangeSelector(
    selectedKey: String,
    customBudgetRaw: String,
    presets: List<BudgetPreset>,
    onPresetSelected: (String) -> Unit,
    onCustomBudgetChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val s = LocalStrings.current
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // 2 x 2 chip grid
        presets.chunked(2).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { preset ->
                    val isSelected = selectedKey == preset.key
                    Surface(
                        modifier = Modifier.weight(1f).clickable { onPresetSelected(preset.key) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant,
                        border = if (isSelected) null
                                 else androidx.compose.foundation.BorderStroke(
                                     1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                            Text(preset.label,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                        else MaterialTheme.colorScheme.onSurface)
                            Text(preset.range,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.80f)
                                        else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                if (row.size < 2) Spacer(Modifier.weight(1f))
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 4.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
        )

        // Custom Rupiah input
        OutlinedTextField(
            value = if (customBudgetRaw.isEmpty()) "" else formatRupiahInput(customBudgetRaw),
            onValueChange = { raw ->
                onCustomBudgetChange(raw.filter { it.isDigit() })
            },
            label = { Text(s.budgetOrSpecific, style = MaterialTheme.typography.labelMedium) },
            placeholder = {
                Text(s.budgetExample, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f))
            },
            leadingIcon = {
                Text("Rp", style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 14.dp))
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        )
    }
}

// ══════════════════════════════════════════════════════════════════════
//  ANIMATED VIBE CARD
// ══════════════════════════════════════════════════════════════════════

@Composable
fun AnimatedVibeCard(
    vibe: VibeOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val overlayAlpha by animateFloatAsState(
        targetValue = if (isSelected) 0f else 0.30f,
        animationSpec = tween(200), label = "overlay"
    )
    val borderAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = tween(200), label = "border"
    )

    Box(modifier = modifier.height(130.dp)
        .shadow(elevation = if (isSelected) 12.dp else 4.dp,
            shape = RoundedCornerShape(14.dp),
            ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
        .clip(RoundedCornerShape(14.dp))
        .clickable { onClick() }
    ) {
        vibe.animationScene(Modifier.fillMaxSize())
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = overlayAlpha)))
        if (isSelected) {
            Box(Modifier.fillMaxSize().border(2.dp,
                MaterialTheme.colorScheme.secondary.copy(alpha = borderAlpha),
                RoundedCornerShape(14.dp)))
        }
        Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.72f))))
            .padding(horizontal = 10.dp, vertical = 10.dp)) {
            Column {
                Text(vibe.label, style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold, color = Color.White)
                Text(vibe.subtitle, style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.80f))
            }
        }
        AnimatedVisibility(visible = isSelected,
            modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
            enter = fadeIn() + expandVertically()) {
            Surface(shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Check, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.padding(4.dp))
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════
//  REUSABLE FORM COMPONENTS
// ══════════════════════════════════════════════════════════════════════

@Composable
fun FormSection(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp))
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground, letterSpacing = 0.4.sp)
            }
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 26.dp))
            }
            HorizontalDivider(modifier = Modifier.padding(top = 4.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f))
        }
        content()
    }
}

@Composable
fun ElegantTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector,
    isRequired: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        label = {
            Text(if (isRequired) "$label *" else label, style = MaterialTheme.typography.labelMedium)
        },
        placeholder = {
            Text(placeholder, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f))
        },
        leadingIcon = {
            Icon(icon, contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f),
                modifier = Modifier.size(18.dp))
        },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            capitalization = capitalization
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    )
}
