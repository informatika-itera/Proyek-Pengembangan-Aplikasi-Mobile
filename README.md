# 🏠 UrHomie AI - Smart Home Shopping App

UrHomie AI adalah aplikasi shopping alat rumah tangga modern berbasis **Kotlin Multiplatform** yang dirancang untuk membantu pengguna menemukan dan membeli kebutuhan rumah dengan lebih mudah, cepat, dan cerdas menggunakan teknologi **Artificial Intelligence (AI)**.

Aplikasi ini menghadirkan pengalaman belanja online dengan tampilan **Material UI/UX** yang modern, clean, dan user-friendly serta mendukung platform **Android & iOS** dalam satu codebase.

Dengan fitur AI seperti **Smart Recommendation**, **AI Shopping Assistant**, dan **Smart Search**, UrHomie AI memberikan pengalaman belanja yang lebih personal, efisien, dan interaktif.

---

# ✨ Fitur Utama

- 🏠 **Home Page**  
  Menampilkan produk unggulan, promo, dan rekomendasi produk.

- 📂 **Categories**  
  Menampilkan berbagai kategori produk rumah tangga untuk mempermudah pencarian.

- 🔍 **Smart Search Product**  
  Fitur pencarian produk berbasis AI untuk hasil yang lebih cepat dan akurat.

- ❤️ **Wishlist**  
  Menyimpan produk favorit pengguna agar mudah ditemukan kembali.

- 🛒 **Purchase Product**  
  Sistem pembelian produk yang praktis dan mudah digunakan.

- 🔐 **Authentication**  
  Fitur Login & Register untuk keamanan akun pengguna.

- 👤 **Profile & Settings**  
  Mengelola informasi akun dan pengaturan aplikasi.

- 🤖 **AI Product Recommendation**  
  Memberikan rekomendasi produk berdasarkan aktivitas dan preferensi pengguna.

- 💬 **AI Shopping Assistant**  
  Asisten AI yang membantu pengguna memilih produk rumah tangga sesuai kebutuhan.

- 🌙 **Modern Material UI/UX**  
  Tampilan modern, responsive, dan nyaman digunakan.

- 📱 **Cross Platform**  
  Mendukung Android & iOS menggunakan Kotlin Multiplatform.

---

# 🏗️ Arsitektur & Teknologi

## Clean Architecture + MVVM

```text
Presentation Layer
├── Screen (Compose UI)
├── Components
└── ViewModel

Domain Layer
├── UseCase
├── Repository Interface
└── Business Logic

Data Layer
├── Repository Implementation
├── Remote API
├── Local Database
└── Preferences
```

---

# 🛠️ Tech Stack

| Layer | Technology |
|-------|------------|
| UI | Compose Multiplatform |
| Design | Material Design 3 |
| State Management | StateFlow & ViewModel |
| Navigation | Compose Navigation |
| Networking | Ktor Client |
| Local Database | SQLDelight |
| Preferences | DataStore |
| Dependency Injection | Koin |
| AI Integration | Google Gemini API |
| Platform | Kotlin Multiplatform |

---

# 📁 Struktur Project

```text
composeApp/src/
├── commonMain/
│   ├── data/
│   ├── domain/
│   ├── presentation/
│   ├── components/
│   ├── navigation/
│   └── theme/
│
├── androidMain/
└── iosMain/
```

---

# 🚀 Setup Project

## Clone Repository

```bash
git clone https://github.com/MIFTAAHULKHR/urHomie.app.git
```

## Jalankan Project

```bash
./gradlew build
```

Buka project menggunakan Android Studio lalu jalankan aplikasi.

---

# 🤖 Fitur AI

## 🔹 Smart Recommendation
AI memberikan rekomendasi produk berdasarkan aktivitas dan preferensi pengguna.

## 🔹 AI Shopping Assistant
Asisten chatbot AI yang membantu pengguna memilih produk rumah tangga sesuai kebutuhan.

## 🔹 Smart Search
Pencarian produk berbasis AI agar hasil lebih relevan dan cepat.

---

# 📱 Tampilan Aplikasi

- Home
- Categories
- Search
- Wishlist
- Purchase Product
- Settings
- Authentication

---

# 🎯 Pengembangan Selanjutnya

- 💳 Payment Gateway
- 🔔 Push Notification
- 📦 Tracking Pesanan
- ⭐ Review & Rating Produk
- 🌙 Dark Mode
- 🧠 Personalized AI Recommendation
- 🛍️ AI Product Comparison
- 📍 Tracking Pengiriman Real-time

---

# 👥 Tim Pengembang

- Andini
- Miftah

---

# 📄 License

Project ini dibuat untuk kebutuhan pembelajaran dan pengembangan aplikasi mobile menggunakan Kotlin Multiplatform.

---

# 💡 Tagline

> **"Smart Shopping for Smart Homes."**
