package com.example.raillog.presentation

import com.example.raillog.presentation.screens.requisition.RequisitionFormState
import com.example.raillog.presentation.screens.requisition.RequisitionViewModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RequisitionViewModelTest {

    @Test
    fun `test project code regex validation for LRT`() {
        val state = RequisitionFormState(projectType = "LRT", projectCode = "LRT-JABO-24A")
        assertTrue(state.isProjectCodeValid, "LRT-JABO-24A harusnya valid")
    }

    @Test
    fun `test project code regex validation for KRL`() {
        val state = RequisitionFormState(projectType = "KRL", projectCode = "KRL-YO-24B")
        assertTrue(state.isProjectCodeValid, "KRL-YO-24B harusnya valid")
        
        val invalidPrefix = state.copy(projectCode = "LRT-YO-24B")
        assertFalse(invalidPrefix.isProjectCodeValid, "Prefix LRT tidak boleh untuk tipe KRL")
    }

    @Test
    fun `test project code regex validation for High-Speed`() {
        val state = RequisitionFormState(projectType = "High-Speed", projectCode = "KCIC-JKT-01")
        assertTrue(state.isProjectCodeValid, "KCIC-JKT-01 harusnya valid untuk High-Speed")
    }

    @Test
    fun `test project code format integrity`() {
        val state = RequisitionFormState(projectType = "LRT", projectCode = "LRTJABO24A")
        assertFalse(state.isProjectCodeValid, "Format tanpa dash '-' harusnya tidak valid")
        
        val tooShort = state.copy(projectCode = "LRT-24A")
        assertFalse(tooShort.isProjectCodeValid, "Format kurang dari 3 segmen harusnya tidak valid")
    }

    @Test
    fun `test canSubmit logic with signature and items`() {
        val baseState = RequisitionFormState(
            requestorName = "Giovan Lado",
            employeeId = "RLN-001",
            phoneNumber = "08123456789",
            projectType = "LRT",
            projectCode = "LRT-JABO-24A",
            isSigned = true
        )
        
        // Scenario 1: No items selected
        assertFalse(baseState.canSubmit, "Tidak boleh submit jika tidak ada item yang dipilih")
        
        // Scenario 2: With items
        val stateWithItems = baseState.copy(
            catalogItems = baseState.catalogItems.mapIndexed { index, item ->
                if (index == 0) item.copy(reqQty = 5) else item
            }
        )
        assertTrue(stateWithItems.canSubmit, "Form lengkap dengan item harusnya bisa submit")
        
        // Scenario 3: No signature
        assertFalse(stateWithItems.copy(isSigned = false).canSubmit, "Tanpa tanda tangan tidak boleh submit")
    }
}
