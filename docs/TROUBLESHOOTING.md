# 🔧 Troubleshooting Guide

Panduan untuk mengatasi masalah umum yang mungkin ditemui saat mengerjakan project.

---

## 🚨 Masalah Umum & Solusi

### 1. Memori Penuh (Installation Failed)
**Gejala:** `INSTALL_FAILED_INSUFFICIENT_STORAGE`
**Solusi:**
1. Hapus aplikasi Rosea yang lama di HP/Emulator.
2. Jika pakai Emulator: Buka **Device Manager** > Klik titik tiga di emulator > **Wipe Data**.
3. Jalankan perintah `./gradlew clean` di terminal.

### 2. UI Element (Tombol) Hilang atau Terpotong
**Gejala:** Tombol di bawah tidak muncul atau tertutup keyboard.
**Solusi:**
1. **Gunakan Scaffold Bottom Bar**: Letakkan tombol aksi utama di slot `bottomBar` pada `Scaffold`.
2. **Aktifkan Scroll**: Tambahkan `Modifier.verticalScroll(rememberScrollState())` pada Column utama.
3. **Handle Window Insets**: Tambahkan `Modifier.imePadding()` agar komponen naik saat keyboard muncul.

---

## 🔍 Debugging Tips

### 1. Debug StateFlow
Gunakan `onEach` untuk memantau data yang mengalir (gunakan parameter default agar tidak error):
```kotlin
val uiState = someFlow
    .onEach { data -> println("DEBUG: $data") }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "Memuat..."
    )
```

### 2. Cek Recomposisi
Tambahkan log sederhana untuk melihat kapan UI digambar ulang:
```kotlin
@Composable
fun myComponent() {
    androidx.compose.runtime.SideEffect { 
        println("Recomposing...") 
    }
}
```

---
*Dokumen ini adalah bagian dari template project Pengembangan Aplikasi Mobile - ITERA*
