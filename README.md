# 🐬 StudyMate

[![Android Build & Test Pipeline](https://github.com/joyapul119140157/Proyek-Pengembangan-Aplikasi-Mobile/actions/workflows/ci.yml/badge.svg?branch=project%2F123140043-119140157-StudyMate)](https://github.com/joyapul119140157/Proyek-Pengembangan-Aplikasi-Mobile/actions)

<img width="1024" height="543" alt="image" src="https://github.com/user-attachments/assets/c7546e6c-321d-4f36-9086-34ee10f5dcb2" />

Aplikasi mobile multiplatform (Android & iOS) yang dirancang untuk menjembatani celah antara **"mencatat"** dan **"memahami"**. Dibangun dengan Kotlin Multiplatform (KMP), StudyMate memanfaatkan **Gemini AI** untuk mengubah catatan kelas yang berantakan menjadi materi belajar yang terstruktur dan interaktif.

---

## 👥 Tim

| Nama | NIM | GitHub | Role |
|------|-----|--------|------|
| Tengku Hafid Diraputra | 123140043 | [@ThDptr](https://github.com/ThDptr) | Lead & Android Dev |
| Joyapul Hanscalvin Panjaitan | 119140157 | [@joyapul119140157](https://github.com/joyapul119140157) | FE Dev & QA |

**Mata Kuliah:** IF25-22017 Pengembangan Aplikasi Mobile  
**Dosen:** Pak Habib ([@mh4Scripts](https://github.com/mh4Scripts))  
**Institut:** Institut Teknologi Sumatera (ITERA)

---

## 📱 Deskripsi Aplikasi

StudyMate bekerja dalam **satu siklus pembelajaran yang utuh**, dari input mentah hingga pengujian pemahaman:

1. **Capture**: Masukkan catatan cepat saat kuliah berlangsung — fokus pada kecepatan, bukan kerapian.
2. **Refine**: Gemini AI memproses teks: memperbaiki struktur, menambah konteks, dan menjelaskan istilah teknis.
3. **Consistency**: Sistem Streaks memotivasi pengguna untuk mencatat minimal satu materi setiap hari.
4. **Recall**: Catatan yang telah dirapikan diolah menjadi kuis otomatis untuk menguji pemahaman.

---

## 🏗️ Arsitektur & Tech Stack

- **Framework:** Compose Multiplatform (KMP)
- **Architecture:** Clean Architecture + MVVM
- **AI Engine:** Google Gemini API (`gemini-1.5-flash`)
- **Local DB:** SQLDelight
- **DI:** Koin
- **Networking:** Ktor

---

## 🚀 Setup

1. **Clone repository**
2. **Setup `local.properties`**: Tambahkan `GEMINI_API_KEY=AIza...`
3. **Sync & Run**: Klik tombol Gajah (Sync) lalu jalankan di Android Studio.
