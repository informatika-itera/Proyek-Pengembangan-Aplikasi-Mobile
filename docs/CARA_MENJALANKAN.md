# Cara Menjalankan KelazZz

Panduan ini menjelaskan cara setup, build, test, dan menjalankan KelazZz. KelazZz adalah aplikasi Android berbasis Kotlin Multiplatform dan Compose Multiplatform untuk presensi, rekap kehadiran, jadwal akademik, reminder lokal, dan AI asisten akademik mahasiswa ITERA.

> Status target: Android adalah target aktif. Struktur Kotlin Multiplatform tetap dipakai untuk memisahkan shared logic, tetapi target iOS tidak aktif pada project ini.

---

## 1. Prasyarat

| Software | Versi/Catatan |
|----------|---------------|
| JDK | 17 |
| Android Studio | Ladybug 2024.2.1 atau lebih baru |
| Android SDK | compile/target SDK 35 |
| Git | 2.x |
| Android device/emulator | Disarankan API 34+ |

Hardware yang nyaman: RAM minimal 8 GB, 16 GB lebih baik untuk Gradle dan Android Studio.

---

## 2. Clone Repository

```bash
git clone https://github.com/eltoruz/KelazZz.git
cd KelazZz
```

Branch project kelompok:

```bash
git checkout project/123140098-123140077-KelazZz
```

Jika clone dari repository kelas/upstream, sesuaikan URL remote dengan repo yang digunakan tim.

---

## 3. Setup `local.properties`

Buat file `local.properties` di root project, sejajar dengan `settings.gradle.kts`.

```properties
# Android SDK, biasanya otomatis dibuat Android Studio
sdk.dir=/path/to/android/sdk

# API key untuk AI Asisten
OPENCODE_API_KEY=your_real_key_here
```

Catatan:
- File `local.properties` tidak boleh di-commit.
- Tanpa `OPENCODE_API_KEY`, aplikasi tetap bisa dibuka, tetapi fitur AI akan gagal ketika memanggil API.
- Di CI, key placeholder digunakan agar build tetap berjalan.

---

## 4. Mendapatkan OpenCode Go API Key

1. Buka https://opencode.ai/go.
2. Login ke akun OpenCode.
3. Ambil API key dari dashboard/console.
4. Tempel ke `local.properties` pada `OPENCODE_API_KEY=...`.

Jangan membagikan API key di commit, issue, PR, atau screenshot publik.

---

## 5. Build via Android Studio

1. Buka Android Studio.
2. Pilih **File > Open** lalu pilih folder root `KelazZz`.
3. Klik **Trust Project**.
4. Tunggu Gradle sync selesai.
5. Pilih run configuration `composeApp`.
6. Jalankan ke emulator atau device Android.

---

## 6. Build dari Terminal

Project menggunakan Gradle Wrapper, jadi tidak perlu install Gradle manual.

```bash
# Build semua target yang aktif
./gradlew build

# Build APK debug
./gradlew :composeApp:assembleDebug

# Install ke emulator/device aktif
./gradlew :composeApp:installDebug
```

Generate interface SQLDelight jika diperlukan:

```bash
./gradlew :composeApp:generateCommonMainKelazZzDatabaseInterface
```

Database yang digunakan:
- `KelazZzDatabase`
- Package generated: `com.kelazzz.app.data.local`
- File schema: `Jadwal.sq` dan `Presensi.sq`
- Nama database Android: `kelazzz_v2.db`

---

## 7. Menjalankan Test dan Coverage

```bash
# Semua unit test lokal
./gradlew test

# Unit test Android debug
./gradlew :composeApp:testDebugUnitTest

# Validasi coverage Sprint 4
./gradlew test koverVerify

# Generate report coverage HTML
./gradlew koverHtmlReport

# Compile source instrumented UI test
./gradlew :composeApp:compileDebugAndroidTestSources
```

Compose UI test penuh membutuhkan emulator atau device:

```bash
./gradlew :composeApp:connectedDebugAndroidTest
```

---

## 8. Menjalankan di Android

### Emulator

1. Android Studio > **Tools > Device Manager > Create Device**.
2. Pilih device, misalnya Pixel 7.
3. Pilih system image API 34 atau lebih baru.
4. Jalankan emulator.
5. Klik **Run** pada configuration `composeApp`.

### HP Fisik

1. Aktifkan Developer Options.
2. Aktifkan USB debugging.
3. Sambungkan HP via USB.
4. Izinkan debugging saat prompt muncul.
5. Jalankan configuration `composeApp`.

Permission yang dipakai:
- Internet untuk API Pocket ITERA dan OpenCode Go.
- Camera untuk QR scanner.
- Post notifications untuk reminder lokal pada Android 13+.

---

## 9. Checklist Verifikasi Manual

- [ ] Login menggunakan email mahasiswa `@student.itera.ac.id` atau NIM.
- [ ] Login non-mahasiswa ditolak.
- [ ] Home menampilkan nama pengguna, agenda terdekat, dan warning kehadiran.
- [ ] Rekap presensi bisa sync dan menampilkan daftar mata kuliah/riwayat.
- [ ] Presensi bisa membuka QR scanner dan input token manual.
- [ ] Jadwal bisa tambah, edit, detail, dan hapus agenda.
- [ ] Jenis `Kelas` memakai pilihan hari dan reminder mingguan.
- [ ] Jenis tugas/kuis/ujian/presentasi memakai tanggal biasa.
- [ ] Reminder lokal bisa dijadwalkan.
- [ ] AI Asisten bisa mengirim pertanyaan jika `OPENCODE_API_KEY` valid.
- [ ] Profil menampilkan akun, pilihan tema, dan logout.

---

## 10. Troubleshooting Cepat

| Gejala | Solusi |
|--------|--------|
| `SDK location not found` | Isi `sdk.dir` di `local.properties` atau buka lewat Android Studio. |
| AI error 401/403 | Cek `OPENCODE_API_KEY`, lalu rebuild. |
| SQLDelight generated class tidak ditemukan | Jalankan `./gradlew :composeApp:generateCommonMainKelazZzDatabaseInterface`. |
| Camera tidak terbuka | Pastikan permission camera diberikan di device/emulator. |
| Notifikasi tidak muncul | Pastikan permission notification diberikan pada Android 13+. |
| Gradle sync lambat | Normal pada sync pertama karena dependency KMP cukup besar. |
| Test coverage gagal | Jalankan `./gradlew test koverVerify --stacktrace` dan cek test yang gagal. |

Lebih lengkap ada di [TROUBLESHOOTING.md](./TROUBLESHOOTING.md).

---

## 11. File Penting

```text
KelazZz/
|-- local.properties                 # Dibuat lokal, tidak di-commit
|-- settings.gradle.kts
|-- build.gradle.kts
|-- gradlew / gradlew.bat
|-- gradle/libs.versions.toml
|-- composeApp/
|   |-- build.gradle.kts
|   `-- src/
|       |-- commonMain/kotlin/com/kelazzz/app/
|       |-- commonMain/sqldelight/com/kelazzz/app/data/local/
|       |   |-- Jadwal.sq
|       |   `-- Presensi.sq
|       |-- commonTest/
|       |-- androidMain/
|       |-- androidUnitTest/
|       `-- androidInstrumentedTest/
`-- docs/
```

---

## 12. Tips Pengembangan

- Pull branch terbaru sebelum mulai coding.
- Jalankan minimal `./gradlew test` sebelum push perubahan logic.
- Jalankan `./gradlew koverVerify` jika menyentuh test/coverage.
- Gunakan Logcat untuk debugging API, DataStore, scanner, dan notification.
- Untuk reset data lokal saat development, uninstall app dari emulator/device lalu install ulang.
