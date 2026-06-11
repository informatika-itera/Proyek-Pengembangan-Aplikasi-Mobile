
![Build](https://img.shields.io/badge/Build-Passing-success?style=flat-square)
![Platform](https://img.shields.io/badge/Platform-Android-green?style=flat-square)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0-purple?style=flat-square)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-Material3-blue?style=flat-square)

# 🛒 MaPen UMKM

---

> [!TIP]
>
> 📱 **Demo Aplikasi**
>
> ▶️ **[Tonton Video Demo Aplikasi di Sini](https://youtu.be/iDGt6VhbgqY)**
>
> *(Klik tautan di atas untuk memutar video demo aplikasi langsung di YouTube)*

MaPen UMKM adalah aplikasi manajemen penjualan berbasis Android yang dirancang untuk membantu pelaku **Usaha Mikro, Kecil, dan Menengah (UMKM)** dalam mengelola transaksi, produk, stok, serta laporan penjualan secara digital.

Aplikasi ini dibangun menggunakan **Kotlin**, **Jetpack Compose**, dan **Material Design 3** dengan pendekatan **Clean Architecture** dan pola **MVVM**. Selain fitur manajemen penjualan, aplikasi juga mengintegrasikan **Smart Business Assistant** berbasis Artificial Intelligence (AI) untuk memberikan insight bisnis sederhana, rekomendasi stok, dan ringkasan penjualan secara otomatis.

Proyek ini dikembangkan sebagai implementasi pengembangan aplikasi Android modern yang berfokus pada kemudahan penggunaan, performa, dan skalabilitas.

---

# 👥 Tim Pengembang

<table>
    <tr>
        <th>Nama</th>
        <th>NIM</th>
        <th>Role</th>
    </tr>
    <tr>
        <td>Danar Prayogo</td>
        <td>123140015</td>
        <td>Lead & Android Developer</td>
    </tr>
    <tr>
        <td>Exaudi Amin Hitasoit</td>
        <td>123140161</td>
        <td>Backend & AI Developer</td>
    </tr>
</table>

---

# 📝 Deskripsi

MaPen UMKM merupakan aplikasi manajemen penjualan yang ditujukan untuk membantu pemilik UMKM dalam mengelola aktivitas usaha sehari-hari secara lebih efisien.

Melalui aplikasi ini, pengguna dapat:

- Mencatat transaksi penjualan
- Mengelola data produk
- Memantau stok barang
- Melihat riwayat transaksi
- Menghasilkan laporan penjualan
- Mendapatkan insight bisnis sederhana melalui AI

Aplikasi dirancang dengan tampilan modern, intuitif, dan mudah digunakan sehingga cocok digunakan oleh berbagai jenis usaha kecil dan menengah.

---

# ✨ Fitur Aplikasi

## 🟢 Minimum Features

| Status | Feature | Deskripsi |
|----------|----------|----------|
| ⬜ | Login Page | Halaman autentikasi pengguna |
| ⬜ | Dashboard Penjualan | Ringkasan performa bisnis |
| ⬜ | Manajemen Produk | CRUD produk dan stok |
| ⬜ | Transaksi Penjualan | Pencatatan transaksi |
| ⬜ | Riwayat Transaksi | Histori transaksi |
| ⬜ | Laporan Penjualan | Laporan harian, mingguan, bulanan |

---

## 🤖 Bonus Features (AI)

| Status | Feature | Deskripsi |
|----------|----------|----------|
| ⬜ | Analisis Penjualan Otomatis | Menampilkan produk terlaris |
| ⬜ | Prediksi Stok | Memberikan rekomendasi restock |
| ⬜ | Ringkasan Laporan Otomatis | Ringkasan harian & bulanan |
| ⬜ | Smart Search | Pencarian produk lebih cepat |
| ⬜ | Insight Penjualan | Analisis pola transaksi |

---

# 📱 Main Screens

| Screen | Keterangan |
|----------|----------|
| 🔐 Login Screen | Halaman masuk pengguna |
| 📊 Dashboard | Ringkasan kondisi bisnis |
| 📦 Manajemen Produk | Pengelolaan produk dan stok |
| 🛒 Transaksi Penjualan | Pencatatan transaksi |
| 📜 Riwayat Transaksi | Histori transaksi |
| 📈 Laporan Penjualan | Laporan berdasarkan periode |
| 🤖 Smart Business Assistant | Insight dan rekomendasi bisnis |
---

# 🛠️ Tech Stack

| Technology | Keterangan |
| --- | --- |
| Kotlin | Bahasa Pemrograman Utama |
| Jetpack Compose | Framework UI Android |
| Material Design 3 | Design System |
| Koin | Dependency Injection |
| SQLDelight | Local Database |
| Navigation Compose | Navigasi Antar Halaman |
| MVVM | Architecture Pattern |
| Clean Architecture | Struktur Pengembangan |

---

# 📂 Struktur Project

```text
composeApp/
└── src/
    └── commonMain/
        └── kotlin/
            └── com.example.mapenumkm/
                ├── core/
                ├── data/
                ├── domain/
                ├── presentation/
                │   ├── components/
                │   ├── navigation/
                │   ├── screens/
                │   │   ├── addnote/
                │   │   ├── ai/
                │   │   ├── detail/
                │   │   ├── home/
                │   │   └── login/
                │   └── theme/
                └── App.kt

```

---

# 🎨 UI/UX Design

### Design Principles

* ✨ Modern
* 🧹 Clean
* 📱 Mobile First
* 🚀 User Friendly
* 🎯 Focused on UMKM Needs

### Components

* Card Layout
* Rounded Button
* Search Bar
* Bottom Navigation
* Dashboard Analytics
* Responsive Layout

### Color Palette

| Color | Hex |
| --- | --- |
| Green Primary | `#16A34A` |
| Green Dark | `#15803D` |
| Green Light | `#DCFCE7` |
| Blue Accent | `#60A5FA` |
| Yellow Accent | `#FACC15` |
| Purple Accent | `#A78BFA` |

---

# 🤖 Smart Business Assistant

Smart Business Assistant merupakan fitur berbasis AI sederhana yang membantu pengguna memahami kondisi bisnis secara cepat.

### Contoh Output AI

```text
📊 Ringkasan Penjualan Hari Ini

Total Pendapatan : Rp250.000
Jumlah Transaksi : 10
Produk Terlaris : Es Teh
Stok Menipis : Kopi Susu (3 pcs)

💡 Rekomendasi:
Segera lakukan restock Kopi Susu agar tidak kehabisan stok.

```

Fitur ini dirancang agar tetap ringan, mudah diimplementasikan, dan realistis untuk tahap awal pengembangan aplikasi.

---

# 🚀 Installation

### Clone Repository

```bash
git clone [https://github.com/username/mapen-umkm.git](https://github.com/username/mapen-umkm.git)

```

### Open Project

```bash
Buka project menggunakan Android Studio

```

### Sync Gradle

```bash
Tunggu proses Gradle Sync selesai

```

### Run Application

```bash
Jalankan pada Emulator atau Android Device

```

---

# 🧪 Hasil Pengujian (Code Coverage)

Pengujian unit dilakukan untuk memastikan keandalan fungsi-fungsi inti aplikasi. Berikut adalah ringkasan hasil *code coverage* dari modul `composeApp`:

<img width="1600" height="623" alt="testing" src="https://github.com/user-attachments/assets/0d21df70-4466-4917-ad9e-191b9fa2fcc1" />



---

# 🎯 Project Goal

Tujuan utama MaPen UMKM adalah membantu pelaku UMKM dalam:

* Mengelola transaksi penjualan
* Mengelola data produk
* Mengontrol stok barang
* Melihat laporan penjualan
* Mendapatkan insight bisnis sederhana

Dengan pendekatan modern menggunakan Kotlin dan Jetpack Compose, aplikasi ini diharapkan menjadi solusi digital yang sederhana namun bermanfaat bagi pelaku UMKM.

---

# 📌 Conclusion

MaPen UMKM merupakan aplikasi manajemen penjualan berbasis Android yang menggabungkan kemudahan penggunaan, pencatatan transaksi, pengelolaan produk, laporan penjualan, dan fitur AI sederhana dalam satu platform.

Aplikasi ini dirancang untuk membantu UMKM melakukan digitalisasi proses bisnis secara bertahap dengan teknologi modern yang ringan dan mudah dikembangkan.

---

```

```
