package com.example.flushcards.screens.learningScreens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flushcards.model.FlashCard
import com.example.flushcards.model.Module
import com.example.flushcards.ui.theme.FlushCardsTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun WriteScreen(module: Module, onExit: () -> Unit) {

    if (module.cards.isEmpty()) return

    var sessionTrigger by remember { mutableIntStateOf(0) }
    var isFinished by remember { mutableStateOf(false) }
    var rightAnswers by remember { mutableIntStateOf(0) }
    var wrongAnswers by remember { mutableIntStateOf(0) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var guessMeaning by remember(currentIndex) { mutableStateOf("") }

    val cardsToLearn = remember(module, sessionTrigger) {
        module.getCardsToLearn()
    }

    if (isFinished) {
        module.finishLearning(cardsToLearn, wrongAnswers)

        LearningResultScreen(
            rightAnswers,
            wrongAnswers,
            onRetry = {
                sessionTrigger++
                isFinished = false
                currentIndex = 0
                rightAnswers = 0
                wrongAnswers = 0
                guessMeaning = ""
            },
            onExit = onExit
        )
        return
    }

    val currentCard = remember(currentIndex) { cardsToLearn[currentIndex] }
    var answerState: Boolean? by remember(currentIndex) { mutableStateOf(null) }
    var showAnswer by remember(currentIndex) { mutableStateOf(false) }
    var isProcessing by remember(currentIndex) { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    BackHandler { onExit() }

    val checkAnswer = {
        if (!isProcessing && answerState == null) {

            scope.launch {
                if (currentCard.meaning.equals(guessMeaning.trim(), ignoreCase = true)) {
                    answerState = true
                    currentCard.rightAnswer()
                    rightAnswers++
                    delay(700)
                    currentIndex++
                } else {
                    answerState = false
                    if (currentCard.isFirstTry) wrongAnswers++
                    currentCard.wrongAnswer()
                    delay(700)
                    answerState = null
                }

                if (currentIndex == cardsToLearn.size) isFinished = true
            }
        }
    }

    val revealAnswer = {
        if (!isProcessing) {
            isProcessing = true
            scope.launch {
                showAnswer = true
                delay(1500)
                currentIndex++

                if (currentIndex == cardsToLearn.size) isFinished = true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Vertical).union(WindowInsets.ime))
            .padding(horizontal = 16.dp)
            .padding(top = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MatchHeader(
            title = module.name,
            current = currentIndex + 1,
            total = cardsToLearn.size,
            onBack = onExit
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (!showAnswer) {
            WordCard(word = currentCard.word)
        } else {
            WordCard(
                word = currentCard.meaning,
                containerColor = getContainerColor(showAnswer),
                borderColor = getBorderColor(showAnswer))
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

        OutlinedTextField(
            value = guessMeaning,
            onValueChange = { if (!isProcessing) guessMeaning = it },
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
                focusedContainerColor = getContainerColor(answerState),
                unfocusedContainerColor = getContainerColor(answerState),
                focusedBorderColor = getBorderColor(answerState),
                unfocusedBorderColor = getBorderColor(answerState)
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { checkAnswer() })
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Button(
                onClick = { checkAnswer() },
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
                visible = !currentCard.isFirstTry,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = { revealAnswer() } ,
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
}

@Composable
fun getBorderColor(state: Boolean?): Color {
    val borderColor by animateColorAsState(
        targetValue = when (state) {
            true -> Color(0xFF81C784)
            false -> MaterialTheme.colorScheme.error
            null -> MaterialTheme.colorScheme.outlineVariant
        },
        label = "BorderColorAnimation"
    )
    return borderColor
}

@Composable
fun getContainerColor(state: Boolean?): Color {
    val containerColor by animateColorAsState(
        targetValue = when (state) {
            true -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)
            false -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
            null -> MaterialTheme.colorScheme.surface
        },
        label = "ContainerColorAnimation"
    )
    return containerColor
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