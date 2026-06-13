# 📱 Cooknote — Smart Pantry & AI Chef Assistant

> **Ubah sisa bahan di dapurmu menjadi resep lezat dan kurangi limbah makanan (*zero-waste*).**

Aplikasi manajemen bahan makanan dan asisten koki pintar berbasis AI menggunakan **Kotlin Multiplatform (KMP)**. Dibangun dengan Clean Architecture, Compose Multiplatform, dan Google Gemini API.

---

## 🎬 Demo Video

[![Demo Cooknote](https://img.youtube.com/vi/GS4FgCWs2ZM/maxresdefault.jpg)](https://youtu.be/GS4FgCWs2ZM)

▶️ **[Tonton Demo di YouTube](https://youtu.be/GS4FgCWs2ZM)**

---

## 👥 Informasi Kelompok

**Program Studi Teknik Informatika — Institut Teknologi Sumatera (ITERA)**
**Mata Kuliah:** Pengembangan Aplikasi Mobile (IF25-22017) | Tahun Akademik Genap 2025/2026

| Nama | NIM | Peran |
|------|-----|-------|
| M. Hafizurrahman Akbar | 123140123 | Domain & Data Layer |
| Jordy Anugrah Akbar | 123140141 | Presentation Layer & UI |

**Dosen Pengampu:** Pak Habib — [@mh4Scripts](https://github.com/mh4Scripts)

---

## ✨ Fitur Utama

| Fitur | Deskripsi |
|-------|-----------|
| 📝 **Pantry Inventory** | Tambah, edit, hapus, dan kelola bahan makanan yang tersedia di dapur |
| 🔍 **Smart Search & Filter** | Cari bahan dan filter berdasarkan kategori (Sayur, Daging, Bumbu, dll) |
| 🤖 **AI Chef Assistant** | Generate resep masakan step-by-step menggunakan bahan tersisa di pantry (Gemini API) |
| ⭐ **Simpan Resep Favorit** | Offline-first penyimpanan resep favorit menggunakan SQLDelight |
| 🔐 **Login & Profil** | Sistem autentikasi dengan halaman profil terintegrasi |
| 🌙 **Dark Mode** | Tema gelap/terang untuk kenyamanan penggunaan |
| 📱 **Cross-Platform** | Android & iOS dari satu codebase menggunakan KMP |

---

## 🏗️ Arsitektur

Menggunakan **Clean Architecture + MVVM**:

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                        │
│         Screen (Composable)  ◄──────►  ViewModel            │
│                                         (StateFlow)         │
└───────────────────────────────────┬─────────────────────────┘
                                    │
┌───────────────────────────────────▼─────────────────────────┐
│                         DOMAIN LAYER                         │
│              Use Cases (Business Logic)                      │
│              Repository Interface                            │
└───────────────────────────────────┬─────────────────────────┘
                                    │
┌───────────────────────────────────▼─────────────────────────┐
│                          DATA LAYER                          │
│           SQLDelight (Local) │ Ktor (Remote) │ DataStore     │
└─────────────────────────────────────────────────────────────┘
```

### 🛠️ Tech Stack

| Layer | Teknologi |
|-------|-----------|
| **UI** | Compose Multiplatform, Material 3 |
| **State** | StateFlow, ViewModel |
| **Navigation** | Compose Navigation (Type-safe) |
| **Networking** | Ktor Client |
| **Local DB** | SQLDelight |
| **Preferences** | DataStore |
| **DI** | Koin |
| **AI** | Google Gemini API |
| **Testing** | Kotlin Test, Turbine |

---

## 📁 Struktur Project

```
composeApp/src/
├── commonMain/kotlin/com/example/noteai/
│   ├── core/              # Core utilities & DI
│   ├── data/              # Data layer (SQLDelight, Ktor, Repository Impl)
│   ├── domain/            # Domain layer (Models, UseCases, Repository Interfaces)
│   └── presentation/      # Presentation layer (UI, Navigation, ViewModels)
├── commonMain/sqldelight/ # SQLDelight schema (.sq files)
├── androidMain/kotlin/    # Android-specific code
└── iosMain/kotlin/        # iOS-specific code
```

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Ladybug (2024.2.1) atau lebih baru
- Xcode 15+ (untuk iOS)
- JDK 17+

### Setup

1. **Clone repository**
   ```bash
   git clone git@github.com:informatika-itera/Proyek-Pengembangan-Aplikasi-Mobile.git
   cd Proyek-Pengembangan-Aplikasi-Mobile
   ```

2. **Gunakan branch kelompok**
   ```bash
   git checkout project/123140123-123140141-Cooknote
   ```

3. **Setup `local.properties`**
   ```bash
   cp local.properties.example local.properties
   # Edit local.properties dan isi:
   # GEMINI_API_KEY=your_api_key_here
   ```
   > Dapatkan API key gratis di: https://aistudio.google.com/

4. **Sync & Build**
   ```bash
   ./gradlew build                       # build semua target
   ./gradlew :composeApp:assembleDebug   # build APK debug saja
   ```

### Build Release APK

```bash
# 1. Generate keystore (satu kali)
keytool -genkey -v -keystore my-release-key.jks -keyalg RSA \
  -keysize 2048 -validity 10000 -alias my-key-alias

# 2. Build release APK
./gradlew assembleRelease
# Output: composeApp/build/outputs/apk/release/composeApp-release.apk
```

> ⚠️ **JANGAN** commit keystore password ke Git! Gunakan `local.properties` atau environment variables.

---

## 🧪 Testing

### Unit Testing (15 Tests)

Lokasi: `composeApp/src/commonTest/kotlin/com/example/noteai/`

| # | Test | Deskripsi |
|---|------|-----------|
| 1 | `insertNote` | Menambah catatan ke database lokal |
| 2 | `searchNotes` | Pencarian catatan berdasarkan judul |
| 3 | `deleteNote` | Penghapusan catatan |
| 4 | `registerUser` | Alur pendaftaran akun pengguna |
| 5 | `loginUser` | Verifikasi login dengan kredensial benar |
| 6 | `logoutUser` | Penghapusan sesi pengguna |
| 7 | `initialRecipesState` | Memastikan list resep awal kosong |
| 8 | `saveRecipe` | Menyimpan resep baru ke database |
| 9 | `filterFavoriteRecipes` | Filter resep favorit |
| 10 | `initialPantryItems` | Memastikan pantry awal kosong |
| 11 | `searchPantry` | Mencari bahan makanan di inventory |
| 12 | `categoryFilterPantry` | Filter kategori bahan dapur |
| 13 | `initialChatState` | Verifikasi pesan pembuka AI Chef |
| 14 | `chatInputUpdate` | Perubahan state teks saat mengetik |
| 15 | `chatSendMessage` | Alur kirim pesan dan pembersihan input |

Cara menjalankan: Klik kanan folder `commonTest/kotlin` → **Run 'Tests in noteai'**

### UI Testing (5 Tests)

Lokasi: `composeApp/src/androidInstrumentedTest/kotlin/com/example/noteai/AppUiTest.kt`

| # | Test | Deskripsi |
|---|------|-----------|
| 1 | `testLoginFlow` | Uji input login dan tombol masuk |
| 2 | `testNavigationBetweenTabs` | Uji navigasi antar menu utama |
| 3 | `testAddPantryItemFlow` | Uji alur tambah bahan makanan baru |
| 4 | `testRecipeClickNavigation` | Uji klik resep menuju detail |
| 5 | `testProfileDisplay` | Uji tampilan nama & email user di profil |

---

## 📋 Sprint Progress

| Sprint | Minggu | Status | Deliverables |
|--------|--------|--------|--------------|
| Sprint 1: Planning | W11 | ✅ Selesai | Setup project, struktur folder, tema, README |
| Sprint 2: Core Features | W12 | ✅ Selesai | Domain model, CRUD bahan makanan, SQLDelight |
| Sprint 3: Advanced Features | W13 | ✅ Selesai | Search, filter kategori, offline-first, login |
| Sprint 4: Polish & Testing | W14 | ✅ Selesai | Gemini AI, UI polish, unit & UI tests |
| Sprint 5: Final Preparation | W15 | ✅ Selesai | Bug fixes, dokumentasi, demo, release APK |
| **UAS Demo Day** | **W16** | 🎯 **Next** | **Live Demo!** |

---

## 📚 Dokumentasi Lengkap

| Dokumen | Deskripsi |
|---------|-----------|
| [🚀 Cara Menjalankan](docs/CARA_MENJALANKAN.md) | **BACA INI DULU!** Panduan setup dan running aplikasi |
| [📋 Panduan Project](docs/PANDUAN_PROJECT.md) | Informasi lengkap tentang project, timeline, dan penilaian |
| [🌿 Git Workflow](docs/GIT_WORKFLOW.md) | Cara menggunakan Git dan branching strategy |
| [📜 Aturan Modifikasi](docs/ATURAN_MODIFIKASI.md) | Apa yang boleh dan tidak boleh dimodifikasi |
| [🏗️ Struktur Kode](docs/STRUKTUR_KODE.md) | Penjelasan arsitektur dan struktur folder |
| [🔧 Troubleshooting](docs/TROUBLESHOOTING.md) | Solusi untuk masalah umum |

---

## 📄 License

MIT License — silakan gunakan untuk pembelajaran.

---

<div align="center">

**Program Studi Teknik Informatika**
**Institut Teknologi Sumatera (ITERA)**

*"Success is where preparation and opportunity meet."*

</div>