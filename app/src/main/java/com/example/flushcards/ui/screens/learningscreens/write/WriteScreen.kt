package com.example.flushcards.ui.screens.learningscreens.write

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flushcards.data.model.FlashCard
import com.example.flushcards.data.model.Module
import com.example.flushcards.ui.components.AppTopBar
import com.example.flushcards.ui.components.LearningCardsProgress
import com.example.flushcards.ui.components.WordCard
import com.example.flushcards.ui.screens.LearningResultScreen
import com.example.flushcards.ui.screens.learningscreens.write.components.WriteScreenContent
import com.example.flushcards.ui.theme.FlushCardsTheme
import com.example.flushcards.util.optionButtonBorderColor
import com.example.flushcards.util.optionButtonColor

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