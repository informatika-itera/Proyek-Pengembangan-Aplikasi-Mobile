# Cara Menjalankan My Bawang Gacha

Panduan lengkap untuk menjalankan aplikasi My Bawang Gacha (Kotlin Multiplatform).

## Status Target Build
- Android: Jalur utama yang didukung penuh.
- iOS: Logika kode dibagikan (shared Kotlin) dan proyek Xcode (`iosApp/`) sudah tersedia di repositori ini. Kompilasi target iOS memerlukan macOS dengan Xcode terinstal.

## 1. Prasyarat

| Software | Versi Minimum | Catatan |
| :--- | :--- | :--- |
| JDK | 17 (disarankan 17 / 21) | Bawaan Android Studio sudah cukup |
| Android Studio | Ladybug (2024.2.1) atau lebih baru | Wajib untuk Compose Multiplatform tooling |
| Android SDK | API 34 / 35 | Diinstal via SDK Manager Android Studio |
| Git | 2.x | Untuk clone dan branching |
| Xcode | 15.0 atau lebih baru | Hanya jika ingin melakukan build iOS (khusus macOS) |

Spesifikasi perangkat keras yang disarankan: RAM minimal 8 GB (16 GB disarankan) dan ruang penyimpanan kosong sekitar 10 GB.

## 2. Kloning Repositori

```bash
git clone https://github.com/sinavarasina/Proyek-Pengembangan-Aplikasi-Mobile.git
cd Proyek-Pengembangan-Aplikasi-Mobile
```

Buat branch proyek kelompok sesuai konvensi penamaan di Git Workflow:

```bash
git checkout -b project/NIM1-NIM2-NamaApp
```

## 3. Setup local.properties

Berkas `local.properties` tidak dimasukkan ke dalam kontrol repositori Git (sudah terdaftar di `.gitignore`). Setiap pengembang harus membuatnya secara mandiri di direktori root proyek.

Salin dari contoh template yang tersedia:

```bash
cp local.properties.example local.properties
```

Edit berkas `local.properties` tersebut dan tambahkan API key Gemini Anda:

```properties
# Lokasi Android SDK (Android Studio akan mengisinya otomatis saat sync)
# macOS:
# sdk.dir=/Users/<USER>/Library/Android/sdk
# Linux:
# sdk.dir=/home/<USER>/Android/Sdk
# Windows:
# sdk.dir=C\:\\Users\\<USER>\\AppData\\Local\\Android\\Sdk

# Google Gemini API Key
GEMINI_API_KEY=AIzaSy...key_gemini_anda...
```

Jika `GEMINI_API_KEY` tidak diisi, aplikasi tetap dapat dijalankan tetapi fitur asisten AI akan mengembalikan error.

## 4. Dapatkan Gemini API Key

1. Buka Google AI Studio di https://aistudio.google.com/
2. Masuk menggunakan akun Google Anda.
3. Klik tombol Get API Key, lalu buat kunci API baru.
4. Salin kunci tersebut dan masukkan ke dalam berkas `local.properties` pada baris `GEMINI_API_KEY=`.

## 5. Build dan Sinkronisasi via Android Studio

1. Buka Android Studio.
2. Pilih File -> Open, arahkan ke direktori root proyek.
3. Klik Trust Project jika diminta.
4. Tunggu sinkronisasi Gradle selesai. Sinkronisasi pertama dapat memakan waktu 5 hingga 15 menit tergantung kecepatan internet Anda karena mengunduh dependensi Kotlin Multiplatform.
5. Jika muncul pemberitahuan untuk menginstal modul SDK yang kurang, klik opsi instalasi yang disediakan.

Setelah proses sinkronisasi sukses, Anda dapat memilih konfigurasi run androidApp di toolbar atas.

## 6. Build dari Terminal

Proyek ini dilengkapi dengan Gradle wrapper. Anda tidak perlu menginstal Gradle secara manual.

```bash
# Kompilasi seluruh proyek dan jalankan pengujian
./gradlew build

# Build berkas APK debug untuk Android
./gradlew :androidApp:assembleDebug

# Pasang aplikasi ke emulator atau perangkat Android yang aktif
./gradlew :androidApp:installDebug
```

Catatan: Pada sistem operasi Windows, gunakan `gradlew.bat` sebagai pengganti `./gradlew`.

Untuk membuat berkas interface SQLDelight secara manual:
```bash
./gradlew :composeApp:generateCommonMainNoteDatabaseInterface
```

Menjalankan pengujian (unit test):
```bash
# Menjalankan seluruh pengujian di semua target
./gradlew allTests

# Menjalankan unit test JVM/Android saja
./gradlew :composeApp:testDebugUnitTest
```

## 7. Jalankan di Android

### 7.1 Menggunakan Emulator
1. Di Android Studio, buka Tools -> Device Manager -> Create Device.
2. Buat perangkat virtual baru (misal: Pixel 7) dengan image API 34 atau lebih baru.
3. Jalankan emulator tersebut.
4. Pilih konfigurasi run androidApp di toolbar atas, kemudian klik tombol Run (ikon segitiga hijau) atau tekan Shift + F10.

### 7.2 Menggunakan Perangkat Fisik
1. Hubungkan perangkat Android menggunakan kabel data.
2. Aktifkan Opsi Pengembang (Developer Options) dengan mengetuk Build Number sebanyak 7 kali di menu Pengaturan Ponsel.
3. Aktifkan USB Debugging di dalam menu Developer Options.
4. Pilih perangkat fisik Anda pada toolbar Android Studio, lalu jalankan aplikasi.

## 8. Jalankan di iOS

Kompilasi dan jalankan aplikasi target iOS langsung menggunakan emulator Xcode atau perangkat simulator dari Android Studio (jika menggunakan plugin KMP) atau jalankan perintah berikut dari terminal macOS:

```bash
# Kompilasi kerangka kerja debug iOS simulator untuk Apple Silicon
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
```

Hasil kompilasi akan berada di direktori `composeApp/build/bin/iosSimulatorArm64/debugFramework/`.
Untuk menjalankan aplikasi secara penuh di simulator, buka direktori `iosApp/` menggunakan Xcode, pilih simulator target, lalu klik tombol Run di Xcode.

## 9. Verifikasi Aplikasi Berjalan

Lakukan pengujian manual berikut untuk memastikan aplikasi berjalan dengan normal:

- Discover: Halaman utama menampilkan rekomendasi musiman dan daftar anime/manga populer.
- Detail: Tap pada media menampilkan halaman informasi lengkap beserta relasi media.
- Library CRUD:
  - Buka detail media, pilih Tambah ke Library.
  - Masukkan status, progress episode/chapter, skor, dan catatan personal.
  - Simpan dan verifikasi item muncul di tab yang sesuai di My Library.
  - Edit progress atau status, lalu simpan perubahan.
  - Hapus item dari library dan pastikan dialog konfirmasi muncul.
- Gacha: Masuk ke menu Gacha, pilih tipe preferensi, klik tombol roll, dan pastikan hasil gacha muncul secara acak.
- Catatan: Menambahkan catatan lokal baru, mengubahnya, dan menghapusnya.
- AI Assistant: Membuka fitur chat AI, mengirimkan pertanyaan, dan menerima respon dari Gemini.
- Settings: Merubah tema (Terang, Gelap, Sistem) dan mengganti skema warna (misal: Gruvbox atau Code Geass) untuk memverifikasi perubahan visual.

## 10. Troubleshooting Umum

| Masalah | Solusi |
| :--- | :--- |
| SDK location not found | Isi variabel `sdk.dir` secara manual di berkas `local.properties` atau buka proyek via Android Studio agar dikonfigurasi otomatis. |
| API Key Kosong atau Error 401 | Pastikan variabel `GEMINI_API_KEY` di `local.properties` sudah terisi dengan kunci yang valid dari Google AI Studio, lalu lakukan rebuild proyek. |
| Class NoteDatabase tidak ditemukan | Jalankan perintah `./gradlew :composeApp:generateCommonMainNoteDatabaseInterface` lalu pilih Build -> Rebuild Project di Android Studio. |
| Gradle Sync lambat | Hal ini normal saat pertama kali sinkronisasi karena mengunduh dependensi multiplatform. Pastikan koneksi internet Anda stabil. |
| Daemon terminated karena kehabisan memori | Naikkan alokasi heap JVM di `gradle.properties` dengan mengubah baris: `org.gradle.jvmargs=-Xmx6g`. |

## 11. Struktur Berkas Kunci

```
Proyek-Pengembangan-Aplikasi-Mobile/
├── local.properties           # Berkas konfigurasi SDK dan API key (lokal, di-ignore)
├── local.properties.example   # Template berkas konfigurasi lokal
├── settings.gradle.kts        # Pengaturan modul proyek
├── build.gradle.kts           # Konfigurasi build root proyek
├── androidApp/                # Modul khusus aplikasi Android
├── iosApp/                    # Proyek Xcode untuk aplikasi iOS
├── composeApp/                # Modul bersama (shared) Kotlin Multiplatform
│   ├── build.gradle.kts       # Dependensi modul bersama
│   └── src/
│       ├── commonMain/        # Kode Kotlin yang dibagikan ke Android dan iOS
│       │   ├── kotlin/        # Logika bisnis dan UI presentation
│       │   └── sqldelight/    # Skema dan query SQLDelight (Note.sq, Anime.sq, Library.sq, Manga.sq, AiChat.sq, MediaCache.sq)
│       ├── androidMain/       # Implementasi spesifik platform Android
│       └── iosMain/           # Implementasi spesifik platform iOS
└── docs/                      # Dokumentasi proyek
```

## 12. Tips Pengembangan

- Logcat: Gunakan Logcat di Android Studio dan saring dengan tag `HTTP:` untuk memantau log jaringan dari Ktor Client.
- Preferensi DataStore: Berkas preferensi tersimpan lokal di dalam direktori internal data aplikasi masing-masing platform dengan package name `id.my.sinanonym.mybawanggacha`.

