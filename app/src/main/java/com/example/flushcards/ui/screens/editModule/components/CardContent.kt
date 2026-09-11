package com.example.flushcards.ui.screens.editModule.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flushcards.R
import com.example.flushcards.data.model.FlashCard
import com.example.flushcards.data.preview.SampleData
import com.example.flushcards.data.services.TranslationService
import com.example.flushcards.ui.theme.FlushCardsTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun CardContent(
    card: FlashCard,
    onWordChange: (String) -> Unit,
    onMeaningChange: (String) -> Unit,
) {
    var suggestedTranslation by remember { mutableStateOf("") }
    var isTextFieldFocused by remember { mutableStateOf(false) }

    LaunchedEffect(card.word) {
        val word = card.word
        if (word.isNotBlank()) {
            delay(500.milliseconds)
            if (word.trim() == card.word.trim()) {
                suggestedTranslation = TranslationService.translate(word)
            }
        } else {
            suggestedTranslation = ""
        }
    }

    Column(modifier = Modifier.padding(12.dp)) {
        InputTextField(
            label = "Термин",
            value = card.word,
            onValueChange = { newWord -> onWordChange(newWord) }
        )

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            color = MaterialTheme.colorScheme.outlineVariant,
            thickness = 1.dp
        )

        TranslateWordRow(
            isVisible = suggestedTranslation.isNotBlank() && isTextFieldFocused,
            suggestedTranslation = suggestedTranslation,
            onTranslateClick = {
                onMeaningChange(suggestedTranslation)
                suggestedTranslation = ""
            }
        )

        InputTextField(
            label = "Значение",
            value = card.meaning,
            onValueChange = { newMeaning -> onMeaningChange(newMeaning) },
            modifier = Modifier.onFocusChanged { focusState ->
                isTextFieldFocused = focusState.isFocused
            },
        )
    }
}

@Composable
private fun InputTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Medium) },
        textStyle = TextStyle(fontSize = 16.sp),
        modifier = modifier
            .fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
private fun TranslateWordRow(
    isVisible: Boolean,
    suggestedTranslation: String,
    onTranslateClick: () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable { onTranslateClick() }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back), // поставить иконку перевода/магии
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Перевод: $suggestedTranslation",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CardContentPreview() {
    FlushCardsTheme {
        CardContent(
            SampleData.vocabularyModule.cards.first(),
            onWordChange = {},
            onMeaningChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InputTextFieldPreview() {
    FlushCardsTheme {
        InputTextField(
            label = "Термин",
            value = "Солнце",
            onValueChange = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 350, heightDp = 50)
@Composable
fun TranslateWordRowPreview() {
    FlushCardsTheme {
        TranslateWordRow(
            isVisible = true,
            suggestedTranslation = "apple",
            onTranslateClick = {}
        )
    }
}