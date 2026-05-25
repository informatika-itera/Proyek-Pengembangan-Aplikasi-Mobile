# 🥗 FitKos - AI Healthy Lifestyle Assistant

![CI](https://github.com/raapstronaut/FitKos/actions/workflows/ci.yml/badge.svg?branch=project%2F123140046-123140173-FitKos)


FitKos adalah aplikasi Android berbasis Kotlin yang dirancang untuk membantu penghuni kos menjaga pola hidup sehat sesuai budget harian. Aplikasi ini membantu pengguna mencatat makanan dan pengeluaran makan, memantau jumlah minum air, menandai olahraga ringan, serta mendapatkan tips sehat yang sederhana dan realistis.

FitKos juga direncanakan memiliki AI Assistant yang dapat membaca data harian pengguna, memberikan rekomendasi berdasarkan data yang diinput pengguna dan memberikan evaluasi serta saran sehat hemat dengan gaya bahasa yang santai dan mudah dipahami.

## 👥 Tim Pengembangan


| Nama | NIM | GitHub |
| --- | --- | --- |
| Jana Rohman Wasiso | 123140046 | [@10-046-JanaRohman](https://github.com/10-046-JanaRohman) |
| Muhammad Rafi Ilham | 123140173 | [@raapstronaut](https://github.com/raapstronaut) |

## ✨  Fitur Utama
### Sudah Diimplementasikan pada Sprint 2
- [x] Dashboard harian
- [x] Catatan makanan harian
- [x] Pencatatan harga/pengeluaran makan
- [x] Add/Edit catatan makanan
- [x] Detail catatan makanan
- [x] Local storage untuk menyimpan catatan makanan
- [x] Basic CRUD untuk catatan makanan
- [x] Multi-screen navigation
- [x] AI Assistant dasar dengan Gemini API

### Direncanakan untuk Sprint Berikutnya
- [ ] Target minum air
- [ ] Checklist olahraga ringan
- [ ] Tips menu sehat hemat
- [ ] Search/filter catatan makanan
- [ ] Dark mode
- [ ] Offline-first enhancement
- [ ] Evaluasi kebiasaan harian berbasis data pengguna

## 🏗️ Arsitektur & Teknologi Stack
FitKos menggunakan pendekatan Clean Architecture dan MVVM agar kode lebih rapi, mudah dikembangkan, dan mudah diuji.

- Presentation Layer: UI, Screen, Navigation, ViewModel, dan StateFlow
- Domain Layer: Model, Repository Interface, dan Use Case
- Data Layer: Repository Implementation, Local Data Source, Remote Data Source, dan Model Data
- Dependency Injection: Menggunakan Koin

| Layer | Technology |
|---|---|
| Language | Kotlin |
| Framework | Kotlin Multiplatform |
| UI | Compose Multiplatform, Material Design 3 |
| Architecture | MVVM, Clean Architecture, StateFlow |
| Storage | SQLDelight, DataStore |
| Networking & AI | Ktor Client, Gemini API |
| Dependency Injection | Koin |
| CI/CD | GitHub Actions |

## 📁 Project Structure

```text
├── core/                         # Di, network, util
├── data/                         # Local, remote, repository
├── domain/                       # Model, repository, usecases
└── presentation/
    ├── components/               # Shared UI components
    ├── navigation/               # App routes and navigation host
    ├── screens/
    │   ├── dashboard/            # Daily summary and quick actions
    │   ├── home/                 # Meal log list
    │   ├── addnote/              # Add/Edit meal log
    │   ├── detail/               # Meal log detail
    │   └── ai/                   # FitKos AI Assistant
    └── theme/                    # Material theme
```

## 📌 Sprint 1 - Planning & Setup
### Deliverables Sprint 1
- [x] GitHub repository dibuat
- [x] Semua anggota tim ditambahkan sebagai collaborator
- [x] Project Kotlin Multiplatform berhasil disiapkan
- [x] Struktur folder project mengikuti pembagian architecture layer
- [x] GitHub Actions CI disiapkan
- [x] CI badge ditampilkan pada README
- [x] README awal dibuat
- [x] Ide aplikasi dan rencana fitur ditentukan
- [x] Pembagian tugas awal tim dibuat

## 📌 Sprint 2 - Core Features
### Deliverables Sprint 2
- [x] Minimal 3 working screens
- [x] Navigation antar screen dengan arguments
- [x] Data layer menggunakan Repository pattern
- [x] Local storage menggunakan SQLDelight
- [x] Basic CRUD operations working
- [x] UI states dasar
- [x] Semua fitur utama dapat diakses dari aplikasi
- [x] CI tetap passing
      
### CRUD Operations
- Create: menambahkan catatan makanan
- Read: menampilkan daftar dan detail catatan makanan
- Update: mengubah catatan makanan
- Delete: menghapus catatan makanan

### 🎥 Demo Sprint 2
[Demo](https://youtu.be/MZyQGsvdlZo)

## 🚀 Getting Started

1. Clone Repository

```bash
git clone https://github.com/raapstronaut/FitKos.git
```

2. Setup API Key

Tambahkan `GEMINI_API_KEY=your_key` di file `local.properties`.

3. Buka di Android Studio Ladybug+ dan jalankan task: `:composeApp:installDebug`

## 📄 Lisensi

---MIT License — dibuat untuk keperluan pembelajaran Pengembangan Aplikasi Mobile ITERA.
