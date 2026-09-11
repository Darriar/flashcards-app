package com.example.flushcards.ui.screens.editModule

import com.example.flushcards.data.constants.ModuleConfig
import com.example.flushcards.data.model.Module

data class EditModuleUiState (
    val module: Module,
    val localModule: Module,
    val isSearchActive: Boolean = false,
    val isImportActive: Boolean = false,
    val highlightedCardIndex: Int = -1,
) {
    val validCardsCount: Int
        get() = localModule.cards.count { it.word.isNotBlank() && it.meaning.isNotBlank() }

    val isReadyEnabled: Boolean
        get() = (validCardsCount >= ModuleConfig.MIN_CARDS_COUNT) && localModule.name.isNotBlank()

}