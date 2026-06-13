# Git Workflow

## Branch utama

```text
main
```

`main` harus selalu dalam kondisi dapat dibuild untuk Android dan common source set.

## Branch fitur

Format yang disarankan:

```text
feature/<nama-fitur>
fix/<nama-bug>
docs/<nama-dokumen>
release/<version>
```

Contoh:

```text
feature/gacha-history
fix/search-cache-refresh
docs/rewrite-project-docs
release/1.0.1
```

## Commit

Gunakan commit yang menjelaskan perubahan secara spesifik.

Contoh:

```text
feat(gacha): add deck session anti repetition
fix(ai): handle empty Gemini token from settings
docs(project): rewrite codebase documentation
chore(ci): add release dry build workflow
```

## Pull request

Sebelum merge:

- pastikan CI lulus
- pastikan perubahan dokumentasi sesuai fitur yang berubah
- pastikan tidak ada file lokal seperti `local.properties` atau keystore yang ikut commit
- untuk release, pastikan version dan changelog sudah sesuai
