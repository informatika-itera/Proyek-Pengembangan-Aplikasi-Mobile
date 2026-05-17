# Hujjah

![CI](https://github.com/Awesome1209/Proyek-Pengembangan-Aplikasi-Mobile/actions/workflows/ci.yml/badge.svg)

## 📌 Project Overview

**Hujjah** adalah aplikasi mobile referensi Islam berbasis AI yang membantu pengguna membaca Al-Qur’an dan hadis secara manual, serta menemukan dalil berdasarkan kondisi sehari-hari melalui fitur **Hujjah Lens**.

Melalui Hujjah Lens, pengguna dapat menuliskan kondisi seperti:

- "Aku sedang marah"
- "Aku merasa sedih"
- "Aku ingin bertaubat"
- "Aku malas shalat"
- "Aku takut masa depan"

Aplikasi kemudian akan memetakan input pengguna ke topik Islami yang relevan, menampilkan referensi Al-Qur’an dan hadis, lalu menyusun **Solusi Berdalil**, yaitu solusi yang setiap poinnya memiliki dasar dari ayat atau hadis yang ditampilkan.

> Hujjah bukan aplikasi fatwa dan bukan pengganti ulama. Hujjah adalah aplikasi pembelajaran referensi Islam berbasis Al-Qur’an dan hadis.

---

## 👥 Team

| Nama | NIM | GitHub Username                               | Role |
|---|---|-----------------------------------------------|---|
| AWI SEPTIAN PRASETYO | 123140201 | [@awesome1209](https://github.com/Awesome1209) | Lead / Mobile Developer |
| MUHAMMAD BIMASTIAR | 123140211 | [@211-Bimas](https://github.com/211-Bimas)    | API & Data Developer |

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

- [ ] **Hujjah Lens**
    - Input kondisi pengguna.
    - AI memetakan input ke topik Islami.
    - Menampilkan referensi Al-Qur’an dan hadis.
    - Menyusun Solusi Berdalil.

- [ ] **Al-Qur’an Manual**
    - Menampilkan daftar surah.
    - Menampilkan detail surah.
    - Menampilkan ayat dan terjemahan Bahasa Indonesia.
    - Bookmark ayat.

- [ ] **Hadis Manual**
    - Menampilkan daftar hadis atau hadis tematik.
    - Menampilkan detail hadis.
    - Bookmark hadis.

- [ ] **Reference Detail**
    - Menampilkan detail referensi Al-Qur’an atau hadis.
    - Menampilkan teks Arab, terjemahan, sumber, dan topik.

- [ ] **Bookmark Reference**
    - Menyimpan referensi Al-Qur’an dan hadis ke database lokal.
    - Menampilkan daftar referensi tersimpan.
    - Menghapus bookmark.

- [ ] **Multi-screen Navigation**
    - Navigasi antar halaman menggunakan Compose Navigation.
    - Mendukung argument passing untuk detail referensi.

- [ ] **State Management**
    - Menggunakan ViewModel.
    - Menggunakan StateFlow untuk UI state.

- [ ] **Clean Architecture**
    - Memisahkan layer `presentation`, `domain`, dan `data`.

- [ ] **Dependency Injection**
    - Menggunakan Koin.

- [ ] **Testing**
    - Unit test untuk use case/repository.
    - UI test untuk screen utama.

---

### Bonus Features

- [ ] **Gemini AI Integration**
    - AI digunakan untuk mapping input, ringkasan, dan Solusi Berdalil.

- [ ] **Offline-first Bookmark**
    - Referensi yang sudah disimpan dapat diakses tanpa internet.

- [ ] **Dark Mode**
    - Mendukung tema terang dan gelap.

- [ ] **Meaningful Animations**
    - Animasi sederhana untuk transisi dan loading state.

- [ ] **CI/CD**
    - GitHub Actions untuk build dan test otomatis.

---

## 🧠 AI Usage Policy

AI dalam Hujjah digunakan sebagai **reasoning layer**, bukan sebagai sumber utama.

Sumber utama aplikasi tetap:

- Al-Qur’an
- Hadis
- Data/API referensi yang digunakan aplikasi

AI digunakan untuk:

- Memahami input pengguna.
- Memetakan input ke kategori/topik Islami.
- Membuat keyword pencarian.
- Meringkas dalil yang sudah ditemukan.
- Menyusun Solusi Berdalil berdasarkan referensi yang ditampilkan.

AI tidak digunakan untuk:

- Memberikan fatwa final.
- Menentukan hukum halal-haram secara bebas.
- Menggantikan ustadz/ulama.
- Menjawab tanpa referensi.
- Menambahkan dalil yang tidak tersedia dalam data aplikasi.

Disclaimer:

> Ringkasan AI dan Solusi Berdalil bersifat pembelajaran umum, bukan fatwa atau pengganti nasihat ulama.

---

## 🧩 Initial Hujjah Lens Categories

Pada tahap MVP, input pengguna bersifat bebas, tetapi sistem akan memetakan input tersebut ke kategori awal yang terkurasi.

| Kategori | Contoh Input | Contoh Referensi |
|---|---|---|
| Mengendalikan Amarah | marah, emosi, kesal | QS Ali Imran: 134, hadis “Jangan marah” |
| Ketenangan Hati | sedih, galau, kecewa | QS Ar-Ra’d: 28, QS Al-Insyirah: 5–6 |
| Sabar | diuji, musibah, capek | QS Al-Baqarah: 153, QS Az-Zumar: 10 |
| Taubat | dosa, menyesal, ingin berubah | QS Az-Zumar: 53, QS At-Tahrim: 8 |
| Syukur | kurang bersyukur, iri | QS Ibrahim: 7 |
| Shalat | malas shalat, lalai | QS Al-Ma’un: 4–5, QS Al-Baqarah: 45 |
| Tawakkal / Cemas | takut masa depan, overthinking | QS Ath-Thalaq: 3 |
| Ilmu | malas belajar, ingin belajar | QS Al-Mujadilah: 11 |
| Orang Tua | konflik dengan orang tua | QS Al-Isra: 23 |
| Rezeki | takut miskin, kerja, nafkah | QS Hud: 6, QS Ath-Thalaq: 2–3 |

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
| AI | Gemini API |
| Testing | kotlin.test, MockK, Turbine, Compose Test |
| CI/CD | GitHub Actions |

---

## 🌐 API & Data Sources

### Al-Qur’an

Primary source:

```text
equran.id API
```

Digunakan untuk:

- Daftar surah.
- Detail surah.
- Ayat Al-Qur’an.
- Terjemahan Bahasa Indonesia.
- Audio atau tafsir jika memungkinkan.

Backup source:

```text
alquran.cloud API
```

Digunakan jika sumber utama tidak tersedia atau ada endpoint yang tidak sesuai kebutuhan.

---

### Hadis

Primary plan:

```text
Data lokal hadis tematik
```

Digunakan agar demo tetap stabil meskipun internet atau API hadis bermasalah.

Optional API:

```text
gadingnst/hadith-api
```

Digunakan jika API stabil dan sesuai kebutuhan aplikasi.

---

### AI

```text
Gemini API
```

Digunakan untuk:

- Mapping input pengguna ke kategori Islami.
- Membuat ringkasan pembelajaran.
- Menyusun Solusi Berdalil berdasarkan daftar dalil yang sudah ditemukan.

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

## 📁 Recommended Project Structure

```text
composeApp/src/commonMain/kotlin/...
├── core/
│   ├── di/
│   ├── network/
│   ├── util/
│   └── constants/
├── data/
│   ├── local/
│   │   ├── database/
│   │   └── mapper/
│   ├── remote/
│   │   ├── quran/
│   │   │   ├── QuranApiService.kt
│   │   │   └── dto/
│   │   ├── hadith/
│   │   │   ├── HadithApiService.kt
│   │   │   └── dto/
│   │   └── ai/
│   │       ├── GeminiService.kt
│   │       └── dto/
│   └── repository/
│       ├── QuranRepositoryImpl.kt
│       ├── HadithRepositoryImpl.kt
│       ├── HujjahRepositoryImpl.kt
│       └── BookmarkRepositoryImpl.kt
├── domain/
│   ├── model/
│   │   ├── SourceType.kt
│   │   ├── IslamicReference.kt
│   │   ├── HujjahResult.kt
│   │   ├── EvidenceBasedSolution.kt
│   │   └── BookmarkReference.kt
│   ├── repository/
│   │   ├── QuranRepository.kt
│   │   ├── HadithRepository.kt
│   │   ├── HujjahRepository.kt
│   │   └── BookmarkRepository.kt
│   └── usecase/
│       ├── AnalyzeUserInputUseCase.kt
│       ├── GetQuranReferencesUseCase.kt
│       ├── GetHadithReferencesUseCase.kt
│       ├── GenerateHujjahSolutionUseCase.kt
│       ├── SaveBookmarkUseCase.kt
│       ├── DeleteBookmarkUseCase.kt
│       └── GetBookmarksUseCase.kt
└── presentation/
    ├── navigation/
    ├── theme/
    ├── components/
    └── screens/
        ├── home/
        ├── result/
        ├── quran/
        ├── hadith/
        ├── detail/
        └── bookmark/
```

---

## 📱 Planned Screens

| Screen | Description |
|---|---|
| Splash / Welcome Screen | Menampilkan logo dan tagline Hujjah |
| Hujjah Lens Screen | Input kondisi pengguna dan kategori cepat |
| Hujjah Result Screen | Menampilkan topik, dalil, ringkasan, dan Solusi Berdalil |
| Al-Qur’an List Screen | Menampilkan daftar surah |
| Al-Qur’an Detail Screen | Menampilkan daftar ayat dan terjemahan |
| Hadis Screen | Menampilkan daftar hadis atau hadis tematik |
| Reference Detail Screen | Menampilkan detail ayat/hadis |
| Bookmark Screen | Menampilkan referensi tersimpan |
| About / Disclaimer Screen | Menampilkan informasi aplikasi dan disclaimer AI |

Bottom navigation:

```text
Hujjah Lens | Al-Qur’an | Hadis | Tersimpan
```

---

## 🗃 Local Database Plan

Local database digunakan untuk menyimpan bookmark dan cache referensi.

### BookmarkReferenceEntity

```sql
CREATE TABLE BookmarkReferenceEntity (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    reference_id TEXT NOT NULL,
    source_type TEXT NOT NULL,
    title TEXT NOT NULL,
    reference TEXT NOT NULL,
    arabic_text TEXT NOT NULL,
    translation TEXT NOT NULL,
    topic TEXT NOT NULL,
    saved_at INTEGER NOT NULL
);
```

Fungsi utama:

- Menyimpan bookmark ayat.
- Menyimpan bookmark hadis.
- Menampilkan referensi tersimpan.
- Menghapus bookmark.

---

## 🧪 Testing Plan

Testing dilakukan secara bertahap.

### Unit Tests

Target unit test:

- AnalyzeUserInputUseCase
- GenerateHujjahSolutionUseCase
- SaveBookmarkUseCase
- DeleteBookmarkUseCase
- GetBookmarksUseCase
- QuranRepository
- HadithRepository
- BookmarkRepository

### UI Tests

Target UI test:

- Hujjah Lens input and result flow
- Al-Qur’an list/detail screen
- Bookmark screen

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

## Vidio Sprint 

### Sprint 2



## 📄 License

Project ini dibuat untuk keperluan tugas mata kuliah **Pengembangan Aplikasi Mobile**.

Institut Teknologi Sumatera  
Program Studi Teknik Informatika  
Tahun Akademik Genap 2025/2026