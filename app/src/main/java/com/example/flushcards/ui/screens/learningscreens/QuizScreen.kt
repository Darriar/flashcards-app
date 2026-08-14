package com.example.flushcards.ui.screens.learningscreens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flushcards.R
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
fun QuizScreen(module: Module, onExit: () -> Unit) {

    if (module.cards.isEmpty()) return

    var sessionTrigger by remember { mutableIntStateOf(0) }
    val isFinished = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val selectedOption = remember { mutableStateOf<String?>(null) }
    val currentIndex = remember { mutableIntStateOf(0) }
    val rightOptions = remember { mutableIntStateOf(0) }
    val wrongOptions = remember { mutableIntStateOf(0) }

    val cardsToLearn = remember(module, sessionTrigger) { module.getCardsToLearn() }

    if (isFinished.value) {
        module.finishLearning(cardsToLearn, wrongOptions.intValue)

        LearningResultScreen(
            rightOptions.intValue, wrongOptions.intValue, onRetry = {
                sessionTrigger++
                isFinished.value = false
                currentIndex.intValue = 0
                rightOptions.intValue = 0
                wrongOptions.intValue = 0
                selectedOption.value = null
            }, onExit = onExit
        )
        return
    }

    val currentCard = cardsToLearn[currentIndex.intValue]

    val options = remember(currentCard) {
        (module.cards.filter { it.getBack(module.isTermFirst) != currentCard.getBack(module.isTermFirst) }
            .map { it.getBack(module.isTermFirst) }.distinct().shuffled()
            .take(3) + currentCard.getBack(module.isTermFirst)).shuffled()
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
            title = module.name,
            onBack = onExit,
            rightPartContent = {
                LearningCardsProgress(
                    currentCardIndex = currentIndex.intValue + 1,
                    total = cardsToLearn.size)
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        WordCard(currentCard.getFront(module.isTermFirst))

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
            options.forEach { option ->
                AnswerCard(
                    option, selectedOption.value, currentCard, module.isTermFirst,
                    onClick = {
                        selectedOption.value = option
                        if (option == currentCard.getBack(module.isTermFirst)) {
                            currentCard.rightAnswer()
                            rightOptions.intValue++
                        } else {
                            currentCard.wrongAnswer()
                            wrongOptions.intValue++
                        }

                        scope.launch {
                            delay(ModuleConfig.HIGHLIGHT_DURATION)
                            selectedOption.value = null
                            if (currentIndex.intValue < cardsToLearn.size - 1) {
                                currentIndex.intValue++
                            } else {
                                isFinished.value = true
                            }
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

    }
}

@Composable
fun AnswerCard(
    option: String,
    selectedOption: String?,
    currentCard: FlashCard,
    isTermFirst: Boolean,
    onClick: () -> Unit
) {
    val isCorrect = if (selectedOption != option) null else option == currentCard.getBack(isTermFirst)

    OutlinedButton(
        onClick = {
            if (selectedOption != null) return@OutlinedButton
            onClick()
        },
        colors = optionButtonColor(isCorrect),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 8.dp),
        border = BorderStroke(
            width = 2.dp, color = optionButtonBorderColor(isCorrect)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = option,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizScreenPreview() {
    FlushCardsTheme {
        QuizScreen(
            Module(1, "testModule", mutableListOf(FlashCard(1, "test", "тестовый"))), onExit = {})
    }
}