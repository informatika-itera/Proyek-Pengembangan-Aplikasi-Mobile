package com.example.rosea.domain.repository

import com.example.rosea.domain.model.Address
import kotlinx.coroutines.flow.Flow

interface AddressRepository {
    fun getAllAddresses(): Flow<List<Address>>
    suspend fun getAddressById(id: Long): Address?
    suspend fun insertAddress(address: Address)
    suspend fun deleteAddress(id: Long)
    suspend fun setDefaultAddress(id: Long)
}
