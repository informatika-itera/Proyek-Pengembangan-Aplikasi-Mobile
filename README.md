# 🥗 FitKos

![CI](https://github.com/raapstronaut/Proyek-Pengembangan-Aplikasi-Mobile/actions/workflows/ci.yml/badge.svg?branch=project%2F123140046-123140173-FitKos)

FitKos adalah aplikasi Android berbasis Kotlin yang dirancang untuk membantu penghuni kos menjaga pola hidup sehat sesuai budget harian. Aplikasi ini membantu pengguna mencatat makanan dan pengeluaran makan, memantau jumlah minum air, menandai olahraga ringan, serta mendapatkan tips sehat yang sederhana dan realistis.

FitKos juga direncanakan memiliki AI Assistant yang dapat membaca data harian pengguna, memberikan rekomendasi berdasarkan data yang diinput pengguna dan memberikan evaluasi serta saran sehat hemat dengan gaya bahasa yang santai dan mudah dipahami.

## 👥 Tim Pengembangan


| Nama | NIM | GitHub |
| --- | --- | --- |
| Jana Rohman Wasiso | 123140046 | [@10-046-JanaRohman](https://github.com/10-046-JanaRohman) |
| Muhammad Rafi Ilham | 123140173 | [@raapstronaut](https://github.com/raapstronaut) |

## ✨ Fitur Aplikasi

### 🟢 Fitur Minimum

- [ ] Dashboard harian
- [ ] Home Screen
- [ ] Daily check-in
- [ ] Catatan makanan dan budget harian
- [ ] Water tracker
- [ ] Exercise checklist
- [ ] Healthy tips
- [ ] Multi-screen navigation
- [ ] Local storage / CRUD
- [ ] State management dengan StateFlow

### 🤖 Fitur Bonus

- [ ] AI Assistant dengan Gemini API
- [ ] Evaluasi kebiasaan harian
- [ ] Dark mode
- [ ] Offline-first storage
- [ ] CI/CD dengan GitHub Actions

## 🏗️ Arsitektur & Teknologi

FitKos direncanakan menggunakan pendekatan Clean Architecture dan MVVM agar kode lebih rapi, mudah dikembangkan, dan mudah diuji.

- Presentation Layer: UI, Screen, Navigation, ViewModel, dan StateFlow
- Domain Layer: Model, Repository Interface, dan Use Case
- Data Layer: Repository Implementation, Local Data Source, Remote Data Source, dan Model Data
- Dependency Injection: Menggunakan Koin

## 🧰 Tech Stack

- Kotlin Multiplatform
- Compose Multiplatform
- Material Design 3
- MVVM
- Clean Architecture
- StateFlow
- SQLDelight
- DataStore
- Ktor Client
- Koin
- Gemini API
- GitHub Actions

## 📁 Struktur Project

Project ini menggunakan struktur folder KMP dengan pembagian layer yang jelas di `commonMain`:

```text
composeApp/src/commonMain/kotlin/com/example/fitkos/
├── data/
├── domain/
├── presentation/
└── di/
```

## 🚀 Getting Started

Clone Repository

```bash
git clone https://github.com/raapstronaut/FitKos.git
```

Checkout Branch

```bash
git checkout project/123140046-123140173-FitKos
```

Setup API Key

Tambahkan `GEMINI_API_KEY=your_key` di file `local.properties`.

Build & Run

Buka di Android Studio Ladybug+ dan jalankan task:

```bash
:composeApp:installDebug
```

Atau jalankan melalui terminal:

```bash
gradlew.bat :composeApp:assembleDebug
```

Template ini diperbarui untuk memenuhi persyaratan Sprint 1 mata kuliah Pengembangan Aplikasi Mobile - ITERA.
