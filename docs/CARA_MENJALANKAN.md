# ðŸš€ Cara Menjalankan bookku

Panduan lengkap untuk menjalankan template aplikasi **bookku** (Kotlin Multiplatform).

> **Status target build:**
> - âœ… **Android** â€” jalur utama yang didukung penuh oleh template ini.
> - âš ï¸ **iOS** â€” kode shared (Kotlin) sudah ter-set untuk target iOS (X64/Arm64/SimulatorArm64),
>   tetapi project Xcode (`iosApp/`) **belum disertakan** di template ini.
>   Lihat bagian *"Menjalankan iOS (lanjutan)"* di bawah jika Anda ingin mencoba target iOS.

---

## 1. Prasyarat

| Software           | Versi minimum             | Buku                                        |
| ------------------ | ------------------------- | ---------------------------------------------- |
| **JDK**            | 17 (disarankan 17 / 21)   | Bawaan Android Studio sudah cukup              |
| **Android Studio** | Ladybug (2024.2.1) atau â†‘ | Wajib untuk Compose Multiplatform tooling      |
| **Android SDK**    | API 34 / 35               | Diinstall via SDK Manager Android Studio       |
| **Git**            | 2.x                       | Untuk clone & branching                        |
| **Xcode** (opt.)   | 15.0+                     | Hanya kalau ingin build iOS, **macOS-only**    |

Hardware yang nyaman: RAM minimal 8 GB (16 GB lebih lega), free space Â±10 GB.

---

## 2. Clone Repository

```bash
git clone https://github.com/informatika-itera/Proyek-Pengembangan-Aplikasi-Mobile.git
cd Proyek-Pengembangan-Aplikasi-Mobile
```

Buat branch project kelompok sesuai aturan di [GIT_WORKFLOW.md](./GIT_WORKFLOW.md):

```bash
git checkout -b project/121140003-121140004-NamaApp
```

---

## 3. Setup `local.properties`

File `local.properties` **TIDAK ter-commit** ke repository (sudah di `.gitignore`).
Setiap orang perlu membuatnya sendiri di root project (sejajar dengan `settings.gradle.kts`).

Cara termudah: copy dari template:

```bash
cp local.properties.example local.properties
```

Lalu edit `local.properties`:

```properties
# Lokasi Android SDK (Android Studio biasanya mengisi otomatis saat sync)
# macOS  :
# sdk.dir=/Users/<USER>/Library/Android/sdk
# Linux  :
# sdk.dir=/home/<USER>/Android/Sdk
# Windows:
# sdk.dir=C\:\\Users\\<USER>\\AppData\\Local\\Android\\Sdk

# Google Gemini API Key (lihat langkah 4)
GEMINI_API_KEY=AIzaSy....your_real_key....
```

> Tanpa `GEMINI_API_KEY` aplikasi tetap **bisa dibuka**, tetapi fitur AI (ringkas,
> generate ide, perbaiki tulisan, dll) akan gagal dengan error 401/403.

---

## 4. Dapatkan Gemini API Key

1. Buka https://aistudio.google.com
2. Login dengan akun Google.
3. Klik **Get API Key** â†’ **Create API Key** â†’ pilih project (atau buat baru).
4. Copy key dan tempel ke `local.properties` di baris `GEMINI_API_KEY=`.

> âš ï¸ **Jangan share / commit API key.** File `local.properties` sudah di-ignore.

---

## 5. Build & Sync via Android Studio (cara yang dianjurkan)

1. Buka Android Studio.
2. **File â†’ Open** â†’ pilih folder root project (`Pryk-PAM`).
3. Klik **Trust Project**.
4. Tunggu Gradle sync selesai. Sync pertama bisa 5â€“15 menit (download
   Compose Multiplatform, KMP runtime, dependencies).
5. Bila ada notifikasi *"Install missing platform"*, klik **Install**.

Saat sync sukses Anda akan melihat run configuration **composeApp** di toolbar.

---

## 6. Build dari Terminal (alternatif)

Project ini sudah berisi Gradle wrapper. Anda **tidak** perlu menginstall Gradle
manual â€” wrapper akan mendownload Gradle 8.9 sendiri.

```bash
# Pertama kali (download dependencies + build semua artifact)
./gradlew build

# Build APK debug saja (lebih cepat)
./gradlew :composeApp:assembleDebug

# Install ke emulator/device yang sedang aktif
./gradlew :composeApp:installDebug
```

> Di Windows pakai `gradlew.bat ...` (bukan `./gradlew`).

Generate file SQLDelight (biasanya otomatis, tapi kalau perlu manual):

```bash
./gradlew :composeApp:generateCommonMainNoteDatabaseInterface
```

> Nama task ini berasal dari konfigurasi di `composeApp/build.gradle.kts`:
> `sqldelight { databases { create("BookDatabase") { ... } } }`.

Jalankan unit test (commonTest):

```bash
# Semua test di semua target
./gradlew allTests

# Hanya unit test JVM/Android debug
./gradlew :composeApp:testDebugUnitTest
```

---

## 7. Jalankan di Android

### 7.1 Pakai Emulator

1. Android Studio â†’ **Tools â†’ Device Manager â†’ Create Device**.
2. Pilih device (mis. **Pixel 7**), system image **API 34** atau lebih baru.
3. Klik **Finish**, lalu **Run** emulator (â–¶).
4. Pilih run configuration **composeApp** di toolbar atas.
5. Klik tombol **Run** (â–¶) atau tekan **Shift + F10**.

### 7.2 Pakai HP Fisik

1. HP Android â†’ **Settings â†’ About Phone** â†’ ketuk **Build Number** 7Ã—
   untuk mengaktifkan **Developer Options**.
2. **Developer Options** â†’ aktifkan **USB debugging**.
3. Sambungkan HP ke laptop via kabel USB â†’ pilih **Allow** saat dialog muncul.
4. Pilih device di toolbar Android Studio â†’ **Run** (â–¶).

---

## 8. Menjalankan iOS (lanjutan, opsional)

Template ini **belum menyertakan** folder `iosApp/` dengan project Xcode yang
siap pakai. Anda punya 2 opsi:

### Opsi A â€” Pakai KMP Wizard JetBrains

1. Buka https://kmp.jetbrains.com.
2. Generate template baru "Compose Multiplatform" (Android + iOS).
3. Copy folder `iosApp/` hasil wizard ke root project ini.
4. Edit `iosApp/iosApp/iOSApp.swift` agar memanggil `MainViewControllerKt.MainViewController()`
   dari module `ComposeApp` (lihat dokumentasi inline di
   `composeApp/src/iosMain/kotlin/com/example/bookku/MainViewController.kt`).

### Opsi B â€” Build framework saja

Walau belum ada folder `iosApp/`, kode Kotlin Anda tetap bisa dikompilasi
ke framework iOS:

```bash
# Build framework debug untuk simulator Apple Silicon
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
```

Hasilnya ada di `composeApp/build/bin/iosSimulatorArm64/debugFramework/`.

> **Buku:** build target iOS hanya berjalan di **macOS** (butuh Kotlin/Native
> toolchain dan Xcode). Pada Windows/Linux target iOS akan otomatis di-skip.

---

## 9. Verifikasi Aplikasi Berjalan

Checklist setelah app jalan:

- [ ] Splash â†’ Home Screen tampil dengan FAB **+**.
- [ ] Tap **+** â†’ bisa membuat catatan baru (judul + konten).
- [ ] Buku baru muncul di daftar Home.
- [ ] Tap catatan â†’ masuk ke detail screen.
- [ ] Pin / unpin berjalan; catatan ter-pin pindah ke atas.
- [ ] Search (ikon ðŸ”) menyaring berdasarkan judul/konten.
- [ ] Filter kategori (chip "Semua/Umum/Pekerjaan/...") berfungsi.
- [ ] Sort menu (ikon â‡…) mengubah urutan.
- [ ] Hapus dari card Home bekerja.
- [ ] AI Assistant (ikon âœ¨) terbuka.
- [ ] Dengan API key valid â†’ aksi **Ringkas** mengembalikan teks (butuh konten â‰¥ 50 karakter).

---

## 10. Troubleshooting Cepat

| Gejala                                                 | Solusi                                                            |
| ------------------------------------------------------ | ------------------------------------------------------------------ |
| `SDK location not found`                               | Edit `local.properties`, isi `sdk.dir=...` atau buka project lewat Android Studio agar diisi otomatis. |
| `GEMINI_API_KEY` kosong / 401 Unauthorized             | Periksa baris `GEMINI_API_KEY=...` di `local.properties` lalu rebuild. |
| `Cannot resolve symbol 'BookDatabase'`                 | Jalankan `./gradlew :composeApp:generateCommonMainNoteDatabaseInterface`, lalu **Build â†’ Rebuild Project**. |
| Gradle sync lambat sekali pertama kali                 | Normal â€” dependencies KMP cukup besar (~1 GB). Pastikan internet stabil. |
| `Daemon ... was terminated` saat build                 | Naikkan heap di `gradle.properties`: `org.gradle.jvmargs=-Xmx6g`. |
| Build error setelah ganti versi                        | `./gradlew clean` lalu rebuild; bila tetap gagal hapus folder `.gradle/` lokal lalu sync ulang. |
| Error `Plugin com.android.application not found` di Linux/CI | Pastikan punya akses ke repo Google + Maven Central (cek setting proxy/firewall). |

Lebih lengkap di [`TROUBLESHOOTING.md`](./TROUBLESHOOTING.md).

---

## 11. File Penting

```
Pryk-PAM/
â”œâ”€â”€ local.properties           â† BUAT FILE INI (tidak ter-commit)
â”œâ”€â”€ local.properties.example   â† Template, di-commit
â”œâ”€â”€ settings.gradle.kts
â”œâ”€â”€ build.gradle.kts
â”œâ”€â”€ gradlew / gradlew.bat      â† Wrapper, dipanggil sebagai ./gradlew
â”œâ”€â”€ gradle/
â”‚   â”œâ”€â”€ libs.versions.toml     â† Daftar versi semua dependency
â”‚   â””â”€â”€ wrapper/
â”œâ”€â”€ composeApp/
â”‚   â”œâ”€â”€ build.gradle.kts
â”‚   â””â”€â”€ src/
â”‚       â”œâ”€â”€ commonMain/        â† Kode shared Kotlin
â”‚       â”œâ”€â”€ commonMain/sqldelight/  â† Skema DB (Note.sq)
â”‚       â”œâ”€â”€ commonTest/        â† Unit test
â”‚       â”œâ”€â”€ androidMain/       â† Implementasi spesifik Android
â”‚       â””â”€â”€ iosMain/           â† Implementasi spesifik iOS
â””â”€â”€ docs/                      â† Dokumentasi (file ini ada di sini)
```

---

## 12. Tips Pengembangan

- **Live Edit** Compose: aktif by default, edit `@Composable` lalu lihat
  hasilnya tanpa restart app (selama struktur tidak berubah drastis).
- **Logcat**: `View â†’ Tool Windows â†’ Logcat` (filter dengan tag `HTTP:` untuk
  melihat log Ktor karena kita pakai `enableLogging = true`).
- **Run unit test cepat** dari IDE: klik kanan file `*Test.kt` â†’ **Run**.
- **Debug DataStore**: file preferences disimpan di
  `/data/data/com.example.bookku/files/bookku.preferences_pb` (Android).

---

## Referensi

- [Kotlin Multiplatform docs](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [SQLDelight](https://cashapp.github.io/sqldelight/)
- [Koin DI](https://insert-koin.io/)
- [Ktor Client](https://ktor.io/docs/welcome.html)
- [Google Gemini API](https://ai.google.dev/docs)


