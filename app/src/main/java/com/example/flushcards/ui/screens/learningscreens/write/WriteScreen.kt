package com.example.flushcards.ui.screens.learningscreens.write

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flushcards.data.model.FlashCard
import com.example.flushcards.data.model.Module
import com.example.flushcards.ui.screens.learningResult.LearningResultScreen
import com.example.flushcards.ui.screens.learningscreens.write.components.WriteScreenContent
import com.example.flushcards.ui.theme.FlushCardsTheme

@Composable
fun WriteScreen(
    module: Module,
    onExit: () -> Unit,
    viewModel: WriteViewModel = viewModel(
        key = module.id.toString(),
        factory = viewModelFactory {
            initializer { WriteViewModel(module = module)}
        }
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isFinished) {
        LearningResultScreen(
            correctCount = uiState.correctAnswers,
            wrongCount = uiState.wrongAnswers,
            onRetry = { viewModel.restartGame() },
            onExit = onExit
        )
        return
    }

    BackHandler { onExit() }

    WriteScreenContent(
        uiState = uiState,
        onGuessChange = { viewModel.onGuessChange(it) },
        checkAnswer = { viewModel.checkAnswer() },
        revealAnswer = { viewModel.revealAnswer() },
        onExit = onExit
    )
}




@Preview(showBackground = true)
@Composable
fun WriteScreenPreview() {
    FlushCardsTheme {
        WriteScreen(
            Module(1, "testModule", mutableListOf(FlashCard(1, "Brave", "Смелый"))),
            onExit = {}
        )
    }
}