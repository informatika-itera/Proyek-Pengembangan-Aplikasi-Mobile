package com.studyhub.presentation.theme

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val CurvedBottomShape = GenericShape { size, _ ->
    moveTo(0f, 0f)
    lineTo(size.width, 0f)
    lineTo(size.width, size.height - 40f)
    quadraticTo(
        size.width / 2f, size.height + 40f,
        0f, size.height - 40f
    )
    close()
}

val StudyHubShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(10.dp), // For icons/small boxes
    medium = RoundedCornerShape(16.dp), // For task cards
    large = RoundedCornerShape(20.dp), // For larger widgets/cards
    extraLarge = RoundedCornerShape(28.dp) // For hero headers/full cards
)

val PillShape = RoundedCornerShape(999.dp)
val CardShape = RoundedCornerShape(16.dp)
val LargeCardShape = RoundedCornerShape(20.dp)
val IconBoxShape = RoundedCornerShape(12.dp)
