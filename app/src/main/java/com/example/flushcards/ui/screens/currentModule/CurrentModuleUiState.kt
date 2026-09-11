package com.example.flushcards.ui.screens.currentModule

import com.example.flushcards.data.model.FlashCard
import com.example.flushcards.data.model.Module

data class CurrentModuleUiState(
    val module: Module? = null,
    val cards: List<FlashCard> = emptyList(),
    val moduleName: String = "",
    val totalCardsCount: Int = 0,
    val isTermFirst: Boolean = true,
    val highlightedCardIndex: Int = -1,
    val isSearchActive: Boolean = false,

)