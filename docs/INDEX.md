# Indeks Dokumentasi Proyek

Direktori `docs/` berisi panduan, aturan, dan dokumentasi teknis untuk pengembangan proyek aplikasi My Bawang Gacha. Berikut adalah indeks dan penjelasan singkat dari setiap berkas dokumentasi yang tersedia.

## Panduan Utama

### 1. [Panduan Proyek (PANDUAN_PROJECT.md)](PANDUAN_PROJECT.md)
Menyediakan informasi dasar terkait mata kuliah Pengembangan Aplikasi Mobile (PAM), tujuan proyek, pembagian tugas kelompok, kebutuhan sistem minimum, alur pengerjaan berbasis sprint (Sprint 1-5), rubrik penilaian, serta tips sukses kolaborasi.

### 2. [Struktur Kode dan Arsitektur (STRUKTUR_KODE.md)](STRUKTUR_KODE.md)
Menjelaskan arsitektur perangkat lunak yang diimplementasikan (Clean Architecture + MVVM), rincian struktur folder dan paket bersama (`id.my.sinanonym.mybawanggacha`), deskripsi tanggung jawab setiap layer (Domain, Data, Presentation), alur ketergantungan (dependency flow), penerapan expect/actual pattern, konfigurasi Dependency Injection menggunakan Koin, serta aturan penulisan kode (naming conventions).

### 3. [Cara Menjalankan Aplikasi (CARA_MENJALANKAN.md)](CARA_MENJALANKAN.md)
Panduan teknis langkah demi langkah untuk mengkloning repositori, mengatur konfigurasi API Key Gemini melalui berkas `local.properties`, melakukan kompilasi Gradle dari terminal, menjalankan aplikasi di emulator atau perangkat fisik Android, melakukan build target iOS di simulator macOS, serta panduan verifikasi manual aplikasi dan troubleshooting dasar.

### 4. [Aturan Modifikasi Template (ATURAN_MODIFIKASI.md)](ATURAN_MODIFIKASI.md)
Berisi regulasi ketat mengenai komponen kode yang wajib dimodifikasi (seperti nama paket, identitas aplikasi, skema SQLDelight, repositori, dan tema), komponen yang diperbolehkan untuk dimodifikasi (seperti use cases baru, prompt AI, dan dependensi tambahan), serta komponen yang dilarang keras untuk diubah strukturnya (seperti arsitektur folder utama, setup DI Koin, dan entry point platform).

### 5. [Alur Git dan Branching (GIT_WORKFLOW.md)](GIT_WORKFLOW.md)
Panduan kolaborasi menggunakan Git, meliputi format wajib penamaan branch kelompok dan individu, langkah kloning dan sinkronisasi dengan remote upstream, alur harian (daily workflow), tips menghindari conflict, serta konvensi penulisan pesan commit (commit message convention) sesuai tipe perubahan.

### 6. [Panduan Troubleshooting (TROUBLESHOOTING.md)](TROUBLESHOOTING.md)
Dokumentasi solusi untuk mengatasi masalah teknis yang sering muncul selama pengembangan, seperti kegagalan sinkronisasi Gradle, API Key tidak terdeteksi, error migrasi database SQLite, masalah rendering Compose Preview, kegagalan injeksi dependensi Koin, kegagalan push Git, serta tips debugging log jaringan Ktor.

---

## Dokumentasi Kode Spesifik (Direktori `docs/code/`)

### 1. [Pengujian Manual Sprint 2 (code/SPRINT2_MANUAL_TEST.md)](code/SPRINT2_MANUAL_TEST.md)
Daftar checklist pengujian manual untuk memverifikasi fitur inti pada tahapan Sprint 2, mencakup pengujian alur navigasi antar layar, operasi CRUD pada database library lokal, validasi input formulir, dan persistensi data lokal.

### 2. [Skema Warna Code Geass (code/color_palletes/code_geass/CODE_GEASS.md)](code/color_palletes/code_geass/CODE_GEASS.md)
Dokumentasi mengenai skema warna khusus yang diimplementasikan di aplikasi, yang terinspirasi dari karakter C.C (untuk tema terang) dan Lelouch Lamperouge (untuk tema gelap). Dokumen ini melampirkan tabel kode warna heksadesimal lengkap untuk setiap komponen Material Design 3 (MD3).
