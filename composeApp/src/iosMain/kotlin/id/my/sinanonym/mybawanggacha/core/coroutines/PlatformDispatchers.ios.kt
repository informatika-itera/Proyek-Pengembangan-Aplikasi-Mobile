package id.my.sinanonym.mybawanggacha.core.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual object PlatformDispatchers {
    actual val io: CoroutineDispatcher = Dispatchers.Default
}
