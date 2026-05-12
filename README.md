# FinTrack - Smart Personal Finance Tracker 🚀

**FinTrack** adalah aplikasi manajemen keuangan personal berbasis mobile yang dirancang untuk membantu pengguna mengelola finansial secara lebih cerdas, modern, dan terukur. Dengan integrasi data kurs *real-time* dan teknologi **Artificial Intelligence (AI)**, FinTrack bukan sekadar buku kas digital, melainkan asisten finansial pribadi dalam genggaman.

---

## 📌 Deskripsi Proyek
Dalam era digital, fluktuasi nilai mata uang dan aset digital terjadi sangat cepat. **FinTrack** hadir untuk menjembatani kebutuhan pencatatan harian dengan kondisi ekonomi global secara *real-time*. Proyek ini dikembangkan untuk mata kuliah **Pengembangan Aplikasi Mobile** dengan fokus pada integrasi API eksternal dan pengolahan data berbasis kecerdasan buatan.

---

## ✨ Fitur Utama

*   **📊 Multi-Asset Dashboard:** Pantau saldo sekaligus pergerakan kurs mata uang asing (USD, EUR, JPY) dan aset kripto (BTC, ETH) secara langsung.
*   **💸 Smart Expense Logger:** Catat pengeluaran harian dengan konversi otomatis ke IDR menggunakan kurs terbaru.
*   **🤖 AI Financial Assistant (Gemini AI):** Fitur konsultasi interaktif untuk analisis kebiasaan belanja dan tips hemat yang dipersonalisasi.
*   **📈 Visualisasi Data:** Grafik pengeluaran berdasarkan kategori (Makanan, Transportasi, dsb) untuk memudahkan evaluasi.
*   **🔒 Local Persistence:** Penyimpanan data transaksi secara aman di database lokal perangkat.

---

## 🛠️ Stack Teknologi

*   **Platform:** Android (Kotlin) / Kotlin Multiplatform (KMP).
*   **UI Framework:** Jetpack Compose.
*   **Network (API):** 
    *   **Ktor / Retrofit:** Koneksi API.
    *   **ExchangeRate API:** Data kurs Forex.
    *   **CoinGecko API:** Data aset digital/Crypto.
    *   **Gemini AI API:** Engine Asisten Cerdas.
*   **Database:** Room Database / SQLite.
*   **Architecture:** MVVM (Model-View-ViewModel).

---

## ⚙️ Cara Kerja AI di FinTrack
Asisten AI bekerja dengan memproses data ringkasan transaksi pengguna melalui **Gemini API**. AI memberikan feedback berupa:
1. Analisis pola pengeluaran bulanan.
2. Prediksi arus kas berdasarkan histori data.
3. Rekomendasi anggaran yang lebih efisien.

---

## 🚀 Persiapan Proyek
1. **Dapatkan API Key:**
   * Gemini API: [Google AI Studio](https://aistudio.google.com/)
   * ExchangeRate API: [ExchangeRate-API](https://www.exchangerate-api.com/)
2. **Konfigurasi:**
   Masukkan API Key ke dalam file `local.properties` atau file konstanta pada proyek.
3. **Build:**
   Buka di Android Studio dan jalankan `Run app`.

---

**Dibuat untuk memenuhi tugas mata kuliah Pengembangan Aplikasi Mobile.**
