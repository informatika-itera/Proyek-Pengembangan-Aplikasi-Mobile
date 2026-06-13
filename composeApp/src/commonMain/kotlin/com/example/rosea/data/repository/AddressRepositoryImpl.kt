package com.example.rosea.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.rosea.data.local.AddressEntity
import com.example.rosea.data.local.NoteDatabase
import com.example.rosea.domain.model.Address
import com.example.rosea.domain.repository.AddressRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AddressRepositoryImpl(
    db: NoteDatabase
) : AddressRepository {

    private val queries = db.addressQueries

    override fun getAllAddresses(): Flow<List<Address>> {
        return queries.getAllAddresses()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }

    override suspend fun getAddressById(id: Long): Address? {
        return withContext(Dispatchers.Default) {
            queries.getAddressById(id).executeAsOneOrNull()?.toDomain()
        }
    }

    override suspend fun insertAddress(address: Address) {
        withContext(Dispatchers.Default) {
            queries.insertAddress(
                id = if (address.id == 0L) null else address.id,
                label = address.label,
                receiverName = address.receiverName,
                phoneNumber = address.phoneNumber,
                fullAddress = address.fullAddress,
                isDefault = if (address.isDefault) 1L else 0L
            )
        }
    }

    override suspend fun deleteAddress(id: Long) {
        withContext(Dispatchers.Default) {
            queries.deleteAddress(id)
        }
    }

    override suspend fun setDefaultAddress(id: Long) {
        withContext(Dispatchers.Default) {
            queries.transaction {
                queries.updateDefaultAddress()
                queries.setDefaultAddress(id)
            }
        }
    }

    private fun AddressEntity.toDomain(): Address {
        return Address(
            id = id,
            label = label,
            receiverName = receiverName,
            phoneNumber = phoneNumber,
            fullAddress = fullAddress,
            isDefault = isDefault == 1L
        )
    }
}
