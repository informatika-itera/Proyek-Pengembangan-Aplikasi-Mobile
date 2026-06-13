package com.example.sholatyuk.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.example.sholatyuk.data.local.SholatYukDatabase
import com.example.sholatyuk.data.local.entity.toDomain
import com.example.sholatyuk.data.local.entity.toDomainList
import com.example.sholatyuk.data.remote.dto.DoaDto
import com.example.sholatyuk.domain.model.Doa
import com.example.sholatyuk.domain.model.DoaCategory
import com.example.sholatyuk.domain.repository.DoaRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class DoaRepositoryImpl(
    database: SholatYukDatabase,
    private val httpClient: HttpClient // Ditambahkan agar bisa menggunakan Ktor untuk API
) : DoaRepository {

    // Mengambil doaQueries yang di-generate otomatis oleh SQLDelight dari Doa.sq
    private val queries = database.doaQueries

    override fun getAllDoa(): Flow<List<Doa>> {
        return queries.getAllDoa()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.toDomainList() // Menggunakan extension function dari DoaMapper.kt
            }
    }

    override fun getDoaByCategory(category: DoaCategory): Flow<List<Doa>> {
        return queries.getDoaByCategory(category.name)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.toDomainList()
            }
    }

    override fun getFavoriteDoa(): Flow<List<Doa>> {
        return queries.getFavoriteDoa()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.toDomainList()
            }
    }

    override fun getDoaById(id: Long): Flow<Doa?> {
        return queries.getDoaById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { entity ->
                entity?.toDomain()
            }
    }

    override suspend fun toggleFavorite(id: Long) {
        withContext(Dispatchers.IO) {
            queries.toggleFavorite(id)
        }
    }

    override suspend fun syncDoaFromApi() {
        withContext(Dispatchers.IO) {
            // Cek apakah database lokal masih kosong
            val count = queries.countDoa().executeAsOne()

            if (count == 0L) {
                try {
                    // 1. Ambil data dari API menggunakan Ktor
                    val response: List<DoaDto> = httpClient.get("https://doa-doa-api-ahmadramadhan.fly.dev/api").body()

                    // 2. Simpan semua data ke SQLDelight secara otomatis
                    queries.transaction {
                        response.forEachIndexed { index, dto ->
                            queries.insertDoa(
                                id = (index + 1).toLong(), // Generate ID berurutan
                                title = dto.doa,
                                arabic = dto.ayat,
                                latin = dto.latin,
                                translation = dto.artinya,
                                category = DoaCategory.DAILY.name // Berikan kategori default
                            )
                        }
                    }
                } catch (e: Exception) {
                    // Tangani error jika gagal fetch (misal: tidak ada koneksi internet)
                    e.printStackTrace()
                }
            }
        }
    }
}