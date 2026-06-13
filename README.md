# MoveIn
[![CI](https://github.com/Kaizenix/123140169-123140186-MoveIn/actions/workflows/ci.yml/badge.svg)](https://github.com/Kaizenix/123140169-123140186-MoveIn/actions/workflows/ci.yml)  

![Kotlin](https://img.shields.io/badge/Kotlin-Multiplatform-purple)
![Compose](https://img.shields.io/badge/Compose-Multiplatform-blue)
![Platform](https://img.shields.io/badge/Platform-Android-green)
![Architecture](https://img.shields.io/badge/Architecture-MVVM-orange)

## About MoveIn

**MoveIn** merupakan aplikasi mobile yang membantu pengguna menemukan berbagai rekomendasi aktivitas menarik saat merasa bosan, jenuh, stres, atau kurang produktif.
Pengguna dapat memilih mood yang sedang dirasakan, lalu aplikasi akan memberikan rekomendasi aktivitas yang sesuai secara interaktif dan menyenangkan.

Aplikasi ini dirancang untuk membantu pengguna tetap aktif, produktif, dan memiliki kegiatan positif setiap hari.

---

## Demonstrasi Aplikasi MoveIn

**Link Youtube : https://youtu.be/osOMGAx2U6U?si=jL2OJUy7P6WyCAAX**

# Team Members

| Nama                | NIM       | GitHub                   |
| ------------------- | --------- | ------------------------ |
| Muhamad Arif Ardani | 123140186 | @Kaizenix                |
| Raisya Syifa Saleh  | 123140169 | @14-169-RaisyaSyifaSaleh |

---

# Target Users

* Mahasiswa
* Pelajar
* Remaja
* Pengguna yang sering merasa bosan atau kurang produktif

---

# Main Features

## Authentication

* Login akun
* Register akun baru
* Logout akun

## Mood Selection

Pengguna dapat memilih mood yang sedang dirasakan:

* Bosan
* Sedih
* Capek
* Semangat
* Gabut
* Stress

## Random Activity Generator

Aplikasi memberikan rekomendasi aktivitas secara acak seperti:

* Mendengarkan podcast
* Jalan santai 10 menit
* Menonton film
* Merapikan meja belajar
* Membaca artikel menarik
* Mencoba resep sederhana

Fitur tambahan:

* Tombol **“Acak Lagi”** untuk mendapatkan rekomendasi baru

## Activity Categories

Aktivitas dibagi menjadi beberapa kategori:

* Produktif
* Hiburan
* Self-improvement
* Kesehatan
* Sosial
* Santai

## Daily Challenge

Contoh challenge harian:

* Minum air 2 liter
* Tidak rebahan selama 1 jam
* Membaca 5 halaman buku

Reward:

* Badge
* Poin

## Favorite Activity

* Menyimpan aktivitas favorit
* Melihat kembali aktivitas yang disukai

## Activity History

* Menyimpan riwayat aktivitas pengguna
* Melihat progress aktivitas harian

## Quotes & Motivation

* Menampilkan quotes motivasi harian
* Membantu meningkatkan semangat pengguna

## Statistics Page

Menampilkan:

* Jumlah challenge selesai
* Mood paling sering dipilih
* Aktivitas favorit pengguna

---

# Project Architecture

Project menggunakan:

* MVVM Architecture
* Clean Architecture
* Repository Pattern

## Folder Structure

```bash
composeApp/
└── src/commonMain/kotlin/com/movein/
    ├── data/
    ├── domain/
    ├── presentation/
    ├── navigation/
    ├── di/
    └── components/
```

---

# Tech Stack

| Technology            | Description                |
| --------------------- | -------------------------- |
| Kotlin Multiplatform  | Cross-platform development |
| Compose Multiplatform | Modern UI Toolkit          |
| Ktor Client           | Networking                 |
| SQLDelight            | Local Database             |
| DataStore             | Preferences Storage        |
| Koin                  | Dependency Injection       |
| Coroutines & Flow     | Async Programming          |

---

# Sprint Planning

## Sprint 1 — Planning & Setup
### Completion Checklist
| Task | Status | Description |
| :--- | :---: | :--- |
| Project idea & requirements | ✅ Done | Ide utama aplikasi MoveIn telah ditentukan, yaitu aplikasi rekomendasi aktivitas berdasarkan mood pengguna. |
| Setup GitHub repository | ✅ Done | Repository GitHub telah dibuat sebagai tempat kolaborasi dan version control project. |
| Setup KMP project | ✅ Done | Project telah menggunakan Kotlin Multiplatform dan Compose Multiplatform sebagai basis pengembangan aplikasi. |
| Setup Clean Architecture | ✅ Done | Struktur project telah dirancang menggunakan pendekatan Clean Architecture dengan pemisahan layer data, domain, presentation, navigation, dan dependency injection. |
| Setup CI/CD | ✅ Done | GitHub Actions telah dikonfigurasi untuk menjalankan proses build otomatis setiap ada push atau pull request. |
| Create README documentation | ✅ Done | Dokumentasi README telah dibuat berisi deskripsi project, fitur, teknologi, arsitektur, sprint planning, dan cara menjalankan project. |

## Sprint 2 — Core Features
### Completion Checklist
| Task | Status | Description |
| :--- | :---: | :--- |
| Implement login & register | ✅ Done | Halaman login dan register telah dibuat, dilengkapi validasi input sederhana, serta terhubung ke navigation flow aplikasi. |
| Mood selection page | ✅ Done | Pengguna dapat memilih mood seperti Bosan, Sedih, Capek, Semangat, Gabut, dan Stress. |
| Random activity generator | ✅ Done | Aplikasi dapat memberikan rekomendasi aktivitas secara acak berdasarkan mood yang dipilih pengguna. |
| Navigation setup | ✅ Done | Navigation telah menghubungkan halaman Login, Register, Main, Home, Journey, Detox, Growth, dan Profile. |
| Local data layer prototype | ✅ Done | Project telah memiliki struktur repository dan penyimpanan lokal sementara berbasis state/list untuk mendukung fitur activity history. |


## Sprint 3 — Advanced Features
### 3 Completion Checklist
| Task | Status | Description |
| :--- | :---: | :--- |
| Favorite activity | ✅ Done | Pengguna dapat menyimpan aktivitas ke daftar favorit dan menghapusnya kembali dari daftar favorit. |
| Activity history | ✅ Done | Aktivitas penting seperti favorite, daily challenge, breathing, dan generated activity dicatat ke Journey Log. |
| Daily challenge | ✅ Done | Aplikasi menyediakan daily challenge dengan reward momentum dan status penyelesaian harian. |
| Quotes feature | ✅ Done | Aplikasi menampilkan quotes motivasi dan menyediakan tombol untuk mengganti quote. |
| Ktor Client / API integration | ✅ Done | Project telah memiliki konfigurasi Ktor Client untuk request API dengan JSON serialization, logging, dan timeout. |
| Gemini AI integration | ✅ Done | Project telah memiliki Gemini API service untuk menghasilkan rekomendasi aktivitas berdasarkan mood pengguna. |

## Sprint 4 — Polish & Testing
### Completion Checklist
| Task | Status | Description |
| :--- | :---: | :--- |
| UI improvement | ✅ Done | Tampilan aplikasi dirapikan menggunakan card, spacing, rounded corner, typography hierarchy, dan visual status yang lebih konsisten. |
| Responsive design | ✅ Done | Layout dibuat lebih adaptif menggunakan responsive grid sederhana agar tampilan tetap rapi pada layar compact maupun wide. |
| Unit testing | ✅ Done | Unit test ditambahkan untuk menguji logic growth phase berdasarkan nilai momentum pengguna. |
| Bug fixing | ✅ Done | Perbaikan dilakukan pada flow login/register, navigation, sprint feature section, dan integrasi state agar aplikasi lebih stabil. |
| Error state handling | ✅ Done | Komponen reusable untuk empty state dan error state telah ditambahkan. |
| CI test integration | ✅ Done | Workflow GitHub Actions diperbarui agar menjalankan build dan unit test secara otomatis. |

## Sprint 5 — Final Preparation
### Completion Checklist
| Task | Status | Description |
| :--- | :---: | :--- |
| Final Bug Fixing | ✅ Done | Semua bug sisa pada integrasi Koin, SQLite, Auth flow, dan state management telah dibersihkan sehingga aplikasi stabil tanpa crash. |
| Presentation Slides (PDF/PPTX) | ✅ Done | Slide presentasi telah disusun sesuai standar komponen (Problem, Solution, Tech Stack, Live Demo, Architecture Diagram, & Challenges). |
| Demo Script & Practice | ✅ Done | Script simulasi demo berdurasi 3 menit telah ditulis dan dipraktikkan bersama tim minimal 2 kali untuk sinkronisasi live-clik emulator. |
| Build Release APK | ✅ Done | Proses Keystore generation dan konfigurasi signingConfigs di gradle selesai. Signed Release APK telah berhasil di-compile melalui `./gradlew assembleRelease`. |
| App Versioning | ✅ Done | Menerapkan Semantic Versioning (MAJOR.MINOR.PATCH) dengan konfigurasi versionCode 1 dan versionName "1.0.0" pada build.gradle.kts. |
| Finalize README | ✅ Done | Dokumentasi README MoveIn telah dilengkapi dengan visual folder structure, tech stack, cara setup/run, serta tautan unduhan file APK. |
| Video Backup Recording | ✅ Done | Rekaman video backup jalannya aplikasi telah disiapkan sebagai antisipasi jika terjadi kendala teknis (lost connection/lag) pada layar proyektor saat Demo Day. |
| Submission E-Learning | ✅ Done | Seluruh berkas deliverable berupa tautan GitHub Repo aktif, file PDF/PPTX Slide, dan Signed APK telah diunggah ke portal e-learning ITERA sebelum deadline. |

---

## Project Plan (Sprint 1 - 5) — MoveIn

| Minggu ke- | Aktivitas | Penanggung Jawab |
| :---: | :--- | :--- |
| **W11** | Sprint 1: Project setup & rancangan arsitektur | Raisya & Ardani|
| **W12** | Sprint 2: Core feature (UI, Navigation, Data Layer lokal) | Raisya & Ardani |
| **W13** | Sprint 3: Advanced feature (Ktor Client / API integration & Gemini AI) | Raisya & Ardani |
| **W14** | Sprint 4: Polish UI, handle error state, dan Unit Testing | Raisya & Ardani |
| **W15** | Sprint 5: Final preparation, bug fixing, dan optimasi aplikasi | Raisya & Ardani |
| **W16** | UAS: Final Demo Day presentasi proyek | Raisya & Ardani |


# Setup Project

## Clone Repository

```bash
git clone https://github.com/Kaizenix/123140169-123140186-MoveIn.git
```

## Open Project

* Open project menggunakan Android Studio
* Sync Gradlea2
* Run pada emulator atau device Android

---

# Status Project
✅ **Sprint 1 Completed — Planning & Setup**  
✅ **Sprint 2 Completed — Core Features**  
✅ **Sprint 3 Completed — Advanced Features**  
✅ **Sprint 4 Completed — Polish & Testing**  
✅ **Sprint 5 Completed — Final Preparation**

# License

This project is developed for academic purposes at Institut Teknologi Sumatera (ITERA).
