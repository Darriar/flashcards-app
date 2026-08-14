package com.example.flushcards.ui.screens.learningscreens.flashCards

import com.example.flushcards.data.model.FlashCard

data class FlashCardsUiState(
    val moduleName: String = "",
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val currentIndex: Int = 0,
    val currentCard: FlashCard? = null,
    val isFlipped: Boolean = false,
    val isFinished: Boolean = false,
    val totalCardsCount: Int = 0
)
