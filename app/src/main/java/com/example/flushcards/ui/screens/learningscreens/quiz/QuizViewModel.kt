package com.example.flushcards.ui.screens.learningscreens.quiz

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
import kotlin.collections.shuffled
import kotlin.time.Duration.Companion.milliseconds

class QuizViewModel(private val module: Module): ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private val cardsToLearn = mutableListOf<FlashCard>()

    init {
        setupGame()
    }

    private fun setupGame(){
        cardsToLearn.clear()
        cardsToLearn.addAll(module.getCardsToLearn())

        val firstCard = cardsToLearn.first()

        _uiState.update {
            it.copy(
                moduleName = module.name,
                totalCardsCount = cardsToLearn.size,
                isTermFirst = module.isTermFirst,
                currentIndex = 0,
                currentCard = firstCard,
                options = generateOptions(firstCard)
            )
        }

    }

    private fun generateOptions(currentCard: FlashCard?): List<String> {
        if (currentCard == null) return emptyList()

        val isTermFirst = module.isTermFirst
        val targetBack = currentCard.getBack(isTermFirst)

        return( module.cards
            .filter { card ->  card.getBack(isTermFirst) != targetBack }
            .map {card -> card.getBack(isTermFirst) }
            .distinct()
            .shuffled()
            .take(ModuleConfig.MIN_CARDS_COUNT - 1) + targetBack)
        .shuffled()
    }

    fun onAnswerCardClick(option: String) {
        val isCorrectAnswer = option == _uiState.value.currentCard?.getBack(module.isTermFirst)
        _uiState.update {
            it.copy(
                selectedOption = option,
                correctCount =  if (isCorrectAnswer) it.correctCount + 1 else it.correctCount,
                wrongCount = if (!isCorrectAnswer) it.wrongCount + 1 else it.wrongCount
            )
        }
            viewModelScope.launch {
                if (isCorrectAnswer) {
                    _uiState.value.currentCard?.rightAnswer()
                } else {
                    _uiState.value.currentCard?.wrongAnswer()
                }

                delay(ModuleConfig.HIGHLIGHT_DURATION.milliseconds)

                val isFinished = _uiState.value.currentIndex >= _uiState.value.totalCardsCount - 1

                if (isFinished) {
                    module.finishLearning(
                        cardsToLearn = cardsToLearn,
                        wrongAnswers = _uiState.value.wrongCount
                    )
                    _uiState.update {
                        it.copy(
                            selectedOption = null,
                            isFinished = true
                        )
                    }
                } else {
                    val nextIndex = _uiState.value.currentIndex + 1
                    val nextCard = cardsToLearn[nextIndex]
                    _uiState.update {
                        it.copy(
                            selectedOption = null,
                            currentIndex = nextIndex,
                            currentCard = nextCard,
                            options = generateOptions(nextCard)
                        )
                    }
                }
            }

    }

    fun restartGame() {
        _uiState.update { QuizUiState() }
        setupGame()
    }
}