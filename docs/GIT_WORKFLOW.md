# 🌿 Git Workflow & Branching Strategy

Dokumen ini menjelaskan cara menggunakan Git untuk project ini, termasuk strategi branching yang harus diikuti setiap mahasiswa.

---

## 📌 Konsep Dasar

### Repository Structure

```
┌─────────────────────────────────────────────────────────────┐
│                    REPOSITORY TEMPLATE                      │
│                      (Dosen/Asisten)                        │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ Fork
                              ▼
┌──────────────────────────────────────────────────────────────┐
│                   FORKED REPOSITORY                          │
│                   (Salah satu anggota kelompok)              │
│                                                              │
│  main ─────────────────────────────────────────────────────  │
│    │                                                         │
│    ├── project/121140001-TodoMaster (individu) ──────────────│
│    │        │                                                │
│    │        ├── feature/add-task-screen                      │
│    │        └── fix/database-crash                           │
│    │                                                         │
│    ├── project/121140003-121140004-FitnessApp (2 orang) ─────│
│    │        │                                                │
│    │        ├── feature/workout-tracker                      │
│    │        └── feature/progress-chart                       │
│    │                                                         │
│    └── project/121140007-121140008-121140009-StudyPlanner ───│
│             │            (3 orang)                           │
│             ├── feature/schedule-view                        │
│             └── feature/reminder                             │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

> **📝 Note untuk Kelompok:** 
> - Cukup **1 orang yang fork** repository
> - Anggota lain di-invite sebagai **collaborator** di Settings > Collaborators
> - Semua anggota clone dari repository yang sudah di-fork

---

## 🏷️ Branch Naming Convention

### Format Wajib

```
project/[Kelompok]-[NamaAplikasi]
```

**Format Kelompok:** `NIM1` atau `NIM1-NIM2` atau `NIM1-NIM2-NIM3`

### Contoh Branch Project

**Kelompok 1 Orang (Individu):**
| NIM | Nama Aplikasi | Branch Name |
|-----|---------------|-------------|
| 121140001 | Todo Master | `project/121140001-TodoMaster` |
| 121140002 | Expense Tracker | `project/121140002-ExpenseTracker` |

**Kelompok 2 Orang:**
| NIM Anggota | Nama Aplikasi | Branch Name |
|-------------|---------------|-------------|
| 121140003, 121140004 | Fitness App | `project/121140003-121140004-FitnessApp` |
| 121140005, 121140006 | Recipe Book | `project/121140005-121140006-RecipeBook` |

**Kelompok 3 Orang:**
| NIM Anggota | Nama Aplikasi | Branch Name |
|-------------|---------------|-------------|
| 121140007, 121140008, 121140009 | Study Planner | `project/121140007-121140008-121140009-StudyPlanner` |
| 121140010, 121140011, 121140012 | Health Tracker | `project/121140010-121140011-121140012-HealthTracker` |

### Aturan Penamaan
- Gunakan **NIM lengkap** semua anggota (tanpa spasi)
- NIM dipisahkan dengan **dash (-)** 
- NIM diurutkan dari **kecil ke besar**
- Nama aplikasi menggunakan **PascalCase** (huruf besar di awal setiap kata)
- **Tidak boleh** mengandung spasi atau karakter khusus selain dash
- **Tidak boleh** sama dengan kelompok lain

---

## 🔄 Workflow Step-by-Step

### 1. Fork Repository (Sekali di Awal)

```bash
# Di GitHub:
# 1. Buka repository template
# 2. Klik "Fork"
# 3. Pilih akun Anda
```

### 2. Clone Forked Repository

```bash
# Clone ke komputer lokal
git clone https://github.com/USERNAME_ANDA/NoteAI-KMP.git

# Masuk ke folder
cd NoteAI-KMP

# Cek remote
git remote -v
# Output:
# origin  https://github.com/USERNAME_ANDA/NoteAI-KMP.git (fetch)
# origin  https://github.com/USERNAME_ANDA/NoteAI-KMP.git (push)
```

### 3. Tambahkan Upstream Remote

```bash
# Tambahkan remote ke repository template (untuk sync update)
git remote add upstream https://github.com/DOSEN/NoteAI-KMP-Template.git

# Verifikasi
git remote -v
# Output:
# origin    https://github.com/USERNAME_ANDA/NoteAI-KMP.git (fetch)
# origin    https://github.com/USERNAME_ANDA/NoteAI-KMP.git (push)
# upstream  https://github.com/DOSEN/NoteAI-KMP-Template.git (fetch)
# upstream  https://github.com/DOSEN/NoteAI-KMP-Template.git (push)
```

### 4. Buat Branch Project

```bash
# Pastikan di branch main
git checkout main

# Buat branch project baru (sesuaikan dengan kelompok Anda)

# Contoh Individu:
git checkout -b project/121140001-TodoMaster

# Contoh Kelompok 2 orang:
git checkout -b project/121140003-121140004-FitnessApp

# Contoh Kelompok 3 orang:
git checkout -b project/121140007-121140008-121140009-StudyPlanner

# Verifikasi branch aktif
git branch
```

### 5. Daily Workflow (untuk Kerja Kelompok)

```bash
# 1. SEBELUM mulai coding, SELALU pull dulu!
git pull origin project/121140003-121140004-FitnessApp

# 2. Pastikan di branch project kelompok
git checkout project/121140003-121140004-FitnessApp

# 3. Coding... (buat perubahan)

# 4. Cek status perubahan
git status

# 5. Add perubahan
git add .
# atau untuk file spesifik:
git add path/to/file.kt

# 6. Commit dengan message deskriptif
git commit -m "feat: add task list screen with filter"

# 7. Push ke GitHub
git push origin project/121140003-121140004-FitnessApp
```

### ⚠️ Tips Kolaborasi Kelompok

1. **Selalu pull sebelum mulai coding** untuk mendapatkan perubahan terbaru dari anggota lain
2. **Komunikasikan** file yang sedang dikerjakan untuk menghindari conflict
3. **Commit dan push secara berkala** agar anggota lain bisa melihat progress
4. **Bagi tugas berdasarkan file/folder** untuk meminimalkan conflict
5. **Review commit anggota lain** untuk memahami perubahan yang dibuat

---

## 📝 Commit Message Convention

### Format

```
<type>: <description>

[optional body]
```

### Types

| Type | Kapan Digunakan | Contoh |
|------|-----------------|--------|
| `feat` | Menambah fitur baru | `feat: add search functionality` |
| `fix` | Memperbaiki bug | `fix: resolve crash on empty list` |
| `refactor` | Refactoring kode | `refactor: extract repository logic` |
| `style` | Perubahan UI/styling | `style: update button colors` |
| `docs` | Update dokumentasi | `docs: add API documentation` |
| `test` | Menambah/update test | `test: add unit tests for ViewModel` |
| `chore` | Maintenance | `chore: update dependencies` |

### Contoh Commit Messages

```bash
# ✅ BAIK
git commit -m "feat: implement task creation with validation"
git commit -m "fix: resolve database migration issue"
git commit -m "refactor: move API calls to repository"
git commit -m "style: improve task card design"
git commit -m "test: add HomeViewModel unit tests"
git commit -m "docs: update README with setup instructions"

# ❌ BURUK
git commit -m "update"
git commit -m "fix bug"
git commit -m "changes"
git commit -m "asdfgh"
git commit -m "WIP"
```

---

## 🌿 Feature Branches (Opsional tapi Direkomendasikan)

Untuk fitur besar, gunakan feature branches:

```bash
# Dari branch project, buat feature branch
git checkout project/121140001-TodoMaster
git checkout -b feature/add-notification

# Coding...
git add .
git commit -m "feat: add notification service"

# Merge kembali ke branch project
git checkout project/121140001-TodoMaster
git merge feature/add-notification

# Hapus feature branch (opsional)
git branch -d feature/add-notification
```

### Feature Branch Naming

```
feature/[nama-fitur]
fix/[nama-bug]
refactor/[nama-refactor]
```

Contoh:
- `feature/dark-mode`
- `feature/export-pdf`
- `fix/login-crash`
- `refactor/clean-architecture`

---

## 🔄 Sync dengan Template Updates

Jika dosen mengupdate template:

```bash
# 1. Fetch updates dari upstream
git fetch upstream

# 2. Checkout ke main
git checkout main

# 3. Merge updates
git merge upstream/main

# 4. Push ke fork Anda
git push origin main

# 5. Update branch project Anda (hati-hati conflict!)
git checkout project/121140001-TodoMaster
git merge main

# 6. Resolve conflicts jika ada, lalu
git push origin project/121140001-TodoMaster
```

---

## ⚠️ Hal yang Harus Dihindari

### 1. JANGAN Push ke Main
```bash
# ❌ JANGAN LAKUKAN INI
git checkout main
git add .
git commit -m "my changes"
git push origin main

# ✅ LAKUKAN INI
git checkout project/121140001-TodoMaster
git add .
git commit -m "feat: my changes"
git push origin project/121140001-TodoMaster
```

### 2. JANGAN Commit File Sensitif
```bash
# File yang TIDAK BOLEH di-commit:
local.properties          # API keys
*.keystore               # Signing keys
google-services.json     # Firebase config
.env                     # Environment variables
```

### 3. JANGAN Force Push
```bash
# ❌ SANGAT BERBAHAYA
git push --force

# Jika terpaksa (konsultasi dulu):
git push --force-with-lease
```

---

## 📊 Git History yang Baik

### Contoh History yang Baik
```
* abc1234 feat: add AI summarize feature
* def5678 test: add unit tests for AIRepository
* ghi9012 feat: implement Gemini API integration
* jkl3456 refactor: extract AI logic to use case
* mno7890 feat: add task detail screen
* pqr1234 fix: resolve navigation crash
* stu5678 feat: implement task list with CRUD
* vwx9012 chore: initial project setup
```

### Contoh History yang Buruk
```
* abc1234 fix
* def5678 update
* ghi9012 changes
* jkl3456 asdf
* mno7890 WIP
* pqr1234 test
* stu5678 ...
```

---

## 🛠️ Git Commands Cheat Sheet

### Basic Commands
```bash
git status                  # Cek status
git add .                   # Add semua perubahan
git add file.kt             # Add file spesifik
git commit -m "message"     # Commit
git push                    # Push ke remote
git pull                    # Pull dari remote
```

### Branch Commands
```bash
git branch                  # List branches
git branch -a               # List semua branches (termasuk remote)
git checkout branch-name    # Pindah branch
git checkout -b new-branch  # Buat & pindah ke branch baru
git branch -d branch-name   # Hapus branch
```

### History Commands
```bash
git log                     # Lihat history
git log --oneline          # History ringkas
git log --graph            # History dengan graph
git diff                   # Lihat perubahan
```

### Undo Commands
```bash
git checkout -- file.kt    # Undo perubahan file
git reset HEAD file.kt     # Unstage file
git reset --soft HEAD~1    # Undo commit terakhir (keep changes)
git reset --hard HEAD~1    # Undo commit terakhir (discard changes)
```

---

## 📋 Checklist Sebelum Push

- [ ] Semua file yang diperlukan sudah di-add
- [ ] Tidak ada file sensitif yang ter-commit
- [ ] Commit message mengikuti convention
- [ ] Code sudah di-test dan berjalan
- [ ] Tidak ada error/warning yang diabaikan
- [ ] Branch yang benar sudah di-checkout

---

## 🆘 Troubleshooting

### "Permission denied" saat push
```bash
# Cek remote URL
git remote -v

# Jika menggunakan HTTPS, pastikan credential benar
# Atau gunakan SSH:
git remote set-url origin git@github.com:USERNAME/REPO.git
```

### Conflict saat merge
```bash
# 1. Buka file yang conflict
# 2. Cari marker conflict:
<<<<<<< HEAD
your changes
=======
their changes
>>>>>>> branch-name

# 3. Edit file, hapus marker, pilih perubahan yang benar
# 4. Add dan commit
git add .
git commit -m "fix: resolve merge conflict"
```

### Lupa checkout branch
```bash
# Jika sudah commit di branch yang salah:
# 1. Catat commit hash
git log --oneline -1

# 2. Reset branch saat ini
git reset --hard HEAD~1

# 3. Checkout ke branch yang benar
git checkout project/121140001-TodoMaster

# 4. Cherry-pick commit
git cherry-pick <commit-hash>
```

---

*Dokumen ini adalah bagian dari template project Pengembangan Aplikasi Mobile - ITERA*
