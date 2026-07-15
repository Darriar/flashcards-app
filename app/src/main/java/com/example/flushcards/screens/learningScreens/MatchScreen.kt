package com.example.flushcards.screens.learningScreens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.example.flushcards.ui.theme.ButtonGradientStart
import com.example.flushcards.ui.theme.CorrectGreen
import com.example.flushcards.ui.theme.FlushCardsTheme
import com.example.flushcards.ui.theme.WrongRed
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val CARDS_IN_GROUP = 4

@Composable
fun MatchScreen(module: Module, onExit: () -> Unit) {

    if (module.hasNoCards()) return

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


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.secondary,
                        MaterialTheme.colorScheme.primary
                    )
                )
            )
            //.background(Color(0xFFF8FAFF))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MatchHeader(module.name, learnedCards.size, cardsCount, onExit)

        Column(
            modifier = Modifier
                // .padding(24.dp)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(100.dp))

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

                            if (cardsToLearn.isEmpty()) {
                                isFinished = true
                            }
                        }

                        checkedWordCard = null
                        checkedMeaningCard = null
                        isPairCorrect = null
                    }
                }
                
                Column(modifier = Modifier.weight(1f)) {
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

                Spacer(modifier = Modifier.width(20.dp))

                Column(modifier = Modifier.weight(1f)) {
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
        }
    }
}


fun checkAnswer(wordCard: FlashCard,
                meaningCard: FlashCard,
                cardsToLearn: MutableList<FlashCard>,
                learnedCards: MutableList<FlashCard>,
                onCorrect: () -> Unit,
                onWrong: () -> Unit): Boolean {
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
            .padding(vertical = 18.dp)
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier)

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Bold,
                )

                LinearProgressIndicator(
                    progress = { current.toFloat() / total },
                    modifier = Modifier
                        .width(170.dp)
                        .height(5.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surface,
                    strokeCap = StrokeCap.Round,
                )
            }
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
        targetValue =
            if (isSelected)
                MaterialTheme.colorScheme.primary
            else if (isCorrect == true)
                CorrectGreen.copy(alpha = 0.5f)
            else if (isCorrect == false)
                WrongRed.copy(alpha = 0.5f)
            else
                Color.White
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = cardColor,//if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
        shadowElevation = 0.dp,
        //border = if (isSelected) BorderStroke(2.dp, ButtonGradientStart) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1E2235),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(5.dp)
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))
}

fun takeCards(cardsToLearn: MutableList<FlashCard>, currentWords: MutableList<FlashCard>, currentMeanings: MutableList<FlashCard>) {

    if (cardsToLearn.size < CARDS_IN_GROUP) return

    val newCardWord = cardsToLearn
        .filter { !currentWords.contains(it) }
        .shuffled()
        .first()
    currentWords.add(newCardWord)

    var hasMatch = false

    for (cardWord in currentWords){
        for (cardMeaning in currentMeanings) {
            if (cardWord.meaning == cardMeaning.meaning)   {
                hasMatch = true
                break
            }
        }
    }

    var cardsToShuffle: MutableList<FlashCard> = cardsToLearn.toMutableList()
    if (hasMatch)
        cardsToShuffle = cardsToLearn.filter { !currentMeanings.contains(it) }.toMutableList()

    val newCardMeaning = cardsToShuffle
        .shuffled()
        .first()
    currentMeanings.add(newCardMeaning)

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
        FlushCardsTheme {
            MatchScreen(module = module, onExit = {})
        }
    }
