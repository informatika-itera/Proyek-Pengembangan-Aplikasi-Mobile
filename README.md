# KostHub

**KostHub** adalah aplikasi mobile multiplatform yang dirancang untuk membantu mahasiswa menemukan dan mengelola informasi indekos di sekitar kampus ITERA menggunakan algoritma klasterisasi untuk rekomendasi yang lebih baik.

## Tim

- **Nashrullah Fathul Qoriib** (122140162)
- **Havidz Ridho Pratama** (122140160)

## Fitur Minimum (Minimum Requirements)

Aplikasi ini memenuhi standar wajib proyek akhir:
- **UI/UX**: Minimal 5 layar responsif dengan Material Design 3.
- **Arsitektur**: Mengimplementasikan *Clean Architecture* (Presentation, Domain, Data) dan pola MVVM.
- **Data**: Integrasi REST API (Ktor) dan Database Lokal (SQLDelight) untuk operasi CRUD.
- **State**: Manajemen status UI yang tertata menggunakan `StateFlow` (Loading, Success, Error, Empty).
- **Testing**: Minimal 10 *unit tests* dan 3 *UI tests* dengan cakupan > 50%.

## Tech Stack

- **Framework**: Kotlin Multiplatform (KMP) & Compose Multiplatform.
- **Dependency Injection**: Koin.
- **Networking**: Ktor Client & Kotlinx Serialization.
- **Local Storage**: SQLDelight & DataStore Preferences.
- **Testing**: `kotlin.test`, MockK, Turbine, dan Compose Test.

## Timeline & Progress Per Sprint

### Sprint 1: Planning & Setup (Week 11)

*Fokus: Infrastruktur dan Arsitektur* 

- [x] Pembentukan tim dan persetujuan ide proyek KostHub.
- [x] Setup repositori GitHub dan konfigurasi kolaborator.
- [x] Inisialisasi proyek KMP dengan struktur folder *Clean Architecture*.
- [x] Konfigurasi GitHub Actions untuk CI (Build & Test passing).
- [x] Dokumentasi awal dan rencana kerja tim.

### Sprint 2: Core Features (Week 12)

*Fokus: UI Dasar dan Data Layer* 

- [ ] Setup navigasi antar layar menggunakan `NavHost` dan *argument passing*.
- [ ] Pembuatan *Repository pattern* dan database lokal SQLDelight.
- [ ] Implementasi fungsi CRUD dasar untuk data kos.
- [ ] Manajemen status UI (Loading, Success, Error) pada layar utama.

### Sprint 3: Advanced Features (Week 13)

*Fokus: Integrasi API dan Pencarian* 

- [ ] Integrasi REST API menggunakan Ktor Client untuk data kos publik.
- [ ] Implementasi fitur *Search* dan *Filter* kos berdasarkan harga/fasilitas.
- [ ] Dukungan *Offline Mode* agar data yang sudah di-cache tetap dapat diakses.
- [ ] Penambahan layar tambahan seperti *Settings* atau *User Profile*.
- [ ] Integrasi fitur bonus (misal: klasterisasi AI atau *Dark Mode*).

### Sprint 4: Polish & Testing (Week 14)

*Fokus: Stabilitas dan Kualitas Kode* 

- [ ] Perbaikan seluruh bug yang ditemukan dan penanganan *edge cases*.
- [ ] Polesan UI (konsistensi spasi 8dp, tipografi, dan Material 3).
- [ ] Penulisan 10+ *Unit Tests* untuk Repository dan ViewModel.
- [ ] Penulisan 3+ *UI Tests* untuk alur pengguna kritikal.
- [ ] Analisis performa dan pencapaian target *test coverage* > 50%.

### Sprint 5: Final Preparation (Week 15)

*Fokus: Build dan Kesiapan Demo Day* 

- [ ] Pembersihan kode akhir dan perbaikan bug minor.
- [ ] Pembuatan *Signed Release APK* untuk demonstrasi.
- [ ] Penyusunan slide presentasi (Masalah, Solusi, Arsitektur).
- [ ] Penulisan naskah demo dan latihan presentasi tim.
- [ ] Pembuatan video *backup* demonstrasi aplikasi.

## Setup Instructions

1. **Clone Repository**:
```bash
git clone https://github.com/ORG/REPO.git
cd KostHub

```

2. **Konfigurasi Lokal**:
Pastikan Anda memiliki JDK 17 yang terinstal.


```bash
cp local.properties.example local.properties

```

3. **Build & Test**:
Gunakan Gradle wrapper untuk memastikan konsistensi:
```bash
[cite_start]./gradlew test            # Menjalankan unit tests [cite: 1409]
[cite_start]./gradlew assembleDebug   # Membuat build debug untuk Android [cite: 2119]

```

4. **Cek Coverage** (Opsional):
```bash
[cite_start]./gradlew koverHtmlReport # Melihat laporan test coverage [cite: 1411]

```

## License

MIT License - Proyek ini dibuat untuk tujuan akademik di Program Studi Teknik Informatika, Institut Teknologi Sumatera (ITERA).
