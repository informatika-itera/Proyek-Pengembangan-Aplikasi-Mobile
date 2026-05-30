package com.example.travelplanner

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import com.example.travelplanner.core.util.NetworkMonitor
import com.example.travelplanner.core.util.VoiceInputManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import okio.Path.Companion.toOkioPath

/**
 * Android MainActivity
 *
 * Entry point untuk Android app, lengkap dengan integrasi Speech Recognition asli.
 * Juga mengkonfigurasi Coil ImageLoader dengan OkHttp engine yang stabil.
 */
class MainActivity : ComponentActivity(), SingletonImageLoader.Factory {

    private lateinit var networkMonitor: NetworkMonitor

    // Register ActivityResultLauncher for Speech-to-Text overlay intent
    private val speechRecognizerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = results?.firstOrNull() ?: ""
            if (spokenText.isNotBlank()) {
                VoiceInputManager.onVoiceInputResult?.invoke(spokenText)
            } else {
                VoiceInputManager.onVoiceInputResult?.invoke("Error: Teks tidak terdeteksi")
            }
        } else {
            VoiceInputManager.onVoiceInputResult?.invoke("Error: Perekaman suara dibatalkan")
        }
    }

    /**
     * Configure Coil's singleton ImageLoader with OkHttp engine.
     * This ensures reliable HTTPS image loading with proper redirect following.
     */
    override fun newImageLoader(context: PlatformContext): ImageLoader {
        val coilHttpClient = HttpClient(OkHttp) {
            install(HttpTimeout) {
                requestTimeoutMillis = 30_000
                connectTimeoutMillis = 15_000
            }
            engine {
                config {
                    followRedirects(true)
                    followSslRedirects(true)
                    retryOnConnectionFailure(true)
                }
            }
        }

        return ImageLoader.Builder(context)
            .components {
                add(KtorNetworkFetcherFactory(httpClient = { coilHttpClient }))
            }
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(applicationContext.cacheDir.resolve("coil_cache").toOkioPath())
                    .maxSizeBytes(50L * 1024 * 1024) // 50 MB disk cache
                    .build()
            }
            .crossfade(true)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        networkMonitor = NetworkMonitor(applicationContext)

        // Wire shared VoiceInputManager to Android native implementation
        VoiceInputManager.voiceInputLauncher = {
            try {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "id-ID")
                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Silakan bicara untuk mencatat pengeluaran...")
                }
                speechRecognizerLauncher.launch(intent)
            } catch (e: Exception) {
                VoiceInputManager.onVoiceInputResult?.invoke("Error: Layanan suara tidak tersedia (${e.message})")
            }
        }

        setContent {
            App(networkMonitor = networkMonitor)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (VoiceInputManager.voiceInputLauncher != null) {
            VoiceInputManager.voiceInputLauncher = null
        }
    }
}
