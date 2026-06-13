# Troubleshooting

## Gradle tidak menemukan Android SDK

Pastikan Android Studio sudah membuka project dan membuat `local.properties`. Jika belum, buat manual:

```properties
sdk.dir=/path/to/Android/Sdk
```

Jangan commit `local.properties`.

## AI Assistant menampilkan token belum diatur

Buka Settings dan isi AI API token. Token tidak perlu diatur melalui `local.properties`.

## Jikan request gagal atau lambat

Kemungkinan penyebab:

- koneksi tidak stabil
- rate limit Jikan
- network mode di Settings sedang Offline Only
- endpoint Jikan sedang bermasalah

Cek Logcat untuk log request Ktor bila debugging Android.

## Data lama tetap muncul setelah refresh

Beberapa repository memakai cache agar UI tetap menampilkan konten saat refresh gagal. Gunakan Settings untuk membersihkan cache bila ingin memaksa data baru.

## iOS simulator compile gagal di CI

Build iOS simulator bersifat optional pada CI. Android dan common build tetap menjadi check utama. Perbaiki iOS secara terpisah di macOS/Xcode.

## Release gagal karena signing

Pastikan secrets berikut tersedia:

```text
ANDROID_RELEASE_KEYSTORE_BASE64
ANDROID_RELEASE_KEYSTORE_PASSWORD
ANDROID_RELEASE_KEY_ALIAS
ANDROID_RELEASE_KEY_PASSWORD
```

Pastikan keystore base64 valid dan alias sesuai.

## Release gagal karena format tag

Tag release harus berbentuk:

```text
v1.0.0
v1.0.0-rc4
```

Workflow akan menolak tag yang tidak sesuai pola release.

## SQLDelight error setelah schema berubah

Pastikan migration ditambahkan sesuai perubahan schema dan schema version build info diperbarui bila diperlukan. Jalankan:

```bash
./gradlew :composeApp:compileKotlinMetadata
```

## Compose resource tidak ter-load

Lakukan clean build:

```bash
./gradlew clean
./gradlew :androidApp:assembleDebug
```
