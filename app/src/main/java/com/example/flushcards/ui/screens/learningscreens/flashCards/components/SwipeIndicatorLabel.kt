package com.example.flushcards.ui.screens.learningscreens.flashCards.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flushcards.R
import com.example.flushcards.ui.theme.FlushCardsTheme

@Composable
fun SwipeIndicatorLabel(
    isVisible: Boolean,
    isSwipingRight: Boolean,
    isSwipingLeft: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible && (isSwipingRight || isSwipingLeft),
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Text(
            text = stringResource(if (isSwipingRight) R.string.i_know else R.string.i_dont_know),
            color = if (isSwipingRight) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SwipeIndicatorLabelPreview() {
    FlushCardsTheme {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SwipeIndicatorLabel(
                isVisible = true,
                isSwipingRight = true,
                isSwipingLeft = false
            )

            SwipeIndicatorLabel(
                isVisible = true,
                isSwipingRight = false,
                isSwipingLeft = true
            )
        }
    }
}