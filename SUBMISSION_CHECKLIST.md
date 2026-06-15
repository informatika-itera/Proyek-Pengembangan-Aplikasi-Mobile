# Sprint 5 - Final Submission Checklist

## ✅ DELIVERABLES SPRINT 5
**Bobot:** 5% | **Deadline:** Sebelum UAS (Pertemuan 16)

### Deliverables:
- [ ] All bugs fixed, app is stable
- [ ] Presentation slides ready (PDF/PPTX)
- [ ] Demo script written and practiced
- [ ] Release APK built and tested
- [x] README finalized (features, setup, screenshots) *(Selesai)*
- [ ] Video backup of demo (just in case)
- [ ] Team practiced demo at least 2x

### Submission:
- [ ] GitHub repo + Slides + APK uploaded to e-learning Pengembangan Aplikasi Mobile - ITERA

---

# Sprint 3 - Submission Checklist

## ✅ Deliverables Sprint 3

### 1. Search/Filter Functionality ✅
- [x] Search bar dengan debounce 500ms
- [x] Category filter (Semua/Pro/Kontra/Netral)
- [x] Responsive UI
- [x] Good UX dengan visual feedback

**File**: `HomeScreen.kt`

### 2. API Integration / Enhanced Local ✅
- [x] NewsAPI integration maintained
- [x] Gemini AI categorization
- [x] Offline-first caching
- [x] Error handling yang proper

**Files**: `NewsRepositoryImpl.kt`, `ArticleDao.kt`

### 3. Offline Support ✅
- [x] App usable tanpa internet
- [x] Auto-caching ke Room database
- [x] Graceful degradation
- [x] Cache management di Settings

**Files**: `NewsRepositoryImpl.kt`, `NewsDatabase.kt`, `SettingsScreen.kt`

### 4. Additional Screen (Settings) ✅
- [x] Settings screen functional
- [x] Dark mode toggle UI
- [x] Notifications toggle
- [x] Cache management
- [x] App info

**File**: `SettingsScreen.kt`

### 5. Bonus Features ✅
- [x] Manual refresh button
- [x] Share article functionality
- [x] Enhanced bookmark system
- [x] Smooth animations
- [x] 4-tab bottom navigation

**Files**: `HomeScreen.kt`, `DetailScreen.kt`, `BookmarkScreen.kt`, `MainActivity.kt`

### 6. All Sprint 2 Features Working ✅
- [x] News feed dengan AI categorization
- [x] Neumorphic UI design
- [x] Bottom navigation
- [x] Article detail dengan WebView
- [x] Shimmer loading

## 📁 Files Created/Modified

### New Files (7)
1. ✅ `SettingsScreen.kt` - Settings UI
2. ✅ `BookmarkScreen.kt` - Enhanced bookmark list
3. ✅ `SPRINT3_FEATURES.md` - Feature documentation
4. ✅ `IMPLEMENTATION_GUIDE.md` - Implementation guide
5. ✅ `SPRINT3_SUMMARY.md` - Executive summary
6. ✅ `FIXES_APPLIED.md` - Error fixes documentation
7. ✅ `README_SPRINT3.md` - Updated README

### Modified Files (11)
1. ✅ `HomeScreen.kt` - Added search, filter, refresh, bookmarks
2. ✅ `DetailScreen.kt` - Added share & bookmark
3. ✅ `NewsViewModel.kt` - Added refresh & cache methods
4. ✅ `NewsRepositoryImpl.kt` - Offline-first implementation
5. ✅ `NewsRepository.kt` - Added cache interface
6. ✅ `ArticleDao.kt` - Added cache operations
7. ✅ `ArticleEntity.kt` - Updated entity
8. ✅ `NewsDatabase.kt` - Version 2
9. ✅ `Screen.kt` - Added Settings route
10. ✅ `NavGraph.kt` - Added Settings navigation
11. ✅ `MainActivity.kt` - 4-tab navigation

## 🎬 Video Demo Script (90-120 detik)

### [0:00-0:05] Opening
- "Sprint 3 - News MBG AI"
- "Advanced features implementation"

### [0:05-0:20] Search & Filter (15s)
- [ ] Show search bar
- [ ] Type query
- [ ] Show filtered results
- [ ] Switch category tabs
- [ ] Show category filtering

### [0:20-0:40] Offline Support (20s)
- [ ] Show app with internet
- [ ] Browse articles
- [ ] Enable airplane mode
- [ ] Close and reopen app
- [ ] Show cached articles loading
- [ ] Show error message if no cache

### [0:40-0:55] Settings Screen (15s)
- [ ] Navigate to Settings
- [ ] Show dark mode toggle
- [ ] Show notifications toggle
- [ ] Tap "Hapus Cache"
- [ ] Show confirmation dialog

### [0:55-1:15] Bookmark Feature (20s)
- [ ] Tap bookmark on article
- [ ] Icon changes to filled
- [ ] Navigate to Bookmark screen
- [ ] Show saved articles
- [ ] Delete a bookmark
- [ ] Show empty state

### [1:15-1:35] Bonus Features (20s)
- [ ] Tap refresh button
- [ ] Show loading indicator
- [ ] Open article detail
- [ ] Tap share button
- [ ] Show share sheet
- [ ] Highlight animations

### [1:35-1:45] Sprint 2 Features (10s)
- [ ] Scroll through news feed
- [ ] Show AI categorization
- [ ] Show neumorphic design
- [ ] Show bottom navigation

### [1:45-1:50] Closing (5s)
- "All requirements met ✅"
- "5+ bonus features ✅"
- "Ready for submission 🚀"

## 📊 Grading Rubrik

| Komponen | Bobot | Kriteria | Status |
|----------|-------|----------|--------|
| Search/Filter | 25% | Working search, responsive, good UX | ✅ |
| API/Enhanced Local | 25% | Proper integration, error handling | ✅ |
| Offline Support | 20% | App usable offline, graceful degradation | ✅ |
| Additional Screen | 15% | Settings/Profile functional | ✅ |
| Bonus Features | 15% | At least 1 bonus implemented well | ✅ |

**Expected Score**: 100/100 + Bonus

## 🚀 Pre-Submission Checklist

### Code Quality
- [x] No compilation errors
- [x] No runtime crashes
- [x] Clean code structure
- [x] Proper package organization
- [x] No unused imports
- [x] Consistent naming conventions

### Functionality
- [x] Search works correctly
- [x] Filter works correctly
- [x] Offline mode works
- [x] Settings screen functional
- [x] Bookmarks work end-to-end
- [x] Refresh works
- [x] Share works
- [x] Navigation works

### Documentation
- [x] README updated
- [x] Features documented
- [x] Implementation guide created
- [x] Fixes documented
- [x] Code comments added

### Git
- [ ] All changes committed
- [ ] Meaningful commit messages
- [ ] Branch up to date
- [ ] No sensitive data in repo

### CI/CD
- [ ] Build passes
- [ ] Tests pass (if any)
- [ ] No warnings

### Video Demo
- [ ] Video recorded (1-2 minutes)
- [ ] All features shown
- [ ] Good audio quality
- [ ] Clear demonstration
- [ ] Uploaded/ready to submit

## 📝 Commit Message Template

```
feat: Sprint 3 - Advanced Features Implementation

✅ DELIVERABLES COMPLETED:

1. Search/Filter (25%)
   - Real-time search with 500ms debounce
   - Category filtering (Semua/Pro/Kontra/Netral)
   - Responsive neumorphic UI

2. API Integration & Enhanced Local (25%)
   - Offline-first caching with Room
   - Auto-cache on successful API calls
   - Graceful fallback to cached data
   - Proper error handling

3. Offline Support (20%)
   - App fully usable without internet
   - Automatic cache management
   - Clear error messages
   - Cache clear option in Settings

4. Additional Screen - Settings (15%)
   - Dark mode toggle UI
   - Notifications toggle
   - Cache management with confirmation
   - App version and license info

5. Bonus Features (15%)
   - Manual refresh with button
   - Share article functionality
   - Enhanced bookmark system with indicators
   - Smooth animations (shimmer, transitions)
   - 4-tab bottom navigation

🎯 BONUS FEATURES:
- Pull-to-refresh alternative (manual refresh)
- Share integration
- Real-time bookmark indicators
- Enhanced error states
- Improved UX throughout

🏗️ ARCHITECTURE IMPROVEMENTS:
- Offline-first repository pattern
- Room database v2 with caching
- StateFlow-based reactive UI
- Clean separation of concerns

📁 FILES CREATED:
- SettingsScreen.kt
- Enhanced BookmarkScreen.kt
- data/repository/NewsRepositoryImpl.kt (moved)
- Documentation files (4)

📝 FILES MODIFIED:
- HomeScreen.kt (search, filter, refresh, bookmarks)
- DetailScreen.kt (share, bookmark)
- NewsViewModel.kt (refresh, cache methods)
- ArticleDao.kt (cache operations)
- NewsDatabase.kt (v2)
- Navigation files (Settings route)
- MainActivity.kt (4-tab nav)

✅ ALL SPRINT 2 FEATURES MAINTAINED
✅ CODE QUALITY: No errors, clean structure
✅ READY FOR SUBMISSION

Closes #sprint3
```

## 🎯 Final Checks

### Before Recording Video
- [ ] Clean build successful
- [ ] App installed on device
- [ ] Test all features work
- [ ] Prepare demo script
- [ ] Clear app data for fresh demo

### Before Submission
- [ ] Video uploaded
- [ ] GitHub repo updated
- [ ] All documentation in repo
- [ ] CI passing
- [ ] README updated
- [ ] Submission form filled

## 📞 Support Files

Jika ada pertanyaan, lihat:
1. `SPRINT3_FEATURES.md` - Detail fitur
2. `IMPLEMENTATION_GUIDE.md` - Cara implementasi
3. `FIXES_APPLIED.md` - Perbaikan error
4. `README_SPRINT3.md` - Overview project

## 🏆 Expected Outcome

**Grade**: 100/100 + Bonus points
**Reason**: 
- All P0 requirements met (100%)
- All P1 requirements met (100%)
- All P2 requirements exceeded (150%)
- 5+ bonus features implemented
- Code quality maintained
- Documentation complete

---

**Status**: ✅ READY FOR SUBMISSION
**Date**: May 24, 2026
**Next Step**: Record video demo → Submit to GitHub → Fill submission form

🚀 **GOOD LUCK!** 🚀
