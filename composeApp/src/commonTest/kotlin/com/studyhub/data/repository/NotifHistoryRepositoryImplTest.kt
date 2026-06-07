package com.studyhub.data.repository

import com.studyhub.data.fake.FakeNotifHistoryDataSource
import com.studyhub.data.fake.FakePreferencesDataSource
import com.studyhub.domain.model.NotifHistoryItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class NotifHistoryRepositoryImplTest {
    private lateinit var fakeDataSource: FakeNotifHistoryDataSource
    private lateinit var fakePrefs: FakePreferencesDataSource
    private lateinit var repository: NotifHistoryRepositoryImpl

    @BeforeTest
    fun setup() {
        fakeDataSource = FakeNotifHistoryDataSource()
        fakePrefs = FakePreferencesDataSource()
        // Initialize with a large lastCleanup to avoid auto-cleanup in every test
        fakePrefs.lastCleanup = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        repository = NotifHistoryRepositoryImpl(fakeDataSource, fakePrefs)
    }

    @Test
    fun `given history when getHistory then returns list`() = runTest {
        val item = NotifHistoryItem("1", "t1", "Title", "Sub", "Reason", 0L, false)
        fakeDataSource.items.add(item)
        val result = repository.getHistory()
        assertEquals(1, result.size)
    }

    @Test
    fun `given unread items when getUnreadCount then returns correct count`() = runTest {
        fakeDataSource.items.add(NotifHistoryItem("1", "t1", "T", "S", "R", 0L, false))
        fakeDataSource.items.add(NotifHistoryItem("2", "t2", "T", "S", "R", 0L, true))
        assertEquals(1, repository.getUnreadCount())
    }

    @Test
    fun `given item when addToHistory then inserted`() = runTest {
        // Set lastCleanup to now so addToHistory doesn't run cleanup
        fakePrefs.lastCleanup = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        repository.addToHistory("task1", "Tugas Algo", "Algoritma", "Deadline dekat")
        assertTrue(fakeDataSource.insertCalled)
        assertEquals(1, fakeDataSource.items.size)
    }

    @Test
    fun `given auto delete disabled when runAutoCleanup then not run`() = runTest {
        fakePrefs.autoDelete = false
        fakePrefs.lastCleanup = 0L // Trigger potential run
        val result = repository.runAutoCleanupIfNeeded()
        assertFalse(result.wasRun)
    }

    @Test
    fun `given last cleanup recent when runAutoCleanup then skip`() = runTest {
        fakePrefs.autoDelete = true
        fakePrefs.lastCleanup = kotlinx.datetime.Clock.System.now().toEpochMilliseconds() - 3600_000L // 1 hour ago
        val result = repository.runAutoCleanupIfNeeded()
        assertFalse(result.wasRun)
    }

    @Test
    fun `given last cleanup long ago when runAutoCleanup then runs`() = runTest {
        fakePrefs.autoDelete = true
        fakePrefs.lastCleanup = 0L
        val result = repository.runAutoCleanupIfNeeded()
        assertTrue(result.wasRun)
    }

    @Test
    fun `given old items when deleteOlderThan then returns deleted count`() = runTest {
        fakeDataSource.items.add(NotifHistoryItem("1", "t1", "T", "S", "R", 100L, false))
        val deleted = repository.deleteOlderThan(200L)
        assertEquals(1, deleted)
    }

    @Test
    fun `given excess items when deleteExcessItems then trims to max`() = runTest {
        repeat(10) { fakeDataSource.items.add(NotifHistoryItem("$it", "t", "T", "S", "R", it.toLong() + 1000, false)) }
        val deleted = repository.deleteExcessItems(5)
        assertEquals(5, deleted)
    }
}
