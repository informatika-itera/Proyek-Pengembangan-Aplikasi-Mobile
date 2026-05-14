# PocketGuard - Expense Tracker AI

PocketGuard adalah aplikasi manajemen keuangan personal berbasis mobile yang membantu pengguna melacak pengeluaran dan pemasukan dengan bantuan kecerdasan buatan (Gemini AI). Aplikasi ini dibangun menggunakan **Kotlin Multiplatform (KMP)** dan **Compose Multiplatform**.

## 🚀 Fitur Utama
- **Pencatatan Keuangan**: Simpan data pengeluaran dan pemasukan secara lokal (Offline-first).
- **Analisis AI**: Menggunakan Gemini AI untuk memberikan saran penghematan atau ringkasan pengeluaran bulanan.
- **Kategori Pintar**: Pengelompokan transaksi berdasarkan kategori (Makanan, Transportasi, Hiburan, dll).
- **Dark Mode Support**: Tampilan yang nyaman di mata untuk penggunaan malam hari.

## 🛠️ Teknologi
- **Framework**: Kotlin Multiplatform & Compose Multiplatform
- **Database Lokal**: SQLDelight (SQLite)
- **Networking**: Ktor Client
- **AI Engine**: Google Gemini API
- **Dependency Injection**: Koin

## 📋 Anggota Kelompok
- **Jefri Wahyu Fernando Sembiring** - 123140206 
- **Arta Eka** - 123140209 


## 🏗️ Struktur Proyek
Proyek ini mengikuti **Clean Architecture** dengan pembagian modul:
- `composeApp`: UI dan Logic untuk Android & iOS.
- `shared`: (Optional jika ada) Logic yang digunakan bersama di luar UI.

---
*Proyek ini dikembangkan untuk memenuhi Tugas Besar Mata Kuliah Pengembangan Aplikasi Mobile - Informatika ITERA.*