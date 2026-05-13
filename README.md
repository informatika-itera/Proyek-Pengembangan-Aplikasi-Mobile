# 📚 StudyHub

> Aplikasi Manajemen Tugas Mahasiswa berbasis Kotlin Multiplatform dengan integrasi AI

---

## 👥 Anggota Tim

| Nama | NIM |
|------|-----|
| Maxavier Girvanus Manurung | 123140191 |
| Muhammad Rafiq Ridho | 123140197 |

---

## 📖 Tentang StudyHub

**StudyHub** adalah aplikasi manajemen tugas yang dirancang khusus untuk meningkatkan produktivitas mahasiswa di lingkungan kampus. Aplikasi ini dibangun menggunakan **Kotlin Multiplatform (KMP)** sehingga satu codebase dapat berjalan di platform Android maupun iOS.

StudyHub mengadopsi arsitektur **Clean Architecture + MVVM** yang memisahkan logika bisnis, data, dan tampilan secara jelas, serta mengintegrasikan **Gemini AI** untuk menghadirkan fitur-fitur cerdas yang membantu mahasiswa mengelola waktu dan prioritas belajar mereka.

### Tujuan Aplikasi

- Membantu mahasiswa melacak tugas dan deadline secara terorganisir
- Mengurangi keterlambatan pengumpulan tugas dengan sistem reminder otomatis
- Memberikan rekomendasi prioritas tugas yang cerdas berbasis AI
- Menyediakan tampilan kalender terintegrasi untuk perencanaan akademik

---

## ✨ Fitur Aplikasi

### 🔐 Autentikasi

- **Login** — Masuk ke akun menggunakan email dan password via Firebase Authentication.
- **Register** — Pendaftaran akun baru dengan validasi data pengguna. Sesi pengguna tersimpan otomatis sehingga tidak perlu login ulang setiap saat.

### ✅ Manajemen Tugas

- **Tambah Tugas** — Menambahkan tugas baru lengkap dengan judul, mata kuliah, deskripsi, dan tingkat kesulitan.
- **Edit & Hapus Tugas** — Mengubah detail tugas atau menghapus tugas yang sudah tidak relevan.
- **Status Selesai / Belum** — Menandai tugas sebagai selesai dengan satu klik. Tugas yang sudah selesai akan diarsipkan secara otomatis.

### 📅 Deadline & Kalender

- **Deadline Tugas** — Setiap tugas memiliki tanggal dan waktu deadline yang wajib diisi sebagai acuan pengerjaan.
- **Kalender Tugas** — Tampilan kalender bulanan yang menampilkan semua tugas berdasarkan tanggal deadline, memudahkan mahasiswa dalam merencanakan jadwal belajar mingguan maupun bulanan.

### 🔔 Reminder Notifikasi

Notifikasi push otomatis dikirimkan sebelum deadline tugas tiba. Pengingat dapat dikustomisasi sesuai preferensi pengguna, misalnya H-1 hari atau H-3 jam sebelum deadline. Fitur ini bekerja di Android maupun iOS.

---

## 🤖 Fitur AI — Powered by Gemini

### 🎯 Smart Priority

Fitur **Smart Priority** memanfaatkan Gemini AI untuk menganalisis seluruh daftar tugas mahasiswa dan menghasilkan urutan prioritas pengerjaan yang optimal.

Gemini menganalisis deadline, estimasi waktu pengerjaan, dan distribusi tugas per mata kuliah agar tidak menumpuk di hari yang sama. Hasilnya berupa rekomendasi urutan tugas beserta alasan singkat mengapa tugas tersebut perlu didahulukan, sehingga mahasiswa tidak perlu lagi bingung harus mulai dari mana.

### ⏰ Smart Reminder

Fitur **Smart Reminder** menggunakan Gemini AI untuk menentukan waktu pengingat yang adaptif dan dipersonalisasi, bukan sekadar interval waktu tetap.

AI menganalisis riwayat penyelesaian tugas mahasiswa — apakah cenderung mengerjakan jauh-jauh hari atau mendekati deadline — lalu mempertimbangkan kompleksitas tugas untuk menghasilkan jadwal reminder yang paling efektif bagi masing-masing pengguna. Dengan cara ini, reminder yang diterima terasa lebih relevan dan tepat waktu.

---

© 2024 Maxavier Girvanus Manurung & Muhammad Rafiq Ridho — Institut Teknologi Sumatera