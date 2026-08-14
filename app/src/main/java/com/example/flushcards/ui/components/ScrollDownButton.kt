package com.example.flushcards.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flushcards.data.model.FlashCard
import com.example.flushcards.ui.theme.FlushCardsTheme
import kotlinx.coroutines.launch

@Composable
fun ScrollDownButton(
    state: LazyListState,
    cards: List<FlashCard>,
    modifier: Modifier = Modifier
) {
    val isLastCardVisible by remember(state, cards.size) {
        derivedStateOf {
            if (cards.isEmpty()) return@derivedStateOf false

            val lastCardId = cards.last().id
            val lastCardInfo = state.layoutInfo.visibleItemsInfo.find { it.key == lastCardId }
            if (lastCardInfo != null) {
                val cardBottom = lastCardInfo.offset + lastCardInfo.size
                cardBottom <= state.layoutInfo.viewportEndOffset
            } else {
                false
            }
        }
    }

    AnimatedVisibility(
        visible = !isLastCardVisible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        ButtonContent(state = state)
    }
}

@Composable
fun ButtonContent(state: LazyListState) {
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        FloatingActionButton(
            onClick = {
                scope.launch {
                    if (state.layoutInfo.totalItemsCount > 0) {
                        state.animateScrollToItem(state.layoutInfo.totalItemsCount - 1)
                    }
                }
            },
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.shadow(
                elevation = 6.dp,
                shape = CircleShape
            )
        ) {
            Icon(
                imageVector = Icons.Default.ArrowDownward,
                contentDescription = "Прокрутить вниз",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ButtonContentPreview() {
    FlushCardsTheme {
        ButtonContent(
            state = rememberLazyListState()
        )
    }
}