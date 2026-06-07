# Changelog

Semua perubahan penting pada proyek **StudyHub** akan didokumentasikan di file ini.

## [1.0.0] - 2024-06-08
### Added
- **Progress Screen**: Dashboard statistik lengkap dengan completion rate, streak hari ini, dan progress per mata kuliah.
- **Pomodoro Management**: Refaktorisasi Pomodoro menjadi background-persistent manager dengan dukungan notifikasi ongoing.
- **Notification History Cleanup**: Fitur auto-delete riwayat notifikasi (default 24 jam) untuk efisiensi penyimpanan.
- **Comprehensive Tests**: 45+ unit tests mencakup logic domain, repository, dan presentation.
- **Kover Integration**: Setup test coverage reporting dengan target minimal 70%.

### Fixed
- **UI/UX Polish**: Perbaikan keyboard options (IME action), penambahan content description pada icon, dan standarisasi state handling (Loading/Error/Empty).
- **Edge Cases**: Penanganan subject kosong (default ke "Lainnya"), label "Terlambat" pada tugas overdue, dan pemangkasan title yang terlalu panjang.
- **Navigation Persistence**: State scroll dan tab pada bottom navigation kini tetap terjaga saat bernavigasi balik.

## [0.3.0] - 2024-06-01
### Added
- **AI Integration**: Integrasi Groq Cloud API untuk fitur Smart Priority dan Smart Reminder.
- **Notification System**: Push notification untuk pengingat deadline menggunakan AlarmManager di Android.
- **Offline Sync**: Implementasi `SyncQueue` untuk menyimpan perubahan saat offline dan sinkronisasi otomatis.

## [0.2.0] - 2024-05-20
### Added
- **Task Management**: CRUD tugas lengkap dengan database lokal SQLDelight.
- **Calendar View**: Visualisasi tugas berbasis tanggal.
- **Theme Management**: Dark mode persisten via Jetpack DataStore.

## [0.1.0] - 2024-05-10
### Added
- **Foundation**: Setup project Kotlin Multiplatform, Clean Architecture layers, dan Koin Dependency Injection.
