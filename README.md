# TripMate ✈️

> **Travel Planning & Tracking Companion** — Rencanakan, jalani, dan kenang perjalananmu dalam satu aplikasi.

## 📖 Tentang TripMate

TripMate adalah aplikasi mobile travel planning & tracking yang dirancang untuk mendampingi pengguna di setiap tahap perjalanan — mulai dari perencanaan awal, selama perjalanan berlangsung, hingga evaluasi dan kenangan setelah trip selesai.

Berbeda dari aplikasi travel lainnya yang berfokus pada rekomendasi otomatis, TripMate menempatkan pengguna sebagai pencatat aktif perjalanannya sendiri. AI hadir sebagai asisten — merangkum, menghitung, dan merekomendasikan — bukan menggantikan pengalaman personal.

---

## ✨ Fitur Utama

### Pre-Trip Planning
- **Itinerary Builder** — Susun jadwal perjalanan per hari dengan detail destinasi
- **Packing Checklist** — Template daftar bawaan berdasarkan jenis trip
- **Budget Planning** — Atur anggaran per kategori pengeluaran
- **Weather Check** — Cek prakiraan cuaca di destinasi tujuan

### During Trip
- **Expense Tracker** — Catat pengeluaran real-time per orang per kategori
- **Map Pin** — Tandai lokasi yang sudah dikunjungi vs yang direncanakan
- **Photo Notes** — Lampirkan foto pada catatan destinasi
- **Offline Mode** — Akses data tanpa koneksi internet

### Post-Trip
- **Post-Trip Review** — Evaluasi apa yang sesuai dan tidak sesuai dari rencana
- **Memory Journal** — Catatan kenangan dan foto per destinasi
- **Trip Summary** — Statistik lengkap: total pengeluaran, destinasi visited vs planned
- **Rating Destinasi** — Beri bintang dan ulasan untuk setiap tempat
- **Export PDF** — Simpan ringkasan trip dalam format PDF

### AI Features *(powered by Gemini API)*
- **AI Trip Summary** — Rangkum seluruh perjalanan secara otomatis
- **AI Split Bill** — Hitung patungan cerdas dari input pengeluaran

### Social & Group
- **Split Bill Tracker** — Catat siapa membayar apa dalam perjalanan grup
- **Trip Sharing** — Bagikan itinerary ke teman satu rombongan
- **Wishlist Destinasi** — Simpan daftar tempat impian yang ingin dikunjungi

---

## 🛠️ Tech Stack

| Layer | Teknologi |
|---|---|
| Language | Kotlin Multiplatform (KMP) |
| UI | Compose Multiplatform |
| Architecture | Clean Architecture + MVVM |
| Local Database | SQLDelight |
| Networking | Ktor |
| Dependency Injection | Koin |
| Preferences | DataStore |
| AI | Gemini AI API |

---

## 📁 Struktur Proyek

```
TripMate/
├── shared/                     # Shared KMP module
│   ├── commonMain/
│   │   ├── data/               # Repository, data source, models
│   │   ├── domain/             # Use cases, domain models
│   │   └── presentation/       # ViewModels
│   └── androidMain/            # Platform-specific implementations
│       └── DatabaseDriverFactory.kt
├── composeApp/                 # Android UI module
│   └── src/androidMain/
│       └── ui/
│           ├── home/
│           ├── trips/
│           ├── expense/
│           ├── ai/
│           └── profile/
└── local.properties            # SDK path & API keys (tidak di-commit)
```

---

## 🚀 Cara Menjalankan

### Prasyarat
- Android Studio Hedgehog atau lebih baru
- JDK 17+
- Android SDK (API 26+)
- Gemini API Key ([dapatkan di sini](https://aistudio.google.com/app/apikey))

### Setup

1. **Clone repository**
   ```bash
   git clone https://github.com/Ramaaaadevs/Proyek-Pengembangan-Aplikasi-Mobile.git
   cd Proyek-Pengembangan-Aplikasi-Mobile
   git checkout project/123140116-123140135-TripMate
   ```

2. **Buat file `local.properties`** di root project
   ```properties
   # Linux/Ubuntu
   sdk.dir=/home/[username]/Android/Sdk

   # Windows
   # sdk.dir=C\:\\Users\\[username]\\AppData\\Local\\Android\\Sdk

   GEMINI_API_KEY=api_key_kamu
   ```

3. **Buka di Android Studio** → Tunggu Gradle sync selesai → Run ▶️

---

## 👥 Tim Pengembang

| NIM | Nama |
|---|---|
| 123140116 | Diwan Ramadhani Dwi Putra |
| 123140135 | *(Partner)* |

**Mata Kuliah:** Pengembangan Aplikasi Mobile (IF25-22017)  
**Institusi:** Institut Teknologi Sumatera (ITERA)

---

## 📅 Sprint Timeline

| Sprint | Minggu | Target |
|---|---|---|
| Sprint 1 | 11 | Setup repo, fork, branch, build sukses |
| Sprint 2 | 12 | 3+ screens, navigasi, CRUD, local storage |
| Sprint 3 | 13 | Search, filter, sort, API/AI, offline support |
| Sprint 4 | 14 | Bug fixes, UI polish, 10+ unit tests |
| Sprint 5 | 15 | Demo ready, APK, README lengkap |
| UAS | 16 | Live demo & presentasi |

---

## 📄 Lisensi

Proyek ini dibuat untuk keperluan akademik di Institut Teknologi Sumatera.  
Template dasar menggunakan **NoteAI** oleh mh4Scripts.

---

*Last updated: Sprint 1, Week 11*
