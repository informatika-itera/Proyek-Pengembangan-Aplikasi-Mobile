# 📚 StudyHub

> Aplikasi Manajemen Tugas Mahasiswa dengan integrasi AI untuk optimalisasi produktivitas akademik.

---

## 👥 Anggota Tim

| Nama | NIM |
|------|-----|
| Maxavier Girvanus Manurung | 123140191 | Muhammad Rafiq Ridho | 123140197 |

---

## 📖 Tentang StudyHub

**StudyHub** adalah aplikasi manajemen tugas lintas platform (Android & iOS) yang dirancang khusus untuk meningkatkan produktivitas mahasiswa. StudyHub mengadopsi arsitektur **Clean Architecture + MVVM** yang memisahkan logika bisnis, data, dan tampilan secara jelas, serta memanfaatkan potensi AI guna membantu mahasiswa mengelola waktu dan prioritas belajar mereka secara cerdas.

---

## 🛠️ Tech Stack

Aplikasi ini dibangun menggunakan teknologi modern dalam ekosistem Kotlin Multiplatform (KMP):

- **Core Framework**: [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html) & [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- **Dependency Injection**: [Koin](https://insert-koin.io/)
- **Database Local**: [SQLDelight](https://cashapp.github.io/sqldelight/)
- **Local Storage**: [Jetpack DataStore](https://developer.android.com/jetpack/androidx/releases/datastore)
- **Networking**: [Ktor Client](https://ktor.io/)
- **AI Engine**: [Groq Cloud API](https://groq.com/) (Llama-3 model)
- **Testing**: Kotlin Test, Coroutines Test, Turbine, Kover
- **Analysis**: Detekt

---

## 🚀 Status Pengembangan

| Sprint | Status | Keterangan |
|--------|--------|------------|
| Sprint 1 | ✅ Selesai | Foundation, Clean Architecture, DI setup |
| Sprint 2 | ✅ Selesai | CRUD Tugas, Navigation, Calendar View |
| Sprint 3 | ✅ Selesai | Groq AI Integration, Notifications, Offline Sync |
| Sprint 4 | ✅ Selesai | Polish & Release Ready, Comprehensive Tests, Progress Dash |

---

## ✨ Fitur Utama

### 🎯 Smart Priority AI
AI menganalisis seluruh daftar tugas Anda berdasarkan urgensi, tingkat kesulitan, dan deadline untuk memberikan rekomendasi urutan pengerjaan yang optimal.

### ⏰ Smart Reminder AI
Sistem pengingat adaptif yang mempelajari kebiasaan pengerjaan tugas Anda. AI akan menyarankan waktu pengingat yang tepat agar Anda tidak menunda pekerjaan.

### ⏱️ Pomodoro Timer
Timer produktivitas terintegrasi (25/5/15 menit) dengan notifikasi status dan persistensi background. Terintegrasi langsung dengan tugas yang sedang dikerjakan.

### 📈 Progress Dashboard
Visualisasi statistik belajar mulai dari completion rate mingguan, streak pengerjaan tugas berturut-turut, hingga breakdown progress per mata kuliah.

### 📅 Advanced Calendar
Tampilan kalender interaktif dengan dot indicators untuk hari yang memiliki deadline, memudahkan perencanaan akademik jangka panjang.

### 📴 Robust Offline Support
Bekerja sepenuhnya offline dengan sinkronisasi otomatis saat internet kembali tersedia menggunakan sistem antrean sinkronisasi (*Sync Queue*).

---

## 🏗️ Architecture Decision Records

### Mengapa SQLDelight?
SQLDelight menyediakan type-safe SQL queries yang dikompilasi menjadi kode Kotlin. Ini memungkinkan berbagi skema database dan logika query antara Android dan iOS tanpa overhead library runtime yang berat seperti Room.

### Mengapa Groq API?
Groq Cloud menawarkan latensi inferensi yang sangat rendah (LPU) yang ideal untuk aplikasi mobile yang membutuhkan respon AI instan. Selain itu, Groq memiliki model Llama-3 yang sangat kompeten untuk task analisis teks terstruktur.

### Local-First Design
Aplikasi dirancang dengan prinsip local-first. Database lokal adalah sumber kebenaran tunggal (*single source of truth*), memastikan performa tinggi dan ketersediaan fitur inti tanpa tergantung pada kestabilan server atau internet.

---

## 🛠️ Menjalankan Project

1. Clone repository ini.
2. Tambahkan `GROQ_API_KEY` di file `local.properties`.
3. Jalankan perintah `./gradlew :composeApp:assembleDebug` untuk build Android.
4. Untuk menjalankan unit test: `./gradlew :composeApp:testDebugUnitTest`.
5. Untuk melihat laporan coverage: `./gradlew :composeApp:koverHtmlReport`.

---
