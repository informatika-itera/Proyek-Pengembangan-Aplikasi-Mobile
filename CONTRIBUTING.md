# Panduan Kontribusi - Pusaka Kata

Terima kasih telah bergabung dalam pengembangan **Pusaka Kata**! Untuk menjaga kualitas kode dan alur kerja yang rapi (terutama untuk penilaian bot AI), harap ikuti aturan berikut:

## 🌿 Alur Kerja Branch
1. **Jangan commit langsung ke `main`**.
2. Buat branch baru untuk setiap fitur/perbaikan: `feature/nama-fitur` atau `fix/nama-bug`.
3. Lakukan **Pull Request (PR)** ke branch `main`.
4. Pastikan CI (GitHub Actions) berstatus **Passing** (hijau) sebelum melakukan merge.

## 💬 Konvensi Commit
Gunakan prefix berikut pada pesan commit Anda:
- `feat:` Untuk penambahan fitur baru.
- `fix:` Untuk perbaikan bug.
- `docs:` Untuk perubahan dokumentasi (README, dll).
- `style:` Untuk perubahan visual/UI tanpa mengubah logika.
- `refactor:` Untuk perbaikan struktur kode.
- `test:` Untuk penambahan/perubahan unit test.
- `chore:` Untuk tugas pemeliharaan (update library, dll).

Contoh: `feat: implementasi dark mode pada layar pengaturan`

## 🧪 Testing & Coverage
- Jalankan unit test sebelum push: `./gradlew test`
- Pastikan **Code Coverage** tetap berada di atas **50%**. Anda bisa mengecek laporan coverage lokal di `composeApp/build/reports/kover/html/index.html`.

---
*Dibuat untuk memfasilitasi penilaian Auto-Grader GitHub.*
