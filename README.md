<div align="center">
  <img width="150" height="150" alt="Logo FitGen" src="composeApp/src/commonMain/composeResources/drawable/logo2.png" />
</div>

<div align="center">

# 📱 FitGen

![Kotlin](https://img.shields.io/badge/Kotlin-Multiplatform-0081CB?logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-Supported-4CAF50?logo=android&logoColor=white)
![Status](https://img.shields.io/badge/Status-Completed-4CAF50)
[![CI/CD Build](https://github.com/panesbelpois/Proyek-Pengembangan-Aplikasi-Mobile/actions/workflows/ci.yml/badge.svg)](https://github.com/panesbelpois/Proyek-Pengembangan-Aplikasi-Mobile/actions)

*Aplikasi pelacak kebugaran dan kesehatan komprehensif dengan asisten virtual cerdas.*

</div>
---

# Deskripsi Singkat & Latar Belakang

Fitgen adalah aplikasi pelacak kebugaran dan kesehatan komprehensif berbasis Kotlin Multiplatform (KMP). Di era modern, banyak orang kesulitan memantau aktivitas fisik dan pola makan sehat mereka secara konsisten karena kurangnya alat pencatatan yang cerdas dan terintegrasi.

Oleh karena itu, FitGen hadir untuk merevolusi cara pengguna memantau gaya hidup sehat mereka dengan menggabungkan berbagai fungsionalitas dalam satu platform. Mulai dari pencatatan olahraga harian, pelacak nutrisi otomatis dengan AI, hingga asisten virtual pribadi untuk memandu rutinitas kesehatan harian Anda. Semua ini disajikan dalam antarmuka yang modern, dinamis, dan terpadu untuk platform Android dan iOS menggunakan satu basis kode.

## Konteks Proyek

```text
📦 composeApp/src/commonMain
 ┣ **Catatan:** Proyek ini disusun secara khusus untuk memenuhi **Tugas Besar Mata Kuliah Pengembangan Aplikasi Mobile (PAM)**
```
--- 
## Fitur Utama
1. **AI Food Scanner (Pemindai Nutrisi)** - Pindai gambar makananmu, dan AI akan mengidentifikasi nama makanan, estimasi kalori, serta rincian makronutrien (Protein, Karbohidrat, Lemak) secara otomatis
2. **AI Workout Generator/Smart Assistant** - Chatbot cerdas yang siap menjawab pertanyaanmu mengenai rekomendasi latihan, penyesuaian jadwal latihan, diet, dan pemulihan tubuh
3. **Daily Tracker (Nutrisi & Hidrasi)** - Lacak jumlah asupan harianmu, asupan nutrisi, air, serta login streak untuk menjaga motivasi dengan grafik yang interaktif.
4. **Popular Challenges & Exercises** - Ikuti tantangan olahraga populer dan cari katalog latihan dengan panduan animasi/GIF (didukung oleh Wger API).
5. **Dark Mode** - Pengalaman pengguna yang nyaman bagi mata baik di kondisi terang maupun gelap.

## Screenshots
Berikut adalah cuplikan fitur-fitur dari aplikasi FitGen:

1. **Homepage & Dashboard**
   | **Homepage Light Mode** | **Homepage Dark Mode** |
   | :---: | :---: |
   | <img width="200" alt="Homepage Light" src="docs/images/homepage1.jpg" /> | <img width="200" alt="Homepage Dark" src="docs/images/ss1.jpg" /> | 

2. **Popular Challenges & Detail**
   | **Popular Challenges Light** | **Detail Light** | **Popular Challenges Dark** | **Detail Dark** |
   | :---: | :---: | :---: | :---: |
   | <img width="200" alt="Popular Challenges Light" src="docs/images/homepage_detail_popular_challenges.jpg" /> | <img width="200" alt="Detail Light" src="docs/images/homepage_detail_workout.jpg" /> | <img width="200" alt="Popular Challenges Dark" src="docs/images/ss3.jpg" /> | <img width="200" alt="Detail Dark" src="docs/images/ss5.jpg" /> |

3. **AI Workout & Assistant**
   | **AI Assistant Light** | **AI Assistant Dark** |
   | :---: | :---: |
   | <img width="200" alt="AI Assistant Light" src="docs/images/homepage_ai_wo_generator1.jpg" /> | <img width="200" alt="AI Assistant Dark" src="docs/images/ss4.jpg" /> |

4. **Classic Workout & Timer + Tambah Custom Latihan**
   | **Classic Workout Light** | **Timer Light** | **Custom Latihan Light** | **Classic Workout Dark** | **Timer Dark** | **Custom Latihan Dark** | 
   | :---: | :---: | :---: | :---: | :---: | :---: |
   | <img width="200" alt="Classic Workout Light" src="docs/images/homepage2.jpg" /> | <img width="200" alt="Timer Light" src="docs/images/homepage_timer.jpg" /> | <img width="200" alt="Custom Latihan Light" src="docs/images/latihanpage_rutinitas_custom.jpg" /> | <img width="200" alt="Classic Workout Dark" src="docs/images/ss2.jpg" /> | <img width="200" alt="Timer Dark" src="docs/images/ss6.jpg" /> | <img width="200" alt="Custom Latihan Dark" src="docs/images/ss16.jpeg" /> |

5. **Page Latihan & Rekap Latihan**
   | **Page Latihan Light** | **Page Latihan Dark** |
   | :---: | :---: |
   | <img width="200" alt="Rekap Latihan Light" src="docs/images/latihanpage_riwayat_latihan.jpg" /> | <img width="200" alt="Rekap Latihan Dark" src="docs/images/ss7.jpg" /> |

6. **Custom Latihan & Search Bar Latihan**
   | **Rutinitas Baru Light** | **Search Bar Light** | **Rutinitas Baru Dark** | **Search Bar Dark** |
   | :---: | :---: | :---: | :---: |
   | <img width="200" alt="Rutinitas Baru Light" src="docs/images/latihanpage_rutinitas_baru.jpg" /> | <img width="200" alt="Search Light" src="docs/images/latihanpage_search_result.jpg" /> | <img width="200" alt="Rutinitas Baru Dark" src="docs/images/ss14.jpeg" /> | <img width="200" alt="Search Dark" src="docs/images/ss15.jpeg" /> |

7. **Page Nutrisi Harian & AI Cam Food Scanner**
   | **Nutrisi Harian Light** | **Food Scanner Light** | **Nutrisi Harian Dark** | **Food Scanner Dark** |
   | :---: | :---: | :---: | :---: |
   | <img width="200" alt="Nutrisi Light" src="docs/images/nutrisipage.jpg" /> | <img width="200" alt="Scanner Light" src="docs/images/aifoodscanner1.jpg" /> | <img width="200" alt="Nutrisi Dark" src="docs/images/ss10.jpg" /> | <img width="200" alt="Scanner Dark" src="docs/images/ss9.jpg" /> |

8. **Profil Page & Edit Profil**
   | **Profil Light** | **Edit Profil Light** | **Profil Dark** | **Edit Profil Dark** |
   | :---: | :---: | :---: | :---: |
   | <img width="200" alt="Profil Light" src="docs/images/profile_page1.jpg" /> | <img width="200" alt="Edit Profil Light" src="docs/images/edit_profile.jpg" /> | <img width="200" alt="Profil Dark" src="docs/images/ss11.jpg" /> | <img width="200" alt="Edit Profil Dark" src="docs/images/ss12.jpg" /> |

9. **Sprint 4: Unit Test & Code Coverage**
   | **Laporan Otomatis Unit Test (100% Passed)** |
   | :---: |
   | <img width="800" alt="Laporan Unit Test" src="docs/images/ss13.png" /> |

---

## Error Handling Terintegrasi
Aplikasi telah dilengkapi validasi data dan error handling untuk menjamin stabilitas:

* Validasi Profil Pengguna:
   * Maksimal berat badan (BB) adalah 500 kg.
   * Maksimal tinggal badan (TB) adalah 300 cm.
   * Maksimal umur adalah 120 tahun
   * Nilai di atas tidak boleh negatif/minus.
* Koneksi Internet (AI Page): Jika tidak ada koneksi internet saat menggunakan AI Workout atau Scanner, aplikasi akan menolak permintaan dan memunculkan notifikasi/pesan "Cek jaringan Anda".

---

## Seluruh Tech Stack
Aplikasi ini menggunakan teknologi lintas platform yang modern dan clean architecture (MVVM):

* UI Framework: Compose Multiplatform - UI deklaratif untuk Android & iOS.
* Networking: Ktor Client - HTTP Request untuk API Gemini dan Wger.
* Dependency Injection: Koin - Mengelola lifecycle, ViewModel, dan Repository.
* Local Database: SQLDelight - Database SQLite lokal yang type-safe.
* Local Preferences: DataStore & Okio - Menyimpan konfigurasi, state login, dan streak harian.
* Image/Media Processing: Coil3 (render gambar/GIF) & Peekaboo (fitur kamera/galeri native).
* Secrets Management: BuildKonfig - Menyimpan kunci API secara aman.
* Concurrency: Kotlin Coroutines & Flow.

---

## 🏗️ Diagram Arsitektur MVVM

FitGen mengimplementasikan pola arsitektur **MVVM (Model-View-ViewModel)** yang dipadukan dengan prinsip *Clean Architecture* untuk memastikan kode komponen tetap terpisah, modular, mudah diuji (*testable*), serta optimal saat dijalankan di lingkungan Kotlin Multiplatform.

```mermaid
graph TD
    %% Definisi Node Utama
    V["🎨 View / UI Screen<br>(Compose Multiplatform)"]
    VM["⚡ ViewModel<br>(State Holders & Events)"]
    UC["⚙️ Use Cases<br>(Domain Layer / Business Logic)"]
    RI["📋 Repository Interface"]
    RI_Impl["🏗️ Repository Implementation<br>(Data Layer)"]
    
    %% Penyimpanan & Sumber Data
    DB[("💾 Local Database<br>(SQLDelight / DataStore)")]
    API["🌐 Remote API<br>(Ktor / Gemini / Wger)"]

    %% Alur Hubungan Komponen (Logika yang Diperbaiki)
    V -->|"Kirim Intent / Aksi"| VM
    V -.->|"Observe UI State (Flow)"| VM
    VM -->|"Panggil Logic / Use Cases"| UC
    UC -->|"Melakukan Penyimpanan / Akses Data"| RI
    RI_Impl -.->|"Mengimplementasikan"| RI
    RI_Impl -->|"Operasi Local Database"| DB
    RI_Impl -->|"Operasi Remote API"| API

    %% Penataan Gaya Visual (Mermaid Styling)
    classDef layerView fill:#f9f0ff,stroke:#d3adf7,stroke-width:2px;
    classDef layerVM fill:#e6f7ff,stroke:#91d5ff,stroke-width:2px;
    classDef layerDomain fill:#f6ffed,stroke:#b7eb8f,stroke-width:2px;
    classDef layerData fill:#fff7e6,stroke:#ffd591,stroke-width:2px;
    
    class V layerView;
    class VM layerVM;
    class UC,RI layerDomain;
    class RI_Impl,DB,API layerData;
```

---

## 📥 Download Aplikasi (APK)
Anda dapat mengunduh dan mencoba langsung aplikasi FitGen di perangkat Android Anda melalui tautan berikut:
**[⬇️ Download FitGen APK](https://s.itera.id/FitGenAPK)**

---

## Persyaratan Sistem & Instalasi
Persyaratan Sistem:
* **Java**: JDK 17 atau lebih baru
* **IDE**: Android Studio Ladybug/Intellij IDEA
* **Untuk iOS**: macOS dengan versi terbaru Xcode terinstal

Instalasi & Cara Menjalankan
1. Clone repositori ini
   ```bash
   git clone <link-repositori>
   ```
2. Siapkan local.properties: Duplikat file local.properties.example menjadi local.properties dan isi dengan API Key yang valid (untuk Gemini AI API dll).
3. Sinkronisasi Gradle: Buka project di Android Studio, lalu lakukan sync project dengan Gradle files.
4. Jalankan Aplikasi:
   * Android: Pilih konfigurasi run composeApp untuk Android dan tekan Run (shift + F10).
   * iOS: Pilih konfigurasi run iosApp dan jalankan di simulator atau perangkat fisik Apple.
  
---

## Struktur Proyek
Berikut adalah gambaran tingkat tinggi dari struktur proyek Kotlin Multiplatform ini:

```text
📦 composeApp/src/commonMain
 ┣ 📂 core          # Utilitas umum, Config Ktor, Ekstensi, dll.
 ┣ 📂 data          # Repository Implementation, Data Sources (SQLDelight, Ktor).
 ┣ 📂 di            # Konfigurasi Dependency Injection dengan Koin.
 ┣ 📂 domain        # Interface Repository, Models (Entity), dan Use Cases.
 ┣ 📂 presentation  # Layer UI (Screens), ViewModels, dan Theme.
 ┗ 📂 sqldelight    # Skema Tabel Database (.sq) untuk Code Generation.
```
 ---

 ## Video YouTube
 Tonton demonstrasi lengkap fitur dan cara penggunaan aplikasi FitGen pada video di bawah ini:
 <div align="left">
  <a href="https://youtube.com/shorts/2iAqDp_CnL0">
    <img width="300" src="https://img.youtube.com/vi/2iAqDp_CnL0/hqdefault.jpg" alt="Tonton Demo FitGen di YouTube">
  </a>
</div>

 ---

 ## Anggota Tim
 
 | **No** | **Nama** | **NIM** | **Role** | **GitHub** |
 | :---: | --- | :---: | --- | --- |
 | 1 | Anisah Octa Rohila | 123140137 | Lead Developer, UI/UX & AI Integrator | [@panesbelpois](https://github.com/panesbelpois) |
 | 2 | Fanisa Aulia Safitri | 123140121 | Co-Developer, Local Database & QA Tester | [@Applepi121](https://github.com/Applepi121) |


**Dosen Pengampu**: Muhammad Habib Alghifari, S.kom., M.Kom.
