package com.example.flushcards.util

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun cardHighlightBackgroundColor(isHighlighted: Boolean): Color {
    val backgroundColor by animateColorAsState(
        targetValue = if (isHighlighted) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },

        animationSpec = tween(
            durationMillis = if (isHighlighted) 150 else 600
        )
    )
    return backgroundColor
}

@Composable
fun cardHighlightBorderColor(isHighlighted: Boolean): Color {
    val borderColor by animateColorAsState(
        targetValue = if (isHighlighted) {
            MaterialTheme.colorScheme.primary
        } else {
            Color.Transparent
        },

        animationSpec = tween(
            durationMillis = if (isHighlighted) 150 else 600
        )
    )
    return borderColor
}

@Composable
fun tapCardColor(isSelected: Boolean, isCorrect: Boolean?): Color {
    val cardColor by animateColorAsState(
        targetValue = when {
            isSelected -> MaterialTheme.colorScheme.primary
            isCorrect == true -> MaterialTheme.colorScheme.tertiaryContainer
            isCorrect == false -> MaterialTheme.colorScheme.errorContainer
            else -> MaterialTheme.colorScheme.surface
        }
    )
    return cardColor
}

@Composable
fun tapCardTextColor(isSelected: Boolean, isCorrect: Boolean?): Color {
    val textColor by animateColorAsState(
        targetValue = when {
            isSelected -> MaterialTheme.colorScheme.onPrimary
            isCorrect == true -> MaterialTheme.colorScheme.onTertiaryContainer
            isCorrect == false -> MaterialTheme.colorScheme.onErrorContainer
            else -> MaterialTheme.colorScheme.onSurface
        }
    )
    return textColor
}

@Composable
fun tapCardBorderColor(isSelected: Boolean, isCorrect: Boolean?): BorderStroke {
    val borderStroke = when {
        isSelected -> BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        isCorrect == true -> BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary)
        isCorrect == false -> BorderStroke(2.dp, MaterialTheme.colorScheme.error)
        else -> BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    }
    return borderStroke
}

@Composable
fun optionButtonColor(isCorrect: Boolean?): ButtonColors {
    val buttonColors = when (isCorrect) {
        true -> {
            ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.8f),
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            )
        }
        false -> {
            ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f),
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        }
        else -> {
            ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        }
    }
    return buttonColors
}

@Composable
fun optionButtonBorderColor(isCorrect: Boolean?): Color {
    val borderColors = when (isCorrect) {
        true -> MaterialTheme.colorScheme.tertiary
        false -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outlineVariant
    }
    return borderColors
}





