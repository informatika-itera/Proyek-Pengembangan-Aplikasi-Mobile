package com.example.gamenews

import androidx.compose.ui.window.ComposeUIViewController
import com.example.gamenews.core.di.initKoinIOS

/**
 * iOS Main View Controller Factory
 * 
 * Digunakan oleh Swift/iOS untuk membuat UIViewController
 * yang menampilkan Compose UI.
 * 
 * Cara penggunaan di Swift:
 * ```swift
 * import ComposeApp
 * 
 * struct ContentView: UIViewControllerRepresentable {
 *     func makeUIViewController(context: Context) -> UIViewController {
 *         return MainViewControllerKt.MainViewController()
 *     }
 *     
 *     func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
 * }
 * ```
 */
fun MainViewController() = ComposeUIViewController(
    configure = {
        // Initialize Koin for iOS
        initKoinIOS()
    }
) {
    App()
}
