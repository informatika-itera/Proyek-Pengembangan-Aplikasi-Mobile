# Pantau Jompo 🛡️

## Team

- **Pradana Figo Ariansya** (Lead) — 123140063
- **Muhammad Piela Nugraha** (Dev) — 123140200

## Description

Pantau Jompo adalah aplikasi pemantauan kesehatan cerdas berbasis Android yang membantu pengguna melacak aktivitas fisik, mencatat asupan nutrisi menggunakan AI, serta mengakses berita kesehatan terkini — semuanya dalam satu platform terintegrasi.

## Features

### Minimum

- [x] Pelacakan aktivitas fisik real-time berbasis GPS (rute, jarak, durasi, kalori)
- [x] AI Nutrition Scanner — identifikasi makanan dan kandungan gizi via kamera
- [x] Manajemen riwayat kebugaran (CRUD: olahraga, gizi, keluhan fisik)
- [x] Portal berita kesehatan dengan News API integration
- [x] Dashboard profil dengan kalkulator BMI dan metrik konsistensi olahraga
### Bonus

- [ ] AI Summarizer — meringkas artikel berita kesehatan menjadi poin-poin utama
- [ ] AI Recommendation — rekomendasi target kalori harian yang dipersonalisasi
- [ ] Dark Mode (AMOLED-friendly)
- [ ] Grafik tren aktivitas mingguan/bulanan
## Tech Stack

| Layer | Teknologi |
|-------|-----------|
| Language | Kotlin |
| UI | XML Layouts + Material Design 3 |
| Database | Room (SQLite) |
| Async | Coroutines + Flow |
| Networking | Retrofit 2 + OkHttp |
| AI / Vision | Gemini API / ML Kit |
| Maps & GPS | Google Maps SDK + FusedLocationProvider |
| News | News API |
| DI | Hilt |
| Architecture | MVVM + Repository Pattern |

## Architecture

Menggunakan pola **MVVM (Model-View-ViewModel)** dengan **Repository Pattern** sesuai rekomendasi Google Android Architecture Guidelines.

```
UI Layer (Activity / Fragment)
        │ observes StateFlow / LiveData
        ▼
ViewModel Layer
        │ calls
        ▼
Repository Layer
    ┌───┴───┐
    ▼       ▼
Room DB   Retrofit
(local)   (remote: Gemini, News API, Maps)
```

## Setup

1. Clone repo
   ```bash
   git clone https://github.com/username/pantau-jompo.git
   ```

2. Buka di Android Studio (Hedgehog 2023.1.1+)
3. Tambahkan API keys di `local.properties`:
   ```properties
   GEMINI_API_KEY=your_key_here
   NEWS_API_KEY=your_key_here
   MAPS_API_KEY=your_key_here
   ```

4. Run di emulator atau device (min. API 26)