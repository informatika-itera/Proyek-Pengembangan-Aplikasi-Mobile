# CI dan Release

Workflow berada di:

```text
.github/workflows/
```

## KMP CI

File:

```text
.github/workflows/KMP-ci.yml
```

Trigger:

- push ke `main`
- pull request ke `main`

Job:

| Job | Runner | Task utama |
| --- | --- | --- |
| Android build | Ubuntu latest | `:composeApp:compileAndroidMain`, `:composeApp:assembleAndroidMain` |
| Shared Kotlin checks | Ubuntu latest | `:composeApp:compileKotlinMetadata`, `:composeApp:assembleUnitTest` |
| iOS simulator build | macOS latest | `:composeApp:compileKotlinIosSimulatorArm64` sebagai optional compile |

Semua job memakai JDK 17 Zulu dan Gradle setup action.

## Release dry build

File:

```text
.github/workflows/release-dry-build.yml
```

Fungsi:

- validasi pipeline release pada pull request atau manual dispatch
- membuat versi dry otomatis bila input kosong
- compile metadata dan unit test sources
- build APK release
- rename artifact
- generate SHA256SUMS
- upload artifact
- build iOS simulator artifact secara optional

Format versi dry:

```text
0.0.0-dry.<github_run_number>.<short_sha>
```

## Release

File:

```text
.github/workflows/release.yml
```

Trigger:

- push tag `v*`
- workflow dispatch dengan input `version` dan `prerelease`

Tahap release:

1. Validasi versi.
2. Build signed Android APK.
3. Verify signature dengan `apksigner`.
4. Rename APK sesuai versi dan architecture.
5. Generate SHA256SUMS.
6. Build iOS simulator artifact optional.
7. Upload artifact.
8. Buat atau validasi tag `v<version>`.
9. Publish GitHub Release.

## Secrets Android signing

```text
ANDROID_RELEASE_KEYSTORE_BASE64
ANDROID_RELEASE_KEYSTORE_PASSWORD
ANDROID_RELEASE_KEY_ALIAS
ANDROID_RELEASE_KEY_PASSWORD
```

## Artifact Android

Nama artifact GitHub Actions:

```text
mybawanggacha-<version>-signed-android-apks
```

Nama APK:

```text
MyBawangGacha-v<version>-<arch>.apk
```

Architecture yang dikenali workflow:

- `arm-v8a`
- `arm-v7a`
- `x86-64`
- `universal`

## Artifact iOS

iOS simulator artifact bersifat optional. Workflow tidak menggagalkan release Android bila build iOS optional gagal, selama validasi Android release sukses.
