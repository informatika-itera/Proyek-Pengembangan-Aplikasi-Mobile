package com.example.raillog.presentation.screens.requisition

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.raillog.data.local.datastore.UserPreferences
import com.example.raillog.presentation.components.PageHeader
import com.example.raillog.presentation.components.SurfaceCard
import com.example.raillog.presentation.components.SubtleDivider
import com.example.raillog.presentation.theme.RailLogColors
import com.example.raillog.presentation.theme.Spacing
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequisitionScreen(
    viewModel: RequisitionViewModel = koinViewModel(),
    draftId: String? = null,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    userPreferences: UserPreferences = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    var currentStep by remember { mutableIntStateOf(1) }

    LaunchedEffect(Unit) {
        if (draftId.isNullOrBlank()) {
            val accountsString = userPreferences.staffAccounts.first()
            val activeUsername = userPreferences.activeUsername.first()
            
            // Parse accounts to find the one matching activeUsername
            val accountList = if (accountsString.isNotEmpty()) {
                accountsString.split(";").map { it.split("|") }
            } else {
                emptyList()
            }
            val activeAccount = accountList.find { it.size >= 5 && it[0] == activeUsername }
            
            if (activeAccount != null) {
                // Format: user|pass|name|id|phone
                viewModel.updateName(activeAccount[2])
                viewModel.updateEmployeeId(activeAccount[3])
                viewModel.updatePhone(activeAccount[4])
            }
        } else {
            viewModel.loadDraft(draftId) { savedStep -> currentStep = savedStep }
        }
    }

    LaunchedEffect(uiState.submitSuccess) {
        if (uiState.submitSuccess) onNavigateToHome()
    }

    Scaffold(
        containerColor = RailLogColors.Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (currentStep) {
                            1 -> "Identitas"; 2 -> "Spesifikasi"; 3 -> "Material"
                            4 -> "Justifikasi"; else -> "Otorisasi"
                        },
                        fontWeight = FontWeight.SemiBold,
                        color = RailLogColors.TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.saveDraftAutomatically(currentStep)
                        if (currentStep > 1) currentStep-- else onNavigateBack()
                    }) {
                        Icon(
                            if (currentStep == 1) Icons.Default.Close else Icons.AutoMirrored.Filled.ArrowBack,
                            "Back",
                            tint = RailLogColors.TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RailLogColors.Surface)
            )
        },
        bottomBar = {
            BottomActionBar(
                currentStep = currentStep,
                isSubmitting = uiState.isSubmitting,
                onNext = { if (currentStep < 5) currentStep++ },
                onBack = { if (currentStep > 1) currentStep-- },
                onSubmit = { viewModel.submitRequisition() },
                viewModel = viewModel
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            StepProgressBar(currentStep = currentStep)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Spacing.pagePadding)
            ) {
                when (currentStep) {
                    1 -> Step1Identity(uiState, viewModel)
                    2 -> Step2ProjectSpecs(uiState, viewModel)
                    3 -> Step3MaterialCatalog(uiState, viewModel)
                    4 -> Step4Justification(uiState, viewModel)
                    5 -> Step5FinalReview(uiState, viewModel)
                }
            }
        }
    }
}

// ── Step 1 ────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Step1Identity(uiState: RequisitionFormState, viewModel: RequisitionViewModel) {
    val departments = listOf("Maintenance", "Infrastructure", "Signals", "Logistics")
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val instant = Instant.fromEpochMilliseconds(it)
                        val dt = instant.toLocalDateTime(TimeZone.currentSystemDefault())
                        viewModel.updateDate("${dt.dayOfMonth}/${dt.monthNumber}/${dt.year}")
                    }
                    showDatePicker = false
                }) { Text("Setel", fontWeight = FontWeight.Medium, color = RailLogColors.PrimaryAction) }
            }
        ) { DatePicker(state = datePickerState) }
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Spacer(Modifier.height(Spacing.md))
            PageHeader("Identitas resmi", "Data ditarik otomatis dari akun Anda")
            Spacer(Modifier.height(Spacing.lg))

            FormTextField("Nama pengaju", Icons.Default.Person, uiState.requestorName,
                { viewModel.updateName(it) }, "Nama sesuai ID")
            FormTextField("NIP / ID pegawai", Icons.Default.Badge, uiState.employeeId,
                { viewModel.updateEmployeeId(it) }, "NIP sesuai profil")
            FormTextField("Kontak WhatsApp", Icons.Default.Phone, uiState.phoneNumber,
                { viewModel.updatePhone(it) }, "Kontak pengaju")

            SubtleDivider(modifier = Modifier.padding(vertical = Spacing.md))

            FormTextField("Supervisor site", Icons.Default.SupervisorAccount, uiState.supervisorName,
                { viewModel.updateSupervisor(it) }, "Nama atasan langsung")
            DropdownField("Unit kerja", Icons.Default.Business, uiState.department,
                departments, { viewModel.updateDepartment(it) })

            Text("Tanggal dibutuhkan", style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium, color = RailLogColors.TextSecondary)
            Spacer(Modifier.height(6.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = uiState.dateOfRequest, onValueChange = {}, readOnly = true,
                    placeholder = { Text("Pilih tanggal", color = RailLogColors.TextTertiary) },
                    leadingIcon = { Icon(Icons.Default.CalendarToday, null,
                        tint = RailLogColors.TextTertiary, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = outlinedFieldColors()
                )
                Box(modifier = Modifier.matchParentSize().clickable { showDatePicker = true })
            }
            Spacer(Modifier.height(Spacing.xl))
        }
    }
}

// ── Step 2 ────────────────────────────────────────────────────────────────────

@Composable
private fun Step2ProjectSpecs(uiState: RequisitionFormState, viewModel: RequisitionViewModel) {
    val sites = listOf(
        "Depo MRT Lebak Bulus", "Depo LRT Harjamukti", "Depo LRT Kelapa Gading",
        "Balai Yasa Manggarai", "Workshop KCI Depok", "Depo HSR Tegalluar",
        "Workshop KAI Madiun", "Depo Cipinang"
    )
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Spacer(Modifier.height(Spacing.md))
            PageHeader("Spesifikasi audit", "Pilih tipe armada dan tujuan")
            Spacer(Modifier.height(Spacing.lg))

            Text("Tipe armada", style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium, color = RailLogColors.TextSecondary)
            Spacer(Modifier.height(Spacing.sm))
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.itemGap)) {
                ProjectTypeCard(Modifier.weight(1f), "MRT", Icons.Default.Subway,
                    uiState.projectType == "MRT") { viewModel.updateProjectType("MRT") }
                ProjectTypeCard(Modifier.weight(1f), "LRT", Icons.Default.DirectionsTransit,
                    uiState.projectType == "LRT") { viewModel.updateProjectType("LRT") }
                ProjectTypeCard(Modifier.weight(1f), "KRL", Icons.Default.Train,
                    uiState.projectType == "KRL") { viewModel.updateProjectType("KRL") }
            }
            Spacer(Modifier.height(Spacing.itemGap))
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.itemGap)) {
                ProjectTypeCard(Modifier.weight(1f), "Whoosh / HSR", Icons.Default.Speed,
                    uiState.projectType == "HSR") { viewModel.updateProjectType("HSR") }
                ProjectTypeCard(Modifier.weight(1f), "KAI Executive", Icons.Default.AirlineSeatReclineExtra,
                    uiState.projectType == "PASSENGER") { viewModel.updateProjectType("PASSENGER") }
            }
            Spacer(Modifier.height(Spacing.lg))

            FormTextField("Kode proyek", Icons.Default.Numbers, uiState.projectCode,
                { viewModel.updateProjectCode(it) }, "Format: [TIPE]-[AREA]-[ID]")
            DropdownField("Depo tujuan", Icons.Default.Factory, uiState.destinationSite,
                sites, { viewModel.updateDestinationSite(it) })
        }
    }
}

// ── Step 3 ────────────────────────────────────────────────────────────────────

@Composable
private fun Step3MaterialCatalog(uiState: RequisitionFormState, viewModel: RequisitionViewModel) {
    val displayedItems = uiState.catalogItems.filter { item ->
        (uiState.selectedCategory == "All" || item.category == uiState.selectedCategory) &&
                (uiState.searchQuery.isBlank() || item.name.contains(uiState.searchQuery, ignoreCase = true))
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(Modifier.height(Spacing.md))
        PageHeader("Katalog material", "Input kuantitas untuk setiap komponen")
        Spacer(Modifier.height(Spacing.md))
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Cari komponen teknis...", color = RailLogColors.TextTertiary) },
            leadingIcon = { Icon(Icons.Default.Search, null,
                tint = RailLogColors.TextTertiary, modifier = Modifier.size(18.dp)) },
            shape = RoundedCornerShape(10.dp),
            colors = outlinedFieldColors()
        )
        Spacer(Modifier.height(Spacing.md))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(Spacing.itemGap)) {
            items(displayedItems) { item ->
                SurfaceCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.name, style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium, color = RailLogColors.TextPrimary)
                            Spacer(Modifier.height(2.dp))
                            Text("Stok: ${item.stock} ${item.unit}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (item.isSafe) RailLogColors.Success600 else RailLogColors.Danger600)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { viewModel.updateItemQuantity(item.id, item.reqQty - 1) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Remove, "Kurang", tint = RailLogColors.PrimaryAction)
                            }
                            OutlinedTextField(
                                value = if (item.reqQty == 0) "" else item.reqQty.toString(),
                                onValueChange = { val qty = it.filter { c -> c.isDigit() }.toIntOrNull() ?: 0; viewModel.updateItemQuantity(item.id, qty) },
                                modifier = Modifier.width(60.dp),
                                placeholder = { Text("0", textAlign = TextAlign.Center, color = RailLogColors.TextTertiary) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontWeight = FontWeight.Medium, color = RailLogColors.TextPrimary),
                                colors = outlinedFieldColors()
                            )
                            IconButton(
                                onClick = { viewModel.updateItemQuantity(item.id, item.reqQty + 1) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Add, "Tambah", tint = RailLogColors.PrimaryAction)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Step 4 ────────────────────────────────────────────────────────────────────

@Composable
private fun Step4Justification(uiState: RequisitionFormState, viewModel: RequisitionViewModel) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Spacer(Modifier.height(Spacing.md))
            PageHeader("Lembar justifikasi", "Berikan alasan teknis pengadaan kepada admin")
            Spacer(Modifier.height(Spacing.lg))
            OutlinedTextField(
                value = uiState.notes,
                onValueChange = { viewModel.updateNotes(it) },
                modifier = Modifier.fillMaxWidth().height(180.dp),
                placeholder = { Text("Tuliskan alasan teknis penggunaan barang...",
                    color = RailLogColors.TextTertiary) },
                shape = RoundedCornerShape(10.dp),
                colors = outlinedFieldColors()
            )
            Spacer(Modifier.height(Spacing.lg))
            Button(
                onClick = { viewModel.runPreSubmitCheck() },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RailLogColors.PrimaryAction),
                shape = RoundedCornerShape(10.dp),
                enabled = !uiState.isProcessingPreCheck
            ) {
                if (uiState.isProcessingPreCheck)
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp)
                else {
                    Icon(Icons.Default.AutoAwesome, null,
                        tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Audit kelayakan dengan AI", fontWeight = FontWeight.Medium)
                }
            }
            if (uiState.aiPreCheckResult != null) {
                Spacer(Modifier.height(Spacing.md))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(RailLogColors.AISurface)
                        .border(1.dp, RailLogColors.AIBorder, RoundedCornerShape(10.dp))
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.AutoAwesome, null,
                            tint = RailLogColors.AIText, modifier = Modifier.size(14.dp))
                        Text("Analisis AI", style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold, color = RailLogColors.AIText)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(uiState.aiPreCheckResult,
                        style = MaterialTheme.typography.bodySmall,
                        color = RailLogColors.TextPrimary, lineHeight = 20.sp)
                }
            }
            Spacer(Modifier.height(Spacing.xl))
        }
    }
}

// ── Step 5 ────────────────────────────────────────────────────────────────────

@Composable
private fun Step5FinalReview(uiState: RequisitionFormState, viewModel: RequisitionViewModel) {
    var signaturePaths by remember { mutableStateOf(listOf<Path>()) }
    var currentPath by remember { mutableStateOf<Path?>(null) }
    val requestedItems = uiState.catalogItems.filter { it.reqQty > 0 }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Spacer(Modifier.height(Spacing.md))
            PageHeader("Otorisasi digital", "Tanda tangan ini bersifat mengikat untuk audit resmi")
            Spacer(Modifier.height(Spacing.lg))

            // Summary card
            SurfaceCard {
                Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                    Text("Ringkasan pengajuan", style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium, color = RailLogColors.TextTertiary)
                    Spacer(Modifier.height(Spacing.md))
                    SummaryRow("Pengaju", uiState.requestorName)
                    SubtleDivider(modifier = Modifier.padding(vertical = 8.dp))
                    SummaryRow("ID Pegawai", uiState.employeeId)
                    SubtleDivider(modifier = Modifier.padding(vertical = 8.dp))
                    SummaryRow("Proyek", "${uiState.projectType} (${uiState.projectCode})")
                    SubtleDivider(modifier = Modifier.padding(vertical = 8.dp))
                    SummaryRow("Tujuan", uiState.destinationSite)
                }
            }

            Spacer(Modifier.height(Spacing.md))

            // Material list
            SurfaceCard {
                Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                    Text("Daftar material", style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium, color = RailLogColors.TextTertiary)
                    Spacer(Modifier.height(Spacing.md))
                    requestedItems.forEachIndexed { index, item ->
                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically) {
                            Text(item.name, style = MaterialTheme.typography.bodyMedium,
                                color = RailLogColors.TextPrimary, modifier = Modifier.weight(1f))
                            Text("${item.reqQty} ${item.unit}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold, color = RailLogColors.PrimaryAction)
                        }
                        if (index < requestedItems.lastIndex)
                            SubtleDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }

            Spacer(Modifier.height(Spacing.lg))

            // Signature
            Text("Tanda tangan digital", style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium, color = RailLogColors.TextSecondary)
            Spacer(Modifier.height(Spacing.sm))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(RailLogColors.Surface)
                    .border(1.dp, RailLogColors.BorderSubtle, RoundedCornerShape(12.dp))
                    .clipToBounds()
            ) {
                Canvas(modifier = Modifier.fillMaxSize().pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            currentPath = Path().apply { moveTo(offset.x, offset.y) }
                        },
                        onDrag = { change, _ ->
                            currentPath?.lineTo(change.position.x, change.position.y)
                        },
                        onDragEnd = {
                            currentPath?.let {
                                signaturePaths = signaturePaths + it
                                viewModel.setSignedStatus(true)
                            }
                            currentPath = null
                        }
                    )
                }) {
                    signaturePaths.forEach {
                        drawPath(it, RailLogColors.PrimaryAction,
                            style = Stroke(5f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                    }
                    currentPath?.let {
                        drawPath(it, RailLogColors.PrimaryAction,
                            style = Stroke(5f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                    }
                }
                if (signaturePaths.isEmpty()) {
                    Text("Gambarkan tanda tangan di sini",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodySmall,
                        color = RailLogColors.TextTertiary)
                }
                IconButton(
                    onClick = { signaturePaths = emptyList(); viewModel.setSignedStatus(false) },
                    modifier = Modifier.align(Alignment.TopEnd).size(36.dp)
                ) {
                    Icon(Icons.Default.Refresh, "Hapus",
                        tint = RailLogColors.TextTertiary, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(Modifier.height(Spacing.xl))
        }
    }
}

// ── Shared components ─────────────────────────────────────────────────────────

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodySmall,
            color = RailLogColors.TextTertiary, modifier = Modifier.width(100.dp))
        Text(value, style = MaterialTheme.typography.bodyMedium,
            color = RailLogColors.TextPrimary, fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f), textAlign = TextAlign.End)
    }
}

@Composable
private fun BottomActionBar(
    currentStep: Int,
    isSubmitting: Boolean,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onSubmit: () -> Unit,
    viewModel: RequisitionViewModel
) {
    Surface(
        color = RailLogColors.Surface,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = Spacing.pagePadding, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(Spacing.itemGap)
        ) {
            if (currentStep > 1) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, RailLogColors.BorderSubtle)
                ) {
                    Text("Kembali", color = RailLogColors.TextPrimary, fontWeight = FontWeight.Medium)
                }
            }
            Button(
                onClick = { if (currentStep == 5) onSubmit() else onNext() },
                modifier = Modifier.weight(2f).height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RailLogColors.PrimaryAction),
                shape = RoundedCornerShape(10.dp),
                enabled = !isSubmitting
            ) {
                if (isSubmitting)
                    CircularProgressIndicator(color = Color.White,
                        modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                else
                    Text(
                        if (currentStep == 5) "Submit audit resmi" else "Lanjut ke tahap ${currentStep + 1}",
                        fontWeight = FontWeight.Medium
                    )
            }
        }
    }
}

@Composable
private fun StepProgressBar(currentStep: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.pagePadding, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        for (i in 1..5) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        if (i <= currentStep) RailLogColors.PrimaryAction
                        else RailLogColors.BorderSubtle
                    )
            )
        }
    }
}

@Composable
private fun ProjectTypeCard(
    modifier: Modifier,
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.height(90.dp),
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) RailLogColors.PrimaryAction else RailLogColors.Surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) RailLogColors.PrimaryAction else RailLogColors.BorderSubtle
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null,
                tint = if (isSelected) Color.White else RailLogColors.TextSecondary,
                modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(4.dp))
            Text(title, style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) Color.White else RailLogColors.TextSecondary,
                textAlign = TextAlign.Center)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormTextField(
    label: String,
    icon: ImageVector,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    readOnly: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = Spacing.md)) {
        Text(label, style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium, color = RailLogColors.TextSecondary)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            readOnly = readOnly,
            placeholder = { Text(placeholder, color = RailLogColors.TextTertiary) },
            leadingIcon = { Icon(icon, null,
                tint = RailLogColors.TextTertiary, modifier = Modifier.size(18.dp)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = outlinedFieldColors()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownField(
    label: String,
    icon: ImageVector,
    selected: String,
    options: List<String>,
    onSelection: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = Spacing.md)) {
        Text(label, style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium, color = RailLogColors.TextSecondary)
        Spacer(Modifier.height(6.dp))
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value = selected, onValueChange = {}, readOnly = true,
                leadingIcon = { Icon(icon, null,
                    tint = RailLogColors.TextTertiary, modifier = Modifier.size(18.dp)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                shape = RoundedCornerShape(10.dp),
                colors = outlinedFieldColors()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(RailLogColors.Surface)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, style = MaterialTheme.typography.bodyMedium,
                            color = RailLogColors.TextPrimary) },
                        onClick = { onSelection(option); expanded = false }
                    )
                }
            }
        }
    }
}

@Composable
private fun outlinedFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = RailLogColors.TextPrimary,
    unfocusedTextColor = RailLogColors.TextPrimary,
    focusedBorderColor = RailLogColors.PrimaryAction,
    unfocusedBorderColor = RailLogColors.BorderSubtle,
    focusedContainerColor = RailLogColors.Surface,
    unfocusedContainerColor = RailLogColors.Surface,
    focusedLabelColor = RailLogColors.PrimaryAction,
    unfocusedLabelColor = RailLogColors.TextTertiary
)