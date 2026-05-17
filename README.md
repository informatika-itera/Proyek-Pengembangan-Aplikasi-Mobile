# Pusaka Kata: Petualangan Kosakata & Mitologi Nusantara

![KMP CI](https://github.com/MuharyanSyaifullah/Proyek-Pengembangan-Aplikasi-Mobile/actions/workflows/ci.yml/badge.svg)

**Pusaka Kata** adalah aplikasi edukasi interaktif berbasis **Kotlin Multiplatform (KMP)** yang dirancang untuk memperkaya penguasaan kosakata baku, peribahasa, dan kesusastraan Indonesia bagi pengguna Android dan iOS.

---

## 👥 Tim Pengembang
* **Muharyan Syaifullah** (123140045) - Lead Developer & Architecture
* **Eka Putri Azhari Ritonga** (123140028) - UI/UX Designer & Developer

---

## ✨ Fitur Utama
1. 🧠 **Smart Flashcard (SRS):** Hafalan kosakata dengan algoritma pengulangan adaptif.
2. 📸 **OCR Scanner:** Ekstraksi kosakata langsung dari buku menggunakan kamera (Google ML Kit & Apple Vision).
3. 🎲 **Gacha Reward:** Koleksi kartu karakter mitologi Nusantara (Tuyul, Barong, Garuda) setiap selesai belajar.
4. 📚 **Galeri Nusantara:** Ensiklopedia digital karakter mitologi yang telah dikoleksi.

---

## 🛠️ Tech Stack & Arsitektur
Aplikasi ini menggunakan standar **Modern Android & iOS Development**:
* **Language:** Kotlin
* **Framework:** Compose Multiplatform (UI Lintas Platform)
* **Architecture:** Clean Architecture (Domain, Data, UI)
* **DI:** Koin (Dependency Injection) - *Bonus 10% Implemented*
* **Local DB:** SQLDelight / Room KMP
* **Concurrency:** Kotlin Coroutines & Flow
* **CI/CD:** GitHub Actions

---

## 📂 Struktur Proyek (Clean Architecture)
```text
composeApp/src/commonMain/kotlin/id/pusakakata/
├── data/           # Repository implementations & Data Sources
├── domain/         # Business Logic (Model, UseCase, Repository Interface)
├── di/             # Dependency Injection (Koin Modules)
└── ui/             # Presentation layer (Compose UI)
```

---

## 📅 Rencana Proyek (Project Plan)

| Sprint | Fokus | Target Fitur |
| :--- | :--- | :--- |
| **Sprint 1** | Setup | Initial KMP Setup, Clean Architecture, CI/CD, Koin DI |
| **Sprint 2** | Database | SQLDelight Setup, Word Entity, Collection Entity |
| **Sprint 3** | Core Logic | Algoritma SRS & Weighted RNG Gacha |
| **Sprint 4** | UI/UX | Home Screen & Flashcard UI |
| **Sprint 5** | OCR & Camera | Integrasi ML Kit (Android) & Apple Vision (iOS) |
| **Sprint 6** | Finalization | Polishing, Bug Fixing, & Documentation |

---

## 🚀 Cara Menjalankan
1. Clone repository.
2. Buka di Android Studio (Koala/Ladybug terbaru).
3. Pastikan sudah ada JDK 17.
4. Run `composeApp` di emulator Android atau simulator iOS.
