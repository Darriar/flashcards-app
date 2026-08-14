package com.example.flushcards.ui.screens.learningscreens.flashCards.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flushcards.data.model.FlashCard
import com.example.flushcards.data.preview.SampleData
import com.example.flushcards.ui.theme.FlushCardsTheme
import kotlinx.coroutines.launch

@Composable
fun FlashCardView(
    card: FlashCard,
    isFlipped: Boolean,
    isTermFirst: Boolean,
    onFlip: () -> Unit,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    modifier: Modifier = Modifier
) {
    val offsetX = remember { Animatable(0f) }
    val cardAlpha = remember { Animatable(0f) }
    val rotation = remember { Animatable(0f) }

    var isLabelVisible by remember { mutableStateOf(true) }
    val flipDuration = 400
    val scope = rememberCoroutineScope()

    val density = LocalDensity.current
    val screenWidthPx = LocalWindowInfo.current.containerSize.width.toFloat()
    val swipeThreshold = screenWidthPx * 0.35f
    val minOffsetPx = with(density) { 25.dp.toPx() }

    LaunchedEffect(card) {
        isLabelVisible = true
        offsetX.snapTo(0f)
        rotation.snapTo(0f)
        cardAlpha.snapTo(0f)
        cardAlpha.animateTo(1f, animationSpec = tween(flipDuration))
    }

    LaunchedEffect(isFlipped) {
        val targetRotation = if (isFlipped) 180f else 0f
        if (rotation.value != targetRotation) {
            rotation.animateTo(targetRotation, tween(600))
        }
    }

    val isSwipingRight = offsetX.value > minOffsetPx
    val isSwipingLeft = offsetX.value < -minOffsetPx

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(420.dp)
            .padding(horizontal = 16.dp)
            .pointerInput(card) {
                detectTapGestures(onTap = { onFlip() })
            }
            .pointerInput(card) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        when {
                            offsetX.value > swipeThreshold -> {
                                scope.launch {
                                    isLabelVisible = false
                                    offsetX.animateTo(screenWidthPx * 1.5f, tween(flipDuration))
                                    onSwipeRight()
                                }
                            }

                            offsetX.value < -swipeThreshold -> {
                                scope.launch {
                                    isLabelVisible = false
                                    offsetX.animateTo(-screenWidthPx * 1.5f, tween(flipDuration))
                                    onSwipeLeft()
                                }
                            }

                            else -> {
                                scope.launch { offsetX.animateTo(0f, tween(flipDuration)) }
                            }
                        }
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        scope.launch { offsetX.snapTo(offsetX.value + dragAmount) }
                    }
                )
            }
            .graphicsLayer {
                translationX = offsetX.value
                rotationZ = (offsetX.value / screenWidthPx) * 15f
                alpha = cardAlpha.value
            }
    ) {
        FlashCardFace(
            card = card,
            isTermFirst = isTermFirst,
            rotationValue = rotation.value,
            density = density
        )

        SwipeIndicatorLabel(
            isVisible = isLabelVisible,
            isSwipingRight = isSwipingRight,
            isSwipingLeft = isSwipingLeft,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 24.dp)
        )
    }
}