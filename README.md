# My Bawang Gacha
> My Bawang Gacha adalah aplikasi Anime List dan Gacha yang dikembangkan menggunakan Kotlin Multiplatform.

## Fitur Utama

- Anime List: Menampilkan dan mengelola daftar anime.
- Gacha: Sistem gacha untuk mendapatkan judul atau karakter anime.
- Integrasi AI LLM: Memiliki sedikit integrasi AI menggunakan LLM untuk memberikan ringkasan atau fitur interaktif terkait anime.

## Persiapan dan Instalasi

### Kebutuhan Sistem

- Android Studio Ladybug (2024.2.1) atau versi lebih baru
- JDK 17+

### Cara Menjalankan

1. Clone repository ini ke komputer lokal Anda.
2. Atur konfigurasi `local.properties`. Jika belum ada, salin dari template atau buat file baru dan masukkan API key Anda.
   ```properties
   GEMINI_API_KEY=masukkan_api_key_anda_disini
   ```
3. Jalankan perintah build:
   ```bash
   ./gradlew build
   ```
4. Untuk menjalankan aplikasi di Android:
   ```bash
   ./gradlew :composeApp:installDebug
   ```

## Arsitektur

Proyek ini menggunakan Clean Architecture dan MVVM:
- UI: Compose Multiplatform
- Jaringan: Ktor Client
- Database Lokal: SQLDelight
- Dependency Injection: Koin
- AI: Google Gemini API

## Project Plan

Lihat detail progress di [PLAN.md](PLAN.md)

## Status Badge

![CI](https://github.com/sinavarasina/Proyek-Pengembangan-Aplikasi-Mobile/actions/workflows/ci.yml/badge.svg)

## Dosen Pengampu
### Pak Habib
[GitHub: mh4Scripts](https://github.com/mh4Scripts)

## Member Kelompok
### Varasina Farmadani (123140107)
[GitHub: sinavarasina](https://github.com/sinavarasina)
### Faiq Ghozy Erlangga (123140139)
[GitHub: Faiq1818](https://github.com/Faiq1818)

**Program Studi Teknik Informatika**  
Institut Teknologi Sumatera (ITERA)

## Lisensi

MIT License


