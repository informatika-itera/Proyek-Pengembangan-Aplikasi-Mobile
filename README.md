# 💸 Money-Z

![CI](https://github.com/[USERNAME]/[REPO-NAME]/actions/workflows/ci.yml/badge.svg)

> Aplikasi manajemen keuangan pintar berbasis Kotlin Multiplatform yang membantu Anda mencatat arus kas, menetapkan anggaran, dan memantau laporan keuangan — kini dilengkapi asisten AI untuk pencatatan transaksi via chat.

---

## 👥 Tim

| Nama | NIM |
|------|------|
| [Martino Kelvin] | 123140165 | 
| [Louis Hutabarat] | 123140052 | 


---

## 📖 Deskripsi

**Money-Z** adalah aplikasi *expense tracker* berbasis **Kotlin Multiplatform (KMP)** yang dirancang untuk memudahkan pengelolaan keuangan pribadi. Pengguna dapat mencatat setiap pemasukan dan pengeluaran, mengatur anggaran bulanan per kategori, serta memantau kondisi keuangan melalui laporan statistik dan grafik interaktif.

Yang membedakan Money-Z dari aplikasi serupa adalah kehadiran **asisten AI berbasis chat**. Alih-alih mengisi form secara manual, pengguna cukup mengetikkan transaksi dalam bahasa natural — misalnya *"Beli kopi 25rb"* — dan AI akan secara otomatis mencatatnya ke dalam sistem.

**Tujuan utama aplikasi ini:**
- Membantu pengguna memiliki kebiasaan mencatat keuangan dengan cara yang mudah dan tidak membebani.
- Memberikan gambaran keuangan yang jelas melalui laporan visual.
- Memanfaatkan teknologi AI untuk pengalaman pencatatan yang lebih intuitif.

---

## ✨ Fitur

### Minimum
- [ ] Track uang masuk & keluar
- [ ] Rekap penetapan anggaran
- [ ] Kategori pengeluaran & pemasukan
- [ ] Laporan keuangan (statistik/grafik)

### Bonus
- [ ] Chatbot AI untuk pencatatan pengeluaran maupun pemasukan (Integrasi AI API)

---

## 🛠️ Tech Stack

| Teknologi | Kegunaan |
|-----------|----------|
| **Kotlin Multiplatform (KMP)** | Berbagi logika bisnis antar platform (Android & iOS) dari satu codebase |
| **Compose Multiplatform** | Framework UI deklaratif untuk membangun antarmuka yang konsisten di semua platform |
| **Ktor** | HTTP client untuk kebutuhan *networking* dan integrasi dengan API eksternal |
| **SQLDelight** | Penyimpanan data lokal dengan kueri *type-safe* berbasis SQL |
| **Koin** | Framework *Dependency Injection* yang ringan dan idiomatik untuk Kotlin |

---

## 🏗️ Arsitektur

Proyek ini mengimplementasikan **Clean Architecture** dengan pola **MVVM** (Model-View-ViewModel), yang membagi kode ke dalam tiga layer utama:

```
┌──────────────────────────────────────┐
│         Presentation Layer           │
│  Screen (Composable) ◄──► ViewModel  │
│         (State, Events)              │
└──────────────────┬───────────────────┘
                   │
┌──────────────────▼───────────────────┐
│            Domain Layer              │
│   Use Cases ──► Repository Interface │
│      (Pure Kotlin / Business Logic)  │
└──────────────────┬───────────────────┘
                   │
┌──────────────────▼───────────────────┐
│             Data Layer               │
│   Repository Impl ──► Local (SQLDelight) │
│                  └──► Remote (Ktor)  │
└──────────────────────────────────────┘
```

- **Presentation Layer**: Menangani tampilan UI menggunakan Compose Multiplatform dan mengelola *state* melalui ViewModel. Layer ini hanya berkomunikasi dengan Domain layer.
- **Domain Layer**: Berisi aturan bisnis inti (*use cases*) dan antarmuka repository. Layer ini murni Kotlin dan tidak bergantung pada framework atau platform apapun.
- **Data Layer**: Bertanggung jawab atas implementasi repository, pengelolaan data lokal (SQLDelight), dan komunikasi dengan server/API eksternal (Ktor).

---

## 🚀 Setup

Ikuti langkah-langkah berikut untuk menjalankan proyek Money-Z secara lokal:

### Prasyarat
- Android Studio **Hedgehog** (2023.1.1) atau lebih baru
- JDK **17** atau lebih baru
- Android Emulator atau perangkat fisik dengan USB Debugging aktif

### Langkah-langkah

**1. Clone repository**

```bash
git clone https://github.com/[USERNAME]/[REPO-NAME].git
cd [REPO-NAME]
```

**2. Buka di Android Studio**

- Buka Android Studio, pilih **File** → **Open...**.
- Arahkan ke folder hasil *clone* di atas.
- Tunggu hingga proses **Gradle Sync** selesai secara otomatis.

**3. Run di emulator atau device**

- Di toolbar atas, pastikan konfigurasi run yang dipilih adalah **`composeApp`**.
- Pilih target perangkat (emulator atau device fisik yang terhubung).
- Klik tombol **▶ Run** atau tekan `Shift + F10`.

---

## 📄 Lisensi

Proyek ini dibuat untuk keperluan Tugas Akhir Mata Kuliah **Pengembangan Aplikasi Mobile**  
Program Studi Teknik Informatika — Institut Teknologi Sumatera

---

