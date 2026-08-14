package com.example.flushcards.ui.screens.learningscreens

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flushcards.data.constants.ModuleConfig
import com.example.flushcards.data.model.FlashCard
import com.example.flushcards.data.model.Module
import com.example.flushcards.ui.components.AppTopBar
import com.example.flushcards.ui.components.LearningCardsProgress
import com.example.flushcards.ui.components.WordCard
import com.example.flushcards.ui.screens.LearningResultScreen
import com.example.flushcards.ui.theme.FlushCardsTheme
import com.example.flushcards.util.optionButtonBorderColor
import com.example.flushcards.util.optionButtonColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun WriteScreen(module: Module, onExit: () -> Unit) {
    if (module.cards.isEmpty()) return

    var sessionTrigger by remember { mutableIntStateOf(0) }

    val isFinished = remember(sessionTrigger) { mutableStateOf(false) }
    var rightAnswers by remember(sessionTrigger) { mutableIntStateOf(0) }
    var wrongAnswers by remember(sessionTrigger) { mutableIntStateOf(0) }
    var currentIndex by remember(sessionTrigger) { mutableIntStateOf(0) }

    val cardsToLearn = remember(module, sessionTrigger) {
        module.getCardsToLearn()
    }

    if (isFinished.value) {
        module.finishLearning(cardsToLearn, wrongAnswers)

        LearningResultScreen(
            correctCount = rightAnswers,
            wrongCount = wrongAnswers,
            onRetry = { sessionTrigger++ },
            onExit = onExit
        )
        return
    }

    val currentCard = cardsToLearn.getOrNull(currentIndex) ?: return

    var guessMeaning by remember(currentIndex) { mutableStateOf("") }
    var answerState: Boolean? by remember(currentIndex) { mutableStateOf(null) }
    var showAnswer by remember(currentIndex) { mutableStateOf(false) }
    var isProcessing by remember(currentIndex) { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    BackHandler { onExit() }

    fun checkAnswer() {
        if (isProcessing || answerState != null) return

        isProcessing = true
        scope.launch {
            val isCorrect = currentCard.getBack(module.isTermFirst)
                .equals(guessMeaning.trim(), ignoreCase = true)

            if (isCorrect) {
                answerState = true
                currentCard.rightAnswer()
                rightAnswers++
                delay(ModuleConfig.HIGHLIGHT_DURATION)

                if (currentIndex + 1 >= cardsToLearn.size) {
                    isFinished.value = true
                } else {
                    currentIndex++
                }
            } else {
                answerState = false
                if (currentCard.isFirstTry) wrongAnswers++
                currentCard.wrongAnswer()
                delay(ModuleConfig.HIGHLIGHT_DURATION)
                answerState = null
                isProcessing = false
            }
        }
    }

    fun revealAnswer() {
        if (isProcessing) return

        isProcessing = true
        scope.launch {
            showAnswer = true
            delay(ModuleConfig.HIGHLIGHT_DURATION * 2)

            if (currentIndex + 1 >= cardsToLearn.size) {
                isFinished.value = true
            } else {
                currentIndex++
            }
        }
    }

    WriteScreenContent(
        moduleName = module.name,
        currentCard = currentCard,
        isTermFirst = module.isTermFirst,
        cardsToLearnCount = cardsToLearn.size,
        currentIndex = currentIndex,
        guessMeaning = guessMeaning,
        answerState = answerState,
        showAnswer = showAnswer,
        isProcessing = isProcessing,
        onGuessChange = { if (!isProcessing) guessMeaning = it },
        checkAnswer = { checkAnswer() },
        revealAnswer = { revealAnswer() },
        onExit = onExit
    )
}

@Composable
fun WriteScreenContent(
    moduleName: String,
    currentCard: FlashCard,
    isTermFirst: Boolean,
    cardsToLearnCount: Int,
    currentIndex: Int,
    guessMeaning: String,
    answerState: Boolean?,
    showAnswer: Boolean,
    isProcessing: Boolean,
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
            title = moduleName,
            onBack = onExit,
            rightPartContent = {
                LearningCardsProgress(
                    currentCardIndex = currentIndex + 1,
                    total = cardsToLearnCount
                )
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (!showAnswer) {
            WordCard(word = currentCard.getFront(isTermFirst))
        } else {
            WordCard(
                word = currentCard.getBack(isTermFirst),
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
            value = guessMeaning,
            answerState = answerState,
            onValueChange = onGuessChange,
            onDone = { checkAnswer() }
        )

        Spacer(modifier = Modifier.height(16.dp))


        WriteActionButtons(
            isFirstTry = currentCard.isFirstTry,
            isProcessing = isProcessing,
            onCheckAnswer = { checkAnswer() },
            onRevealAnswer = { revealAnswer() }
        )
    }
}

@Composable
private fun InputGuessField(
    value: String,
    answerState: Boolean?,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit
) {
    val textFieldBorderColor = optionButtonBorderColor(isCorrect = answerState)
    val textFieldContainerColor = optionButtonColor(isCorrect = answerState).containerColor

    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        placeholder = {
            Text(
                text = "Значение...",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = textFieldContainerColor,
            unfocusedContainerColor = textFieldContainerColor,
            focusedBorderColor = textFieldBorderColor,
            unfocusedBorderColor = textFieldBorderColor
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onDone() })
    )
}

@Composable
private fun WriteActionButtons(
    isFirstTry: Boolean,
    isProcessing: Boolean,
    onCheckAnswer: () -> Unit,
    onRevealAnswer: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Button(
            onClick = onCheckAnswer,
            enabled = !isProcessing,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = "Проверить",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        AnimatedVisibility(
            visible = !isFirstTry,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onRevealAnswer,
                    enabled = !isProcessing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(
                        text = "Показать значение",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
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