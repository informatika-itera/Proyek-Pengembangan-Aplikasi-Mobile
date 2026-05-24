package com.example.rosea

import androidx.compose.runtime.Composable
import com.example.rosea.presentation.screens.main.MainScreen // 👈 Import rumah baru kita
import com.example.rosea.presentation.theme.RoseaTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    // 1. Ambil instance SyncManager secara global menggunakan Koin
    val syncManager = org.koin.compose.koinInject<com.example.rosea.domain.usecase.OrderSyncManager>()

    // 2. Nyalakan mesin pemantau di latar belakang (hanya dipanggil sekali)
    androidx.compose.runtime.LaunchedEffect(Unit) {
        syncManager.startObserving()
    }

    RoseaTheme {
        MainScreen()
    }
}