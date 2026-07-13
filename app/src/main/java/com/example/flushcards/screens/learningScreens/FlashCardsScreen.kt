package com.example.flushcards.screens.learningScreens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

    if (module.hasNoCards()) return

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

        FinishLearning(
            rightAnswers = rightAnswers,
            wrongAnswers = wrongAnswers,
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
        modifier = Modifier.fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE8EBFA),
                        Color(0xFFD1D9F0),
                        Color(0xFFAAB6E0),
                        Color(0xFF8A98D4)
                    )
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        FlashCardsHeader(
            title = module.name,
            current = currentIndex + 1,
            total = cardsToLearn.size,
            onBack = onExit,
        )

        SwipeableCard(
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
                } },
            onSwipeRight = {
                currentCard.wrongAnswer()
                wrongAnswers++
                isFlipped = false
                if (currentIndex < cardsToLearn.size - 1) {
                    currentIndex++
                } else {
                    isFinished = true
                } }
        )
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
            .padding(18.dp)
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(40.dp),
        color = Color.White.copy(alpha = 0.9f),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Back",
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .clickable { onBack() },
                tint = Color.DarkGray,
            )

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.DarkGray,
                fontWeight = FontWeight.Bold
            )

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$current ",
                        color = Color(0xFF7E91D4),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "/ $total",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
                LinearProgressIndicator(
                    progress = { current.toFloat() / total },
                    modifier = Modifier
                        .width(48.dp)
                        .height(3.dp),
                    color = Color(0xFF7E91D4),
                    trackColor = Color(0xFFE0E0E0),
                    strokeCap = StrokeCap.Square
                )
            }
        }
    }
}

@Composable
fun SwipeableCard(
    card: FlashCard,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
) {
    val offsetX = remember { Animatable(0f) }
    val flipDuration = 400
    val scope = rememberCoroutineScope()
    val rotationYs = remember (offsetX.value) {
         (offsetX.value / 10).coerceIn(-15f, 15f)
    }
        Card(
            modifier = Modifier
                .padding(vertical = 72.dp, horizontal = 32.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .clickable { onFlip() }
                .graphicsLayer {
                    translationX = offsetX.value
                    scaleX = 1f
                    scaleY = 1f
                    rotationY = rotationYs
                    cameraDistance = 12 * density
                }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            when {
                                offsetX.value > 300f -> {
                                    scope.launch {
                                        offsetX.animateTo(1000f, tween(flipDuration))
                                        offsetX.snapTo(-1000f)
                                        offsetX.animateTo(0f, tween (flipDuration))
                                        onSwipeRight()
                                    }
                                }

                                offsetX.value < -300f -> {
                                    scope.launch {
                                        offsetX.animateTo(-1000f, tween(flipDuration))
                                        offsetX.snapTo(1000f)
                                        offsetX.animateTo(0f, tween (flipDuration))
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
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
            ) {
                Text(
                    text = if (!isFlipped) card.word else card.meaning,
                    fontSize = 38.sp,
                    lineHeight = 44.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Serif,
                    color = Color(0xFF1A1C2E),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )

                Text(
                    text = stringResource(id = R.string.tap_to_flip),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray,
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

