package com.example.arcane.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arcane.data.local.datastore.DataStoreFactory
import com.example.arcane.data.local.datastore.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okio.FileSystem
import okio.Path.Companion.toPath

class SettingsViewModel(
    private val userPreferences: UserPreferences,
    private val dataStoreFactory: DataStoreFactory
) : ViewModel() {

    val isDarkMode: StateFlow<Boolean> = userPreferences.isDarkMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val userName: StateFlow<String> = userPreferences.userName
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    val favoriteGenre: StateFlow<String> = userPreferences.favoriteGenre
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    val readingGoal: StateFlow<Int> = userPreferences.readingGoal
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 12
        )

    val profilePhotoPath: StateFlow<String> = userPreferences.profilePhoto
        .map { filename ->
            if (filename.isNotBlank()) {
                "${dataStoreFactory.producePath()}/$filename"
            } else {
                ""
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setDarkMode(enabled)
        }
    }

    fun updateUserName(name: String) {
        viewModelScope.launch {
            userPreferences.setUserName(name)
        }
    }

    fun updateFavoriteGenre(genre: String) {
        viewModelScope.launch {
            userPreferences.setFavoriteGenre(genre)
        }
    }

    fun updateReadingGoal(goal: Int) {
        viewModelScope.launch {
            userPreferences.setReadingGoal(goal)
        }
    }

    fun updateProfilePhoto(byteArray: ByteArray) {
        viewModelScope.launch {
            val directory = dataStoreFactory.producePath()
            val newFilename = "profile_${kotlin.random.Random.nextLong(10000000, 99999999)}.jpg"
            val newPath = "$directory/$newFilename".toPath()

            // Hapus file foto lama jika ada untuk menghemat ruang
            val oldFilename = userPreferences.profilePhoto.first()
            if (oldFilename.isNotBlank()) {
                val oldPath = "$directory/$oldFilename".toPath()
                try {
                    FileSystem.SYSTEM.delete(oldPath)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // Simpan gambar baru ke lokal
            try {
                FileSystem.SYSTEM.write(newPath) {
                    write(byteArray)
                }
                userPreferences.setProfilePhoto(newFilename)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun clearProfilePhoto() {
        viewModelScope.launch {
            val directory = dataStoreFactory.producePath()
            val oldFilename = userPreferences.profilePhoto.first()
            if (oldFilename.isNotBlank()) {
                val oldPath = "$directory/$oldFilename".toPath()
                try {
                    FileSystem.SYSTEM.delete(oldPath)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            userPreferences.clearProfilePhoto()
        }
    }
}