package com.example.Roomie.domain.usecase

import com.example.Roomie.domain.model.Room
import com.example.Roomie.domain.repository.FacilityRepository
import kotlinx.coroutines.flow.Flow

class SearchRoomsFilteredUseCase(private val repository: FacilityRepository) {
    operator fun invoke(query: String, minCapacity: Int, maxCapacity: Int): Flow<List<Room>> =
        repository.searchRoomsFiltered(query, minCapacity, maxCapacity)
}
