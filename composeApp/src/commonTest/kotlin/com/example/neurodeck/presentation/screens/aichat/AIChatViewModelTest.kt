package com.example.neurodeck.presentation.screens.aichat

import app.cash.turbine.test
import com.example.neurodeck.domain.model.ChatMessage
import com.example.neurodeck.domain.model.MessageRole
import com.example.neurodeck.fakes.FakeChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AIChatViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var chatRepo: FakeChatRepository

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(dispatcher)
        chatRepo = FakeChatRepository()
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `state awal kosong dengan saran default`() = runTest {
        val vm = AIChatViewModel(chatRepo)

        vm.uiState.test {
            val state = awaitItem()
            assertTrue(state.isEmpty)
            assertTrue(state.suggestedQuestions.isNotEmpty(), "Ada saran pertanyaan default")
            assertFalse(state.canSend, "Input kosong => tidak bisa kirim")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `canSend true setelah input diisi`() = runTest {
        val vm = AIChatViewModel(chatRepo)

        vm.uiState.test {
            awaitItem() // initial
            vm.onInputChange("Apa itu rekursi?")
            val state = awaitItem()
            assertEquals("Apa itu rekursi?", state.inputText)
            assertTrue(state.canSend)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `observeMessages menampilkan history dari repository`() = runTest {
        chatRepo.messagesFlow.value = listOf(
            ChatMessage(id = 1, role = MessageRole.User, content = "Halo", timestamp = Clock.System.now()),
            ChatMessage(id = 2, role = MessageRole.Assistant, content = "Hai!", timestamp = Clock.System.now()),
        )
        val vm = AIChatViewModel(chatRepo)

        vm.uiState.test {
            var state = awaitItem()
            while (state.messages.isEmpty()) state = awaitItem()
            assertEquals(2, state.messages.size)
            assertFalse(state.isEmpty)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `send mengirim pesan ke repository`() = runTest {
        val vm = AIChatViewModel(chatRepo)

        vm.uiState.test {
            awaitItem() // initial — mengaktifkan subscription (WhileSubscribed)
            vm.onInputChange("Pertanyaan saya")
            // tunggu sampai state mencerminkan input sebelum send() membacanya
            var s = awaitItem()
            while (s.inputText != "Pertanyaan saya") s = awaitItem()

            vm.send()
            assertEquals(listOf("Pertanyaan saya"), chatRepo.sentMessages)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `send di-ignore kalau input blank`() = runTest {
        val vm = AIChatViewModel(chatRepo)
        vm.onInputChange("   ")

        vm.send()

        assertTrue(chatRepo.sentMessages.isEmpty())
    }

    @Test
    fun `send gagal menampilkan errorSnackbar`() = runTest {
        chatRepo.sendResult = Result.failure(RuntimeException("Network error"))
        val vm = AIChatViewModel(chatRepo)

        vm.uiState.test {
            awaitItem() // initial
            vm.onInputChange("tanya")
            vm.send()
            var state = awaitItem()
            while (state.errorSnackbar == null) state = awaitItem()
            assertNotNull(state.errorSnackbar)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `consumeSnackbar menghapus pesan error`() = runTest {
        chatRepo.sendResult = Result.failure(RuntimeException("err"))
        val vm = AIChatViewModel(chatRepo)

        vm.uiState.test {
            awaitItem()
            vm.onInputChange("tanya")
            vm.send()
            var withErr = awaitItem()
            while (withErr.errorSnackbar == null) withErr = awaitItem()
            assertNotNull(withErr.errorSnackbar)

            vm.consumeSnackbar()
            var cleared = awaitItem()
            while (cleared.errorSnackbar != null) cleared = awaitItem()
            assertNull(cleared.errorSnackbar)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `sendSuggestion mengirim pertanyaan saran`() = runTest {
        val vm = AIChatViewModel(chatRepo)

        vm.uiState.test {
            awaitItem() // initial — aktifkan subscription supaya combine hidup
            vm.sendSuggestion("Apa itu spaced repetition?")
            // sendSuggestion set input lalu panggil send(); dengan subscriber aktif,
            // uiState.value sudah ter-update sebelum send() membacanya.
            assertEquals(listOf("Apa itu spaced repetition?"), chatRepo.sentMessages)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clearHistory memanggil repository`() = runTest {
        chatRepo.messagesFlow.value = listOf(
            ChatMessage(id = 1, role = MessageRole.User, content = "x", timestamp = Clock.System.now()),
        )
        val vm = AIChatViewModel(chatRepo)

        vm.clearHistory()

        assertTrue(chatRepo.clearCalled)
    }

    @Test
    fun `onInputChange membatasi panjang maksimal input`() = runTest {
        val vm = AIChatViewModel(chatRepo)

        vm.uiState.test {
            awaitItem() // initial
            vm.onInputChange("a".repeat(5000))
            var state = awaitItem()
            while (state.inputText.isEmpty()) state = awaitItem()
            // MAX_INPUT_LENGTH = 2000 (private), input harus ter-clamp
            assertTrue(state.inputText.length <= 2000)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
