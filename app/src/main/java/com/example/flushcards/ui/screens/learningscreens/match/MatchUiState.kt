package com.example.flushcards.ui.screens.learningscreens.match

import com.example.flushcards.data.model.FlashCard

data class MatchUiState(
    val moduleName: String = "",
    val isTermFirst: Boolean = true,
    val currentWords: List<FlashCard?> = emptyList(),
    val currentMeanings: List<FlashCard?> = emptyList(),
    val selectedWord: FlashCard? = null,
    val selectedMeaning: FlashCard? = null,
    val checkedWord: FlashCard? = null,
    val checkedMeaning: FlashCard? = null,
    val isPairCorrect: Boolean? = null,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val totalCardsCount: Int = 0,
    val learnedCardsCount: Int = 0,
    val isFinished: Boolean = false,
    val isProcessing: Boolean = false
)
