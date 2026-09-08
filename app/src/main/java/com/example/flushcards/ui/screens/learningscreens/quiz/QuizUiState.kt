package com.example.flushcards.ui.screens.learningscreens.quiz

import com.example.flushcards.data.model.FlashCard

data class QuizUiState (
    val moduleName: String = "",
    val totalCardsCount: Int = 0,
    val isTermFirst: Boolean = false,
    val options: List<String> = listOf(),
    val selectedOption: String? = null,
    val currentCard: FlashCard? = null,
    val currentIndex: Int = 0,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val isFinished: Boolean = false
)