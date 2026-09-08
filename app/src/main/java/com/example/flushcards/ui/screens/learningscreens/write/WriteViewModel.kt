package com.example.flushcards.ui.screens.learningscreens.write

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
import kotlin.time.Duration.Companion.milliseconds

class WriteViewModel(private val module: Module): ViewModel() {
    private val _uiState = MutableStateFlow(WriteUiState())
    val uiState: StateFlow<WriteUiState> = _uiState.asStateFlow()

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
                isTermFirst = module.isTermFirst,
                totalCardsCount = cardsToLearn.size,
                currentCard = cardsToLearn[it.currentIndex],
            )
        }
    }

    fun onGuessChange(newGuess: String) {
        if (!_uiState.value.isProcessing) {
            _uiState.update {
                it.copy(
                    guessMeaning = newGuess
                )
            }
        }
    }

    fun checkAnswer() {
        val currentCard = _uiState.value.currentCard ?: return
        if (_uiState.value.isProcessing || _uiState.value.answerState != null) return

        viewModelScope.launch {
            val isCorrect = _uiState.value.currentCard!!.getBack(module.isTermFirst)
                .equals(_uiState.value.guessMeaning.trim(), ignoreCase = true)

            _uiState.update { it.copy(isProcessing = true, answerState = isCorrect) }

            if (isCorrect) {
                currentCard.rightAnswer()

                val isFinished = _uiState.value.currentIndex >= _uiState.value.totalCardsCount - 1

                delay(ModuleConfig.HIGHLIGHT_DURATION.milliseconds)

                if (isFinished) {
                    module.finishLearning(cardsToLearn, _uiState.value.wrongAnswers)
                }

                val nextIndex = _uiState.value.currentIndex + 1
                _uiState.update {
                    it.copy(
                        answerState = null,
                        correctAnswers = it.correctAnswers + 1,
                        currentIndex = if (!isFinished) nextIndex else it.currentIndex,
                        currentCard = if (!isFinished) cardsToLearn[nextIndex] else it.currentCard,
                        guessMeaning = "",
                        isProcessing = false,
                        isFinished = isFinished
                    )
                }

            } else {
                currentCard.wrongAnswer()

                _uiState.update {
                    it.copy(
                        wrongAnswers = if (currentCard.isFirstTry) it.wrongAnswers + 1 else it.wrongAnswers
                    )
                }

                delay(ModuleConfig.HIGHLIGHT_DURATION.milliseconds)

                _uiState.update {
                    it.copy(
                        guessMeaning = "",
                        answerState = null,
                        isProcessing = false
                    )
                }
            }
        }
    }

    fun revealAnswer() {
        if (_uiState.value.isProcessing) return

        viewModelScope.launch {
            val isFinished = _uiState.value.currentIndex >= _uiState.value.totalCardsCount - 1

            _uiState.update {
                it.copy(
                    isProcessing = true,
                    showAnswer = true
                )
            }

            delay((ModuleConfig.HIGHLIGHT_DURATION * 2).milliseconds)

            if (isFinished) {
                module.finishLearning(cardsToLearn, _uiState.value.wrongAnswers)
            }

            val nextIndex = _uiState.value.currentIndex + 1
            _uiState.update {
                it.copy(
                    currentIndex = if (!isFinished) nextIndex else it.currentIndex,
                    currentCard = if (!isFinished) cardsToLearn[nextIndex] else it.currentCard,
                    isFinished = isFinished,
                    isProcessing = false,
                    showAnswer = false
                )
            }
        }
    }

    fun restartGame() {
        _uiState.update { WriteUiState() }
        setupGame()
    }
}