# ROSÉA - Beauty E-Commerce App

A modern beauty e-commerce application built with Compose Multiplatform.

## 🚀 Features
- **Product Catalog**: Reactive product listing with category filters.
- **Smart Search**: Real-time product search with debounce.
- **Shopping Bag**: Manage products before checkout.
- **AI Beauty Advisor**: Integrated Gemini AI for beauty consultations.
- **Offline-First**: Local storage using SQLDelight and DataStore.

## 🧪 Testing Instructions (Sprint 4)

### Unit Tests
Total: 14 Tests (Repository & ViewModels)
- **ProductRepositoryTest**: Verifies data fetching, searching, and filtering.
- **HomeViewModelTest**: Verifies UI state transformations, search logic, and sorting.
- **DetailViewModelTest**: Verifies product loading and "Add to Bag" functionality.

**How to run:**
```bash
./gradlew :composeApp:testDebugUnitTest
```

### UI Tests
- **HomeScreenTest**: Verifies product list visibility and search bar functionality.
- **NavigationTest**: Verifies navigation from Home to Detail and Cart.

**How to run:**
```bash
./gradlew :composeApp:connectedDebugAndroidTest
```

### Code Coverage
We use the standard JaCoCo or Kover setup for coverage reports.
**Target**: 50%+ Coverage (Current: ~70%)

## Test Results

| Test Report | Test Report (Class) |
| :---: | :---: |
| <img width="1759" height="755" alt="Screenshot 2026-06-07 180358" src="https://github.com/user-attachments/assets/e913af67-8783-4a4a-893e-d349e9d9b4a0" /> | <img width="1759" height="787" alt="Screenshot 2026-06-07 180410" src="https://github.com/user-attachments/assets/4e27ed30-3d5a-4e86-87b4-d8e727c7b784" /> |

### Coverage Report
```bash
# Generate laporan coverage
./gradlew koverHtmlReport

# Hasil tersimpan di:
# composeApp/build/reports/kover/html/index.html
```


## 🛠 Tech Stack
- **UI**: Jetpack Compose / Compose Multiplatform
- **DI**: Koin
- **Local DB**: SQLDelight
- **Network**: Ktor
- **Concurrency**: Kotlin Coroutines & Flow
- **AI**: Google Gemini API
