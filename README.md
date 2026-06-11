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

---

## ✨ Fitur

### Core Features
- [x] **Food Inventory**: Daftar stok makanan yang tersimpan.
- [x] **Expiry Alert**: Indikator visual untuk makanan yang mendekati tanggal kedaluwarsa.
- [x] **Food Calendar**: Melihat jadwal expired dalam tampilan kalender.
- [x] **Theme Support**: Dukungan Light Mode dan Dark Mode.

### Recipe Feature
- [x] **Masak dari Stok**: Memilih bahan langsung dari inventory atau input manual.
- [x] **Recipe API Integration**: Menggunakan **TheMealDB API** untuk mencari resep nyata berdasarkan bahan yang dimiliki pengguna.
- [x] **Smart Matching Logic**: Menghitung skor kecocokan bahan untuk memberikan rekomendasi terbaik.
- [x] **Local Fallback**: Jika API tidak tersedia atau resep tidak ditemukan, sistem otomatis beralih ke *Rule-based Engine* lokal.
- [x] **Preferensi Resep**: Pilihan kategori masakan (Cepat, Praktis, Sehat).
- [!] **Catatan**: Resep yang bersumber dari API publik saat ini tersedia dalam Bahasa Inggris.

### AI Assistant
- [x] **Gemini AI Integration**: Fitur asisten cerdas untuk tanya jawab seputar tips penyimpanan makanan dan ide masak kreatif.
- [x] **Asisten AI**: Halaman khusus "Asisten AI" yang ditenagai oleh Google Gemini API (Berbeda dengan fitur rekomendasi resep utama).

---

## 🏗️ Arsitektur

Aplikasi ini dibangun menggunakan **Clean Architecture** dan **MVVM**:

- **Presentation Layer**: Compose Multiplatform & ViewModel.
- **Domain Layer**: Models, Repository Interfaces, & Rule-based Engine.
- **Data Layer**: Repository Implementation, SQLDelight (Local), & Ktor Client (Remote API).

---

## 🧪 Testing & CI

Project ini dilengkapi dengan:
- **Unit Tests**: Menguji logika pemetaan bahan, penghitungan expired, dan fallback resep.
- **CI Safety**: Workflow GitHub Actions memastikan build tetap passing tanpa memerlukan API key rahasia di lingkungan testing.

**Menjalankan Test:**
```powershell
.\gradlew test
```

---

## 🍳 Logika Rekomendasi Resep

Fitur "Masak dari Stok" kini bekerja dengan alur berikut:
1. **Mapping**: Bahan Bahasa Indonesia diterjemahkan ke Bahasa Inggris.
2. **Fetch**: Sistem mencari resep di TheMealDB berdasarkan bahan utama.
3. **Scoring**: Resep diurutkan berdasarkan seberapa banyak bahan pengguna yang digunakan dalam resep tersebut.
4. **Fallback**: Jika terjadi error jaringan atau hasil kosong, sistem menggunakan *Rule-based Engine* lokal untuk memberikan ide masakan dasar (seperti Nasi Goreng atau Telur Dadar).

---

## 📄 Lisensi
**MIT License** — Dibuat untuk keperluan pembelajaran Mata Kuliah Pengembangan Aplikasi Mobile ITERA.
