package com.example.flushcards.model

data class FlashCard (
    val id: Int,
    var word: String,
    var meaning: String,
    var progress: Int = 0,
    var roundsUntilReview: Int = 0
)
