package com.example.Roomie.domain.repository

import com.example.Roomie.domain.model.Facility
import kotlinx.coroutines.flow.Flow

interface FacilityRepository {
    fun getFacilities(): Flow<List<Facility>>
    fun getFacilityById(id: String): Flow<Facility?>
}
