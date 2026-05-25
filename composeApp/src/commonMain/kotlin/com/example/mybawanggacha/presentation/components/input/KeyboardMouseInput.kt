package com.example.mybawanggacha.presentation.components.input

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role

@Composable
fun rememberKeyboardMouseInteractionSource(): MutableInteractionSource {
    return remember { MutableInteractionSource() }
}

@Composable
fun MutableInteractionSource.collectKeyboardMouseActiveState(): Boolean {
    val focused by collectIsFocusedAsState()
    val hovered by collectIsHoveredAsState()

    return focused || hovered
}

@Composable
fun Modifier.keyboardMouseClickable(
    onClick: () -> Unit,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    interactionSource: MutableInteractionSource = rememberKeyboardMouseInteractionSource(),
    onKeyEvent: (KeyEvent) -> Boolean = { false }
): Modifier {
    val indication = LocalIndication.current
    val hoverModifier = if (enabled) {
        pointerHoverIcon(PointerIcon.Hand)
    } else {
        this
    }

    return hoverModifier
        .onPreviewKeyEvent { event ->
            when {
                !enabled -> false
                onKeyEvent(event) -> true
                event.isKeyboardClickActivationKey() -> {
                    onClick()
                    true
                }
                else -> false
            }
        }
        .clickable(
            enabled = enabled,
            interactionSource = interactionSource,
            indication = indication,
            onClickLabel = onClickLabel,
            role = Role.Button,
            onClick = onClick
        )
}

fun KeyEvent.isKeyboardNavigationPreviousKey(): Boolean {
    return type == KeyEventType.KeyDown &&
        (key == Key.DirectionUp || key == Key.DirectionLeft)
}

fun KeyEvent.isKeyboardNavigationNextKey(): Boolean {
    return type == KeyEventType.KeyDown &&
        (key == Key.DirectionDown || key == Key.DirectionRight)
}

private fun KeyEvent.isKeyboardClickActivationKey(): Boolean {
    return type == KeyEventType.KeyUp &&
        (key == Key.Enter || key == Key.NumPadEnter || key == Key.Spacebar)
}
