package com.example.travelplanner.presentation.screens.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelplanner.domain.model.TripFinanceSummary
import com.example.travelplanner.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class FinanceSummaryViewModel(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {
    val financeSummaries: StateFlow<List<TripFinanceSummary>> = expenseRepository.getFinanceSummaryList()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
