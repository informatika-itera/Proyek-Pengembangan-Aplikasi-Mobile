package id.my.sinanonym.mybawanggacha.presentation.components

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.withTimeoutOrNull
internal const val TITLE_COPY_LONG_PRESS_THRESHOLD_MS: Long = 450L

@Composable
fun CopyableTitleText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    fontWeight: FontWeight? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    longPressThresholdMillis: Long = TITLE_COPY_LONG_PRESS_THRESHOLD_MS
) {
    val clipboardManager = LocalClipboardManager.current

    Text(
        text = text,
        modifier = modifier.copyToClipboardOnLongPress(
            text = text,
            thresholdMillis = longPressThresholdMillis,
            onCopy = {
                clipboardManager.setText(AnnotatedString(text))
            }
        ),
        style = style,
        color = color,
        fontWeight = fontWeight,
        maxLines = maxLines,
        overflow = overflow
    )
}

private fun Modifier.copyToClipboardOnLongPress(
    text: String,
    thresholdMillis: Long,
    onCopy: () -> Unit
): Modifier {
    if (text.isBlank()) return this

    return pointerInput(text, thresholdMillis) {
        awaitEachGesture {
            awaitFirstDown(requireUnconsumed = false)

            val releasedOrCancelledBeforeThreshold = withTimeoutOrNull(
                timeMillis = thresholdMillis.coerceAtLeast(1L)
            ) {
                waitForUpOrCancellation()
                true
            }

            if (releasedOrCancelledBeforeThreshold == null) {
                onCopy()

                do {
                    val event = awaitPointerEvent()
                    event.changes.forEach { change -> change.consume() }
                } while (event.changes.any { change -> change.pressed })
            }
        }
    }
}
