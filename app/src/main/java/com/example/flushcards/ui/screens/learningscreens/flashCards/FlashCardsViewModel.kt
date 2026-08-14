package com.example.flushcards.ui.screens.learningscreens.flashCards

import androidx.lifecycle.ViewModel
import com.example.flushcards.data.model.FlashCard
import com.example.flushcards.data.model.Module
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale

class FlashCardsViewModel(private val module: Module): ViewModel() {
    private val _uiState = MutableStateFlow(FlashCardsUiState())

    val uiState: StateFlow<FlashCardsUiState> = _uiState.asStateFlow()

    private val cardsToLearn = mutableListOf<FlashCard>()

    init {
        setupGame()
    }

    private fun setupGame() {
        cardsToLearn.clear()
        cardsToLearn.addAll(module.getCardsToLearn())

        _uiState.update {
            it.copy(
                moduleName = module.name,
                currentCard = cardsToLearn[it.currentIndex],
                totalCardsCount = cardsToLearn.size
            )
        }
    }

    fun onFlip() {
        _uiState.update { it.copy(isFlipped = !it.isFlipped) }
    }

    fun onKnowAnswer() {
        _uiState.value.currentCard?.rightAnswer()
        processAnswer(isCorrect = true)
    }

    fun onDontKnowAnswer() {
        _uiState.value.currentCard?.wrongAnswer()
        processAnswer(isCorrect = false)
    }

    private fun processAnswer(isCorrect: Boolean) {
        val nextIndex = _uiState.value.currentIndex + 1
        val isFinished = nextIndex >= cardsToLearn.size

        _uiState.update { state ->
            state.copy(
                correctCount = if (isCorrect) state.correctCount + 1 else state.correctCount,
                wrongCount = if (!isCorrect) state.wrongCount + 1 else state.wrongCount,
                currentIndex = nextIndex,
                currentCard = cardsToLearn.getOrNull(nextIndex),
                isFlipped = false,
                isFinished = isFinished
            )
        }
    }

    fun onPronounce(): Pair<String, Locale>? {
        val card = _uiState.value.currentCard ?: return null
        val isFlipped = _uiState.value.isFlipped
        val isTermFirst = module.isTermFirst

        val locale = if ((!isFlipped && isTermFirst) || (isFlipped && !isTermFirst)) {
            Locale.UK
        } else {
            Locale.forLanguageTag("ru-RU")
        }
        val text = if (!isFlipped) card.getFront(isTermFirst) else card.getBack(isTermFirst)

        return text to locale
    }


    fun restartGame() {
        _uiState.update { FlashCardsUiState() }
        setupGame()
    }
}