package com.example.flushcards.ui.screens.learningscreens.flashCards

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flushcards.data.model.Module
import com.example.flushcards.data.preview.SampleData
import com.example.flushcards.ui.components.AppTopBar
import com.example.flushcards.ui.components.LearningCardsProgress
import com.example.flushcards.ui.screens.LearningResultScreen
import com.example.flushcards.ui.screens.learningscreens.flashCards.components.FlashCardView
import com.example.flushcards.ui.screens.learningscreens.flashCards.components.PronounceButton
import com.example.flushcards.ui.theme.FlushCardsTheme


@Composable
fun FlashCardsScreen(
    module: Module,
    onExit: () -> Unit,
    viewModel: FlashCardsViewModel = viewModel(
        key = module.id.toString(),
        factory = viewModelFactory {
            initializer { FlashCardsViewModel (module = module) }
        }
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isFinished) {
        LearningResultScreen(
            correctCount = uiState.correctCount,
            wrongCount = uiState.wrongCount,
            onRetry = { viewModel.restartGame() },
            onExit = onExit
        )
        return
    }

    BackHandler { onExit() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppTopBar(
            title = uiState.moduleName,
            onBack = onExit,
            rightPartContent = {
                LearningCardsProgress(
                    currentCardIndex = uiState.currentIndex + 1,
                    total = uiState.totalCardsCount
                )
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        FlashCardView(
            card = uiState.currentCard!!,
            isFlipped = uiState.isFlipped,
            isTermFirst = module.isTermFirst,
            onFlip = { viewModel.onFlip() },
            onSwipeLeft = { viewModel.onDontKnowAnswer() },
            onSwipeRight = { viewModel.onKnowAnswer() }
        )

        Box(
            contentAlignment = Alignment.BottomStart,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.Start)
                .padding(bottom = 32.dp)
        ) {
            PronounceButton(onClick = {viewModel.onPronounce() } )
        }
    }

}


@Preview(showBackground = true)
@Composable
fun FlashCardsScreenPreview() {
    FlushCardsTheme {
        FlashCardsScreen(
            module = SampleData.vocabularyModule,
            onExit = {}
        )
    }
}