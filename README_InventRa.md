# 📦 InventRa — KMP Project (Sprint 1: Foundation)

Template project **Kotlin Multiplatform** untuk mata kuliah **Pengembangan Aplikasi Mobile** di ITERA.

InventRa adalah aplikasi manajemen inventaris berbasis AI yang dikembangkan di atas arsitektur Clean Architecture + MVVM dengan Kotlin Multiplatform.

---

## 📚 Dokumentasi

| Dokumen | Deskripsi |
|---------|-----------|
| [🚀 Cara Menjalankan](./docs/CARA_MENJALANKAN.md) | Panduan setup dan menjalankan aplikasi |
| [📋 Panduan Project](./docs/PANDUAN_PROJECT.md) | Timeline, sprint, dan penilaian |
| [🌿 Git Workflow](./docs/GIT_WORKFLOW.md) | Branching strategy dan workflow |
| [📜 Aturan Modifikasi](./docs/ATURAN_MODIFIKASI.md) | Apa yang boleh dan tidak boleh diubah |
| [🏗️ Struktur Kode](./docs/STRUKTUR_KODE.md) | Arsitektur dan struktur folder |
| [🔧 Troubleshooting](./docs/TROUBLESHOOTING.md) | Solusi masalah umum |

---

## ✅ Sprint 1: Foundation — Checklist

### 1. Clone & Setup Project

```bash
# Clone repository
git clone https://github.com/informatika-itera/Proyek-Pengembangan-Aplikasi-Mobile.git
cd Proyek-Pengembangan-Aplikasi-Mobile

# Buat branch kelompok
git checkout -b project/121140XXX-InventRa
```

Setup `local.properties`:

```bash
cp local.properties.example local.properties
# Edit: isi sdk.dir dan GEMINI_API_KEY
```

Sync & build:

```bash
./gradlew build
# atau di Android Studio: File → Sync Project with Gradle Files
```

**Status:** ✅ Selesai

---

### 2. Pahami Struktur Folder

Proyek menggunakan **Clean Architecture + MVVM** dengan tiga layer utama:

```
composeApp/src/
├── commonMain/kotlin/com/example/inventra/
│   ├── core/                   # DI, network config, utilities
│   │   ├── di/AppModule.kt
│   │   ├── network/
│   │   └── util/
│   ├── data/                   # Data layer (implementasi)
│   │   ├── local/              # SQLDelight + DataStore
│   │   ├── remote/             # Ktor + Gemini API
│   │   └── repository/
│   ├── domain/                 # Domain layer (pure Kotlin)
│   │   ├── model/Note.kt
│   │   ├── repository/
│   │   └── usecase/
│   └── presentation/           # UI layer (Compose)
│       ├── navigation/
│       ├── screens/
│       │   ├── home/
│       │   ├── addnote/
│       │   ├── detail/
│       │   └── ai/
│       ├── components/
│       └── theme/
├── commonMain/sqldelight/      # Skema database (Note.sq)
├── androidMain/                # Implementasi spesifik Android
└── iosMain/                    # Implementasi spesifik iOS
```

**Status:** ✅ Dipahami

---

### 3. Modifikasi Tema / Warna

Tema InventRa menggunakan palet warna kustom yang telah dimodifikasi pada `Theme.kt`:

| Peran | Warna | Hex |
|-------|-------|-----|
| **Primary** | Hijau | `#4D5B37` |
| **Secondary** | Biru | `#17579F` |
| **Tertiary** | Kuning | `#CBCF1A` |

Perubahan diterapkan di:

```
composeApp/src/commonMain/kotlin/com/example/inventra/presentation/theme/Theme.kt
```

#### Cuplikan perubahan `Theme.kt`

```kotlin
// InventRa Custom Color Palette
private val Primary        = Color(0xFF4D5B37)   // Hijau
private val OnPrimary      = Color(0xFFFFFFFF)
private val PrimaryContainer    = Color(0xFFCBD9AA)
private val OnPrimaryContainer  = Color(0xFF111E00)

private val Secondary      = Color(0xFF17579F)   // Biru
private val OnSecondary    = Color(0xFFFFFFFF)
private val SecondaryContainer  = Color(0xFFD2E4FF)
private val OnSecondaryContainer = Color(0xFF001B3E)

private val Tertiary       = Color(0xFFCBCF1A)   // Kuning
private val OnTertiary     = Color(0xFF313300)
private val TertiaryContainer   = Color(0xFFEEF284)
private val OnTertiaryContainer = Color(0xFF1F2100)
```

**Status:** ✅ Selesai

---

## ✨ Fitur Aplikasi

- 📝 **CRUD Notes** — Tambah, edit, hapus, dan lihat catatan inventaris
- 🔍 **Search & Filter** — Cari dan filter berdasarkan kategori
- 🤖 **AI Assistant** — Ringkas, generate ide, perbaiki tulisan (powered by Gemini)
- 🌙 **Dark Mode** — Tema gelap/terang otomatis
- 📱 **Cross-Platform** — Android & iOS dari satu codebase

---

## 🏗️ Arsitektur & Tech Stack

### Clean Architecture + MVVM

```
┌─────────────────────────────────────────┐
│           PRESENTATION LAYER            │
│   Screen (Composable) ◄──► ViewModel   │
└─────────────────────┬───────────────────┘
                      │
┌─────────────────────▼───────────────────┐
│             DOMAIN LAYER                │
│   Use Cases ──► Repository Interface   │
└─────────────────────┬───────────────────┘
                      │
┌─────────────────────▼───────────────────┐
│              DATA LAYER                 │
│  SQLDelight │ Ktor (Gemini) │ DataStore │
└─────────────────────────────────────────┘
```

### Tech Stack

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

## 📁 Package

```
com.example.inventra
```

> Package telah diubah dari `com.example.noteai` → `com.example.inventra` sesuai nama proyek InventRa.

---

## 🚀 Cara Menjalankan

### Prasyarat

- Android Studio Ladybug (2024.2.1+)
- JDK 17+
- Android SDK API 34/35

### Langkah

1. Clone dan masuk ke folder project
2. Buat `local.properties` dari template:
   ```bash
   cp local.properties.example local.properties
   ```
3. Isi `GEMINI_API_KEY` dari [Google AI Studio](https://aistudio.google.com/)
4. Buka di Android Studio → Tunggu Gradle sync
5. Jalankan dengan run configuration **composeApp**

---

## 🧪 Menjalankan Test

```bash
# Semua test
./gradlew allTests

# Unit test Android debug
./gradlew :composeApp:testDebugUnitTest
```

---

## 📅 Progress Sprint

| Sprint | Status | Deliverables |
|--------|--------|--------------|
| Sprint 1 — Foundation | ✅ Selesai | Setup repo, pahami struktur, modifikasi tema |
| Sprint 2 — Core Features | 🔲 Mendatang | 3+ screens, navigasi, CRUD, local storage |
| Sprint 3 — Advanced | 🔲 Mendatang | Search debounce, filter, sort, offline |
| Sprint 4 — Polish | 🔲 Mendatang | Bug fixes, UI polish, 10+ unit tests |
| Sprint 5 — Final | 🔲 Mendatang | Demo, slides, APK, README lengkap |

---

## 👨‍🏫 Dosen Pengampu

**Pak Habib** — [GitHub: mh4Scripts](https://github.com/mh4Scripts)

**Program Studi Teknik Informatika**
Institut Teknologi Sumatera (ITERA)

---

*InventRa dikembangkan sebagai project mata kuliah Pengembangan Aplikasi Mobile menggunakan Kotlin Multiplatform.*