# MetaForge: MLBB Strategic Drafting Advisor

## 1. Deskripsi Proyek
**MetaForge** adalah aplikasi *mobile multiplatform* yang dirancang untuk memandu pemain Mobile Legends: Bang Bang (MLBB) dalam fase *draft pick*. MetaForge menggunakan **Sistem Skoring Algoritmik (Rule-Based System)** secara *offline* untuk menghitung poin sinergi dan persentase *counter* lawan.

## 2. Tim Pengembang
* **Anggota 1:** Anselmus Herpin Hasugian - 123140020 - @forkaton
* **Anggota 2:** Adi Septriansyah - 123140021 - @Protoflicker

## 3. Fitur Utama
### Minimum Requirements
* **Drafting Dashboard:** Menampilkan data meta terkini (Top Ban, Top Pick).
* **Hero Encyclopedia:** Katalog hero lengkap beserta data *base score* dan nilai positif negatif dari hero tersebut.
* **Draft Simulator:** Simulasi fase *pick & ban* interaktif.
* **Search & Filter:** Pencarian hero berdasarkan *role* atau nama.
* **Responsive UI:** Menggunakan antarmuka Material Design 3 yang optimal untuk berbagai ukuran layar.

## 4. Tech Stack
* **Framework:** Kotlin Multiplatform (KMP) & Compose Multiplatform
* **Architecture:** Clean Architecture + MVVM Pattern
* **Networking:** Ktor Client & Kotlinx Serialization
* **Local Storage:** SQLDelight (Database) & DataStore (Preferences)
* **Dependency Injection:** Koin
* **AI Integration:** Google Gemini API (Untuk *Narrative Reporting*)
* **CI/CD:** GitHub Actions

## 5. Arsitektur Proyek
Aplikasi ini dikembangkan dengan prinsip **Clean Architecture**:
* `data/`: Mengelola database lokal SQLDelight (tabel *Hero, Synergy, Counter*) dan integrasi Ktor untuk API.
* `domain/`: Berisi logika murni aplikasi (*Use Cases*), termasuk algoritma penghitungan skor komprehensif untuk *drafting*.
* `presentation/`: Menangani antarmuka visual menggunakan Compose, serta manajemen state reaktif menggunakan `StateFlow`.

## 6. Alur Kerja Proyek (Sprint 1 - 5)
* **Sprint 1: Planning & Setup**
  Perencanaan fitur, perancangan skema algoritma *scoring*, inisialisasi repositori KMP, setup pipeline CI/CD, dan modifikasi *package name*.
* **Sprint 2: Core Features**
  Implementasi database hero secara lokal, pembuatan UI *Draft Simulator*, dan integrasi navigasi antar layar.
* **Sprint 3: Advanced Features**
  Implementasi logika algoritma *Scoring System* di dalam *Domain Layer* dan penerapan fitur pencarian *debounce*.
* **Sprint 4: Polish & Testing**
  Penyempurnaan desain UI/UX, integrasi Gemini API untuk narasi *Synergy Report*, serta pembuatan Unit Tests dan pengujian aplikasi.
* **Sprint 5: Final Preparation**
  Pengujian stabilitas *offline*, kompilasi rilis APK, penulisan dokumen presentasi akhir, dan persiapan demonstrasi UAS.
