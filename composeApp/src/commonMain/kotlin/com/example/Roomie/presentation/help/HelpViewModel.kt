package com.example.Roomie.presentation.help

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class FaqItem(
    val question: String,
    val answer: String,
    val isExpanded: Boolean = false
)

class HelpViewModel : ViewModel() {
    private val _faqItems = MutableStateFlow(
        listOf(
            FaqItem(
                "Berapa lama laporan saya akan diproses?",
                "Tim Sarpras biasanya melakukan verifikasi dalam 1-2 hari kerja setelah laporan dikirim."
            ),
            FaqItem(
                "Bagaimana cara mendapatkan E-Permit?",
                "E-Permit akan otomatis muncul di menu 'Jadwal' setelah pengajuan peminjaman ruangan disetujui oleh admin."
            ),
            FaqItem(
                "Apakah saya bisa membatalkan laporan?",
                "Untuk saat ini laporan yang sudah terkirim tidak dapat dibatalkan manual. Silakan hubungi admin jika ada kesalahan input."
            )
        )
    )
    val faqItems: StateFlow<List<FaqItem>> = _faqItems.asStateFlow()

    fun toggleFaq(index: Int) {
        val currentList = _faqItems.value.toMutableList()
        val item = currentList[index]
        currentList[index] = item.copy(isExpanded = !item.isExpanded)
        _faqItems.value = currentList
    }
}
