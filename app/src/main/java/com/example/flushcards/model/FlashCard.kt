package com.example.flushcards.model

import kotlinx.serialization.Serializable

@Serializable
data class FlashCard (
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
}
