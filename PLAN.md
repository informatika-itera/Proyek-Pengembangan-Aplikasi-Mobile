# Project Plan — My Bawang Gacha

## TIM
<table>
  <tr>
    <td align="center">
      <a href="https://github.com/sinavarasina">
        <img src="https://github.com/sinavarasina.png" width="100px;" alt="Varasina Farmadani" style="border-radius:50%;"/><br />
        <sub><b>Varasina Farmadani</b></sub>
      </a><br />
      <sub>NIM. 123140107</sub>
    </td>
    <td align="center">
      <a href="https://github.com/Faiq1818">
        <img src="https://github.com/Faiq1818.png" width="100px;" alt="Faiq Ghozy Erlangga" style="border-radius:50%;"/><br />
        <sub><b>Faiq Ghozy Erlangga</b></sub>
      </a><br />
      <sub>NIM. 123140139</sub>
    </td>
  </tr>
</table>


## Deskripsi Singkat

My Bawang Gacha adalah aplikasi Kotlin Multiplatform untuk menemukan, menyimpan, dan mengatur anime/manga.

Pengguna dapat membuat daftar anime/manga pribadi, melacak status tontonan/bacaan, melihat detail dari Jikan API, mencari anime/manga, menyimpan item ke library lokal, serta menggunakan fitur gacha untuk mendapatkan rekomendasi acak berdasarkan preferensi seperti genre, type, score, status, serta aturan apakah item yang sudah watched/read boleh muncul kembali.

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
- [x] MangaDetailScreen
  - menampilkan detail manga
  - menerima argument ID dari navigation
- [x] AnimeListScreen
  - menampilkan daftar anime berdasarkan tab/kategori
  - mendukung pagination/load more
- [x] MangaListScreen
  - menampilkan daftar manga berdasarkan tab/kategori
  - mendukung pagination/load more
- [x] SearchScreen
  - mencari anime dan manga
  - menampilkan hasil search gabungan berdasarkan tipe media
- [x] MyListScreen / MyLibraryScreen
  - tab/list status:
    - Plan to Watch / Plan to Read
    - Watching / Reading
    - Completed / Watched
    - On Hold
    - Dropped
- [x] ListEntryEditor
  - form tambah/edit item ke daftar pribadi
  - edit status
  - edit progress episode/chapter
  - edit rating pribadi/catatan opsional
- [x] Navigation setup
  - AppNavHost
  - Routes
  - argument passing by ID
  - back navigation
  - navigasi Home, Anime, Manga, Search, Library, Settings, dan Detail

### Domain & Data Layer

- [x] Domain model dasar:
  - `AnimeSummary`
  - `AnimeDetail`
  - `AnimeEpisode`
  - `AnimeRelation`
  - `MangaSummary`
  - `MangaDetail`
  - `MangaRelation`
- [x] Domain model list management:
  - `LibraryEntry`
  - `MediaType`
  - `LibraryStatus`
  - `UserProgress`
  - `UserScore`
- [x] Repository pattern untuk anime detail/recommendation:
  - `AnimeRepository`
  - `AnimeRepositoryImpl`
- [x] Repository pattern untuk manga detail/list:
  - `MangaRepository`
  - `MangaRepositoryImpl`
- [x] Repository pattern untuk search:
  - `SearchRepository`
  - `SearchRepositoryImpl`
- [x] Repository untuk list lokal pengguna:
  - `LibraryRepository`
  - `LibraryRepositoryImpl`
- [x] SQLDelight database setup
- [x] SQLDelight table untuk library:
  - saved anime/manga
  - status
  - progress episode/chapter
  - user score
  - updated timestamp
- [x] SQLDelight table untuk episode progress anime
- [x] SQLDelight table/cache untuk anime detail, manga detail, relation preview, dan media page
- [x] CRUD library:
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
  - Refreshing
- [x] ViewModel menggunakan repository, bukan langsung service API
- [x] DTO Jikan tidak dibawa langsung ke UI detail
- [x] Koin modules untuk data + ViewModel
- [x] Koin network module memakai satu shared `HttpClient` binding
- [x] Basic validation untuk form list entry
- [x] Confirm dialog saat delete item dari list
- [x] Basic manual test untuk flow CRUD

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
- [x] Search anime via Jikan
- [x] Search manga via Jikan
- [x] Fetch manga detail
- [x] Fetch manga list/top manga
- [x] Fetch current season, upcoming season, top anime, dan season archive
- [x] DTO mapping lengkap untuk fitur yang dipakai:
  - Anime DTO → domain model
  - Manga DTO → domain model
  - Search DTO → domain model
  - Relation preview DTO → domain model
- [x] Error handling Jikan dasar:
  - network/server error
  - empty result
  - rate limit
  - unavailable server / HTTP error
- [x] Rate limiter untuk Jikan API
- [x] Debounce search query agar tidak spam request API

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

- [x] Cache anime/manga detail ke SQLDelight
- [x] Cache relation preview ke SQLDelight
- [x] Cache media page/list ke SQLDelight
- [x] Pull-to-refresh di Home/Discover
- [x] Pull-to-refresh di AnimeList
- [x] Pull-to-refresh di MangaList
- [x] Pull-to-refresh di Search
- [x] Pull-to-refresh di MyLibrary
- [x] Pull-to-refresh di AnimeDetail
- [x] Pull-to-refresh di MangaDetail
- [x] Refresh state:
  - idle
  - refreshing
  - success
  - failed but cache/content available
- [x] Stale-while-revalidate untuk refresh list/detail agar konten lama tetap tampil saat refresh
- [x] Force refresh untuk detail screen agar pull-refresh bisa bypass cache lama
- [ ] Offline mode final untuk seluruh screen saat tidak ada koneksi

### Additional Screen

- [x] SettingsScreen
  - theme mode: system/light/dark
  - clear cache
  - app info/about
- [x] About/Info section
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
- [x] Fix bug duplicate/single `HttpClient` binding di network module
- [x] Fix compile error `AnimatedContent` content lambda pada `AnimatedSectionContent`
- [x] Fix blink putih awal navigasi dengan root surface/background
- [ ] Fix semua P0 bugs
- [ ] Fix semua broken navigation
- [ ] Pastikan semua fitur Sprint 2 dan Sprint 3 tetap berjalan

### UI Polish

- [ ] Konsistensi spacing berbasis 8dp grid
- [ ] Konsistensi typography
- [ ] Konsistensi warna Material 3
- [x] Empty state yang jelas untuk:
  - list kosong
  - search no result
  - cache kosong saat offline
- [x] Error state yang ramah + retry button
- [x] Loading state/skeleton untuk screen yang mengambil API
  - anime list skeleton
  - manga list skeleton
  - library list skeleton
- [ ] Image placeholder/error placeholder
- [ ] Perbaiki long text overflow
- [x] Animasi ringan:
  - section transition di AnimeDetail
  - section transition di MangaDetail
  - pull-refresh indicator
  - button/card feedback dasar
- [x] UI refresh tidak mengosongkan konten lama saat refresh berjalan

### Testing

- [ ] Unit tests minimal 10:
  - Repository tests
  - ViewModel tests
  - Mapper tests
  - Gacha filter/random logic tests
- [x] Mapper tests untuk Jikan anime/manga dasar
- [x] Service tests untuk endpoint Jikan dasar
- [x] Repository/search tests dasar
- [ ] ViewModel tests untuk screen utama
- [ ] Gacha filter/random logic tests
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

- [x] Gunakan key di LazyColumn/LazyVerticalGrid
- [x] Tambahkan `contentType` di LazyColumn/LazyVerticalGrid untuk item sejenis
- [x] Hindari request API berlebihan
  - Jikan rate limiter
  - cache detail/list
  - debounce search
- [x] Pastikan search memakai debounce
- [x] Pastikan screen detail tidak terasa jank
  - animated section transition
  - pull-refresh tanpa blank state
  - stale-while-revalidate
- [x] Kurangi blink putih saat navigasi dengan root background yang stabil
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
- [ ] Pull-refresh dan cache behavior sudah diuji pada device/emulator
- [ ] Flow Home → Detail → Add/Edit Library → Search sudah diuji ulang

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
- [ ] Dokumentasi cache/offline behavior
- [ ] Dokumentasi pull-refresh dan stale-while-revalidate behavior
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
  - cache + pull-refresh
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

- [x] Buka Home/Discover
- [x] Tampilkan anime/manga recommendation
- [x] Buka detail anime
- [x] Buka detail manga
- [x] Tambahkan item ke MyList
- [x] Ubah status/progress item
- [x] Search anime/manga
- [x] Tunjukkan pull-refresh di list/detail
- [ ] Jalankan gacha dengan preferensi:
  - genre tertentu
  - minimum score
  - media type anime/manga
  - include/exclude watched/read
- [ ] Buka hasil gacha ke detail
- [ ] Tunjukkan error/offline handling atau fallback cache
- [x] Tunjukkan Settings/About jika tersedia

### Expected Q&A Preparation

- [ ] Mengapa menggunakan Kotlin Multiplatform?
- [ ] Mengapa menggunakan Clean Architecture + MVVM?
- [ ] Bagaimana data user disimpan?
- [ ] Bagaimana aplikasi handle Jikan API rate limit?
- [ ] Bagaimana aplikasi handle offline mode?
- [ ] Bagaimana pull-refresh tetap mempertahankan konten lama?
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