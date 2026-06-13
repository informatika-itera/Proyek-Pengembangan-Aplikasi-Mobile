# Cache Code

Cache digunakan untuk mengurangi request berulang, mempercepat UI, dan menjaga konten tetap tampil ketika network gagal.

## Cache source

| Source | Fungsi |
| --- | --- |
| `AnimeDetailCacheLocalDataSource` | Simpan dan baca detail anime beserta episode JSON. |
| `MangaDetailCacheLocalDataSource` | Simpan dan baca detail manga JSON. |
| `MediaPageCacheLocalDataSource` | Simpan dan baca halaman list/search berdasarkan cache key. |
| `RelationPreviewCacheLocalDataSource` | Simpan dan baca preview relasi media. |

## Cache key

Media page dan relation preview memakai `cache_key`. Cache key harus stabil untuk parameter request yang sama.

## Stale-while-revalidate

Beberapa repository dapat menampilkan cache dulu, lalu mencoba refresh remote. Bila remote gagal, UI tetap memiliki konten lama dan menampilkan state error/refresh failed.

## Force refresh

Detail screen dan list tertentu dapat memaksa refresh agar request remote tidak selalu tertahan oleh cache lama.

## Offline only

Saat network mode `Offline Only`, repository tidak perlu memanggil remote source dan harus memakai data lokal/cache yang tersedia.

## Clear cache

Settings menyediakan aksi clear cache. Implementasi clear cache harus menjaga data user seperti library, notes, dan AI chat agar tidak terhapus kecuali memang ada aksi khusus.
