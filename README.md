# MetaForge: MLBB Strategic Drafting Advisor

## 1. Deskripsi Proyek
**MetaForge** adalah aplikasi *mobile multiplatform* yang dirancang untuk memandu pemain Mobile Legends: Bang Bang (MLBB) dalam fase *draft pick*. MetaForge menggunakan **Sistem Skoring Algoritmik (Rule-Based System)** secara *offline* untuk menghitung prioritas *pick*, poin sinergi, dan persentase *counter* lawan untuk memberikan rekomendasi *drafting* sekelas turnamen *Esports*.

## 2. Tim Pengembang
* **Anggota 1:** Anselmus Herpin Hasugian - 123140020 - @forkaton
* **Anggota 2:** Adi Septriansyah - 123140021 - @Protoflicker

## 3. Fitur Utama (Sprint 2)
* **Draft Simulator (Sistem 1-2-2-2-2-1):** Simulasi fase *pick & ban* 5v5 yang dikunci secara akurat sesuai aturan turnamen resmi MLBB. Pengguna bisa mengatur *Rank*, *Party Size*, dan *Turn Pick*.
* **Hero Encyclopedia & Tier List:** Katalog 30 Hero Meta yang secara otomatis dikelompokkan ke dalam Tier SS (*Ban Priority*), Tier S , dan Tier A (*Situational*).
* **Smart AI Recommendation:** Saat giliran pengguna tiba, sistem akan menganalisis draf yang ada dan memberikan saran hero terbaik beserta *Warning* jika hero *counter* musuh belum di-*ban*.
* **CRUD & State Management:** Pengguna bebas memilih (Create/Update) dan membatalkan/menghapus (Delete) hero pada *slot* akibat *misclick* selama draf berlangsung.

## 4. Tech Stack
* **Framework:** Kotlin Multiplatform (KMP) & Compose Multiplatform
* **Architecture:** Clean Architecture + MVVM Pattern
* **Networking:** Ktor Client & Kotlinx Serialization (Disiapkan untuk integrasi API di Sprint 3)
* **Local Storage:** DataStore (DraftPreferences) & Data Statis (HeroDataSource)
* **Dependency Injection:** Koin
* **State Management:** StateFlow & UI State Sealed Classes
* **CI/CD:** GitHub Actions

## 5. Arsitektur Proyek
Aplikasi ini dikembangkan dengan prinsip **Clean Architecture**:
* `data/`: Mengelola data lokal (30 Meta Heroes), preferensi `DataStore`, dan implementasi `DraftRepositoryImpl`.
* `domain/`: Berisi model murni aplikasi (`Hero`, `DraftState`, `HeroRole`) dan *Repository Interfaces* yang menjembatani data dan UI.
* `presentation/`: Menangani antarmuka visual (Compose) berbahasa Inggris penuh, serta manajemen *state* reaktif menggunakan `ViewModel` (contoh: `DraftViewModel`, `HeroSelectViewModel`).

## 6. Alur Kerja Proyek (Sprint 1 - 5)
* ✅ **Sprint 1: Planning & Setup**
  Perencanaan fitur, inisialisasi repositori KMP, setup pipeline CI/CD, Koin DI, dan modifikasi *package name*.
* ✅ **Sprint 2: Core Features (Saat Ini)**
  Implementasi database hero lokal, pembuatan antarmuka (Home, DraftSetup, DraftArena, HeroList, HeroInfo), sistem CRUD untuk fase draf, navigasi argumen kompleks, dan State Management penuh. 
* ⏳ **Sprint 3: Advanced Features**
  Integrasi API Gemini secara penuh (Online), pemantapan logika *Scoring System* lanjutan, dan penerapan fitur pencarian *debounce*.
* ⏳ **Sprint 4: Polish & Testing**
  Pembuatan Unit Tests, UI Tests, penyempurnaan animasi, dan pengujian batas responsivitas batas aplikasi.
* ⏳ **Sprint 5: Final Preparation**
  Pengujian stabilitas *offline*, kompilasi rilis APK, penulisan dokumen presentasi akhir, dan persiapan demonstrasi UAS.

---

## Video Demo Sprint 2

Tautan berikut berisi demonstrasi pemenuhan target **Sprint 2** (Navigasi antar layar, implementasi Data Layer & Repository, CRUD pada fase *Pick & Ban*, dan pengelolaan UI State):


https://github.com/user-attachments/assets/9f3c1265-c264-40a0-b0ac-dc754f712ab0


