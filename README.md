# 📚 StudyMate


Aplikasi mobile multiplatform (Android-first) untuk mahasiswa yang membantu **mencatat materi kuliah**, **merangkum otomatis dengan AI**, **membuat soal latihan**, dan **mengingatkan jadwal belajar** berdasarkan kalender ujian pribadi.

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

StudyMate hadir sebagai teman belajar digital bagi mahasiswa ITERA. Dengan StudyMate, mahasiswa dapat:

1. **Mencatat materi kuliah** per mata kuliah secara terstruktur
2. **Merangkum catatan otomatis** menggunakan **Gemini API** — hemat waktu belajar sebelum ujian
3. **Membuat soal latihan otomatis** dari catatan yang sudah dibuat — belajar lebih aktif dan efektif
4. **Mengatur jadwal ujian** dan menerima **pengingat belajar cerdas** berdasarkan jarak hari menuju ujian
5. **Melihat riwayat belajar** — pantau progress dan konsistensi belajar harian

---

## ✨ Fitur

### Minimum (Wajib)
- [ ] **Onboarding & Profil Mahasiswa** — Input data: nama, NIM, program studi, semester — disimpan lokal dengan SQLDelight
- [ ] **Manajemen Mata Kuliah** — Tambah, edit, hapus mata kuliah beserta informasi SKS dan dosen pengampu
- [ ] **Catatan per Mata Kuliah** — Buat catatan dengan judul, isi teks, dan tanggal; tampil per mata kuliah
- [ ] **Jadwal Ujian** — Input jadwal UTS/UAS per mata kuliah; tampil dalam bentuk daftar terurut berdasarkan tanggal terdekat
- [ ] **Pengingat Belajar** — Notifikasi lokal otomatis H-7, H-3, dan H-1 sebelum ujian
- [ ] **Riwayat Belajar** — Catat sesi belajar (mata kuliah, durasi, tanggal) dan tampilkan statistik mingguan
- [ ] **Navigasi Multi-Screen** — Minimal 5 layar: Onboarding, Home/Dashboard, Catatan, Jadwal Ujian, Profil
- [ ] **State Management** — MVVM + StateFlow untuk semua UI state
- [ ] **Minimal 10 unit tests** + 3 UI tests, coverage > 50%
- [ ] **Koin DI** — Dependency injection setup

### Bonus (Target)
- [ ] **AI Summarizer (+10%)** — Gunakan Gemini API untuk merangkum catatan menjadi poin-poin penting secara otomatis
- [ ] **AI Quiz Generator (+10%)** — Generate soal pilihan ganda / esai dari isi catatan menggunakan Gemini API
- [ ] **Offline First (+5%)** — Semua catatan dan jadwal tetap dapat diakses tanpa koneksi internet; sinkronisasi saat online
- [ ] **Dark Mode (+5%)** — Support tema gelap/terang dengan Material 3
- [ ] **Animasi (+5%)** — Animasi transisi antar screen, animasi loading saat proses AI, animasi progress belajar

**Total target bonus: +35%**

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
│   ├── Remote: Ktor + Gemini API (AI)            │
│   └── Local:  SQLDelight (catatan, jadwal, profil)│
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
│   │   ├── dao/               # NoteDao, SubjectDao, ExamDao, StudySessionDao
│   │   ├── entity/            # NoteEntity, SubjectEntity, ExamEntity
│   │   └── datastore/         # Preferences (tema, onboarding state)
│   ├── remote/
│   │   ├── api/               # GeminiService (summarize, quiz generate)
│   │   └── dto/               # GeminiRequest, GeminiResponse
│   └── repository/            # Repository implementations
├── domain/
│   ├── model/                 # Note, Subject, Exam, StudySession, Quiz
│   ├── repository/            # Repository interfaces
│   └── usecase/
│       ├── SummarizeNoteUseCase.kt
│       ├── GenerateQuizUseCase.kt
│       ├── GetUpcomingExamsUseCase.kt
│       └── ScheduleStudyReminderUseCase.kt
└── presentation/
    ├── navigation/             # NavHost, Routes
    ├── theme/                  # Material3 Colors, Typography, Dark Mode
    ├── components/             # Reusable composables (SubjectChip, QuizCard, CountdownBadge)
    └── screens/
        ├── onboarding/         # Input profil mahasiswa
        ├── home/               # Dashboard: ringkasan ujian terdekat + aktivitas terkini
        ├── notes/              # Daftar & detail catatan per mata kuliah
        ├── exam/               # Jadwal ujian + countdown
        ├── quiz/               # Tampilan soal latihan hasil generate AI
        └── profile/            # Profil + riwayat belajar + statistik
```

---

## 🛠️ Tech Stack

| Komponen | Teknologi |
|----------|-----------|
| **Framework** | Kotlin Multiplatform, Compose Multiplatform |
| **Architecture** | MVVM, Clean Architecture, Repository Pattern |
| **Async** | Coroutines, Flow, StateFlow |
| **Networking** | Ktor Client + Kotlinx Serialization |
| **AI** | Google Gemini API (gemini-2.0-flash) |
| **Local Storage** | SQLDelight (catatan, jadwal, profil) |
| **Preferences** | DataStore (tema, onboarding flag) |
| **Notifikasi** | AlarmManager + NotificationManager (Android) |
| **DI** | Koin |
| **Testing** | kotlin.test, MockK, Turbine, Compose Test |
| **CI/CD** | GitHub Actions |

---

## 🗂️ Sprint Plan

| Sprint | Minggu | Target | PIC |
|--------|--------|--------|-----|
| **Sprint 1** | W11 | Planning, repo setup, CI/CD, README | Berdua |
| **Sprint 2** | W12 | Onboarding screen, profil mahasiswa (SQLDelight), navigasi 5 screen, Home Dashboard UI | Hafid |
| **Sprint 2** | W12 | Manajemen mata kuliah & catatan (CRUD), domain models, use cases | Joyapul |
| **Sprint 3** | W13 | Jadwal ujian + countdown, pengingat belajar (local notification) | Hafid |
| **Sprint 3** | W13 | Gemini API integration: AI Summarizer + Quiz Generator | Joyapul |
| **Sprint 4** | W14 | Dark mode, animasi UI, riwayat & statistik belajar | Hafid |
| **Sprint 4** | W14 | Unit tests & UI tests, bug fixes, offline-first cache | Joyapul |
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
   - Pilih run configuration `composeApp` di Android Studio, atau:
   ```bash
   ./gradlew :composeApp:installDebug
   ```

### Menjalankan Tests
```bash
./gradlew allTests
./gradlew :composeApp:testDebugUnitTest
```

---

## 📡 API Reference

### Gemini API
- **Base URL:** `https://generativelanguage.googleapis.com`
- **Endpoint:** `POST /v1beta/models/gemini-2.0-flash:generateContent`
- **Model:** `gemini-2.0-flash` (gratis tier)
- Digunakan untuk:
  - **Summarize** — Merangkum isi catatan menjadi poin-poin utama
  - **Quiz Generate** — Membuat soal latihan pilihan ganda/esai dari catatan

#### Contoh Prompt AI Summarizer
```
Kamu adalah asisten belajar untuk mahasiswa. Rangkum catatan berikut menjadi
maksimal 5 poin utama yang padat dan mudah dipahami, dalam Bahasa Indonesia.

Catatan:
{isi_catatan}
```

#### Contoh Prompt AI Quiz Generator
```
Berdasarkan catatan berikut, buatkan 5 soal pilihan ganda dengan 4 opsi jawaban (A/B/C/D)
beserta kunci jawaban. Format output dalam JSON.

Catatan:
{isi_catatan}
```

---

## 📝 Logika Pengingat Belajar

Pengingat dikirim secara otomatis berdasarkan jarak hari menuju ujian:

| Jarak ke Ujian | Jenis Pengingat | Pesan |
|----------------|-----------------|-------|
| H-7 | Notifikasi ringan | "Ujian [MK] 1 minggu lagi. Mulai review catatan sekarang!" |
| H-3 | Notifikasi sedang | "3 hari lagi ujian [MK]. Coba kerjakan soal latihan!" |
| H-1 | Notifikasi kuat | "Besok ujian [MK]! Buka ringkasan AI dan cek soal latihan." |
| H-0 | Notifikasi hari H | "Hari ini ujian [MK]. Semangat! 💪" |


Pengingat diimplementasikan menggunakan **AlarmManager** (Android) dengan `expect/actual` pattern untuk mendukung multiplatform.

---

## 📊 Struktur Data Utama

### Note
```kotlin
data class Note(
    val id: String,
    val subjectId: String,
    val title: String,
    val content: String,
    val summary: String?,       // Hasil AI summarizer
    val createdAt: Long,
    val updatedAt: Long
)
```

### Exam
```kotlin
data class Exam(
    val id: String,
    val subjectId: String,
    val type: ExamType,         // UTS / UAS / KUIS
    val date: Long,
    val location: String?,
    val notes: String?
)
```

### Quiz
```kotlin
data class Quiz(
    val id: String,
    val noteId: String,
    val questions: List<QuizQuestion>,
    val generatedAt: Long
)

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctAnswer: Int      // Index jawaban benar (0-3)
)
```

---

## 📄 Lisensi

MIT License — dibuat untuk keperluan pembelajaran Pengembangan Aplikasi Mobile ITERA.
