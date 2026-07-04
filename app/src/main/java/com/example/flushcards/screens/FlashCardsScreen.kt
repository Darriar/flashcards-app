package com.example.flushcards.screens

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flushcards.model.FlashCard
import com.example.flushcards.model.Module
import com.example.flushcards.ui.theme.FlushCardsTheme

@Composable
fun FlashCardsScreen(module: Module, onExit: () -> Unit = {}) {

    if (module.cards.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "В этом модуле нет карточек",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }
        return
    }

    var isFinished by remember { mutableStateOf(false) }
    var rightAnswers by remember { mutableIntStateOf(0) }
    var wrongAnswers by remember { mutableIntStateOf(0) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    var sessionTrigger by remember { mutableIntStateOf(0) }

    val cardsToLearn = remember(module, sessionTrigger) {
        val filtered = module.cards.filter { it.roundsUntilReview <= 0 }
        filtered.ifEmpty { module.cards }
    }

    if (isFinished) {
        if (wrongAnswers == 0)
            module.cards.forEach {
                it.progress = 0
                it.roundsUntilReview = 0
            }
        else
            module.cards.forEach { if (!cardsToLearn.contains(it)) it.roundsUntilReview-- }

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
                    Text(currentCard.progress.toString())
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
                    currentCard.progress = 0
                    currentCard.roundsUntilReview = 0
                    wrongAnswers++
                    if (currentIndex < cardsToLearn.size - 1) {
                        currentIndex++
                    } else {
                        isFinished = true
                    }
                    isFlipped = false
                },
                modifier = Modifier.weight(1f)
            ) { Text("Не знаю") }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    currentCard.progress++
                    currentCard.roundsUntilReview = currentCard.progress
                    rightAnswers++
                    if (currentIndex < cardsToLearn.size - 1) {
                        currentIndex++
                    } else {
                        isFinished = true
                    }
                    isFlipped = false
                },
                modifier = Modifier.weight(1f)
            ) { Text("Знаю") }
        }
    }
}

@Composable
fun FinishLearning(rightAnswers: Int, wrongAnswers: Int, onRetry: () -> Unit, onExit: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Вы прошли модуль!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Всего изучено: ${rightAnswers + wrongAnswers}",
            fontSize = 20.sp
        )
        Text(
            text = "Знаете: $rightAnswers",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Не знаете: $wrongAnswers",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Продолжить изучение")
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        OutlinedButton(
            onClick = onExit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("На сегодня хватит")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    FlushCardsTheme {
        FlashCardsScreen(
            Module("testModule", mutableListOf(FlashCard(1, "test", "тестовый")))
        )
    }
}
