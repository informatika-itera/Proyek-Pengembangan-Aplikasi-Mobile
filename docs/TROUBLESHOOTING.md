# 🔧 Troubleshooting Guide

Panduan untuk mengatasi masalah umum yang mungkin ditemui saat mengerjakan project.

---

## 🚨 Masalah Umum & Solusi

### 1. Gradle Sync Failed

**Gejala:**
```
Gradle sync failed: Could not resolve all dependencies
```

**Solusi:**
```bash
# 1. Invalidate caches
# Android Studio > File > Invalidate Caches > Invalidate and Restart

# 2. Clean project
./gradlew clean

# 3. Delete .gradle folder
rm -rf ~/.gradle/caches
rm -rf .gradle

# 4. Sync ulang
# Android Studio > File > Sync Project with Gradle Files
```

---

### 2. API Key Not Found / Empty

**Gejala:**
```
GEMINI_API_KEY is empty
API call failed: 401 Unauthorized
```

**Solusi:**

1. Pastikan file `local.properties` ada di root project:
```properties
sdk.dir=/path/to/android/sdk
GEMINI_API_KEY=your_actual_api_key_here
```

2. Pastikan API key valid:
   - Buka https://aistudio.google.com/
   - Cek API key masih aktif
   - Generate key baru jika perlu

3. Rebuild project:
```bash
./gradlew clean build
```

---

### 3. Database Migration Error

**Gejala:**
```
android.database.sqlite.SQLiteException: no such table: NoteEntity
Database version mismatch
```

**Solusi:**

**Opsi A: Clear App Data (Development)**
- Uninstall app dari emulator/device
- Install ulang

**Opsi B: Implement Migration (Production)**
```kotlin
// Di DatabaseDriverFactory
val driver = AndroidSqliteDriver(
    schema = FitGenDatabase.Schema,
    context = context,
    name = "fitgen.db",
    callback = object : AndroidSqliteDriver.Callback(NoteDatabase.Schema) {
        override fun onUpgrade(
            db: SupportSQLiteDatabase,
            oldVersion: Int,
            newVersion: Int
        ) {
            // Migration logic
        }
    }
)
```

---

### 4. Compose Preview Not Working

**Gejala:**
```
Preview tidak muncul
"Render problem" di Android Studio
```

**Solusi:**

1. Pastikan menggunakan Android Studio versi terbaru
2. Tambahkan `@Preview` annotation:
```kotlin
@Preview
@Composable
fun NoteCardPreview() {
    NoteAITheme {
        NoteCard(
            note = Note(title = "Preview", content = "Test"),
            onClick = {},
            onPinClick = {},
            onDeleteClick = {}
        )
    }
}
```

3. Build project:
```bash
./gradlew :composeApp:assembleDebug
```

---

### 5. Koin Dependency Not Found

**Gejala:**
```
org.koin.core.error.NoBeanDefFoundException
No definition found for class 'NoteRepository'
```

**Solusi:**

1. Pastikan module terdaftar:
```kotlin
// AppModule.kt
val repositoryModule = module {
    singleOf(::NoteRepositoryImpl) bind NoteRepository::class
}

val sharedModules = listOf(
    networkModule,
    databaseModule,
    repositoryModule,  // ← Pastikan ada
    viewModelModule
)
```

2. Pastikan initKoin dipanggil:
```kotlin
// Android: Application class
override fun onCreate() {
    super.onCreate()
    initKoin(platformModules = listOf(androidModule)) {
        androidContext(this@NoteAIApplication)
    }
}
```

---

### 6. iOS Build Failed

**Gejala:**
```
Xcode build failed
Framework not found
```

**Solusi:**

1. Generate framework:
```bash
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
```

2. Di Xcode:
   - Clean Build Folder (Cmd+Shift+K)
   - Build ulang (Cmd+B)

3. Pastikan Cocoapods terinstall:
```bash
sudo gem install cocoapods
cd iosApp
pod install
```

---

### 7. Network Request Failed

**Gejala:**
```
java.net.UnknownHostException
Connection timeout
```

**Solusi:**

1. Cek internet permission (Android):
```xml
<!-- AndroidManifest.xml -->
<uses-permission android:name="android.permission.INTERNET" />
```

2. Cek URL endpoint:
```kotlin
// Pastikan URL benar
private val baseUrl = "https://generativelanguage.googleapis.com/v1beta"
```

3. Tambahkan logging:
```kotlin
install(Logging) {
    level = LogLevel.ALL  // Untuk debugging
}
```

---

### 8. StateFlow Not Updating UI

**Gejala:**
- Data berubah tapi UI tidak update
- `collectAsState` tidak reactive

**Solusi:**

1. Pastikan menggunakan `collectAsStateWithLifecycle`:
```kotlin
// ✅ BENAR
val uiState by viewModel.uiState.collectAsStateWithLifecycle()

// ❌ SALAH (untuk Android)
val uiState by viewModel.uiState.collectAsState()
```

2. Pastikan Flow emit di main dispatcher:
```kotlin
val uiState = repository.getAllNotes()
    .flowOn(Dispatchers.IO)
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
```

---

### 9. Git Push Rejected

**Gejala:**
```
! [rejected] main -> main (non-fast-forward)
error: failed to push some refs
```

**Solusi:**

```bash
# 1. Pull dulu
git pull origin project/121140001-TodoMaster --rebase

# 2. Resolve conflict jika ada

# 3. Push lagi
git push origin project/121140001-TodoMaster
```

**JANGAN gunakan `--force` kecuali benar-benar tahu apa yang dilakukan!**

---

### 10. Test Failed

**Gejala:**
```
Test failed: Expected <2> but was <1>
Coroutine test timeout
```

**Solusi:**

1. Untuk coroutine tests, gunakan `runTest`:
```kotlin
@Test
fun `test something`() = runTest {
    // test code
}
```

2. Untuk Flow tests dengan Turbine:
```kotlin
@Test
fun `test flow`() = runTest {
    repository.getAllNotes().test {
        val result = awaitItem()
        assertEquals(expected, result)
        cancelAndIgnoreRemainingEvents()
    }
}
```

3. Setup test dispatcher:
```kotlin
@BeforeTest
fun setup() {
    Dispatchers.setMain(StandardTestDispatcher())
}

@AfterTest
fun tearDown() {
    Dispatchers.resetMain()
}
```

---

## 🔍 Debugging Tips

### 1. Enable Verbose Logging

```kotlin
// Di HttpClientFactory
install(Logging) {
    logger = object : Logger {
        override fun log(message: String) {
            println("HTTP: $message")
        }
    }
    level = LogLevel.ALL
}
```

### 2. Debug StateFlow

```kotlin
val uiState = someFlow
    .onEach { println("DEBUG: $it") }  // Log setiap emit
    .stateIn(...)
```

### 3. Debug Compose Recomposition

```kotlin
@Composable
fun MyScreen() {
    SideEffect {
        println("MyScreen recomposed")
    }
    // ...
}
```

### 4. Android Studio Debugger

1. Set breakpoint (klik di gutter kiri)
2. Run > Debug (bukan Run)
3. Gunakan "Evaluate Expression" untuk inspect variables

---

## 📞 Mendapatkan Bantuan

### 1. Sebelum Bertanya

- [ ] Sudah baca error message dengan teliti
- [ ] Sudah search error di Google/StackOverflow
- [ ] Sudah cek dokumentasi resmi
- [ ] Sudah coba solusi dari Troubleshooting Guide ini

### 2. Format Pertanyaan yang Baik

```markdown
**Masalah:**
[Jelaskan masalah dengan singkat]

**Error Message:**
```
[Paste error message lengkap]
```

**Yang Sudah Dicoba:**
1. [Solusi 1]
2. [Solusi 2]

**Environment:**
- Android Studio: [versi]
- Kotlin: [versi]
- OS: [Windows/Mac/Linux]
```

### 3. Dimana Bertanya

1. **Issue di Repository** - Untuk bug atau pertanyaan teknis
2. **Grup Kelas** - Untuk pertanyaan umum
3. **Konsultasi Dosen** - Untuk masalah kompleks

---

## 📚 Resources

### Official Documentation
- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [SQLDelight](https://cashapp.github.io/sqldelight/)
- [Koin](https://insert-koin.io/docs/quickstart/kotlin)
- [Ktor](https://ktor.io/docs/welcome.html)

### Community
- [Kotlin Slack](https://kotlinlang.slack.com/)
- [Stack Overflow - Kotlin](https://stackoverflow.com/questions/tagged/kotlin)
- [Reddit - Kotlin](https://www.reddit.com/r/Kotlin/)

---

*Dokumen ini adalah bagian dari template project Pengembangan Aplikasi Mobile - ITERA*
