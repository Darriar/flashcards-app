package com.example.flushcards.ui.screens.learningscreens.match.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flushcards.data.model.FlashCard
import com.example.flushcards.data.preview.SampleData
import com.example.flushcards.ui.theme.FlushCardsTheme
import com.example.flushcards.util.tapCardBorderColor
import com.example.flushcards.util.tapCardColor
import com.example.flushcards.util.tapCardTextColor

@Composable
fun MatchGrid(
    currentWords: List<FlashCard?>,
    currentMeanings: List<FlashCard?>,
    selectedWord: FlashCard?,
    selectedMeaning: FlashCard?,
    checkedWordCard: FlashCard?,
    checkedMeaningCard: FlashCard?,
    isPairCorrect: Boolean?,
    isTermFirst: Boolean,
    onWordClick: (FlashCard) -> Unit,
    onMeaningClick: (FlashCard) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        MatchCardsColumn(
            cards = currentWords,
            selectedCard = selectedWord,
            checkedCard = checkedWordCard,
            isPairCorrect = isPairCorrect,
            getText = { it.getFront(isTermFirst) },
            onCardClick = onWordClick,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(16.dp))

        MatchCardsColumn(
            cards = currentMeanings,
            selectedCard = selectedMeaning,
            checkedCard = checkedMeaningCard,
            isPairCorrect = isPairCorrect,
            getText = { it.getBack(isTermFirst) },
            onCardClick = onMeaningClick,
            modifier = Modifier.weight(1f)
        )
    }
}
@Composable
private fun MatchCardsColumn(
    cards: List<FlashCard?>,
    selectedCard: FlashCard?,
    checkedCard: FlashCard?,
    isPairCorrect: Boolean?,
    getText: (FlashCard) -> String?,
    onCardClick: (FlashCard) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        cards.forEach { card ->
            val isSelected = card != null && selectedCard?.id == card.id
            val isCorrect = if (card != null && checkedCard?.id == card.id) isPairCorrect else null

            MatchCard(
                text = card?.let(getText),
                isSelected = isSelected && isCorrect == null,
                isCorrect = isCorrect,
                onClick = {
                    if (card != null && isPairCorrect == null) {
                        onCardClick(card)
                    }
                }
            )
        }
    }
}

@Composable
fun MatchCard(
    text: String?,
    isSelected: Boolean,
    isCorrect: Boolean?,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .alpha(if (text == null) 0f else 1f),
        shape = RoundedCornerShape(16.dp),
        color = tapCardColor(isSelected = isSelected, isCorrect = isCorrect),
        border = tapCardBorderColor(isSelected = isSelected, isCorrect = isCorrect),
        shadowElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            if (text != null) {
                Text(
                    text = text,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = tapCardTextColor(isSelected = isSelected,isCorrect = isCorrect),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MatchCardPreview() {
    FlushCardsTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            MatchCard(
                text = "Immutable",
                isSelected = false,
                isCorrect = null,
                onClick = {}
            )

            MatchCard(
                text = "Recomposition",
                isSelected = true,
                isCorrect = null,
                onClick = {}
            )

            MatchCard(
                text = "Coroutine",
                isSelected = false,
                isCorrect = true,
                onClick = {}
            )

            MatchCard(
                text = "StateFlow",
                isSelected = false,
                isCorrect = false,
                onClick = {}
            )

            MatchCard(
                text = null,
                isSelected = false,
                isCorrect = null,
                onClick = {}
            )
        }
    }
}


@Preview(name = "MatchCardsColumn Preview", showBackground = true)
@Composable
private fun MatchCardsColumnPreview() {
    val cards = SampleData.englishVocabularyCards.take(4)

    FlushCardsTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            MatchCardsColumn(
                cards = listOf(
                    cards[0],
                    cards[1],
                    cards[2],
                    null
                ),
                selectedCard = cards[1],
                checkedCard = cards[2],
                isPairCorrect = false,
                getText = { it.word },
                onCardClick = {},
                modifier = Modifier
            )
        }
    }
}


@Preview(name = "MatchGrid Preview", showBackground = true, widthDp = 380)
@Composable
private fun MatchGridPreview() {
    val words = SampleData.englishVocabularyCards.take(4)
    val meanings = listOf(
        words[1],
        words[0],
        null,
        words[3]
    )

    FlushCardsTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            MatchGrid(
                currentWords = listOf(words[0], words[1], null, words[3]),
                currentMeanings = meanings,
                selectedWord = words[0],       // Выбран термин Immutable
                selectedMeaning = null,
                checkedWordCard = null,
                checkedMeaningCard = null,
                isPairCorrect = null,
                isTermFirst = true,
                onWordClick = {},
                onMeaningClick = {}
            )
        }
    }
}