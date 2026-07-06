package com.example.flushcards.screens.learningScreens

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flushcards.model.FlashCard
import com.example.flushcards.model.Module
import com.example.flushcards.ui.theme.FlushCardsTheme

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
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 10.dp)
        ) {
            Text(
                text = "Запоминание слов",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Карточка ${currentIndex + 1} из ${cardsToLearn.size}",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .clickable{ isFlipped = !isFlipped},
            colors = CardDefaults.cardColors(
                containerColor = if (isFlipped) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = if (isFlipped) currentCard.meaning else currentCard.word,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isFlipped) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedButton(
                onClick = {
                    currentCard.wrongAnswer()
                    wrongAnswers++
                    isFlipped = false

                    if (currentIndex < cardsToLearn.size - 1) {
                        currentIndex++
                    } else {
                        isFinished = true
                    }
                },
                modifier = Modifier.weight(1f)
            ) { Text("Не знаю") }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    currentCard.rightAnswer()
                    rightAnswers++
                    isFlipped = false

                    if (currentIndex < cardsToLearn.size - 1) {
                        currentIndex++
                    } else {
                        isFinished = true
                    }
                },
                modifier = Modifier.weight(1f)
            ) { Text("Знаю") }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    FlushCardsTheme {
        FlashCardsScreen(
            Module("testModule", mutableListOf(FlashCard(1, "test", "тестовый"))),
            onExit = {}
        )
    }
}
