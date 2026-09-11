package com.example.flushcards.ui.screens.learningscreens.quiz

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flushcards.R
import com.example.flushcards.data.model.Module
import com.example.flushcards.data.preview.SampleData
import com.example.flushcards.ui.components.AppTopBar
import com.example.flushcards.ui.components.LearningCardsProgress
import com.example.flushcards.ui.components.WordCard
import com.example.flushcards.ui.screens.learningResult.LearningResultScreen
import com.example.flushcards.ui.screens.learningscreens.quiz.components.AnswerCard
import com.example.flushcards.ui.theme.FlushCardsTheme

@Composable
fun QuizScreen(
    module: Module,
    onExit: () -> Unit,
    viewModel: QuizViewModel = viewModel(
        key = module.id.toString(),
        factory = viewModelFactory {
            initializer { QuizViewModel(module = module) }
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
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AppTopBar(
            title = uiState.moduleName,
            onBack = onExit,
            rightPartContent = {
                LearningCardsProgress(
                    currentCardIndex = uiState.currentIndex + 1,
                    total = uiState.totalCardsCount)
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        WordCard(uiState.currentCard!!.getFront(uiState.isTermFirst))

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(id = R.string.quiz_label),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            textAlign = TextAlign.Start
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            uiState.options.forEach { option ->
                AnswerCard(
                    option, uiState.selectedOption, uiState.currentCard!!, module.isTermFirst,
                    onClick = { viewModel.onAnswerCardClick(option) }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

    }
}



@Preview(showBackground = true)
@Composable
fun QuizScreenPreview() {
    FlushCardsTheme {
        QuizScreen(
            module = SampleData.vocabularyModule, onExit = {})
    }
}