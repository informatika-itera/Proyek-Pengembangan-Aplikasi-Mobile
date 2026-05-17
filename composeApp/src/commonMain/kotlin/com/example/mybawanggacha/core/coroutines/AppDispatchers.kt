package com.example.mybawanggacha.core.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

data class AppDispatchers(
    val default: CoroutineDispatcher = Dispatchers.Default,
    val io: CoroutineDispatcher = Dispatchers.IO,
    val main: CoroutineDispatcher = Dispatchers.Main
)
