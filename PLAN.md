
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

My Bawang Gacha adalah aplikasi Kotlin Multiplatform untuk menemukan, mencari, menyimpan, dan mengatur anime/manga.

Aplikasi menyediakan fitur discovery, anime list, manga list, search, detail media, library lokal, gacha rekomendasi acak berbasis preferensi, catatan personal, AI assistant berbasis Gemini, pengaturan tampilan, pemantauan request, serta informasi build/release aplikasi.

---

## Sprint 1 — Planning & Setup

**Referensi:** [Materi 11 — Project Sprint 1: Planning](https://kuliah2.itera.ac.id/pluginfile.php/78212/mod_resource/content/1/Materi_11_Project_Sprint1_Planning.pdf)

Fokus Sprint 1: menentukan ide aplikasi, struktur awal project, arsitektur, dan fondasi build.

* [x] Pembentukan tim dan pembagian role
* [x] Pemilihan ide proyek: anime/manga manager + preference-based gacha
* [x] Setup GitHub repository
* [x] Setup Kotlin Multiplatform project:

  * `androidApp`
  * `iosApp`
  * `composeApp`
* [x] Setup struktur Clean Architecture:

  * `core`
  * `data`
  * `domain`
  * `presentation`
* [x] Setup MVVM untuk presentation layer
* [x] Setup Compose Multiplatform untuk UI bersama
* [x] Setup Koin Dependency Injection
* [x] Setup Ktor Client untuk network layer
* [x] Setup SQLDelight untuk database lokal
* [x] Setup DataStore untuk preferensi aplikasi
* [x] Setup GitHub Actions CI
* [x] Setup identitas aplikasi:

  * application id/package: `id.my.sinanonym.mybawanggacha`
  * nama aplikasi: `My Bawang Gacha`
* [x] Menentukan core entity:

  * Anime
  * Manga
  * Library entry
  * Library status
  * User progress
  * User score
  * Gacha preference
  * Gacha history
  * AI chat session
  * Note
* [x] README awal berisi:

  * nama aplikasi
  * deskripsi singkat
  * anggota tim
  * fitur utama
  * tech stack
  * cara build/run dasar
* [x] Project plan document

---

## Sprint 2 — Core Features

**Referensi:** [Materi 12 — Project Sprint 2: Core Features](https://kuliah2.itera.ac.id/pluginfile.php/78227/mod_resource/content/1/Materi_12_ProjectSprint2_Core_Features.pdf)

Fokus Sprint 2: fitur dasar aplikasi berjalan dengan local storage, navigation, dan struktur arsitektur yang rapi.

### UI & Navigation

* [x] HomeScreen / DiscoverScreen:

  * menampilkan rekomendasi awal
  * menampilkan bagian anime dan manga
  * menjadi entry utama aplikasi
* [x] AnimeListScreen:

  * menampilkan daftar anime
  * mendukung pagination/load more
  * mendukung layout grid responsif
* [x] MangaListScreen:

  * menampilkan daftar manga
  * mendukung pagination/load more
  * mendukung layout grid responsif
* [x] AnimeDetailScreen:

  * menampilkan detail anime
  * menerima argument ID dari navigation
  * menampilkan metadata, sinopsis, genre, relasi, dan episode
* [x] MangaDetailScreen:

  * menampilkan detail manga
  * menerima argument ID dari navigation
  * menampilkan metadata, sinopsis, genre, dan relasi
* [x] SearchScreen:

  * mencari anime dan manga
  * mendukung debounce query
  * menampilkan hasil berdasarkan tipe media
* [x] MyLibraryScreen:

  * menampilkan item anime/manga yang disimpan user
  * mendukung status Plan to Watch/Read, Watching/Reading, Completed, On Hold, dan Dropped
* [x] ListEntryEditor:

  * tambah item ke library
  * edit status
  * edit progress episode/chapter
  * edit skor personal
  * edit catatan opsional
  * hapus item dari library
* [x] Navigation setup:

  * `AppNavHost`
  * route utama aplikasi
  * argument passing by ID
  * back navigation
  * navigasi Home, Anime, Manga, Search, Library, Gacha, AI, Settings, dan Detail

### Domain & Data Layer

* [x] Domain model anime:

  * `AnimeSummary`
  * `AnimeDetail`
  * `AnimeEpisode`
  * `AnimeRelation`
* [x] Domain model manga:

  * `MangaSummary`
  * `MangaDetail`
  * `MangaRelation`
* [x] Domain model library:

  * `LibraryEntry`
  * `MediaType`
  * `LibraryStatus`
  * `UserProgress`
  * `UserScore`
* [x] Repository pattern untuk anime:

  * `AnimeRepository`
  * `AnimeRepositoryImpl`
* [x] Repository pattern untuk manga:

  * `MangaRepository`
  * `MangaRepositoryImpl`
* [x] Repository pattern untuk search:

  * `SearchRepository`
  * `SearchRepositoryImpl`
* [x] Repository pattern untuk library lokal:

  * `LibraryRepository`
  * `LibraryRepositoryImpl`
* [x] SQLDelight database setup
* [x] SQLDelight table untuk library:

  * saved anime/manga
  * media type
  * status
  * progress episode/chapter
  * user score
  * notes
  * created timestamp
  * updated timestamp
* [x] SQLDelight table untuk episode progress anime
* [x] SQLDelight table/cache untuk:

  * anime detail
  * manga detail
  * relation preview
  * media page/list
  * search result
  * AI chat
  * notes
* [x] CRUD library:

  * Create: tambah anime/manga ke list
  * Read: tampilkan list berdasarkan status/media
  * Update: ubah status/progress/rating/notes
  * Delete: hapus item dari list

### State Management & Quality

* [x] UI state dasar:

  * Loading
  * Success
  * Error
  * Empty
  * Refreshing
* [x] ViewModel menggunakan repository, bukan langsung remote service
* [x] DTO Jikan tidak dibawa langsung ke UI
* [x] Mapper DTO ke domain model
* [x] Koin module untuk data layer
* [x] Koin module untuk domain/usecase
* [x] Koin module untuk ViewModel
* [x] Shared `HttpClient` binding di network module
* [x] Basic validation untuk form library entry
* [x] Confirm dialog saat delete item dari library
* [x] Verifikasi flow CRUD library

---

## Sprint 3 — Advanced Features

**Referensi:** [Materi 13 — Project Sprint 3: Advanced Features](https://kuliah2.itera.ac.id/pluginfile.php/78217/mod_resource/content/1/Materi_13_ProjectSprint3_Advanced_Features.pdf)

Fokus Sprint 3: integrasi API lebih lengkap, gacha, cache/offline behavior, AI assistant, dan settings.

### Jikan API Integration

* [x] Integrasi Jikan API dengan Ktor + Kotlinx Serialization
* [x] Fetch anime recommendations
* [x] Fetch anime full detail
* [x] Fetch anime episodes
* [x] Fetch anime relation preview
* [x] Fetch manga detail
* [x] Fetch manga list/top manga
* [x] Fetch current season anime
* [x] Fetch upcoming season anime
* [x] Fetch top anime
* [x] Fetch season archive
* [x] Search anime via Jikan
* [x] Search manga via Jikan
* [x] Search filter:

  * query
  * type
  * status
  * score
  * genre
  * rating
  * order/sort
* [x] DTO mapping untuk data yang dipakai aplikasi:

  * Anime DTO ke domain model
  * Manga DTO ke domain model
  * Search DTO ke domain model
  * Relation preview DTO ke domain model
* [x] Error handling Jikan:

  * network error
  * server error
  * empty result
  * rate limit
  * invalid response
* [x] Rate limiter untuk mengurangi spam request Jikan
* [x] Debounce search query

### Gacha Feature

* [x] GachaScreen:

  * input preferensi pengguna
  * media type anime/manga/both
  * genre
  * score minimum
  * status airing/publishing/completed
  * format/type seperti TV, Movie, OVA, Manga, dan Light Novel
  * opsi include/exclude watched/read item
* [x] GachaResultScreen:

  * menampilkan hasil gacha
  * tombol reroll
  * tombol buka detail
  * tombol tambah ke library
* [x] Gacha domain/usecase:

  * filter kandidat berdasarkan preferensi
  * randomize kandidat
  * exclude item library bila user memilih exclude watched/read
  * mengurangi hasil yang terasa berulang
* [x] Gacha repository:

  * mengambil kandidat anime/manga
  * menyimpan preference terakhir
  * menyimpan history hasil gacha
* [x] Gacha session/deck behavior:

  * reroll memakai pool kandidat
  * menghindari item sama muncul terlalu cepat
  * reset deck saat preferensi berubah atau pool habis
* [x] Overlay/feedback animasi gacha:

  * menampilkan proses gacha sebelum hasil final
  * menyediakan skip animation

### Offline & Cache

* [x] Cache anime detail ke SQLDelight
* [x] Cache manga detail ke SQLDelight
* [x] Cache relation preview ke SQLDelight
* [x] Cache media page/list ke SQLDelight
* [x] Cache search result ke SQLDelight
* [x] Pull-to-refresh di Home/Discover
* [x] Pull-to-refresh di AnimeList
* [x] Pull-to-refresh di MangaList
* [x] Pull-to-refresh di Search
* [x] Pull-to-refresh di MyLibrary
* [x] Pull-to-refresh di AnimeDetail
* [x] Pull-to-refresh di MangaDetail
* [x] Refresh state:

  * idle
  * refreshing
  * success
  * failed but cache/content available
* [x] Stale-while-revalidate agar konten lama tetap tampil saat refresh
* [x] Force refresh untuk detail screen
* [x] Network mode setting:

  * online/cache behavior
  * cache fallback
  * data/offline control dari Settings

### AI Assistant & Notes

* [x] Integrasi Gemini API
* [x] AI token/API key disimpan lewat Settings aplikasi
* [x] Model AI dapat dipilih dari Settings
* [x] Personality AI dapat dipilih dari Settings
* [x] AIAssistantScreen:

  * chat assistant berbasis Gemini
  * konteks aplikasi untuk anime, manga, library, gacha, dan notes
  * riwayat chat tersimpan lokal
  * session chat persisten
* [x] AI tools untuk notes:

  * summarize
  * improve writing
  * generate ideas
  * suggest title
* [x] Media card dari respons AI dapat diarahkan ke detail anime/manga
* [x] Token usage AI dicatat untuk monitoring di Settings

### Settings & About

* [x] SettingsScreen:

  * Appearance
  * Data & Offline
  * AI API
  * Request Usage
  * About
* [x] Theme mode:

  * system
  * light
  * dark
* [x] Color scheme:

  * Default
  * Catppuccin
  * Gruvbox
  * Hatsune Miku
  * Pak Habib
  * Code Geass
* [x] Request usage indicator:

  * Jikan request budget
  * AI token usage
* [x] About section:

  * app info
  * source data
  * developer info
  * build/runtime metadata
  * release check
  * logo aplikasi

---

## Sprint 4 — Polish & Testing

**Referensi:** [Materi 14 — Project Sprint 4: Polish & Testing](https://kuliah2.itera.ac.id/pluginfile.php/78218/mod_resource/content/1/Materi_14_ProjectSprint4_Polish_Testing.pdf)

Fokus Sprint 4: stabilitas, konsistensi UI, edge case, performa, dan test.

### Bug Fixing

* [x] Fix duplicate/single `HttpClient` binding di network module
* [x] Fix compile error pada animated content/detail section
* [x] Fix blink putih awal navigasi dengan root surface/background
* [x] Fix search cache decode agar cache lama tidak menyebabkan error saat dibaca
* [x] Fix network mode dan cache policy agar repository dapat memilih cache/remote sesuai setting
* [x] Fix AI chat session compile/database issues setelah fitur persistent session
* [x] Fix build info generation agar kompatibel dengan configuration cache dan CI
* [x] Fix Android package/application id rename ke `id.my.sinanonym.mybawanggacha`
* [x] Fix Compose resource packaging dan launcher/About logo asset setelah rename package
* [x] Fix Android keyboard inset pada AI chat input agar tidak tertutup keyboard
* [x] Fix SQLDelight episode progress migration/upsert untuk watched/bookmarked episode

### UI Polish

* [x] Empty state untuk:

  * library kosong
  * search no result
  * cache kosong saat offline
* [x] Error state yang ramah dengan retry button
* [x] Loading state/skeleton untuk screen yang mengambil API
* [x] Pull-refresh tidak mengosongkan konten lama
* [x] Animasi ringan:

  * section transition di AnimeDetail
  * section transition di MangaDetail
  * pull-refresh indicator
  * button/card feedback
  * gacha animation overlay
* [x] Refactor Settings menjadi submenu yang lebih rapi dan scrollable
* [x] Tambahkan color scheme picker horizontal dan palette cards
* [x] Polish AI Assistant:

  * markdown rendering
  * model dropdown di header
  * session chat persisten
  * media card dari respons AI
  * action reset/detail menu lebih compact
* [x] Polish About screen:

  * build/runtime metadata
  * developer info/email
  * launcher logo hero
* [x] Polish episode list anime:

  * swipe untuk toggle watched
  * swipe untuk bookmark episode
  * overlay aksi swipe
  * episode watched dibuat lebih redup
* [x] Anime/manga card grid dibuat responsif dengan minimal 2 kolom di layar kecil
* [x] Poster pada detail dapat ditekan untuk diperbesar
* [x] Feedback copy title ke clipboard

### Testing

* [x] Mapper tests untuk Jikan anime/manga dasar
* [x] Service tests untuk endpoint Jikan dasar
* [x] Repository/search tests dasar
* [x] Gacha filter/random logic tests
* [x] Cache policy dan network mode tests
* [x] Build info generation checks untuk CI
* [x] Host-safe KMP tests agar CI Android/common tetap stabil

### Performance

* [x] Gunakan key di LazyColumn/LazyVerticalGrid
* [x] Tambahkan `contentType` untuk item list/grid yang sejenis
* [x] Hindari request API berlebihan:

  * Jikan rate limiter
  * cache detail/list/search
  * debounce search
* [x] Pastikan search memakai debounce
* [x] Pastikan detail screen tidak terasa jank:

  * animated section transition
  * pull-refresh tanpa blank state
  * stale-while-revalidate
* [x] Kurangi blink putih saat navigasi dengan root background stabil
* [x] Search components dan screen components distandarkan
* [x] Gacha filter UI dibuat lebih ringkas
* [x] Request usage/Jikan budget indicator ditambahkan

---

## Sprint 5 — Finalization

**Referensi:** [Materi 15 — Project Sprint 5: Final Preparation](https://kuliah2.itera.ac.id/pluginfile.php/78229/mod_resource/content/1/Materi_15_ProjectSprint5_Final_Preparation.pdf)

Fokus Sprint 5: finalisasi dokumentasi, release workflow, dan kesiapan final project.

### Documentation

* [x] README diperbarui sebagai dokumentasi project
* [x] VERSIONING diperbarui sesuai workflow release
* [x] Dokumentasi arsitektur dibuat
* [x] Dokumentasi struktur kode dibuat
* [x] Dokumentasi cara menjalankan dibuat
* [x] Dokumentasi fitur dibuat
* [x] Dokumentasi database dibuat
* [x] Dokumentasi API dan cache dibuat
* [x] Dokumentasi gacha dibuat
* [x] Dokumentasi AI assistant dibuat
* [x] Dokumentasi settings/about dibuat
* [x] Dokumentasi testing dibuat
* [x] Dokumentasi CI/release dibuat
* [x] Dokumentasi troubleshooting dibuat
* [x] Dokumentasi kode dibuat di `docs/code`

### CI & Release

* [x] GitHub Actions CI tersedia
* [x] Android build workflow tersedia
* [x] Shared Kotlin checks workflow tersedia
* [x] iOS simulator compile workflow tersedia sebagai optional job
* [x] Release dry build workflow tersedia
* [x] Signed Android release APK workflow tersedia
* [x] ABI split APK disiapkan:

  * `arm64-v8a`
  * `armeabi-v7a`
  * `x86_64`
  * universal
* [x] SHA256SUMS untuk release artifact disiapkan
* [x] GitHub Secrets untuk Android signing didokumentasikan
* [x] Versioning/build metadata disiapkan di About screen

