package com.example.flushcards.screens.learningScreens

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.example.flushcards.ui.theme.Black
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun QuizScreen(module: Module, onExit: () -> Unit) {

    if (module.hasNoCards()) return

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
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.secondary,
                        MaterialTheme.colorScheme.primary
                    )
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FlashCardsHeader(module.name, currentIndex + 1, cardsToLearn.size, onBack = { onExit() })


        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, top = 42.dp, bottom = 16.dp, end = 24.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Transparent),
                //.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = currentCard.word,
                color = Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(10.dp)
                )
        }

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Transparent),
            //.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(id = com.example.flushcards.R.string.quiz_label),
                color = Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(10.dp)
            )
        }

       // Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            answers.forEach { answer ->
                val buttonColor = when {
                    selectedAnswer == answer && answer == currentCard.meaning ->
                        ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)

                    selectedAnswer == answer ->
                        ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)

                    else -> ButtonDefaults.buttonColors()
                }

                OutlinedButton(
                    onClick = {
                        if (selectedAnswer != null) return@OutlinedButton
                        selectedAnswer = answer
                        if (answer == currentCard.meaning) {
                            currentCard.rightAnswer()
                            rightAnswers++
                        } else {
                            currentCard.wrongAnswer()
                            wrongAnswers++
                        }

                        scope.launch {
                            delay(500)
                            selectedAnswer = null
                            if (currentIndex < cardsToLearn.size - 1) {
                                currentIndex++
                            } else {
                                isFinished = true
                            }
                        }
                    },
                    //colors = buttonColor,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .padding(horizontal = 24.dp, vertical = 10.dp)
                        .border(
                            color = Color.LightGray,
                            width = 2.dp,
                            shape = RoundedCornerShape(18.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = answer,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Black,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(5.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun QuizScreenPreview() {
    QuizScreen(
        Module("testModule", mutableListOf(FlashCard(1, "test", "тестовый"))),
        onExit = {}
    )
}
