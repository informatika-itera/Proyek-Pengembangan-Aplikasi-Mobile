# My Bawang Gacha

<p align="center">
  <a href="https://youtu.be/UAeIq0xMsbk">
    <img src="https://img.youtube.com/vi/UAeIq0xMsbk/maxresdefault.jpg" alt="Video Demo My Bawang Gacha" width="70%">
    <br>
    <b>[PLAY] Putar Video Demo (YouTube)</b>
  </a>
</p>

My Bawang Gacha adalah aplikasi Anime List, Manga List, dan Gacha yang dikembangkan menggunakan Kotlin Multiplatform (KMP) untuk platform Android dan iOS. Proyek ini dibangun di atas template Clean Architecture dan MVVM dengan integrasi Google Gemini API dan Jikan API (MyAnimeList).

![CI](https://github.com/sinavarasina/Proyek-Pengembangan-Aplikasi-Mobile/actions/workflows/KMP-ci.yml/badge.svg)

## Fitur Utama

- Discover & Search: Menampilkan anime dan manga populer, rekomendasi musiman, serta melakukan pencarian media menggunakan Jikan API dengan optimasi debounce query.
- Library Management: Menyimpan dan melacak status tontonan/bacaan (Plan to Watch, Watching, Completed, On Hold, Dropped) lengkap dengan jumlah episode/chapter, skor personal (1-10), dan catatan khusus.
- Gacha System: Memberikan rekomendasi anime atau manga secara acak (gacha) berdasarkan preferensi media yang dimasukkan pengguna.
- Note System: Membuat, membaca, memperbarui, dan menghapus catatan personal secara lokal.
- AI Assistant: Integrasi dengan Google Gemini API untuk melakukan tanya jawab interaktif dengan riwayat percakapan yang disimpan di database lokal.
- Dashboard & Settings: Konfigurasi API key Gemini, pergantian tema (Terang, Gelap, Sistem) dan skema warna (Default, Catppuccin, Gruvbox, Hatsune Miku, Pak Habib, Code Geass), serta pemantauan kuota request Jikan API dan penggunaan token AI.

## Persiapan dan Instalasi

### Kebutuhan Sistem

- Android Studio Ladybug (2024.2.1) atau versi lebih baru
- JDK 17 atau versi lebih baru
- Xcode 15 atau versi lebih baru (khusus untuk build target iOS di macOS)

### Cara Menjalankan

1. Clone repository ini ke direktori lokal Anda.
2. Salin template konfigurasi properti lokal:
   ```bash
   cp local.properties.example local.properties
   ```
3. Lakukan sync Gradle di Android Studio.
4. Jalankan perintah build melalui terminal:
   ```bash
   ./gradlew build
   ```
5. Untuk menjalankan aplikasi di emulator atau perangkat Android yang terhubung:
   ```bash
   ./gradlew :androidApp:installDebug
   ```

## Arsitektur dan Struktur Proyek

Aplikasi ini menerapkan prinsip Clean Architecture dengan pemisahan menjadi tiga layer utama: Data, Domain, dan Presentation.

### Modul Utama
- `androidApp`: Entry point dan konfigurasi spesifik untuk aplikasi Android.
- `iosApp`: Entry point dan konfigurasi proyek Xcode untuk aplikasi iOS.
- `composeApp`: Shared module berisi modul bersama (95%+ dari basis kode), termasuk logika bisnis, penanganan data, dan UI (Compose Multiplatform).

### Tech Stack
- UI Framework: Compose Multiplatform
- Jaringan: Ktor Client
- Database Lokal: SQLDelight
- Dependency Injection: Koin
- Penyimpanan Preferensi: DataStore (Preferences)
- Image Loading: Coil Compose
- Integrasi AI: Google Gemini API

Untuk penjelasan lebih detail mengenai struktur paket, kelas, dan alur dependensi, silakan merujuk ke [Struktur Kode](docs/STRUKTUR_KODE.md).

## Dokumentasi Tambahan

Panduan lengkap proyek ini dibagi ke dalam beberapa dokumen di direktori `docs/`:
- [Indeks Dokumentasi](docs/INDEX.md): Panduan navigasi seluruh file dokumentasi yang tersedia.
- [Panduan Proyek](docs/PANDUAN_PROJECT.md): Ketentuan sprint, rubrik penilaian, dan timeline pengembangan.
- [Struktur Kode](docs/STRUKTUR_KODE.md): Penjelasan rinci tentang arsitektur Clean Architecture + MVVM, modul, dan alur data.
- [Cara Menjalankan](docs/CARA_MENJALANKAN.md): Langkah detail instalasi, setup emulator, dan kompilasi proyek.
- [Aturan Modifikasi](docs/ATURAN_MODIFIKASI.md): Regulasi memodifikasi berkas template.
- [Git Workflow](docs/GIT_WORKFLOW.md): Panduan branching, penamaan branch, dan konvensi commit.
- [Troubleshooting](docs/TROUBLESHOOTING.md): Solusi untuk berbagai masalah kompilasi dan runtime yang umum terjadi.

## Dosen Pengampu
<table>
  <tr>
    <td align="center">
      <a href="https://github.com/mh4Scripts">
        <img src="https://github.com/mh4Scripts.png" width="100px;" alt="Pak Habib" style="border-radius:50%;"/><br />
        <sub><b>Pak Habib</b></sub>
      </a><br />
      <sub>mh4Scripts</sub>
    </td>
  </tr>
</table>

## Anggota Kelompok
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

**Program Studi Teknik Informatika**  
Institut Teknologi Sumatera (ITERA)

## Lisensi

MIT License



