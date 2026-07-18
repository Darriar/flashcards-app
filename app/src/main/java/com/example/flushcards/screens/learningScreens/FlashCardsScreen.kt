package com.example.flushcards.screens.learningScreens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flushcards.R
import com.example.flushcards.model.FlashCard
import com.example.flushcards.model.Module
import com.example.flushcards.ui.theme.FlushCardsTheme
import kotlinx.coroutines.launch

@Composable
fun FlashCardsScreen(module: Module, onExit: () -> Unit) {

    if (module.cards.isEmpty()) return

    var sessionTrigger by remember { mutableIntStateOf(0) }
    var isFinished by remember { mutableStateOf(false) }
    var rightAnswers by remember { mutableIntStateOf(0) }
    var wrongAnswers by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    var currentIndex by remember { mutableIntStateOf(0) }

    val cardsToLearn = remember(module, sessionTrigger) {
        module.getCardsToLearn()
    }

    if (isFinished) {
        module.finishLearning(cardsToLearn, wrongAnswers)

        LearningResultScreen(
            correctCount = rightAnswers,
            wrongCount = wrongAnswers,
            onRetry = {
                sessionTrigger++
                isFinished = false
                currentIndex = 0
                rightAnswers = 0
                wrongAnswers = 0
                isFlipped = false
            },
            onExit = onExit
        )
        return
    }

    val currentCard = cardsToLearn[currentIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FlashCardsHeader(
            title = module.name,
            current = currentIndex + 1,
            total = cardsToLearn.size,
            onBack = onExit,
        )

        Spacer(modifier = Modifier.weight(1f))

        FlashCardView(
            card = currentCard,
            isFlipped = isFlipped,
            onFlip = { isFlipped = !isFlipped },
            onSwipeLeft = {
                currentCard.rightAnswer()
                rightAnswers++
                isFlipped = false
                if (currentIndex < cardsToLearn.size - 1) {
                    currentIndex++
                } else {
                    isFinished = true
                }
            },
            onSwipeRight = {
                currentCard.wrongAnswer()
                wrongAnswers++
                isFlipped = false
                if (currentIndex < cardsToLearn.size - 1) {
                    currentIndex++
                } else {
                    isFinished = true
                }
            }
        )

        Spacer(modifier = Modifier.weight(1f))
    }

}

@Composable
fun FlashCardsHeader(
    title: String,
    current: Int,
    total: Int,
    onBack: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Row (
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Back",
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .clickable { onBack() },
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                textAlign = TextAlign.Center
            )

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$current ",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "/ $total",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                LinearProgressIndicator(
                    progress = { if (total > 0) current.toFloat() / total else 0f },
                    modifier = Modifier
                        .width(48.dp)
                        .height(5.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.outlineVariant,
                    strokeCap = StrokeCap.Round
                )
            }
        }
    }
}

@Composable
fun FlashCardView(
    card: FlashCard,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
) {
    val offsetX = remember { Animatable(0f) }
    val cardAlpha = remember { Animatable(0f) }
    var isLabelVisible by remember { mutableStateOf(true) }
    val flipDuration = 400
    val scope = rememberCoroutineScope()
    val rotationYs = remember(offsetX.value) {
        (offsetX.value / 10).coerceIn(-15f, 15f)
    }

    LaunchedEffect(card) {
        isLabelVisible = true
        offsetX.snapTo(0f)
        cardAlpha.snapTo(0f)
        cardAlpha.animateTo(1f, animationSpec = tween(flipDuration))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp)
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(28.dp))
            .clickable { onFlip() }
            .graphicsLayer {
                translationX = offsetX.value
                rotationY = rotationYs
                cameraDistance = 12 * density
                alpha = cardAlpha.value
            }
            .pointerInput(card) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        when {
                            offsetX.value > 300f -> {
                                scope.launch {
                                    isLabelVisible = false
                                    offsetX.animateTo(1000f, tween(flipDuration))
                                    onSwipeRight()
                                }
                            }
                            offsetX.value < -300f -> {
                                scope.launch {
                                    isLabelVisible = false
                                    offsetX.animateTo(-1000f, tween(flipDuration))
                                    onSwipeLeft()
                                }
                            }
                            else -> {
                                scope.launch { offsetX.animateTo(0f, tween(flipDuration)) }
                            }
                        }
                    }, onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        scope.launch { offsetX.snapTo(offsetX.value + dragAmount) }
                    }
                )
            },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            val minOffset = 25

            this@Card.AnimatedVisibility(
                visible = isLabelVisible && (offsetX.value > minOffset || offsetX.value < -minOffset),
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
            ) {
                Text(
                    text = if (offsetX.value > minOffset) stringResource(R.string.i_know) else stringResource(R.string.i_dont_know),
                    color = if (offsetX.value > minOffset) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = if (!isFlipped) card.word else card.meaning,
                fontSize = 30.sp,
                lineHeight = 36.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 16.dp)
            )

            Text(
                text = stringResource(id = R.string.tap_to_flip),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.align(Alignment.BottomCenter),
                letterSpacing = 1.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FlashCardsScreenPreview() {
    FlushCardsTheme {
        FlashCardsScreen(
            module = Module(
                name = "Spanish Essentials",
                cards = mutableListOf(
                    FlashCard(1, "hola", "hello"),
                    FlashCard(2, "gracias", "thank you")
                )
            ),
            onExit = {}
        )
    }
}