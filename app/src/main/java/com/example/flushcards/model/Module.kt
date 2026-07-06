package com.example.flushcards.model

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

// задать минимальный размер при инициализаци в окне карты необходимое колво
data class Module(
    var name: String,
    var cards: MutableList<FlashCard>
) {

    fun getCardsToLearn(): MutableList<FlashCard> {
        val newCards = cards.filter { it.roundsUntilReview <= 0 }.toMutableList()
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

    @Composable
    fun hasNoCards(): Boolean {

        if (cards.isEmpty()) {
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
        }
        return cards.isEmpty()
    }

}
