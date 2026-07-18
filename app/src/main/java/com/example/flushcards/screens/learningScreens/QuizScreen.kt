package com.example.flushcards.screens.learningScreens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flushcards.model.FlashCard
import com.example.flushcards.model.Module
import com.example.flushcards.ui.theme.FlushCardsTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.inc
import kotlin.text.compareTo

@Composable
fun QuizScreen(module: Module, onExit: () -> Unit) {

    if (module.cards.isEmpty()) return

    var sessionTrigger by remember { mutableIntStateOf(0) }
    var isFinished by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var rightAnswers by remember { mutableIntStateOf(0) }
    var wrongAnswers by remember { mutableIntStateOf(0) }

    val cardsToLearn = remember(module, sessionTrigger) {
        module.getCardsToLearn().shuffled()
    }

    if (isFinished) {
        module.finishLearning(cardsToLearn, wrongAnswers)

        LearningResultScreen(
            rightAnswers,
            wrongAnswers,
            onRetry = {
                sessionTrigger++
                isFinished = false
                currentIndex = 0
                rightAnswers = 0
                wrongAnswers = 0
                selectedAnswer = null
            },
            onExit = onExit
        )
        return
    }

    val currentCard = cardsToLearn[currentIndex]

    val answers = remember(currentCard) {
        (module.cards
            .filter { it.meaning != currentCard.meaning }
            .map { it.meaning }
            .distinct()
            .shuffled()
            .take(3) + currentCard.meaning).shuffled()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FlashCardsHeader(
            title = module.name,
            current = currentIndex + 1,
            total = cardsToLearn.size,
            onBack = { onExit() }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp, horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentCard.word,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(id = com.example.flushcards.R.string.quiz_label),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            textAlign = TextAlign.Start
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            answers.forEach { answer ->
                AnswerCard(answer, selectedAnswer, currentCard,
                    onClick =  {
                        selectedAnswer = answer
                        if (answer == currentCard.meaning) {
                            currentCard.rightAnswer()
                            rightAnswers++
                        } else {
                            currentCard.wrongAnswer()
                            wrongAnswers++
                        }

                        scope.launch {
                            delay(600)
                            selectedAnswer = null
                            if (currentIndex < cardsToLearn.size - 1) {
                                currentIndex++
                            } else {
                                isFinished = true
                            }
                        }
                    })
            }
        }

        Spacer(modifier = Modifier.weight(1f))

    }
}

@Composable
fun AnswerCard(answer: String, selectedAnswer: String?, currentCard: FlashCard, onClick: () -> Unit) {
    val isCurrent = selectedAnswer == answer
    val isCorrect = answer == currentCard.meaning

    val buttonColors = when {
        (isCurrent && isCorrect) || (selectedAnswer != null && isCorrect) -> ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.8f),
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
        isCurrent && !isCorrect -> ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f),
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
        else -> {
            ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        }
    }

    val borderColors = when {
        (isCurrent && isCorrect) || (selectedAnswer != null && isCorrect) -> Color(0xFF81C784)
        isCurrent && !isCorrect -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outlineVariant
    }

    OutlinedButton(
        onClick = {
            if (selectedAnswer != null) return@OutlinedButton
            onClick()
        },
        colors = buttonColors,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 8.dp),
        border = BorderStroke(
            width = 2.dp,
            color = borderColors
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = answer,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizScreenPreview() {
    FlushCardsTheme {
        QuizScreen(
            Module("testModule", mutableListOf(FlashCard(1, "test", "тестовый"))),
            onExit = {}
        )
    }
}