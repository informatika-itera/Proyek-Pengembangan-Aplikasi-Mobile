# 📜 Aturan Modifikasi Template

Dokumen ini menjelaskan **apa yang boleh dan tidak boleh** dimodifikasi dalam template project. Ikuti aturan ini untuk memastikan project Anda tetap terstruktur dengan baik.

---

## 🎯 Prinsip Utama

1. **Pertahankan Arsitektur** - Clean Architecture + MVVM harus tetap digunakan
2. **Gunakan Pattern yang Sama** - Repository, Use Case, ViewModel patterns
3. **Ikuti Konvensi Penamaan** - Konsisten dengan style yang sudah ada
4. **Dokumentasikan Perubahan** - Update README jika ada perubahan signifikan

---

## ✅ WAJIB Dimodifikasi

Bagian-bagian ini **HARUS** diubah sesuai project Anda:

### 1. Package Name & App Identity

```kotlin
// SEBELUM (Template)
package com.example.noteai

// SESUDAH (Project Anda)
package com.example.todomaster    // Sesuaikan dengan nama app
package com.example.expensetracker
package com.example.fitnessapp
```

**File yang perlu diubah:**
- `composeApp/build.gradle.kts` → `namespace` dan `applicationId`
- Semua file `.kt` → package declaration
- `AndroidManifest.xml` → package references

### 2. Domain Models

Ganti model `Note` dengan model sesuai aplikasi Anda:

```kotlin
// SEBELUM (Template)
data class Note(
    val id: Long,
    val title: String,
    val content: String,
    val category: NoteCategory,
    ...
)

// SESUDAH (Contoh: Todo App)
data class Task(
    val id: Long,
    val title: String,
    val description: String,
    val priority: TaskPriority,
    val dueDate: Instant?,
    val isCompleted: Boolean,
    ...
)

// SESUDAH (Contoh: Expense App)
data class Transaction(
    val id: Long,
    val amount: Double,
    val category: TransactionCategory,
    val date: Instant,
    val notes: String?,
    ...
)
```

### 3. SQLDelight Schema

Update schema database sesuai model baru:

```sql
-- SEBELUM (Note.sq)
CREATE TABLE NoteEntity (
    id INTEGER PRIMARY KEY,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    ...
);

-- SESUDAH (Task.sq - untuk Todo App)
CREATE TABLE TaskEntity (
    id INTEGER PRIMARY KEY,
    title TEXT NOT NULL,
    description TEXT,
    priority TEXT NOT NULL,
    due_date INTEGER,
    is_completed INTEGER NOT NULL DEFAULT 0,
    ...
);
```

### 4. Repository Interface & Implementation

```kotlin
// SEBELUM
interface NoteRepository {
    fun getAllNotes(): Flow<List<Note>>
    suspend fun insertNote(note: Note): Long
    ...
}

// SESUDAH
interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    fun getTasksByPriority(priority: TaskPriority): Flow<List<Task>>
    fun getCompletedTasks(): Flow<List<Task>>
    suspend fun insertTask(task: Task): Long
    suspend fun toggleComplete(id: Long)
    ...
}
```

### 5. Screens & ViewModels

Buat screens sesuai kebutuhan aplikasi:

```
SEBELUM (NoteAI):
├── screens/
│   ├── home/
│   ├── addnote/
│   ├── detail/
│   └── ai/

SESUDAH (Todo App):
├── screens/
│   ├── home/          → TaskListScreen
│   ├── addtask/       → AddTaskScreen
│   ├── calendar/      → CalendarScreen (BARU)
│   ├── statistics/    → StatisticsScreen (BARU)
│   └── ai/            → AIAssistantScreen (modifikasi)
```

### 6. Navigation Routes

```kotlin
// SEBELUM
sealed interface Route {
    data object Home : Route
    data class AddNote(val noteId: Long? = null) : Route
    data class NoteDetail(val noteId: Long) : Route
    ...
}

// SESUDAH
sealed interface Route {
    data object TaskList : Route
    data class AddTask(val taskId: Long? = null) : Route
    data class TaskDetail(val taskId: Long) : Route
    data object Calendar : Route          // BARU
    data object Statistics : Route        // BARU
    ...
}
```

### 7. App Theme & Branding

```kotlin
// Ubah warna sesuai branding aplikasi Anda
private val Primary = Color(0xFF6750A4)      // Ubah ke warna brand Anda
private val Secondary = Color(0xFF625B71)    // Ubah ke warna sekunder
```

### 8. README.md

Update README dengan:
- Nama aplikasi Anda
- Deskripsi fitur
- Screenshot
- Cara instalasi

---

## 🔧 BOLEH Dimodifikasi

Bagian-bagian ini **boleh** diubah sesuai kebutuhan:

### 1. Use Cases

Tambah/modifikasi use case sesuai kebutuhan:

```kotlin
// Boleh menambah use case baru
class GetOverdueTasksUseCase(
    private val repository: TaskRepository
) {
    operator fun invoke(): Flow<List<Task>> {
        return repository.getOverdueTasks()
    }
}

// Boleh memodifikasi logic use case
class CalculateStatisticsUseCase(
    private val repository: TaskRepository
) {
    // Custom business logic
}
```

### 2. AI Features

Boleh mengubah prompt dan fitur AI:

```kotlin
// Modifikasi system prompt
object SystemPrompts {
    val TASK_SUGGESTER = """
        Kamu adalah asisten produktivitas.
        Bantu user membuat task yang SMART.
        ...
    """.trimIndent()
    
    // Tambah prompt baru
    val DEADLINE_ADVISOR = """
        ...
    """.trimIndent()
}
```

### 3. UI Components

Boleh membuat komponen UI baru:

```kotlin
// Tambah komponen baru
@Composable
fun PriorityBadge(priority: TaskPriority) { ... }

@Composable
fun CalendarDay(date: LocalDate, tasks: List<Task>) { ... }

@Composable
fun ProgressChart(data: StatisticsData) { ... }
```

### 4. Dependencies

Boleh menambah library (dengan alasan yang jelas):

```kotlin
// Di libs.versions.toml
[libraries]
# Boleh ditambah:
chart-library = { ... }      # Untuk chart/grafik
calendar-library = { ... }   # Untuk kalender
notification-work = { ... }  # Untuk notification
```

### 5. Testing

Boleh dan **dianjurkan** menambah tests:

```kotlin
// Tambah test baru
class TaskRepositoryTest { ... }
class CalendarViewModelTest { ... }
class StatisticsUseCaseTest { ... }
```

---

## ❌ TIDAK BOLEH Dimodifikasi

Bagian-bagian ini **TIDAK BOLEH** diubah strukturnya:

### 1. Folder Structure (Arsitektur)

```
❌ TIDAK BOLEH mengubah struktur folder utama:

composeApp/src/commonMain/kotlin/
├── core/           ← JANGAN diubah strukturnya
│   ├── di/
│   ├── network/
│   └── util/
├── data/           ← JANGAN diubah strukturnya
│   ├── local/
│   ├── remote/
│   └── repository/
├── domain/         ← JANGAN diubah strukturnya
│   ├── model/
│   ├── repository/
│   └── usecase/
└── presentation/   ← JANGAN diubah strukturnya
    ├── navigation/
    ├── screens/
    ├── components/
    └── theme/
```

### 2. expect/actual Pattern

```kotlin
// JANGAN hapus atau ubah pattern expect/actual
// File expect di commonMain:
expect object ApiConfig {
    val geminiApiKey: String
}

// File actual di androidMain/iosMain:
actual object ApiConfig {
    actual val geminiApiKey: String = ...
}
```

### 3. Koin DI Setup

```kotlin
// JANGAN ubah cara setup Koin
// Boleh menambah module, tapi jangan ubah strukturnya

fun initKoin(
    platformModules: List<Module>,
    config: KoinAppDeclaration? = null
) {
    startKoin {
        config?.invoke(this)
        modules(platformModules + sharedModules)
    }
}
```

### 4. Build Configuration

```kotlin
// JANGAN ubah konfigurasi build yang kritis:
// - Kotlin version
// - Compose version
// - Target SDK configuration
// - Source set structure
```

### 5. Platform-Specific Entry Points

```kotlin
// JANGAN ubah struktur entry point:
// Android: MainActivity, Application class
// iOS: MainViewController

// Boleh menambah logic, tapi jangan ubah pattern
```

---

## 📋 Checklist Modifikasi

Sebelum mengubah template, pastikan:

### Untuk Setiap Perubahan Model:
- [ ] Update domain model (`domain/model/`)
- [ ] Update entity (`data/local/entity/`)
- [ ] Update mapper functions
- [ ] Update SQLDelight schema (`.sq` file)
- [ ] Update repository interface (`domain/repository/`)
- [ ] Update repository implementation (`data/repository/`)
- [ ] Update use cases (`domain/usecase/`)
- [ ] Update ViewModels
- [ ] Update UI Screens
- [ ] Update tests

### Untuk Menambah Screen Baru:
- [ ] Buat ViewModel di `screens/[nama]/`
- [ ] Buat Screen composable di `screens/[nama]/`
- [ ] Tambah Route di `navigation/Routes.kt`
- [ ] Tambah ke NavHost di `navigation/AppNavHost.kt`
- [ ] Buat UI tests jika perlu

### Untuk Menambah Fitur AI:
- [ ] Tambah method di `AIRepository` interface
- [ ] Implement di `AIRepositoryImpl`
- [ ] Buat use case jika diperlukan
- [ ] Tambah system prompt di `GeminiService`
- [ ] Update AI screen/ViewModel

---

## 🔍 Contoh Modifikasi yang Benar

### Mengubah dari NoteAI ke TodoMaster

#### Step 1: Rename Package
```bash
# Di Android Studio:
# Right-click package > Refactor > Rename
# com.example.fitgen → com.example.todomaster
```

#### Step 2: Update Model
```kotlin
// domain/model/Task.kt
data class Task(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val dueDate: Instant? = null,
    val isCompleted: Boolean = false,
    val createdAt: Instant = Clock.System.now()
)

enum class TaskPriority { LOW, MEDIUM, HIGH, URGENT }
```

#### Step 3: Update Schema
```sql
-- sqldelight/Task.sq
CREATE TABLE TaskEntity (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    description TEXT NOT NULL DEFAULT '',
    priority TEXT NOT NULL DEFAULT 'MEDIUM',
    due_date INTEGER,
    is_completed INTEGER NOT NULL DEFAULT 0,
    created_at INTEGER NOT NULL
);

getAllTasks:
SELECT * FROM TaskEntity ORDER BY is_completed ASC, due_date ASC;

getIncompleteTasks:
SELECT * FROM TaskEntity WHERE is_completed = 0 ORDER BY due_date ASC;
```

#### Step 4: Update Repository
```kotlin
// domain/repository/TaskRepository.kt
interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    fun getIncompleteTasks(): Flow<List<Task>>
    fun getTasksByPriority(priority: TaskPriority): Flow<List<Task>>
    fun getTaskById(id: Long): Flow<Task?>
    suspend fun insertTask(task: Task): Long
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(id: Long)
    suspend fun toggleComplete(id: Long)
}
```

#### Step 5: Implement Repository
```kotlin
// data/repository/TaskRepositoryImpl.kt
class TaskRepositoryImpl(
    private val database: TaskDatabase
) : TaskRepository {
    // Implementation...
}
```

#### Step 6: Update DI
```kotlin
// core/di/AppModule.kt
val repositoryModule = module {
    singleOf(::TaskRepositoryImpl) bind TaskRepository::class
}
```

---

## ⚠️ Peringatan

### Jangan Lakukan:
1. **Menghapus layer** - Semua layer (domain, data, presentation) harus ada
2. **Bypass repository** - ViewModel tidak boleh akses database langsung
3. **Hardcode API key** - Selalu gunakan `local.properties`
4. **Mengabaikan error** - Fix semua error sebelum commit
5. **Copy-paste tanpa paham** - Pastikan mengerti kode yang digunakan

### Harus Dilakukan:
1. **Test setiap perubahan** - Minimal manual test
2. **Commit secara berkala** - Jangan tunggu semua selesai
3. **Dokumentasikan perubahan** - Update README
4. **Ikuti naming convention** - Konsisten dengan template
5. **Review kode sendiri** - Cek sebelum push

---

*Dokumen ini adalah bagian dari template project Pengembangan Aplikasi Mobile - ITERA*
