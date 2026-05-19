# ðŸ—ï¸ Struktur Kode & Arsitektur

Dokumen ini menjelaskan struktur kode dan arsitektur yang digunakan dalam template project.

---

## ðŸ“ Arsitektur: Clean Architecture + MVVM

```
â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”
â”‚                        PRESENTATION LAYER                         â”‚
â”‚  â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”  â”‚
â”‚  â”‚                         UI (Screen)                         â”‚  â”‚
â”‚  â”‚            Composable functions, UI state rendering         â”‚  â”‚
â”‚  â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜  â”‚
â”‚                              â–² â”‚                                  â”‚
â”‚                    State     â”‚ â”‚ Events                           â”‚
â”‚                              â”‚ â–¼                                  â”‚
â”‚  â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”  â”‚
â”‚  â”‚                        ViewModel                            â”‚  â”‚
â”‚  â”‚         StateFlow, event handling, UI state management      â”‚  â”‚
â”‚  â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜  â”‚
â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜
                               â–² â”‚
                               â”‚ â”‚ Calls
                               â”‚ â–¼
â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”
â”‚                          DOMAIN LAYER                             â”‚
â”‚  â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”  â”‚
â”‚  â”‚                        Use Cases                            â”‚  â”‚
â”‚  â”‚               Business logic, orchestration                 â”‚  â”‚
â”‚  â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜  â”‚
â”‚                              â–² â”‚                                  â”‚
â”‚                              â”‚ â”‚                                  â”‚
â”‚  â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”  â”‚
â”‚  â”‚                  Repository Interface                       â”‚  â”‚
â”‚  â”‚                    Contract/abstraction                     â”‚  â”‚
â”‚  â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜  â”‚
â”‚                                                                   â”‚
â”‚  â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”  â”‚
â”‚  â”‚                       Domain Models                         â”‚  â”‚
â”‚  â”‚               Pure Kotlin data classes                      â”‚  â”‚
â”‚  â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜  â”‚
â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜
                               â–² â”‚
                               â”‚ â”‚ Implements
                               â”‚ â–¼
â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”
â”‚                           DATA LAYER                              â”‚
â”‚  â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”  â”‚
â”‚  â”‚                Repository Implementation                    â”‚  â”‚
â”‚  â”‚            Coordinates data sources, caching                â”‚  â”‚
â”‚  â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜  â”‚
â”‚              â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”¼â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”                    â”‚
â”‚              â–¼               â–¼               â–¼                    â”‚
â”‚  â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â” â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â” â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”       â”‚
â”‚  â”‚   Local Source   â”‚ â”‚ Remote Sourceâ”‚ â”‚   DataStore     â”‚       â”‚
â”‚  â”‚    (SQLDelight)  â”‚ â”‚    (Ktor)    â”‚ â”‚  (Preferences)  â”‚       â”‚
â”‚  â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜ â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜ â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜       â”‚
â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜
```

---

## ðŸ“ Struktur Folder Detail

```
composeApp/src/
â”‚
â”œâ”€â”€ commonMain/kotlin/com/example/bookku/    # â† Shared code (95%+)
â”‚   â”‚
â”‚   â”œâ”€â”€ core/                                 # Core utilities
â”‚   â”‚   â”œâ”€â”€ di/                               # Dependency Injection
â”‚   â”‚   â”‚   â””â”€â”€ AppModule.kt                  # Koin modules definition
â”‚   â”‚   â”‚
â”‚   â”‚   â”œâ”€â”€ network/                          # Network configuration
â”‚   â”‚   â”‚   â”œâ”€â”€ ApiConfig.kt                  # expect: API keys
â”‚   â”‚   â”‚   â””â”€â”€ HttpClientFactory.kt          # Ktor client setup
â”‚   â”‚   â”‚
â”‚   â”‚   â””â”€â”€ util/                             # Utilities
â”‚   â”‚       â”œâ”€â”€ DatabaseDriverFactory.kt      # expect: DB driver
â”‚   â”‚       â””â”€â”€ Extensions.kt                 # Extension functions
â”‚   â”‚
â”‚   â”œâ”€â”€ data/                                 # Data Layer
â”‚   â”‚   â”œâ”€â”€ local/
â”‚   â”‚   â”‚   â”œâ”€â”€ entity/
â”‚   â”‚   â”‚   â”‚   â””â”€â”€ BookMapper.kt             # Entity â†” Domain mappers
â”‚   â”‚   â”‚   â”œâ”€â”€ dao/                          # (Generated by SQLDelight)
â”‚   â”‚   â”‚   â””â”€â”€ datastore/
â”‚   â”‚   â”‚       â””â”€â”€ UserPreferences.kt        # DataStore preferences
â”‚   â”‚   â”‚
â”‚   â”‚   â”œâ”€â”€ remote/
â”‚   â”‚   â”‚   â”œâ”€â”€ api/
â”‚   â”‚   â”‚   â”‚   â””â”€â”€ GeminiService.kt          # API service
â”‚   â”‚   â”‚   â””â”€â”€ dto/
â”‚   â”‚   â”‚       â””â”€â”€ GeminiDto.kt              # Request/Response DTOs
â”‚   â”‚   â”‚
â”‚   â”‚   â””â”€â”€ repository/
â”‚   â”‚       â”œâ”€â”€ NoteRepositoryImpl.kt         # Repository implementation
â”‚   â”‚       â””â”€â”€ AIRepositoryImpl.kt           # AI repository implementation
â”‚   â”‚
â”‚   â”œâ”€â”€ domain/                               # Domain Layer (Pure Kotlin)
â”‚   â”‚   â”œâ”€â”€ model/
â”‚   â”‚   â”‚   â””â”€â”€ Note.kt                       # Domain model
â”‚   â”‚   â”‚
â”‚   â”‚   â”œâ”€â”€ repository/
â”‚   â”‚   â”‚   â”œâ”€â”€ NoteRepository.kt             # Repository interface
â”‚   â”‚   â”‚   â””â”€â”€ AIRepository.kt               # AI repository interface
â”‚   â”‚   â”‚
â”‚   â”‚   â””â”€â”€ usecase/
â”‚   â”‚       â””â”€â”€ NoteUseCases.kt               # Business logic
â”‚   â”‚
â”‚   â”œâ”€â”€ presentation/                         # Presentation Layer
â”‚   â”‚   â”œâ”€â”€ navigation/
â”‚   â”‚   â”‚   â”œâ”€â”€ Routes.kt                     # Navigation routes
â”‚   â”‚   â”‚   â””â”€â”€ AppNavHost.kt                 # Navigation host
â”‚   â”‚   â”‚
â”‚   â”‚   â”œâ”€â”€ screens/
â”‚   â”‚   â”‚   â”œâ”€â”€ home/
â”‚   â”‚   â”‚   â”‚   â”œâ”€â”€ HomeViewModel.kt
â”‚   â”‚   â”‚   â”‚   â””â”€â”€ HomeScreen.kt
â”‚   â”‚   â”‚   â”œâ”€â”€ addnote/
â”‚   â”‚   â”‚   â”‚   â”œâ”€â”€ AddBookViewModel.kt
â”‚   â”‚   â”‚   â”‚   â””â”€â”€ AddBookScreen.kt
â”‚   â”‚   â”‚   â”œâ”€â”€ detail/
â”‚   â”‚   â”‚   â”‚   â”œâ”€â”€ BookDetailViewModel.kt
â”‚   â”‚   â”‚   â”‚   â””â”€â”€ BookDetailScreen.kt
â”‚   â”‚   â”‚   â””â”€â”€ ai/
â”‚   â”‚   â”‚       â”œâ”€â”€ AIAssistantViewModel.kt
â”‚   â”‚   â”‚       â””â”€â”€ AIAssistantScreen.kt
â”‚   â”‚   â”‚
â”‚   â”‚   â”œâ”€â”€ components/
â”‚   â”‚   â”‚   â””â”€â”€ NoteComponents.kt             # Reusable UI components
â”‚   â”‚   â”‚
â”‚   â”‚   â””â”€â”€ theme/
â”‚   â”‚       â””â”€â”€ Theme.kt                      # Material theme
â”‚   â”‚
â”‚   â””â”€â”€ App.kt                                # App entry point
â”‚
â”œâ”€â”€ commonMain/sqldelight/                    # SQLDelight schema
â”‚   â””â”€â”€ com/example/bookku/
â”‚       â””â”€â”€ Note.sq                           # Database schema & queries
â”‚
â”œâ”€â”€ commonTest/kotlin/                        # Shared tests
â”‚   â””â”€â”€ com/example/bookku/
â”‚       â”œâ”€â”€ data/repository/
â”‚       â”‚   â””â”€â”€ NoteRepositoryTest.kt
â”‚       â””â”€â”€ presentation/
â”‚           â””â”€â”€ HomeViewModelTest.kt
â”‚
â”œâ”€â”€ androidMain/kotlin/                       # Android-specific
â”‚   â””â”€â”€ com/example/bookku/
â”‚       â”œâ”€â”€ MainActivity.kt
â”‚       â”œâ”€â”€ bookkuApplication.kt
â”‚       â””â”€â”€ core/
â”‚           â”œâ”€â”€ di/AndroidModule.kt           # actual: Android DI
â”‚           â”œâ”€â”€ network/ApiConfig.android.kt  # actual: BuildConfig
â”‚           â””â”€â”€ util/DatabaseDriverFactory.android.kt  # actual: Android driver
â”‚
â””â”€â”€ iosMain/kotlin/                           # iOS-specific
    â””â”€â”€ com/example/bookku/
        â”œâ”€â”€ MainViewController.kt
        â””â”€â”€ core/
            â”œâ”€â”€ di/IosModule.kt               # actual: iOS DI
            â”œâ”€â”€ network/ApiConfig.ios.kt      # actual: Info.plist
            â””â”€â”€ util/DatabaseDriverFactory.ios.kt  # actual: Native driver
```

---

## ðŸ”‘ Penjelasan Setiap Layer

### 1. Domain Layer (Paling Dalam)

**Karakteristik:**
- Pure Kotlin (tidak ada dependency ke framework)
- Berisi business logic
- Tidak tahu tentang database atau API

**Models (`domain/model/`)**
```kotlin
// Domain model - representasi data dalam aplikasi
data class Note(
    val id: Long = 0,
    val title: String,
    val content: String,
    val category: BookGenre,
    // ... pure data, no framework dependencies
)
```

**Repository Interface (`domain/repository/`)**
```kotlin
// Contract - mendefinisikan operasi yang tersedia
interface NoteRepository {
    fun getAllNotes(): Flow<List<Note>>
    suspend fun addBook(note: Note): Long
    // ... tanpa implementation details
}
```

**Use Cases (`domain/usecase/`)**
```kotlin
// Business logic yang spesifik
class GetAllNotesUseCase(
    private val repository: NoteRepository
) {
    operator fun invoke(sortBy: NoteSortBy): Flow<List<Note>> {
        return repository.getAllNotes().map { notes ->
            // Business logic: sorting, filtering, etc.
            sortNotes(notes, sortBy)
        }
    }
}
```

### 2. Data Layer (Tengah)

**Karakteristik:**
- Implementasi repository
- Berinteraksi dengan database dan API
- Mapping antara entity dan domain model

**Entity & Mapper (`data/local/entity/`)**
```kotlin
// Mapper: Entity (database) â†” Domain Model
fun BookEntity.toDomain(): Note {
    return Note(
        id = id,
        title = title,
        // ... mapping
    )
}
```

**Repository Implementation (`data/repository/`)**
```kotlin
class NoteRepositoryImpl(
    private val database: BookDatabase
) : NoteRepository {
    
    override fun getAllNotes(): Flow<List<Note>> {
        // Implementation: query database, map to domain
        return database.noteQueries.getAllNotes()
            .asFlow()
            .mapToList()
            .map { entities -> entities.toDomainList() }
    }
}
```

**Remote API (`data/remote/`)**
```kotlin
// DTO: Data Transfer Object untuk API
@Serializable
data class GeminiRequest(
    val contents: List<GeminiContent>,
    // ... untuk serialization
)

// Service: Komunikasi dengan API
class GeminiService(private val client: HttpClient) {
    suspend fun generateContent(prompt: String): Result<String> {
        // API call implementation
    }
}
```

### 3. Presentation Layer (Paling Luar)

**Karakteristik:**
- UI dengan Compose
- ViewModel dengan StateFlow
- Event handling

**ViewModel (`presentation/screens/*/`)**
```kotlin
class HomeViewModel(
    private val getAllNotesUseCase: GetAllNotesUseCase
) : ViewModel() {
    
    // UI State menggunakan StateFlow
    val uiState: StateFlow<HomeUiState> = getAllNotesUseCase()
        .map { notes -> HomeUiState.Success(notes) }
        .stateIn(viewModelScope, ...)
    
    // Handle user actions
    fun onSearchQueryChange(query: String) { ... }
}

// Sealed interface untuk UI State
sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val notes: List<Note>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
```

**Screen (`presentation/screens/*/`)**
```kotlin
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel()
) {
    // Collect state
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Render based on state
    when (val state = uiState) {
        is HomeUiState.Loading -> LoadingIndicator()
        is HomeUiState.Success -> NotesList(state.notes)
        is HomeUiState.Error -> ErrorMessage(state.message)
    }
}
```

---

## ðŸ”„ Dependency Flow

```
â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”
â”‚                    DEPENDENCY DIRECTION                      â”‚
â”‚                                                              â”‚
â”‚   Presentation â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â–º Domain â—„â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€ Data           â”‚
â”‚       â”‚                      â”‚                    â”‚          â”‚
â”‚       â”‚                      â”‚                    â”‚          â”‚
â”‚   Knows about:           Knows about:        Knows about:    â”‚
â”‚   - Domain models        - Nothing else      - Domain        â”‚
â”‚   - Use cases            - Pure Kotlin       - Frameworks    â”‚
â”‚   - Compose                                  - Database      â”‚
â”‚   - Navigation                               - Network       â”‚
â”‚                                                              â”‚
â”‚   TIDAK knows:           TIDAK knows:        TIDAK knows:    â”‚
â”‚   - Data layer           - Data layer        - Presentation  â”‚
â”‚   - Database             - Presentation      - UI            â”‚
â”‚   - Network              - Frameworks                        â”‚
â”‚                                                              â”‚
â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜
```

---

## ðŸ§© expect/actual Pattern

Pattern untuk kode platform-specific:

```kotlin
// â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•
// commonMain - EXPECT (Declaration only, no implementation)
// â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•

// File: commonMain/.../ApiConfig.kt
expect object ApiConfig {
    val geminiApiKey: String
}

// â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•
// androidMain - ACTUAL (Android implementation)
// â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•

// File: androidMain/.../ApiConfig.android.kt
actual object ApiConfig {
    actual val geminiApiKey: String = BuildConfig.GEMINI_API_KEY
}

// â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•
// iosMain - ACTUAL (iOS implementation)
// â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•

// File: iosMain/.../ApiConfig.ios.kt
actual object ApiConfig {
    actual val geminiApiKey: String
        get() = NSBundle.mainBundle.objectForInfoDictionaryKey("GEMINI_API_KEY") as? String ?: ""
}
```

---

## ðŸ’‰ Dependency Injection dengan Koin

```kotlin
// â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•
// Module Definitions
// â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•

// Network Module
val networkModule = module {
    single { HttpClientFactory.create() }    // Singleton
    singleOf(::GeminiService)                // Auto-inject dependencies
}

// Repository Module
val repositoryModule = module {
    singleOf(::NoteRepositoryImpl) bind NoteRepository::class
    //       â†‘ Implementation        â†‘ Interface (for injection)
}

// ViewModel Module
val viewModelModule = module {
    viewModelOf(::HomeViewModel)             // Scoped to lifecycle
    viewModelOf(::AddBookViewModel)
}

// â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•
// Initialization
// â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•

// Android
class bookkuApplication : Application() {
    override fun onCreate() {
        initKoin(platformModules = listOf(androidModule)) {
            androidContext(this@bookkuApplication)
        }
    }
}

// iOS
fun initKoinIOS() {
    initKoin(platformModules = listOf(iosModule))
}

// â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•
// Usage in ViewModel/Screen
// â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel()  // Auto-injected!
) {
    // ...
}
```

---

## ðŸ§ª Testing Structure

```kotlin
// â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•
// Fake Repository untuk Testing
// â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•

class FakeNoteRepository : NoteRepository {
    private val notes = MutableStateFlow<List<Note>>(emptyList())
    
    override fun getAllNotes(): Flow<List<Note>> = notes
    
    override suspend fun addBook(note: Note): Long {
        notes.update { it + note.copy(id = nextId++) }
        return nextId
    }
}

// â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•
// Unit Test dengan Turbine (Flow testing)
// â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•

class NoteRepositoryTest {
    
    @Test
    fun `addBook should add note to list`() = runTest {
        // Arrange
        val repository = FakeNoteRepository()
        
        // Act
        repository.addBook(Note(title = "Test"))
        
        // Assert dengan Turbine
        repository.getAllNotes().test {
            val notes = awaitItem()
            assertEquals(1, notes.size)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
```

---

## ðŸ“Š Data Flow Example

Contoh: User menambah note baru

```
â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”
â”‚ 1. USER ACTION                                                 â”‚
â”‚    User tap "Save" button di AddBookScreen                     â”‚
â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜
                              â”‚
                              â–¼
â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”
â”‚ 2. SCREEN                                                      â”‚
â”‚    onClick = { viewModel.saveNote() }                          â”‚
â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜
                              â”‚
                              â–¼
â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”
â”‚ 3. VIEWMODEL                                                   â”‚
â”‚    fun saveNote() {                                            â”‚
â”‚        viewModelScope.launch {                                 â”‚
â”‚            saveNoteUseCase(note)                               â”‚
â”‚        }                                                       â”‚
â”‚    }                                                           â”‚
â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜
                              â”‚
                              â–¼
â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”
â”‚ 4. USE CASE                                                    â”‚
â”‚    suspend operator fun invoke(note: Note): Result<Long> {     â”‚
â”‚        // Validation                                           â”‚
â”‚        if (note.isEmpty) return Result.failure(...)            â”‚
â”‚        // Delegate to repository                               â”‚
â”‚        return repository.addBook(note)                      â”‚
â”‚    }                                                           â”‚
â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜
                              â”‚
                              â–¼
â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”
â”‚ 5. REPOSITORY IMPLEMENTATION                                   â”‚
â”‚    override suspend fun addBook(note: Note): Long {         â”‚
â”‚        val values = note.toEntityValues()                      â”‚
â”‚        queries.addBook(...)                                 â”‚
â”‚        return queries.lastInsertId().executeAsOne()            â”‚
â”‚    }                                                           â”‚
â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜
                              â”‚
                              â–¼
â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”
â”‚ 6. DATABASE (SQLDelight)                                       â”‚
â”‚    INSERT INTO BookEntity (title, content, ...) VALUES (...)   â”‚
â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜
                              â”‚
                              â–¼
â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”
â”‚ 7. FLOW UPDATE                                                 â”‚
â”‚    getAllNotes query otomatis emit data baru                   â”‚
â”‚    UI ter-update karena collect StateFlow                      â”‚
â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜
```

---

## ðŸŽ¯ Best Practices

### 1. Naming Conventions

| Type | Convention | Example |
|------|------------|---------|
| Package | lowercase | `com.example.bookku.domain` |
| Class | PascalCase | `NoteRepository`, `HomeViewModel` |
| Function | camelCase | `getAllNotes()`, `onSaveClick()` |
| Variable | camelCase | `noteList`, `isLoading` |
| Constant | SCREAMING_SNAKE | `MAX_TITLE_LENGTH` |
| File | PascalCase.kt | `NoteRepository.kt` |

### 2. File Organization

```kotlin
// Urutan dalam file:
class HomeViewModel(
    // 1. Constructor parameters
    private val repository: NoteRepository
) : ViewModel() {
    
    // 2. Constants
    companion object {
        private const val DEBOUNCE_MS = 300L
    }
    
    // 3. Private state
    private val _searchQuery = MutableStateFlow("")
    
    // 4. Public state
    val uiState: StateFlow<HomeUiState> = ...
    
    // 5. Public functions
    fun onSearchQueryChange(query: String) { ... }
    
    // 6. Private functions
    private fun sortNotes(notes: List<Note>): List<Note> { ... }
}
```

### 3. UI State Pattern

```kotlin
// Sealed interface untuk semua kemungkinan state
sealed interface UiState {
    data object Loading : UiState
    data class Success(val data: Data) : UiState
    data class Error(val message: String) : UiState
}

// Gunakan when expression untuk handle semua state
when (val state = uiState) {
    is UiState.Loading -> LoadingIndicator()
    is UiState.Success -> Content(state.data)
    is UiState.Error -> ErrorMessage(state.message)
}
```

---

*Dokumen ini adalah bagian dari template project Pengembangan Aplikasi Mobile - ITERA*


