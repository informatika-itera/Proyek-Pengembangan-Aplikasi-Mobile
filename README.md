# Pusaka Kata: Petualangan Kosakata & Mitologi Nusantara

![KMP CI](https://github.com/MuharyanSyaifullah/Proyek-Pengembangan-Aplikasi-Mobile/actions/workflows/ci.yml/badge.svg)

**Pusaka Kata** adalah aplikasi edukasi interaktif berbasis **Kotlin Multiplatform (KMP)** yang dirancang untuk memperkaya penguasaan kosakata baku, puitis, dan arkais Indonesia melalui bantuan AI dan gamifikasi mitologi Nusantara. (Updated by Eka)

---

## 👥 Tim Pengembang
* **Muharyan Syaifullah** (123140045) - Lead Developer & Architecture
* **Eka Putri Azhari Ritonga** (123140028) - UI/UX Designer & Developer

---

## ✨ Fitur Utama (Update Minggu 13)
1. 🧠 **Smart Flashcard (SRS):** Hafalan kosakata menggunakan **Algoritma SM-2 (SuperMemo-2)** yang menghitung jadwal review secara adaptif berdasarkan kualitas jawaban pengguna.
2. ✨ **Asisten AI (Gemini Flash 2.5):** Integrasi kecerdasan buatan untuk otomatisasi:
    - **Definisi Otomatis:** Memberikan makna kata yang akurat.
    - **Klasifikasi Kategori:** Mengelompokkan kata ke dalam *Umum, Sastra,* atau *Arkais*.
    - **Generator Kalimat:** Membuat contoh penggunaan kalimat yang puitis dan formal secara otomatis.
3. 🎲 **Sistem Gacha Kartu Pusaka:** Kumpulkan kartu karakter legendaris Nusantara (Gajah Mada, Nyi Roro Kidul, dll) menggunakan token yang didapat dari kuis.
4. 📝 **Kuis Pintar:** Tantangan menebak definisi kata untuk melatih ingatan dan mendapatkan reward token.
5. 📴 **Dukungan Offline:** Database lokal SQLDelight yang sudah terisi dengan **15 Kosakata Pusaka Awal** (Sasmita, Renjana, dll) agar aplikasi bisa langsung digunakan tanpa internet.

---

## 🛠️ Tech Stack & Arsitektur
Aplikasi ini menggunakan standar **Modern Android & iOS Development**:
* **Language:** Kotlin 2.0+
* **Framework:** Compose Multiplatform (UI Lintas Platform)
* **Architecture:** Clean Architecture (Domain, Data, UI)
* **DI:** Koin (Dependency Injection)
* **Networking:** Ktor Client 2.3.12 (with Content Negotiation & Logging)
* **Serialization:** Kotlinx Serialization 1.6.3
* **Local DB:** SQLDelight 2.0.2
* **Time:** Kotlinx Datetime
* **AI Engine:** Google Gemini AI (v1beta API)

---

## 📂 Struktur Proyek (Clean Architecture)
```text
composeApp/src/commonMain/kotlin/id/pusakakata/
├── core/           # Utility, Networking, & DI Base Setup
├── data/           # Repository implementations, Mappers, & Remote/Local Sources
├── domain/         # Business Logic (Model, Repository Interface)
├── di/             # Koin Dependency Injection Modules
└── ui/             # Presentation layer (Compose UI, ViewModels, Components)
```

---

## 📅 Progres Proyek (Week 13 Completion)

| Fitur | Status | Detail |
| :--- | :--- | :--- |
| **REST API & AI** | ✅ Selesai | Integrasi Gemini AI & Unofficial KBBI API |
| **Database Lokal** | ✅ Selesai | SQLDelight dengan Auto-populate 15 data awal |
| **SRS Algorithm** | ✅ Selesai | Implementasi Algoritma SM-2 fungsional |
| **Gamification** | ✅ Selesai | Sistem Gacha & Manajemen Token |
| **UI/UX Polish** | ✅ Selesai | Pop-up AI terpadu & Layar Detail Lengkap |

---

## 🧪 Pengujian (Testing)
Aplikasi ini memiliki cakupan pengujian yang luas untuk menjamin stabilitas:
- **Unit Tests (25+ test):** Mencakup semua ViewModel (`Home`, `AddEdit`, `Gacha`, `Quiz`, `Flashcard`, `Favorite`, `Profile`) dan logika Bisnis (`SRS`, `GachaSystem`).
- **UI Tests (3 test):** Menguji alur navigasi utama dan interaksi kritis pada layar Beranda, Favorit, dan Profil.

### Cara Menjalankan Test:
1. **Unit Test:**
   ```bash
   ./gradlew testDebugUnitTest
   ```
   Atau buka folder `commonTest` dan klik kanan -> *Run 'Tests in id.pusakakata'*.
2. **UI Test (Instrumentation):**
   Hubungkan perangkat Android/Emulator, lalu jalankan:
   ```bash
   ./gradlew connectedAndroidTest
   ```
3. **Coverage Report:**
   Gunakan plugin *Kover* atau *Jacoco* (jika terkonfigurasi) atau fitur *Run with Coverage* di Android Studio pada folder `commonTest`.

---

## 🚀 Cara Menjalankan
1. Clone repository ini.
2. Masukkan `GEMINI_API_KEY` Anda ke dalam file `local.properties`.
3. Buka di Android Studio (Ladybug atau terbaru).
4. Run `composeApp` di emulator Android atau simulator iOS.
