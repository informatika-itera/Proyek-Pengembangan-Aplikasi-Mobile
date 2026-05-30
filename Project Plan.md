## 📅 Project Plan & Task Assignment

### Sprint 1: Planning & Setup (Week 11) - [PASSED]
* **Target:** Pembentukan tim, penentuan ide proyek ("Rewind"), inisialisasi struktur folder Clean Architecture, konfigurasi awal Koin DI, pengaturan GitHub Actions CI, dan penulisan dokumentasi README awal.
* **Assignment:** Bersama seluruh anggota tim.

### Sprint 2: Core Features (Week 12) - [PASSED]
* **Target:** Implementasi skema database lokal SQLDelight (CRUD), perancangan minimal 3 halaman utama (Home, Detail, Add/Edit), dan setup navigasi antar-halaman terintegrasi argumen passing.
* **Assignment:**
    * **Refi Ikhsanti (Domain):** Pembuatan Domain Model `Movie`, `MovieRepository` interface, dan Use Cases CRUD.
    * **Choirunnisa Syawaldina (Data):** Penulisan skema `Movie.sq`, implementasi `MovieRepositoryImpl`, pembuatan mapper, dan konfigurasi modul DI Koin.
    * **Keira Lakeisha (Presentation):** Pembuatan `HomeScreen`, `DetailScreen`, `AddEditScreen`, `ViewModels`, dan konfigurasi `AppNavHost`.

### Sprint 3: Advanced Features (Week 13) - [CURRENT]
* **Target:** Integrasi TMDB REST API menggunakan Ktor Client, fitur pencarian (*search & filter*), serta implementasi bonus fitur asisten AI berbasis Google Gemini API.
* **Assignment:**
    * **Refi Ikhsanti (API Integration & Advanced System):** Integrasi TMDB API (Ktor Client) untuk fitur Search & Detail, implementasi Offline-First Caching (sinkronisasi API ke database), serta pengerjaan Light Mode & SettingsScreen untuk manajemen preferensi pengguna.
    * **Choirunnisa Syawaldina (Feature Polish & Analytics):** Pembuatan ProfileScreen untuk menampilkan data pengguna & statistik tontonan, serta implementasi Search & Filter di HomeScreen menggunakan fungsi repository.
    * **Keira Lakeisha (Enhancement & AI):** Implementasi Compose Animation untuk memperhalus transisi UI, serta optimalisasi fitur AI Assistant (melalui Prompt Engineering) agar rekomendasi semakin relevan.

### Sprint 4: Polish & Testing (Week 14)
* **Target:** Poles visual UI/UX (animasi mikro, keselarasan palet warna *twilight*), penyelesaian minimal 10 unit tests dan 3 UI tests dengan coverage > 50%.

### Sprint 5: Final Preparation (Week 15)
* **Target:** Perbaikan bug akhir (*bug fixes*), finalisasi dokumentasi, dan persiapan demonstrasi proyek untuk UAS Demo Day.