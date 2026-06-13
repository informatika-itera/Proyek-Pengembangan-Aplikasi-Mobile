package com.example.sholatyuk.presentation.screens.prayer

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sholatyuk.presentation.screens.qibla.QiblaViewModel
import com.example.sholatyuk.presentation.theme.*
import org.koin.compose.viewmodel.koinViewModel // Koin untuk Compose Multiplatform
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QiblaScreen(
    onNavigateBack: () -> Unit,
    viewModel: QiblaViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Animasi halus untuk jarum kompas
    val animatedNeedle by animateFloatAsState(
        targetValue = uiState.needleAngle,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "needle"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Arah Kiblat", color = TextWhite, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, // Menggunakan AutoMirrored
                            contentDescription = "Kembali",
                            tint = AccentYellow
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepBlue)
            )
        },
        containerColor = DeepBlue
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient( // Qualifier androidx dihapus
                        colors = listOf(DarkTeal, DeepBlue)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = AccentYellow)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Mendeteksi lokasi...", color = TextWhite)
                    }
                }

                uiState.error != null -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text("⚠️", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = uiState.error!!,
                            color = TextWhite,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center // Menggunakan import TextAlign
                        )
                    }
                }

                !uiState.isCompassAvailable -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📵", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Perangkat ini tidak memiliki\nsensor kompas",
                            color = TextWhite,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                else -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    ) {
                        // ── Info kota & jarak ──────────────────────────────
                        if (uiState.cityName.isNotEmpty()) {
                            Text(
                                text = uiState.cityName,
                                color = AccentYellow,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        Text(
                            text = "Jarak ke Ka'bah: ${uiState.distanceKm} km",
                            color = TextWhite.copy(alpha = 0.7f),
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // ── Kompas ─────────────────────────────────────────
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(280.dp)
                        ) {
                            // Lingkaran luar kompas
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawCompassRing()
                                drawCardinalPoints()
                            }

                            // Jarum kompas yang berputar
                            Canvas(modifier = Modifier.size(220.dp)) {
                                rotate(animatedNeedle) {
                                    drawCompassNeedle()
                                }
                            }

                            // Titik tengah
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(Color.White, CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // ── Info sudut ─────────────────────────────────────
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(32.dp)
                        ) {
                            InfoChip(
                                label = "Arah Kiblat",
                                value = "${uiState.qiblaAngle.toInt()}°"
                            )
                            InfoChip(
                                label = "Kompas",
                                value = "${uiState.azimuth.toInt()}°"
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // ── Petunjuk ───────────────────────────────────────
                        Text(
                            text = "Putar HP hingga jarum menunjuk ke atas ↑",
                            color = TextWhite.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

// ── Canvas helpers ────────────────────────────────────────────────────────────

private fun DrawScope.drawCompassRing() {
    // Lingkaran luar
    drawCircle(
        color = Color(0xFF1E3A5F),
        radius = size.minDimension / 2f
    )
    // Border kuning
    drawCircle(
        color = Color(0xFFFFD700),
        radius = size.minDimension / 2f,
        style = Stroke(width = 3.dp.toPx())
    )
    // Lingkaran dalam (dekorasi)
    drawCircle(
        color = Color(0xFF0D2137),
        radius = size.minDimension / 2f - 12.dp.toPx()
    )

    // Garis-garis penanda derajat
    val center = Offset(size.width / 2, size.height / 2)
    val outerR = size.minDimension / 2f - 4.dp.toPx()
    for (i in 0 until 72) {
        val angle = (i * 5.0) * (PI / 180.0) // Konversi ke radian menggunakan PI
        val isMajor = i % 9 == 0  // setiap 45°
        val innerR = if (isMajor) outerR - 14.dp.toPx() else outerR - 8.dp.toPx()
        val strokeW = if (isMajor) 2.dp.toPx() else 1.dp.toPx()
        drawLine(
            color = if (isMajor) Color(0xFFFFD700) else Color.White.copy(alpha = 0.4f),
            start = Offset(
                center.x + outerR * sin(angle).toFloat(),
                center.y - outerR * cos(angle).toFloat()
            ),
            end = Offset(
                center.x + innerR * sin(angle).toFloat(),
                center.y - innerR * cos(angle).toFloat()
            ),
            strokeWidth = strokeW
        )
    }
}

private fun DrawScope.drawCardinalPoints() {
    // Label N, S, E, W digambar di luar lingkaran — cukup tandai dengan titik besar
    val center = Offset(size.width / 2, size.height / 2)
    val r = size.minDimension / 2f - 22.dp.toPx()
    val dirs = listOf(0.0, 90.0, 180.0, 270.0)
    dirs.forEach { deg ->
        val angle = deg * (PI / 180.0) // Konversi ke radian menggunakan PI
        drawCircle(
            color = if (deg == 0.0) Color.Red else Color(0xFFFFD700),
            radius = 5.dp.toPx(),
            center = Offset(
                center.x + r * sin(angle).toFloat(),
                center.y - r * cos(angle).toFloat()
            )
        )
    }
}

private fun DrawScope.drawCompassNeedle() {
    val center = Offset(size.width / 2, size.height / 2)
    val needleLength = size.minDimension / 2f - 20.dp.toPx()
    val needleWidth  = 8.dp.toPx()

    // ── Ujung atas: merah (menunjuk kiblat) ──
    val path = Path().apply {
        moveTo(center.x, center.y - needleLength)          // puncak
        lineTo(center.x - needleWidth, center.y)           // kiri tengah
        lineTo(center.x + needleWidth, center.y)           // kanan tengah
        close()
    }
    drawPath(path, color = Color(0xFFE53935))              // merah

    // ── Ujung bawah: putih ──
    val pathDown = Path().apply {
        moveTo(center.x, center.y + needleLength * 0.7f)  // bawah
        lineTo(center.x - needleWidth, center.y)
        lineTo(center.x + needleWidth, center.y)
        close()
    }
    drawPath(pathDown, color = Color.White.copy(alpha = 0.85f))

    // Border tipis jarum
    drawPath(path,     color = Color.Black.copy(alpha = 0.3f), style = Stroke(1.dp.toPx()))
    drawPath(pathDown, color = Color.Black.copy(alpha = 0.3f), style = Stroke(1.dp.toPx()))

    // Ikon Ka'bah kecil di ujung jarum (lingkaran hitam)
    drawCircle(
        color = Color(0xFF1A1A1A),
        radius = 10.dp.toPx(),
        center = Offset(center.x, center.y - needleLength)
    )
    drawCircle(
        color = Color(0xFFFFD700),
        radius = 10.dp.toPx(),
        center = Offset(center.x, center.y - needleLength),
        style = Stroke(1.5.dp.toPx())
    )
}

// ── Komponen kecil ─────────────────────────────────────────────────────────────

@Composable
private fun InfoChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = AccentYellow,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = TextWhite.copy(alpha = 0.6f),
            fontSize = 11.sp
        )
    }
}