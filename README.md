# 🍎 FoodSaver - KMP Project

Aplikasi **FoodSaver** berbasis **Kotlin Multiplatform** untuk membantu mengelola ketersediaan makanan dan mengurangi pemborosan makanan.

Aplikasi ini dikembangkan sebagai tugas mata kuliah **Pengembangan Aplikasi Mobile** di ITERA.

## ✨ Fitur Utama (Planned)

- 📝 **Inventory Makanan** - Pantau stok makanan di kulkas atau lemari
- ⏰ **Reminder Kedaluwarsa** - Notifikasi sebelum makanan basi
- 🤖 **AI Food Assistant** - Rekomendasi resep berdasarkan bahan yang hampir habis
- 📊 **Waste Tracker** - Statistik makanan yang terbuang
- 📱 **Cross-Platform** - Android & iOS

## 🏗️ Arsitektur & Teknologi

### Clean Architecture + MVVM

Aplikasi mengikuti pola **Clean Architecture** yang dibagi menjadi 3 layer utama:
1. **Presentation Layer**: UI (Compose) dan ViewModels.
2. **Domain Layer**: Business Logic, Models, dan Repository Interfaces.
3. **Data Layer**: Implementasi Repository, Local DB (SQLDelight), dan Remote API (Ktor).

### Tech Stack

| Layer | Technology |
|-------|------------|
| **UI** | Compose Multiplatform, Material 3 |
| **State Management** | StateFlow, ViewModel |
| **Navigation** | Compose Navigation (Type-safe) |
| **Networking** | Ktor Client |
| **Local DB** | SQLDelight |
| **Preferences** | DataStore |
| **DI** | Koin |
| **Testing** | Kotlin Test, Turbine |

## 📁 Struktur Project

```
composeApp/src/
├── commonMain/kotlin/com/example/foodsaver/
│   ├── core/                      # Core utilities & DI
│   ├── data/                      # Data layer (Local & Remote)
│   ├── domain/                    # Domain layer (Business Logic)
│   └── presentation/              # Presentation layer (UI & ViewModel)
```

## 🚀 Development

### CI/CD
Proyek ini dilengkapi dengan **GitHub Actions** untuk memastikan setiap perubahan kode melewati proses build dan test secara otomatis.

### Setup
1. Copy `local.properties.example` ke `local.properties`.
2. Masukkan `GEMINI_API_KEY` untuk fitur AI.
3. Sync Gradle dan jalankan aplikasi.

---
**Kelompok:**
- Radit (NIM: 123140089)
- Partner (NIM: 123140125)
