package com.example.pantaujompo.presentation.screens.home

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import android.location.Geocoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil3.compose.AsyncImage
import com.example.pantaujompo.core.util.AppStrings
import com.example.pantaujompo.data.local.datastore.UserPreferences
import com.example.pantaujompo.data.remote.model.WeatherInfo
import com.example.pantaujompo.domain.TrackingManager
import com.example.pantaujompo.presentation.theme.*
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToTracking: (String) -> Unit,
    onNavigateToRiwayat: () -> Unit = {},
    onNavigateToProfil: () -> Unit = {},
    onNavigateToAiChat: () -> Unit = {},
    viewModel: DashboardViewModel = koinViewModel(),
    userPreferences: UserPreferences = koinInject()
) {
    val context = LocalContext.current
    val language by userPreferences.language.collectAsState(initial = "id")
    fun str(key: String): String = AppStrings.get(key, language)
    
    val userName by viewModel.userName.collectAsState()
    val profileImageUri by viewModel.profileImageUri.collectAsState()
    val weeklyJarak by viewModel.weeklyJarak.collectAsState(initial = 0.0)
    val weeklyKalori by viewModel.weeklyKalori.collectAsState(initial = 0)
    val weeklyDurasi by viewModel.weeklyDurasi.collectAsState(initial = 0)
    
    val dailyJarak by viewModel.dailyJarak.collectAsState(initial = 0.0)
    val dailyKalori by viewModel.dailyKalori.collectAsState(initial = 0)
    
    val dailyKarbo by viewModel.dailyKarbo.collectAsState(initial = 0)
    val dailyProtein by viewModel.dailyProtein.collectAsState(initial = 0)
    val dailyLemak by viewModel.dailyLemak.collectAsState(initial = 0)

    val isDark by userPreferences.isDarkMode.collectAsState(initial = true)
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    val accentColor = MaterialTheme.colorScheme.primary
    val surfaceColor = if (isDark) Color(0xFF151515) else MaterialTheme.colorScheme.surface
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

    var showActivityMenu by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 0..4 -> str("selamat_malam")
        in 5..10 -> str("selamat_pagi")
        in 11..14 -> str("selamat_siang")
        in 15..17 -> str("selamat_sore")
        else -> str("selamat_malam")
    }

    // Tracking state
    val isTrackingStarted by TrackingManager.hasStarted.collectAsState()
    val trackingSeconds by TrackingManager.seconds.collectAsState()
    val weatherService = koinInject<com.example.pantaujompo.data.remote.api.WeatherService>()
    var weatherInfo by remember { mutableStateOf<WeatherInfo?>(null) }
    var locationName by remember { mutableStateOf("Lokasi Anda") }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var locationFetched by remember { mutableStateOf(false) }

    val appLocale = if (language == "en") Locale("en", "US") else Locale("id", "ID")

    // Calendar week
    val calendar = Calendar.getInstance(appLocale).apply {
        firstDayOfWeek = Calendar.MONDAY
        set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    }
    val sdfDay = SimpleDateFormat("EEE", appLocale)
    val sdfDate = SimpleDateFormat("dd", appLocale)
    val sdfKey = SimpleDateFormat("yyyyMMdd", appLocale)
    val todayKey = sdfKey.format(Date())
    
    val weekDays = List(7) {
        val dayName = sdfDay.format(calendar.time)
        val dateNum = sdfDate.format(calendar.time)
        val isToday = sdfKey.format(calendar.time) == todayKey
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        Triple(dayName, dateNum, isToday)
    }
    
    val todayIndex = weekDays.indexOfFirst { it.third }
    val listState = rememberLazyListState()
    
    LaunchedEffect(todayIndex) {
        if (todayIndex >= 0) {
            listState.animateScrollToItem(maxOf(0, todayIndex - 1))
        }
    }

    val trackingDistance by TrackingManager.totalDistanceMeters.collectAsState()
    val currentJenis = TrackingManager.jenisOlahraga.collectAsState().value

    @SuppressLint("MissingPermission")
    fun fetchWeather() {
        if (!locationFetched) {
            try {
                // Menggunakan BALANCED_POWER_ACCURACY (Sinyal WiFi/BTS) agar super cepat dan tidak menunggu satelit GPS
                fusedLocationClient.getCurrentLocation(com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY, null).addOnCompleteListener { task ->
                    val loc = if (task.isSuccessful) task.result else null
                    coroutineScope.launch {
                        try {
                            val lat = loc?.latitude ?: -6.2088
                            val lon = loc?.longitude ?: 106.8456
                            
                            weatherInfo = weatherService.getWeatherAndAqi(lat, lon)
                            
                            if (loc != null) {
                                withContext(Dispatchers.IO) {
                                    try {
                                        val geocoder = Geocoder(context, Locale.getDefault())
                                        val addresses = geocoder.getFromLocation(lat, lon, 1)
                                        if (!addresses.isNullOrEmpty()) {
                                            val address = addresses[0]
                                            val city = address.locality ?: address.subAdminArea ?: address.adminArea
                                            if (city != null) locationName = city
                                        }
                                    } catch (e: Exception) {
                                        // Ignore geocoding errors
                                    }
                                }
                            } else {
                                locationName = "Jakarta (Default)"
                            }
                        } catch (e: Exception) {
                            // If it fails, we will show "Gagal"
                            locationName = "Gagal memuat"
                        } finally {
                            locationFetched = true
                        }
                    }
                }
            } catch (e: SecurityException) {
                locationFetched = true
            }
        }
    }

    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions -> 
            val fine = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true
            val coarse = permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
            if (fine || coarse) fetchWeather()
        }
    )

    LaunchedEffect(Unit) {
        viewModel.fetchDailyInsight()
        
        val hasFine = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (hasFine || hasCoarse) {
            fetchWeather()
        } else {
            permissionsLauncher.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    MeshBackground(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // ==================== HEADER (Avatar, Greeting, Theme, AI Magic) ====================
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(accentColor.copy(alpha = 0.3f), Color.Transparent),
                                radius = 70f
                            )
                        )
                        .border(1.dp, accentColor.copy(alpha = 0.5f), CircleShape)
                        .clickable { onNavigateToProfil() },
                    contentAlignment = Alignment.Center
                ) {
                    if (profileImageUri.isNotBlank()) {
                        AsyncImage(
                            model = profileImageUri,
                            contentDescription = "Profile",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                        )
                    } else {
                        Icon(Icons.Default.PersonOutline, null, tint = accentColor, modifier = Modifier.size(28.dp))
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(greeting, color = textSecondary, fontSize = 13.sp)
                    
                    Text(
                        if (userName.isNotBlank()) userName else str("pengguna"),
                        color = textPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    
                    if (weatherInfo != null) {
                        val iconWeather = if (weatherInfo!!.weatherCode <= 3) "☀️" else if (weatherInfo!!.weatherCode <= 69) "🌧️" else "☁️"
                        Text("$iconWeather ${weatherInfo!!.temperature}°C", color = textSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
                
                Box(
                    modifier = Modifier.size(44.dp).clip(CircleShape)
                        .background(if (isDark) Color(0xFF222222) else Color(0xFFE0E0E0))
                        .clickable { coroutineScope.launch { userPreferences.setDarkMode(!isDark) } },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (isDark) Icons.Default.WbSunny else Icons.Default.NightsStay, 
                        contentDescription = "Toggle Theme", 
                        tint = if (isDark) Color(0xFFFFD54F) else Color(0xFF5E35B1), 
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))
                
                // AI Button (Magic Glowing)
                val infiniteTransition = rememberInfiniteTransition()
                val pulseScale by infiniteTransition.animateFloat(
                    initialValue = 0.9f, targetValue = 1.1f,
                    animationSpec = infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse)
                )
                
                val sweepGradient = if (isDark) {
                    Brush.sweepGradient(listOf(accentColor.copy(0.3f), accentColor.copy(0.7f), accentColor.copy(0.3f)))
                } else {
                    Brush.sweepGradient(listOf(accentColor.copy(0.5f), Color(0xFF00BCD4), accentColor.copy(0.5f)))
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(sweepGradient)
                        .padding(2.dp) // Border thickness
                        .clip(CircleShape)
                        .background(surfaceColor)
                        .clickable { onNavigateToAiChat() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AutoAwesome, null, tint = accentColor, modifier = Modifier.size(24.dp))
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ==================== CALENDAR DAYS ====================
            LazyRow(
                state = listState,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(weekDays) { (day, date, isToday) ->
                    Box(
                        modifier = Modifier
                            .width(58.dp)
                            .height(92.dp)
                            .clip(RoundedCornerShape(32.dp))
                            .background(
                                if (isToday) Brush.verticalGradient(listOf(accentColor.copy(alpha = 0.3f), accentColor.copy(alpha=0.1f)))
                                else SolidColor(surfaceColor.copy(alpha=0.6f))
                            )
                            .border(
                                if (isToday) 2.dp else 1.dp,
                                if (isToday) accentColor else MaterialTheme.colorScheme.outline.copy(alpha=0.2f),
                                RoundedCornerShape(32.dp)
                            )
                            .clickable { onNavigateToRiwayat() },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                day, fontSize = 11.sp, fontWeight = FontWeight.Medium,
                                color = if (isToday) accentColor else textSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isToday) accentColor
                                        else if (isDark) Color(0xFF252525) else Color(0xFFE8ECF0)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    date, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp,
                                    color = if (isToday) Color.Black
                                    else if (isDark) Color.White else Color(0xFF001524)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Widget Cuaca telah dihapus dan dipindahkan ke Header (Minimalist View)

            // ==================== STATS ROW ====================
            Text(
                str("total_minggu_ini"), 
                color = textPrimary, 
                fontWeight = FontWeight.ExtraBold, 
                fontSize = 18.sp,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HomeStatCard(Modifier.weight(1f).fillMaxHeight(), Icons.Default.DirectionsRun, str("total_jarak"),
                    String.format(Locale.US, "%.1f", weeklyJarak), "km", Color(0xFF00E676), isDark)
                HomeStatCard(Modifier.weight(1f).fillMaxHeight(), Icons.Default.LocalFireDepartment, str("kalori"),
                    "$weeklyKalori", "kcal", Color(0xFFFF9100), isDark)
                HomeStatCard(Modifier.weight(1f).fillMaxHeight(), Icons.Default.Timer, str("durasi"),
                    "$weeklyDurasi", "min", Color(0xFF00BCD4), isDark)
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ==================== START WORKOUT CARD ====================
            if (isTrackingStarted) {
                val formatTime = String.format(Locale.US, "%02d:%02d:%02d", trackingSeconds / 3600, (trackingSeconds % 3600) / 60, trackingSeconds % 60)
                val distKm = trackingDistance / 1000.0
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .glassCard(shape = RoundedCornerShape(24.dp), neonColor = accentColor)
                        .clickable { onNavigateToTracking(currentJenis) }
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(52.dp).clip(CircleShape)
                                .background(accentColor.copy(0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.DirectionsRun, null, tint = accentColor, modifier = Modifier.size(30.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(str("sedang_berjalan"), color = accentColor, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
                            Text(
                                "$currentJenis • $formatTime • ${String.format(Locale.US, "%.2f km", distKm)}",
                                color = textSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold
                            )
                        }
                        Box(
                            modifier = Modifier.size(44.dp).clip(CircleShape).background(accentColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ArrowForward, null, tint = if (isDark) Color.Black else Color.White, modifier = Modifier.size(24.dp))
                        }
                    }
                }
            } else {
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val buttonScale by animateFloatAsState(
                    targetValue = if (isPressed) 0.95f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .scale(buttonScale)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary))) // Use theme primary/secondary
                        .border(1.dp, Color.White.copy(alpha=0.3f), RoundedCornerShape(24.dp))
                        .clickable(interactionSource = interactionSource, indication = null) { showActivityMenu = true }
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(52.dp).clip(CircleShape)
                                .background(Color.White.copy(0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.DirectionsRun, null, tint = Color.White, modifier = Modifier.size(30.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(str("mulai_aktivitas"), color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
                            Text(
                                str("siap_pecahkan_rekor"),
                                color = Color.White.copy(0.8f), fontSize = 12.sp
                            )
                        }
                        Box(
                            modifier = Modifier.size(44.dp).clip(CircleShape).background(Color.White.copy(0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PlayArrow, null, tint = Color.White, modifier = Modifier.size(26.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ==================== AKTIVITAS HARI INI ====================
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(str("aktivitas_hari_ini"), color = textPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                Text(
                    str("lihat_semua"),
                    color = accentColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onNavigateToRiwayat() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                ActivityCard(
                    modifier = Modifier.weight(1f),
                    title = str("kalori_aktif"),
                    value = "$dailyKalori",
                    unit = "kcal",
                    icon = Icons.Default.LocalFireDepartment,
                    color = Color(0xFFFF9100),
                    progress = if (dailyKalori > 0) (dailyKalori / 500f).coerceAtMost(1f) else 0f,
                    isDark = isDark,
                    onClick = onNavigateToRiwayat
                )
                ActivityCard(
                    modifier = Modifier.weight(1f),
                    title = str("jarak_tempuh"),
                    value = String.format(Locale.US, "%.1f", dailyJarak),
                    unit = "km",
                    icon = Icons.Default.Place,
                    color = Color(0xFF00E676),
                    progress = if (dailyJarak > 0) (dailyJarak / 5.0).toFloat().coerceAtMost(1f) else 0f,
                    isDark = isDark,
                    onClick = onNavigateToRiwayat
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==================== NUTRISI HARI INI ====================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .glassCard(shape = RoundedCornerShape(22.dp), neonColor = Color(0xFF00BCD4))
                    .clickable { onNavigateToRiwayat() }
                    .padding(24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(str("nutrisi_harian"), color = textSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Icon(Icons.Default.ArrowForward, contentDescription = "Detail Nutrisi", tint = textSecondary, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        NutritionBarItem(str("karbo"), "${dailyKarbo}g", Color(0xFF00BCD4), if (dailyKarbo > 0) (dailyKarbo / 300f).coerceAtMost(1f) else 0f, isDark)
                        NutritionBarItem(str("protein"), "${dailyProtein}g", Color(0xFF00E676), if (dailyProtein > 0) (dailyProtein / 100f).coerceAtMost(1f) else 0f, isDark)
                        NutritionBarItem(str("lemak"), "${dailyLemak}g", Color(0xFFFF5252), if (dailyLemak > 0) (dailyLemak / 70f).coerceAtMost(1f) else 0f, isDark)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ==================== SARAN GAYA HIDUP SEHAT (Auto Carousel) ====================
            Text(
                "Saran Gaya Hidup Sehat", 
                color = textPrimary, 
                fontWeight = FontWeight.ExtraBold, 
                fontSize = 18.sp,
                modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 16.dp)
            )
            
            val tips = listOf(
                Triple(Icons.Default.DirectionsRun, "Aktivitas Fisik Rutin", "Lakukan olahraga ringan minimal 30 menit sehari untuk menjaga kebugaran jantung dan otot tubuh agar tidak kaku."),
                Triple(Icons.Default.Bedtime, "Tidur Berkualitas", "Istirahat yang cukup 7-8 jam per malam sangat penting untuk regenerasi sel dan menjaga imunitas tetap kuat."),
                Triple(Icons.Default.RestaurantMenu, "Pola Makan Seimbang", "Perbanyak konsumsi sayur, buah, dan protein. Kurangi makanan tinggi gula atau garam berlebih untuk kesehatan optimal.")
            )
            val colors = listOf(Color(0xFF00E676), Color(0xFF651FFF), Color(0xFFFF9100))
            
            val pagerState = rememberPagerState(pageCount = { tips.size })
            
            // Auto slide logic
            LaunchedEffect(pagerState) {
                while(true) {
                    kotlinx.coroutines.delay(4000)
                    val nextPage = (pagerState.currentPage + 1) % tips.size
                    pagerState.animateScrollToPage(nextPage)
                }
            }
            
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth().height(124.dp),
                contentPadding = PaddingValues(horizontal = 24.dp),
                pageSpacing = 16.dp
            ) { page ->
                LifestyleCard(
                    icon = tips[page].first,
                    title = tips[page].second,
                    desc = tips[page].third,
                    color = colors[page],
                    isDark = isDark
                )
            }
            
            // Pager Indicator
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(tips.size) { index ->
                    val color = if (pagerState.currentPage == index) accentColor else MaterialTheme.colorScheme.outline.copy(alpha=0.3f)
                    Box(
                        modifier = Modifier.padding(2.dp).size(8.dp).clip(CircleShape).background(color)
                    )
                }
            }

            Spacer(modifier = Modifier.height(160.dp))
        }
    }

    // Bottom Sheet Pilihan Olahraga
    if (showActivityMenu) {
        ModalBottomSheet(
            onDismissRequest = { showActivityMenu = false },
            containerColor = surfaceColor
        ) {
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                Text(str("pilih_jenis_aktivitas"), color = textPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(str("pantau_aktivitas"), color = textSecondary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(24.dp))

                ActivityMenuItem(
                    str("jalan_santai"), 
                    Icons.Default.DirectionsWalk, 
                    Color(0xFF00BCD4), 
                    if (language == "en") "Light steps, easy breath" else "Langkah ringan, napas lega"
                ) {
                    showActivityMenu = false
                    onNavigateToTracking("Jalan")
                }
                Spacer(modifier = Modifier.height(16.dp))
                ActivityMenuItem(
                    str("lari_jogging"), 
                    Icons.Default.DirectionsRun, 
                    Color(0xFF00E676), 
                    if (language == "en") "Burn calories, push limits" else "Bakar kalori, pacu adrenalin"
                ) {
                    showActivityMenu = false
                    onNavigateToTracking("Lari")
                }
                Spacer(modifier = Modifier.height(16.dp))
                ActivityMenuItem(
                    str("bersepeda"), 
                    Icons.Default.DirectionsBike, 
                    Color(0xFFFF9100), 
                    if (language == "en") "Healthy ride, explore routes" else "Gowes sehat, jelajahi rute"
                ) {
                    showActivityMenu = false
                    onNavigateToTracking("Sepeda")
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun ActivityMenuItem(title: String, icon: ImageVector, color: Color, desc: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.1f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(color),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = Color.Black, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(desc, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun HomeStatCard(modifier: Modifier, icon: ImageVector, title: String, value: String, unit: String, color: Color, isDark: Boolean) {
    val surfaceColor = if (isDark) Color(0xFF131313) else MaterialTheme.colorScheme.surface
    val textColor = if (isDark) Color.White else Color.Black
    val textSecondaryColor = if (isDark) Color(0xFFA0A0A0) else Color(0xFF4A4A4A)

    Box(
        modifier = modifier
            .background(surfaceColor, RoundedCornerShape(24.dp))
            .border(1.dp, color.copy(0.25f), RoundedCornerShape(24.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(12.dp))
            Column {
                Text(title, color = textSecondaryColor, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(value, color = textColor, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(unit, color = textSecondaryColor, fontSize = 10.sp, modifier = Modifier.padding(bottom = 2.dp))
                }
            }
        }
    }
}

@Composable
fun ActivityCard(
    modifier: Modifier, title: String, value: String, unit: String,
    icon: ImageVector, color: Color, progress: Float, isDark: Boolean, onClick: () -> Unit
) {
    val surfaceColor = if (isDark) Color(0xFF131313) else MaterialTheme.colorScheme.surface
    val textColor = if (isDark) Color.White else Color.Black
    val textSecondaryColor = if (isDark) Color(0xFFA0A0A0) else Color(0xFF4A4A4A)
    val bgColor = if (isDark) Color(0xFF222222) else Color(0xFFE8ECF0)

    Box(
        modifier = modifier
            .height(150.dp)
            .background(surfaceColor, RoundedCornerShape(22.dp))
            .border(1.dp, color.copy(0.2f), RoundedCornerShape(22.dp))
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(color.copy(0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
                }
                Icon(Icons.Default.ArrowOutward, null, tint = textSecondaryColor, modifier = Modifier.size(16.dp))
            }

            Column {
                Text(title, color = textSecondaryColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(value, color = textColor, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(unit, color = textSecondaryColor, fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape).background(bgColor)) {
                    val safeProgress = progress.coerceIn(0.01f, 1f)
                    if (progress > 0f) {
                        Box(modifier = Modifier.fillMaxWidth(safeProgress).height(6.dp).clip(CircleShape).background(color))
                    }
                }
            }
        }
    }
}

@Composable
fun NutritionBarItem(label: String, value: String, color: Color, progress: Float, isDark: Boolean) {
    val bgColor = if (isDark) Color(0xFF222222) else Color(0xFFE8ECF0)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text(value, color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Box(modifier = Modifier.width(42.dp).height(6.dp).clip(RoundedCornerShape(3.dp)).background(bgColor)) {
            val safeProgress = progress.coerceIn(0.01f, 1f)
            if (progress > 0f) {
                Box(modifier = Modifier.fillMaxWidth(safeProgress).height(6.dp).clip(RoundedCornerShape(3.dp)).background(color))
            }
        }
    }
}

@Composable
fun LifestyleCard(icon: ImageVector, title: String, desc: String, color: Color, isDark: Boolean) {
    val surfaceColor = if (isDark) Color(0xFF131313) else MaterialTheme.colorScheme.surface
    val textColor = if (isDark) Color.White else Color.Black
    val textSecondaryColor = if (isDark) Color(0xFFA0A0A0) else Color(0xFF4A4A4A)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(surfaceColor)
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, color = textColor, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(desc, color = textSecondaryColor, fontSize = 12.sp, lineHeight = 16.sp)
            }
        }
    }
}
