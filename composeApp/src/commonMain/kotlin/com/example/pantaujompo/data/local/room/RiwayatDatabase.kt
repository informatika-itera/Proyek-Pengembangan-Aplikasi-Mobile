package com.example.pantaujompo.data.local.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "riwayat_lari")
data class RiwayatEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val judul: String = "",
    val deskripsi: String = "",
    val jenis: String = "Lari", // Baru: Jalan, Lari, atau Sepeda
    val jarak: Double,
    val kalori: Int,
    val durasi: Int,
    val pace: String,
    val tanggal: Long = System.currentTimeMillis(),
    val ruteString: String,
    val photoUri: String? = null
)

@Dao
interface RiwayatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRiwayat(riwayat: RiwayatEntity)

    @Query("SELECT * FROM riwayat_lari ORDER BY tanggal DESC")
    fun getAllRiwayat(): Flow<List<RiwayatEntity>>

    @Query("DELETE FROM riwayat_lari WHERE id = :id")
    suspend fun hapusRiwayatById(id: Int)
}

@Entity(tableName = "riwayat_makanan")
data class MakananEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val namaMakanan: String,
    val protein: Int,
    val karbo: Int,
    val lemak: Int,
    val info: String,
    val photoUri: String = "", // Baru: Untuk menampilkan foto makanan
    val kategori: String = "Lainnya", // Kategori makanan: Sarapan, Makan Siang, Makan Malam, Camilan
    val tanggal: Long = System.currentTimeMillis()
)

@Dao
interface MakananDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMakanan(makanan: MakananEntity)

    @Query("SELECT * FROM riwayat_makanan ORDER BY tanggal DESC")
    fun getAllMakanan(): Flow<List<MakananEntity>>

    @Query("DELETE FROM riwayat_makanan WHERE id = :id")
    suspend fun hapusMakananById(id: Int)
}

// BUMP VERSION KE 6
@Database(entities = [RiwayatEntity::class, MakananEntity::class], version = 6, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun riwayatDao(): RiwayatDao
    abstract fun makananDao(): MakananDao
}