```

## 🚀 Setup
1. **Clone repository ini ke komputer lokal Anda:**
   ```bash
   git clone [https://github.com/username/mapen-umkm.git](https://github.com/username/mapen-umkm.git)

```

2. **Buka proyek di Android Studio:**
* Pilih `File` > `Open` > Arahkan ke folder hasil clone.
* Tunggu proses *Gradle Sync* selesai.


3. **Konfigurasi API Key (Jika diperlukan):**
* Tambahkan API Key Google Gemini / OpenAI pada file konfigurasi/`local.properties` sesuai kebutuhan modul AI.


4. **Jalankan Aplikasi:**
* Sambungkan perangkat Android fisik atau aktifkan Emulator.
* Klik tombol **Run** (`Shift + F1Tentu, ini adalah penyesuaian README dari aplikasi **MaPen UMKM** dengan mengikuti struktur template kedua yang kamu berikan.



Beberapa penyesuaian dilakukan pada bagian *Tech Stack* dan *Architecture* agar tetap sinkron dengan detail proyek asli kamu (Android native, MVVM, Room), namun menggunakan format mintaan.

---

# 🛒 MaPen UMKM (Manajemen Penjualan UMKM)

## 👥 Team
- **Danar Prayogo (123140015)** - [@danarPrayogo](https://github.com/danarPrayogo)
- **Exaudi Amin Hitasoit (123140161)** - [@16-123140161-ExaudiAminHutasoit](https://github.com/16-123140161-ExaudiAminHutasoit)


## 📝 Description

MaPen UMKM adalah aplikasi berbasis Android yang dirancang khusus untuk membantu pelaku usaha kecil dan menengah (UMKM) dalam mengelola transaksi penjualan secara digital, cepat, dan efisien.

Aplikasi ini diintegrasikan dengan teknologi *Artificial Intelligence* (AI) melalui Smart Business Assistant untuk memberikan analisis penjualan otomatis, rekomendasi manajemen stok, serta ringkasan laporan berkala agar pelaku usaha dapat mengambil keputusan bisnis dengan lebih cerdas dan modern.

## ✨ Features

### 🟢 Minimum (Fitur Utama)

* [ ] **Dashboard Penjualan:** Tampilan ringkas performa bisnis harian.
* [ ] **Manajemen Produk:** Fitur lengkap untuk Tambah, Edit, Hapus, dan Cari Produk.
* [ ] **Transaksi Penjualan:** Perhitungan total belanjaan dan kembalian secara otomatis.
* [ ] **Riwayat & Penyimpanan:** Penyimpanan data produk dan histori transaksi yang aman secara lokal.
* [ ] **Laporan Penjualan:** Laporan transaksi berkala dengan UI yang modern dan responsif.

### 🤖 Bonus (Fitur AI & Smart Automation)

* [ ] **Analisis Penjualan Otomatis:** AI menganalisis produk paling laris dan tren pasar.
* [ ] **Rekomendasi Produk & Prediksi Stok:** Deteksi otomatis untuk produk yang perlu ditambah stoknya sebelum habis.
* [ ] **Ringkasan Laporan Otomatis:** Pembuatan *summary* laporan harian dan bulanan bertenaga AI.
* [ ] **Smart Search:** Pencarian produk cerdas menggunakan *keyword prediction*.
* [ ] **Insight Penjualan:** Analisis waktu transaksi tersibuk dan pola pembelian pelanggan.

## ⚙️ Tech Stack

* **IDE:** Android Studio
* **Language:** Kotlin / Java
* **UI Design:** Material Design 3, ViewBinding, RecyclerView
* **Database:** SQLite, Room Database
* **Asynchronous:** Coroutines & LiveData
* **AI & Analytics:** Firebase ML Kit, Google Gemini API / OpenAI API, TensorFlow Lite (Optional)

## 🏗️ Architecture

Proyek ini dikembangkan menggunakan arsitektur **MVVM (Model-View-ViewModel)** dengan **Repository Pattern** untuk memastikan kode yang *scalable*, *testable*, dan mudah dirawat.

### Struktur Folder Project:

```bash
app/src/main/
├── java/com/example/umkmapp/
│   ├── ai/            # AI module & prediction
│   ├── activities/    # Activity aplikasi (View)
│   ├── adapters/      # RecyclerView Adapter
│   ├── database/      # Room / SQLite Database (Model)
│   ├── models/        # Data Model
│   ├── repository/    # Repository Pattern
│   ├── viewmodel/     # MVVM ViewModel
│   └── utils/         # Utility & helper
└── res/
    ├── layout/        # XML Layout
    ├── drawable/      # Asset gambar & icon
    └── values/        # Theme, Color, String

```

## 🚀 Setup

1. **Clone repository ini ke komputer lokal Anda:**
```bash
git clone [https://github.com/username/mapen-umkm.git](https://github.com/username/mapen-umkm.git)

```


2. **Buka proyek di Android Studio:**
* Pilih `File` > `Open` > Arahkan ke folder hasil clone.
* Tunggu proses *Gradle Sync* selesai.


3. **Konfigurasi API Key (Jika diperlukan):**
* Tambahkan API Key Google Gemini / OpenAI pada file konfigurasi/`local.properties` sesuai kebutuhan modul AI.


4. **Jalankan Aplikasi:**
* Sambungkan perangkat Android fisik atau aktifkan Emulator.
* Klik tombol **Run** (`Shift + F10`) di Android Studio.



```

```
