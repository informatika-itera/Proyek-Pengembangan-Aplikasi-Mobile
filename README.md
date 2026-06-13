# 🌹 ROSÉA - Beauty E-Commerce App

## 👥 Informasi Tim
Proyek ini dikembangkan oleh:

| Nama | NIM | Peran | GitHub |
| :--- | :--- | :--- | :--- |
| **Andini Rahma Kemala** | 123140067 | Presentation Layer, UI/UX, Testing | [@04-123140067-AndiniRahmaKemala](https://github.com/04-123140067-AndiniRahmaKemala) |
| **Miftahul Khair** | 123140064 | Domain & Data Layer, Database, API | [@MIFTAAHULKHR](https://github.com/MIFTAAHULKHR) |

---

## 📝 Deskripsi Aplikasi
**ROSÉA** adalah platform e-commerce produk kecantikan yang dirancang menggunakan **Compose Multiplatform**. Aplikasi ini bertujuan untuk memberikan pengalaman berbelanja yang personal bagi pengguna dengan menghadirkan fitur **AI Beauty Advisor** yang didukung oleh Google Gemini, membantu pengguna menemukan produk yang paling sesuai dengan kebutuhan kulit mereka.

---

## ✨ Fitur Utama
- **Product Catalog**: Penjelajahan produk kecantikan dengan filter kategori yang responsif.
- **Smart Search**: Pencarian produk real-time menggunakan teknik *debounce* untuk efisiensi.
- **Shopping Bag**: Manajemen keranjang belanja yang intuitif sebelum melakukan checkout.
- **AI Beauty Advisor**: Konsultasi kecantikan cerdas terintegrasi dengan **Gemini AI**.
- **Offline-First**: Sinkronisasi data lokal menggunakan SQLDelight sehingga aplikasi tetap dapat diakses tanpa koneksi internet.

---

## 🛠 Tech Stack
- **Multiplatform Framework**: Compose Multiplatform (Android & iOS)
- **Dependency Injection**: Koin
- **Local Database**: SQLDelight
- **Networking**: Ktor Client
- **Local Storage**: Jetpack DataStore
- **Concurrency**: Kotlin Coroutines & Flow
- **AI Integration**: Google Gemini API

---

## 📐 Arsitektur
Aplikasi ini menerapkan **Clean Architecture** dengan pola **MVVM (Model-View-ViewModel)**:

- **Presentation Layer**: Mengelola UI dengan Compose dan UI State dengan ViewModels.
- **Domain Layer**: Berisi Business Logic, Model Domain, dan Interface Repository (Pure Kotlin).
- **Data Layer**: Implementasi Repository, integrasi SQLDelight (Lokal), dan Ktor (Remote).

---

## 🚀 Getting Started

1. **Clone Repository**
   ```bash
   git clone https://github.com/informatika-itera/Proyek-Pengembangan-Aplikasi-Mobile.git
   ```
2. **Setup API Key**
   - Buat file `local.properties` di root project.
   - Tambahkan API Key Gemini Anda: `GEMINI_API_KEY=AIzaSy...`
3. **Buka di Android Studio**
   - Gunakan **Android Studio Ladybug (2024.2.1)** atau versi terbaru.
   - Tunggu proses Gradle Sync selesai.
4. **Jalankan di Device**
   - Pilih modul `composeApp` dan jalankan di emulator atau perangkat fisik Android.

---

## 📥 Download
Tautan untuk mengunduh versi terbaru aplikasi:
- [**Download APK (Releases)**](https://drive.google.com/drive/u/0/folders/1hs4WXJSi4xF8fQ64f5mo_P4LIonOy4RP)

---

## Video Presentasi ROSÉA 123140064-123140067
▶️ [Presentasi Akhir ROSÉA](https://youtu.be/vTU-n-fGePc)

---
## 📥 PPT
Tautan untuk mengunduh PPT PROYEK:
- [**Download PPT**](https://canva.link/dzm1stlt26pfqjo)

---

## 🖼 Screenshots
| Home Screen | Product Detail | AI Advisor | Shopping Bag | Profile | Dark Mode |
| :---: | :---: | :---: | :---: | :---: | :---: |
| <img width="720" height="1600" alt="WhatsApp Image 2026-06-12 at 21 37 46" src="https://github.com/user-attachments/assets/d4898fc6-56d3-4f17-9070-4e27769ce113" /> | <img width="720" height="1600" alt="WhatsApp Image 2026-06-12 at 21 37 48" src="https://github.com/user-attachments/assets/ea147850-b505-4f42-8e78-b2349ea8c846" /> | <img width="720" height="1600" alt="WhatsApp Image 2026-06-12 at 21 37 47 (1)" src="https://github.com/user-attachments/assets/f764e89f-f046-450e-aa1d-68df12445f33" /> | <img width="720" height="1600" alt="WhatsApp Image 2026-06-12 at 21 37 48 (1)" src="https://github.com/user-attachments/assets/d3ad2a38-3ac4-4b27-8d05-57a214893f62" /> | <img width="720" height="1600" alt="WhatsApp Image 2026-06-12 at 21 37 48 (2)" src="https://github.com/user-attachments/assets/bc18540e-46fb-46de-9884-99ffd4959032" /> | <img width="720" height="1600" alt="WhatsApp Image 2026-06-12 at 23 17 10" src="https://github.com/user-attachments/assets/e0ed5417-fb39-46be-a9e7-713405960338" /> |  

---

## Test Results

<div align="center">
  <img src="https://github.com/user-attachments/assets/e913af67-8783-4a4a-893e-d349e9d9b4a0" width="100%" alt="ROSÉA Header" />
  <p><i>A modern beauty e-commerce application built with Compose Multiplatform.</i></p>
</div>

---
*Dokumen ini adalah bagian dari Proyek Pengembangan Aplikasi Mobile - ITERA.*
