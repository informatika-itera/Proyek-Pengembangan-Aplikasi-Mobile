# Cara Menjalankan

## Kebutuhan sistem

| Kebutuhan | Versi minimum |
| --- | --- |
| Android Studio | Ladybug 2024.2.1 atau lebih baru |
| JDK | 17 atau lebih baru |
| Android SDK | Sesuai konfigurasi Gradle project |
| Xcode | 15 atau lebih baru untuk target iOS di macOS |

## Konfigurasi lokal

`local.properties` hanya diperlukan untuk konfigurasi lokal Android SDK jika Android Studio tidak membuatnya otomatis.

Contoh minimal:

```properties
sdk.dir=/path/to/Android/Sdk
```

Project ini tidak membutuhkan langkah `cp local.properties.example local.properties` sebagai bagian utama setup. Token Gemini tidak perlu ditulis di `local.properties`; token diatur dari Settings aplikasi.

## Setup pertama

```bash
git clone https://github.com/sinavarasina/Proyek-Pengembangan-Aplikasi-Mobile.git
cd Proyek-Pengembangan-Aplikasi-Mobile
chmod +x ./gradlew
```

Buka project di Android Studio, biarkan Gradle sync selesai, lalu jalankan salah satu task build di bawah.

## Build shared metadata

```bash
./gradlew :composeApp:compileKotlinMetadata
```

## Build Android debug

```bash
./gradlew :androidApp:assembleDebug
```

Install ke emulator atau device:

```bash
./gradlew :androidApp:installDebug
```

## Build Android release lokal

```bash
./gradlew :androidApp:assembleRelease   -PbuildProfile=release   -PbuildTarget=Android   -Papp.versionName=1.0.0
```

Release signed resmi sebaiknya memakai GitHub Actions karena membutuhkan signing secrets.

## Build iOS simulator

Di macOS:

```bash
./gradlew :composeApp:compileKotlinIosSimulatorArm64
```

Atau buka:

```text
iosApp/iosApp.xcodeproj
```

Entry point iOS memakai SwiftUI host yang memanggil `MainViewController()` dari shared Compose module.

## Konfigurasi AI di aplikasi

1. Jalankan aplikasi.
2. Buka Settings.
3. Masukkan AI API token.
4. Pilih model dan personality jika diperlukan.
5. Buka AI Assistant.

Jika token kosong, Gemini service akan mengembalikan error bahwa token belum diatur di Settings.

## Build dengan metadata versi

```bash
./gradlew :androidApp:assembleDebug   -Papp.versionName=1.0.1   -PbuildProfile=debug   -PbuildTarget=Android
```

Metadata build ditampilkan melalui About screen.
