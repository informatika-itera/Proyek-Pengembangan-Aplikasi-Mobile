package com.example.raillog.presentation.screens.admin_main

import com.example.raillog.data.local.datastore.UserPreferences
import com.example.raillog.domain.model.*
import com.example.raillog.domain.repository.SupplyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.first

import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import kotlinx.datetime.Clock
import kotlin.test.*

class FakeSupplyRepository : SupplyRepository {
    private val items = MutableStateFlow<List<SupplyItem>>(emptyList())
    private val drafts = MutableStateFlow<List<DraftItem>>(emptyList())

    fun setItems(list: List<SupplyItem>) { items.value = list }

    override suspend fun migrateDataToUser(username: String) {
        // Do nothing for tests
    }

    override fun getAllItems(activeUsername: String): Flow<List<SupplyItem>> = items.asStateFlow()

    override fun getItemById(id: Long): Flow<SupplyItem?> =
        MutableStateFlow(items.value.find { it.id == id }).asStateFlow()

    override suspend fun insertItem(item: SupplyItem, activeUsername: String) {
        items.value = items.value + item.copy(id = (items.value.maxOfOrNull { it.id } ?: 0L) + 1)
    }

    override suspend fun updateItem(item: SupplyItem) {
        items.value = items.value.map { if (it.id == item.id) item else it }
    }

    override suspend fun deleteItem(id: Long) {
        items.value = items.value.filterNot { it.id == id }
    }

    override suspend fun updateStatus(id: Long, status: SupplyStatus) {
        items.value = items.value.map { if (it.id == id) it.copy(status = status) else it }
    }

    override suspend fun saveDraft(draftId: String, projectTitle: String, currentStep: Int, lastUpdated: Long, formStateJson: String, activeUsername: String) {
        drafts.value = drafts.value + DraftItem(draftId, projectTitle, currentStep, lastUpdated, formStateJson)
    }

    override fun getAllDrafts(activeUsername: String): Flow<List<DraftItem>> = drafts.asStateFlow()

    override suspend fun deleteDraft(draftId: String) {
        drafts.value = drafts.value.filterNot { it.draftId == draftId }
    }
}

// Minimal implementation to avoid datastore issues in commonTest
class FakeUserPreferences : UserPreferences(
    // Simple mock or dummy implementation that satisfies the constructor
    // If we cannot mock, we can just pass a null if it's allowed or use a very basic dummy
    // Since we need an actual DataStore instance, and it's complicated,
    // let's try to just not use DataStore at all by creating a subclass that overrides DataStore
    // Or just use a simple wrapper that we can inject.
    // Given the constraints, the easiest is to override properties in UserPreferences directly
    // and ignore the constructor parameter if possible, but that's Kotlin.
    // Let's use a dummy object for DataStore
    object : androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences> {
        override val data = flowOf(androidx.datastore.preferences.core.emptyPreferences())
        override suspend fun updateData(transform: suspend (androidx.datastore.preferences.core.Preferences) -> androidx.datastore.preferences.core.Preferences) = data.first()
    }
) {
    override val activeUsername = flowOf("admin")
}

@OptIn(ExperimentalCoroutinesApi::class)
class AdminMainViewModelTest {
    // Tests...
}
