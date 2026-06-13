package com.example.fitgen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.gif.AnimatedImageDecoder
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade

/**
 * Android MainActivity
 * 
 * Entry point untuk Android app.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge display
        enableEdgeToEdge()
        
        setContent {
            setSingletonImageLoaderFactory { context ->
                ImageLoader.Builder(context)
                    .components {
                        add(KtorNetworkFetcherFactory())
                        add(AnimatedImageDecoder.Factory()) // Decoder khusus GIF untuk Android
                    }
                    .crossfade(true)
                    .build()
            }
            App()
        }
    }
}
