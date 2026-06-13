package com.example.rosea.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rosea.domain.model.Address
import com.example.rosea.domain.repository.AddressRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AddressViewModel(
    private val addressRepository: AddressRepository
) : ViewModel() {

    val addresses: StateFlow<List<Address>> = addressRepository.getAllAddresses()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun saveAddress(
        id: Long = 0,
        label: String,
        receiverName: String,
        phoneNumber: String,
        fullAddress: String,
        isDefault: Boolean
    ) {
        viewModelScope.launch {
            val address = Address(
                id = id,
                label = label,
                receiverName = receiverName,
                phoneNumber = phoneNumber,
                fullAddress = fullAddress,
                isDefault = isDefault
            )
            if (isDefault) {
                // Repository impl handles resetting others if needed, 
                // but let's be explicit if repo didn't handle it in transaction
                addressRepository.insertAddress(address)
                addressRepository.setDefaultAddress(address.id)
            } else {
                addressRepository.insertAddress(address)
            }
        }
    }

    fun deleteAddress(id: Long) {
        viewModelScope.launch {
            addressRepository.deleteAddress(id)
        }
    }

    fun makeDefault(id: Long) {
        viewModelScope.launch {
            addressRepository.setDefaultAddress(id)
        }
    }
}
