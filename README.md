# CoffeSpace: Live Seat & Ambience Tracker ☕️📍

**CoffeSpace** adalah aplikasi mobile inovatif yang dibangun menggunakan **Kotlin Multiplatform (KMP)** dan **Compose Multiplatform**. Aplikasi ini dirancang untuk memecahkan masalah klasik mahasiswa teknik: mencari tempat ngerjain tugas (kafe) yang pasti ada kursinya, suasananya kondusif, dan fasilitasnya lengkap.

Aplikasi ini merupakan proyek **Tugas Besar Mata Kuliah Pengembangan Aplikasi Mobile** di Program Studi Teknik Informatika, **Institut Teknologi Sumatera (ITERA)**.

---

## 🚀 Overview
Dalam ekosistem mahasiswa yang dinamis di Bandar Lampung, kafe bukan sekadar tempat minum kopi, melainkan ruang kerja ketiga (*third space*). **CoffeSpace** hadir untuk memberikan data real-time mengenai kepadatan dan suasana kafe, sehingga produktivitas tetap terjaga tanpa harus "hunting" tempat secara manual.

---

## ✨ Fitur Utama (Scope Project)

### 1. Real-time Seat Monitoring
* Menampilkan status okupansi meja (Kosong, Tersedia, Penuh).
* Integrasi database real-time untuk pembaruan status tanpa refresh.

### 2. Ambience Noise Detection
* Memberikan label pada kafe berdasarkan tingkat kebisingan: **Quiet** (fokus), **Moderate** (santai), atau **Loud** (diskusi).

### 3. Productivity Filters
* Filter berdasarkan ketersediaan **Stopkontak** (Power Outlets).
* Informasi kecepatan **Wi-Fi** (Speed Test results).
* Penanda lokasi ramah kantong mahasiswa (Range harga menu).

### 4. Location-Based Service (Maps)
* Integrasi peta untuk menemukan kafe terdekat dari posisi pengguna saat ini.

---

## 🏗 Arsitektur & Teknologi

Proyek ini menerapkan **Clean Architecture** untuk memastikan kode mudah diuji dan dikelola:
* **Presentation Layer:** Jetpack Compose (Android) & Compose Multiplatform (iOS/Shared).
* **Domain Layer:** Business logic, Use Cases, & Entities (Shared Module).
* **Data Layer:** Repository pattern, Ktor (API), & SQLDelight (Local DB).

**Tech Stack:**
- **Language:** Kotlin
- **UI Framework:** Compose Multiplatform
- **Dependency Injection:** Koin
- **Networking:** Ktor
- **Concurrency:** Kotlin Coroutines & Flow

---

## 📈 Roadmap Sprint (Timeline)

Sesuai dengan kurikulum Pertemuan 11 Pengembangan Aplikasi Mobile ITERA:
* **Sprint 1 (Planning):** Architecture design, UI Mockup, & Repository setup.
* **Sprint 2 (Core):** Authentication, Cafe Listing, & Google Maps integration.
* **Sprint 3 (Advanced):** Real-time Seat tracking & Noise Indicator logic.
* **Sprint 4 (Testing):** UI/UX Polishing, Bug fixing, & Unit Testing.
* **Sprint 5 (Final):** Deployment & Presentation Preparation.

---

## 👥 Tim Pengembang
* **Andika Rahman Pratama** (123140090) - [Leader
* **Muhammad Farhan Muzakhi** (123140075) - [Coder]

---

## 🛠 Cara Menjalankan
1. Clone repository: `git clone https://github.com/[username]/coffespace.git`
2. Buka di Android Studio (Versi Ladybug atau terbaru).
3. Jalankan `:composeApp` pada perangkat Android atau simulator iOS.

---
© 2026 - Teknik Informatika ITERA
