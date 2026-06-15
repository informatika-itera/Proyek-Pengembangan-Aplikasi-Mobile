# Penjelasan Komprehensif: Aspek Teknis dan Non-Teknis Aplikasi News MBG

Aplikasi **News MBG (Mbgnews)** bukan sekadar proyek pembaca berita biasa. Di balik tampilannya yang elegan, terdapat fondasi arsitektur perangkat lunak yang kokoh dan keputusan desain yang berpusat pada pengguna (User-Centered Design). Dokumen ini menguraikan secara mendalam dua pilar utama dari aplikasi ini: **Aspek Non-Teknis (Bisnis, UI/UX, dan Nilai Pengguna)** serta **Aspek Teknis (Arsitektur Kode, Library, dan Alur Data)**. Penjelasan ini sangat berguna untuk bahan presentasi, dokumentasi portofolio, maupun pemahaman mendalam untuk sidang akhir.

---

## 1. Aspek Non-Teknis: Memecahkan Masalah Pengguna (User Experience & Value)

Aspek non-teknis berfokus pada **mengapa** fitur ini dibuat dan **bagaimana** fitur tersebut memberikan nilai tambah yang nyata bagi pengguna akhir.

### A. Desain Premium Neumorphism (Meningkatkan Keterlibatan Pengguna)
Aplikasi berita pada umumnya memiliki tata letak yang datar (flat) dan kaku. News MBG mengadopsi gaya desain **Neumorphism** (Soft UI). Desain ini menggunakan bayangan (shadows) dan cahaya (highlights) yang presisi untuk menciptakan ilusi bahwa elemen antarmuka (UI) menonjol keluar dari layar atau tenggelam ke dalamnya. 
* **Dampak Psikologis**: Tampilan ini memberikan kesan *premium, eksklusif, dan sangat modern*. Pengguna merasa seperti menekan tombol fisik sungguhan. Hal ini secara bawah sadar meningkatkan kenyamanan mata saat membaca dalam durasi yang lama, terutama saat disandingkan dengan tema gelap (*Dark Mode*).

### B. Analisis Sentimen AI: Melawan *Clickbait* dan Bias
Di era informasi yang sangat cepat, pengguna sering kali terjebak oleh judul berita *clickbait* yang provokatif. Pengguna membutuhkan waktu ekstra untuk mencerna apakah sebuah berita berpihak, menyerang, atau sekadar melaporkan fakta netral.
* **Solusi News MBG**: Lewat fitur indikator sentimen (Pro, Kontra, Netral) yang ditenagai oleh Google Gemini AI, pengguna bisa langsung mengetahui *nada* (tone) dari artikel tersebut sebelum membacanya secara utuh. Jika indikator menyala merah (Kontra), pengguna tahu bahwa artikel ini mungkin berisi kritikan tajam. Jika hijau (Pro), itu adalah dukungan. Fitur ini secara radikal meningkatkan **literasi digital instan** dan menghemat waktu pengguna untuk memilah informasi.

### C. Keandalan Tanpa Internet (Offline Accessibility)
Tidak semua pengguna memiliki akses internet yang stabil 24/7. Saat bepergian di kereta, pesawat, atau daerah minim sinyal, aplikasi yang sepenuhnya bergantung pada internet akan menjadi bongkahan yang tidak berguna.
* **Solusi News MBG**: Menerapkan pendekatan penyimpanan lokal (Offline Bookmark). Saat pengguna menemukan berita menarik ketika sedang *online*, mereka dapat menyimpannya. Sistem secara utuh menyimpan teks, judul, gambar (tautan), dan data relevan ke dalam memori perangkat. Saat internet mati, aplikasi tidak akan *crash* atau menampilkan layar putih (*blank screen*), melainkan tetap fungsional menyajikan berita yang telah disimpan. Ini adalah bentuk empati dalam rekayasa perangkat lunak terhadap pengguna.

### D. Pencarian Intuitif dan Bebas Frustasi
Pencarian berita di dalam aplikasi ini menggunakan metode pencarian langsung (*real-time*) dengan teknik *Debouncing*. Artinya, saat pengguna mengetik kata "Teknologi", aplikasi tidak membanjiri server dengan permintaan setiap kali satu huruf diketik (T.. e.. k.. n..), melainkan menunggu pengguna selesai mengetik sejenak sebelum mencari. Ini memberikan pengalaman yang mulus tanpa jeda (*lag*) dan menghemat penggunaan kuota.

---

## 2. Aspek Teknis: Fondasi Kode dan Arsitektur Aplikasi

Di balik pengalaman pengguna yang mulus, terdapat susunan kode yang dirancang dengan sangat ketat mengikuti standar industri perangkat lunak modern. Proyek ini mengimplementasikan **Clean Architecture**, **MVVM**, dan teknologi dasar **Kotlin Multiplatform (KMP)**.

### A. Pola Clean Architecture: Pemisahan Tanggung Jawab (*Separation of Concerns*)
Aplikasi dibagi menjadi tiga lapisan (*layer*) utama yang dikunci ketat dan tidak boleh saling tumpang tindih secara sembarangan:
1. **Presentation Layer (UI & ViewModel)**: Berisi komponen Jetpack Compose (`HomeScreen`, `DetailScreen`) dan `ViewModel`. Lapisan ini **"bodoh"**; ia sama sekali tidak tahu darimana data berasal (apakah dari internet Ktor atau database SQLDelight). Ia hanya tahu cara menampilkan aliran data yang diberikan oleh ViewModel.
2. **Domain Layer (Model & Repository Interface)**: Ini adalah "jantung" aplikasi murni. Berisi antarmuka `NewsRepository` dan model `Article`. Lapisan ini bebas dari *library* pihak ketiga (tidak ada import Framework Android, Ktor, atau SQL). Hal ini menjamin bahwa aturan bisnis inti aplikasi tidak terikat pada teknologi tertentu dan tidak akan rusak jika sewaktu-waktu teknologi database diganti.
3. **Data Layer (RepositoryImpl, Remote, Local)**: Bertugas mengambil data asli. Di sinilah kelas `NewsRepositoryImpl` bekerja keras mengatur lalu lintas. Jika ViewModel meminta data, *Data Layer* yang memutuskan: *"Apakah saya harus memanggil Ktor API, atau mengambil dari SQLDelight?"*

### B. Jetpack Compose dan Reaktivitas (StateFlow)
Antarmuka pengguna (UI) dibangun tidak lagi menggunakan sistem XML lama Android yang rumit, melainkan menggunakan **Jetpack Compose** (paradigma UI deklaratif modern). Dalam paradigma ini, UI adalah cerminan langsung dari *State* (kondisi).
* Seluruh kode menggunakan aliran data konstan lewat `StateFlow` dari pustaka `kotlinx.coroutines`. Sebagai contoh, saat pengguna menekan tombol "Bookmark", database SQLDelight diperbarui. Karena `StateFlow` terus memantau (*observe*) database, aliran data akan langsung bereaksi, dan Jetpack Compose secara otomatis menggambar ulang (*recompose*) ikon bookmark di layar dari ikon kosong menjadi terisi penuh secara instan, tanpa kode *callback* yang rawan error.

### C. Strategi Offline-First Caching (SQLDelight)
Proyek ini memaksimalkan kecepatan aplikasi dengan strategi basis data lokal.
* **Teknologi**: Menggunakan **SQLDelight**, sebuah pustaka database modern yang secara otomatis men-*generate* kode Kotlin yang aman (type-safe) dari perintah murni bahasa SQL (di dalam file `Article.sq`). 
* **Mekanisme CRUD**: Fungsi `saveArticle`, `deleteArticle`, dan `getBookmarkedArticles` pada `NewsRepositoryImpl` sepenuhnya bergantung pada aliran (Flow) SQLDelight. Saat data ditambahkan ke database, perintah `asFlow().mapToList()` memastikan bahwa layar UI mana pun yang mendengarkan akan langsung menerima list pembaruan detik itu juga.

### D. Injeksi Dependensi dengan Koin (Dependency Injection)
Dalam pengembangan aplikasi skala besar, membuat objek secara manual (seperti menulis `val api = NewsApi(httpClient)`) di ratusan file akan menyebabkan kode kusut (*Spaghetti Code*) dan sangat sulit diuji (*Testing*).
* **Solusi**: Menerapkan pustaka **Koin**. Di dalam file `AppModule.kt`, semua bahan bangunan (HttpClient Ktor, Database SQLite, antarmuka Repository, hingga instance ViewModel) diracik dan didaftarkan dalam satu wadah (*container*). Saat sebuah layar Compose membutuhkan `NewsViewModel`, ia tinggal memanggil fungsi `koinViewModel()`, dan Koin secara otomatis menyuntikkan (*inject*) semua dependensi jaringan dan database yang dibutuhkan oleh ViewModel tersebut di belakang layar.

### E. Integrasi Jaringan Ktor dan Gemini AI (Prompt Engineering)
Untuk lapisan komunikasi jaringan, aplikasi dengan cerdas menghindari Retrofit (yang hanya khusus untuk Android lama) dan memilih menggunakan **Ktor Client** agar fondasi arsitekturnya sepenuhnya siap untuk Kotlin Multiplatform (dapat dibuild ke iOS/Desktop).
* **Interaksi API**: Ktor bertugas mengambil struktur JSON berita dari API eksternal dan menangani lalu lintas pertukaran data JSON untuk *Request* analisis kecerdasan buatan (AI).
* **Prompt Engineering di GeminiApi**: Keajaiban AI aplikasi ini terletak pada file `GeminiApi.kt`. Aplikasi secara dinamis membungkus (*wrap*) bagian *title* dan *description* dari berita ke dalam sebuah *prompt* perintah instruksi yang sangat ketat: *"Analisis sentimen berita berikut. Balas hanya dengan satu kata: 'Pro', 'Kontra', atau 'Netral'."* Teknik instruksi sempit ini secara krusial memastikan bahwa API Generative AI Google merespons dengan latensi yang sangat rendah (cepat) dan format yang seragam. Ini mencegah AI berhalusinasi atau memberikan paragraf penjelasan panjang, sehingga UI aplikasi bisa langsung menerjemahkan jawaban satu kata tersebut menjadi pewarnaan indikator yang akurat di layar Detail.

---

## 3. Kesimpulan Akhir

Secara **non-teknis**, News MBG adalah portal berita yang sangat mengerti rasa frustasi pengguna modern terhadap aplikasi usang. Ia menawarkan kecepatan akses, estetika visual yang amat menenangkan mata (Neumorphism), pemahaman konteks berita instan tanpa perlu repot membaca (lewat Gemini AI), dan keandalan ketersediaan akses data saat kuota habis (Offline Bookmark).

Secara **teknis**, aplikasi ini bukan sekadar tugas akhir, melainkan mahakarya rekayasa perangkat lunak skala korporat yang mematuhi prinsip perancangan *Solid*, isolasi kode *Clean Architecture*, dan sistem aliran data reaktif modern. Penggunaan *Dependency Injection* (Koin), Jaringan modular (Ktor), Database *Type-safe* (SQLDelight), dan UI Deklaratif (Jetpack Compose) menjadi bukti nyata bahwa aplikasi ini dibangun di atas *Tech Stack* mutakhir (*cutting-edge*). Kode ini telah disiapkan secara profesional, mudah untuk diuji secara otomatis (*testable*), tahan terhadap *bugs* seiring perubahan arsitektur, dan yang terpenting: basis kode ini satu langkah lagi siap untuk dikonversi menjadi aplikasi lintas platform seperti iOS.
