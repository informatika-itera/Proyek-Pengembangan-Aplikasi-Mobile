package com.example.pantaujompo.presentation.screens.riwayat

import android.graphics.Paint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil3.compose.AsyncImage
import org.koin.compose.viewmodel.koinViewModel
import com.example.pantaujompo.data.local.room.MakananEntity
import com.example.pantaujompo.data.local.room.RiwayatEntity
import com.example.pantaujompo.presentation.theme.*
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

import com.example.pantaujompo.core.util.AppStrings

// ======================== ENTRY POINT ========================
@Composable
fun RiwayatScreen(
    viewModel: RiwayatViewModel = koinViewModel(),
    onNavigateToDetail: (Int) -> Unit = {}
) {
    val riwayatLariList by viewModel.riwayatLariState.collectAsState()
    val makananList by viewModel.makananState.collectAsState()
    val language by viewModel.languageState.collectAsState()
    val currentFilter by viewModel.timeFilter.collectAsState()

    RiwayatListView(
        riwayatLariList = riwayatLariList,
        makananList = makananList,
        onActivityClick = { onNavigateToDetail(it.id) },
        onDeleteActivity = { viewModel.deleteActivity(it) },
        onDeleteMakanan = { viewModel.deleteMakanan(it) },
        language = language,
        currentFilter = currentFilter,
        onFilterChange = { viewModel.setTimeFilter(it) }
    )
}

// ======================== MAIN VIEW ========================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiwayatListView(
    riwayatLariList: List<RiwayatEntity>,
    makananList: List<MakananEntity>,
    onActivityClick: (RiwayatEntity) -> Unit,
    onDeleteActivity: (Int) -> Unit,
    onDeleteMakanan: (Int) -> Unit,
    language: String,
    currentFilter: TimeFilter,
    onFilterChange: (TimeFilter) -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background == DarkBackground
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    val accentColor = MaterialTheme.colorScheme.primary
    val surfaceColor = if (isDark) Color(0xFF131313) else MaterialTheme.colorScheme.surface

    // Tab: 0 = Olahraga, 1 = Nutrisi
    var selectedTabIndex by remember { mutableStateOf(0) }
    // Offset minggu: 0 = minggu ini, -1 = minggu lalu
    var weekOffset by remember { mutableStateOf(0) }
    // Bar/hari yang sedang difilter (null = semua hari)
    var selectedDayKey by remember { mutableStateOf<String?>(null) }
    // State detail makanan yang dipilih
    var selectedMakanan by remember { mutableStateOf<MakananEntity?>(null) }

    // Format tanggal
    val sdfDay = SimpleDateFormat("EEE", Locale("id", "ID"))
    val sdfDate = SimpleDateFormat("dd", Locale("id", "ID"))
    val sdfKey = SimpleDateFormat("yyyyMMdd", Locale("id", "ID"))
    val sdfMonth = SimpleDateFormat("dd MMM", Locale("id", "ID"))
    val sdfKeyParse = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

    // Hitung 7 hari dalam minggu yang dipilih (mulai Senin)
    val weekDays = remember(weekOffset) {
        val cal = Calendar.getInstance(Locale("id", "ID")).apply {
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            add(Calendar.WEEK_OF_YEAR, weekOffset)
        }
        List(7) {
            val triple = Triple(
                sdfDay.format(cal.time),    // Nama hari singkat (Sen, Sel, ...)
                sdfDate.format(cal.time),    // Tanggal (01, 02, ...)
                sdfKey.format(cal.time)      // Key unik untuk filter (yyyyMMdd)
            )
            cal.add(Calendar.DAY_OF_MONTH, 1)
            triple
        }
    }

    // Label periode minggu, misal: "12 Mei – 18 Mei"
    val weekLabel = remember(weekDays) {
        try {
            val start = sdfKeyParse.parse(weekDays.first().third)
            val end = sdfKeyParse.parse(weekDays.last().third)
            if (start != null && end != null) "${sdfMonth.format(start)} – ${sdfMonth.format(end)}" else ""
        } catch (e: Exception) { "" }
    }

    // Menggunakan filter dari ViewModel, hanya terapkan selectedDayKey dari bar chart jika ada
    val filteredLariList = riwayatLariList
        .filter { selectedDayKey == null || sdfKey.format(Date(it.tanggal)) == selectedDayKey }
        .sortedByDescending { it.tanggal }

    val filteredMakananList = makananList
        .filter { selectedDayKey == null || sdfKey.format(Date(it.tanggal)) == selectedDayKey }
        .sortedByDescending { it.tanggal }

    // Hitung ringkasan mingguan olahraga
    val weekKeys = weekDays.map { it.third }.toSet()
    val weeklyLari = riwayatLariList.filter { sdfKey.format(Date(it.tanggal)) in weekKeys }
    val weeklyKm = weeklyLari.sumOf { it.jarak }
    val weeklyMenit = weeklyLari.sumOf { it.durasi }
    val weeklyKalLari = weeklyLari.sumOf { it.kalori }

    // Hitung ringkasan mingguan nutrisi
    val weeklyMakan = makananList.filter { sdfKey.format(Date(it.tanggal)) in weekKeys }
    val weeklyKalNutrisi = weeklyMakan.sumOf { it.protein * 4 + it.karbo * 4 + it.lemak * 9 }
    val weeklyProtein = weeklyMakan.sumOf { it.protein }

    // Nilai bar chart per hari (olahraga = km, nutrisi = kalori)
    val dailyLariKm = weekDays.map { (_, _, key) ->
        riwayatLariList.filter { sdfKey.format(Date(it.tanggal)) == key }.sumOf { it.jarak }.toFloat()
    }
    val dailyKalNutrisi = weekDays.map { (_, _, key) ->
        makananList.filter { sdfKey.format(Date(it.tanggal)) == key }
            .sumOf { it.protein * 4 + it.karbo * 4 + it.lemak * 9 }.toFloat()
    }
    val barValues = if (selectedTabIndex == 0) dailyLariKm else dailyKalNutrisi
    // Nilai max bar: minimal 5km untuk olahraga, 500kcal untuk nutrisi
    val maxBarValue = (barValues.maxOrNull() ?: 0f).coerceAtLeast(if (selectedTabIndex == 0) 5f else 500f)
    val barUnit = if (selectedTabIndex == 0) "km" else "kcal"
    val barColor = if (selectedTabIndex == 0) accentColor else Color(0xFF00BCD4)

    MeshBackground(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // ======== HEADER ========
            item {
                Spacer(modifier = Modifier.height(48.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Statistik", color = textPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp)
                        Text("Riwayat & Pencapaian", color = textSecondary, fontSize = 13.sp)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))

                // ======== TAB SEGMENTED BUTTON ========
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(surfaceColor)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                        .padding(4.dp)
                ) {
                    listOf(
                        Pair(Icons.Default.DirectionsRun, if (language == "en") "Workout" else "Olahraga"),
                        Pair(Icons.Default.RestaurantMenu, if (language == "en") "Nutrition" else "Nutrisi")
                    ).forEachIndexed { index, (icon, label) ->
                        val isSelected = selectedTabIndex == index
                        val activeColor = if (index == 0) accentColor else Color(0xFF00BCD4)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) activeColor
                                    else Color.Transparent
                                )
                                .clickable {
                                    selectedTabIndex = index
                                    selectedDayKey = null
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    icon,
                                    null,
                                    tint = if (isSelected) Color.Black else textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    label,
                                    color = if (isSelected) Color.Black else textSecondary,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ======== KARTU NAVIGATOR MINGGU + CHART ========
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(surfaceColor)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        // ==== NAVIGASI MINGGU ====
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Tombol ke minggu sebelumnya
                            Box(
                                modifier = Modifier.size(38.dp).clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { weekOffset--; selectedDayKey = null },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.ChevronLeft, null, tint = textPrimary, modifier = Modifier.size(22.dp))
                            }

                            // Label periode minggu
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    when (weekOffset) {
                                        0 -> AppStrings.get("minggu_ini", language)
                                        -1 -> "Minggu Lalu"
                                        else -> "$weekOffset minggu lalu"
                                    },
                                    color = textPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp
                                )
                                Text(weekLabel, color = textSecondary, fontSize = 12.sp)
                            }

                            // Tombol ke minggu berikutnya (nonaktif jika sudah minggu ini)
                            Box(
                                modifier = Modifier.size(38.dp).clip(CircleShape)
                                    .background(
                                        if (weekOffset < 0) MaterialTheme.colorScheme.surfaceVariant
                                        else Color.Gray.copy(0.15f)
                                    )
                                    .clickable(enabled = weekOffset < 0) { weekOffset++; selectedDayKey = null },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.ChevronRight, null,
                                    tint = if (weekOffset < 0) textPrimary else textSecondary.copy(0.3f),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // ==== RINGKASAN STATS MINGGUAN ====
                        Row(modifier = Modifier.fillMaxWidth()) {
                            if (selectedTabIndex == 0) {
                                // Stats olahraga: Jarak, Waktu, Kalori
                                WeeklyStatItem("Jarak", "${String.format(Locale.US, "%.1f", weeklyKm)} km", accentColor, Modifier.weight(1f))
                                Box(modifier = Modifier.width(1.dp).height(44.dp).background(MaterialTheme.colorScheme.outline).align(Alignment.CenterVertically))
                                WeeklyStatItem("Waktu", "${weeklyMenit}m", Color(0xFF00BCD4), Modifier.weight(1f))
                                Box(modifier = Modifier.width(1.dp).height(44.dp).background(MaterialTheme.colorScheme.outline).align(Alignment.CenterVertically))
                                WeeklyStatItem("Kalori", "$weeklyKalLari kcal", Color(0xFFFF9100), Modifier.weight(1f))
                            } else {
                                // Stats nutrisi: Kalori, Protein, Asupan
                                WeeklyStatItem("Total Kalori", "$weeklyKalNutrisi kcal", Color(0xFF00BCD4), Modifier.weight(1f))
                                Box(modifier = Modifier.width(1.dp).height(44.dp).background(MaterialTheme.colorScheme.outline).align(Alignment.CenterVertically))
                                WeeklyStatItem("Protein", "${weeklyProtein}g", Color(0xFF00E676), Modifier.weight(1f))
                                Box(modifier = Modifier.width(1.dp).height(44.dp).background(MaterialTheme.colorScheme.outline).align(Alignment.CenterVertically))
                                WeeklyStatItem("Asupan", "${weeklyMakan.size}", Color(0xFFFF9100), Modifier.weight(1f))
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // ==== BAR CHART 7 HARI ====
                        Row(modifier = Modifier.fillMaxWidth()) {
                            // Label sumbu Y (kiri)
                            Column(
                                modifier = Modifier.width(38.dp).height(120.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Format label: jika >= 1000 pakai 'k', jika float tampilkan 1 desimal
                                fun fmtLabel(v: Float): String = when {
                                    v >= 1000f -> "${(v / 1000f).toInt()}k"
                                    v > 0f -> String.format(Locale.US, "%.1f", v)
                                    else -> "0"
                                }
                                Text("${fmtLabel(maxBarValue)}$barUnit", color = textSecondary, fontSize = 8.sp, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
                                Text("${fmtLabel(maxBarValue / 2)}$barUnit", color = textSecondary, fontSize = 8.sp, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
                                Text("0", color = textSecondary, fontSize = 8.sp, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Batang-batang bar 7 hari
                            Row(
                                modifier = Modifier.weight(1f).height(140.dp),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                weekDays.forEachIndexed { index, (dayName, dateNum, dateKey) ->
                                    val isSelected = selectedDayKey == dateKey
                                    val rawVal = barValues.getOrElse(index) { 0f }
                                    // Animasi tinggi bar saat pertama muncul atau saat nilai berubah
                                    val heightFrac by animateFloatAsState(
                                        targetValue = (rawVal / maxBarValue).coerceIn(0f, 1f),
                                        animationSpec = tween(400),
                                        label = "bar_$index"
                                    )

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Bottom,
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .clickable {
                                                // Toggle: klik bar yang sama untuk batal filter
                                                selectedDayKey = if (isSelected) null else dateKey
                                            }
                                    ) {
                                        // Nilai di atas bar (hanya jika ada data)
                                        if (rawVal > 0f) {
                                            Text(
                                                when {
                                                    rawVal >= 1000f -> "${(rawVal / 1000f).toInt()}k"
                                                    else -> String.format(Locale.US, "%.1f", rawVal)
                                                },
                                                color = if (isSelected) barColor else textSecondary,
                                                fontSize = 7.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))

                                        // Batang bar dengan animasi tinggi
                                        Box(
                                            modifier = Modifier
                                                .width(if (isSelected) 24.dp else 18.dp)
                                                .fillMaxHeight(heightFrac.coerceAtLeast(0.04f))
                                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                                .background(
                                                    when {
                                                        isSelected -> barColor
                                                        rawVal > 0f -> barColor.copy(0.35f)
                                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                                    }
                                                )
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        // Label tanggal dan hari
                                        Text(
                                            dateNum,
                                            color = if (isSelected) barColor else textSecondary,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal
                                        )
                                        Text(dayName, color = if (isSelected) textPrimary else textSecondary, fontSize = 9.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // Chip info filter aktif — tampil saat ada hari dipilih
                if (selectedDayKey != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(barColor.copy(0.1f))
                            .border(1.dp, barColor.copy(0.25f), RoundedCornerShape(12.dp))
                            .clickable { selectedDayKey = null }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.FilterList, null, tint = barColor, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Filter hari aktif — ketuk untuk tampilkan semua", color = barColor, fontSize = 12.sp, modifier = Modifier.weight(1f))
                        Icon(Icons.Default.Close, null, tint = barColor, modifier = Modifier.size(14.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ======== JUDUL SECTION LIST ========
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (selectedTabIndex == 0) AppStrings.get("riwayat_olahraga", language) else "Riwayat Nutrisi",
                        color = textPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp
                    )
                    val count = if (selectedTabIndex == 0) filteredLariList.size else filteredMakananList.size
                    if (count > 0) Text("$count ${AppStrings.get("total_sesi", language)}", color = textSecondary, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // ======== LIST KONTEN BERDASARKAN TAB ========
            if (selectedTabIndex == 0) {
                // --- TAB OLAHRAGA ---
                if (filteredLariList.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 40.dp), contentAlignment = Alignment.Center) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .glassCard(shape = RoundedCornerShape(24.dp), neonColor = accentColor)
                                    .padding(32.dp)
                            ) {
                                Icon(Icons.Default.DirectionsRun, contentDescription = null, tint = accentColor, modifier = Modifier.size(64.dp))
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Belum Ada Aktivitas",
                                    color = textPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    if (selectedDayKey != null) "Tidak ada olahraga di hari ini."
                                    else "Yuk, mulai olahraga pertamamu minggu ini!",
                                    color = textSecondary, fontSize = 14.sp, textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(filteredLariList, key = { it.id }) { activity ->
                        Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 5.dp)) {
                            ItemOlahragaCard(
                                activity = activity,
                                onDelete = { onDeleteActivity(activity.id) },
                                onClick = { onActivityClick(activity) }
                            )
                        }
                    }
                }
            } else {
                // --- TAB NUTRISI ---
                if (filteredMakananList.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 40.dp), contentAlignment = Alignment.Center) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .glassCard(shape = RoundedCornerShape(24.dp), neonColor = Color(0xFF00BCD4))
                                    .padding(32.dp)
                            ) {
                                Icon(Icons.Default.RestaurantMenu, contentDescription = null, tint = Color(0xFF00BCD4), modifier = Modifier.size(64.dp))
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Belum Ada Nutrisi",
                                    color = textPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    if (selectedDayKey != null) "Tidak ada asupan di hari ini."
                                    else "Catat asupan makan pertamamu minggu ini!",
                                    color = textSecondary, fontSize = 14.sp, textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(filteredMakananList, key = { it.id }) { makanan ->
                        Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 5.dp)) {
                            ItemMakananCard(
                                makanan = makanan,
                                onDelete = { onDeleteMakanan(makanan.id) },
                                onClick = { selectedMakanan = makanan }
                            )
                        }
                    }
                }
            }
        }
    }

    // ======== BOTTOM SHEET DETAIL MAKANAN ========
    selectedMakanan?.let { mkn ->
        val isDark2 = MaterialTheme.colorScheme.background == DarkBackground
        val surface2 = if (isDark2) Color(0xFF131313) else MaterialTheme.colorScheme.surface
        val textP2 = MaterialTheme.colorScheme.onBackground
        val textS2 = MaterialTheme.colorScheme.onSurfaceVariant
        val accent2 = MaterialTheme.colorScheme.primary

        ModalBottomSheet(
            onDismissRequest = { selectedMakanan = null },
            containerColor = surface2
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                Text("Detail Nutrisi", color = textP2, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                Spacer(modifier = Modifier.height(16.dp))
                // Tampilkan foto makanan jika ada
                if (!mkn.photoUri.isNullOrBlank()) {
                    AsyncImage(
                        model = mkn.photoUri,
                        contentDescription = "Food",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(16.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                androidx.compose.ui.graphics.Brush.linearGradient(
                                    listOf(
                                        accent2.copy(0.12f),
                                        Color(0xFF00BCD4).copy(0.08f)
                                    )
                                )
                            )
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Restaurant,
                                contentDescription = null,
                                tint = accent2,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Nutrisi Teranalisis AI",
                                color = accent2,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(mkn.namaMakanan, color = textP2, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")).format(Date(mkn.tanggal)),
                    color = textS2, fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                // 4 chip nutrisi
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NutrientDetailChip("Protein", mkn.protein, "g", Color(0xFF00E676), Modifier.weight(1f))
                    NutrientDetailChip("Karbo", mkn.karbo, "g", Color(0xFF00BCD4), Modifier.weight(1f))
                    NutrientDetailChip("Lemak", mkn.lemak, "g", Color(0xFFFF9100), Modifier.weight(1f))
                    NutrientDetailChip("Kalori", mkn.protein * 4 + mkn.karbo * 4 + mkn.lemak * 9, "kcal", Color(0xFFFF5252), Modifier.weight(1.2f))
                }
                // Insight AI jika ada
                if (mkn.info.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(accent2.copy(0.08f))
                            .border(1.dp, accent2.copy(0.2f), RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Text("💡 Insight AI", color = accent2, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(mkn.info, color = textS2, fontSize = 13.sp, lineHeight = 20.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// ======================== KOMPONEN PENDUKUNG ========================

/** Kolom stats mingguan (label + nilai berwarna) */
@Composable
fun WeeklyStatItem(label: String, value: String, color: Color, modifier: Modifier) {
    Column(modifier = modifier.padding(vertical = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
    }
}

/** Chip nutrisi kecil di bottom sheet detail */
@Composable
fun NutrientDetailChip(label: String, value: Int, unit: String, color: Color, modifier: Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(0.12f))
            .border(1.dp, color.copy(0.2f), RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$value$unit", color = color, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
            Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
        }
    }
}

/** Card olahraga dengan icon emoji, stats chip, dan tombol hapus */
@Composable
fun ItemOlahragaCard(
    activity: RiwayatEntity,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background == DarkBackground
    val surfaceColor = if (isDark) Color(0xFF1A1A1A) else MaterialTheme.colorScheme.surface
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    val accentColor = MaterialTheme.colorScheme.primary
    val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale("id", "ID"))
    var showDeleteConfirm by remember { mutableStateOf(false) }

    // Pilih icon berdasarkan jenis aktivitas
    val jenisIcon = when (activity.jenis?.lowercase()) {
        "sepeda" -> Icons.Default.DirectionsBike
        "jalan" -> Icons.Default.DirectionsWalk
        else -> Icons.Default.DirectionsRun
    }
    val jenisLabel = activity.jenis ?: "Lari"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glassCard()
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            // Ikon aktivitas
            Box(
                modifier = Modifier.size(52.dp).clip(CircleShape)
                    .background(accentColor.copy(0.1f))
                    .border(1.dp, accentColor.copy(0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) { Icon(jenisIcon, null, tint = accentColor, modifier = Modifier.size(28.dp)) }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(jenisLabel, color = textPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(sdf.format(Date(activity.tanggal)), color = textSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                // Chip-chip statistik
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ActivityChip("${String.format(Locale.US, "%.2f", activity.jarak)} km", accentColor)
                    ActivityChip("${activity.durasi}m", Color(0xFF00BCD4))
                    ActivityChip("${activity.kalori} kcal", Color(0xFFFF9100))
                }
            }
            // Tombol hapus
            IconButton(onClick = { showDeleteConfirm = true }) {
                Icon(Icons.Default.DeleteOutline, null, tint = Color(0xFFFF5252).copy(0.7f), modifier = Modifier.size(20.dp))
            }
        }
    }

    // Dialog konfirmasi hapus
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Hapus Aktivitas?", color = MaterialTheme.colorScheme.onBackground) },
            text = { Text("Riwayat ini akan dihapus permanen.", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            confirmButton = {
                Button(
                    onClick = { onDelete(); showDeleteConfirm = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                ) { Text("Hapus", color = Color.White, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Batal", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = if (isDark) Color(0xFF1A1A1A) else MaterialTheme.colorScheme.surface
        )
    }
}

/** Chip kecil untuk statistik aktivitas olahraga */
@Composable
fun ActivityChip(text: String, color: Color) {
    Text(
        text, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold,
        modifier = Modifier
            .background(color.copy(0.12f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    )
}

/** Card makanan yang bisa diklik untuk lihat detail */
@Composable
fun ItemMakananCard(
    makanan: MakananEntity,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background == DarkBackground
    val surfaceColor = if (isDark) Color(0xFF1A1A1A) else MaterialTheme.colorScheme.surface
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    val formatter = SimpleDateFormat("HH:mm", Locale("id", "ID"))
    val kalori = makanan.protein * 4 + makanan.karbo * 4 + makanan.lemak * 9
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glassCard()
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            // Foto atau emoji default
            Box(
                modifier = Modifier.size(52.dp).clip(RoundedCornerShape(14.dp))
                    .background(if (isDark) Color(0xFF252525) else Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                if (!makanan.photoUri.isNullOrBlank()) {
                    AsyncImage(
                        model = makanan.photoUri,
                        contentDescription = "Food",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = Color(0xFF00BCD4),
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(makanan.namaMakanan, color = textPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("${formatter.format(Date(makanan.tanggal))} · $kalori kcal", color = textSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ActivityChip("P:${makanan.protein}g", Color(0xFF00E676))
                    ActivityChip("K:${makanan.karbo}g", Color(0xFF00BCD4))
                    ActivityChip("L:${makanan.lemak}g", Color(0xFFFF9100))
                }
            }
            IconButton(onClick = { showDeleteConfirm = true }) {
                Icon(Icons.Default.DeleteOutline, null, tint = Color(0xFFFF5252).copy(0.7f), modifier = Modifier.size(20.dp))
            }
            Icon(Icons.Default.ChevronRight, null, tint = textSecondary, modifier = Modifier.size(20.dp))
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Hapus Riwayat Makan?", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground) },
            text = { Text("Asupan ini akan dihapus permanen dari riwayat gizi Anda.", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            confirmButton = {
                Button(
                    onClick = { onDelete(); showDeleteConfirm = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                ) { Text("Hapus", color = Color.White, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Batal", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = if (isDark) Color(0xFF1A1A1A) else MaterialTheme.colorScheme.surface
        )
    }
}
