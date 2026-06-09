# 🥗 FoodSaver

[![FoodSaver CI](https://github.com/rdtngh/123140089-123140125-FoodSaver/actions/workflows/build.yml/badge.svg)](https://github.com/rdtngh/123140089-123140125-FoodSaver/actions/workflows/build.yml)



> Track. Cook. Save Food.

FoodSaver adalah aplikasi mobile multiplatform berbasis Android-first yang membantu pengguna mencatat stok makanan, memantau tanggal kedaluwarsa, mendapatkan pengingat makanan yang hampir expired, serta memperoleh rekomendasi resep dari bahan yang tersedia agar makanan tidak terbuang.

---

## 👥 Tim

| Nama | NIM | GitHub | Role |
| :--- | :--- | :--- | :--- |
| Bening Apni Prameswari | 123140089 | [@beningapniprameswari](https://github.com/beningapniprameswari) | Lead & UI/UX Developer |
| Raditya Alrasyid Nugroho | 123140125 | [@rdtngh](https://github.com/rdtngh) | Logic & Android Dev |

**Mata Kuliah:** IF25-22017 Pengembangan Aplikasi Mobile  
**Dosen Pengampu:** Pak Habib [@mh4Scripts](https://github.com/mh4Scripts)

---

## 📝 Deskripsi Aplikasi

FoodSaver dirancang sebagai solusi cerdas untuk mengelola bahan makanan di rumah dan menekan angka *food waste*. Seringkali kita lupa dengan bahan makanan yang tersimpan di kulkas hingga akhirnya melewati tanggal kedaluwarsa. FoodSaver hadir untuk memastikan hal tersebut tidak terjadi lagi.

Aplikasi ini membantu pengguna untuk:
- **Mencatat stok makanan**: Mengelola inventaris bahan makanan dengan mudah beserta kategorinya.
- **Manajemen Kedaluwarsa**: Menyimpan tanggal expired dan memantau statusnya secara real-time (Aman, Hampir Expired, atau Expired).
- **Rekomendasi Cerdas**: Menampilkan bahan yang harus segera dimasak pada halaman utama dan memberikan saran melalui kartu AI FoodSaver.
- **Visualisasi Kalender**: Melihat jadwal kedaluwarsa bahan makanan dalam tampilan kalender yang intuitif.
- **Masak dari Stok**: Fitur pencarian resep berdasarkan bahan yang sudah ada di inventaris atau input manual, membantu pengguna menentukan menu masakan tanpa bingung.
- **Pengalaman Pengguna**: Mendukung mode gelap (*dark mode*) dan terang (*light mode*) untuk kenyamanan visual, serta pengaturan notifikasi pengingat.

---

## ✨ Fitur

### Core Features
- [x] **Food Inventory**: Daftar stok makanan yang tersimpan.
- [x] **Add/Edit Food**: Menambah dan mengubah data makanan serta tanggal kedaluwarsa.
- [x] **Detail Makanan**: Informasi lengkap mengenai stok bahan.
- [x] **Expiry Alert**: Indikator visual untuk makanan yang mendekati tanggal kedaluwarsa.
- [x] **Food Calendar**: Melihat estimasi expired bahan makanan di halaman kalender.
- [x] **Profile & Pengaturan**: Personalisasi akun dan preferensi aplikasi.
- [x] **Search & Filter**: Mencari bahan makanan dan memfilternya berdasarkan kategori.
- [x] **Bottom Navigation**: Navigasi antar halaman yang mudah.
- [x] **Status Kedaluwarsa Otomatis**: Logika perhitungan sisa hari secara real-time.
- [x] **Empty State & Error Handling**: Tampilan informatif saat data kosong atau terjadi kesalahan.
- [x] **Theme Support**: Dukungan Light Mode dan Dark Mode.

### Recipe Feature
- [x] **Page Resep**: Halaman khusus untuk mencari inspirasi masakan.
- [x] **Masak dari Stok**: Memilih bahan langsung dari inventory yang tersedia.
- [x] **Input Bahan Manual**: Menambahkan bahan tambahan di luar inventory menggunakan sistem Chip/Tag.
- [x] **Rule-based Recipe Recommendation**: Algoritma rekomendasi resep berdasarkan kombinasi bahan.
- [x] **Preferensi Resep**: Pilihan kategori masakan (Cepat, Praktis, Sehat).
- [x] **Toggle Prioritas**: Mengutamakan bahan yang hampir expired untuk dijadikan bahan resep.
- [x] **Hasil Rekomendasi**: Menampilkan detail resep yang disarankan.
- [x] **Reset Bahan**: Membersihkan pilihan bahan dengan satu klik.
- [ ] **AI Integration**: Rencana integrasi AI API pada pengembangan tahap selanjutnya.

### Reminder & Settings
- [x] **Tema Aplikasi**: Switcher antara Dark Mode dan Light Mode.
- [x] **Pengaturan Notifikasi**: Opsi untuk mengaktifkan pengingat.
- [ ] **Push Reminder**: Fitur pengingat otomatis ke perangkat (Target pengembangan).

---

## 🏗️ Arsitektur

Aplikasi ini dibangun menggunakan prinsip **Clean Architecture** untuk memastikan kode yang mudah dikelola, diuji, dan dikembangkan.

- **Kotlin Multiplatform (KMP)** & **Compose Multiplatform**
- **MVVM (Model-View-ViewModel)** Pattern
- **Repository Pattern**
- **StateFlow & UI State** untuk manajemen reaktif UI.

### Diagram Arsitektur

```text
Presentation Layer:
    [Screens] <-> [Components] <-> [ViewModel] <-> [UI State]
          |
Domain Layer:
    [Model] <-> [Repository Interface] <-> [Use Case / Helper Logic]
          |
Data Layer:
    [Repository Impl] <-> [Local Data Source (SQLDelight/DataStore)] <-> [Rule-based Engine]
```

---

## 📂 Struktur Folder

```text
composeApp/src/commonMain/kotlin/com/example/foodsaver/
├── core/             # DI, Network, & Utilities
│   ├── di/           # Dependency Injection setup
│   ├── network/      # API configurations
│   └── util/         # Helper classes
├── data/             # Implementasi data layer
│   ├── local/        # Database (SQLDelight) & DataStore
│   ├── remote/       # API Service
│   └── repository/   # Implementasi Repository
├── domain/           # Business logic layer
│   ├── model/        # Data models / Entity
│   ├── repository/   # Repository interfaces
│   └── usecase/      # Application logic / Interactors
└── presentation/     # UI layer
    ├── navigation/   # NavGraph dan Route
    ├── theme/        # Color, Type, dan Theme (Material 3)
    ├── components/   # UI components yang reusable
    └── screens/      # Feature screens (Home, AddFood, Recipe, Calendar, AI, MealPlan, Profile, dsb)
```

---

## 🛠️ Tech Stack

| Komponen | Teknologi |
| :--- | :--- |
| **Framework** | Kotlin Multiplatform, Compose Multiplatform |
| **UI** | Material 3 |
| **Architecture** | MVVM, Clean Architecture, Repository Pattern |
| **Async/State** | Coroutines, Flow, StateFlow |
| **Local Storage** | SQLDelight (Database), DataStore (Preferences) |
| **Recipe Logic** | Rule-based Recommendation Engine |
| **CI/CD** | GitHub Actions |
| **Platform** | Android-first focus |

---

## 📅 Sprint Plan

| Sprint | Fokus Utama | Status |
| :--- | :--- | :--- |
| **Sprint 1** | Project setup, GitHub Repo, CI Setup, README awal, Planning. | ✅ Done |
| **Sprint 2** | UI Screens utama, Navigation, Data Layer (Local), CRUD Stok Makanan. | ✅ Done |
| **Sprint 3** | Expiry Alert logic, Calendar view, Profile, Dark Mode, Recipe Page awal. | ✅ Done |
| **Sprint 4** | **Bug fixes, UI Polish, Unit/UI Tests, Edge cases handling.** | 🚀 In Progress |
| **Sprint 5** | Final fixes, Documentation, Persiapan presentasi & demo. | ⏳ To Do |

---

## 🚀 Setup & Cara Menjalankan

### Prerequisites
- **Android Studio Ladybug** atau versi lebih baru.
- **JDK 17** atau yang lebih tinggi.
- **Android Emulator** atau perangkat fisik.

### Langkah-langkah
1. Clone repository ini:
   ```bash
   https://github.com/rdtngh/123140089-123140125-FoodSaver.git
   ```
2. Buka project di Android Studio.
3. Tunggu proses **Gradle Sync** selesai.
4. Jalankan aplikasi melalui tombol **Run** atau melalui terminal:

**Windows PowerShell:**
```powershell
.\gradlew :composeApp:installDebug
```

---

## 🧪 Testing

Pengembangan pada Sprint 4 menargetkan kualitas kode dengan:
- Minimal **10 Unit Tests** untuk Domain & Data logic.
- Minimal **3 UI Tests** menggunakan Compose Test library.
- Target coverage minimal **50%**.

**Menjalankan Unit Test:**
```powershell
.\gradlew test
```

**Verifikasi Build:**
```powershell
.\gradlew clean build
```

---

## ⏳ Logika Status Kedaluwarsa

FoodSaver menghitung selisih hari (`daysLeft`) antara tanggal saat ini dan tanggal kedaluwarsa:

- `daysLeft < 0`: **Expired** (Sudah Kedaluwarsa)
- `daysLeft == 0`: **Expired Hari Ini**
- `daysLeft` **1 s/d 3**: **Hampir Expired** (Label: Segera Masak)
- `daysLeft > 3`: **Aman**

Status ini diterapkan secara konsisten pada:
- Badge status di halaman Home & Inventory.
- Filter pada Expiry Alert.
- Indikator warna pada Food Calendar.
- Prioritas bahan dalam rekomendasi resep.

---

## 🍳 Logika Rekomendasi Resep

Rekomendasi resep bekerja menggunakan **Rule-based Engine** yang mencocokkan input bahan dengan database resep lokal.

**Parameter Input:**
- Bahan dari Inventory (Checkbox selection).
- Bahan Manual (Chip/Tag input).
- Preferensi (Cepat, Praktis, Sehat).

**Contoh Aturan (Rules):**
- `Nasi` + `Telur` → **Nasi Goreng Telur**
- `Mie` + `Bakso` → **Mie Bakso Praktis**
- `Roti` + `Susu` → **Roti Panggang Susu**
- `Buah` + `Susu` → **Smoothie Sehat**

*Catatan: Integrasi AI API yang sesungguhnya direncanakan pada tahap pengembangan backend di masa mendatang untuk menjaga keamanan API Key.*

---

## 🎨 UI/UX

Desain FoodSaver mengusung tema **Fresh Grocery** dengan karakteristik:
- **Color Palette**: Dominasi warna hijau (Fresh Green) yang melambangkan kesegaran makanan.
- **Rounded Design**: Penggunaan card dengan corner radius besar untuk kesan modern dan ramah.
- **Feedback Visual**: Snackbar untuk konfirmasi aksi dan status badge yang kontras (Merah untuk Expired, Kuning untuk Warning).
- **Responsive**: Mendukung orientasi layar dan adaptasi Dark/Light mode secara mulus.

---

## ⚙️ CI/CD

Project ini menggunakan **GitHub Actions** untuk memastikan integritas kode.
Setiap *Pull Request* akan memicu workflow build otomatis untuk memastikan:
1. Kode dapat dicompile dengan sukses.
2. Semua unit test lulus (passed).
3. Build artifact (APK) dapat dihasilkan tanpa error.

---
## Video Demonstrasi
https://github.com/user-attachments/assets/6fa632c2-4215-4743-bf04-352040a5a544

## 📄 Lisensi

**MIT License** — Dibuat untuk keperluan pembelajaran Mata Kuliah Pengembangan Aplikasi Mobile ITERA.
