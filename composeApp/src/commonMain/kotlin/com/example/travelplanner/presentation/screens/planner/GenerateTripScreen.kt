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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ══════════════════════════════════════════════════════════════════════
//  DATA
// ══════════════════════════════════════════════════════════════════════

data class VibeOption(
    val label: String,
    val subtitle: String,
    val animationScene: @Composable (Modifier) -> Unit
)

private val vibeOptions = listOf(
    VibeOption("Alam",        "Pegunungan & hutan")  { m -> AlamAnimatedScene(m) },
    VibeOption("Pantai",      "Laut & tepi pantai")   { m -> PantaiAnimatedScene(m) },
    VibeOption("Kuliner",     "Wisata cita rasa")     { m -> KulinerAnimatedScene(m) },
    VibeOption("Sejarah",     "Budaya & peninggalan") { m -> SejarahAnimatedScene(m) },
    VibeOption("Santai",      "Slow travel & rehat")  { m -> SantaiAnimatedScene(m) },
    VibeOption("Petualangan", "Adrenalin & outdoor")  { m -> PetualanganAnimatedScene(m) }
)

// ══════════════════════════════════════════════════════════════════════
//  SCREEN
// ══════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateTripScreen(
    onNavigateBack: () -> Unit,
    onGenerateSuccess: (String) -> Unit
) {
    var departureCity      by remember { mutableStateOf("") }
    var destination        by remember { mutableStateOf("") }
    var startDate          by remember { mutableStateOf("") }
    var endDate            by remember { mutableStateOf("") }
    var budget             by remember { mutableStateOf("") }
    var numberOfTravelers  by remember { mutableStateOf("1") }
    var selectedVibe       by remember { mutableStateOf("") }
    var specialNotes       by remember { mutableStateOf("") }

    val isFormValid = departureCity.isNotBlank() && destination.isNotBlank()
            && startDate.isNotBlank() && endDate.isNotBlank() && selectedVibe.isNotBlank()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text("Rancang Perjalanan",
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.3.sp)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            Surface(
                tonalElevation = 4.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                    Button(
                        onClick = { onGenerateSuccess("dummy_trip_id_verification") },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        enabled = isFormValid,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null,
                            modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Buat Itinerary dengan AI",
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.3.sp)
                    }
                    if (!isFormValid) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("* Lengkapi semua kolom wajib untuk melanjutkan",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // ── SECTION 1: RUTE ──────────────────────────────────────
            FormSection(icon = Icons.Default.AirplanemodeActive, title = "Rute Perjalanan") {
                ElegantTextField(
                    value = departureCity, onValueChange = { departureCity = it },
                    label = "Kota Keberangkatan", placeholder = "Jakarta",
                    icon = Icons.Default.FlightTakeoff, isRequired = true
                )
                ElegantTextField(
                    value = destination, onValueChange = { destination = it },
                    label = "Kota Tujuan", placeholder = "Bali",
                    icon = Icons.Default.LocationOn, isRequired = true
                )
            }

            // ── SECTION 2: TANGGAL ───────────────────────────────────
            FormSection(icon = Icons.Default.CalendarMonth, title = "Jadwal Perjalanan") {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ElegantTextField(
                        value = startDate, onValueChange = { startDate = it },
                        label = "Berangkat", placeholder = "10 Jul",
                        icon = Icons.Default.FlightTakeoff, isRequired = true,
                        modifier = Modifier.weight(1f)
                    )
                    ElegantTextField(
                        value = endDate, onValueChange = { endDate = it },
                        label = "Kembali", placeholder = "14 Jul",
                        icon = Icons.Default.FlightLand, isRequired = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ── SECTION 3: DETAIL ────────────────────────────────────
            FormSection(icon = Icons.Default.Group, title = "Detail Rombongan") {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ElegantTextField(
                        value = numberOfTravelers,
                        onValueChange = { if (it.all(Char::isDigit) && it.length <= 2) numberOfTravelers = it },
                        label = "Jumlah Orang", placeholder = "2",
                        icon = Icons.Default.People,
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f)
                    )
                    ElegantTextField(
                        value = budget, onValueChange = { budget = it },
                        label = "Estimasi Budget", placeholder = "Rp 2.500.000",
                        icon = Icons.Default.Wallet,
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ── SECTION 4: VIBE (ANIMATED CARDS) ────────────────────
            FormSection(icon = Icons.Default.Explore, title = "Gaya Perjalanan",
                subtitle = "Pilih satu yang paling mencerminkan perjalananmu") {
                val rows = vibeOptions.chunked(2)
                rows.forEach { rowItems ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
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

            // ── SECTION 5: CATATAN ───────────────────────────────────
            FormSection(icon = Icons.Default.EditNote, title = "Catatan Perjalanan",
                subtitle = "Preferensi khusus, kebutuhan, atau permintaan tambahan") {
                OutlinedTextField(
                    value = specialNotes, onValueChange = { specialNotes = it },
                    placeholder = {
                        Text("Contoh: vegetarian, ada balita, hindari tempat ramai...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                    },
                    modifier = Modifier.fillMaxWidth().height(96.dp),
                    maxLines = 4,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

// ══════════════════════════════════════════════════════════════════════
//  ANIMATED VIBE CARD — canvas animation + selection state
// ══════════════════════════════════════════════════════════════════════

@Composable
fun AnimatedVibeCard(
    vibe: VibeOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scaleAnim by animateFloatAsState(
        targetValue = if (isSelected) 1.03f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )
    val borderAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = tween(200), label = "border"
    )
    val overlayAlpha by animateFloatAsState(
        targetValue = if (isSelected) 0f else 0.30f,
        animationSpec = tween(200), label = "overlay"
    )

    Box(
        modifier = modifier
            .height(130.dp)
            .shadow(
                elevation = if (isSelected) 12.dp else 4.dp,
                shape = RoundedCornerShape(14.dp),
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
            )
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        // Animated canvas scene
        vibe.animationScene(Modifier.fillMaxSize())

        // Dark overlay when not selected (dims the animation)
        Box(
            Modifier.fillMaxSize()
                .background(Color.Black.copy(alpha = overlayAlpha))
        )

        // Gold selection border ring
        if (isSelected) {
            Box(
                Modifier.fillMaxSize()
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = borderAlpha),
                        shape = RoundedCornerShape(14.dp)
                    )
            )
        }

        // Label overlay at bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.72f))
                    )
                )
                .padding(horizontal = 10.dp, vertical = 10.dp)
        ) {
            Column {
                Text(
                    text = vibe.label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = vibe.subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.80f)
                )
            }
        }

        // Selected checkmark badge (top-right)
        AnimatedVisibility(
            visible = isSelected,
            modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
            enter = fadeIn() + expandVertically()
        ) {
            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(24.dp)
            ) {
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(icon, contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp))
                Text(title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    letterSpacing = 0.4.sp)
            }
            if (subtitle != null) {
                Text(subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 26.dp))
            }
            HorizontalDivider(
                modifier = Modifier.padding(top = 4.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
            )
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
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(if (isRequired) "$label *" else label,
                style = MaterialTheme.typography.labelMedium)
        },
        placeholder = {
            Text(placeholder,
                style = MaterialTheme.typography.bodySmall,
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
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    )
}
