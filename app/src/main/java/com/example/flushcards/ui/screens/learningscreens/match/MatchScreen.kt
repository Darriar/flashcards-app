package com.example.flushcards.ui.screens.learningscreens.match

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import com.example.flushcards.ui.screens.LearningResultScreen
import com.example.flushcards.ui.screens.learningscreens.match.components.MatchGrid
import com.example.flushcards.ui.screens.learningscreens.match.components.MatchHeader
import com.example.flushcards.ui.theme.FlushCardsTheme

@Composable
fun MatchScreen(
    module: Module,
    onExit: () -> Unit,
    viewModel: MatchViewModel = viewModel(
        key = module.id.toString(),
        factory = viewModelFactory {
            initializer { MatchViewModel(module = module) }
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
        MatchHeader(
            viewModel = viewModel,
            onBack = onExit,
        )

        MatchGrid(
            currentWords = uiState.currentWords,
            currentMeanings = uiState.currentMeanings,
            selectedWord = uiState.selectedWord,
            selectedMeaning = uiState.selectedMeaning,
            checkedWordCard = uiState.checkedWord,
            checkedMeaningCard = uiState.checkedMeaning,
            isPairCorrect = uiState.isPairCorrect,
            isTermFirst = uiState.isTermFirst,
            onWordClick = { viewModel.onWordClick(it) },
            onMeaningClick = { viewModel.onMeaningClick(it) },
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun MatchScreenPreview() {

    FlushCardsTheme {
        MatchScreen(module = SampleData.vocabularyModule, onExit = {})
    }
}