package com.example.flushcards.data.model

import kotlinx.serialization.Serializable

@Serializable
data class FlashCard(
    val id: Int,
    var word: String,
    var meaning: String,
    var progress: Int = 0,
    var roundsUntilReview: Int = 0,
    var isFirstTry: Boolean = true
) {
    fun resetFirstTry() {
        isFirstTry = true
    }

    fun rightAnswer() {
        if (isFirstTry) {
            progress++
            roundsUntilReview = progress
        }
    }

    fun wrongAnswer() {
        isFirstTry = false
        progress = 0
        roundsUntilReview = 0
    }

    fun getFront(isTermFirst: Boolean): String = if (isTermFirst) word else meaning
    fun getBack(isTermFirst: Boolean): String = if (isTermFirst) meaning else word
}
