package com.example.flushcards.ui.screens.learningscreens.write.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flushcards.ui.components.AppTopBar
import com.example.flushcards.ui.components.LearningCardsProgress
import com.example.flushcards.ui.components.WordCard
import com.example.flushcards.ui.screens.learningscreens.write.WriteUiState
import com.example.flushcards.util.optionButtonBorderColor
import com.example.flushcards.util.optionButtonColor

@Composable
fun WriteScreenContent(
    uiState: WriteUiState,
    onGuessChange: (String) -> Unit,
    checkAnswer: () -> Unit,
    revealAnswer: () -> Unit,
    onExit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(
                WindowInsets.systemBars.only(WindowInsetsSides.Vertical).union(WindowInsets.ime)
            )
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
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

        Spacer(modifier = Modifier.height(20.dp))

        if (!uiState.showAnswer) {
            WordCard(word = uiState.currentCard!!.getFront(uiState.isTermFirst))
        } else {
            WordCard(
                word = uiState.currentCard!!.getBack(uiState.isTermFirst),
                containerColor = optionButtonColor(isCorrect = true).containerColor,
                borderColor = optionButtonBorderColor(isCorrect = true)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Введите перевод термина",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            textAlign = TextAlign.Start
        )

        InputGuessField(
            value = uiState.guessMeaning,
            answerState = uiState.answerState,
            onValueChange = onGuessChange,
            onDone = { checkAnswer() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        WriteActionButtons(
            isFirstTry = uiState.currentCard.isFirstTry,
            isProcessing = uiState.isProcessing,
            onCheckAnswer = { checkAnswer() },
            onRevealAnswer = { revealAnswer() }
        )
    }
}


