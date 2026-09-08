package com.example.flushcards.ui.screens.learningscreens.write

import com.example.flushcards.data.model.FlashCard

data class WriteUiState (
    val moduleName: String = "",
    val isTermFirst: Boolean = true,
    val totalCardsCount: Int = 0,
    val currentIndex: Int = 0,
    val currentCard: FlashCard? = null,
    val guessMeaning: String = "",
    val answerState: Boolean? = null,
    val showAnswer: Boolean = false,
    var isProcessing: Boolean = false,
    val correctAnswers: Int = 0,
    val wrongAnswers: Int = 0,
    val isFinished: Boolean = false,
)