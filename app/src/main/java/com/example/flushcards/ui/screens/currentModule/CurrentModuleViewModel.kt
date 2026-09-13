package com.example.flushcards.ui.screens.currentModule

import androidx.lifecycle.ViewModel
import com.example.flushcards.data.model.Module
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CurrentModuleViewModel(private val module: Module): ViewModel() {
    private val _uiState =   MutableStateFlow(CurrentModuleUiState())

    val uiState: StateFlow<CurrentModuleUiState> = _uiState.asStateFlow()

    init {
        _uiState.update {
            it.copy(
                module = module,
                cards = module.cards,
                moduleName = module.name,
                totalCardsCount = module.cards.size,
                isTermFirst = module.isTermFirst
            )
        }
    }

    fun onSearchClick() {
        _uiState.update {
            it.copy( isSearchActive = true )
        }
    }

    fun onDoNotSearch() {
        _uiState.update {
            it.copy( isSearchActive = false )
        }
    }
    fun onSelectCard(cardIndex: Int) {
        _uiState.update {
            it.copy( highlightedCardIndex = cardIndex )
        }
    }

    fun onDoNotSelectCard() {
        _uiState.update {
            it.copy( highlightedCardIndex = -1 )
        }
    }

    fun resetProgress() {
        module.resetProgress()
        _uiState.update { it.copy(cards = module.cards.toList()) }
    }

    fun setCardMode(isTermFirst: Boolean) {
        if (isTermFirst) {
            module.showTermFirst()
        } else {
            module.showMeaningFirst()
        }
        _uiState.update { it.copy(isTermFirst = isTermFirst) }
    }

}