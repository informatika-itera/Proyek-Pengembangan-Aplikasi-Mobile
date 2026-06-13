package com.example.neurodeck.data.repository

import app.cash.sqldelight.db.SqlDriver
import com.example.neurodeck.data.local.NeuroDeckDatabase
import com.example.neurodeck.data.local.TestDatabaseFactory
import com.example.neurodeck.domain.model.MessageRole
import com.example.neurodeck.fakes.FakeAIRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Integration test untuk [ChatRepositoryImpl] memakai DB in-memory +
 * [FakeAIRepository]. Memverifikasi alur composite sendMessage:
 * simpan pesan user → panggil AI → simpan balasan (atau pesan error).
 */
class ChatRepositoryImplTest {

    private lateinit var driver: SqlDriver
    private lateinit var database: NeuroDeckDatabase
    private lateinit var fakeAi: FakeAIRepository
    private lateinit var repository: ChatRepositoryImpl

    @BeforeTest
    fun setup() {
        driver = TestDatabaseFactory.createDriver()
        database = TestDatabaseFactory.create(driver)
        fakeAi = FakeAIRepository()
        repository = ChatRepositoryImpl(database, fakeAi)
    }

    @AfterTest
    fun teardown() {
        driver.close()
    }

    @Test
    fun `observeMessages kosong di awal`() = runTest {
        assertTrue(repository.observeMessages().first().isEmpty())
    }

    @Test
    fun `sendMessage sukses menyimpan pesan user dan balasan AI`() = runTest {
        fakeAi.chatReply = "Recursion adalah fungsi yang memanggil dirinya sendiri."

        val result = repository.sendMessage("Apa itu recursion?")

        assertTrue(result.isSuccess)
        val messages = repository.observeMessages().first()
        assertEquals(2, messages.size, "1 pesan user + 1 balasan AI")
        assertEquals(MessageRole.User, messages[0].role)
        assertEquals("Apa itu recursion?", messages[0].content)
        assertEquals(MessageRole.Assistant, messages[1].role)
        assertEquals(fakeAi.chatReply, messages[1].content)
    }

    @Test
    fun `sendMessage menolak pesan kosong`() = runTest {
        val result = repository.sendMessage("   ")
        assertTrue(result.isFailure)
        assertTrue(repository.observeMessages().first().isEmpty())
    }

    @Test
    fun `sendMessage gagal menyimpan pesan error AI sebagai bubble assistant`() = runTest {
        fakeAi.throwOnChat = true

        val result = repository.sendMessage("Pertanyaan yang gagal")

        assertTrue(result.isFailure)
        val messages = repository.observeMessages().first()
        // User message tetap tersimpan + 1 pesan error dari assistant
        assertEquals(2, messages.size)
        assertEquals(MessageRole.User, messages[0].role)
        assertTrue(messages[1].isError, "Bubble assistant ditandai error untuk UI retry")
    }

    @Test
    fun `sendMessage meneruskan history ke AI dengan role yang benar`() = runTest {
        repository.sendMessage("Halo")
        repository.sendMessage("Lanjut")

        // History terakhir yang dikirim ke AI harus berisi turn user sebelumnya
        val history = fakeAi.lastHistory!!
        assertTrue(history.isNotEmpty())
        assertTrue(history.any { it.first == "user" }, "Ada turn dengan role 'user'")
        assertTrue(history.any { it.first == "model" }, "Balasan sebelumnya di-map ke 'model'")
    }

    @Test
    fun `clearHistory mengosongkan semua pesan`() = runTest {
        repository.sendMessage("Pesan 1")
        assertTrue(repository.observeMessages().first().isNotEmpty())

        repository.clearHistory()
        assertTrue(repository.observeMessages().first().isEmpty())
    }
}
