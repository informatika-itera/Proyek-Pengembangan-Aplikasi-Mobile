
# My Bawang Gacha

<p align="center">
  <a href="https://youtu.be/UAeIq0xMsbk">
    <img src="https://img.youtube.com/vi/UAeIq0xMsbk/maxresdefault.jpg" alt="Video Demo My Bawang Gacha" width="70%">
    <br>
    <b>Watch video demo on YouTube</b>
  </a>
</p>

<p align="center">
  <a href="https://github.com/sinavarasina/Proyek-Pengembangan-Aplikasi-Mobile/actions/workflows/KMP-ci.yml">
    <img src="https://github.com/sinavarasina/Proyek-Pengembangan-Aplikasi-Mobile/actions/workflows/KMP-ci.yml/badge.svg" alt="CI">
  </a>
  <a href="https://github.com/sinavarasina/Proyek-Pengembangan-Aplikasi-Mobile/releases/latest">
    <img src="https://img.shields.io/github/v/release/sinavarasina/Proyek-Pengembangan-Aplikasi-Mobile?label=latest%20release" alt="Latest Release">
  </a>
  <a href="https://github.com/sinavarasina/Proyek-Pengembangan-Aplikasi-Mobile/releases">
    <img src="https://img.shields.io/github/downloads/sinavarasina/Proyek-Pengembangan-Aplikasi-Mobile/total?label=downloads" alt="Total Downloads">
  </a>
  <a href="https://github.com/sinavarasina/Proyek-Pengembangan-Aplikasi-Mobile/blob/main/LICENSE">
    <img src="https://img.shields.io/github/license/sinavarasina/Proyek-Pengembangan-Aplikasi-Mobile" alt="License">
  </a>
</p>

<p align="center">
  <a href="https://github.com/sinavarasina/Proyek-Pengembangan-Aplikasi-Mobile/releases/latest">
    <b>Download latest release</b>
  </a>
</p>

My Bawang Gacha adalah aplikasi Kotlin Multiplatform untuk menemukan, mencari, menyimpan, dan mengatur anime/manga. Aplikasi ini menyediakan discover list, detail media, library lokal, catatan, AI assistant berbasis Gemini, serta sistem gacha rekomendasi anime/manga berdasarkan preferensi pengguna.

Project ini memakai Compose Multiplatform sebagai UI shared untuk Android dan iOS, Clean Architecture, MVVM, Koin, SQLDelight, DataStore, Ktor Client, Jikan API, Gemini API, dan GitHub Releases API.

## Fitur utama

| Fitur                | Ringkasan                                                                                                              |
| -------------------- | ---------------------------------------------------------------------------------------------------------------------- |
| Home dan Discover    | Menampilkan anime dan manga populer, musim berjalan, upcoming, rekomendasi, dan watch episode dari Jikan API.          |
| Anime dan Manga List | Menampilkan list media dengan pagination, kategori, filter, dan pull-to-refresh.                                       |
| Search               | Mencari anime/manga dengan debounce, filter media type, genre, score, status, dan format.                              |
| Detail Media         | Menampilkan detail anime/manga, poster, sinopsis, metadata, relasi, episode anime, dan aksi tambah ke library.         |
| My Library           | Menyimpan anime/manga lokal dengan status, progress episode/chapter, skor personal, dan catatan.                       |
| Gacha                | Menghasilkan rekomendasi acak berdasarkan preferensi, deck session, histori, dan pengecualian item yang sudah dikenal. |
| Notes                | CRUD catatan lokal dengan kategori, warna, pin, dan integrasi ke AI assistant.                                         |
| AI Assistant         | Chat Gemini dengan konteks aplikasi, pilihan model, personality, token usage tracking, dan histori lokal.              |
| Settings dan About   | Tema, color scheme, mode jaringan, API token AI, cache, build info, release info, dan metadata runtime.                |

## Platform

| Platform | Status                                                                                                          |
| -------- | --------------------------------------------------------------------------------------------------------------- |
| Android  | Target utama. Build debug dan release tersedia melalui Gradle.                                                  |
| iOS      | Target KMP tersedia melalui Xcode project dan ComposeUIViewController. Build simulator bersifat optional di CI. |

## Menjalankan project

Kebutuhan utama:

* Android Studio Ladybug 2024.2.1 atau lebih baru
* JDK 17 atau lebih baru
* Android SDK
* Xcode 15 atau lebih baru untuk target iOS di macOS

Android Studio biasanya membuat `local.properties` secara otomatis untuk `sdk.dir`. Project ini tidak membutuhkan instruksi manual `cp local.properties.example local.properties` untuk Gemini. Token Gemini diatur dari Settings aplikasi.

Build dari terminal:

```bash
./gradlew :composeApp:compileKotlinMetadata
./gradlew :composeApp:compileAndroidMain
./gradlew :androidApp:assembleDebug
```

Install debug ke perangkat Android:

```bash
./gradlew :androidApp:installDebug
```

Build iOS simulator dari macOS:

```bash
./gradlew :composeApp:compileKotlinIosSimulatorArm64
```

Panduan lengkap ada di [docs/CARA_MENJALANKAN.md](docs/CARA_MENJALANKAN.md).

## Struktur repository

```text
.
├── androidApp/                 Android application module
├── iosApp/                     Xcode project dan entry point iOS
├── composeApp/                 Shared Kotlin Multiplatform module
│   └── src/
│       ├── commonMain/         Domain, data, presentation, resources, SQLDelight
│       ├── androidMain/        actual implementation Android
│       ├── iosMain/            actual implementation iOS
│       └── commonTest/         Unit tests shared
├── docs/                       Dokumentasi project
├── .github/workflows/          CI, dry build, dan release automation
├── VERSIONING.md               Kebijakan versi dan release
└── PLAN.md                     Status fitur dan roadmap project
```

Dokumentasi kode ada di [docs/code/INDEX.md](docs/code/INDEX.md).

## Arsitektur

Project menggunakan pemisahan layer berikut:

```text
Presentation -> Domain -> Data
```

Presentation berisi screen, ViewModel, navigation, theme, dan komponen UI. Domain berisi model, repository contract, dan use case. Data berisi implementasi repository, local source, remote source, cache, DTO, mapper, DataStore, dan SQLDelight.

Detail arsitektur ada di [docs/ARSITEKTUR.md](docs/ARSITEKTUR.md) dan [docs/STRUKTUR_KODE.md](docs/STRUKTUR_KODE.md).

## Konfigurasi AI

Token Gemini disimpan melalui Settings aplikasi. Runtime membaca token dari DataStore terlebih dahulu, kemudian memakai fallback platform `ApiConfig` jika tersedia. Jika keduanya kosong, AI Assistant akan menampilkan error bahwa token belum diatur.

Dokumentasi AI ada di [docs/AI_ASSISTANT.md](docs/AI_ASSISTANT.md).

## CI dan release

CI berjalan pada push dan pull request ke branch `main`. Workflow melakukan build Android, compile metadata shared, compile unit test sources, dan compile iOS simulator secara optional.

Release workflow membuat signed Android APK, checksum SHA-256, optional iOS simulator artifact, tag `v<version>`, dan GitHub Release.

Detail ada di [docs/CI_RELEASE.md](docs/CI_RELEASE.md) dan [VERSIONING.md](VERSIONING.md).

## Dosen pengampu

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/mh4Scripts">
        <img src="https://github.com/mh4Scripts.png" width="100px;" alt="Pak Habib" style="border-radius:50%;"/><br />
        <sub><b>Pak Habib</b></sub>
      </a><br />
      <sub>mh4Scripts</sub>
    </td>
  </tr>
</table>

## Anggota kelompok

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/sinavarasina">
        <img src="https://github.com/sinavarasina.png" width="100px;" alt="Varasina Farmadani" style="border-radius:50%;"/><br />
        <sub><b>Varasina Farmadani</b></sub>
      </a><br />
      <sub>NIM. 123140107</sub>
    </td>
    <td align="center">
      <a href="https://github.com/Faiq1818">
        <img src="https://github.com/Faiq1818.png" width="100px;" alt="Faiq Ghozy Erlangga" style="border-radius:50%;"/><br />
        <sub><b>Faiq Ghozy Erlangga</b></sub>
      </a><br />
      <sub>NIM. 123140139</sub>
    </td>
  </tr>
</table>

**Program Studi Teknik Informatika**
Institut Teknologi Sumatera (ITERA)

## License

MIT License

