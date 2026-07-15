package com.example.flushcards.screens.learningScreens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flushcards.model.FlashCard
import com.example.flushcards.model.Module
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
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = currentCard.word)

        Spacer(modifier = Modifier.width(20.dp))

        answers.forEach { answer ->
            val buttonColor = when {
                selectedAnswer == answer && answer == currentCard.meaning -> 
                    ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                selectedAnswer == answer -> 
                    ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                else -> ButtonDefaults.buttonColors()
            }
            
            Button(
                onClick = {
                    if (selectedAnswer != null) return@Button
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
                colors = buttonColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(text = answer, fontSize = 18.sp)
            }
        }
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
