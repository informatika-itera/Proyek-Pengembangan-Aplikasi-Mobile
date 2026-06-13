# Struktur Kode dan Arsitektur

Dokumen ini menjelaskan struktur kode KelazZz dan batas tanggung jawab tiap layer.

---

## Ringkasan Arsitektur

KelazZz memakai Clean Architecture + MVVM.

```text
Presentation Layer
Screens, reusable components, navigation, ViewModel, UI state
        |
        v
Domain Layer
Domain models, repository interfaces, use cases, validation
        |
        v
Data Layer
Repository implementations, Pocket API, OpenCode API, SQLDelight, DataStore
```

Dependency diarahkan ke dalam:
- Presentation boleh mengenal Domain.
- Data mengimplementasikan kontrak dari Domain.
- Domain tidak bergantung pada UI, database, API, atau Android.

---

## Struktur Folder Utama

```text
composeApp/src/
|-- commonMain/
|   |-- kotlin/com/kelazzz/app/
|   |   |-- App.kt
|   |   |-- core/
|   |   |   |-- network/
|   |   |   |-- notification/
|   |   |   `-- util/
|   |   |-- data/
|   |   |   |-- local/datastore/
|   |   |   |-- remote/ai/
|   |   |   |-- remote/pocket/
|   |   |   `-- repository/
|   |   |-- di/
|   |   |-- domain/
|   |   |   |-- model/
|   |   |   |-- repository/
|   |   |   |-- usecase/
|   |   |   `-- validation/
|   |   `-- presentation/
|   |       |-- components/
|   |       |-- navigation/
|   |       |-- screens/
|   |       |   |-- ai/
|   |       |   |-- home/
|   |       |   |-- jadwal/
|   |       |   |-- kalender/
|   |       |   |-- login/
|   |       |   |-- presensi/
|   |       |   |-- profile/
|   |       |   `-- rekap/
|   |       `-- theme/
|   `-- sqldelight/com/kelazzz/app/data/local/
|       |-- Jadwal.sq
|       |-- Presensi.sq
|       `-- 1.sqm
|-- androidMain/
|   |-- AndroidManifest.xml
|   `-- kotlin/com/kelazzz/app/
|       |-- MainActivity.kt
|       |-- KelazZzApplication.kt
|       |-- core/di/
|       |-- core/network/
|       |-- core/notification/
|       |-- core/util/
|       |-- data/local/datastore/
|       `-- presentation/components/
|-- commonTest/
|-- androidUnitTest/
`-- androidInstrumentedTest/
```

---

## Layer Domain

Domain berisi model dan aturan bisnis yang tidak bergantung pada framework.

Isi penting:

| Folder/File | Fungsi |
|-------------|--------|
| `domain/model/` | Model seperti `Jadwal`, `Kelas`, `Presensi`, `AttendanceSummary`, `ThemeMode`, `ChatMessage` |
| `domain/repository/` | Kontrak `AuthRepository`, `JadwalRepository`, `PresensiRepository`, `AIRepository` |
| `domain/usecase/UseCases.kt` | Use case untuk login, presensi, jadwal, dan AI |
| `domain/validation/StudentEmailPolicy.kt` | Validasi dan normalisasi email mahasiswa ITERA |

Contoh kontrak repository:

```kotlin
interface JadwalRepository {
    fun getAllJadwal(): Flow<List<Jadwal>>
    fun getUpcomingJadwal(fromDate: String, limit: Int): Flow<List<Jadwal>>
    fun getJadwalById(id: Long): Flow<Jadwal?>
    suspend fun insertJadwal(jadwal: Jadwal): Long
    suspend fun updateJadwal(jadwal: Jadwal)
    suspend fun deleteJadwal(id: Long)
}
```

---

## Layer Data

Data layer menghubungkan domain dengan sumber data nyata.

Isi penting:

| Folder/File | Fungsi |
|-------------|--------|
| `data/remote/pocket/PocketApiService.kt` | Request ke API Pocket ITERA |
| `data/remote/ai/OpenCodeGoService.kt` | Request ke OpenCode Go API |
| `data/remote/ai/ChatToolHandler.kt` | Menyediakan konteks lokal untuk AI |
| `data/local/datastore/UserPreferences.kt` | Token sesi, data user, device info, tema |
| `data/repository/AuthRepositoryImpl.kt` | Implementasi login/logout dan session |
| `data/repository/PresensiRepositoryImpl.kt` | Sync kelas, presensi, rekap, dan cache |
| `data/repository/JadwalRepositoryImpl.kt` | CRUD jadwal dan scheduling reminder |
| `data/repository/AIRepositoryImpl.kt` | Chat AI dan riwayat percakapan |

SQLDelight:
- `Jadwal.sq` untuk jadwal dan reminder.
- `Presensi.sq` untuk kelas, presensi, dan ringkasan kehadiran.
- Generated database: `KelazZzDatabase`.

DataStore:
- File Android: `kelazzz.preferences_pb`.
- Lokasi: internal storage aplikasi.
- Digunakan untuk session dan theme preference.

---

## Layer Presentation

Presentation berisi UI Compose, ViewModel, UI state, dan navigation.

Screen utama:

| Screen | Fungsi |
|--------|--------|
| `login/` | Login mahasiswa Pocket ITERA |
| `home/` | Dashboard, agenda terdekat, warning kehadiran |
| `presensi/` | QR scanner dan token manual |
| `rekap/` | Rekap mata kuliah dan riwayat presensi |
| `jadwal/` | List, tambah/edit, dan detail jadwal |
| `kalender/` | Tampilan kalender jadwal |
| `ai/` | AI Asisten Akademik |
| `profile/` | Data user, theme switch, logout |

ViewModel bertugas:
- Mengambil data dari repository/use case.
- Mengubah Flow menjadi `StateFlow`.
- Menyimpan form state.
- Menangani event dari UI.
- Mengekspos error/loading/success state.

Screen Compose bertugas:
- Render state.
- Mengirim event ke ViewModel.
- Tidak menjalankan business logic langsung.

---

## Dependency Injection

Koin dipakai untuk menyediakan dependency.

| File | Fungsi |
|------|--------|
| `di/AppModule.kt` | Root shared modules |
| `di/DataModule.kt` | HttpClient, services, database, DataStore, repositories |
| `di/ViewModelModule.kt` | ViewModel injection |
| `androidMain/core/di/AndroidModule.kt` | Dependency khusus Android |

Alur init Android:

```text
KelazZzApplication.onCreate()
        |
        v
initKoin(platformModules = listOf(androidModule))
        |
        v
dataModule + viewModelModule + androidModule
```

---

## Platform-Specific Code

Common code memakai `expect`, Android memakai `actual`.

Contoh:

| Common | Android actual |
|--------|----------------|
| `ApiConfig.kt` | `ApiConfig.android.kt` membaca `BuildConfig.OPENCODE_API_KEY` |
| `DatabaseDriverFactory.kt` | `DatabaseDriverFactory.android.kt` memakai `AndroidSqliteDriver` |
| `DataStoreFactory.kt` | `DataStoreFactory.android.kt` memakai `context.filesDir` |
| `JadwalNotificationScheduler.kt` | `AndroidJadwalNotificationScheduler.kt` memakai `AlarmManager` |
| `QrCodeScannerView.kt` | `QrCodeScannerView.android.kt` memakai CameraX + ML Kit |

Target iOS belum aktif, jadi tidak ada alur build iOS yang dijaga sebagai target final.

---

## Data Flow Contoh: Tambah Jadwal

```text
User isi form jadwal
        |
JadwalAddEditScreen kirim event ke ViewModel
        |
JadwalAddEditViewModel validasi form
        |
JadwalRepository.insertJadwal()
        |
JadwalRepositoryImpl simpan ke SQLDelight
        |
Jika reminder aktif, scheduler Android menjadwalkan notifikasi
        |
Flow jadwal emit data baru
        |
JadwalListScreen/Home ikut ter-update
```

---

## Data Flow Contoh: Sync Presensi

```text
User membuka/sync rekap
        |
PresensiViewModel/RekapViewModel memanggil repository
        |
PresensiRepositoryImpl mengambil data dari PocketApiService
        |
Data remote dipetakan ke domain dan disimpan ke SQLDelight
        |
UI membaca cache lokal melalui Flow
```

---

## Testing Structure

```text
commonTest/
|-- domain/model/DomainModelTest.kt
|-- domain/usecase/UseCasesTest.kt
|-- domain/validation/StudentEmailPolicyTest.kt
|-- presentation/ViewModelTest.kt
`-- testutil/
    |-- TestFakes.kt
    `-- TestFixtures.kt

androidUnitTest/
`-- data/repository/JadwalRepositoryImplTest.kt

androidInstrumentedTest/
`-- presentation/components/CommonComponentsUiTest.kt
```

Jenis test:
- Domain model dan validation memakai `kotlin.test`.
- ViewModel memakai `kotlinx-coroutines-test`.
- Repository jadwal memakai SQLDelight SQLite driver in-memory.
- UI component test memakai Compose UI Test.

---

## Naming Convention

| Jenis | Contoh |
|-------|--------|
| Package | `com.kelazzz.app.domain` |
| Model | `Jadwal`, `Presensi`, `AttendanceSummary` |
| Repository interface | `JadwalRepository` |
| Repository implementation | `JadwalRepositoryImpl` |
| ViewModel | `JadwalAddEditViewModel` |
| Screen | `JadwalAddEditScreen` |
| Test | `JadwalRepositoryImplTest` |

---

## Prinsip Saat Mengubah Kode

- Jangan bypass repository dari ViewModel.
- Jangan taruh logic API/database di Composable.
- Jika mengubah model, cek schema SQLDelight, mapper, repository, UI, dan test.
- Jika menambah dependency, masukkan lewat `gradle/libs.versions.toml`.
- Jika menambah fitur Android-specific, buat kontrak di common bila perlu dan implementasi di `androidMain`.
- Update docs jika fitur atau cara menjalankan berubah.
