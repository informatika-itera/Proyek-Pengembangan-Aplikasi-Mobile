# 📱 ARCANE
Asisten perpustakaan digital berbasis KMP yang mengintegrasikan Google Books API dan AI Gemini untuk manajemen literatur cerdas serta analisis riset otomatis.

![Build Status](https://github.com/genhaa/Proyek-Pengembangan-Aplikasi-Mobile.git)
![Kotlin Version](https://img.shields.io/badge/Kotlin-2.0.0-blue?logo=kotlin)
![Compose Version](https://img.shields.io/badge/Compose-Multiplatform-orange)

---

## 👥 Informasi Tim
Proyek ini dikembangkan oleh **B Square** sebagai bagian dari tugas mata kuliah Pengembangan Aplikasi Mobile.

| Nama | NIM | GitHub | Role |
| :--- | :--- | :--- | :--- |
| [Memory Simanjuntak] | [123140095] | https://github.com/13-095-memory | Lead Developer / UI Design |
| [Nama Anggota 2] | [NIM 2] | https://github.com/genhaa | Backend & Data |


---

## 🚀 Fitur Aplikasi (Sprint 1 Planning)

### Fitur Utama (Minimum Requirements):
- [ ] **Modern UI/UX:** Minimal 5 layar menggunakan Material Design 3.
- [ ] **Data Management:** Integrasi REST API menggunakan Ktor atau Database Lokal SQLDelight.
- [ ] **State Management:** Implementasi StateFlow untuk reaktivitas UI.
- [ ] **Navigation:** Navigasi antar layar yang aman (Safe Args).

### Fitur Tambahan (Bonus):
- [ ] Support iOS Platform.
- [ ] Dark Mode Support.
- [ ] [Tulis fitur unikmu di sini, misal: Integrasi Google Maps/AI].

---

## 🏗️ Arsitektur & Tech Stack
Aplikasi ini dibangun menggunakan **Clean Architecture** dan pola **MVVM (Model-View-ViewModel)** untuk memastikan kode yang mudah diuji dan dipelihara.

- **Multiplatform:** Kotlin Multiplatform (KMP)
- **UI Framework:** Compose Multiplatform
- **Dependency Injection:** Koin (Target Bonus +10%)
- **Networking:** Ktor
- **Local DB:** SQLDelight (jika pakai DB)
- **Testing:** Kotlin Test & Mockative

---

## 🛠️ Project Setup & CI/CD
Proyek ini sudah dilengkapi dengan **GitHub Actions** untuk Continuous Integration (CI). Setiap perubahan kode (push/pull request) akan melewati proses:
1. **Lint Check:** Memastikan standar penulisan kode.
2. **Build Test:** Memastikan aplikasi dapat dicompile dengan sukses.
3. **Unit Testing:** Menjalankan test otomatis sebelum masuk ke branch utama.

---

## 📈 Rencana Pengerjaan (Project Plan)
- **Sprint 1:** Project Setup, Architecture Setup, & UI Mockup.
- **Sprint 2:** Core Features & API Integration.
- **Sprint 3:** Advanced Features & State Management.
- **Sprint 4:** Testing, Bug Fixing, & Polishing.
- **Sprint 5:** Final fixes, demo preparation.
