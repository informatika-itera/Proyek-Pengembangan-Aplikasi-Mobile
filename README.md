# 🗣️ CakapAi

![CI](https://github.com/MartinoKelvin/Proyek-Pengembangan-Aplikasi-Mobile/actions/workflows/ci.yml/badge.svg)

> Aplikasi pembelajaran bahasa interaktif berbasis level yang menerapkan konsep gamifikasi. Selesaikan tantangan di setiap kuis untuk membuka level baru dan tingkatkan kemampuan bahasa asing Anda dengan bantuan asisten AI cerdas.

---

## 👥 Tim

| Nama              | NIM       |
| ----------------- | --------- |
| Martino Kelvin  | 123140165 |
| Louis Hutabarat | 123140052 |

---

## 📖 Deskripsi

**CakapAi** adalah aplikasi edukasi dan gamifikasi berbasis **Kotlin Multiplatform (KMP)** untuk pembelajaran bahasa secara interaktif. Dengan sistem berbasis level, pengguna akan membuka level berikutnya dan melanjutkan progres pembelajaran untuk setiap kuis yang diselesaikan dengan benar.

Tidak seperti aplikasi kuis statis lainnya, **CakapAi** dilengkapi integrasi **Gemini API** sebagai *Dynamic Quiz Generator*, sehingga soal yang didapatkan selalu bervariasi dan menantang. Aplikasi ini juga memungkinkan pembelajaran *offline* dengan *database* lokal, pencarian kosakata *real-time* via Dictionary API, hingga fitur *AI Tutor* untuk melatih percakapan dan *grammar*.

**Tujuan utama aplikasi ini:**

- Memotivasi pengguna untuk belajar bahasa asing secara konsisten melalui progres level yang menantang.
- Memberikan pengalaman kuis yang dinamis, interaktif, dan didukung oleh *feedback* visual (animasi).
- Memanfaatkan AI untuk pembelajaran yang adaptif, memberikan soal kustom serta evaluasi otomatis layaknya tutor pribadi

---

## ✨ Fitur

### Minimum

- [ ] **Operasi CRUD Lokal:** Menyimpan profil pengguna dan status progres level/materi yang terbuka.
- [ ] **Sistem Kuis Interaktif:** Format Pilihan Ganda dan Tebak Kata dengan sistem validasi jawaban yang cepat dan akurat.
- [ ] **Navigasi Multi-layar:** Pengiriman argumen antar layar (mengirimkan `levelId` dari layar *Map* ke layar *Quiz*).

### Bonus

- [ ] **AI Integration (+10%):** Menggunakan **Gemini API** sebagai *Dynamic Quiz Generator* untuk memunculkan 5 soal acak secara dinamis berdasarkan level.
- [ ] **Animations (+5%):** Animasi sukses (*confetti*/warna hijau) saat benar, dan efek getar/warna merah saat salah menggunakan Compose.
- [ ] **Offline First (+5%):** Data level dasar (*basic vocabulary*) tersimpan dalam *local database* agar dapat dimainkan tanpa koneksi internet.

### Layar Utama (Screens)

1. **Home / Map Screen:** "Peta perjalanan" belajar, menampilkan level terang (terbuka) dan tergembok (*locked*) berdasarkan data lokal.
2. **Quiz Screen:** Antarmuka dinamis kuis yang menampilkan soal, opsi jawaban, dan *progress bar* nyawa (*health*).
3. **Result Screen:** Layar akhir kuis yang menampilkan skor pencapaian, akurasi, status kelulusan level, serta tombol kembali ke *Map*.
4. **Dictionary / Vocabulary Screen:** Integrasi *Free Dictionary API* untuk pencarian arti kata secara *real-time* lengkap dengan fonetik.
5. **AI Tutor Screen:** Fitur *chat* interaktif di mana pengguna mengetik kalimat bahasa asing dan *Gemini API* membalas sekaligus mengoreksi *grammar*.

---

## 🛠️ Tech Stack

| Teknologi                      | Kegunaan                                                                           |
| ------------------------------ | ---------------------------------------------------------------------------------- |
| **Kotlin Multiplatform (KMP)** | Berbagi logika bisnis antar platform (Android & iOS) dari satu codebase            |
| **Compose Multiplatform**      | Framework UI deklaratif untuk antarmuka responsif dan animasi lintas platform      |
| **Ktor & Kotlinx Serialization**| HTTP client untuk komunikasi data JSON dengan Gemini API dan Free Dictionary API   |
| **SQLDelight**                 | Local Repository untuk menyimpan data progres level dan kamus *offline*            |
| **Koin**                       | Framework *Dependency Injection* untuk manajemen dependensi yang efisien           |
| **StateFlow**                  | Manajemen *state* kuis pada layer *Presentation* (e.g. `Loading`, `QuestionActive`)|
| **kotlin.test & MockK**        | Implementasi Unit Test untuk pengujian logika kelulusan level dan validasi kuis    |

---

## 🏗️ Arsitektur

Proyek ini mengimplementasikan **Clean Architecture** dengan pola **MVVM** (Model-View-ViewModel), yang membagi kode ke dalam tiga layer utama:

```
┌───────────────────────────────────────────┐
│            Presentation Layer             │
│   Screens (Compose) ◄──► StateFlow / VM   │
│            (State, Events)                │
└─────────────────────┬─────────────────────┘
                      │
┌─────────────────────▼─────────────────────┐
│               Domain Layer                │
│ CheckAnswerUseCase, UpdateLevelProgressUseCase│
│         (Pure Kotlin / Business)          │
└─────────────────────┬─────────────────────┘
                      │
┌─────────────────────▼─────────────────────┐
│                Data Layer                 │
│ Repository ──► Local (SQLDelight CRUD)    │
│            └──► Remote (Ktor / Gemini)    │
└───────────────────────────────────────────┘
```

- **Presentation Layer**: Menangani tampilan UI (5 Screens) menggunakan Compose Multiplatform. Manajemen *state* kuis menggunakan StateFlow (memantau status seperti `Loading`, `QuestionActive`, `AnswerChecked`, `QuizFinished`).
- **Domain Layer**: Berisi *Use Cases* spesifik, contohnya `CheckAnswerUseCase` (mencocokkan jawaban) dan `UpdateLevelProgressUseCase` (mengatur kelulusan level), murni Kotlin tanpa dependensi framework.
- **Data Layer**: Mengelola implementasi *repository*, operasi CRUD lokal dengan SQLDelight, dan komunikasi JSON ke *endpoint* jarak jauh via Ktor.

### 📂 Struktur Folder (Estimasi)

Sebagai proyek Kotlin Multiplatform (KMP), *source code* utama berada di modul `composeApp` pada *source set* `commonMain`. Berikut adalah estimasi struktur foldernya berdasarkan penerapan **Clean Architecture**:

```text
composeApp/src/commonMain/kotlin/com/example/cakapai/
├── App.kt                           # Entry point Compose Multiplatform
├── core/                            # Komponen inti dan utilitas
│   ├── network/                     # Ktor client & konfigurasi
│   ├── database/                    # SQLDelight config & queries
│   └── di/                          # Koin modules (Injection)
├── data/                            # Layer Data
│   ├── local/                       # Local data source (SQLDelight)
│   ├── remote/                      # Remote data source (Gemini API, Dictionary API)
│   └── repository/                  # Implementasi Repository
├── domain/                          # Layer Domain
│   ├── model/                       # Data class & entitas bisnis
│   ├── repository/                  # Interfaces Repository
│   └── usecase/                     # Logika bisnis (CheckAnswer, UpdateLevelProgress)
└── presentation/                    # Layer Presentation
    ├── screens/                     # UI Layar (Home, Quiz, Result, Dictionary, AI Tutor)
    ├── components/                  # Reusable UI components (Buttons, Cards, Dialogs)
    └── viewmodel/                   # StateFlow & logika UI (MVVM)
```

---

## 🚀 Setup

Ikuti langkah-langkah berikut untuk menjalankan proyek CakapAi secara lokal:

### Prasyarat

- Android Studio **Hedgehog** (2023.1.1) atau lebih baru
- JDK **17** atau lebih baru
- Android Emulator atau perangkat fisik dengan USB Debugging aktif

### Langkah-langkah

**1. Clone repository**

```bash
git clone https://github.com/MartinoKelvin/Proyek-Pengembangan-Aplikasi-Mobile.git
cd Proyek-Pengembangan-Aplikasi-Mobile
```

**2. Buka di Android Studio**

- Buka Android Studio, pilih **File** → **Open...**.
- Arahkan ke folder hasil _clone_ di atas.
- Tunggu hingga proses **Gradle Sync** selesai secara otomatis.

**3. Run di emulator atau device**

- Di toolbar atas, pastikan konfigurasi run yang dipilih adalah **`composeApp`**.
- Pilih target perangkat (emulator atau device fisik yang terhubung).
- Klik tombol **▶ Run** atau tekan `Shift + F10`.

*(Catatan: Pastikan Anda telah memiliki API Key yang valid dari Google AI Studio jika ingin menggunakan fitur Dynamic Quiz & AI Tutor secara maksimal).*

---

## 📄 Lisensi

Proyek ini dibuat untuk keperluan Tugas Akhir Mata Kuliah **Pengembangan Aplikasi Mobile**  
Program Studi Teknik Informatika — Institut Teknologi Sumatera

---
