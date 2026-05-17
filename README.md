# MyWallet - Expense Tracker App

![CI](https://github.com/HANIFAHHASANAH-123140082/123140082-123140069-MyWallet/actions/workflows/ci.yml/badge.svg)

Aplikasi mobile multiplatform untuk mencatat pengeluaran, budgeting, dan melihat statistik keuangan pribadi.

---

## Tim

| Nama | NIM | GitHub |
|------|-----|--------|
| Hanifah Hasanah | 123140082 | [@HANIFAHHASANAH-123140082](https://github.com/HANIFAHHASANAH-123140082) |
| Zahwa Natasya Hamzah | 123140069 | [@15-069-ZahwaNatasyaHamzah](https://github.com/15-069-ZahwaNatasyaHamzah) |

---

## Deskripsi

MyWallet adalah aplikasi pencatat keuangan pribadi yang membantu pengguna melacak pengeluaran harian, mengatur anggaran bulanan, dan memvisualisasikan pola pengeluaran. Dibangun dengan Kotlin Multiplatform dan Compose Multiplatform.

---

## Fitur

### Minimum
- [x] Catat pengeluaran dengan kategori
- [x] Lihat daftar transaksi
- [x] Detail transaksi
- [x] Edit dan hapus transaksi (CRUD)
- [x] Minimal 5 screen dengan navigasi
- [x] State management dengan StateFlow + MVVM
- [x] Dependency Injection dengan Koin
- [ ] Statistik pengeluaran per kategori dan per bulan
- [ ] Local database dengan SQLDelight
- [ ] Minimal 10 unit tests, 3 UI tests, coverage lebih dari 50%

### Bonus
- [ ] iOS Support 
- [ ] AI Integration dengan Gemini API
- [ ] Offline First 
- [ ] Dark Mode 
- [ ] Animations 
- [x] CI/CD 
- [ ] Play Store Ready

---

## Tech Stack

| Kategori | Teknologi |
|----------|-----------|
| Framework | Kotlin Multiplatform, Compose Multiplatform |
| Architecture | MVVM, Clean Architecture, Repository Pattern |
| Async | Coroutines, Flow, StateFlow |
| Networking | Ktor Client, Kotlinx Serialization |
| Storage | SQLDelight, DataStore Preferences |
| DI | Koin |
| Testing | kotlin.test, MockK, Turbine, Compose Test |

---

## Arsitektur

PRESENTATION LAYER - UI (Composables), ViewModels, UI State

DOMAIN LAYER - Use Cases, Domain Models, Repository Interfaces

DATA LAYER - Repository Impl, Data Sources (Local), DTOs

---

## Struktur Folder

composeApp/src/commonMain/kotlin/com/mywallet/
- App.kt
- di/ (Koin.kt, DataModule.kt, ViewModelModule.kt, Modules.kt)
- data/local/ (TransactionLocalDataSource.kt)
- data/repository/ (TransactionRepositoryImpl.kt)
- data/model/ (TransactionEntity.kt)
- domain/model/ (Transaction.kt)
- domain/repository/ (TransactionRepository.kt)
- domain/usecase/
- navigation/ (NavGraph.kt, Screen.kt)
- presentation/add/ (AddTransactionScreen.kt, AddTransactionViewModel.kt, EditTransactionScreen.kt)
- presentation/components/ (TransactionItem.kt)
- presentation/detail/ (DetailScreen.kt, DetailViewModel.kt)
- presentation/home/ (HomeScreen.kt, HomeUiState.kt, HomeViewModel.kt)
- presentation/screens/history/ (HistoryScreen.kt)

---

## Setup
git clone https://github.com/HANIFAHHASANAH-123140082/123140082-123140069-MyWallet.git
cd 123140082-123140069-MyWallet
git checkout project/123140082-123140069-MyWallet
.\gradlew.bat :composeApp:assembleDebug

---

## Sprint Plan

| Sprint | Minggu | Target |
|--------|--------|--------|
| Sprint 1 | W11 | Planning, setup repo, CI/CD, arsitektur dasar |
| Sprint 2 | W12 | Core features: screens, navigasi, data layer, CRUD |
| Sprint 3 | W13 | Statistik, integrasi API, local DB |
| Sprint 4 | W14 | UI polish, bug fixes, testing |
| Sprint 5 | W15 | Final preparation, demo prep |
| UAS | W16 | Final Demo Day |

---

## Task Assignment Sprint 1

| Task |
|------|
| Setup GitHub repository |
| Setup KMP project structure |
| Clean Architecture folder |
| Koin DI setup |
| GitHub Actions CI |
| README documentation |

## Task Assignment Sprint 2

| Task |
|------|
| HomeScreen + ViewModel |
| DetailScreen + ViewModel |
| AddTransactionScreen + ViewModel |
| EditTransactionScreen |
| HistoryScreen |
| Navigation setup |
| Data layer + Repository |
| UI Design improvement |

---

Pengembangan Aplikasi Mobile (RA)
