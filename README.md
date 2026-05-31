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

### Sprint 3 — Advanced Features (Current Progress)
- **AI Token Management Infrastructure**: Sistem manajemen token AI dengan kompresi data, caching (SQLDelight), dan pembatasan kuota harian untuk efisiensi biaya.
- **Smart Priority & Reminder Logic**: Implementasi Repository dan Use Case untuk pengurutan tugas cerdas dan pengingat adaptif berbasis pola pengerjaan pengguna.
- **Enhanced Task Visualization**: Indikator visual otomatis untuk tugas yang terlambat (*overdue*) dengan styling khusus (merah, strike-through) untuk meningkatkan kesadaran deadline.
- **CI/CD Optimization**: Konfigurasi otomatisasi build GitHub Actions dan analisis kode statis menggunakan **Detekt**.
- **Sprint 3 Navigation & UI**: Penambahan rute navigasi untuk fitur Smart Priority, Progress, dan Pomodoro, serta integrasi shortcut AI di beranda.
- **Database Migration System**: Implementasi skema migrasi SQLDelight untuk mendukung pembaruan struktur database secara aman pada perangkat pengguna.
- **Advanced Task Management**: Perbaikan sistem *state reset* pada form tambah/edit tugas dan sentralisasi aksi tambah tugas untuk UX yang lebih konsisten.

---

## ✨ Fitur Saat Ini

- **Manajemen Tugas**: CRUD (Create, Read, Update, Delete) tugas lengkap dengan kategori, deskripsi, dan tingkat kesulitan.
- **Visualisasi Kalender**: Tampilan kalender bulanan terintegrasi untuk melacak deadline tugas secara visual.
- **Offline Support**: Akses data tugas yang tersimpan secara lokal tanpa koneksi internet.

---

