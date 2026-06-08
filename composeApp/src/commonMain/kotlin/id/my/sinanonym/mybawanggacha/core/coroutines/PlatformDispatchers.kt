package id.my.sinanonym.mybawanggacha.core.coroutines

import kotlinx.coroutines.CoroutineDispatcher

expect object PlatformDispatchers {
    val io: CoroutineDispatcher
}
