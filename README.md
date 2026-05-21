

````markdown
# 🛒 MaPen UMKM  
## Manajemen Penjualan UMKM

MaPen UMKM adalah aplikasi berbasis Android yang dirancang untuk membantu pelaku Usaha Mikro, Kecil, dan Menengah (UMKM) dalam mengelola transaksi penjualan secara digital, cepat, sederhana, dan efisien.

Aplikasi ini membantu pemilik usaha dalam mencatat transaksi, mengelola produk dan stok, melihat riwayat penjualan, serta memantau laporan penjualan secara berkala. MaPen UMKM juga dilengkapi dengan fitur **Smart Business Assistant** berbasis Artificial Intelligence (AI) sederhana untuk memberikan ringkasan penjualan, rekomendasi stok, dan insight bisnis yang mudah dipahami.

---

## 👥 Team

- **Danar Prayogo**  
  NIM: 123140015  
  GitHub: @danarPrayogo

- **Exaudi Amin Hitasoit**  
  NIM: 123140161  
  GitHub: @16-123140161-ExaudiAminHutasoit

---

## 📝 Description

MaPen UMKM adalah aplikasi manajemen penjualan yang ditujukan untuk membantu pemilik UMKM mengelola aktivitas usaha sehari-hari.

Melalui aplikasi ini, pengguna dapat melakukan pencatatan transaksi penjualan, mengelola data produk, memantau stok barang, melihat riwayat transaksi, serta memperoleh laporan penjualan dengan tampilan yang modern dan mudah digunakan.

Aplikasi ini juga mengintegrasikan fitur AI sederhana melalui **Smart Business Assistant** untuk membantu pengguna memahami kondisi bisnis, seperti produk paling laris, stok yang hampir habis, serta ringkasan laporan penjualan harian atau bulanan.

---

## ✨ Features

### 🟢 Minimum Features

- [ ] **Login Page**  
  Halaman awal aplikasi untuk masuk ke sistem MaPen UMKM.

- [ ] **Dashboard Penjualan**  
  Menampilkan ringkasan performa bisnis harian, seperti total penjualan, jumlah transaksi, produk terlaris, dan stok menipis.

- [ ] **Manajemen Produk**  
  Fitur untuk menambah, mengedit, menghapus, dan mencari produk. Setiap produk memiliki data nama, harga, dan jumlah stok.

- [ ] **Transaksi Penjualan**  
  Fitur untuk mencatat transaksi penjualan dengan perhitungan total belanja dan kembalian secara otomatis.

- [ ] **Riwayat & Penyimpanan**  
  Menyimpan data produk dan histori transaksi secara lokal agar dapat dilihat kembali oleh pengguna.

- [ ] **Laporan Penjualan**  
  Menampilkan laporan transaksi harian, mingguan, dan bulanan dengan tampilan sederhana, modern, dan responsif.

---

### 🤖 Bonus Features: AI & Smart Automation

- [ ] **Analisis Penjualan Otomatis**  
  Smart Business Assistant menganalisis produk paling laris berdasarkan data transaksi.

- [ ] **Rekomendasi Produk & Prediksi Stok**  
  Memberikan rekomendasi produk yang perlu ditambah stoknya sebelum habis.

- [ ] **Ringkasan Laporan Otomatis**  
  Membuat ringkasan laporan penjualan harian dan bulanan secara otomatis.

- [ ] **Smart Search**  
  Membantu pencarian produk dengan keyword sederhana agar pengguna lebih cepat menemukan produk.

- [ ] **Insight Penjualan**  
  Memberikan informasi sederhana seperti waktu transaksi tersibuk dan pola pembelian pelanggan.

---

## 📱 Main Screens

Aplikasi MaPen UMKM terdiri dari beberapa halaman utama:

1. **Login Screen**  
   Halaman masuk pengguna sebelum mengakses fitur utama aplikasi.

2. **Dashboard Penjualan**  
   Menampilkan ringkasan kondisi bisnis secara cepat.

3. **Manajemen Produk**  
   Digunakan untuk mengelola daftar produk dan stok.

4. **Transaksi Penjualan**  
   Digunakan untuk mencatat transaksi, menghitung total belanja, dan menghitung kembalian.

5. **Riwayat Transaksi**  
   Menampilkan daftar transaksi yang telah dilakukan.

6. **Laporan Penjualan**  
   Menampilkan ringkasan laporan penjualan berdasarkan periode tertentu.

7. **Smart Business Assistant**  
   Memberikan ringkasan, rekomendasi stok, dan insight penjualan sederhana.

---

## 🧭 User Flow

Alur penggunaan aplikasi:

1. Pengguna membuka aplikasi.
2. Pengguna masuk melalui halaman Login.
3. Pengguna melihat ringkasan bisnis di Dashboard.
4. Pengguna mengelola produk pada halaman Manajemen Produk.
5. Pengguna mencatat transaksi penjualan.
6. Data transaksi tersimpan ke riwayat.
7. Stok produk diperbarui secara otomatis.
8. Pengguna melihat laporan penjualan.
9. Smart Business Assistant memberikan ringkasan dan rekomendasi sederhana.

---

## 🛠️ Tech Stack

Project ini dikembangkan menggunakan:

- **Kotlin**
- **Jetpack Compose / Compose Multiplatform**
- **Material Design 3**
- **Koin** untuk dependency injection
- **SQLDelight** untuk penyimpanan data lokal
- **Navigation Compose** untuk navigasi antar halaman

---

## 📂 Project Structure

Struktur utama project:

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
````

---

## 🎨 UI/UX Design

Konsep desain MaPen UMKM dibuat dengan gaya:

* Modern
* Clean
* Minimalis
* Mudah digunakan
* Cocok untuk pemilik UMKM
* Menggunakan warna utama hijau
* Menggunakan aksen biru, kuning, dan ungu pastel
* Menggunakan card, rounded button, search bar, dan bottom navigation

Warna utama aplikasi:

```text
Green Primary : #16A34A
Green Dark    : #15803D
Green Light   : #DCFCE7
Blue Accent   : #60A5FA
Yellow Accent : #FACC15
Purple Accent : #A78BFA
```

---

## 🤖 Smart Business Assistant

Smart Business Assistant adalah fitur pendukung berbasis AI sederhana yang membantu pengguna memahami kondisi usahanya.

Contoh hasil ringkasan AI:

```text
Penjualan hari ini cukup baik.
Total pendapatan Rp250.000 dari 10 transaksi.
Produk paling laris adalah Es Teh.
Stok Kopi Susu tinggal 3, sebaiknya segera ditambah.
```

Fitur AI pada aplikasi ini dibuat sederhana agar tetap ringan dan realistis untuk dikembangkan pada tahap awal.

---

## 🚀 Installation

Langkah menjalankan project:

1. Clone repository ini.

```bash
git clone <repository-url>
```

1. Buka project menggunakan Android Studio.

2. Tunggu proses Gradle Sync selesai.

3. Jalankan aplikasi pada emulator atau perangkat Android.


---

## 🎯 Project Goal

Tujuan utama dari project MaPen UMKM adalah membuat aplikasi manajemen penjualan sederhana yang dapat membantu pelaku UMKM dalam mengelola transaksi, produk, stok, dan laporan penjualan secara lebih mudah melalui perangkat Android.

Aplikasi ini dirancang agar ringan, mudah digunakan, dan tetap memiliki nilai modern melalui fitur Smart Business Assistant.

---

## 📌 Conclusion

MaPen UMKM diharapkan dapat menjadi solusi sederhana bagi pelaku UMKM untuk melakukan pencatatan transaksi, mengelola produk, memantau stok, melihat laporan penjualan, serta memperoleh insight bisnis sederhana melalui bantuan AI.

Dengan fitur yang sederhana dan fokus pada kebutuhan utama UMKM, aplikasi ini realistis untuk dikembangkan sebagai project Android berbasis Kotlin dan Jetpack Compose.

```


```
