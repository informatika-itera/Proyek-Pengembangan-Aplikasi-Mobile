# Hujjah

![CI](https://github.com/Awesome1209/Proyek-Pengembangan-Aplikasi-Mobile/actions/workflows/ci.yml/badge.svg)

## Logo

<p align="center">
<img width="300" height="300" alt="Presentation (1)" src="https://github.com/user-attachments/assets/f004c87f-19c4-474d-807c-6bf0c1dca7c7" />
</p>

## 📌 Project Overview

**Hujjah** adalah aplikasi mobile referensi Islam berbasis AI yang membantu pengguna membaca Al-Qur’an dan hadis secara manual, serta menemukan dalil berdasarkan kondisi sehari-hari melalui fitur **Hujjah Lens**.

Melalui Hujjah Lens, pengguna dapat menuliskan kondisi curhatan atau cerita yang kemudian akan memetakan input pengguna ke topik Islami yang relevan, menampilkan referensi Al-Qur’an, lalu menghubungkan ke halaman ayat Al-qur'an terkait.
> Hujjah bukan aplikasi fatwa dan bukan pengganti ulama. Hujjah adalah aplikasi pembelajaran referensi Islam berbasis Al-Qur’an dan hadis.

---

## 👥 Team

| Nama | NIM | GitHub Username                               | Role |
|---|---|-----------------------------------------------|---|
| AWI SEPTIAN PRASETYO | 123140201 | [@awesome1209](https://github.com/Awesome1209) | Lead / Mobile Developer |
| MUHAMMAD BIMASTIAR | 123140211 | [@211-Bimas](https://github.com/211-Bimas)    | API & Data Developer |

---

## 🎦 Vidio Sprint

## Vidio Demo

<div align="center">
  <a href="https://www.youtube.com/watch?v=rr8zjp12_hg">
    <img src="https://img.youtube.com/vi/rr8zjp12_hg/0.jpg" alt="Preview Aplikasi Mobile AI Islami" width="300">
  </a>
</div>

### Sprint 2

https://github.com/user-attachments/assets/8bc1e5dd-0d35-47af-b859-5827582aedf7

### Sprint 3

https://github.com/user-attachments/assets/3a352384-808b-4a4b-9306-eee70b3a3b75

---

## 🎯 Problem Statement

Banyak pengguna ingin mencari tuntunan Islam berdasarkan kondisi yang sedang mereka alami, tetapi sering tidak mengetahui:

- Surah atau ayat mana yang relevan.
- Hadis apa yang sesuai dengan kondisi tersebut.
- Kata kunci Islami apa yang tepat untuk dicari.
- Bagaimana menghubungkan dalil dengan solusi pembelajaran.

Aplikasi Al-Qur’an atau hadis biasanya mengharuskan pengguna mencari berdasarkan surah, ayat, kitab, atau kata kunci tertentu. Sementara itu, chatbot AI umum dapat menjawab secara bebas, tetapi belum tentu menampilkan referensi secara terstruktur.

**Hujjah** hadir untuk menghubungkan:

```text
Kondisi pengguna → Topik Islami → Dalil Al-Qur’an/Hadis → Solusi Berdalil
```

---

## 💡 Main MVP: Hujjah Lens

MVP utama aplikasi ini adalah **Hujjah Lens**.

Hujjah Lens adalah fitur pencarian dalil berbasis kondisi sehari-hari. Pengguna cukup menulis kondisi dengan bahasa bebas, lalu aplikasi akan:

1. Memahami input pengguna menggunakan AI.
2. Memetakan input ke topik Islami terdekat.
3. Menampilkan dalil Al-Qur’an dan hadis yang relevan.
4. Membuat ringkasan pembelajaran.
5. Menyusun **Solusi Berdalil** berdasarkan referensi yang ditemukan.
6. Memberikan opsi bookmark untuk menyimpan referensi penting.

Contoh:

```text
Input:
"Aku sering marah sama teman"

Output:
Topik:
Mengendalikan Amarah

Dalil Al-Qur’an:
QS Ali Imran: 134

Dalil Hadis:
Hadis “Jangan marah”

Solusi Berdalil:
1. Menahan amarah dan memilih memaafkan.
   Dasar: QS Ali Imran: 134

2. Tidak mengikuti dorongan emosi secara langsung.
   Dasar: Hadis “Jangan marah”
```

---

## 🚀 Features

### Minimum Features

- [x] **Hujjah Lens**
    - Input kondisi pengguna.
    - AI memetakan input ke topik Islami.
    - Menampilkan referensi Al-Qur’an dan hadis.
    - Menyusun Solusi Berdalil.

- [x] **Al-Qur’an Manual**
    - Menampilkan daftar surah.
    - Menampilkan detail surah.
    - Menampilkan ayat dan terjemahan Bahasa Indonesia.
    - Bookmark ayat.

- [x] **Hadis Manual**
    - Menampilkan daftar hadis atau hadis tematik.
    - Menampilkan detail hadis.
    - Bookmark hadis.

- [x] **Reference Detail**
    - Menampilkan detail referensi Al-Qur’an atau hadis.
    - Menampilkan teks Arab, terjemahan, sumber, dan topik.

- [x] **Bookmark Reference**
    - Menyimpan referensi Al-Qur’an dan hadis ke database lokal.
    - Menampilkan daftar referensi tersimpan.
    - Menghapus bookmark.

- [x] **Multi-screen Navigation**
    - Navigasi antar halaman menggunakan Compose Navigation.
    - Mendukung argument passing untuk detail referensi.

- [x] **State Management**
    - Menggunakan ViewModel.
    - Menggunakan StateFlow untuk UI state.

- [x] **Clean Architecture**
    - Memisahkan layer `presentation`, `domain`, dan `data`.

- [x] **Dependency Injection**
    - Menggunakan Koin.

- [x] **Testing**
    - Unit test untuk use case/repository.
    - UI test untuk screen utama.

---

### Bonus Features

- [x] **Gemini AI Integration**
    - AI digunakan untuk mapping input, ringkasan, dan Solusi Berdalil.

- [x] **Offline-first Bookmark**
    - Referensi yang sudah disimpan dapat diakses tanpa internet.

- [x] **Dark Mode**
    - Mendukung tema terang dan gelap.

- [x] **Meaningful Animations**
    - Animasi sederhana untuk transisi dan loading state.

- [x] **CI/CD**
    - GitHub Actions untuk build dan test otomatis.

---

## 🧠 AI Usage Policy

AI dalam Hujjah digunakan sebagai **reasoning & conversational layer** untuk meningkatkan pengalaman interaktif, bukan sebagai pembuat fatwa agama.

Sumber utama dalil dan teks suci tetap diambil secara langsung dari database lokal dan API resmi:
- Al-Qur'an (Teks Arab dan Terjemahan Kemenag RI)
- Hadits (9 Kitab Perawi Terkenal)

AI (Gemini API dengan model `gemini-flash-latest`) digunakan untuk:
- **Hujjah Lens:** Menjadi asisten obrolan spiritual yang hangat dan interaktif. AI memproses curahan hati atau pertanyaan pengguna, memberikan jawaban natural yang memotivasi, dan menyematkan rujukan dalil yang relevan secara kontekstual di dalam paragraf.
- **Navigasi Dalil Otomatis:** AI secara dinamis menyertakan tag navigasi tersembunyi `[NAVIGASI: QS. NamaSurah: Ayat (surahNumber: X, verseNumber: Y)]` yang diparsing oleh aplikasi untuk memunculkan tombol pintasan langsung menuju ayat/surah Al-Qur'an.
- **AI Note Assistant:** Membantu pengguna meringkas konten catatan, menyarankan judul catatan secara singkat dan kreatif, memperbaiki tata bahasa tulisan catatan, serta menghasilkan ide-ide menulis yang baru.

AI tidak digunakan untuk:
- Memberikan fatwa final atau menentukan hukum halal-haram secara bebas.
- Menggantikan posisi ulama/ustadz dalam penyelesaian masalah fikih atau akidah yang kompleks.
- Menafsirkan dalil secara bebas tanpa bersandar pada rujukan tepercaya.

Disclaimer:
> Obrolan dan saran dari Hujjah Lens bersifat pembelajaran dan pengingat spiritual umum, bukan fatwa hukum atau nasihat medis/keagamaan mutlak.

---

## 🧩 Khazanah Dalil Categories (Offline Curated)

Selain curhat interaktif lewat AI, pengguna juga dapat menjelajahi kategori dalil terkurasi secara offline di tab **Khazanah Dalil** (bersumber dari `SampleIslamicReferences`):

| Kategori | Deskripsi / Subtitel | Contoh Rujukan |
|---|---|---|
| Mengendalikan Amarah | Dalil saat emosi mulai menguasai diri | QS. Ali 'Imran: 134, HR. Bukhari (Jangan Marah) |
| Ketenangan Hati | Pengingat saat hati gelisah dan cemas | QS. Ar-Ra'd: 28, QS. Al-Insyirah: 5–6 |
| Sabar | Pegangan ketika menghadapi ujian | QS. Al-Baqarah: 153, QS. Az-Zumar: 10 |
| Taubat | Kembali kepada Allah tanpa putus asa | QS. Az-Zumar: 53, QS. At-Tahrim: 8 |
| Syukur | Mengingat nikmat dan karunia Allah | QS. Ibrahim: 7 |
| Shalat | Mengingat kewajiban tiang agama | QS. Al-Ma’un: 4–5, QS. Al-Baqarah: 45 |
| Tawakkal / Cemas | Pasrah dan percaya pada rencana Allah | QS. Ath-Thalaq: 3 |
| Ilmu | Keutamaan menuntut ilmu dan belajar | QS. Al-Mujadilah: 11 |
| Berbakti Orang Tua | Adab dan kewajiban kepada orang tua | QS. Al-Isra: 23 |
| Rezeki | Jaminan rezeki dan usaha yang berkah | QS. Hud: 6, QS. Ath-Thalaq: 2–3 |

---

## 🛠 Tech Stack

| Kebutuhan | Teknologi |
|---|---|
| Framework | Kotlin Multiplatform |
| UI | Compose Multiplatform, Material Design 3 |
| Architecture | MVVM, Clean Architecture, Repository Pattern |
| Async | Coroutines, Flow, StateFlow |
| Networking | Ktor Client |
| Serialization | Kotlinx Serialization |
| Local Storage | SQLDelight, DataStore Preferences |
| Dependency Injection | Koin |
| AI | Gemini API (`gemini-flash-latest` via REST API) |
| Testing | kotlin.test, MockK, Turbine, Compose Test, Robolectric |
| CI/CD | GitHub Actions |

---

## 🌐 API & Data Sources

### Al-Qur’an

Primary source:
```text
quran-api-id.vercel.app API
```
Digunakan untuk:
- Mengambil daftar 114 surah secara dinamis.
- Mengambil detail ayat dan terjemahan Bahasa Indonesia.
- Database lokal menggunakan **SQLDelight** sebagai *Single Source of Truth* (SSOT) agar aplikasi dapat bekerja sepenuhnya secara luring (*offline-first*).

---

### Hadis

Primary source:
```text
api.hadith.gading.dev API
```
Digunakan untuk:
- Mengambil daftar 9 kitab perawi hadits (Bukhari, Muslim, Tirmidzi, Nasai, Abu Daud, Ibnu Majah, Ahmad, Darimi, Malik).
- Mengambil riwayat hadits berdasarkan rentang nomor hadits secara dinamis.
- Database lokal menggunakan **SQLDelight** sebagai *Single Source of Truth* (SSOT) untuk mendukung akses luring (*offline-first*).

---

### AI

REST API Google AI Studio:
```text
https://generativelanguage.googleapis.com/v1beta
```
Digunakan untuk:
- Mengirim instruksi system prompt dan riwayat percakapan chat obrolan Hujjah Lens.
- Memproses analisis tulisan dan asisten catatan secara langsung menggunakan model `gemini-flash-latest`.

---

## 🧱 Architecture Overview

Hujjah menggunakan prinsip **Clean Architecture** dengan pemisahan layer sebagai berikut:

```text
Presentation Layer
UI, Composable, ViewModel, UI State

Domain Layer
Domain Model, Repository Interface, Use Case

Data Layer
Repository Implementation, Remote Data Source, Local Data Source, DTO, Entity
```

Dependency rule:

```text
Presentation → Domain ← Data
```

Domain tidak bergantung pada data layer maupun presentation layer.

---

## 📁 Project Structure

Struktur direktori aktual dari modul `composeApp` mengikuti arsitektur Clean Architecture:

```text
composeApp/src/commonMain/kotlin/com/example/hujjah/
├── core/
│   ├── di/
│   │   └── AppModule.kt                       # Koin Dependency Injection setup
│   ├── network/
│   │   ├── ApiConfig.kt                       # Ktor API endpoints & keys configuration (expect)
│   │   ├── HttpClientFactory.kt               # Ktor HTTP client provider
│   │   └── NetworkMonitor.kt                  # Network connection monitor (expect)
│   └── util/
│       └── NetworkUtil.kt                     # Utility helpers
├── data/
│   ├── local/
│   │   ├── datastore/
│   │   │   └── UserPreferences.kt             # Daily reading target & streak manager
│   │   └── entity/
│   │       ├── BookmarkMapper.kt              # SQLDelight Bookmark entity mapper
│   │       └── NoteMapper.kt                  # SQLDelight Note entity mapper
│   ├── remote/
│   │   ├── api/
│   │   │   └── GeminiService.kt               # Gemini API client interface
│   │   └── dto/
│   │       ├── GeminiDto.kt                   # Gemini API request/response models
│   │       └── HadithDto.kt                   # Hadith API response models
│   ├── repository/
│   │   ├── AIRepositoryImpl.kt                # AI Services repository implementation
│   │   ├── NoteRepositoryImpl.kt              # Note CRUD repository implementation
│   │   └── hujjah/
│   │       ├── BookmarkRepositoryImpl.kt      # Bookmark repository implementation
│   │       └── HujjahRepositoryImpl.kt        # Quran & Hadith offline-first repository
│   └── sample/
│       └── SampleIslamicReferences.kt         # Pre-defined offline dalil topics & references
├── domain/
│   ├── model/
│   │   ├── Note.kt                            # Note domain entity
│   │   └── islamic/
│   │       ├── BookmarkReference.kt           # Bookmark domain model
│   │       ├── ChatModels.kt                  # Lens Chat domain models
│   │       ├── HadithModels.kt                # Hadith domain models
│   │       ├── IslamicReference.kt            # Islamic curated reference domain model
│   │       ├── QuranModels.kt                 # Quran domain models
│   │       ├── SourceType.kt                  # Source Enum (QURAN, HADITH)
│   │       └── TopicOption.kt                 # Curated topic option model
│   ├── repository/
│   │   ├── AIRepository.kt                    # AI repository interface
│   │   ├── NoteRepository.kt                  # Note repository interface
│   │   └── hujjah/
│   │       ├── BookmarkRepository.kt          # Bookmark repository interface
│   │       └── HujjahRepository.kt            # Quran, Hadith, & Lens Chat repository interface
│   └── usecase/
│       └── NoteUseCases.kt                    # Note Usecases (GetAll, Search, Save, Delete, Summarize, etc.)
└── presentation/
    ├── components/
    │   ├── NoteComponents.kt                  # Reusable Note UI components
    │   └── hujjah/
    │       ├── HujjahComponents.kt            # Curated reference & Islamic UI components
    │       ├── HujjahShimmer.kt               # Loading placeholder animations
    │       └── HujjahSprint2MenuBar.kt        # Custom bottom navigation bar
    ├── navigation/
    │   ├── AppNavHost.kt                      # Compose navigation host & route handler
    │   └── Routes.kt                          # Route definitions & navigation actions
    ├── theme/
    │   ├── Color.kt                           # Gold, OLED Dark, & Clean Light colors
    │   ├── Theme.kt                           # App theme configuration
    │   └── Type.kt                            # Google Font Inter & Outfit typography
    └── screens/
        ├── addnote/                           # Add & Edit note screen & form validation
        ├── ai/                                # Gemini-based AI assistant for notes
        ├── bookmark/                          # Curated dalil saved bookmarks screen
        ├── detail/                            # Local note details view
        ├── hadith/                            # Hadith manual list (9 perawi) & range paginator
        ├── home/                              # Daily targets dashboard & reading streak tracker
        ├── lens/                              # Lens chatbot conversational screen
        ├── notes/                             # Local notes list & dynamic categories filter screen
        ├── profile/                           # Profile editing & settings
        ├── quran/                             # Quran surah list & search screen
        ├── reference/                         # Curated dalil details & explanation screen
        ├── result/                            # Curated dalil topics (Khazanah Dalil) offline screen
        └── splash/                            # Splash logo welcoming screen
```

---

## 📱 Halaman Aplikasi (Screens)

Aplikasi Hujjah memiliki halaman-halaman berikut yang terhubung melalui Compose Navigation:

| Halaman | Deskripsi |
|---|---|
| **Splash Screen** | Menampilkan animasi logo dan tagline Hujjah saat membuka aplikasi. |
| **Home Screen** | Halaman utama (dashboard) berisi target harian membaca (detik), streak harian, pengingat kutipan hari ini (*Quote of the Day*), dan tombol timer membaca. |
| **Hujjah Lens Screen** | Obrolan interaktif dengan asisten spiritual Islam berbasis AI (Gemini) untuk berkonsultasi secara natural dan mendapatkan dalil yang disematkan langsung di obrolan dengan tombol navigasi otomatis. |
| **Khazanah Dalil (Hujjah Result Screen)** | Menampilkan kumpulan dalil pilihan terkurasi yang dapat diakses secara luring (*offline*) berdasarkan kategori/tema yang dipilih. |
| **Al-Qur’an Screen** | Menampilkan daftar 114 surah dengan informasi jumlah ayat dan tempat turunnya surah, dilengkapi pencarian. |
| **Al-Qur’an Detail Screen** | Halaman membaca ayat Al-Qur'an secara penuh beserta terjemahan bahasa Indonesia, mendukung pelacakan rujukan otomatis. |
| **Hadits Screen** | Menyediakan akses manual terhadap 9 kitab perawi hadits (Bukhari, Muslim, dst.) dengan navigasi rentang hadits dinamis. |
| **Reference Detail Screen** | Menampilkan rincian ayat/hadits dari Khazanah Dalil lengkap dengan tafsir penjelasan singkat, serta opsi simpan bookmark. |
| **Bookmark Screen** | Menampilkan seluruh dalil Al-Qur'an dan hadits yang disimpan oleh pengguna ke dalam database lokal. |
| **Profile Screen** | Profil pengguna yang menampilkan target membaca harian, streak membaca aktif, serta pengaturan nama dan foto profil (base64 encoder). |
| **Notes Screen** | Menu **Catatan Harian Saya** bertema emas dengan filter chip kategori dinamis (diambil otomatis dari data tersimpan), fitur pin, pencarian, dan tambah catatan. |
| **Add/Edit Note Screen** | Form penulisan catatan harian dengan integrasi rujukan dalil (surah & ayat atau nomor hadits) dan asisten kecerdasan buatan (Gemini AI). |
| **Note Detail Screen** | Tampilan detail catatan harian pengguna berserta dalil rujukan yang ditautkan. |

Komponen navigasi bawah (*Bottom Navigation*):
```text
Hujjah Lens | Al-Qur’an | Hadis | Catatan | Profil
```

---

## 🗃 Skema Database Lokal (SQLDelight)

Database lokal digunakan sebagai *Single Source of Truth* (SSOT) untuk mendukung fitur luring (*offline-first*) dan menyimpan data pengguna. Skema tabel didefinisikan sebagai berikut:

### 1. Bookmark (com.example.hujjah.data.local.Bookmark)
Menyimpan rujukan dalil Al-Qur'an atau hadits yang ditandai oleh pengguna:
```sql
CREATE TABLE BookmarkEntity (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    reference_id TEXT NOT NULL UNIQUE,
    source_type TEXT NOT NULL,
    title TEXT NOT NULL,
    source_name TEXT NOT NULL,
    arabic_text TEXT NOT NULL,
    translation TEXT NOT NULL,
    explanation TEXT NOT NULL,
    topic_id TEXT NOT NULL,
    topic_title TEXT NOT NULL,
    note TEXT NOT NULL DEFAULT '',
    saved_at INTEGER NOT NULL
);
```

### 2. Catatan Harian (com.example.hujjah.data.local.Note)
Menyimpan tulisan catatan Islami pengguna beserta kategori dinamis:
```sql
CREATE TABLE NoteEntity (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    category TEXT NOT NULL DEFAULT 'GENERAL',
    color TEXT NOT NULL DEFAULT 'DEFAULT',
    is_pinned INTEGER NOT NULL DEFAULT 0,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL
);
```

### 3. Cache Data & Obrolan (com.example.hujjah.data.local.Hujjah)
Digunakan untuk cache luring data Quran/Hadits dan riwayat obrolan Hujjah Lens:
```sql
CREATE TABLE QuranSurahEntity (
    number INTEGER PRIMARY KEY NOT NULL,
    name TEXT NOT NULL,
    translation TEXT NOT NULL,
    numberOfVerses INTEGER NOT NULL,
    revelation TEXT NOT NULL,
    asma TEXT NOT NULL
);

CREATE TABLE QuranVerseEntity (
    surahNumber INTEGER NOT NULL,
    number INTEGER NOT NULL,
    arabic TEXT NOT NULL,
    translation TEXT NOT NULL,
    PRIMARY KEY (surahNumber, number)
);

CREATE TABLE HadithBookEntity (
    id TEXT PRIMARY KEY NOT NULL,
    name TEXT NOT NULL,
    totalHadith INTEGER NOT NULL
);

CREATE TABLE HadithEntity (
    bookId TEXT NOT NULL,
    number INTEGER NOT NULL,
    arab TEXT NOT NULL,
    translation TEXT NOT NULL,
    PRIMARY KEY (bookId, number)
);

CREATE TABLE LensChatEntity (
    id TEXT PRIMARY KEY NOT NULL,
    sender TEXT NOT NULL,
    text TEXT NOT NULL,
    timestamp INTEGER NOT NULL,
    referencesJson TEXT, 
    solutionsJson TEXT
);
```

---

## 🧪 Rencana & Implementasi Pengujian (Testing)

Pengujian dilakukan untuk menjaga kualitas kode dan alur aplikasi agar stabil serta terbebas dari regresi:

### 1. Unit & Flow Tests
Menguji logika bisnis, manipulasi state di ViewModel, penanganan data di repository, dan transformasi data flow:
*   **Notes & Add Note ViewModel:** Menguji inisialisasi status (*state*), penanganan form input, validasi masukan kosong, pinning catatan, serta pencarian catatan.
*   **AI Usecases & Repository:** Memvalidasi fitur pemanggilan Gemini API (`SummarizeNoteUseCase`, `ImproveWritingUseCase`, `GenerateIdeasUseCase`) menggunakan *repository mock/fake*.
*   **Database & Repositories:** Menguji alur CRUD pada data lokal dan respons *offline-first* dari `NoteRepository` dan `BookmarkRepository`.

### 2. UI Tests (Robolectric Compose UI Test)
Menguji interaksi antarmuka pengguna tanpa membutuhkan emulator Android fisik:
*   **Notes Screen UI flow:** Memastikan elemen UI termuat secara konsisten dengan tema Gold, verifikasi input pencarian menyaring item, dan interaksi chip filter memunculkan konten yang sesuai.
*   **Save Note UI flow:** Menyimulasikan pengetikan judul/konten catatan, pemilihan dalil, menekan tombol simpan, dan memverifikasi data masuk ke daftar utama.


---

## ⚙️ CI/CD

Project menggunakan GitHub Actions untuk menjalankan build dan test otomatis pada setiap push atau pull request.

Workflow file:

```text
.github/workflows/ci.yml
```

Target workflow:

- Checkout repository.
- Setup JDK 17.
- Run unit tests.
- Build debug APK.

---

## 🗓 Project Plan

| Sprint | Target |
|---|---|
| Sprint 1 | Planning, setup repository, CI/CD, README, architecture, API decision |
| Sprint 2 | Core screens, navigation, local database, bookmark CRUD |
| Sprint 3 | API integration, Gemini AI, Hujjah Lens, Solusi Berdalil |
| Sprint 4 | UI polish, bug fixing, testing, offline/cache improvement |
| Sprint 5 | Final APK, README final, demo script, presentation, video backup |

---

## 👨‍💻 Task Assignment

| Member | Responsibility |
|---|---|
| Awi Septian Prasetyo | Project setup, architecture, AI integration, Hujjah Lens |
| Muhammad Bimastiar | API/data, UI implementation, testing, documentation |

---

## 🧭 Setup Guide

### 1. Clone Repository

```bash
git clone https://github.com/Awesome1209/Proyek-Pengembangan-Aplikasi-Mobile.git
cd Proyek-Pengembangan-Aplikasi-Mobile
```

### 2. Checkout Project Branch

```bash
git checkout project/123140201-123140211-Hujjah
```

### 3. Open Project

Buka project menggunakan Android Studio:

```text
File → Open → pilih folder root project
```

### 4. Create local.properties

Buat file `local.properties` pada root project.

Isi:

```properties
GEMINI_API_KEY=YOUR_GEMINI_API_KEY
```

Jika diperlukan, Android Studio akan menambahkan SDK path secara otomatis.

Contoh Windows:

```properties
sdk.dir=C\:\\Users\\YourName\\AppData\\Local\\Android\\Sdk
GEMINI_API_KEY=YOUR_GEMINI_API_KEY
```

### 5. Build Project

Windows:

```bash
gradlew.bat :composeApp:assembleDebug
```

macOS/Linux:

```bash
./gradlew :composeApp:assembleDebug
```

### 6. Run App

Jalankan aplikasi melalui Android Studio menggunakan emulator atau device Android.

---

## 🌿 Branching Strategy

Branch utama:

```text
main
```

Branch project:

```text
project/123140201-123140211-Hujjah
```

Branch fitur:

```text
feature/hujjah-lens
feature/quran-screen
feature/hadith-screen
feature/bookmark
feature/gemini-ai
```

Workflow:

```bash
git checkout project/123140201-123140211-Hujjah
git pull origin project/123140201-123140211-Hujjah
git checkout -b feature/nama-fitur
```

Setelah selesai:

```bash
git add .
git commit -m "feat: implement nama fitur"
git push origin feature/nama-fitur
```

---

## ✅ Commit Convention

Format commit:

```text
<type>: <description>
```

Contoh:

```text
feat: add Hujjah Lens screen
feat: implement Quran list screen
feat: add bookmark database schema
fix: resolve Gradle sync issue
docs: update README with project plan
test: add unit tests for bookmark use case
refactor: separate repository interfaces
chore: add GitHub Actions workflow
```

Jenis commit:

| Type | Usage |
|---|---|
| feat | Menambahkan fitur baru |
| fix | Memperbaiki bug |
| refactor | Merapikan struktur kode tanpa mengubah behavior |
| test | Menambahkan atau memperbaiki test |
| docs | Mengubah dokumentasi |
| style | Format kode |
| chore | Maintenance project |

---

## ⚠️ Risk Management

| Risiko | Solusi |
|---|---|
| API Al-Qur’an bermasalah | Gunakan backup alquran.cloud atau data lokal ayat pilihan |
| API hadis tidak stabil | Gunakan data lokal hadis tematik |
| Gemini API error/limit | Gunakan rule-based mapping dan summary fallback |
| Internet mati saat demo | Bookmark dan cache tetap dapat ditampilkan |
| Scope terlalu besar | Fokus pada Hujjah Lens, Qur’an manual, hadis sederhana, dan bookmark |
| CI gagal karena konfigurasi multiplatform | Fokuskan build Android karena Android adalah platform wajib |

---

## 📌 Project Scope Limitation

Hujjah tidak bertujuan menjadi:

- Aplikasi fatwa.
- Pengganti ulama.
- Chatbot Islam bebas.
- Penentu hukum halal-haram.
- Aplikasi Al-Qur’an lengkap seperti mushaf digital penuh.
- Ensiklopedia hadis lengkap.

Hujjah berfokus pada:

- Pencarian dalil berdasarkan kondisi sehari-hari.
- Pembacaan Al-Qur’an dan hadis secara manual.
- Solusi Berdalil berdasarkan referensi.
- Pembelajaran Islam berbasis sumber.

---

## 📖 Disclaimer

Hujjah membantu pengguna menemukan referensi Al-Qur’an dan hadis untuk pembelajaran. Ringkasan AI dan Solusi Berdalil yang ditampilkan bersifat pembelajaran umum, bukan fatwa atau pengganti nasihat ulama.

Untuk persoalan hukum agama yang kompleks, pengguna dianjurkan bertanya kepada ustadz, ulama, atau pihak yang berkompeten.

---

## 📄 License

Project ini dibuat untuk keperluan tugas mata kuliah **Pengembangan Aplikasi Mobile**.

Institut Teknologi Sumatera  
Program Studi Teknik Informatika  
Tahun Akademik Genap 2025/2026
