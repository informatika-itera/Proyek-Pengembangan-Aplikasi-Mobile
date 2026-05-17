# 🎵 MusicKeep - Smart Music Cataloging

Aplikasi **Smart Music Cataloging** (Katalog Musik Pribadi) yang dibangun menggunakan **Kotlin Multiplatform (KMP)** & **Compose Multiplatform**.

Aplikasi ini dikembangkan sebagai Tugas Besar (Tubes) mata kuliah **Pengembangan Aplikasi Mobile**. MusicKeep membantu pengguna mengelola daftar musik favorit mereka dengan deteksi genre/mood berbasis AI (Planned).

### 👤 Identitas Mahasiswa
- **Nama:** Andre Prasetya Daely
- **NIM:** 123140131
- **Program Studi:** Teknik Informatika
- **Mata Kuliah:** Pengembangan Aplikasi Mobile

## ✨ Fitur Utama

- 📋 **Music Cataloging** - Input dan kelola data musik (Judul Lagu & Artis).
- 🎨 **Music-Centric UI** - Antarmuka tema gelap (*Dark Mode*) yang elegan khas aplikasi musik.
- 🤖 **AI Integration** - Deteksi otomatis Genre/Mood lagu (Sprint berikutnya).
- 📱 **Cross-Platform** - Berjalan di Android & iOS dari satu codebase.
- 💾 **Offline First** - Penyimpanan data lokal yang handal.

## 🏗️ Tech Stack & Arsitektur

### Arsitektur: MVVM (Model-View-ViewModel)
Aplikasi ini mengikuti pola arsitektur MVVM dengan State Management reaktif menggunakan StateFlow.

| Komponen | Teknologi |
|----------|-----------|
| **Framework** | Kotlin Multiplatform (KMP) |
| **UI Framework** | Compose Multiplatform (Android & iOS) |
| **Dependency Injection** | Koin |
| **Local Database** | SQLDelight |
| **Asynchronous** | Kotlin Coroutines & Flow |
| **Navigation** | Compose Navigation (Type-safe) |

## 📁 Struktur Project

```
composeApp/src/
├── commonMain/kotlin/com/example/musickeep/
│   ├── core/                      # Utilitas inti & DI
│   ├── data/                      # Data layer (Local & Repository)
│   ├── domain/                    # Domain layer (Model & UseCase)
│   ├── presentation/              # UI layer (Screens, ViewModels, Theme)
│   └── App.kt                     # Entry point aplikasi
│
├── commonMain/sqldelight/         # Skema database (Music.sq)
│
├── androidMain/                   # Implementasi spesifik Android
└── iosMain/                       # Implementasi spesifik iOS
```

## 📝 Tugas Mahasiswa

### Sprint 1: Foundation
- [x] Clone dan setup project
- [x] Pahami struktur folder
- [x] Modifikasi tema/warna

### Sprint 2: Core Features
- [x] Tambahkan field baru (Judul Lagu & Artis)
- [x] Implementasi fitur kategori/tags
- [x] Tambahkan validasi input

### Sprint 3: Advanced Features
- [ ] Implementasi search dengan debounce
- [ ] Tambahkan filter dan sort
- [ ] Implementasi offline-first

### Sprint 4: AI & Polish
- [ ] Integrasikan fitur AI baru
- [ ] UI polish dan animasi
- [ ] Tambahkan unit tests

### Sprint 5: Final
- [ ] Bug fixes
- [ ] Dokumentasi
- [ ] Prepare demo

---

## 🚀 Cara Menjalankan

1. **Prasyarat**:
   - Android Studio Ladybug (2024.2.1) atau lebih baru.
   - JDK 17.
2. **Clone**:
   ```bash
   git clone https://github.com/08-131-AndrePrasetyaDaely/Proyek-Pengembangan-Aplikasi-Mobile.git
   ```
3. **Build**:
   - Klik **Sync Project with Gradle Files**.
   - Jalankan `composeApp` di emulator atau device Android.


