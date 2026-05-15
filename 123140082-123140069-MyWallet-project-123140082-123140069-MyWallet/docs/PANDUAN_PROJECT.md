# 📚 Panduan Project - Pengembangan Aplikasi Mobile

## Informasi Mata Kuliah

| Item | Detail |
|------|--------|
| **Mata Kuliah** | Pengembangan Aplikasi Mobile |
| **Kode** | IF25-22017 |
| **Program Studi** | Teknik Informatika |
| **Institusi** | Institut Teknologi Sumatera (ITERA) |
| **Template** | NoteAI - KMP Project Template |

---

## 🎯 Tujuan Project

Project ini bertujuan untuk:

1. **Memahami arsitektur aplikasi mobile modern** - Clean Architecture + MVVM
2. **Menguasai Kotlin Multiplatform** - Satu codebase untuk Android & iOS
3. **Mengimplementasikan best practices** - DI, Repository Pattern, Use Cases
4. **Mengintegrasikan AI** - Penggunaan API Gemini untuk fitur cerdas
5. **Bekerja dengan Git** - Version control dan kolaborasi tim

---

## 👥 Ketentuan Kelompok

| Ketentuan | Detail |
|-----------|--------|
| **Jumlah Anggota** | 1 - 3 mahasiswa per kelompok |
| **Pembentukan** | Mahasiswa bebas memilih anggota kelompok |
| **Peran** | Setiap anggota harus berkontribusi (terlihat di Git commits) |
| **Penilaian** | Nilai kelompok sama, kecuali ada bukti kontribusi tidak merata |

### Pembagian Tugas yang Disarankan

**Untuk Kelompok 2 Orang:**
| Anggota | Tanggung Jawab |
|---------|----------------|
| Anggota 1 | Domain + Data layer, Database, API |
| Anggota 2 | Presentation layer, UI/UX, Testing |

**Untuk Kelompok 3 Orang:**
| Anggota | Tanggung Jawab |
|---------|----------------|
| Anggota 1 | Domain layer, Use Cases, Business Logic |
| Anggota 2 | Data layer, Database, API Integration |
| Anggota 3 | Presentation layer, UI/UX, Navigation |

### ⚠️ Penting!
- **SETIAP anggota WAJIB** memiliki commit di repository
- Commit harus **meaningful** (bukan hanya edit spasi/komentar)
- Jika ada anggota yang tidak berkontribusi, laporkan ke dosen
- Git history akan diperiksa untuk verifikasi kontribusi

---

## 📋 Persyaratan

### Software yang Dibutuhkan

| Software | Versi Minimum | Download |
|----------|---------------|----------|
| Android Studio | Ladybug 2024.2.1 | [Download](https://developer.android.com/studio) |
| JDK | 17 | Termasuk di Android Studio |
| Git | 2.30+ | [Download](https://git-scm.com/) |
| Xcode (Mac only) | 15+ | App Store |

### Akun yang Dibutuhkan

1. **GitHub Account** - Untuk menyimpan kode project
2. **Google AI Studio** - Untuk mendapatkan API key Gemini (gratis)
   - Daftar di: https://aistudio.google.com/

---

## 🚀 Langkah Memulai Project

### Step 1: Fork Repository (1 Orang per Kelompok)

> **📝 Untuk Kelompok:** Cukup **1 orang saja** yang fork. Anggota lain akan di-invite sebagai collaborator.

1. Buka repository template di GitHub
2. Klik tombol **Fork** di kanan atas
3. Pilih akun GitHub Anda sebagai destination
4. Tunggu proses fork selesai

**Untuk Kelompok - Invite Anggota Lain:**
1. Buka repository yang sudah di-fork
2. Pergi ke **Settings > Collaborators > Add people**
3. Masukkan username GitHub anggota kelompok
4. Anggota lain akan menerima invitation via email

### Step 2: Clone Repository (Semua Anggota)

```bash
# Clone repository yang sudah di-fork (SEMUA anggota kelompok)
git clone git@github.com:informatika-itera/Proyek-Pengembangan-Aplikasi-Mobile.git

# Masuk ke folder project
cd Proyek-Pengembangan-Aplikasi-Mobile
```

### Step 3: Buat Branch Project Kelompok

**PENTING**: Jangan langsung bekerja di branch `main`!

```bash
# Format: project/[NIM-NIM-...]-[NamaAplikasi]

# Contoh Individu (1 orang):
git checkout -b project/121140001-TodoMaster

# Contoh Kelompok 2 orang:
git checkout -b project/121140003-121140004-FitnessApp

# Contoh Kelompok 3 orang:
git checkout -b project/121140007-121140008-121140009-StudyPlanner
```

> **📝 Note:** NIM diurutkan dari kecil ke besar, dipisahkan dengan dash (-)

### Step 4: Setup API Key

```bash
# Copy file template
cp local.properties.example local.properties

# Edit file local.properties
# Tambahkan API key Gemini Anda
```

Isi `local.properties`:
```properties
sdk.dir=/path/to/android/sdk
GEMINI_API_KEY=your_api_key_here
```

### Step 5: Sync & Build

1. Buka project di Android Studio
2. Tunggu Gradle sync selesai
3. Build project: `Build > Make Project`
4. Run di emulator atau device

---

## 📅 Timeline Project (Sprint-based)

| Minggu | Sprint | Deliverables | Bobot |
|--------|--------|--------------|-------|
| 11 | Sprint 1: Foundation | Setup repo, Clean Architecture, DI, CI | 5% |
| 12 | Sprint 2: Core Features | 3+ screens, navigation, CRUD, local storage | 5% |
| 13 | Sprint 3: Advanced | Search, API/enhanced, offline, bonus feature | 5% |
| 14 | Sprint 4: Polish | Bug fixes, UI polish, 10+ tests, coverage | 5% |
| 15 | Sprint 5: Final | Demo ready, slides, APK, README | 5% |
| 16 | UAS | Live demo, Q&A, presentation | 35% |

**Total Bobot Project: 60%**

---

## 📝 Deliverables Setiap Sprint

### Sprint 1: Foundation (Minggu 11)
- [ ] Repository GitHub sudah di-fork dan branch dibuat
- [ ] Project bisa di-build tanpa error
- [ ] Memahami struktur folder Clean Architecture
- [ ] Koin DI sudah berjalan
- [ ] Minimal 1 commit meaningful

### Sprint 2: Core Features (Minggu 12)
- [ ] Minimal 3 screens sudah diimplementasi
- [ ] Navigation antar screen berfungsi
- [ ] CRUD operation berjalan
- [ ] Data tersimpan di local storage (SQLDelight)
- [ ] Minimal 5 commits

### Sprint 3: Advanced Features (Minggu 13)
- [ ] Search dengan debounce
- [ ] Filter dan sort data
- [ ] Integrasi API atau enhanced local features
- [ ] Offline support (cache-first)
- [ ] 1+ bonus feature

### Sprint 4: Polish & Testing (Minggu 14)
- [ ] Semua bugs fixed
- [ ] UI konsisten dan polished
- [ ] 10+ unit tests
- [ ] 3+ UI tests
- [ ] 50%+ code coverage

### Sprint 5: Final Preparation (Minggu 15)
- [ ] Aplikasi stabil tanpa crash
- [ ] Slide presentasi siap
- [ ] Demo script disiapkan
- [ ] Release APK dibuat
- [ ] README lengkap
- [ ] Video backup demo

---

## 📊 Rubrik Penilaian

### Sprint 1-5 (masing-masing 5%)

| Kriteria | Bobot | Deskripsi |
|----------|-------|-----------|
| Completeness | 40% | Semua deliverables terpenuhi |
| Code Quality | 30% | Clean code, proper naming, comments |
| Git Usage | 20% | Commit messages, branching |
| Timeliness | 10% | Dikumpulkan tepat waktu |

### UAS Demo Day (35%)

| Kriteria | Bobot | Deskripsi |
|----------|-------|-----------|
| App Functionality | 30% | Fitur berjalan dengan baik |
| Code Quality | 20% | Arsitektur, best practices |
| Demo & Presentation | 25% | Kemampuan presentasi |
| Technical Depth | 15% | Pemahaman teknis |
| Q&A | 10% | Menjawab pertanyaan |

---

## 💡 Tips Sukses

### Do's ✅
- Commit secara teratur (minimal 1x per hari saat aktif coding)
- Tulis commit message yang deskriptif
- Test aplikasi di berbagai ukuran layar
- Backup code secara berkala (push ke GitHub)
- Tanya jika ada yang tidak dipahami
- Mulai dari yang sederhana, tambahkan fitur bertahap

### Don'ts ❌
- Jangan copy-paste tanpa memahami
- Jangan tunggu deadline untuk mulai
- Jangan abaikan error/warning
- Jangan commit file sensitif (API keys, passwords)
- Jangan plagiat dari teman

---

## 🆘 Bantuan & Resources

### Dokumentasi Resmi
- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Koin DI](https://insert-koin.io/docs/quickstart/kotlin)
- [SQLDelight](https://cashapp.github.io/sqldelight/)

### Troubleshooting
- Lihat file `docs/TROUBLESHOOTING.md`
- Buka Issue di repository template
- Tanya di grup kelas

### Kontak Dosen
- Konsultasi: Sesuai jadwal yang ditentukan
- Email: [sesuai kebijakan dosen]

---

## 📎 File Dokumentasi Lainnya

| File | Deskripsi |
|------|-----------|
| [GIT_WORKFLOW.md](./GIT_WORKFLOW.md) | Panduan Git branching dan workflow |
| [ATURAN_MODIFIKASI.md](./ATURAN_MODIFIKASI.md) | Aturan modifikasi template |
| [STRUKTUR_KODE.md](./STRUKTUR_KODE.md) | Penjelasan struktur kode |
| [TROUBLESHOOTING.md](./TROUBLESHOOTING.md) | Solusi masalah umum |

---

*Dokumen ini adalah bagian dari template project Pengembangan Aplikasi Mobile - ITERA*
