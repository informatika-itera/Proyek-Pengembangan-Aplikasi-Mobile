# SoundLetter 🎵✉️

*SoundLetter* adalah aplikasi mobile inovatif yang menggabungkan ekspresi emosional melalui pesan anonim dengan integrasi musik. Terinspirasi dari tren "Send the Song", aplikasi ini memungkinkan pengguna untuk mengirimkan "surat digital" yang dilengkapi dengan lagu sebagai representasi perasaan mereka kepada orang lain secara anonim.

---

## 🚀 Fitur Utama

### 1. Anonymous Messaging (Compose)
Pengguna dapat mengirimkan pesan atau curhatan secara anonim tanpa perlu pendaftaran akun. Form terdiri dari nama penerima, isi pesan, dan identitas pengirim (opsional).

### 2. AI Song Recommender (Powered by Gemini AI)
Fitur unggulan yang menggunakan *Artificial Intelligence (Gemini API)* untuk menganalisis sentimen dan isi pesan pengguna, kemudian memberikan rekomendasi lagu yang paling relevan secara otomatis sebelum pesan dikirim.

### 3. Music Integration (Spotify API)
Integrasi dengan *Spotify Web API* untuk mencari metadata lagu, menampilkan album art, dan memberikan pratinjau lagu yang dipilih oleh pengirim atau disarankan oleh AI.

### 4. Global Feed & Search
* *Global Feed:* Menampilkan daftar pesan terbaru dari pengguna lain secara publik dalam desain kartu yang estetik.
* *Inbox Search:* Memungkinkan pengguna mencari pesan yang ditujukan khusus untuk nama mereka menggunakan fitur pencarian dinamis.

### 5. Aesthetic UI/UX (Techno-Modern Style)
Antarmuka dikembangkan menggunakan *Material 3* dengan tema *Dark Mode, gradasi biru elektrik, dan gaya *Glassmorphism untuk memberikan kesan futuristik dan modern.

---

## 🛠️ Arsitektur & Teknologi

Aplikasi ini dibangun dengan standar pengembangan industri modern untuk memenuhi kriteria akademik mata kuliah Pengembangan Aplikasi Mobile:

* *Language:* Kotlin
* *UI Framework:* Jetpack Compose (Declarative UI)
* *Architecture:* MVVM (Model-View-ViewModel) + Clean Architecture
* *Dependency Injection:* Koin
* *Asynchronous & State:* Kotlin Coroutines & StateFlow
* *Database & Backend:*
    * *Firebase Firestore:* Penyimpanan data pesan secara real-time.
    * *Room Database:* Penyimpanan lokal untuk fitur bookmark/favorit pesan.
* *Networking:* Retrofit & OkHttp (untuk akses Spotify & Gemini API).
* *AI Engine:* Google Generative AI SDK (Gemini Pro).

---

## 📂 Struktur Proyek Utama

Proyek ini mengikuti struktur Clean Architecture untuk memastikan kode yang mudah diuji (testable) dan dikelola:
```
com.soundletter.app/
├── data/                # Data Layer
│   ├── remote/          # API Service (Spotify, Gemini, Firebase)
│   ├── local/           # Room Database & DAOs
│   └── repository/      # Implementasi Repository
├── domain/              # Domain Layer (Business Logic)
│   ├── model/           # Data Classes (POJO)
│   └── repository/      # Interface Repository
├── ui/                  # Presentation Layer
│   ├── theme/           # Color, Type, Shape (Material 3 Customization)
│   ├── components/      # Reusable UI Components
│   ├── screen/          # Screens (Home, Compose, Detail, Search)
│   └── viewmodel/       # Logic & State Management (StateFlow)
├── di/                  # Dependency Injection Modules (Koin)
└── utils/               # Helper classes & Extensions
```
