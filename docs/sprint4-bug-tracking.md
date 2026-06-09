# Sprint 4 - Bug Tracking

Dokumen ini berisi catatan bug dan polish yang ditemukan selama pengembangan App FitKos serta perbaikan yang sudah dilakukan. Format bug report mengikuti materi Sprint 4, yaitu :
1. Description 
2. Steps to Reproduce 
3. Expected Behavior
4. Actual Behavior
5. Priority
6. Fix Summary
7. Status
---

## 1. Search Field Stuck pada Huruf Pertama

**Category:** Bug  

**Priority:** P1 - High

**Description:**  
Pada halaman Catatan Makan, search field tidak menerima input dengan benar. Saat pengguna mengetik kata seperti `nasi`, field hanya menampilkan huruf pertama, yaitu `N`. Input juga tidak bisa dihapus menggunakan backspace dan daftar catatan tidak terfilter.

**Steps to Reproduce:**
1. Buka aplikasi FitKos.
2. Masuk ke halaman Catatan Makan.
3. Ketik kata seperti `nasi` pada search field.
4. Perhatikan teks pada search field dan hasil list catatan makanan.

**Expected Behavior:**  
Search field menampilkan seluruh input pengguna, misalnya `nasi`, dan daftar catatan makanan terfilter sesuai keyword.

**Actual Behavior:**  
Search field hanya menampilkan huruf pertama, stuck pada `N`, tidak bisa dihapus dengan backspace, dan daftar catatan tidak terfilter.

**Fix Summary:**  
State search query dipisahkan dari UI state utama. TextField membaca state search secara langsung agar input responsif, sedangkan proses search/filter tetap berjalan melalui flow terpisah.

**Status:** Fixed

---

## 2. Teks Tidak Terbaca Saat Dark Mode pada Dashboard dan Water Tracker

**Category:** UI Bug / UI Polish  

**Priority:** P2 - Medium

**Description:**  
Saat dark mode aktif, beberapa teks pada Dashboard dan Water Tracker tidak terbaca dengan jelas. Pada Dashboard, teks yang terdampak berada di card Aksi Cepat dan Tips Sehat Anak Kos. Pada Water Tracker, teks seperti target harian, jumlah gelas, dan riwayat hari ini kurang kontras.

**Steps to Reproduce:**
1. Buka aplikasi FitKos.
2. Aktifkan dark mode melalui Settings.
3. Buka Dashboard dan perhatikan card Aksi Cepat serta Tips Sehat Anak Kos.
4. Buka Water Tracker dan perhatikan teks target harian, jumlah gelas, serta riwayat hari ini.

**Expected Behavior:**  
Semua teks pada Dashboard dan Water Tracker tetap terbaca jelas saat dark mode aktif.

**Actual Behavior:**  
Beberapa teks masih menggunakan warna yang kurang cocok untuk dark mode, sehingga kontrasnya rendah dan sulit terbaca.

**Fix Summary:**  
Warna teks pada Dashboard dan Water Tracker disesuaikan agar mengikuti kebutuhan dark mode. Pada card dengan background pastel, warna teks dibuat lebih kontras. Pada Water Tracker, warna teks dan indikator disesuaikan menggunakan `MaterialTheme.colorScheme`, seperti `onSurface` dan `onSurfaceVariant`.

**Status:** Fixed

---

## 3. Bottom Navigation Tidak Muncul pada AI Assistant
**Category:** Navigation Consistency / UI Polish  

**Priority:** P2 - Medium

**Description:**  
Halaman AI Assistant tidak menampilkan bottom navigation seperti halaman utama lainnya, sehingga navigasi terasa tidak konsisten.

**Steps to Reproduce:**
1. Buka aplikasi FitKos.
2. Masuk ke halaman AI Assistant melalui tab AI atau Dashboard.
3. Perhatikan bagian bawah layar.

**Expected Behavior:**  
AI Assistant tetap menampilkan bottom navigation karena termasuk salah satu menu utama aplikasi.

**Actual Behavior:**  
Bottom navigation tidak muncul pada halaman AI Assistant.

**Fix Summary:**  
Logika pengecekan route pada AppNavHost diperbaiki agar route AI Assistant tetap dikenali dan bottom navigation tampil pada halaman tersebut.

**Status:** Fixed

---

## 4. List Catatan Makan Tertutup Padding Bawah
**Category:** UI Layout Bug  

**Priority:** P2 - Medium

**Description:**  
Pada halaman Catatan Makan, daftar card makanan tidak tampil secara optimal karena area bawah layar seperti tertutup atau terdorong oleh padding/area kosong. Ketika terdapat beberapa catatan makanan, misalnya tiga item, hanya item pertama yang terlihat jelas, sedangkan item kedua dan ketiga tidak langsung terlihat walaupun ruang layar masih cukup.

**Steps to Reproduce:**
1. Buka aplikasi FitKos.
2. Tambahkan beberapa catatan makanan, misalnya tiga catatan.
3. Masuk ke halaman Catatan Makan.
4. Perhatikan daftar card makanan yang tampil di layar.

**Expected Behavior:**  
Jika ruang layar masih cukup, beberapa card makanan seperti item pertama, kedua, dan sebagian item ketiga seharusnya dapat terlihat tanpa harus scroll berlebihan.

**Actual Behavior:**  
Area list terlihat seperti tertutup atau terdorong oleh padding/area kosong bawah, sehingga item kedua dan ketiga tidak langsung terlihat dan pengguna harus scroll untuk melihatnya.

**Fix Summary:**  
Padding bawah pada list dan/atau parent layout disesuaikan agar tidak terlalu besar. Area kosong di atas bottom bar dikurangi sehingga daftar catatan makanan dapat menggunakan ruang layar dengan lebih efektif.

**Status:** Fixed

---