package com.example.flushcards.ui.screens.learningscreens.match

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flushcards.data.constants.ModuleConfig
import com.example.flushcards.data.model.FlashCard
import com.example.flushcards.data.model.Module
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MatchViewModel(private val module: Module) : ViewModel() {
    private val _uiState = MutableStateFlow(MatchUiState())
    val uiState: StateFlow<MatchUiState> = _uiState.asStateFlow()

    private val cardsToLearn = mutableListOf<FlashCard>()
    private val learnedCards = mutableListOf<FlashCard>()

    init {
        setupGame()
    }

    private fun setupGame() {
        cardsToLearn.clear()
        cardsToLearn.addAll(module.getCardsToLearn())

        val initialCards = cardsToLearn.take(ModuleConfig.MIN_CARDS_COUNT)

        _uiState.update {
            it.copy(
                moduleName = module.name,
                currentWords = initialCards.shuffled(),
                currentMeanings = initialCards.shuffled(),
                totalCardsCount = cardsToLearn.size,
                isTermFirst = module.isTermFirst
            )
        }
    }

    fun onWordClick(wordCard: FlashCard) {
        if (_uiState.value.isProcessing) return

        if (_uiState.value.selectedWord == wordCard) {
            _uiState.update { it.copy(selectedWord = null) }
        } else {
            _uiState.update { it.copy(selectedWord = wordCard) }
            checkPairIfBothSelected()
        }
    }

    fun onMeaningClick(meaningCard: FlashCard) {
        if (_uiState.value.isProcessing) return

        if (_uiState.value.selectedMeaning == meaningCard) {
            _uiState.update { it.copy(selectedMeaning = null) }
        } else {
            _uiState.update { it.copy(selectedMeaning = meaningCard) }
            checkPairIfBothSelected()
        }
    }

    private fun checkPairIfBothSelected() {
        val word = _uiState.value.selectedWord ?: return
        val meaning = _uiState.value.selectedMeaning ?: return

        _uiState.update { it.copy(isProcessing = true) }

        viewModelScope.launch {
            val isCorrect = word.getBack(module.isTermFirst) == meaning.getBack(module.isTermFirst)
            val isFirstTry = word.isFirstTry

            if (isCorrect) {
                word.rightAnswer()
                learnedCards.add(word)
                cardsToLearn.remove(word)
            } else {
                word.wrongAnswer()
            }

            // подсветка
            _uiState.update {
                it.copy(
                    selectedWord = null,
                    selectedMeaning = null,
                    checkedWord = word,
                    checkedMeaning = meaning,
                    isPairCorrect = isCorrect,
                    correctCount = if (isCorrect && isFirstTry) it.correctCount + 1 else it.correctCount,
                    wrongCount = if (!isCorrect && isFirstTry) it.wrongCount + 1 else it.wrongCount
                )
            }

            delay(ModuleConfig.HIGHLIGHT_DURATION)

            if (isCorrect) {
                removeMatchedAndReplenish(word, meaning)
            }

            val isGameFinished = cardsToLearn.isEmpty() && _uiState.value.currentWords.all { it == null }

            if (isGameFinished) {
                module.finishLearning(learnedCards, _uiState.value.wrongCount)
            }

            // сброс выделения, разблокировка кликов
            _uiState.update {
                it.copy(
                    checkedWord = null,
                    checkedMeaning = null,
                    isPairCorrect = null,
                    learnedCardsCount = learnedCards.size,
                    isFinished = isGameFinished,
                    isProcessing = false
                )
            }
        }
    }

    private fun removeMatchedAndReplenish(word: FlashCard, meaning: FlashCard) {
        val newWords = _uiState.value.currentWords.toMutableList()
        val newMeanings = _uiState.value.currentMeanings.toMutableList()

        val wordIndex = newWords.indexOf(word)
        val meaningIndex = newMeanings.indexOf(meaning)

        if (wordIndex != -1) newWords[wordIndex] = null
        if (meaningIndex != -1) newMeanings[meaningIndex] = null

        val remainingCards = cardsToLearn.filter { !newWords.contains(it) }
        val nextWordCard = remainingCards.shuffled().firstOrNull()

        if (nextWordCard != null) {
            if (wordIndex != -1) newWords[wordIndex] = nextWordCard

            var hasMatch = false
            for (cardWord in newWords) {
                for (cardMeaning in newMeanings) {
                    if (cardWord?.meaning == cardMeaning?.meaning) {
                        hasMatch = true
                        break
                    }
                }
            }

            val nextMeaningCard: FlashCard
            if (hasMatch) {
                val shuffledCards = cardsToLearn.filter { !newMeanings.contains(it) }.shuffled()
                nextMeaningCard = shuffledCards.first()
            } else {
                nextMeaningCard = nextWordCard
            }
            if (meaningIndex != -1) newMeanings[meaningIndex] = nextMeaningCard
        }

        _uiState.update {
            it.copy(
                currentWords = newWords,
                currentMeanings = newMeanings
            )
        }
    }

    fun restartGame() {
        _uiState.update { MatchUiState() }
        setupGame()
    }
}