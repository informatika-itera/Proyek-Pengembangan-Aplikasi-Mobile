# 📱 EduMate
![CI](https://github.com/tinyDevill/Proyek-Pengembangan-Aplikasi-Mobile/actions/workflows/ci.yml/badge.svg)

## 👥 Team
- Abi Sholihan (Lead) - @tinyDevill
- Muhammad Ghama Al-Fajri (Dev) - @gahehe52

## 📝 Description
EduMate adalah aplikasi asisten belajar berbasis AI yang membantu mahasiswa mengelola tugas kuliah dan memahami materi pembelajaran dengan lebih efektif.

Aplikasi ini dikembangkan menggunakan Kotlin Multiplatform (KMP) sehingga dapat berjalan pada Android dan iOS menggunakan satu codebase yang sama.

## ✨ Features
- Smart Task Management (Menambah tugas, mengedit tugas, menghapus tugas, mengatus deadline tugas, dan mengatur prioritas tugas).
- AI Task Breakdown (Memanfaatkan Google Gemini untuk memecah tugas besar menjadi beberapa langkah kecil yang lebih mudah diselesaikan).
- Study Notes (Menyimpan catatan materi kuliah secara lokal sehingga dapat diakses tanpa koneksi internet)
- AI Summarizer (Merangkum materi atau artikel panjang menjadi poin-poin penting menggunakan Gemini AI)
- Search & Filter (Pencarian tugas, filter berdasarkan status, dan pengurutan berdasarkan deadline)

## 🛠 Tech Stack
KMP, Compose, Material 3, Koin, Ktor Client, SQLDelight, DataStore, Navigation Compose, Kotlin Coroutines, Kotlinx Serialization, Google Gemini API, Coil, Kotlin Test, Turbine

## 🏗 Architecture
Clean Architecture, MVVM (Model View-View Model)

## 🚀 Setup
1. Clone repository
2. Buka di Android Studio
3. Pada file 'local.properties' tambahkan 'GEMINI_API_KEY=your_gemini_api_key'
4. Jalankan di Emulator/Device

## 📍 API Endpoints
Aplikasi ini menggunakan 1 endpoint eksternal, yaitu:

POST https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent

Endpoint tersebut berfungsi untuk menghasilkan respons AI di seluruh fitur AI Assistant.

## 📝 Test Instructions
1. Setelah membuka project di Android Studio, buka terminal Android Studio
2. Jalankan ./gradlew :composeApp:testDebugUnitTest
3. Tunggu tes hingga selesai

## 📦 Download
- Github : https://github.com/tinyDevill/Proyek-Pengembangan-Aplikasi-Mobile/tree/project/123140182-123140192-EduMate
- APK    : https://drive.google.com/file/d/1qDf4ZNExtmD8KUEmhtPuscoEr_ejeCWY/view?usp=sharing

## 📸 Screenshots
- Halaman Beranda ![Beranda](https://drive.google.com/uc?export=view&id=1x_VYwyjM-cbysEXtd_9yJ37FK4A-6Vu8)
- Halaman Tambah Tugas ![Tambah Tugas](https://drive.google.com/uc?export=view&id=16ib-R9tPSNiRdqvJdTFKrYGHWV0a6zhq)
- Halaman Edit Tugas ![Edit Tugas](https://drive.google.com/uc?export=view&id=17nOg7SyZosTrgjUSsjf2WF76BTyEI3AR)
- Halaman Detail Tugas ![Detail Tugas](https://drive.google.com/uc?export=view&id=1HvHJs72gkQumVkV4dXSgNkmggd4KBykG)
- Halaman Pengaturan ![Pengaturan](https://drive.google.com/uc?export=view&id=1JSMGR4LILfeM3-YqiIznDn-jxWgzj_Er)
- Halaman Statistik dan Analitik ![Statistik](https://drive.google.com/uc?export=view&id=1mmPZDelJ4bJBCPr_4k13rmSu7PpbWIe4)
- Halaman Fokus Belajar ![Fokus Belajar](https://drive.google.com/uc?export=view&id=1L535tTOEM3QakUESp76mkopt96xb6Wqf)
- Halaman Profil ![Profil](https://drive.google.com/uc?export=view&id=1kUtWQUN2BlvePqgl7MT5pjeS8dGBsvjA)
- Halaman Fitur AI ![Profil](https://drive.google.com/uc?export=view&id=1bjA_luuDqE1arRZrPgUhPWPJqMvhNxzA)

## 🔗 Link YouTube
https://youtu.be/vs6bGL1QFC8

