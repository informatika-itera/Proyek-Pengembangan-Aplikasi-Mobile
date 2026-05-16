[![CI Status](https://github.com/Kalll11/Proyek-Pengembangan-Aplikasi-Mobile/actions/workflows/ci.yml/badge.svg)](https://github.com/Kalll11/Proyek-Pengembangan-Aplikasi-Mobile/actions/workflows/ci.yml)
# 📝 TodoMaster - Student Task Management System

**TodoMaster** adalah aplikasi manajemen tugas berbasis **Kotlin Multiplatform (KMP)** yang dirancang khusus untuk mengelola produktivitas mahasiswa secara efektif. Sistem ini difokuskan pada pengurangan beban kognitif (*cognitive load*) dengan cara membatasi paparan tugas harian, sehingga pengguna tidak merasa kewalahan.

---

## 👥 Identitas Kelompok
**Program Studi:** Teknik Informatika  
**Mata Kuliah:** Proyek Pengembangan Aplikasi Mobile

* **Ragil Bayu Saputra** - 123140128
* **Cikal Galih Nur Arifin** - 123140109

---

## ✨ Fitur Utama Aplikasi
Aplikasi TodoMaster dirancang dengan beberapa fitur utama berikut:

1. **Daily Task Limitation (Pembatasan Tugas Harian):** Fitur krusial untuk menjaga kesehatan mental dan fokus. Sistem secara cerdas akan membatasi jumlah tugas yang dikerjakan dalam satu hari untuk mencegah beban kognitif berlebih pada mahasiswa.
2. **Sistem CRUD Catatan:** Fungsionalitas inti yang memungkinkan pengguna untuk membuat tugas baru, membaca detail tugas, memperbarui status pekerjaan, dan menghapus tugas yang sudah tidak relevan.
3. **Sinkronisasi Data Real-Time:** Memastikan bahwa setiap perubahan status tugas akan langsung tersinkronisasi sehingga pengelolaan waktu tetap akurat.
4. **Penyimpanan Lokal (SQLDelight):** Mengimplementasikan database lokal yang tangguh untuk menjamin aplikasi tetap bisa beroperasi dengan cepat meskipun tanpa koneksi internet.
5. **Dukungan Lintas Platform:** Dibangun di atas arsitektur KMP, memungkinkan aplikasi ini dijalankan dengan performa native di perangkat Android maupun iOS menggunakan satu basis kode utama.

---

## 🛠️ Teknologi yang Digunakan
- **Bahasa:** Kotlin
- **Framework:** Kotlin Multiplatform (KMP)
- **UI:** Compose Multiplatform (Jetpack Compose)
- **Database:** SQLDelight

---

## 🚀 Cara Menjalankan Proyek
1. Clone repositori ini ke dalam mesin lokal.
2. Pastikan Android Studio telah terinstal dengan plugin KMP terbaru.
3. Buka proyek dan tunggu hingga proses Gradle Sync selesai (Pastikan wrapper sudah sesuai).
4. Pilih konfigurasi `composeApp`.
5. Klik **Run** untuk menjalankan aplikasi pada Emulator atau perangkat fisik Android.