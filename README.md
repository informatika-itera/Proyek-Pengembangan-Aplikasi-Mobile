# FoodSaver - Kotlin Multiplatform

FoodSaver adalah aplikasi pengelola stok makanan pintar yang membantu mengurangi pemborosan makanan (food waste) dengan pengingat kedaluwarsa dan rekomendasi resep berbasis AI.

## 🚀 Sprint 4 Summary (Stabilization & Testing)
Pada Sprint 4, fokus utama adalah stabilitas aplikasi, pengujian menyeluruh, dan pemolesan UI.

### Key Improvements:
- **Bug Fixes**: Navigasi antar halaman kini lebih stabil dengan penanganan rute yang lengkap.
- **UI Polish**: Konsistensi desain Card, Typography, dan Spacing di seluruh layar.
- **Quantity Formatter**: Perbaikan tampilan angka (misal `12.0` menjadi `12`).
- **Edge Cases**: Penanganan kondisi list kosong, gagal load data, dan proses loading AI yang lebih halus.
- **Testing**: Implementasi 15+ Unit Tests dan 3 Critical User Journey UI Tests.

## 🛠 Features
- **Home Dashboard**: Ringkasan stok dan pengingat urgent.
- **Food Inventory**: Kelola stok dengan kategori dan lokasi penyimpanan.
- **Expiry Alert**: Notifikasi visual untuk makanan yang mendekati kedaluwarsa.
- **AI Cook from Stock**: Rekomendasi resep cerdas berdasarkan bahan yang tersedia (Rule-based & AI-ready).
- **Food Calendar**: Jadwal estimasi kesegaran makanan dalam tampilan kalender.
- **Dark & Light Mode**: Dukungan penuh tema gelap dan terang.

## 🧪 Testing Instructions

### Menjalankan Unit Tests
Untuk memastikan logika bisnis berjalan dengan benar:
```powershell
.\gradlew test
```
Hasil test dapat dilihat di: `composeApp/build/reports/tests/test/index.html`

### Menjalankan UI Tests
UI Tests memerlukan emulator atau device Android yang terhubung:
```powershell
.\gradlew connectedDebugAndroidTest
```
Atau jalankan `CriticalUiTests.kt` langsung dari Android Studio.

### Menjalankan Build & Compile Metadata
```powershell
.\gradlew clean build
.\gradlew :composeApp:compileCommonMainKotlinMetadata
```

## 📱 Cara Menjalankan Aplikasi
1. Clone repository ini.
2. Buka di Android Studio (Ladybug atau versi terbaru).
3. Pastikan `local.properties` berisi `GEMINI_API_KEY` (opsional untuk fitur AI).
4. Run konfigurasi `composeApp` pada emulator atau device Android.

## 📝 Catatan Penting
- **Recipe Feature**: Menggunakan kombinasi Rule-based engine untuk kecepatan dan AI untuk variasi.
- **Stability**: Gunakan tombol "Reset" pada halaman Resep jika ingin membersihkan input manual dengan cepat.
- **Dark Mode**: Tema akan mengikuti pengaturan sistem secara otomatis.

---
Dikembangkan oleh: [Nama Anda]
```
