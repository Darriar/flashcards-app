package com.example.flushcards.ui.screens.learningscreens.quiz.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flushcards.data.model.FlashCard
import com.example.flushcards.data.preview.SampleData
import com.example.flushcards.ui.theme.FlushCardsTheme
import com.example.flushcards.util.optionButtonBorderColor
import com.example.flushcards.util.optionButtonColor

@Composable
fun AnswerCard(
    option: String,
    selectedOption: String?,
    currentCard: FlashCard,
    isTermFirst: Boolean,
    onClick: () -> Unit
) {
    val rightAnswer = currentCard.getBack(isTermFirst)
    val isCorrect = when (selectedOption) {
        null -> null
        option -> option == rightAnswer
        else -> if (option == rightAnswer) true else null
    }

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
            width = 2.dp,
            color = optionButtonBorderColor(isCorrect)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
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


@Preview(showBackground = true, name = "Answer Card States")
@Composable
private fun AnswerCardPreview() {
    val sampleCard = SampleData.vocabularyModule.cards.first()

    val correctAnswer = sampleCard.getBack(isTermFirst = true)
    val wrongAnswer = "Wrong Answer"

    FlushCardsTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AnswerCard(
                option = correctAnswer,
                selectedOption = null,
                currentCard = sampleCard,
                isTermFirst = true,
                onClick = {}
            )

            AnswerCard(
                option = correctAnswer,
                selectedOption = correctAnswer,
                currentCard = sampleCard,
                isTermFirst = true,
                onClick = {}
            )

            AnswerCard(
                option = wrongAnswer,
                selectedOption = wrongAnswer,
                currentCard = sampleCard,
                isTermFirst = true,
                onClick = {}
            )
        }
    }
}