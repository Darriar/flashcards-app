package com.example.flushcards.ui.screens.editModule

import androidx.lifecycle.ViewModel
import com.example.flushcards.data.constants.ModuleConfig
import com.example.flushcards.data.model.FlashCard
import com.example.flushcards.data.model.Module
import com.example.flushcards.data.model.ParsedCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EditModuleViewModel(private val module: Module) : ViewModel() {
    private val _uiState = MutableStateFlow(
        EditModuleUiState(
            module = module,
            localModule = createLocalModule()
        )
    )
    val uiState: StateFlow<EditModuleUiState> = _uiState.asStateFlow()

    private fun createLocalModule(): Module {
        val initialCards = module.cards.map { it.copy() }.toMutableList()

        if (initialCards.size < ModuleConfig.MIN_CARDS_COUNT) {
            val startId = if (initialCards.isEmpty()) 1 else initialCards.maxOf { it.id } + 1
            for (i in 0 until (ModuleConfig.MIN_CARDS_COUNT - initialCards.size)) {
                initialCards.add(FlashCard(startId + i, "", ""))
            }
        }

        return module.copy(cards = initialCards)
    }

    fun onSearchClick() {
        _uiState.update { it.copy(isSearchActive = true) }
    }

    fun canselSearch() {
        _uiState.update { it.copy(isSearchActive = false) }
    }

    fun onImportClick() {
        _uiState.update { it.copy(isImportActive = true) }
    }

    fun canselImport() {
        _uiState.update { it.copy(isImportActive = false) }
    }

    fun onSelectCard(index: Int) {
        _uiState.update { it.copy(highlightedCardIndex = index) }
    }

    fun canselSelectCard() {
        _uiState.update { it.copy(highlightedCardIndex = -1) }
    }

    fun onNameChange(newName: String) {
        _uiState.update { state ->
            state.copy(localModule = state.localModule.copy(name = newName))
        }
    }

    fun onWordChange(index: Int, card: FlashCard, newWord: String) {
        _uiState.update { state ->
            val updatedCards = state.localModule.cards.toMutableList()
            updatedCards[index] = card.copy(word = newWord)
            state.copy(localModule = state.localModule.copy(cards = updatedCards))
        }
    }

    fun onMeaningChange(index: Int, card: FlashCard, newMeaning: String) {
        _uiState.update { state ->
            val updatedCards = state.localModule.cards.toMutableList()
            updatedCards[index] = card.copy(meaning = newMeaning)
            state.copy(localModule = state.localModule.copy(cards = updatedCards))
        }
    }

    fun onDeleteCard(index: Int) {
        _uiState.update { state ->
            val updatedCards = state.localModule.cards.toMutableList()
            updatedCards.removeAt(index)
            state.copy(localModule = state.localModule.copy(cards = updatedCards))
        }
    }

    fun onAddCard() {
        _uiState.update { state ->
            val updatedCards = state.localModule.cards.toMutableList()
            val id = if (updatedCards.isEmpty()) 1 else updatedCards.maxOf { it.id } + 1
            updatedCards.add(FlashCard(id, "", ""))
            state.copy(localModule = state.localModule.copy(cards = updatedCards))
        }
    }

    fun onImportCards(parsedCards: List<ParsedCard>) {
        _uiState.update { state ->
            val updatedCards = state.localModule.cards.toMutableList()
            var nextId = if (updatedCards.isEmpty()) 1 else updatedCards.maxOf { it.id } + 1
            parsedCards.forEach {
                updatedCards.add(FlashCard(nextId++, it.word, it.meaning))
            }
            state.copy(
                localModule = state.localModule.copy(cards = updatedCards),
                isImportActive = false
            )
        }
    }
}
