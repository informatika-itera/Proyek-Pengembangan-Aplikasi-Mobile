package com.example.inventra.data.repository

import app.cash.turbine.test
import com.example.inventra.FakeBorrowRepository
import com.example.inventra.domain.model.BorrowRecord
import com.example.inventra.domain.model.BorrowStatus
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BorrowRepositoryTest {

    private lateinit var repository: FakeBorrowRepository

    @BeforeTest
    fun setup() {
        repository = FakeBorrowRepository()
    }

    @Test
    fun `borrowItem should add record with PENDING status`() = runTest {
        val record = createTestRecord()
        repository.borrowItem(record)

        repository.getAllRecords().test {
            val records = awaitItem()
            assertEquals(1, records.size)
            assertEquals(BorrowStatus.PENDING, records.first().status)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getAllRecords should return all records including different statuses`() = runTest {
        repository.borrowItem(createTestRecord(itemName = "Projector"))
        repository.borrowItem(createTestRecord(itemName = "Speaker"))
        repository.borrowItem(createTestRecord(itemName = "Mic"))

        repository.getAllRecords().test {
            val records = awaitItem()
            assertEquals(3, records.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `approveRequest should change status from PENDING to ACTIVE`() = runTest {
        val id = repository.borrowItem(createTestRecord())

        repository.approveRequest(id)

        repository.getAllRecords().test {
            val records = awaitItem()
            assertEquals(BorrowStatus.ACTIVE, records.first().status)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `returnItem should change status to RETURNED`() = runTest {
        val record = createTestRecord().copy(id = 1L, status = BorrowStatus.ACTIVE)
        repository.addRecord(record)

        repository.returnItem(1L, null)

        repository.getAllRecords().test {
            val records = awaitItem()
            assertEquals(BorrowStatus.RETURNED, records.first().status)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getActiveRecords should only return ACTIVE and OVERDUE records`() = runTest {
        repository.addRecord(createTestRecord().copy(id = 1L, status = BorrowStatus.ACTIVE))
        repository.addRecord(createTestRecord().copy(id = 2L, status = BorrowStatus.RETURNED))
        repository.addRecord(createTestRecord().copy(id = 3L, status = BorrowStatus.PENDING))
        repository.addRecord(createTestRecord().copy(id = 4L, status = BorrowStatus.OVERDUE))

        repository.getActiveRecords().test {
            val records = awaitItem()
            assertEquals(2, records.size)
            assertTrue(records.all {
                it.status == BorrowStatus.ACTIVE || it.status == BorrowStatus.OVERDUE
            })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getActiveRecords should be empty when no active borrowings`() = runTest {
        repository.addRecord(createTestRecord().copy(id = 1L, status = BorrowStatus.RETURNED))
        repository.addRecord(createTestRecord().copy(id = 2L, status = BorrowStatus.PENDING))

        repository.getActiveRecords().test {
            val records = awaitItem()
            assertTrue(records.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteAll should clear all records`() = runTest {
        repository.borrowItem(createTestRecord(itemName = "Item A"))
        repository.borrowItem(createTestRecord(itemName = "Item B"))

        repository.deleteAll()

        repository.getAllRecords().test {
            val records = awaitItem()
            assertTrue(records.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createTestRecord(
        id: Long = 0L,
        itemName: String = "Test Item"
    ): BorrowRecord {
        val now = Clock.System.now()
        return BorrowRecord(
            id = id,
            itemId = 1L,
            itemName = itemName,
            borrowerName = "Test User (Pubdok)",
            borrowDate = now,
            dueDate = now.plus(2, DateTimeUnit.DAY, TimeZone.currentSystemDefault()),
            status = BorrowStatus.PENDING,
            fineAmount = 0L
        )
    }
}
