# 🐬 StudyMate

<img width="1024" height="543" alt="image" src="https://github.com/user-attachments/assets/c7546e6c-321d-4f36-9086-34ee10f5dcb2" />




Aplikasi mobile multiplatform (Android & iOS) yang dirancang untuk menjembatani celah antara **"mencatat"** dan **"memahami"**. Dibangun dengan Kotlin Multiplatform (KMP), StudyMate memanfaatkan **Gemini AI** untuk mengubah catatan kelas yang berantakan menjadi materi belajar yang terstruktur dan interaktif.

---

## 👥 Tim

| Nama | NIM | GitHub | Role |
|------|-----|--------|------|
| Tengku Hafid Diraputra | 123140043 | [@ThDptr](https://github.com/ThDptr) | Lead & Android Dev |
| Joyapul Hanscalvin Panjaitan | 119140157 | [@joyapul119140157](https://github.com/joyapul119140157) | FE Dev & QA |

**Mata Kuliah:** IF25-22017 Pengembangan Aplikasi Mobile
**Dosen:** Pak Habib ([@mh4Scripts](https://github.com/mh4Scripts))
**Institut:** Institut Teknologi Sumatera (ITERA)

---

## 📱 Deskripsi Aplikasi

StudyMate bekerja dalam **satu siklus pembelajaran yang utuh**, dari input mentah hingga pengujian pemahaman:

| # | Fase | Deskripsi |
|---|------|-----------|
| 1 | **Capture** | Masukkan catatan cepat saat kuliah berlangsung — fokus pada kecepatan, bukan kerapian |
| 2 | **Refine** | Gemini AI memproses teks: memperbaiki struktur, menambah konteks, dan menjelaskan istilah teknis |
| 3 | **Consistency** | Sistem Streaks memotivasi pengguna untuk mencatat minimal satu materi setiap hari |
| 4 | **Recall** | Catatan yang telah dirapikan diolah menjadi kuis otomatis untuk menguji pemahaman |

---

## ✨ Fitur

### Minimum (Wajib)
- [ ] **Onboarding & Profil Pengguna** — Input nama dan NIM; data disimpan lokal dengan SQLDelight
- [ ] **Smart Notes** — Manajemen catatan per mata kuliah dengan folder terorganisir
- [ ] **AI Smart Refine** — Perbaikan otomatis teks berantakan: struktur, bullet points, dan glosarium istilah teknis
- [ ] **Offline Access** — Catatan tersimpan lokal via SQLDelight, dapat diakses tanpa internet
- [ ] **AI Quiz** — Generate soal pilihan ganda dan flashcard otomatis dari catatan pengguna
- [ ] **Streak Tracker** — Visualisasi konsistensi belajar harian
- [ ] **Kalender & Planner** — Pencatatan jadwal ujian dan tenggat waktu tugas
- [ ] **Navigasi Multi-Screen** — Minimal 5 layar: Beranda, Catatan, Belajar, Kalender, Profil
- [ ] **State Management** — MVVM + StateFlow untuk semua UI state
- [ ] **Minimal 10 unit tests** + 3 UI tests, coverage > 50%
- [ ] **Koin DI** — Dependency injection setup

### Bonus (Target)
- [ ] **Dark Mode (+5%)** — Support tema gelap/terang dengan Material 3
- [ ] **Learning Heatmap (+5%)** — Visualisasi frekuensi belajar mirip contribution graph GitHub
- [ ] **Smart Reminder (+5%)** — Notifikasi cerdas beberapa hari sebelum ujian untuk sesi review
- [ ] **Animations (+5%)** — Transisi antar screen, loading saat proses AI, indikator progress

**Total target bonus: +20%**

---

## 🗂️ Navigasi & Halaman

### 🏠 1. Beranda (Dashboard)
Halaman utama yang memberikan gambaran harian pengguna.
- **Streak Tracker** — Visualisasi api streak konsistensi mencatat harian
- **Daily Mantra** — Pesan motivasi yang dapat diedit secara personal sebagai pengingat tujuan belajar
- **Quick Snippets** — Cuplikan catatan terbaru yang belum dirapikan AI sebagai pengingat tugas tertunda

### 📝 2. Catatan (Smart Notes)
Manajemen materi kuliah yang terorganisir.
- **Tombol + (FAB)** — Tombol aksi cepat di pojok layar untuk langsung membuat catatan baru tanpa perlu navigasi tambahan
- **Folder Matkul** — Pengelompokan catatan berdasarkan mata kuliah
- **AI Smart Refine** — Fitur inti untuk merapikan teks, menyusun bullet points, dan membuat glosarium otomatis
- **Offline Access** — Catatan disimpan lokal dengan SQLDelight

### 🧠 3. Belajar (AI Quiz)
Transformasi catatan menjadi sarana belajar aktif.
- **Auto-Quiz** — AI menghasilkan pertanyaan pilihan ganda atau flashcards dari catatan pengguna
- **Progress Study** — Lacak materi yang sudah dikuasai melalui hasil latihan soal

### 📅 4. Kalender (Planner)
Manajemen jadwal dan pengingat akademik.
- **Exam Schedule** — Pencatatan tanggal ujian atau tenggat waktu proyek
- **Smart Reminder** — Notifikasi cerdas sebelum ujian untuk menyarankan sesi review

### 👤 5. Profil & Riwayat (Identity)
Pusat informasi pengguna dan analitik belajar.
- **Manajemen Profil** — Informasi identitas mahasiswa (Nama & NIM)
- **Learning Heatmap** — Visualisasi frekuensi dan intensitas belajar selama satu semester
- **Theme Switcher** — Pengaturan Mode Terang / Mode Gelap

---

## 🏗️ Arsitektur

Menggunakan **Clean Architecture + MVVM** sesuai panduan mata kuliah.

```
┌─────────────────────────────────────────────────┐
│              PRESENTATION LAYER                  │
│   Screens (Composable) ◄──► ViewModel           │
│             (StateFlow / UDF Pattern)            │
└────────────────────┬────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────┐
│               DOMAIN LAYER                       │
│   Use Cases ◄──► Repository Interfaces          │
│           (Pure Kotlin, no framework)            │
└────────────────────┬────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────┐
│                DATA LAYER                        │
│   Repository Impl                               │
│   ├── Remote: Ktor + Gemini API (AI Refine/Quiz)│
│   └── Local:  SQLDelight (profil, catatan)      │
└─────────────────────────────────────────────────┘
```

### Struktur Folder

```
composeApp/src/commonMain/kotlin/com/studymate/
├── core/
│   ├── di/
│   │   └── AppModule.kt
│   ├── network/
│   │   ├── ApiConfig.kt
│   │   └── HttpClientFactory.kt
│   └── util/
│       ├── DatabaseDriverFactory.kt
│       └── Extension.kt
├── data/
│   ├── local/
│   │   ├── dao/               # SQLDelight DAOs
│   │   ├── entity/            # DB entities (UserProfile, Note, Streak)
│   │   └── datastore/         # Preferences (tema, onboarding flag)
│   ├── remote/
│   │   ├── api/               # GeminiService
│   │   └── dto/               # Response DTOs
│   └── repository/            # Repository implementations
├── domain/
│   ├── model/                 # Note, Quiz, UserProfile, Streak
│   ├── repository/            # Repository interfaces
│   └── usecase/               # RefineNoteUseCase, GenerateQuizUseCase, GetStreakUseCase
└── presentation/
    ├── navigation/             # NavHost, Routes
    ├── theme/                  # Material3 Colors, Typography, Dark Mode
    ├── components/             # Reusable composables (StreakCard, HeatmapView, QuizCard)
    └── screens/
        ├── home/               # Dashboard: streak, mantra, quick snippets
        ├── notes/              # Smart Notes + AI Refine
        ├── quiz/               # AI Quiz & progress tracking
        ├── calendar/           # Planner & Smart Reminder
        └── profile/            # Profil, heatmap, tema
```

---

## 🛠️ Tech Stack

| Komponen | Teknologi |
|----------|-----------|
| **Framework** | Kotlin Multiplatform, Compose Multiplatform |
| **Architecture** | MVVM, Clean Architecture, Repository Pattern |
| **Async** | Coroutines, Flow, StateFlow |
| **Networking** | Ktor Client + Kotlinx Serialization |
| **AI Engine** | Google Gemini API (`gemini-1.5-flash`) |
| **Local Storage** | SQLDelight (catatan, profil, streak) |
| **Preferences** | DataStore (tema, onboarding flag) |
| **DI** | Koin |
| **Testing** | kotlin.test, MockK, Turbine, Compose Test |
| **CI/CD** | GitHub Actions |

---

## 🗂️ Sprint Plan

| Sprint | Minggu | Target | PIC |
|--------|--------|--------|-----|
| **Sprint 1** | W11 | Planning, repo setup, CI/CD, README | Berdua |
| **Sprint 2** | W12 | Onboarding & profil (SQLDelight), navigasi 5 screen, Home UI | Anggota 1 |
| **Sprint 2** | W12 | Gemini API integration (Ktor), domain models, use cases | Anggota 2 |
| **Sprint 3** | W13 | Smart Notes screen + AI Refine feature | Anggota 1 |
| **Sprint 3** | W13 | AI Quiz generation, Streak Tracker logic | Anggota 2 |
| **Sprint 4** | W14 | Kalender & Planner, Dark Mode, animasi UI | Anggota 1 |
| **Sprint 4** | W14 | Unit tests & UI tests, Learning Heatmap, bug fixes | Anggota 2 |
| **Sprint 5** | W15 | Final fixes, dokumentasi, demo prep | Berdua |
| **UAS** | W16 | Demo Day 🎉 | Berdua |

---

## 🚀 Setup & Cara Menjalankan

### Prerequisites
- Android Studio Ladybug (2024.2.1) atau lebih baru
- JDK 17+
- Gradle 8.x

### Langkah Setup

1. **Clone repository**
   ```bash
   git clone https://github.com/ThDptr/StudyMate.git
   cd StudyMate
   ```

2. **Setup `local.properties`**
   ```bash
   cp local.properties.example local.properties
   # Edit local.properties dan isi:
   # GEMINI_API_KEY=your_key_here
   ```
   Dapatkan Gemini API key gratis di: https://aistudio.google.com/

3. **Build project**
   ```bash
   ./gradlew build
   # Atau hanya APK debug (lebih cepat):
   ./gradlew :composeApp:assembleDebug
   ```

4. **Run di Android**
   ```bash
   ./gradlew :composeApp:installDebug
   ```
   Atau pilih run configuration `composeApp` di Android Studio.

### Menjalankan Tests
```bash
./gradlew allTests
./gradlew :composeApp:testDebugUnitTest
```

---

## 📡 API Reference

### Gemini API
- **Model:** `gemini-1.5-flash` (gratis tier)
- Digunakan untuk:
  - **Smart Refine** — Merapikan dan memperkaya catatan mentah pengguna
  - **Auto-Quiz** — Menghasilkan pertanyaan pilihan ganda dan flashcards dari catatan
- Dokumentasi: https://ai.google.dev/docs

---

## 📄 Lisensi

MIT License — dibuat untuk keperluan pembelajaran Pengembangan Aplikasi Mobile ITERA.
