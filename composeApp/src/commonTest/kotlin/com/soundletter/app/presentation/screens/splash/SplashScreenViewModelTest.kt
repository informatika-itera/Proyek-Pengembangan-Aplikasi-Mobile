package com.soundletter.app.presentation.screens.splash

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SplashScreenViewModelTest {

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun `isReady should be true after delay`() = runTest {
        val viewModel = SplashScreenViewModel()
        
        viewModel.isReady.test {
            // Awalnya false
            assertEquals(false, awaitItem())
            
            // Tunggu delay (simulasi asinkronus)
            // Karena menggunakan UnconfinedTestDispatcher, delay akan dilewati dengan cepat
            val finalState = awaitItem()
            assertEquals(true, finalState)
        }
    }
}
