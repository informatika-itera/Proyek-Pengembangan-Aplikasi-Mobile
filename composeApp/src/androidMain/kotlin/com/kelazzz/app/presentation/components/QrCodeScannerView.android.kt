package com.kelazzz.app.presentation.components

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

/**
 * Implementasi actual QrCodeScannerView untuk platform Android.
 * Ditambah fitur Pinch-to-Zoom (mencubit layar) dan Tombol Quick Zoom (1x, 2x, 4x)
 * agar mahasiswa bisa memindai QR Code proyektor yang berada jauh di depan kelas dengan sangat mudah.
 */
@Composable
actual fun QrCodeScannerView(
    onQrCodeScanned: (String) -> Unit,
    modifier: Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember { PreviewView(context) }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val analyzerExecutor = remember { Executors.newSingleThreadExecutor() }
    val scanner = remember {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        BarcodeScanning.getClient(options)
    }

    // Status untuk memastikan kita hanya men-trigger callback sekali (menghindari duplikasi submit)
    var isScanned by remember { mutableStateOf(false) }
    var isDisposed by remember { mutableStateOf(false) }

    // State untuk kontrol zoom kamera
    var cameraControl by remember { mutableStateOf<androidx.camera.core.CameraControl?>(null) }
    var cameraInfo by remember { mutableStateOf<androidx.camera.core.CameraInfo?>(null) }
    var currentZoomRatio by remember { mutableStateOf(1f) }

    // Efek untuk binding lifecycle CameraX
    LaunchedEffect(cameraProviderFuture) {
        cameraProviderFuture.addListener({
            if (isDisposed) return@addListener

            try {
                val cameraProvider = cameraProviderFuture.get()

                // 1. Setup Preview UseCase
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                // 2. Setup Image Analysis UseCase
                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalysis.setAnalyzer(analyzerExecutor) { imageProxy ->
                    val mediaImage = imageProxy.image
                    if (mediaImage != null && !isScanned && !isDisposed) {
                        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                        scanner.process(image)
                            .addOnSuccessListener { barcodes ->
                                if (barcodes.isNotEmpty() && !isScanned && !isDisposed) {
                                    val barcodeValue = barcodes[0].rawValue ?: barcodes[0].displayValue
                                    if (barcodeValue != null) {
                                        isScanned = true
                                        onQrCodeScanned(barcodeValue)
                                    }
                                }
                            }
                            .addOnCompleteListener {
                                imageProxy.close()
                            }
                    } else {
                        imageProxy.close()
                    }
                }

                // 3. Pilih Kamera Belakang
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                // Unbind semua sebelum bind ulang
                cameraProvider.unbindAll()

                // Bind kamera ke lifecycle owner
                val camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )

                // Simpan reference camera control untuk manipulasi zoom
                cameraControl = camera.cameraControl
                cameraInfo = camera.cameraInfo
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(context))
    }

    // Unbind camera provider saat Composable dilepas dari komposisi (menjaga resource leak)
    DisposableEffect(lifecycleOwner) {
        onDispose {
            isDisposed = true
            scanner.close()
            analyzerExecutor.shutdown()
            try {
                if (cameraProviderFuture.isDone) {
                    cameraProviderFuture.get().unbindAll()
                }
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    // Tampilan Overlay Pemindai Premium
    Box(
        modifier = modifier
            // A. Menambahkan Fitur Gesture Pinch-to-Zoom
            .pointerInput(Unit) {
                detectTransformGestures { _, _, zoom, _ ->
                    cameraInfo?.zoomState?.value?.let { zoomState ->
                        val minZoom = zoomState.minZoomRatio
                        val maxZoom = zoomState.maxZoomRatio
                        // Hitung rasio zoom baru secara proporsional dan batasi sesuai kemampuan hardware kamera
                        val newZoom = (currentZoomRatio * zoom).coerceIn(minZoom, maxZoom)
                        cameraControl?.setZoomRatio(newZoom)
                        currentZoomRatio = newZoom
                    }
                }
            }
    ) {
        // Preview Kamera Native
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay Semitransparan Gelap dengan lubang (cutout) di tengah & Sudut Scanner Hijau
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Ukuran area scan: 70% dari dimensi terkecil agar proporsional
            val scanSize = width.coerceAtMost(height) * 0.7f
            val left = (width - scanSize) / 2f
            val top = (height - scanSize) / 2f
            val right = left + scanSize
            val bottom = top + scanSize

            // 1. Gambar area luar semi-transparan hitam (efek bayangan fokus)
            // Bagian atas
            drawRect(
                color = Color.Black.copy(alpha = 0.6f),
                topLeft = Offset(0f, 0f),
                size = Size(width, top)
            )
            // Bagian kiri
            drawRect(
                color = Color.Black.copy(alpha = 0.6f),
                topLeft = Offset(0f, top),
                size = Size(left, scanSize)
            )
            // Bagian kanan
            drawRect(
                color = Color.Black.copy(alpha = 0.6f),
                topLeft = Offset(right, top),
                size = Size(width - right, scanSize)
            )
            // Bagian bawah
            drawRect(
                color = Color.Black.copy(alpha = 0.6f),
                topLeft = Offset(0f, bottom),
                size = Size(width, height - bottom)
            )

            // 2. Gambar sudut pemindai (corners) berwarna hijau neon modern
            val cornerLength = 40f
            val strokeWidth = 8f
            val cornerColor = Color(0xFF4CAF50)

            // Kiri Atas
            drawLine(cornerColor, Offset(left, top), Offset(left + cornerLength, top), strokeWidth)
            drawLine(cornerColor, Offset(left, top), Offset(left, top + cornerLength), strokeWidth)

            // Kanan Atas
            drawLine(cornerColor, Offset(right, top), Offset(right - cornerLength, top), strokeWidth)
            drawLine(cornerColor, Offset(right, top), Offset(right, top + cornerLength), strokeWidth)

            // Kiri Bawah
            drawLine(cornerColor, Offset(left, bottom), Offset(left + cornerLength, bottom), strokeWidth)
            drawLine(cornerColor, Offset(left, bottom), Offset(left, bottom - cornerLength), strokeWidth)

            // Kanan Bawah
            drawLine(cornerColor, Offset(right, bottom), Offset(right - cornerLength, bottom), strokeWidth)
            drawLine(cornerColor, Offset(right, bottom), Offset(right, bottom - cornerLength), strokeWidth)
        }

        // 3. Garis Laser Pemindai Bergerak (Pulsing Neon Scanner Line)
        val infiniteTransition = rememberInfiniteTransition()
        val animatedOffsetY by infiniteTransition.animateFloat(
            initialValue = 0.05f,
            targetValue = 0.95f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val scanSize = width.coerceAtMost(height) * 0.7f
            val left = (width - scanSize) / 2f
            val top = (height - scanSize) / 2f

            val lineY = top + (scanSize * animatedOffsetY)

            // Gambar garis laser hijau dengan gradien memudar ke atas/bawah
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x004CAF50),
                        Color(0xFF4CAF50),
                        Color(0x004CAF50)
                    )
                ),
                topLeft = Offset(left + 15f, lineY - 4f),
                size = Size(scanSize - 30f, 8f)
            )
        }

        // B. Tombol Quick Zoom Pintar (1x, 2x, 4x)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val zoomLevels = listOf(1f, 2f, 4f)
            zoomLevels.forEach { zoom ->
                // Bandingkan dengan toleransi float kecil untuk UI seleksi
                val isSelected = kotlin.math.abs(currentZoomRatio - zoom) < 0.1f
                val labelText = "${zoom.toInt()}x"

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color(0xFF4CAF50) else Color.Transparent)
                        .clickable {
                            cameraInfo?.zoomState?.value?.let { zoomState ->
                                val supportedZoom = zoom.coerceIn(
                                    zoomState.minZoomRatio,
                                    zoomState.maxZoomRatio
                                )
                                cameraControl?.setZoomRatio(supportedZoom)
                                currentZoomRatio = supportedZoom
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = labelText,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}
