package com.kelazzz.app.data.repository

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.kelazzz.app.core.notification.JadwalNotificationScheduler
import com.kelazzz.app.data.local.KelazZzDatabase
import com.kelazzz.app.domain.model.Jadwal
import com.kelazzz.app.domain.model.JenisJadwal
import com.kelazzz.app.testutil.sampleJadwal
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class JadwalRepositoryImplTest {
    private lateinit var driver: SqlDriver
    private lateinit var scheduler: RecordingJadwalNotificationScheduler
    private lateinit var repository: JadwalRepositoryImpl

    @BeforeTest
    fun setUp() {
        driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        KelazZzDatabase.Schema.create(driver)
        scheduler = RecordingJadwalNotificationScheduler()
        repository = JadwalRepositoryImpl(KelazZzDatabase(driver), scheduler)
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }

    @Test
    fun insertJadwalPersistsRowAndSchedulesReminder() = runTest {
        val id = repository.insertJadwal(
            sampleJadwal(id = 0L, judul = "Kuis Mobile", jenis = JenisJadwal.KUIS)
                .copy(reminderOffsetMinutes = 10)
        )

        val stored = repository.getJadwalById(id).first()

        assertNotNull(stored)
        assertEquals("Kuis Mobile", stored.judul)
        assertEquals(JenisJadwal.KUIS, stored.jenis)
        assertEquals(10, stored.reminderOffsetMinutes)
        assertEquals(id, scheduler.scheduled.single().id)
    }

    @Test
    fun updateJadwalChangesStoredDataAndReschedulesReminder() = runTest {
        val id = repository.insertJadwal(sampleJadwal(id = 0L, judul = "Judul Lama"))

        repository.updateJadwal(
            sampleJadwal(id = id, judul = "Judul Baru", tanggal = "2026-06-20")
                .copy(reminderOffsetMinutes = 30)
        )

        val stored = repository.getJadwalById(id).first()
        assertEquals("Judul Baru", stored?.judul)
        assertEquals("2026-06-20", stored?.tanggal)
        assertEquals(id, scheduler.scheduled.last().id)
        assertEquals(30, scheduler.scheduled.last().reminderOffsetMinutes)
    }

    @Test
    fun deleteJadwalRemovesRowAndCancelsReminder() = runTest {
        val id = repository.insertJadwal(sampleJadwal(id = 0L))

        repository.deleteJadwal(id)

        assertNull(repository.getJadwalById(id).first())
        assertEquals(listOf(id), scheduler.cancelled)
    }

    @Test
    fun upcomingJadwalExcludesPastRowsAndRespectsLimit() = runTest {
        repository.insertJadwal(sampleJadwal(id = 0L, judul = "Lampau", tanggal = "2026-06-01"))
        repository.insertJadwal(sampleJadwal(id = 0L, judul = "Hari Ini", tanggal = "2026-06-07"))
        repository.insertJadwal(sampleJadwal(id = 0L, judul = "Besok", tanggal = "2026-06-08"))

        val result = repository.getUpcomingJadwal(fromDate = "2026-06-07", limit = 1).first()

        assertEquals(listOf("Hari Ini"), result.map { it.judul })
    }
}

private class RecordingJadwalNotificationScheduler : JadwalNotificationScheduler {
    val scheduled = mutableListOf<Jadwal>()
    val cancelled = mutableListOf<Long>()

    override suspend fun schedule(jadwal: Jadwal) {
        scheduled += jadwal
    }

    override suspend fun cancel(jadwalId: Long) {
        cancelled += jadwalId
    }
}
