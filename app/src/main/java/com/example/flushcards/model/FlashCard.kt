package com.example.flushcards.model

data class FlashCard (
    val id: Int,
    var word: String,
    var meaning: String,
    var progress: Int = 0,
    var roundsUntilReview: Int = 0
) {
    fun rightAnswer() {
        progress++
        roundsUntilReview = progress
    }

    fun wrongAnswer() {
        progress = 0
        roundsUntilReview = 0
    }
}
