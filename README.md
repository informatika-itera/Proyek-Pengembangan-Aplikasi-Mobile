# KelazZz - Aplikasi Presensi dan Agenda Akademik ITERA

<p align="center">
  <img src="https://github.com/user-attachments/assets/082ccee7-c661-46bf-9a91-a7ab386d9410" alt="KelazZz" width="500">
</p>

KelazZz adalah aplikasi Android berbasis Kotlin Multiplatform dan Compose Multiplatform untuk membantu mahasiswa ITERA melakukan presensi, memantau rekap kehadiran, mengelola jadwal akademik pribadi, dan menggunakan asisten akademik berbasis AI.

Project ini dibuat untuk mata kuliah Pengembangan Aplikasi Mobile. Target aktif saat ini adalah Android. Struktur Kotlin Multiplatform tetap dipakai agar sebagian besar logic berada di `commonMain`, tetapi target iOS belum diaktifkan.

> Disclaimer: aplikasi ini dibuat untuk kebutuhan akademik dan pembelajaran. KelazZz tetap menggunakan autentikasi resmi pengguna Pocket ITERA dan tidak melakukan bypass keamanan, modifikasi sistem kampus, atau akses ke akun pengguna lain.

## Video Demo

Video demo aplikasi: [https://youtu.be/GpRTtH88aHk](https://youtu.be/GpRTtH88aHk)

## Tim Pengembang

| Nama | NIM | GitHub | Role |
|------|-----|--------|------|
| Muhammad Bintang Al Fasya | 123140098 | [@MuhammadBintangAl-Fasya](https://github.com/MuhammadBintangAl-Fasya) | Mobile Developer |
| Rifael Eurico Sitorus | 123140077 | [@eltoruz](https://github.com/eltoruz) | Mobile Developer |

**Mata Kuliah:** Pengembangan Aplikasi Mobile (IF25-22017)
**Program Studi:** Teknik Informatika - Institut Teknologi Sumatera
**Tahun Akademik:** Genap 2025/2026

## Status Project

Project berada pada tahap final untuk scope akademik saat ini. Fitur utama sudah tersedia, test dan coverage sudah dikonfigurasi, CI siap digunakan, dan APK debug berhasil dibuat.

Hasil verifikasi terakhir:

| Pemeriksaan | Perintah | Status |
|-------------|----------|--------|
| Unit test lokal | `./gradlew test` | Lulus, 31/31 test |
| Compose UI test | `./gradlew :composeApp:compileDebugAndroidTestSources` | Lulus compile, 3 UI test tersedia |
| Coverage gate | `./gradlew koverVerify` | Lulus minimum 50% |
| Coverage report | `./gradlew koverHtmlReport` | Report berhasil dibuat, line coverage 65.3% |
| Debug APK | `./gradlew assembleDebug` | Lulus |

Catatan: Compose UI test berada di `androidInstrumentedTest`. Untuk menjalankannya penuh, gunakan emulator atau perangkat Android dengan `./gradlew :composeApp:connectedDebugAndroidTest`.

## Fitur Utama

### Autentikasi dan Sesi

- Login hanya untuk akun mahasiswa Pocket ITERA dengan email `@student.itera.ac.id`.
- Token sesi disimpan menggunakan DataStore Preferences.
- Auto-login selama token masih tersedia.
- Logout dan penghapusan sesi dari halaman profil.

### Presensi QR dan Token Manual

- QR scanner berbasis CameraX dan ML Kit Barcode Scanner.
- Input token manual sebagai fallback ketika scan QR bawaan Pocket ITERA bermasalah.
- Validasi token sebelum request presensi dikirim.
- Pencegahan submit berulang saat token yang sama sedang diproses.

Alur umum:

```text
Scan QR atau input token -> validasi token -> submit ke API Pocket ITERA -> tampilkan hasil
```

### Rekap Presensi dan Offline Cache

- Menampilkan daftar mata kuliah dan riwayat presensi.
- Data presensi dari Pocket ITERA disimpan ke SQLDelight.
- Rekap tetap bisa dibaca dari cache ketika koneksi tidak tersedia.
- Mapping data remote ke domain sudah dilindungi dari data kosong atau format tanggal yang tidak valid.

### Early Warning Kehadiran

- Home menghitung ringkasan kehadiran dari cache lokal.
- Mata kuliah dengan alpha lebih dari satu ditampilkan sebagai peringatan.
- Tidak memakai request API tambahan dan tidak bergantung pada AI.

Contoh output:

```text
Kalkulus Lanjut - 78.0% - Alpha 2
```

### Jadwal Akademik Pribadi

- CRUD jadwal offline: tambah, lihat detail, edit, dan hapus.
- Data tersimpan di SQLDelight melalui tabel `JadwalEntity`.
- Mendukung pengingat lokal untuk jadwal yang dibuat pengguna.
- Validasi input jadwal mencegah data kosong atau waktu yang tidak valid.

### Pengingat Lokal

- Pengingat jadwal dibuat secara lokal di Android.
- Implementasi Android memakai `AlarmManager` dan `BroadcastReceiver`.
- Project ini belum memakai Firebase Cloud Messaging. Notifikasi yang tersedia saat ini adalah local schedule reminder, bukan push notification dari server.

### AI Asisten Akademik

- Chatbot akademik menggunakan OpenCode Go API.
- API key dibaca dari `local.properties` melalui `OPENCODE_API_KEY`.
- Chatbot dapat memakai data akademik lokal seperti jadwal dan ringkasan presensi sebagai konteks.

### Tema

- Mendukung light mode, dark mode, dan system theme.
- Preferensi tema disimpan di DataStore.
- UI memakai Compose Material 3.

### CI/CD

- GitHub Actions menjalankan test, coverage verification, coverage report, dan build debug APK.
- Artifact test report, coverage report, dan APK debug diunggah dari workflow CI.

## Tech Stack

| Area | Teknologi |
|------|-----------|
| Bahasa | Kotlin |
| Framework | Kotlin Multiplatform, Android target aktif |
| UI | Compose Multiplatform, Material 3 |
| Arsitektur | Clean Architecture + MVVM |
| Async | Coroutines, Flow, StateFlow |
| Networking | Ktor Client, OkHttp Android engine, Kotlinx Serialization |
| Database lokal | SQLDelight |
| Preferences | DataStore Preferences |
| Dependency Injection | Koin |
| QR Scanner | CameraX, ML Kit Barcode Scanner |
| AI | OpenCode Go API |
| Pengingat | Android AlarmManager + BroadcastReceiver |
| Testing | kotlin.test, kotlinx-coroutines-test, Turbine, SQLDelight SQLite driver, Compose UI Test |
| Coverage | Kover |
| CI/CD | GitHub Actions |

## Arsitektur

KelazZz memakai Clean Architecture dengan pemisahan presentation, domain, dan data.

```text
Presentation
Screens, ViewModels, UI state, navigation
      |
Domain
Models, repository interfaces, use cases
      |
Data
Remote API, local database, preferences, repository implementations
```

Use case utama yang digunakan:

- `LoginUseCase`
- `GetPresensiUseCase`
- `SubmitPresensiUseCase`
- `AnalyzeAttendanceUseCase`
- `GetJadwalUseCase`
- `SaveJadwalUseCase`
- `DeleteJadwalUseCase`
- `UpdateJadwalUseCase`
- `GetAiResponseUseCase`

## Screen yang Tersedia

| Screen | Fungsi |
|--------|--------|
| Login | Autentikasi mahasiswa Pocket ITERA |
| Home | Dashboard ringkas, early warning, agenda terdekat |
| Rekap Presensi | Daftar dan detail rekap kehadiran |
| Presensi | QR scanner dan input token manual |
| Jadwal | Daftar jadwal akademik pribadi |
| Tambah/Edit Jadwal | Form CRUD jadwal |
| Detail Jadwal | Detail, edit, hapus, dan pengingat jadwal |
| AI Asisten | Chatbot akademik berbasis OpenCode Go API |
| Profile | Data pengguna, pilihan tema, dan logout |

Catatan: route `Notifikasi` belum menjadi screen aktif di NavHost. Pengingat jadwal tetap berjalan melalui mekanisme notifikasi lokal Android.

## Screenshot Aplikasi

| Login | Home | Rekap Presensi |
|-------|------|----------------|
| <img src="https://github.com/user-attachments/assets/d84eeb89-ecc8-4058-adbd-72ee23a0241c" alt="Login" width="220"> | <img src="https://github.com/user-attachments/assets/4eab9f24-50d0-41e8-8535-754a165554e2" alt="Home" width="220"> | <img src="https://github.com/user-attachments/assets/2c48acbf-a726-481e-b465-021f521992d0" alt="Rekap Presensi" width="220"> |

| Presensi QR | Presensi Manual | Jadwal |
|-------------|-----------------|--------|
| <img src="https://github.com/user-attachments/assets/053dee52-3a5d-4c33-a6f6-71a26955cf58" alt="Presensi QR" width="220"> | <img src="https://github.com/user-attachments/assets/3839298d-3cd3-4c6e-8070-1c2568d661bc" alt="Presensi Manual" width="220"> | <img src="https://github.com/user-attachments/assets/e54cf9d4-a9ff-4000-84ba-5a5ff744264a" alt="Jadwal" width="220"> |

| Tambah/Edit Jadwal | Detail Jadwal | AI Asisten |
|--------------------|---------------|------------|
| <img src="https://github.com/user-attachments/assets/c409b870-f338-4ccf-b0f7-3a2fb3033023" alt="Tambah/Edit Jadwal" width="220"> | <img src="https://github.com/user-attachments/assets/57aaee4a-238c-4224-a9d8-5ed74c7593a0" alt="Detail Jadwal" width="220"> | <img src="https://github.com/user-attachments/assets/9a3e50c1-6da9-489a-956d-6e7dd56ccd47" alt="AI Asisten" width="220"> |

| Profil |
|--------|
| <img src="https://github.com/user-attachments/assets/4228d076-325a-4f9d-920f-bdf901545424" alt="Profil" width="220"> |

## Struktur Proyek

```text
.
|-- .github/workflows/
|   `-- ci.yml
|-- composeApp/
|   |-- build.gradle.kts
|   `-- src/
|       |-- commonMain/
|       |   |-- kotlin/com/kelazzz/app/
|       |   |   |-- App.kt
|       |   |   |-- core/
|       |   |   |   |-- network/
|       |   |   |   |-- notification/
|       |   |   |   `-- util/
|       |   |   |-- data/
|       |   |   |   |-- local/datastore/
|       |   |   |   |-- remote/ai/
|       |   |   |   |-- remote/pocket/
|       |   |   |   `-- repository/
|       |   |   |-- di/
|       |   |   |-- domain/
|       |   |   |   |-- model/
|       |   |   |   |-- repository/
|       |   |   |   `-- usecase/
|       |   |   `-- presentation/
|       |   |       |-- components/
|       |   |       |-- navigation/
|       |   |       |-- screens/
|       |   |       |   |-- ai/
|       |   |       |   |-- home/
|       |   |       |   |-- jadwal/
|       |   |       |   |-- kalender/
|       |   |       |   |-- login/
|       |   |       |   |-- presensi/
|       |   |       |   |-- profile/
|       |   |       |   `-- rekap/
|       |   |       `-- theme/
|       |   `-- sqldelight/com/kelazzz/app/data/local/
|       |       |-- Presensi.sq
|       |       `-- Jadwal.sq
|       |-- androidMain/kotlin/com/kelazzz/app/
|       |   |-- MainActivity.kt
|       |   |-- KelazZzApplication.kt
|       |   |-- core/notification/
|       |   |-- core/network/
|       |   |-- core/util/
|       |   |-- core/di/
|       |   `-- presentation/components/
|       |-- commonTest/
|       |-- androidUnitTest/
|       `-- androidInstrumentedTest/
|-- docs/
|   `-- CARA_MENJALANKAN.md
|-- gradle/
|-- local.properties.example
`-- README.md
```

## Database Lokal

SQLDelight digunakan untuk penyimpanan data lokal.

| Tabel | Kegunaan |
|-------|----------|
| `PresensiEntity` | Cache rekap presensi dari API Pocket ITERA |
| `KelasEntity` | Data kelas atau mata kuliah yang dipakai dalam rekap |
| `JadwalEntity` | Jadwal dan pengingat akademik pribadi |

## Cara Menjalankan

### Prasyarat

- Android Studio Ladybug atau lebih baru.
- JDK 17.
- Android SDK dengan compile SDK 35 dan minimum SDK 24.
- Koneksi internet untuk login Pocket ITERA dan AI assistant.
- API key OpenCode Go jika ingin memakai fitur AI.

### Setup `local.properties`

Salin contoh konfigurasi:

```bash
cp local.properties.example local.properties
```

Isi nilai berikut sesuai environment lokal:

```properties
sdk.dir=/path/to/android/sdk
OPENCODE_API_KEY=your_api_key_here
```

Di CI, `local.properties` dibuat otomatis oleh GitHub Actions dengan placeholder API key.

### Build APK Debug

```bash
./gradlew :composeApp:assembleDebug
```

APK debug akan tersedia di:

```text
composeApp/build/outputs/apk/debug/
```

### Install ke Perangkat Android

```bash
./gradlew :composeApp:installDebug
```

Atau jalankan langsung dari Android Studio dengan konfigurasi `composeApp`.

## Testing dan Coverage

Test otomatis mencakup domain model, use case, ViewModel, repository SQLDelight, dan komponen Compose dasar.

Jalankan unit test lokal:

```bash
./gradlew test
```

Compile source instrumented UI test:

```bash
./gradlew :composeApp:compileDebugAndroidTestSources
```

Jalankan UI test di emulator atau perangkat:

```bash
./gradlew :composeApp:connectedDebugAndroidTest
```

Validasi coverage minimum:

```bash
./gradlew koverVerify
```

Buat laporan coverage HTML:

```bash
./gradlew koverHtmlReport
```

Lokasi report:

| Report | Lokasi |
|--------|--------|
| Unit test debug | `composeApp/build/reports/tests/testDebugUnitTest/index.html` |
| Unit test release | `composeApp/build/reports/tests/testReleaseUnitTest/index.html` |
| Kover HTML | `composeApp/build/reports/kover/html/index.html` |

Kover mengecualikan generated code, Android entry point, wiring DI, API remote langsung, dan Composable rendering murni agar coverage fokus pada business logic yang realistis diuji otomatis. Coverage terakhir yang berhasil dibuat: line 65.3%, instruction 58.7%, branch 35.2%, class 70.8%, method 55.3%.

## CI/CD

Workflow berada di `.github/workflows/ci.yml`.

CI berjalan pada push ke `main`, `develop`, dan `project/**`, serta pull request ke `main` atau `develop`.

Tahapan CI:

1. Checkout repository.
2. Setup JDK 17.
3. Setup Gradle.
4. Generate `local.properties` untuk CI.
5. Jalankan `./gradlew test koverVerify`.
6. Generate `./gradlew koverHtmlReport`.
7. Build `./gradlew assembleDebug`.
8. Upload test report, coverage report, dan debug APK sebagai artifact.

## Status Implementasi

| Area | Status | Ringkasan |
|------|--------|-----------|
| Autentikasi | Selesai | Login mahasiswa, validasi email, penyimpanan sesi, auto-login, dan logout |
| Presensi | Selesai | QR scanner, input token manual, validasi token, dan submit presensi |
| Rekap presensi | Selesai | Rekap dari API Pocket ITERA dengan cache lokal SQLDelight |
| Early warning | Selesai | Peringatan kehadiran berbasis data cache lokal |
| Jadwal akademik | Selesai | CRUD jadwal offline dan detail jadwal |
| Pengingat lokal | Selesai | Reminder Android berbasis AlarmManager dan BroadcastReceiver |
| AI asisten | Selesai | Integrasi OpenCode Go API dengan API key lokal |
| Tema | Selesai | Light mode, dark mode, system theme, dan persistensi preferensi |
| Testing | Selesai | Unit test, repository test, ViewModel test, dan Compose UI test |
| CI/CD | Selesai | GitHub Actions untuk test, coverage, report, dan debug APK |

## Checklist Kualitas

| Pemeriksaan | Status |
|-------------|--------|
| Bug prioritas diperbaiki | Selesai |
| UI polish dan state handling | Selesai |
| Unit test lokal | Selesai, 31 test lulus |
| Compose UI test | Selesai, 3 UI test tersedia |
| Coverage minimum 50% | Selesai, Kover line coverage 65.3% |
| Instruksi build dan test | Selesai |
| CI untuk test, coverage, dan build | Selesai |

## Lisensi

Project ini dikembangkan untuk keperluan akademik mata kuliah Pengembangan Aplikasi Mobile, Institut Teknologi Sumatera.
