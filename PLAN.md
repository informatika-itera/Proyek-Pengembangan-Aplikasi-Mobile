# Project Plan — My Bawang Gacha

**Tim:** Varasina Farmadani (123140107) · Faiq Ghozy Erlangga (123140139)

## Deskripsi Singkat

My Bawang Gacha adalah aplikasi Kotlin Multiplatform untuk menemukan, menyimpan, dan mengatur anime/manga.

Pengguna dapat membuat daftar anime/manga pribadi, melacak status tontonan/bacaan, melihat detail dari Jikan API, dan menggunakan fitur gacha untuk mendapatkan rekomendasi acak berdasarkan preferensi seperti genre, type, score, status, serta aturan apakah item yang sudah watched/read boleh muncul kembali.

---

## Sprint 1 — Planning & Setup
**Referensi:** [Materi 11 — Project Sprint 1: Planning](https://kuliah2.itera.ac.id/pluginfile.php/78212/mod_resource/content/1/Materi_11_Project_Sprint1_Planning.pdf)

- [x] Pembentukan tim dan pembagian role
- [x] Pemilihan ide proyek: anime/manga manager + preference-based gacha
- [x] Setup GitHub repository
- [x] Setup struktur Clean Architecture:
  - `data/`
  - `domain/`
  - `presentation/`
  - `core/`
- [x] Setup Koin Dependency Injection
- [x] Setup GitHub Actions CI
- [x] README awal berisi:
  - nama aplikasi
  - anggota tim
  - deskripsi singkat
  - tech stack
  - cara menjalankan project
- [x] Project plan document
- [x] Menentukan core entity:
  - Anime/Manga item
  - Watch/Read status
  - User library/list entry
  - Gacha preference

---

## Sprint 2 — Core Features
**Referensi:** [Materi 12 — Project Sprint 2: Core Features](https://kuliah2.itera.ac.id/pluginfile.php/78227/mod_resource/content/1/Materi_12_Project_Sprint2_Core_Features.pdf)

Fokus Sprint 2: fitur dasar aplikasi harus berjalan secara lokal dengan arsitektur yang rapi.

### UI & Navigation

- [x] HomeScreen / DiscoverScreen
  - menampilkan ringkasan anime/manga atau rekomendasi awal
- [x] AnimeDetailScreen
  - menampilkan detail anime
  - menerima argument ID dari navigation
- [X] MyListScreen
  - tab/list status:
    - Plan to Watch / Plan to Read
    - Watching / Reading
    - Completed / Watched
    - On Hold
    - Dropped
- [X] ListEntryEditor
  - form tambah/edit item ke daftar pribadi
  - edit status
  - edit progress episode/chapter
  - edit rating pribadi/catatan opsional
- [x] Navigation setup
  - AppNavHost
  - Routes
  - argument passing by ID
  - back navigation

### Domain & Data Layer

- [x] Domain model dasar:
  - `AnimeSummary`
  - `AnimeDetail`
  - `AnimeEpisode`
  - `AnimeRelation`
- [X] Domain model list management:
  - `LibraryEntry`
  - `MediaType`
  - `LibraryStatus`
  - `UserProgress`
  - `UserScore`
- [x] Repository pattern untuk anime detail/recommendation:
  - `AnimeRepository`
  - `AnimeRepositoryImpl`
- [X] Repository untuk list lokal pengguna:
  - `LibraryRepository`
  - `LibraryRepositoryImpl`
- [x] SQLDelight database setup
- [X] SQLDelight table untuk library:
  - saved anime/manga
  - status
  - progress episode/chapter
  - user score
  - updated timestamp
- [x] SQLDelight table untuk episode progress anime
- [X] CRUD library:
  - Create: tambah anime/manga ke list
  - Read: tampilkan list berdasarkan status
  - Update: ubah status/progress/rating
  - Delete: hapus dari list

### State Management & Quality

- [x] UI states:
  - Loading
  - Success
  - Error
  - Empty
- [x] ViewModel menggunakan repository, bukan langsung service API
- [x] DTO Jikan tidak dibawa langsung ke UI detail
- [x] Koin modules untuk data + ViewModel
- [X] Basic validation untuk form list entry
- [X] Confirm dialog saat delete item dari list
- [X] Basic manual test untuk flow CRUD

---

## Sprint 3 — Advanced Features
**Referensi:** [Materi 13 — Project Sprint 3: Advanced Features](https://kuliah2.itera.ac.id/pluginfile.php/78217/mod_resource/content/1/Materi_13_Project_Sprint3_Advanced_Features.pdf)

Fokus Sprint 3: fitur pencarian, integrasi API yang lebih lengkap, gacha, cache/offline, dan screen tambahan.

### Jikan API Integration

- [x] Basic Jikan API integration dengan Ktor + Kotlinx Serialization
- [x] Fetch anime recommendations
- [x] Fetch anime full detail
- [x] Fetch anime episodes
- [x] Fetch relation preview
- [ ] Search anime via Jikan
- [ ] Search manga via Jikan
- [ ] Fetch manga detail
- [ ] DTO mapping lengkap:
  - Anime DTO → domain model
  - Manga DTO → domain model
  - Search DTO → domain model
- [ ] Error handling Jikan:
  - network error
  - empty result
  - rate limit
  - unavailable server
- [ ] Debounce search query agar tidak spam request API

### Gacha Feature

- [ ] GachaScreen
  - input preferensi pengguna:
    - media type: anime / manga / both
    - genre
    - score minimum
    - status airing/publishing/completed
    - type: TV, Movie, OVA, Manga, Light Novel, dll
    - include watched/read item atau tidak
- [ ] GachaResultScreen
  - menampilkan hasil gacha
  - tombol reroll
  - tombol buka detail
  - tombol tambah ke list
- [ ] Gacha logic di domain/usecase
  - filter kandidat berdasarkan preferensi
  - randomize hasil
  - exclude item tertentu jika user memilih exclude watched/read
- [ ] Simpan gacha preference terakhir secara lokal
- [ ] Optional: simpan history hasil gacha

### Offline & Cache

- [ ] Cache anime/manga detail ke SQLDelight
- [ ] Cache search result/recommendation seperlunya
- [ ] Offline fallback:
  - tampilkan data cache saat tidak ada internet
  - tampilkan pesan error yang jelas saat data belum pernah dicache
- [ ] Pull-to-refresh di Home/Discover atau MyList
- [ ] Refresh state:
  - idle
  - refreshing
  - success
  - failed but cache available

### Additional Screen

- [ ] SettingsScreen
  - theme mode: system/light/dark
  - clear cache
  - app info/about
- [ ] About/Info section
  - sumber data: Jikan API
  - keterangan bahwa list user disimpan lokal

---

## Sprint 4 — Polish & Testing
**Referensi:** [Materi 14 — Project Sprint 4: Polish & Testing](https://kuliah2.itera.ac.id/pluginfile.php/78218/mod_resource/content/1/Materi_14_Project_Sprint4_Polish_Testing.pdf)

Fokus Sprint 4: stabilitas, konsistensi UI, edge case, performa, dan test.

### Bug Fixing

- [ ] Buat daftar known bugs di GitHub Issues
- [ ] Prioritaskan bug:
  - P0: crash / data loss / fitur utama rusak
  - P1: flow penting terganggu
  - P2: minor UI/UX
- [ ] Fix semua P0 bugs
- [ ] Fix semua broken navigation
- [ ] Pastikan semua fitur Sprint 2 dan Sprint 3 tetap berjalan

### UI Polish

- [ ] Konsistensi spacing berbasis 8dp grid
- [ ] Konsistensi typography
- [ ] Konsistensi warna Material 3
- [ ] Empty state yang jelas untuk:
  - list kosong
  - search no result
  - cache kosong saat offline
- [ ] Error state yang ramah + retry button
- [ ] Loading state/skeleton untuk screen yang mengambil API
- [ ] Image placeholder/error placeholder
- [ ] Perbaiki long text overflow
- [ ] Animasi ringan:
  - list item animation
  - screen transition
  - button/card feedback

### Testing

- [ ] Unit tests minimal 10:
  - Repository tests
  - ViewModel tests
  - Mapper tests
  - Gacha filter/random logic tests
- [ ] UI tests minimal 3 critical journey:
  - buka Home → Detail
  - tambah item ke MyList → muncul di list
  - gacha dengan preferensi → buka result/detail
- [ ] Test error/edge cases:
  - empty list
  - network error
  - no internet
  - missing image
  - long title/synopsis
- [ ] Coverage target minimal 50%
- [ ] README update dengan cara menjalankan test

### Performance

- [ ] Gunakan key di LazyColumn/LazyVerticalGrid
- [ ] Hindari request API berlebihan
- [ ] Pastikan search memakai debounce
- [ ] Pastikan screen detail tidak terasa jank
- [ ] Review recomposition berat di UI besar

---

## Sprint 5 — Final Preparation
**Referensi:** [Materi 15 — Project Sprint 5: Final Preparation](https://kuliah2.itera.ac.id/pluginfile.php/78229/mod_resource/content/1/Materi_15_Project_Sprint5_Final_Preparation.pdf)

Fokus Sprint 5: finalisasi, demo, release build, dokumentasi, dan presentasi.

### Final App Stabilization

- [ ] Semua bug utama fixed
- [ ] Tidak ada crash pada flow demo
- [ ] App bisa dijalankan di device/emulator demo
- [ ] Data demo realistis sudah disiapkan
- [ ] Offline/error path sudah diuji

### Documentation

- [ ] README finalized:
  - deskripsi aplikasi
  - anggota tim + role
  - fitur utama
  - tech stack
  - arsitektur
  - setup guide
  - cara build/run
  - cara menjalankan test
  - screenshots
- [ ] Architecture diagram:
  - UI Layer
  - ViewModel + State
  - Repository
  - Local SQLDelight
  - Remote Jikan API
- [ ] Catatan endpoint Jikan yang dipakai
- [ ] Known limitations / future improvements

### Release & Submission

- [ ] Release APK built
- [ ] Release APK tested
- [ ] Version name/code diset
- [ ] Pastikan credential/secret tidak masuk Git
- [ ] Submit:
  - GitHub repository
  - APK
  - slides PDF/PPTX

### Demo Preparation

- [ ] Presentation slides:
  - title
  - problem statement
  - solution overview
  - features
  - demo flow
  - architecture
  - challenges & learnings
  - future plans
  - Q&A
- [ ] Demo script written
- [ ] Demo flow 10–15 menit:
  - buka Home/Discover
  - buka Detail
  - tambah item ke MyList
  - update progress/status
  - search anime/manga
  - jalankan gacha berdasarkan preferensi
  - tunjukkan offline/error handling
- [ ] Backup video recording demo
- [ ] Tim latihan demo minimal 2x
- [ ] Siapkan jawaban Q&A:
  - kenapa pakai KMP?
  - kenapa Clean Architecture?
  - bagaimana handle offline?
  - bagaimana handle Jikan rate limit?
  - bagaimana pembagian kerja tim?
  - apa tantangan teknis terbesar?

---

## UAS — Final Demo Day
**Referensi:** [Materi 16 — UAS Final Demo Day](https://kuliah2.itera.ac.id/pluginfile.php/78228/mod_resource/content/1/Materi_16_UAS_Final_Demo_Day.pdf)

Fokus UAS: presentasi akhir, live demo, technical explanation, code review, dan Q&A.

### Presentation Format

Target durasi: 10–15 menit.

- [ ] Introduction, 1–2 menit:
  - perkenalan tim
  - nama aplikasi
  - problem statement
- [ ] Features Overview, 1–2 menit:
  - fitur utama aplikasi
  - manfaat aplikasi untuk user
- [ ] Live Demo, 5–7 menit:
  - tunjukkan aplikasi berjalan langsung
  - demo flow utama tanpa terlalu banyak pause
- [ ] Technical Highlights, 2–3 menit:
  - arsitektur aplikasi
  - repository pattern
  - local storage
  - Jikan API integration
  - gacha logic
  - error/offline handling
- [ ] Q&A, 2–3 menit:
  - jawab pertanyaan panel
  - jelaskan alasan teknis dengan percaya diri

### UAS Grading Focus

- [ ] App Functionality:
  - fitur utama berjalan
  - tidak crash
  - UX cukup polished
- [ ] Code Quality:
  - Clean Architecture
  - MVVM
  - Repository pattern
  - state management jelas
  - testing tersedia
- [ ] Demo & Presentation:
  - flow demo jelas
  - pembagian bicara antar anggota
  - tidak membaca slide terus-menerus
- [ ] Technical Depth:
  - bisa menjelaskan keputusan teknis
  - bisa menjelaskan problem solving
  - bisa menjelaskan trade-off
- [ ] Q&A Response:
  - memahami kode sendiri
  - bisa menjawab alasan penggunaan teknologi
  - bisa menjelaskan keterbatasan dan rencana pengembangan

### UAS Demo Flow

- [ ] Buka Home/Discover
- [ ] Tampilkan anime/manga recommendation
- [ ] Buka detail anime
- [ ] Tambahkan item ke MyList
- [ ] Ubah status/progress item
- [ ] Search anime/manga
- [ ] Jalankan gacha dengan preferensi:
  - genre tertentu
  - minimum score
  - media type anime/manga
  - include/exclude watched/read
- [ ] Buka hasil gacha ke detail
- [ ] Tunjukkan error/offline handling atau fallback cache
- [ ] Tunjukkan Settings/About jika tersedia

### Expected Q&A Preparation

- [ ] Mengapa menggunakan Kotlin Multiplatform?
- [ ] Mengapa menggunakan Clean Architecture + MVVM?
- [ ] Bagaimana data user disimpan?
- [ ] Bagaimana aplikasi handle Jikan API rate limit?
- [ ] Bagaimana aplikasi handle offline mode?
- [ ] Bagaimana gacha memilih hasil?
- [ ] Bagaimana mencegah item watched/read muncul lagi jika user tidak ingin?
- [ ] Bagaimana pembagian kerja tim?
- [ ] Apa tantangan teknis terbesar?
- [ ] Apa fitur yang akan dikembangkan berikutnya?

### Backup Plan

- [ ] APK release sudah tersedia
- [ ] Device/emulator demo sudah dites
- [ ] Internet/hotspot cadangan disiapkan
- [ ] Screenshot/video backup demo disiapkan
- [ ] Data demo sudah disiapkan
- [ ] Jika live API gagal, tunjukkan cache/offline state atau video backup