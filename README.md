# SoundLetter 🎵✉️
*Express your emotions anonymously with the perfect soundtrack.*

SoundLetter adalah aplikasi mobile **Kotlin Multiplatform (KMP)** inovatif yang menggabungkan ekspresi emosional melalui pesan anonim dengan integrasi musik pintar. Terinspirasi dari tren "Send the Song", SoundLetter memungkinkan pengguna mengirimkan "surat digital" yang dilengkapi dengan lagu sebagai representasi perasaan mereka secara anonim di platform Android dan iOS.

[![Download APK](https://img.shields.io/badge/Download-APK-green?style=for-the-badge&logo=android)](https://drive.google.com/file/d/134SwvWaqlfksseD-An112zAO_0rF5MI8/view?usp=sharing)

---

## 📺 Video Demo
Klik thumbnail di bawah ini untuk melihat demo aplikasi SoundLetter di YouTube:

[![Tonton Video Demo](https://img.youtube.com/vi/QGcQY_dNWg/0.jpg)](https://youtu.be/QGXcQY_dNWg?si=6Es0jr8UJNRlS316)

---

## 📸 Tampilan Aplikasi

| Halaman | Light Mode | Dark Mode |
| :--- | :---: | :---: |
| **Home** | <img src="Screenshot/Home%20LM.jpg" width="200"> | <img src="Screenshot/Home%20DM.jpg" width="200"> |
| **Search** | <img src="Screenshot/Search%20LM.jpg" width="200"> | <img src="Screenshot/Search%20DM.jpg" width="200"> |
| **Input Data** | <img src="Screenshot/Input%20data%20LM.jpg" width="200"> | <img src="Screenshot/Input%20data%20DM.jpg" width="200"> |
| **History** | <img src="Screenshot/History%20LM.jpg" width="200"> | <img src="Screenshot/History%20DM.jpg" width="200"> |
| **Setting** | <img src="Screenshot/Setting%20LM.jpg" width="200"> | <img src="Screenshot/Setting%20DM.jpg" width="200"> |

---

## 🚀 Fitur Utama

### 1. Anonymous Messaging
Sampaikan perasaan Anda tanpa hambatan. Pengguna dapat mengirimkan pesan atau "curhatan" secara anonim. Form pengiriman dirancang minimalis, hanya membutuhkan nama penerima dan isi pesan, sementara identitas pengirim tetap terjaga kerahasiaannya.

### 2. Mood-Driven AI Recommender (Gemini 2.5 Flash Lite)
Fitur unggulan yang menggunakan **Gemini 2.5 Flash Lite** untuk menganalisis sentimen dan isi pesan pengguna secara real-time. AI bertugas mengekstrak "Mood" atau "Genre Tags" dari teks curhatan, yang kemudian digunakan untuk mencari lagu paling relevan secara otomatis.

### 3. Indie Music Integration (Jamendo API)
Integrasi dengan **Jamendo API** menggunakan parameter `fuzzytags` untuk mendapatkan metadata lagu berdasarkan mood yang dihasilkan AI.
*   **Native Audio Player:** Pemutar musik kustom yang terintegrasi langsung di dalam aplikasi.
*   **Vinyl Animation:** Antarmuka pemutar musik estetik dengan animasi piringan hitam (Vinyl) yang berputar saat lagu dimainkan.

### 4. Global Feed & Interactive Search
*   **Global Feed:** Menampilkan daftar pesan terbaru secara publik dengan desain kartu *Glassmorphism*.
*   **Inbox Search:** Fitur pencarian dinamis untuk menemukan pesan berdasarkan nama penerima atau potongan lirik lagu.

---

## 🛠️ Arsitektur & Tech Stack

Aplikasi ini dibangun dengan standar industri **Clean Architecture** (Domain, Data, Presentation) untuk memastikan kode yang *scalable* dan mudah diuji.

*   **Multiplatform Engine:** [Kotlin Multiplatform (KMP)](https://kotlinlang.org/docs/multiplatform.html)
*   **UI Framework:** Compose Multiplatform (Android & iOS)
*   **Dependency Injection:** Koin
*   **Networking:** Ktor Client (Content Negotiation, Logging, & MockEngine)
*   **Local Database:** SQLDelight (Type-safe SQL)
*   **Asynchronous:** Kotlin Coroutines & StateFlow
*   **Build Config:** BuildKonfig (Safe API Key Management)
*   **Testing:** Kover, Turbine, & Compose UI Test

---

## 🏆 Kualitas Kode & Pengujian
SoundLetter menjamin stabilitas melalui pengujian menyeluruh:

*   **Unit Testing:** **31+ Unit Tests (Passed)** mencakup Business Logic pada ViewModel dan Repository menggunakan *Ktor MockEngine* dan *Turbine*.
*   **UI Testing:** Implementasi otomatisasi pengujian antarmuka menggunakan *ComposeTestRule* untuk validasi interaksi user.
*   **Code Coverage (Kover):**
    *   **Class Coverage:** 68.3%
    *   **ViewModel Line Coverage:** 97% (Menjamin logika UI state yang sangat stabil).

![Hasil Coverage](Screenshot/Hasil%20Coverage.jpeg)

---

## 📂 Struktur Proyek Utama

```
composeApp/src/commonMain/kotlin/com/soundletter/app/
├── data/                # Data Layer (Ktor API, SQLDelight, Implementasi Repository)
├── domain/              # Domain Layer (Business Logic & Interface Repository)
├── presentation/        # Presentation Layer (UI & ViewModels)
│   ├── components/      # Reusable UI (Vinyl Animation, Audio Player)
│   ├── navigation/      # Compose Navigation Multiplatform
│   └── screens/         # Feature Screens (Home, Compose, Search, History)
└── di/                  # Dependency Injection Modules (Koin)
```

---

## ⚙️ Cara Setup & Menjalankan Proyek

### 1. Clone Repository
```bash
git clone https://github.com/15-040-GianIvander/SoundLetter.git
```

### 2. Konfigurasi API Key
Dapatkan API Key dan masukkan ke dalam file `local.properties` di root project:
```properties
GEMINI_API_KEY=your_gemini_api_key_here
JAMENDO_CLIENT_ID=your_jamendo_client_id_here
```

### 3. Build & Run
*   Buka proyek di **Android Studio Ladybug** atau versi terbaru.
*   Lakukan **Gradle Sync**.
*   Jalankan target `composeApp` untuk Android.
*   Untuk iOS, buka `iosApp/iosApp.xcworkspace` melalui Xcode atau jalankan langsung dari Android Studio jika plugin KMP terpasang.

---

## 👥 Tim Pengembang (Kelompok Sprint 4)

* **(123140027) ATALIE SALSABILA — Project Architect & Documentation Lead**
* **(123140039) MUHAMMAD DZAKY — Lead UI/UX Developer & Quality Assurance**
* **(123140040) GIAN IVANDER — Systems Integrator & Core Logic Developer**

---
Copyright © 2024 SoundLetter Team.
