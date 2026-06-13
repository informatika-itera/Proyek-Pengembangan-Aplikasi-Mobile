# Versioning and Release Policy

Project menggunakan format versi SemVer-style.

## Format versi

| Jenis | Format | Contoh |
| --- | --- | --- |
| Stable | `MAJOR.MINOR.PATCH` | `1.0.0` |
| Pre-release | `MAJOR.MINOR.PATCH-rc.N` atau label SemVer lain | `1.0.0-rc.4` |
| Tag release | `v<version>` | `v1.0.0` |
| Dry build | `0.0.0-dry.<run>.<sha>` | `0.0.0-dry.42.a1b2c3d` |

Release workflow menerima input versi tanpa prefix `v`, lalu membuat atau memakai tag `v<version>`.

## Build metadata

Build metadata dihasilkan Gradle ke file generated source:

```text
composeApp/build/generated/build-info/commonMain/kotlin/.../GeneratedBuildInfo.kt
```

File generated tidak disimpan di Git. Metadata dibaca oleh `AppBuildInfoProvider` dan ditampilkan di About screen.

Metadata yang disimpan:

- app name
- version name
- version code atau GitHub Actions run number
- build profile
- build target
- repository URL
- branch
- commit SHA
- clean/dirty state
- build date
- CI flag
- CI run ID
- database schema version
- version ABI

## Version ABI

Version ABI adalah string internal untuk menghubungkan build aplikasi dengan commit dan skema database.

Format:

```text
<short-commit>_db<schema>
```

Jika Git metadata tidak tersedia:

```text
db<schema>
```

Contoh:

```text
9d1f90b9_db7
```

## Version code

Build lokal dapat memakai version code default. Build CI memakai GitHub Actions run number agar setiap artifact memiliki version code yang meningkat.

## Local build

```bash
./gradlew :androidApp:assembleDebug   -Papp.versionName=1.0.0   -PbuildProfile=debug   -PbuildTarget=Android
```

```bash
./gradlew :androidApp:assembleRelease   -Papp.versionName=1.0.0   -PbuildProfile=release   -PbuildTarget=Android
```

## Dry release build

Dry build dipakai untuk memvalidasi pipeline release tanpa menerbitkan GitHub Release.

Workflow:

```text
.github/workflows/release-dry-build.yml
```

Output:

- Android APK release dry build
- SHA256SUMS
- optional iOS simulator artifact

## Release workflow

Workflow:

```text
.github/workflows/release.yml
```

Release dapat dijalankan melalui:

- push tag `v*`
- manual workflow dispatch dengan input `version`

Output utama:

- signed Android APK per architecture
- SHA256SUMS
- optional iOS simulator artifact
- GitHub Release

Nama APK:

```text
MyBawangGacha-v<version>-<arch>.apk
```

Contoh:

```text
MyBawangGacha-v1.0.1-arm-v8a.apk
MyBawangGacha-v1.0.1-arm-v7a.apk
MyBawangGacha-v1.0.1-x86-64.apk
MyBawangGacha-v1.0.1-universal.apk
```

## Secrets release Android

Release signed APK membutuhkan secrets berikut di GitHub Actions:

```text
ANDROID_RELEASE_KEYSTORE_BASE64
ANDROID_RELEASE_KEYSTORE_PASSWORD
ANDROID_RELEASE_KEY_ALIAS
ANDROID_RELEASE_KEY_PASSWORD
```

Release workflow akan gagal jika APK signed tidak ditemukan atau checksum Android tidak tersedia.
