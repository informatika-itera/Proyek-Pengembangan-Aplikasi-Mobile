package com.example.Roomie.presentation.help

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HelpViewModelTest {
    @Test
    fun `toggleFaq should flip isExpanded state`() {
        val viewModel = HelpViewModel()
        val initialState = viewModel.faqItems.value[0].isExpanded
        
        viewModel.toggleFaq(0)
        assertEquals(!initialState, viewModel.faqItems.value[0].isExpanded)
        
        viewModel.toggleFaq(0)
        assertEquals(initialState, viewModel.faqItems.value[0].isExpanded)
    }

    @Test
    fun `initial faqItems should have data`() {
        val viewModel = HelpViewModel()
        assertTrue(viewModel.faqItems.value.isNotEmpty())
        assertFalse(viewModel.faqItems.value.any { it.isExpanded })
    }
}
