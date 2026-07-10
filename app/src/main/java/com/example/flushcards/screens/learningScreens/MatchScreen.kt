package com.example.flushcards.screens.learningScreens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.traceEventEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flushcards.model.FlashCard
import com.example.flushcards.model.Module
import com.example.flushcards.model.Screen
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

    val learnedCards = remember(module, sessionTrigger) {mutableListOf<FlashCard>() }

    val currentWords = remember { mutableStateListOf<FlashCard>() }
    val currentMeanings = remember { mutableStateListOf<FlashCard>() }

    LaunchedEffect(cardsToLearn) {
        currentWords.clear()
        currentMeanings.clear()

        val startWords = cardsToLearn.shuffled().take(CARDS_IN_GROUP).toMutableList()

        currentWords.addAll(startWords.shuffled())
        currentMeanings.addAll(startWords.shuffled())
    }

    if (isFinished) {
        module.finishLearning(learnedCards, wrongAnswers)

        FinishLearning(rightAnswers, wrongAnswers,
            onRetry = {
                rightAnswers = 0
                wrongAnswers = 0
                sessionTrigger++
                selectedWord = null
                selectedMeaning = null
                isFinished = false
            },
            onExit)
        return
    }



    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row() {
            Column() {
                currentWords.forEach { card ->
                    val isSelected = selectedWord == card
                    Button(
                        onClick = {
                            selectedWord = card
                            if (selectedMeaning != null) {
                                val isFirstTry = selectedWord!!.isFirstTry
                                val isCorrect = checkAnswer(cardsToLearn, learnedCards, currentWords, currentMeanings,selectedWord!!, selectedMeaning!!)
                                if (isFirstTry ) {
                                    if (isCorrect) {
                                        rightAnswers++
                                    }
                                    else
                                        wrongAnswers++
                                }

                                if (cardsToLearn.isEmpty())
                                    isFinished = true

                                selectedWord = null
                                selectedMeaning = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Color.Blue else Color.Gray
                        )
                    ) { Text(card.word) }
                }
            }

            Column() {
                currentMeanings.forEach { card ->
                    val isSelected = selectedMeaning == card
                    Button(
                        onClick = {
                            selectedMeaning = card
                            if (selectedWord != null) {
                                val isFirstTry = selectedWord!!.isFirstTry
                                val isCorrect = checkAnswer(cardsToLearn, learnedCards, currentWords, currentMeanings, selectedWord!!, selectedMeaning!!)
                                if (isFirstTry ) {
                                    if (isCorrect) {
                                        rightAnswers++
                                    }
                                    else
                                        wrongAnswers++
                                }

                                if (cardsToLearn.isEmpty())
                                    isFinished = true

                                selectedWord = null
                                selectedMeaning = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Color.Blue else Color.Gray
                        )
                    ) { Text(card.meaning) }
                }
            }
        }




    }
}

fun checkAnswer(cardsToLearn: MutableList<FlashCard>,  learnedCards: MutableList<FlashCard>, currentWords: MutableList<FlashCard>, currentMeanings: MutableList<FlashCard>, cardWord: FlashCard, cardMeaning: FlashCard): Boolean {

    val card = cardsToLearn.find { it == cardWord }
    if (cardWord.meaning == cardMeaning.meaning) {
        card!!.rightAnswer()
        learnedCards.add(card)

        cardsToLearn.remove(card)
        currentWords.remove(cardWord)
        currentMeanings.remove(cardMeaning)

        takeCards(cardsToLearn, currentWords, currentMeanings)
        //light right
        return true
    } else {
        card!!.wrongAnswer()
        // light wrong
        return false
    }
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
    MatchScreen(Module("testModule", mutableListOf(FlashCard(1, "test", "тестовый"))), onExit = {})
}