# EduMate - KMP Project

Aplikasi **EduMate** adalah asisten belajar cerdas berbasis kecerdasan buatan yang dirancang khusus untuk membantu mahasiswa mengelola tugas kuliah dan merangkum materi pelajaran. Aplikasi ini dikembangkan menggunakan **Kotlin Multiplatform (KMP)** dengan menerapkan *Clean Architecture* dan pola MVVM.

EduMate dibuat sebagai pemenuhan Tugas Mata Kuliah **Pengembangan Aplikasi Mobile** di Institut Teknologi Sumatera (ITERA).

---

## Tim Pengembang

| Nama | NIM |
|------|-----|
| Muhammad Ghama Al Fajri | 123140182 |
| Abi Sholihan | 123140192 |


---

## Fitur Utama

- **Manajemen Tugas Cerdas (Smart Task List)** - Buat, edit, dan hapus tugas lengkap dengan tenggat waktu (*deadline*) dan tingkat prioritas.
- **AI Task Breakdown** - Merasa tugas terlalu berat? Gunakan bantuan AI Gemini untuk memecah tugas besar menjadi langkah-langkah (*sub-tasks*) yang lebih kecil dan mudah dieksekusi.
- **Catatan Materi (Study Notes)** - Simpan salinan materi kuliah atau jurnal Anda secara luring (*offline*).
- **AI Summarizer** - Merangkum teks materi kuliah yang sangat panjang menjadi 3-5 poin penting secara otomatis menggunakan *prompt* khusus dari Gemini.
- **Pencarian & Filter Canggih** - Cari tugas dengan *debounce search*, *filter* status (Selesai/Belum Selesai), dan urutkan (*sort*) tugas berdasarkan tenggat waktu paling dekat.
- **Cross-Platform** - Dibangun dengan satu basis kode (Kotlin) untuk berjalan di Android dan iOS.

---

## Arsitektur & Teknologi

Proyek ini sangat patuh terhadap prinsip **Clean Architecture** dan pola **MVVM**.

### Tech Stack

| Layer | Technology |
|-------|------------|
| **UI** | Compose Multiplatform, Material 3 |
| **State** | StateFlow, ViewModel |
| **Navigation** | Compose Navigation (Type-safe) |
| **Networking** | Ktor Client |
| **Local DB** | SQLDelight |
| **Preferences** | DataStore |
| **Dependency Injection** | Koin |
| **AI Integration** | Google Gemini API |
| **Testing** | Kotlin Test, Turbine |