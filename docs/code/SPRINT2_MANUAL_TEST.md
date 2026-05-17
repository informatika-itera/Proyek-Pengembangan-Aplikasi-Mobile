# Sprint 2 Manual Test — My Bawang Gacha

Checklist manual untuk memastikan core features Sprint 2 berjalan.

## Navigation

- [ ] Buka Home/DiscoverScreen.
- [ ] Buka detail anime dari card rekomendasi.
- [ ] Tekan tombol back dari detail.
- [ ] Buka My List dari side rail/home shortcut.
- [ ] Tekan tombol back dari My List.

## Library CRUD

- [ ] Dari AnimeDetailScreen, tekan **Tambah ke My List**.
- [ ] Pilih status item.
- [ ] Isi progress dan total episode/chapter.
- [ ] Isi score pribadi 1-10.
- [ ] Isi catatan opsional.
- [ ] Simpan entry.
- [ ] Pastikan item muncul di MyListScreen.
- [ ] Filter list berdasarkan status.
- [ ] Edit entry dari MyListScreen.
- [ ] Ubah status/progress/score/catatan.
- [ ] Simpan perubahan dan pastikan data terupdate.
- [ ] Hapus entry dan pastikan confirmation dialog muncul.
- [ ] Konfirmasi hapus dan pastikan item hilang dari MyListScreen.

## Validation & State

- [ ] Coba isi progress lebih besar dari total, pastikan validasi muncul.
- [ ] Coba isi score di luar 1-10, pastikan validasi muncul.
- [ ] Pastikan empty state muncul saat My List kosong.
- [ ] Pastikan loading/error/success state tetap tampil dengan benar pada Home dan Detail.

## Local Persistence

- [ ] Tambahkan item ke My List.
- [ ] Tutup dan buka ulang aplikasi.
- [ ] Pastikan item masih ada di MyListScreen.
