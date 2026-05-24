# 📚 StudyHub

> Aplikasi Manajemen Tugas Mahasiswa dengan integrasi AI untuk optimalisasi produktivitas akademik.

---

## 👥 Anggota Tim

| Nama | NIM |
|------|-----|
| Maxavier Girvanus Manurung | 123140191 |
| Muhammad Rafiq Ridho | 123140197 |

---

## 📖 Tentang StudyHub

**StudyHub** adalah aplikasi manajemen tugas lintas platform (Android & iOS) yang dirancang khusus untuk meningkatkan produktivitas mahasiswa. StudyHub mengadopsi arsitektur **Clean Architecture + MVVM** yang memisahkan logika bisnis, data, dan tampilan secara jelas, serta direncanakan untuk memanfaatkan potensi AI guna membantu mahasiswa mengelola waktu dan prioritas belajar mereka secara cerdas.

---

## 🛠️ Tech Stack

Aplikasi ini dibangun menggunakan teknologi modern dalam ekosistem Kotlin Multiplatform (KMP):

- **Core Framework**: [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html) & [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- **Dependency Injection**: [Koin](https://insert-koin.io/)
- **Database Local**: [SQLDelight](https://cashapp.github.io/sqldelight/)
- **Local Storage**: [Jetpack DataStore](https://developer.android.com/jetpack/androidx/releases/datastore)
- **Networking**: [Ktor Client](https://ktor.io/)
- **Image Loading**: [Coil3](https://coil-kt.github.io/coil/)

---

## 🚀 Update Terbaru (Recent Update)

Berikut adalah fondasi teknis yang telah diimplementasikan dalam pengembangan saat ini:

- **Implementasi Clean Architecture**: Pemisahan layer Data, Domain, dan Presentation untuk kode yang lebih terstruktur, skalabel, dan mudah diuji.
- **Local Persistence & Settings**: Penggunaan **SQLDelight** untuk penyimpanan database tugas offline dan **Jetpack DataStore** untuk manajemen preferensi tema.
- **Dependency Injection**: Konfigurasi modul Koin (`AppModule`) untuk manajemen dependensi yang efisien di seluruh platform.
- **Multi-platform Theming**: Sistem tema Dark/Light mode yang adaptif secara native di Android dan iOS.

---

## ✨ Fitur Saat Ini

- **Manajemen Tugas**: CRUD (Create, Read, Update, Delete) tugas lengkap dengan kategori, deskripsi, dan tingkat kesulitan.
- **Visualisasi Kalender**: Tampilan kalender bulanan terintegrasi untuk melacak deadline tugas secara visual.
- **Offline Support**: Akses data tugas yang tersimpan secara lokal tanpa koneksi internet.

---

## 🤖 Rencana Selanjutnya (AI Integration Roadmap)

### 1. Domain AI & Analisis Prioritas
Pengembangan layer domain khusus AI yang mampu menganalisis daftar tugas berdasarkan deadline, estimasi beban kerja, dan tingkat kesulitan untuk memberikan rekomendasi urutan pengerjaan yang paling optimal.

### 2. Jadwal Reminder Adaptif
Sistem pengingat yang mempelajari pola produktivitas pengguna. AI akan menentukan waktu terbaik untuk mengirimkan pengingat agar tidak mengganggu waktu fokus pengerjaan.

### 3. Notification Reminder via AI
Implementasi notifikasi pengingat yang dihasilkan secara dinamis oleh AI (menggunakan model seperti Groq/Gemini). Pesan pengingat akan bersifat persuasif dan motivasional, disesuaikan dengan konteks tugas.

### 4. Tampilan Rekomendasi Prioritas
Penambahan dashboard khusus atau komponen UI yang secara visual menonjolkan "Rekomendasi Utama" hasil analisis AI, membantu mahasiswa fokus pada tugas terpenting setiap harinya.

---
