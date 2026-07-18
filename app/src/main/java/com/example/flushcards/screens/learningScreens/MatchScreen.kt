package com.example.flushcards.screens.learningScreens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val CARDS_IN_GROUP = 4

@Composable
fun MatchScreen(module: Module, onExit: () -> Unit) {

    if (module.cards.isEmpty()) return

    var sessionTrigger by remember { mutableIntStateOf(0) }
    var isFinished by remember { mutableStateOf(false) }

    var selectedWord by remember { mutableStateOf<FlashCard?>(null) }
    var selectedMeaning by remember { mutableStateOf<FlashCard?>(null) }

    var rightAnswers by remember { mutableIntStateOf(0) }
    var wrongAnswers by remember { mutableIntStateOf(0) }

    val cardsToLearn = remember(module, sessionTrigger) {
        module.getCardsToLearn().shuffled().toMutableList()
    }

    val cardsCount by remember(module, sessionTrigger) { mutableIntStateOf(cardsToLearn.size) }

    val currentWords = remember(cardsToLearn) { mutableStateListOf<FlashCard>() }
    val currentMeanings = remember(cardsToLearn) { mutableListOf<FlashCard>() }

    val learnedCards = remember(module, sessionTrigger) { mutableListOf<FlashCard>() }

    LaunchedEffect(cardsToLearn) {
        val startWords = cardsToLearn.shuffled().take(CARDS_IN_GROUP).toMutableList()
        currentWords.addAll(startWords.shuffled())
        currentMeanings.addAll(startWords.shuffled())
    }

    if (isFinished) {
        module.finishLearning(learnedCards, wrongAnswers)

        LearningResultScreen(
            rightAnswers, wrongAnswers,
            onRetry = {
                rightAnswers = 0
                wrongAnswers = 0
                sessionTrigger++
                selectedWord = null
                selectedMeaning = null
                isFinished = false
            },
            onExit
        )
        return
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {   MatchHeader(module.name, learnedCards.size, cardsCount, onExit)

        Spacer(modifier = Modifier.weight(0.5f))

        Text(
            text = "Соедините термины и их значения",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 16.dp),
            textAlign = TextAlign.Center
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val scope = rememberCoroutineScope()

            var checkedWordCard by remember { mutableStateOf<FlashCard?>(null) }
            var checkedMeaningCard by remember { mutableStateOf<FlashCard?>(null) }
            var isPairCorrect by remember { mutableStateOf<Boolean?>(null) }

            val processAnswerResult: (FlashCard, FlashCard, Boolean) -> Unit = { wordCard, meaningCard, isCorrect ->
                scope.launch {
                    checkedWordCard = wordCard
                    checkedMeaningCard = meaningCard
                    isPairCorrect = isCorrect

                    selectedWord = null
                    selectedMeaning = null

                    delay(500)

                    if (isCorrect) {
                        currentWords.remove(wordCard)
                        currentMeanings.remove(meaningCard)
                        takeCards(cardsToLearn, currentWords, currentMeanings)

                        if (cardsToLearn.isEmpty() && currentWords.isEmpty()) {
                            isFinished = true
                        }
                    }

                    checkedWordCard = null
                    checkedMeaningCard = null
                    isPairCorrect = null
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                currentWords.forEach { card ->
                    val isSelected = selectedWord == card
                    val isCorrect = if (checkedWordCard == card) isPairCorrect else null

                    MatchCard(
                        text = card.word,
                        isSelected = isSelected,
                        isCorrect = isCorrect,
                        onClick = {
                            if (isPairCorrect != null) return@MatchCard

                            selectedWord = card
                            if (selectedMeaning != null) {
                                val meaningCard = selectedMeaning!!

                                val isCorrectResult = checkAnswer(
                                    card, meaningCard, cardsToLearn, learnedCards,
                                    onCorrect = { rightAnswers++ },
                                    onWrong = { wrongAnswers++ })

                                processAnswerResult(card, meaningCard, isCorrectResult)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                currentMeanings.forEach { card ->
                    val isSelected = selectedMeaning == card
                    val isCorrect = if (checkedMeaningCard == card) isPairCorrect else null

                    MatchCard(
                        text = card.meaning,
                        isSelected = isSelected,
                        isCorrect = isCorrect,
                        onClick = {
                            if (isPairCorrect != null) return@MatchCard

                            selectedMeaning = card
                            if (selectedWord != null) {
                                val wordCard = selectedWord!!

                                val isCorrectResult = checkAnswer(wordCard,
                                    card, cardsToLearn, learnedCards,
                                    onCorrect = { rightAnswers++ },
                                    onWrong = { wrongAnswers++ })

                                processAnswerResult(wordCard, card, isCorrectResult)
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1.2f))
    }

}

fun checkAnswer(
    wordCard: FlashCard,
    meaningCard: FlashCard,
    cardsToLearn: MutableList<FlashCard>,
    learnedCards: MutableList<FlashCard>,
    onCorrect: () -> Unit,
    onWrong: () -> Unit
): Boolean {
    val isCorrectResult = wordCard.meaning == meaningCard.meaning

    val isFirstTry = wordCard.isFirstTry
    val originalCard = cardsToLearn.find { it == wordCard }
    if (isCorrectResult) {
        originalCard?.rightAnswer()
        originalCard?.let { learnedCards.add(it) }
        cardsToLearn.remove(originalCard)
        if (isFirstTry) onCorrect()
    } else {
        originalCard?.wrongAnswer()
        if (isFirstTry) onWrong()
    }

    return isCorrectResult
}

@Composable
fun MatchHeader(
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

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { if (total > 0) current.toFloat() / total else 0f },
                    modifier = Modifier
                        .width(140.dp)
                        .height(6.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.outlineVariant,
                    strokeCap = StrokeCap.Round,
                )
            }

            Spacer(modifier = Modifier)
        }
    }
}

@Composable
fun MatchCard(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean?,
    onClick: () -> Unit,
) {

    val cardColor by animateColorAsState(
        targetValue = when {
            isSelected -> MaterialTheme.colorScheme.primary
            isCorrect == true -> MaterialTheme.colorScheme.tertiaryContainer
            isCorrect == false -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f)
            else -> MaterialTheme.colorScheme.surface
        },
        label = "CardColorAnimation"
    )

    val textColor by animateColorAsState(
        targetValue = when {
            isSelected -> MaterialTheme.colorScheme.onPrimary
            isCorrect == true -> MaterialTheme.colorScheme.onTertiaryContainer
            isCorrect == false -> MaterialTheme.colorScheme.onErrorContainer
            else -> MaterialTheme.colorScheme.onSurface
        },
        label = "TextColorAnimation"
    )

    val borderStroke = when {
        isSelected -> BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        isCorrect == true -> BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary)
        isCorrect == false -> BorderStroke(2.dp, MaterialTheme.colorScheme.error)
        else -> BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = cardColor,
        border = borderStroke,
        shadowElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

fun takeCards(
    cardsToLearn: MutableList<FlashCard>,
    currentWords: MutableList<FlashCard>,
    currentMeanings: MutableList<FlashCard>
) {
    if (cardsToLearn.isEmpty()) return

    val remainingWords = cardsToLearn.filter { !currentWords.contains(it) }
    if (remainingWords.isEmpty()) return

    val newCardWord = remainingWords.shuffled().first()
    currentWords.add(newCardWord)

    var hasMatch = false
    for (cardWord in currentWords) {
        for (cardMeaning in currentMeanings) {
            if (cardWord.meaning == cardMeaning.meaning) {
                hasMatch = true
                break
            }
        }
    }

    var cardsToShuffle = cardsToLearn.toMutableList()
    if (hasMatch) {
        cardsToShuffle = cardsToLearn.filter { !currentMeanings.contains(it) }.toMutableList()
    }

    if (cardsToShuffle.isNotEmpty()) {
        val newCardMeaning = cardsToShuffle.shuffled().first()
        currentMeanings.add(newCardMeaning)
    }
}

@Preview(showBackground = true)
@Composable
fun MatchScreenPreview() {
    val cards = mutableListOf(
        FlashCard(1, "Brave", "Showing courage in the face of danger."),
        FlashCard(2, "Generous", "Willing to give more of something to others."),
        FlashCard(3, "Patient", "Not easily upset and able to wait calmly."),
        FlashCard(4, "Curious", "Eager to know or learn about something.")
    )

    val module = Module("Match Test", cards)
    MaterialTheme() {
        FlushCardsTheme {
            MatchScreen(module = module, onExit = {})
        }
    }
}