package com.example.flushcards.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Module(
    val id: Int,
    var name: String,
    var cards: MutableList<FlashCard>,
    var isTermFirst: Boolean = true
) {
    fun getCardsToLearn(): MutableList<FlashCard> {
        cards.forEach { it.resetFirstTry() }

        val newCards = cards.filter { it.roundsUntilReview <= 0 }.shuffled()
        return newCards.ifEmpty { cards }.toMutableList()
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

    fun addCard(card: ParsedCard) {
        val id = if (cards.isEmpty()) 1 else cards.maxOf { it.id } + 1
        cards.add(FlashCard(id = id, word = card.word, meaning = card.meaning))
    }

    fun resetProgress() {
        cards.forEach {
            it.progress = 0
            it.roundsUntilReview = 0
        }
    }

    fun trim() {
        name = name.trim()
        cards.forEach { card ->
            card.word = card.word.trim()
            card.meaning = card.meaning.trim()
        }
    }

    fun showTermFirst() {
        isTermFirst = true
    }

    fun showMeaningFirst() {
        isTermFirst = false
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Module

        if (isTermFirst != other.isTermFirst) return false
        if (name != other.name) return false
        if (cards.toList() != other.cards.toList()) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isTermFirst.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + cards.hashCode()
        return result
    }

}
