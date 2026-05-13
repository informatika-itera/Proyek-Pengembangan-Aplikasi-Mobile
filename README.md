# Pusaka Kata: Petualangan Kosakata & Mitologi Nusantara

Pusaka Kata adalah aplikasi edukasi interaktif berbasis Android *native* yang dirancang untuk memperkaya penguasaan kosakata baku, peribahasa, dan kesusastraan Indonesia. Aplikasi ini memecahkan masalah kebosanan dalam menghafal dengan menggabungkan algoritma pembelajaran adaptif (*Spaced Repetition System*), pemrosesan gambar cerdas (OCR), dan mekanik gamifikasi (*Gacha*) untuk mengoleksi karakter legenda dan mitologi Nusantara.

## ✨ Fitur Utama

* 🧠 **Smart Flashcard (Sistem Hafalan Adaptif):** Menggunakan algoritma *Spaced Repetition System* (SRS). Sistem secara otomatis menghitung tingkat retensi memori pengguna dan memprioritaskan kemunculan kosakata yang sering salah dijawab.
* 📸 **Kamera Pemindai Teks (OCR Scanner):** Terintegrasi dengan *Google ML Kit Vision API*. Pengguna dapat memindai teks dari buku fisik, dan aplikasi akan mengekstrak kata-kata untuk langsung dijadikan kartu hafalan baru secara *offline*.
* 🎲 **Sistem Undian Legenda (Gacha Reward):** Sistem *reward* berbasis *Weighted Random Number Generator* (RNG). Setiap menyelesaikan sesi belajar, pengguna mendapatkan token untuk melakukan *pull gacha* karakter mitologi dengan *rarity* yang berbeda (Contoh: Tuyul [Common], Barong [Rare], Garuda [Epic]).
* 📚 **Galeri Nusantara (Kamus Mitologi):** Halaman koleksi (*Encyclopedia*) yang menampilkan detail visual dan cerita rakyat/sejarah dari karakter legenda yang berhasil didapatkan.

## 🛠️ Arsitektur & Teknologi

Proyek ini dibangun dengan standar industri pengembangan Android (*Modern Android Development*):

* **Bahasa Pemrograman:** Kotlin
* **Arsitektur:** MVVM (Model-View-ViewModel)
* **Database Lokal:** Android Room (SQLite) untuk manajemen data kosakata dan status koleksi.
* **Asynchronous Programming:** Kotlin Coroutines & Flow.
* **Machine Learning:** Google ML Kit (Text Recognition) untuk pemindaian teks.
* **UI/Animation:** XML Layouts, MotionLayout, dan Lottie.

## 📂 Struktur Proyek Utama

```text
com.pusakakata.app
│
├── data/               # Room Database, DAO, Entity, dan Repository
├── model/              # Data class untuk Kosakata dan Karakter Mitologi
├── ui/
│   ├── flashcard/      # Logic & UI untuk sesi kuis SRS
│   ├── gacha/          # Animasi dan logic RNG Gacha
│   ├── gallery/        # UI koleksi mitologi
│   └── scanner/        # Integrasi ML Kit dan Kamera
└── utils/              # Helper class (Kalkulasi SRS, Date formatter)

```

## 🚀 Cara Menjalankan Proyek

1. *Clone repository* ini:
```bash
git clone https://github.com/username/Pusaka-Kata.git

```


2. Buka proyek di **Android Studio**.
3. Tunggu proses *Gradle Sync* selesai.
4. Jalankan aplikasi di perangkat Android fisik atau emulator (Shift + F10).

## 🤝 Pengembang Utama

* **Muharyan Syaifullah** (123140045)
* **Eka Putri Azhari Ritonga** (123140029)

**Pengembangan Aplikasi Mobile**

**Program Studi Teknik Informatika, Institut Teknologi Sumatera (ITERA)**

---
