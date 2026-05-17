# 🚀 Cara Menjalankan NoteAI

Panduan lengkap untuk menjalankan template aplikasi **NoteAI** (Kotlin Multiplatform).

> **Status target build:**
> - ✅ **Android** — jalur utama yang didukung penuh oleh template ini.
> - ⚠️ **iOS** — kode shared (Kotlin) sudah ter-set untuk target iOS (X64/Arm64/SimulatorArm64),
>   tetapi project Xcode (`iosApp/`) **belum disertakan** di template ini.
>   Lihat bagian *"Menjalankan iOS (lanjutan)"* di bawah jika Anda ingin mencoba target iOS.

---

## 1. Prasyarat

| Software           | Versi minimum             | Catatan                                        |
| ------------------ | ------------------------- | ---------------------------------------------- |
| **JDK**            | 17 (disarankan 17 / 21)   | Bawaan Android Studio sudah cukup              |
| **Android Studio** | Ladybug (2024.2.1) atau ↑ | Wajib untuk Compose Multiplatform tooling      |
| **Android SDK**    | API 34 / 35               | Diinstall via SDK Manager Android Studio       |
| **Git**            | 2.x                       | Untuk clone & branching                        |
| **Xcode** (opt.)   | 15.0+                     | Hanya kalau ingin build iOS, **macOS-only**    |

Hardware yang nyaman: RAM minimal 8 GB (16 GB lebih lega), free space ±10 GB.

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
3. Klik **Get API Key** → **Create API Key** → pilih project (atau buat baru).
4. Copy key dan tempel ke `local.properties` di baris `GEMINI_API_KEY=`.

> ⚠️ **Jangan share / commit API key.** File `local.properties` sudah di-ignore.

---

## 5. Build & Sync via Android Studio (cara yang dianjurkan)

1. Buka Android Studio.
2. **File → Open** → pilih folder root project (`Pryk-PAM`).
3. Klik **Trust Project**.
4. Tunggu Gradle sync selesai. Sync pertama bisa 5–15 menit (download
   Compose Multiplatform, KMP runtime, dependencies).
5. Bila ada notifikasi *"Install missing platform"*, klik **Install**.

Saat sync sukses Anda akan melihat run configuration **composeApp** di toolbar.

---

## 6. Build dari Terminal (alternatif)

Project ini sudah berisi Gradle wrapper. Anda **tidak** perlu menginstall Gradle
manual — wrapper akan mendownload Gradle 8.9 sendiri.

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
./gradlew :composeApp:generateCommonMainPusakaDatabaseInterface
```

> Nama task ini berasal dari konfigurasi di `composeApp/build.gradle.kts`:
> `sqldelight { databases { create("PusakaDatabase") { ... } } }`.

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

1. Android Studio → **Tools → Device Manager → Create Device**.
2. Pilih device (mis. **Pixel 7**), system image **API 34** atau lebih baru.
3. Klik **Finish**, lalu **Run** emulator (▶).
4. Pilih run configuration **composeApp** di toolbar atas.
5. Klik tombol **Run** (▶) atau tekan **Shift + F10**.

### 7.2 Pakai HP Fisik

1. HP Android → **Settings → About Phone** → ketuk **Build Number** 7×
   untuk mengaktifkan **Developer Options**.
2. **Developer Options** → aktifkan **USB debugging**.
3. Sambungkan HP ke laptop via kabel USB → pilih **Allow** saat dialog muncul.
4. Pilih device di toolbar Android Studio → **Run** (▶).

---

## 8. Menjalankan iOS (lanjutan, opsional)

Template ini **belum menyertakan** folder `iosApp/` dengan project Xcode yang
siap pakai. Anda punya 2 opsi:

### Opsi A — Pakai KMP Wizard JetBrains

1. Buka https://kmp.jetbrains.com.
2. Generate template baru "Compose Multiplatform" (Android + iOS).
3. Copy folder `iosApp/` hasil wizard ke root project ini.
4. Edit `iosApp/iosApp/iOSApp.swift` agar memanggil `MainViewControllerKt.MainViewController()`
   dari module `ComposeApp` (lihat dokumentasi inline di
   `composeApp/src/iosMain/kotlin/com/example/noteai/MainViewController.kt`).

### Opsi B — Build framework saja

Walau belum ada folder `iosApp/`, kode Kotlin Anda tetap bisa dikompilasi
ke framework iOS:

```bash
# Build framework debug untuk simulator Apple Silicon
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
```

Hasilnya ada di `composeApp/build/bin/iosSimulatorArm64/debugFramework/`.

> **Catatan:** build target iOS hanya berjalan di **macOS** (butuh Kotlin/Native
> toolchain dan Xcode). Pada Windows/Linux target iOS akan otomatis di-skip.

---

## 9. Verifikasi Aplikasi Berjalan

Checklist setelah app jalan:

- [ ] Splash → Home Screen tampil dengan FAB **+**.
- [ ] Tap **+** → bisa membuat catatan baru (judul + konten).
- [ ] Catatan baru muncul di daftar Home.
- [ ] Tap catatan → masuk ke detail screen.
- [ ] Pin / unpin berjalan; catatan ter-pin pindah ke atas.
- [ ] Search (ikon 🔍) menyaring berdasarkan judul/konten.
- [ ] Filter kategori (chip "Semua/Umum/Pekerjaan/...") berfungsi.
- [ ] Sort menu (ikon ⇅) mengubah urutan.
- [ ] Hapus dari card Home bekerja.
- [ ] AI Assistant (ikon ✨) terbuka.
- [ ] Dengan API key valid → aksi **Ringkas** mengembalikan teks (butuh konten ≥ 50 karakter).

---

## 10. Troubleshooting Cepat

| Gejala                                                 | Solusi                                                            |
| ------------------------------------------------------ | ------------------------------------------------------------------ |
| `SDK location not found`                               | Edit `local.properties`, isi `sdk.dir=...` atau buka project lewat Android Studio agar diisi otomatis. |
| `GEMINI_API_KEY` kosong / 401 Unauthorized             | Periksa baris `GEMINI_API_KEY=...` di `local.properties` lalu rebuild. |
| `Cannot resolve symbol 'NoteDatabase'`                 | Jalankan `./gradlew :composeApp:generateCommonMainNoteDatabaseInterface`, lalu **Build → Rebuild Project**. |
| Gradle sync lambat sekali pertama kali                 | Normal — dependencies KMP cukup besar (~1 GB). Pastikan internet stabil. |
| `Daemon ... was terminated` saat build                 | Naikkan heap di `gradle.properties`: `org.gradle.jvmargs=-Xmx6g`. |
| Build error setelah ganti versi                        | `./gradlew clean` lalu rebuild; bila tetap gagal hapus folder `.gradle/` lokal lalu sync ulang. |
| Error `Plugin com.android.application not found` di Linux/CI | Pastikan punya akses ke repo Google + Maven Central (cek setting proxy/firewall). |

Lebih lengkap di [`TROUBLESHOOTING.md`](./TROUBLESHOOTING.md).

---

## 11. File Penting

```
Pryk-PAM/
├── local.properties           ← BUAT FILE INI (tidak ter-commit)
├── local.properties.example   ← Template, di-commit
├── settings.gradle.kts
├── build.gradle.kts
├── gradlew / gradlew.bat      ← Wrapper, dipanggil sebagai ./gradlew
├── gradle/
│   ├── libs.versions.toml     ← Daftar versi semua dependency
│   └── wrapper/
├── composeApp/
│   ├── build.gradle.kts
│   └── src/
│       ├── commonMain/        ← Kode shared Kotlin
│       ├── commonMain/sqldelight/  ← Skema DB (Note.sq)
│       ├── commonTest/        ← Unit test
│       ├── androidMain/       ← Implementasi spesifik Android
│       └── iosMain/           ← Implementasi spesifik iOS
└── docs/                      ← Dokumentasi (file ini ada di sini)
```

---

## 12. Tips Pengembangan

- **Live Edit** Compose: aktif by default, edit `@Composable` lalu lihat
  hasilnya tanpa restart app (selama struktur tidak berubah drastis).
- **Logcat**: `View → Tool Windows → Logcat` (filter dengan tag `HTTP:` untuk
  melihat log Ktor karena kita pakai `enableLogging = true`).
- **Run unit test cepat** dari IDE: klik kanan file `*Test.kt` → **Run**.
- **Debug DataStore**: file preferences disimpan di
  `/data/data/com.example.noteai/files/noteai.preferences_pb` (Android).

---

## Referensi

- [Kotlin Multiplatform docs](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [SQLDelight](https://cashapp.github.io/sqldelight/)
- [Koin DI](https://insert-koin.io/)
- [Ktor Client](https://ktor.io/docs/welcome.html)
- [Google Gemini API](https://ai.google.dev/docs)
