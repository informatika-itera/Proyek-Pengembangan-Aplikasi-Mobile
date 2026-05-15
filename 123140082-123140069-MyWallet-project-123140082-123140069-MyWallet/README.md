# 💰 MyWallet

> **Aplikasi Personal Expense Tracker Berbasis Kotlin Multiplatform untuk Membantu Pengguna Mengelola Keuangan Harian Secara Mudah, Modern, dan Efisien**

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android%20%7C%20iOS-brightgreen?style=for-the-badge&logo=kotlin" />
  <img src="https://img.shields.io/badge/Kotlin-Multiplatform-7F52FF?style=for-the-badge&logo=kotlin" />
  <img src="https://img.shields.io/badge/UI-Compose%20Multiplatform-4285F4?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Architecture-Clean%20Architecture-blueviolet?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Sprint-11--15%20%E2%9C%85-success?style=for-the-badge" />
</p>

---

# 📌 Identitas Proyek

| Item | Detail |
|------|--------|
| **Nama Aplikasi** | MyWallet |
| **Mata Kuliah** | Pengembangan Aplikasi Mobile (PAM) |
| **Program Studi** | Teknik Informatika |
| **Institusi** | Institut Teknologi Sumatera (ITERA) |
| **Dosen Pengampu** | Pak Habib (mh4Scripts) |

---

# 👥 Tim Pengembang

| NIM | Nama | Peran |
|-----|------|-------|
| 123140082 | Hanifah Hasanah | Backend Developer, Database & Architecture Implementation |
| 123140069 | Zahwa Natasya Hamzah |UI/UX Designer, Dashboard & Statistics Feature Development|

> **Branch:** `project/123140082-123140069-mywallet`
> 
---

# 🎯 Deskripsi Aplikasi

**MyWallet** adalah aplikasi mobile personal expense tracker berbasis Kotlin Multiplatform yang dirancang untuk membantu pengguna mengelola pemasukan dan pengeluaran harian secara praktis, terstruktur, dan efisien.

Aplikasi ini menggunakan pendekatan **Clean Architecture + MVVM** untuk menghasilkan struktur project yang scalable, maintainable, dan modern.

MyWallet membantu pengguna untuk:
- Mencatat transaksi harian
- Mengelola budget
- Melihat statistik keuangan
- Memantau pengeluaran
- Mengontrol kondisi finansial secara realtime

Dengan fitur:
- Dashboard keuangan
- Expense & income tracking
- Transaction history
- Budget management
- Statistics & charts
- Dark mode
- Offline local storage

---

# ✨ Fitur Utama

## Sprint 1 — Foundation
- [x] Setup Kotlin Multiplatform Project
- [x] Clean Architecture + MVVM
- [x] Navigation Setup
- [x] Dependency Injection dengan Koin
- [x] Material 3 Theme
- [x] GitHub Actions CI/CD Setup

---

## Sprint 2 — Core Features
- [x] Dashboard Screen
- [x] Add Transaction Feature
- [x] Transaction History
- [x] Category Management
- [x] Navigation Multi-screen
- [x] State Management dengan StateFlow

---

## Sprint 3 — Advanced Features
- [x] Statistics & Financial Charts
- [x] Budget Planning Feature
- [x] Monthly Expense Analysis
- [x] Search & Filter Transactions
- [x] Local Database Integration
- [x] Offline First Support

---

## Sprint 4 — Polish & Testing
- [x] Dark Mode
- [x] UI/UX Improvements
- [x] Error Handling
- [x] Unit Testing
- [x] Responsive Layout
- [x] Performance Optimization

---

## Sprint 5 — Final Preparation
- [x] Documentation
- [x] APK Build
- [x] Final Testing
- [x] Demo-ready Application
- [x] Presentation Preparation

---

# 🏗️ Arsitektur & Teknologi

## Clean Architecture + MVVM

```text
┌─────────────────────────────────────────────┐
│           PRESENTATION LAYER                │
│  Compose UI • Screens • ViewModel • State   │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│               DOMAIN LAYER                  │
│     UseCase • Business Logic • Models       │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│                 DATA LAYER                  │
│ Repository • Local Database • DataSource    │
└─────────────────────────────────────────────┘
```

---

# 🧰 Tech Stack

| Layer | Teknologi |
|-------|-----------|
| **UI** | Compose Multiplatform |
| **Language** | Kotlin Multiplatform |
| **Architecture** | Clean Architecture + MVVM |
| **State Management** | StateFlow |
| **Dependency Injection** | Koin |
| **Local Database** | SQLDelight |
| **Preferences** | DataStore |
| **Async Programming** | Kotlin Coroutines |
| **Navigation** | Compose Navigation |
| **Design System** | Material 3 |

---

# 📁 Struktur Project

```text
composeApp/src/
├── commonMain/kotlin/com/mywallet/
│
├── di/
│   ├── AppModule.kt
│   ├── DataModule.kt
│   └── ViewModelModule.kt
│
├── data/
│   ├── local/
│   │   ├── database/
│   │   └── datastore/
│   │
│   ├── repository/
│   └── model/
│
├── domain/
│   ├── model/
│   ├── repository/
│   └── usecase/
│
├── presentation/
│   ├── home/
│   ├── addtransaction/
│   ├── statistics/
│   ├── history/
│   ├── settings/
│   ├── components/
│   ├── navigation/
│   └── theme/
│
├── androidMain/kotlin/
└── iosMain/kotlin/
```

---

# 📚 Implementasi Berdasarkan Materi PAM 11–15

## 📖 Pertemuan 11 — Planning & Setup

### Implementasi
- Setup Kotlin Multiplatform
- Setup Repository GitHub
- Clean Architecture
- Koin Dependency Injection
- GitHub Actions CI/CD

### Hasil Pembelajaran
✔ Struktur project modern  
✔ Workflow development profesional  
✔ Architecture scalable  

---

## 📖 Pertemuan 12 — Core Features

### Implementasi
- Dashboard Screen
- Add Transaction
- Transaction History
- Navigation Compose
- StateFlow State Management

### Hasil Pembelajaran
✔ CRUD operations  
✔ Navigation multi-screen  
✔ Reactive UI management  

---

## 📖 Pertemuan 13 — Advanced Features

### Implementasi
- Statistics Feature
- Budget Management
- Search & Filter
- Offline Database
- Monthly Analysis

### Hasil Pembelajaran
✔ Advanced state management  
✔ Local data persistence  
✔ Financial data visualization  

---

## 📖 Pertemuan 14 — Polish & Testing

### Implementasi
- Dark Mode
- Error Handling
- Unit Testing
- UI Improvements
- Responsive Layout

### Hasil Pembelajaran
✔ Better user experience  
✔ Stable application structure  
✔ Testing workflow  

---

## 📖 Pertemuan 15 — Final Preparation

### Implementasi
- Final Documentation
- APK Build
- Demo Preparation
- Final Optimization

### Hasil Pembelajaran
✔ Production-like application  
✔ Professional project documentation  

---

# 🚀 Cara Menjalankan

## Prasyarat

| Software | Versi Minimum |
|----------|---------------|
| Android Studio | Ladybug (2024.2.1) |
| JDK | 17 |
| Git | 2.x |
| Android SDK | API 34+ |

---

# ⚙️ Setup Project

## 1. Clone Repository

```bash
git clone https://github.com/USERNAME/MyWallet.git
cd MyWallet
```

---

## 2. Build Project

```bash
./gradlew build
```

---

## 3. Run Android

```bash
./gradlew :composeApp:installDebug
```

Atau jalankan langsung menggunakan Android Studio.

---

# 🎨 Design Concept

MyWallet menggunakan konsep:
- Modern Finance App
- Clean UI
- Minimalist Design
- Productivity-focused Interface
- User-friendly Experience

## Color Palette
- Emerald Green
- Dark Navy
- Soft White
- Light Gray

---

# 📱 Main Screens

- Splash Screen
- Dashboard Screen
- Add Transaction Screen
- Transaction History
- Statistics Screen
- Budget Screen
- Settings Screen

---

# 📈 Keunggulan Project

## 💰 Financial Management
Membantu pengguna mengontrol pengeluaran dan pemasukan harian.

## 🏗️ Modern Architecture
Menggunakan Clean Architecture dan MVVM Pattern.

## 🌐 Cross Platform
Satu codebase untuk Android dan iOS.

## 🎨 Modern UI/UX
Menggunakan Compose Multiplatform dan Material 3.

## 📦 Scalable Structure
Mudah dikembangkan untuk fitur tambahan di masa depan.

---

# 🧪 Testing

```bash
# Run all tests
./gradlew allTests

# Android unit tests
./gradlew :composeApp:testDebugUnitTest
```

---

# 📝 Git Workflow

```bash
# Pindah ke branch project
git checkout project/[NIM]-[NIM]-mywallet

# Pull update terbaru
git pull origin project/[NIM]-[NIM]-mywallet

# Commit changes
git commit -m "feat: add transaction feature"
git commit -m "fix: resolve dashboard bug"
git commit -m "style: improve statistics UI"
```

---

# 🔧 Troubleshooting

| Masalah | Solusi |
|---------|--------|
| Gradle sync error | Pastikan menggunakan JDK 17 |
| Build failed | Jalankan `./gradlew clean build` |
| Dependency error | Sync Gradle ulang |
| Emulator lambat | Gunakan emulator API 34 dengan RAM minimal 4GB |

---

# 📚 Referensi

- Kotlin Multiplatform
- Compose Multiplatform
- Koin Dependency Injection
- SQLDelight
- Kotlin Coroutines
- Material 3

---

# 📄 Lisensi

Project ini dibuat untuk keperluan pembelajaran dan tugas besar mata kuliah Pengembangan Aplikasi Mobile (PAM).

---

# 💰 MyWallet

> _“Track Smart, Spend Wise.”_
````
