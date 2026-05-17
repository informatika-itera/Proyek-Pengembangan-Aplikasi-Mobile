package com.dailybliss.app.presentation.util

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

import com.dailybliss.app.core.util.PlatformContext

@Composable
actual fun rememberImagePickerLauncher(
    onResult: (ByteArray?) -> Unit
): ImagePickerLauncher {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                try {
                    val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    onResult(bytes)
                } catch (e: Exception) {
                    onResult(null)
                }
            } else {
                onResult(null)
            }
        }
    )

    return remember {
        object : ImagePickerLauncher {
            override fun launch() {
                launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }
    }
}

actual class FileStorage actual constructor(
    private val context: PlatformContext
) {
    actual suspend fun saveImage(bytes: ByteArray): String? = withContext(Dispatchers.IO) {
        try {
            val fileName = "moment_${UUID.randomUUID()}.jpg"
            val file = File(context.filesDir, fileName)
            file.writeBytes(bytes)
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }
}
