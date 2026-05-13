# 🌸 ROSÉA

> **Aplikasi Beauty & Skincare E-Commerce Berbasis Kotlin Multiplatform dengan Modern UI untuk Pengalaman Belanja Makeup dan Self-Care yang Elegan**

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android%20%7C%20iOS-brightgreen?style=for-the-badge&logo=kotlin" />
  <img src="https://img.shields.io/badge/Kotlin-Multiplatform-7F52FF?style=for-the-badge&logo=kotlin" />
  <img src="https://img.shields.io/badge/UI-Compose%20Multiplatform-4285F4?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Architecture-Clean%20Architecture-ff69b4?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Sprint-11--15%20%E2%9C%85-success?style=for-the-badge" />
</p>

---

# 📌 Identitas Proyek

| Item | Detail |
|------|--------|
| **Nama Aplikasi** | ROSÉA |
| **Mata Kuliah** | Pengembangan Aplikasi Mobile (PAM) |
| **Program Studi** | Teknik Informatika |
| **Institusi** | Institut Teknologi Sumatera (ITERA) |
| **Dosen Pengampu** | Pak Habib (mh4Scripts) |

---

# 👥 Tim Pengembang

| NIM | Nama | Peran |
|-----|------|-------|
| 123140067 | Andini Rahma Kemala | UI/UX, Presentation Layer, Feature Development |
| 123140064 | Miftahul Khoiriyah | Backend Integration, Architecture, Data Layer |

> **Branch:** project/123140064-123140067-ROSÉA

---

# 🎯 Deskripsi Aplikasi

**ROSÉA** adalah aplikasi mobile beauty e-commerce berbasis Kotlin Multiplatform yang menyediakan pengalaman belanja modern untuk produk makeup, skincare, fragrance, dan self-care products.

Aplikasi ini dikembangkan menggunakan pendekatan **Clean Architecture + MVI** serta mendukung berbagai platform seperti Android, iOS, Desktop, dan Web menggunakan satu codebase.

ROSÉA dirancang untuk memberikan pengalaman shopping yang:
- Modern
- Elegant
- Responsive
- Aesthetic
- Production-like

Dengan fitur:
- Shopping cart
- Wishlist
- Checkout system
- Product search
- Notifications
- Dynamic product catalog
- User profile management

---

# ✨ Fitur Utama

## Sprint 1 — Foundation
- [x] Setup project Kotlin Multiplatform
- [x] Struktur Clean Architecture + MVI
- [x] Dependency Injection dengan Koin
- [x] Navigation setup
- [x] Material 3 Theme
- [x] Multi-platform configuration

---

## Sprint 2 — Core Features
- [x] Home Screen
- [x] Product Detail Screen
- [x] Wishlist Feature
- [x] Shopping Cart
- [x] Product Categories
- [x] Authentication Flow
- [x] User Profile
- [x] Search Product
- [x] Checkout Flow

---

## Sprint 3 — Advanced Features
- [x] API Integration menggunakan Ktor
- [x] Dynamic Product Data
- [x] Product Recommendation
- [x] Notification System
- [x] Cross-platform Support
- [x] Modern State Management
- [x] Responsive UI Layout

---

## Sprint 4 — Polish & Optimization
- [x] Modern UI/UX Polish
- [x] Error Handling
- [x] Performance Optimization
- [x] Smooth Navigation
- [x] Dark Mode Support
- [x] Splash Screen Animation

---

## Sprint 5 — Final Preparation
- [x] Demo-ready Application
- [x] Organized Project Structure
- [x] Documentation
- [x] APK Build
- [x] Presentation Preparation

---

# 🏗️ Arsitektur & Teknologi

## Clean Architecture + MVI

```text
┌─────────────────────────────────────────────┐
│            PRESENTATION LAYER               │
│   Compose UI • Screen • State • MVI         │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│               DOMAIN LAYER                  │
│       UseCase • Business Logic • Model      │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│                DATA LAYER                   │
│ Repository • API • Local Storage            │
└─────────────────────────────────────────────┘
```

---

# 🧰 Tech Stack

| Layer | Teknologi |
|-------|-----------|
| **UI** | Compose Multiplatform |
| **Language** | Kotlin Multiplatform |
| **Architecture** | Clean Architecture + MVI |
| **State Management** | StateFlow |
| **Dependency Injection** | Koin |
| **Networking** | Ktor Client |
| **Async Programming** | Kotlin Coroutines |
| **Image Loading** | Coil 3 |
| **Preferences** | DataStore |
| **Navigation** | Compose Navigation |
| **Design System** | Material 3 |

---

# 📁 Struktur Project

```text
composeApp/src/
├── commonMain/kotlin/com/rosea/
│   ├── core/
│   │   ├── di/
│   │   ├── network/
│   │   └── util/
│   │
│   ├── data/
│   │   ├── remote/
│   │   │   ├── api/
│   │   │   └── dto/
│   │   ├── local/
│   │   └── repository/
│   │
│   ├── domain/
│   │   ├── model/
│   │   ├── repository/
│   │   └── usecase/
│   │
│   └── presentation/
│       ├── home/
│       ├── product/
│       ├── wishlist/
│       ├── cart/
│       ├── checkout/
│       ├── profile/
│       ├── search/
│       ├── notification/
│       ├── components/
│       └── theme/
│
├── androidMain/kotlin/
└── iosMain/kotlin/
```

---

# 📚 Implementasi Berdasarkan Materi PAM 11–15

## 📖 Pertemuan 11 — Planning & Setup

### Implementasi
- Setup Kotlin Multiplatform
- Clean Architecture
- Dependency Injection
- GitHub Workflow
- Multi-module structure

### Hasil Pembelajaran
✔ Struktur project modern  
✔ Arsitektur scalable  
✔ Workflow development profesional  

---

## 📖 Pertemuan 12 — Core Features

### Implementasi
- Shopping Cart
- Wishlist
- Product Detail
- Authentication
- Navigation
- User Profile

### Hasil Pembelajaran
✔ State management  
✔ Navigation Compose  
✔ Repository Pattern  
✔ Reactive UI  

---

## 📖 Pertemuan 13 — Advanced Features

### Implementasi
- REST API Integration
- Dynamic Product Data
- Notification Feature
- Search Product
- Recommendation System

### Hasil Pembelajaran
✔ API integration  
✔ Dynamic data handling  
✔ Advanced application feature  

---

## 📖 Pertemuan 14 — Polish & Testing

### Implementasi
- UI consistency
- Error handling
- Performance optimization
- Responsive design

### Hasil Pembelajaran
✔ Better UX  
✔ Stable architecture  
✔ Modern application design  

---

## 📖 Pertemuan 15 — Final Preparation

### Implementasi
- Documentation
- APK Release
- Demo-ready application
- Presentation preparation

### Hasil Pembelajaran
✔ Production-like application  
✔ Professional presentation setup  

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
git clone https://github.com/USERNAME/ROSEA.git
cd ROSEA
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

ROSÉA menggunakan konsep:
- Elegant Beauty App
- Feminine Modern UI
- Soft Pink Aesthetic
- Luxury Beauty Branding
- Clean Shopping Experience

## Color Palette
- Soft Pink
- Rose Nude
- Cream White
- Dusty Rose

---

# 📱 Main Screens

- Splash Screen
- Home Screen
- Product Detail
- Wishlist
- Cart
- Checkout
- Search
- Notifications
- Orders
- Profile

---

# 📈 Keunggulan Project

## ✨ Modern Architecture
Menggunakan Clean Architecture dan MVI Pattern.

## 🌐 Cross Platform
Satu codebase untuk Android, iOS, Desktop, dan Web.

## 🎨 Modern UI/UX
Menggunakan Compose Multiplatform dan Material 3.

## 📦 Scalable Structure
Mudah dikembangkan untuk fitur tambahan.

## 🏭 Production-like Project
Menggunakan struktur dan workflow seperti aplikasi industri modern.

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
git checkout project/[NIM]-[NIM]-ROSEA

# Pull update terbaru
git pull origin project/[NIM]-[NIM]-ROSEA

# Commit changes
git commit -m "feat: add wishlist feature"
git commit -m "fix: resolve navigation issue"
git commit -m "style: improve product card UI"
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
- Koin DI
- Ktor Client
- Material 3
- Kotlin Coroutines

---

# 📄 Lisensi

Project ini dibuat untuk keperluan pembelajaran dan tugas besar mata kuliah Pengembangan Aplikasi Mobile (PAM).

---

# 🌸 ROSÉA

> _“Glow Beyond Beauty”_
