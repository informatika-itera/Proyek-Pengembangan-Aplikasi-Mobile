package com.studyhub.domain.fake

import com.studyhub.domain.model.UserPreferences
import com.studyhub.domain.usecase.preferences.GetUserPreferencesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeGetUserPreferencesUseCase(
    private val fakePrefsRepo: FakePreferencesRepository = FakePreferencesRepository()
) : GetUserPreferencesUseCase(fakePrefsRepo) {
    private val _prefs = MutableStateFlow(UserPreferences())
    
    var prefs: UserPreferences
        get() = _prefs.value
        set(value) { _prefs.value = value }

    override operator fun invoke(): Flow<UserPreferences> = _prefs
}
