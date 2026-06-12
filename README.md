# 🥗 FoodSaver

[![FoodSaver CI](https://github.com/rdtngh/123140089-123140125-FoodSaver/actions/workflows/build.yml/badge.svg?branch=project/123140089-123140125-FoodSaver)](https://github.com/rdtngh/123140089-123140125-FoodSaver/actions/workflows/build.yml?query=branch%3Aproject%2F123140089-123140125-FoodSaver)



> Track. Cook. Save Food.

FoodSaver adalah aplikasi mobile multiplatform berbasis Android-first yang membantu pengguna mencatat stok makanan, memantau tanggal kedaluwarsa, mendapatkan pengingat makanan yang hampir expired, serta memperoleh rekomendasi resep dari bahan yang tersedia agar makanan tidak terbuang.

---

## 👥 Tim Pengembang

| Nama | NIM | GitHub | Role |
| :--- | :--- | :--- | :--- |
| **Bening Apni Prameswari** | 123140089 | [@beningapniprameswari](https://github.com/beningapniprameswari) | Full Stack Developer |
| **Raditya Alrasyid Nugroho** | 123140125 | [@rdtngh](https://github.com/rdtngh) | Full Stack Developer |

**Mata Kuliah:** IF25-22017 Pengembangan Aplikasi Mobile  
**Dosen:** Pak Habib [mh4Scripts](https://github.com/mh4Scripts)  
**Institut:** Institut Teknologi Sumatera (ITERA)

---

## 📝 Deskripsi Aplikasi

**FoodSaver** adalah aplikasi mobile *Android-first* yang dirancang untuk membantu pengguna mengelola stok makanan di rumah secara efisien. Masalah utama yang ingin diselesaikan adalah banyaknya makanan yang terbuang karena lupa atau melewati tanggal kedaluwarsa.

Dengan FoodSaver, pengguna dapat:
- **Mencatat Stok:** Menyimpan data makanan lengkap dengan kategori, lokasi penyimpanan (kulkas/lemari), dan jumlahnya.
- **Pantau Kedaluwarsa:** Melihat status makanan secara *real-time* (Aman, Segera Masak, atau Sudah Kedaluwarsa).
- **Rekomendasi Resep:** Mendapatkan ide masakan berdasarkan bahan yang hampir kedaluwarsa menggunakan integrasi API.
- **Asisten AI:** Bertanya tentang tips penyimpanan makanan melalui fitur AI Chat.

---

## 🎬 Demo Aplikasi

**https://youtu.be/BHWKOMmYIwo**

----

## ✨ Fitur Aplikasi

### A. Minimum / Wajib
- [x] **Manajemen Stok Makanan:** CRUD (Create, Read, Update, Delete) data makanan.
- [x] **Status Kedaluwarsa Otomatis:** Perhitungan status berdasarkan tanggal hari ini.
- [x] **Expiry Alert & Food Calendar:** Visualisasi tanggal penting makanan dalam kalender.
- [x] **Detail Makanan:** Informasi lengkap tiap item termasuk catatan tambahan.
- [x] **Profile & Pengaturan:** Pengaturan profil pengguna dan preferensi aplikasi.
- [x] **Navigasi Multi-screen:** Menggunakan Bottom Navigation (Home, Calendar, Recipe, Profile).
- [x] **State Management:** MVVM dengan StateFlow untuk reaktivitas UI.
- [x] **Local Storage:** Implementasi SQLDelight untuk database dan DataStore untuk preferensi.
- [x] **CI/CD:** Pipeline otomatis menggunakan GitHub Actions.

### B. Advanced / Bonus
- [x] **Search & Filter:** Mencari stok makanan berdasarkan nama atau kategori.
- [x] **Recipe Integration:** Mencari resep dari *TheMealDB* dan *Indonesian Recipe API*.
- [x] **AI Assistant:** Chat bot bertenaga *Google Gemini AI* untuk tips dapur.
- [x] **Rule-based Local Fallback:** Menampilkan resep lokal jika koneksi internet/API bermasalah.
- [x] **UI Polish:** Implementasi Material 3, Dark Mode, serta state Loading/Empty/Error yang rapi.
- [x] **Quantity Formatter:** Penanganan cerdas tampilan angka (misal: 12.0 menjadi 12 pcs).

---

## 🏗️ Arsitektur

Aplikasi ini mengikuti prinsip **Clean Architecture** yang dipadukan dengan **MVVM Pattern** untuk menjaga kode tetap modular dan mudah diuji.

```text
Presentation Layer (UI)
├── Screens (Composable): Home, Recipe, AI, Calendar, dsb.
├── ViewModel: Mengelola logic UI dan StateFlow.
├── UI State: Representasi data yang ditampilkan di layar.
└── Navigation: Navigasi antar screen menggunakan Compose Navigation.

Domain Layer (Business Logic)
├── Models: Data classes utama (Food, Recipe, Note).
├── Repository Interfaces: Kontrak untuk akses data.
├── Use Cases: Logika bisnis spesifik (e.g., GetExpiredFoodsUseCase).
└── Engine: Logika perhitungan status kedaluwarsa.

Data Layer (Infrastructure)
├── Repository Implementation: Menggabungkan data lokal dan remote.
├── Local Data Source: SQLDelight (SQLite) & DataStore.
├── Remote Data Source: Ktor Client (Recipe API & Gemini API).
└── Mapper: Konversi antar DTO, Entity, dan Domain Model.

Core
└── Network, Injection (Koin), Utility, Theme.
```

---

## 📂 Struktur Folder

<details>
<summary><b>Klik untuk melihat struktur folder project</b></summary>

```text
composeApp/src/commonMain/kotlin/com/example/foodsaver/
├── core/               # Konfigurasi Network, DI (Koin), Utility
├── data/               # Implementasi Repository, API, Database, Mapper
│   ├── local/          # SQLDelight & DataStore
│   ├── remote/         # GeminiService, MealApiService
│   └── repository/     # Implementasi Interface Repository
├── domain/             # Business Logic, Model, Interface Repository
│   ├── model/          # Entity domain
│   └── usecase/        # Use Case spesifik
└── presentation/       # UI Layer
    ├── components/     # Reusable UI Components
    ├── navigation/     # Setup Navigasi
    ├── screens/        # Screen-screen utama (Home, Detail, dsb.)
    └── theme/          # Material 3 Theme & Colors
```
</details>

---

## 🛠️ Tech Stack

| Komponen | Teknologi |
| :--- | :--- |
| **Language** | Kotlin Multiplatform (KMP) |
| **UI Framework** | Compose Multiplatform |
| **Dependency Injection** | Koin |
| **Database** | SQLDelight |
| **Local Preferences** | Jetpack DataStore |
| **Networking** | Ktor Client |
| **Serialization** | Kotlinx Serialization |
| **Concurrency** | Coroutines & StateFlow |
| **Image Loading** | Coil3 |
| **Date & Time** | Kotlinx Datetime |
| **AI Integration** | Google Gemini API |
| **CI/CD** | GitHub Actions |

---

## 📈 Sprint Plan / Development Journey

| Sprint | Minggu | Target | Implementasi di FoodSaver |
| :--- | :--- | :--- | :--- |
| **Sprint 1** | W11 | Planning & Setup | Inisialisasi KMP, setup SQLDelight, Koin, dan CI/CD. |
| **Sprint 2** | W12 | Core Features | CRUD stok makanan, navigasi, dan integrasi database lokal. |
| **Sprint 3** | W13 | Advanced Features | Integrasi Recipe API, Gemini AI Chat, dan fitur Kalender. |
| **Sprint 4** | W14 | Polish & Testing | UI Refinement, penanganan error/empty state, dan Unit Testing. |
| **Sprint 5** | W15 | Final Preparation | Dokumentasi README final, pembuatan demo, dan APK build. |
| **UAS** | W16 | Final Demo Day | Final project & demo aplikasi. |

---

## 🚀 Setup & Cara Menjalankan

### Prerequisites
- **Android Studio Ladybug** atau versi terbaru.
- **JDK 17**.
- **Android SDK 35**.

### Langkah-langkah
1. **Clone Repository:**
   ```bash
   git clone https://github.com/rdtngh/123140089-123140125-FoodSaver.git
   cd FoodSaver
   ```
2. **Setup API Key (Opsional):**
   Buat file `local.properties` di root project jika belum ada, lalu tambahkan:
   ```properties
   GEMINI_API_KEY=your_google_gemini_api_key
   ```
   *Aplikasi tetap dapat berjalan tanpa API Key, namun fitur AI akan dinonaktifkan.*

3. **Build & Run (Android):**
   - **Linux/macOS:**
     ```bash
     ./gradlew :composeApp:installDebug
     ```
   - **Windows:**
     ```powershell
     .\gradlew :composeApp:installDebug
     ```

---

## 🔌 API Reference

1. **TheMealDB API / Indonesian Recipe API:**
   - Digunakan untuk mencari rekomendasi resep berdasarkan bahan makanan yang tersedia.
   - Endpoint: `www.themealdb.com/api/json/v1/1/`

2. **Google Gemini API:**
   - Digunakan untuk fitur **Asisten AI FoodSaver**.
   - Memberikan tips penyimpanan makanan dan saran memasak secara interaktif.

---

## 🧠 Logika Utama Aplikasi

### A. Logika Status Kedaluwarsa
Aplikasi menghitung selisih hari antara tanggal hari ini dengan tanggal kedaluwarsa:
- **Sudah Kedaluwarsa:** Selisih < 0 hari.
- **Kedaluwarsa Hari Ini:** Selisih == 0 hari.
- **Segera Masak:** 1 s/d 3 hari sebelum kedaluwarsa.
- **Aman:** > 3 hari sebelum kedaluwarsa.

### B. Logika Quantity Formatter
Menghindari tampilan desimal yang tidak perlu:
- `12.0` ditampilkan sebagai `12`
- `1.5` tetap ditampilkan sebagai `1.5`

### C. Recipe Fallback
Jika koneksi internet terputus atau API limit tercapai:
- Aplikasi akan menampilkan daftar resep lokal (Rule-based) yang tersimpan di dalam aset aplikasi agar pengguna tetap mendapatkan bantuan.

---

## 📱 App Preview

| Beranda | Tambah Makanan | Detail Makanan |
| :---: | :---: | :---: |
| <img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/49c601b9-251c-42f0-b4cd-ca486eb6583d" /> | <img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/136a4e3c-83dc-40d0-a105-91d59d3e820d" /> | <img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/96214d45-5cac-4efc-848f-f32395ab206e" /> |
| **Peringatan** | **Resep** | **Kalender** |
| <img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/d94d0474-fd9e-4e56-bff3-62886c3cfeab" /> | <img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/51355416-aa82-4bb0-9e06-ed959b5eac36" /> | <img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/9986e1d0-d95f-450d-b861-a1104e7df7e2" /> |
| **Profil** | **Asisten AI** | **Dark Mode** |
| <img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/6c0c5653-e24f-4443-b196-1266d9c3cd1a" /> | <img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/b6fa2c47-dc1b-4710-a451-90d084e08090" /> | <img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/fb5416ef-9879-4c66-8e13-efbcd113ef4d" /> |

---

## 🧪 Testing & Coverage

### Menjalankan Unit Test

```bash
.\gradlew :composeApp:test
```
<img width="1600" height="900" alt="image" src="https://github.com/user-attachments/assets/9b60c2a6-9134-4d5f-8cec-7cf7e5eeecef" />

Aplikasi FoodSaver sudah mencakup pengujian pada beberapa bagian utama berikut:

- **Logic Engine**
  - Menguji logic utama aplikasi, terutama validasi status kedaluwarsa makanan.
  - Contoh: menentukan apakah makanan masih aman, mendekati kedaluwarsa, atau sudah kedaluwarsa.

- **Utility / Helper**
  - Menguji beberapa fungsi bantuan yang digunakan dalam aplikasi.
  - Contoh: formatter angka, pengolahan teks, dan pembersihan teks resep.

- **Local Data / Datastore**
  - Menguji sebagian fungsi yang berkaitan dengan pengolahan data lokal.
  - Contoh: penyimpanan atau pemrosesan data sederhana yang digunakan oleh aplikasi.

> Catatan: Berdasarkan hasil Kover Coverage Report, bagian `domain.engine` sudah memiliki coverage paling tinggi, sedangkan beberapa bagian seperti `mapper`, `repository`, `remote API`, dan sebagian besar layer UI masih perlu ditambahkan pengujian lebih lanjut.

---

## 🛠️ GitHub Actions (CI/CD)
Project ini dilengkapi dengan workflow `.github/workflows/build.yml` yang secara otomatis melakukan:
- **Build Check:** Memastikan kode bisa dikompilasi dengan sukses.
- **Unit Testing:** Menjalankan semua test secara otomatis pada setiap *push* atau *pull request*.
- **Lint Check:** Memastikan kualitas kode terjaga.

---

## ⚠️ Batasan Saat Ini (Known Limitations)
- Aplikasi dioptimalkan untuk platform Android (Android-first).
- Notifikasi pengingat masih berbasis UI (Expiry Alert), belum menggunakan *Background WorkManager* untuk *Push Notification*.
- Fitur sinkronisasi cloud (Login) direncanakan untuk pengembangan tahap selanjutnya.

---

## 🔮 Rencana Masa Depan
- [ ] Implementasi Push Notification untuk pengingat otomatis.
- [ ] Sinkronisasi akun menggunakan Firebase/Supabase.
- [ ] Scan struk belanja (OCR) untuk input otomatis.
- [ ] Statistik bulanan tentang makanan yang terbuang.

---

## 📜 Lisensi
Distribusi di bawah **MIT License**. Dibuat untuk keperluan pembelajaran mata kuliah Pengembangan Aplikasi Mobile ITERA.
