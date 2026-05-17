# FinTrack - Smart Personal Finance Tracker 🚀
![CI](https://github.com/Novro/Proyek-Pengembangan-Aplikasi-Mobile/actions/workflows/ci.yml/badge.svg?branch=project/123140094-123140092-FinTrack)

**FinTrack** adalah aplikasi manajemen keuangan personal berbasis mobile multiplatform yang dirancang untuk membantu pengguna mengelola finansial secara lebih cerdas, modern, dan terukur. Dengan integrasi data kurs *real-time* dan teknologi **Artificial Intelligence (AI)**, FinTrack bukan sekadar buku kas digital, melainkan asisten finansial pribadi dalam genggaman.

Proyek ini dibuat untuk memenuhi Tugas Besar mata kuliah **Pengembangan Aplikasi Mobile** - Program Studi Teknik Informatika, Institut Teknologi Sumatera.

---

## 👥 Tim Kelompok & Peran
| Nama | NIM | GitHub Username | Role |
| --- | --- | --- | --- |
| Ivan Nandira Mangunang | 123140094 | @V4nzz | Fullstack Developer |
| Ahmad Aufamahdi Salam | 123140092 | @Novro | Fullstack Developer |

---

## 📌 Deskripsi Proyek
Dalam era digital, fluktuasi nilai mata uang dan aset digital terjadi sangat cepat. **FinTrack** hadir untuk menjembatani kebutuhan pencatatan harian dengan kondisi ekonomi global secara *real-time*. Aplikasi ini fokus pada implementasi arsitektur yang bersih (*Clean Architecture*), pengelolaan *state* yang adaptif, serta integrasi API eksternal dan kecerdasan buatan.

---

## ✨ Fitur Aplikasi

### Fitur Minimum (Wajib)
* **📊 Multi-Asset Dashboard:** Pantau saldo sekaligus pergerakan kurs mata uang asing (USD, EUR, JPY) dan aset kripto (BTC, ETH) secara langsung (Minimal 5 *screens* dengan Material Design 3).
* **💸 Smart Expense Logger:** Fitur CRUD (Create, Read, Update, Delete) untuk mencatat pengeluaran dan pemasukan harian.
* **📈 Visualisasi Data:** Grafik pengeluaran berdasarkan kategori (Makanan, Transportasi, dsb) menggunakan penanganan *state* yang responsif.
* **🗺️ Multi-Screen Navigation:** Perpindahan antar halaman yang aman dengan mekanisme *argument passing*.
* **🧪 Automated Testing:** Implementasi minimal 10 unit tests dan 3 UI tests dengan *coverage* kode > 50%.

### Fitur Bonus (Rencana Pengembangan)
* **🤖 AI Financial Assistant (Gemini AI API):** Fitur konsultasi interaktif untuk analisis kebiasaan belanja dan tips hemat yang dipersonalisasi (+10% Nilai).
* **🔄 Automated CI/CD:** Pengujian otomatis menggunakan GitHub Actions (+5% Nilai).
* **📦 Dependency Injection:** Setup Koin DI sejak awal proyek (+10% Nilai Bonus Sprint 1).
* **🌙 Dark Mode Support:** Dukungan tema gelap dan terang untuk kenyamanan visual pengguna (+5% Nilai).

---

## 🛠️ Stack Teknologi
Sesuai dengan standarisasi arsitektur proyek, FinTrack mengadopsi komponen teknologi berikut:
* **Core Framework:** Kotlin Multiplatform (KMP) & Compose Multiplatform.
* **Architecture:** Clean Architecture (Presentation, Domain, dan Data Layer) dengan MVVM Pattern.
* **Asynchronous & Flow:** Kotlin Coroutines & StateFlow untuk manajemen *state* UI.
* **Networking:** Ktor Client & Kotlinx Serialization (Koneksi ke ExchangeRate API, CoinGecko API, dan Gemini AI API).
* **Local Storage:** SQLDelight & DataStore Preferences (Offline-First Ready).
* **Dependency Injection:** Koin DI.
* **Testing Tool:** kotlin.test, MockK, Turbine, dan Compose Test.

---

## 📅 Rencana Pengembangan (Sprint Planning)
Untuk memastikan pengerjaan selesai tepat waktu sebelum UAS Demo Day, proyek ini dibagi menjadi 5 fase *sprint*:

* **Sprint 1 (W11): Planning & Setup** ➔ Finalisasi kebutuhan proyek, perancangan arsitektur folder, inisialisasi repositori, dan integrasi awal CI/CD GitHub Actions (Fase Sekarang).
* **Sprint 2 (W12): Core Features** ➔ Pembuatan struktur halaman utama (UI Compose), setup navigasi antar halaman, dan implementasi awal data layer (CRUD Lokal).
* **Sprint 3 (W13): Advanced Features** ➔ Integrasi API kurs eksternal menggunakan Ktor Client dan implementasi modul kecerdasan buatan Gemini AI.
* **Sprint 4 (W14): Polish & Testing** ➔ Pemolesan antarmuka (UI Polish), penambahan animasi, penanganan *bug*, serta penulisan unit testing dan UI testing.
* **Sprint 5 (W15): Final Preparation** ➔ Optimasi performa aplikasi secara menyeluruh dan persiapan demo final untuk UAS.

---

## 🚀 Cara Menjalankan Proyek di Lokal
1. **Clone Repositori:**
   ```bash
   git clone -b project/123140094-123140092-FinTrack [https://github.com/Novro/Proyek-Pengembangan-Aplikasi-Mobile.git](https://github.com/Novro/Proyek-Pengembangan-Aplikasi-Mobile.git)