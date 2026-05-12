import os

# Define the content for README.md
readme_content = """# 🎮 GameCo

Aplikasi mobile berbasis **Kotlin Multiplatform (KMP)** yang menyajikan berita game terbaru, fitur pencarian, filter genre/rating, serta rekomendasi berbasis AI yang dipersonalisasi.

---

## 🚀 Fitur Utama

- **Latest Game News:** Menampilkan berita dan data game terbaru secara real-time dari API [GameBrain](https://gamebrain.co).
- **Advanced Filtering:** Filter pencarian berdasarkan genre (Action, RPG, Strategy, dll) dan rating minimal.
- **AI Recommendation:** Memberikan rekomendasi game cerdas menggunakan **Google Gemini AI** berdasarkan kategori favorit pengguna.
- **Local Persistence:** Data favorit dan preferensi pengguna tersimpan secara lokal di perangkat menggunakan SQLDelight.
- **Smart Search:** Pencarian game yang cepat dan responsif.

---

## 🛠️ Tech Stack

Aplikasi ini dibangun menggunakan teknologi modern untuk memastikan performa tinggi dan kemudahan pemeliharaan:

| Kategori | Teknologi | Kegunaan |
|----------|-----------|----------|
| **Framework** | Kotlin Multiplatform (KMP) | Berbagi logika bisnis di Android & iOS |
| **UI Framework** | Compose Multiplatform | Framework UI deklaratif untuk tampilan shared |
| **Arsitektur** | Clean Architecture + MVVM | Pemisahan layer Domain, Data, dan Presentation |
| **AI Engine** | Google Gemini AI | Pemrosesan rekomendasi berbasis teks |
| **Local Database**| SQLDelight | Penyimpanan data lokal yang type-safe |
| **Networking** | Ktor Client | Konsumsi API eksternal (GameBrain & Gemini) |
| **Dep. Injection**| Koin | Manajemen dependency injection |
| **Concurrency** | Kotlin Coroutines & Flow | Penanganan operasi asynchronous dan reaktif |
| **Local Storage** | Multiplatform Settings | Penyimpanan key-value (preferensi user) |

---

## 🏗️ Struktur Arsitektur

Project ini mengikuti standar **Clean Architecture** yang ketat sesuai panduan template:

1.  **Presentation Layer:** Berisi UI (Compose) dan ViewModels yang mengelola state.
2.  **Domain Layer:** Berisi abstraksi Repository, Use Cases, dan Entity (Logic bisnis murni).
3.  **Data Layer:** Implementasi Repository, integrasi API Ktor, dan DAO SQLDelight.

---

## 👥 Anggota Kelompok (ITERA)

| Nama | NIM | Peran Utama |
|------|-----|-------------|
| **Muhammad Farisi Suyitno** | 123140152 | Domain + Data Layer, Database, API |
| **Ali Akbar** | 123140162 | Domain + Data Layer, Database, API |

---

## ⚙️ Cara Menjalankan

1.  **Persiapan API Key:**
    - Dapatkan API Key Gemini di [Google AI Studio](https://aistudio.google.com/).
    - Dapatkan API Key di [GameBrain](https://gamebrain.co).
2.  **Konfigurasi `local.properties`:**
    Tambahkan baris berikut di root project:
    ```properties
    GEMINI_API_KEY=YOUR_GEMINI_KEY
    GAMEBRAIN_API_KEY=YOUR_GAMEBRAIN_KEY
    ```
3.  **Build:**
    Buka project di **Android Studio Ladybug**, tunggu Gradle Sync selesai, dan jalankan pada emulator/perangkat Android.

---

## 🌿 Git Workflow

Semua pengerjaan dilakukan pada branch:
`project/123140152-[123140162]-GameCo`

Format Commit: `<type>: <deskripsi>` (contoh: `feat: add genre filter logic`)

---
*© 2026 - Program Studi Teknik Informatika, Institut Teknologi Sumatera*
"""

# Write the content to a file
file_path = 'README.md'
with open(file_path, 'w', encoding='utf-8') as f:
    f.write(readme_content)

print(f"File {file_path} has been generated.")
