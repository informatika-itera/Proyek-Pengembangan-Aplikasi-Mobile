# MoveIn

![Kotlin](https://img.shields.io/badge/Kotlin-Multiplatform-purple)
![Compose](https://img.shields.io/badge/Compose-Multiplatform-blue)
![Platform](https://img.shields.io/badge/Platform-Android-green)
![Architecture](https://img.shields.io/badge/Architecture-MVVM-orange)

## About MoveIn

**MoveIn** merupakan aplikasi mobile yang membantu pengguna menemukan berbagai rekomendasi aktivitas menarik saat merasa bosan, jenuh, stres, atau kurang produktif.
Pengguna dapat memilih mood yang sedang dirasakan, lalu aplikasi akan memberikan rekomendasi aktivitas yang sesuai secara interaktif dan menyenangkan.

Aplikasi ini dirancang untuk membantu pengguna tetap aktif, produktif, dan memiliki kegiatan positif setiap hari.

---

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

## ❤avorite Activity

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

## print 1 — Planning & Setup

* Project idea & requirements
* Setup GitHub repository
* Setup KMP project
* Setup Clean Architecture
* Setup CI/CD
* Create README documentation

## Sprint 2 — Core Features

* Implement login & register
* Mood selection page
* Random activity generator
* Navigation setup

## Sprint 3 — Advanced Features

* Favorite activity
* Activity history
* Daily challenge
* Quotes feature

## Sprint 4 — Polish & Testing

* UI improvement
* Responsive design
* Unit testing
* Bug fixing

## Sprint 5 — Final Preparation

* Final testing
* Presentation preparation
* Final documentation

---

## Project Plan (Sprint 1 - 5) — MoveIn

| Minggu ke- | Aktivitas | Penanggung Jawab |
| :---: | :--- | :--- |
| **W11** | Sprint 1: Project setup & rancangan arsitektur | Raisya |
| **W12** | Sprint 2: Core feature (UI, Navigation, Data Layer lokal) | Ardani |
| **W13** | Sprint 3: Advanced feature (Ktor Client / API integration & Gemini AI) | Raisya |
| **W14** | Sprint 4: Polish UI, handle error state, dan Unit Testing | Ardani |
| **W15** | Sprint 5: Final preparation, bug fixing, dan optimasi aplikasi | Raisya |
| **W16** | UAS: Final Demo Day presentasi proyek | Ardani & Raisya |


# Setup Project

## Clone Repository

```bash
git clone https://github.com/Kaizenix/123140169-123140186-MoveIn.git
```

## Open Project

* Open project menggunakan Android Studio
* Sync Gradle
* Run pada emulator atau device Android

---

# Status Project

🚧 Currently in Development — Sprint 1 Planning & Setup

---

# License

This project is developed for academic purposes at Institut Teknologi Sumatera (ITERA).
