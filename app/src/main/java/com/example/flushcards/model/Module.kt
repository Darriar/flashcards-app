package com.example.flushcards.model

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

// сделать уникальное имя модуля
@Serializable
data class Module(
    var name: String,
    var cards: MutableList<FlashCard>,
    var isTermFirst: Boolean = true
) {

    fun getCardsToLearn(): MutableList<FlashCard> {
        cards.forEach { it.resetFirstTry()}

        var newCards = cards.filter { it.roundsUntilReview <= 0 }.toMutableList()
        if (!isTermFirst) {
            newCards.forEach { card ->
                card.word = card.meaning.also { card.meaning = card.word }
            }
        }
        return newCards.ifEmpty { cards }
    }

    fun finishLearning(cardsToLearn: List<FlashCard>, wrongAnswers: Int) {
        if (wrongAnswers == 0)
            cards.forEach {
                it.progress = 0
                it.roundsUntilReview = 0
            }
        else
            cards.forEach { if (!cardsToLearn.contains(it)) it.roundsUntilReview-- }
    }

    fun resetProgress() {
        cards.forEach {
            it.progress = 0
            it.roundsUntilReview = 0
        }
    }

    fun showTermFirst() {
        isTermFirst = true
    }

    fun showMeaningFirst() {
        isTermFirst = false
    }

}
