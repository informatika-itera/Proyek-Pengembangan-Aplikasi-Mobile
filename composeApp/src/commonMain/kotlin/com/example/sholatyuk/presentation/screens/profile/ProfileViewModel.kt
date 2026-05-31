package com.example.sholatyuk.presentation.screens.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel : ViewModel() {

    // State untuk Nama dan Bio
    private val _userName = MutableStateFlow("Umar Faruq")
    val userName: StateFlow<String> = _userName

    private val _userBio = MutableStateFlow("Kelompok SholatYuk")
    val userBio: StateFlow<String> = _userBio

    // State untuk Pengaturan
    private val _isAdzanEnabled = MutableStateFlow(true)
    val isAdzanEnabled: StateFlow<Boolean> = _isAdzanEnabled

    private val _isLightModeEnabled = MutableStateFlow(false)
    val isLightModeEnabled: StateFlow<Boolean> = _isLightModeEnabled

    // Fungsi untuk mengubah data
    fun updateUserName(newName: String) { _userName.value = newName }
    fun updateUserBio(newBio: String) { _userBio.value = newBio }
    fun updateAdzan(isEnabled: Boolean) { _isAdzanEnabled.value = isEnabled }
    fun updateLightMode(isEnabled: Boolean) { _isLightModeEnabled.value = isEnabled }
}