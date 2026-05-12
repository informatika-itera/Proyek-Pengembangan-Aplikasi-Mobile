# 📚 Hujjah - Islamic Reference Finder

![CI](https://github.com/USERNAME/team-13-hujjah/actions/workflows/ci.yml/badge.svg)

**Hujjah** adalah aplikasi mobile multiplatform berbasis **Kotlin Multiplatform** dan **Compose Multiplatform** yang membantu pengguna mencari, membaca, menyimpan, dan memahami referensi keislaman dari Al-Qur’an, Hadis, serta pembahasan ulama berdasarkan topik tertentu.

Aplikasi ini dibuat untuk mata kuliah **Pengembangan Aplikasi Mobile** di ITERA sebagai project akhir berbasis **KMP, Compose Multiplatform, Clean Architecture, MVVM, Local Storage, Networking, StateFlow, Koin, AI Integration, dan Testing**.

> **Makna Nama**
>
> Hujjah berarti dasar, bukti, atau argumen yang digunakan untuk menjelaskan suatu perkara. Nama ini dipilih karena aplikasi berfokus pada pencarian dan pengelolaan referensi dalil secara terstruktur.

---

## 📚 Dokumentasi Project

| Dokumen | Deskripsi |
|---------|-----------|
| [🚀 Cara Menjalankan](./docs/CARA_MENJALANKAN.md) | Panduan setup dan menjalankan aplikasi |
| [📋 Panduan Project](./docs/PANDUAN_PROJECT.md) | Informasi project, timeline, dan penilaian |
| [🌿 Git Workflow](./docs/GIT_WORKFLOW.md) | Cara menggunakan Git dan branching strategy |
| [📜 Aturan Modifikasi](./docs/ATURAN_MODIFIKASI.md) | Batasan dan aturan modifikasi project |
| [🏗️ Struktur Kode](./docs/STRUKTUR_KODE.md) | Penjelasan arsitektur dan struktur folder |
| [🔧 Troubleshooting](./docs/TROUBLESHOOTING.md) | Solusi untuk masalah umum saat development |

---

## 👥 Team

| Role | Nama | NIM | GitHub |
|------|------|-----|--------|
| Lead Developer | Nama Anggota 1 | NIM Anggota 1 | [@username1](https://github.com/username1) |
| Mobile Developer | Nama Anggota 2 | NIM Anggota 2 | [@username2](https://github.com/username2) |
| QA & Documentation | Nama Anggota 3 | NIM Anggota 3 | [@username3](https://github.com/username3) |

---

## 📌 Project Overview

**Hujjah** dirancang sebagai aplikasi referensi Islam yang memudahkan pengguna menemukan dalil berdasarkan topik tertentu, seperti shalat, puasa, zakat, akhlak, doa, adab, muamalah, dan tema keislaman lainnya.

Pengguna dapat mencari referensi dalil, membaca detail dalil, menyimpan dalil favorit, membuat catatan pribadi, serta menggunakan fitur AI Assistant untuk membantu merangkum atau menjelaskan isi dalil dengan bahasa yang lebih mudah dipahami.

---

## 🎯 Tujuan Aplikasi

- Membantu pengguna mencari referensi keislaman berdasarkan topik.
- Menyediakan tampilan daftar dan detail dalil yang mudah dibaca.
- Menyediakan fitur bookmark agar referensi penting dapat disimpan.
- Menyediakan fitur catatan pribadi untuk proses belajar.
- Menyediakan bantuan AI untuk merangkum dan menjelaskan dalil.
- Menerapkan konsep pengembangan aplikasi mobile modern menggunakan Kotlin Multiplatform.

---

## 🧩 Pembeda Aplikasi

Hujjah memiliki beberapa pembeda utama:

| Pembeda | Penjelasan |
|--------|------------|
| **Topik Terstruktur** | Dalil dikelompokkan berdasarkan kategori seperti ibadah, akhlak, doa, adab, dan muamalah. |
| **Personal Learning Notes** | Pengguna dapat menulis catatan pribadi pada setiap dalil. |
| **AI Explanation** | AI membantu menjelaskan dalil dengan bahasa sederhana. |
| **Bookmark Offline** | Dalil yang disimpan dapat diakses tanpa internet. |
| **Clean Reading Mode** | Tampilan detail dirancang nyaman untuk membaca teks Arab, terjemahan, dan penjelasan. |
| **Mobile First** | Fokus pada pengalaman penggunaan di perangkat mobile dengan performa optimal. |

---

## ✨ Fitur Aplikasi

### ✅ Minimum Features
- 🔎 **Search Hujjah**
  - Pengguna dapat mencari referensi berdasarkan keyword atau topik.
  - Contoh pencarian: `shalat`, `puasa`, `sedekah`, `akhlak`, `doa`.
- 📚 **Reference List**
  - Menampilkan daftar hasil pencarian atau daftar referensi berdasarkan kategori.
- 📖 **Reference Detail**
  - Menampilkan teks Arab, terjemahan, sumber, kategori, dan penjelasan singkat.
- ⭐ **Bookmark**
  - Pengguna dapat menyimpan referensi penting ke daftar favorit.
- 📝 **Personal Notes**
  - Pengguna dapat membuat, mengubah, dan menghapus catatan pribadi pada referensi tertentu.
- 🗂️ **Category Filter**
  - Referensi dapat difilter berdasarkan kategori.
- 🌙 **Dark Mode**
  - Mendukung tema terang dan gelap.
- ⚙️ **Settings**
  - Pengaturan tema, preferensi tampilan, dan informasi aplikasi.

---

## 🚀 Bonus Features

- 🤖 **AI Assistant**
  - Membantu merangkum dalil.
  - Membantu menjelaskan isi dalil dengan bahasa sederhana.
  - Membantu mengambil poin-poin penting dari dalil.
- 📡 **Remote API Integration**
  - Mengambil data referensi dari remote API atau mock API.
- 📱 **Offline First**
  - Bookmark, cache, dan catatan pribadi dapat diakses tanpa koneksi internet.
- 🎞️ **Meaningful Animation**
  - Animasi transisi antar halaman.
  - Animasi loading, empty state, dan bookmark interaction.
- ✅ **CI/CD**
  - Build dan test otomatis menggunakan GitHub Actions.
- 📦 **Play Store Ready**
  - Menyiapkan APK/AAB release untuk final demo.

---

## 🖥️ Daftar Screen

Aplikasi memiliki lebih dari 5 screen untuk memenuhi minimum requirement project.

| Screen | Deskripsi |
|--------|-----------|
| Splash Screen | Tampilan awal aplikasi |
| Home Screen | Menampilkan search bar, kategori, dan rekomendasi topik |
| Search Result Screen | Menampilkan hasil pencarian referensi |
| Reference Detail Screen | Menampilkan detail dalil, sumber, dan penjelasan |
| Bookmark Screen | Menampilkan referensi yang disimpan |
| Note Editor Screen | Menambah atau mengubah catatan pribadi |
| AI Assistant Screen | Menampilkan fitur ringkasan dan penjelasan AI |
| Settings Screen | Pengaturan tema dan informasi aplikasi |

---

## 🗂️ Kategori Referensi

Kategori awal yang digunakan dalam aplikasi:

| Kategori | Deskripsi |
|----------|-----------|
| Akidah | Referensi tentang keimanan dan tauhid |
| Ibadah | Referensi tentang shalat, puasa, zakat, dan ibadah lainnya |
| Akhlak | Referensi tentang adab, karakter, dan perilaku |
| Muamalah | Referensi tentang hubungan sosial dan transaksi |
| Doa | Kumpulan referensi doa dan dzikir |
| Adab | Referensi tentang etika sehari-hari |
| Sirah | Referensi tentang sejarah dan keteladanan |

---

## 🧩 Data Model

### HujjahReference
```kotlin
data class HujjahReference(
    val id: String,
    val title: String,
    val arabicText: String?,
    val translation: String,
    val source: String,
    val category: HujjahCategory,
    val explanation: String?,
    val isBookmarked: Boolean = false,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)
