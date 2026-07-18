package com.example.flushcards.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flushcards.R
import com.example.flushcards.api.TranslationService
import com.example.flushcards.model.FlashCard
import com.example.flushcards.model.Module
import com.example.flushcards.ui.theme.FlushCardsTheme
import kotlinx.coroutines.delay

@Composable
fun EditModuleScreen(module: Module, onOk: () -> Unit, onExit: () -> Unit) {
    val localCards = remember {
        mutableStateListOf<FlashCard>().apply {
            if (module.cards.isEmpty()) {
                repeat(4) { index -> add(FlashCard(index + 1, "", "")) }
            } else {
                addAll(module.cards)
            }
        }
    }
    var moduleName by remember { mutableStateOf(module.name) }

    val validCardsCount by remember {
        derivedStateOf {
            localCards.count { it.word.isNotBlank() && it.meaning.isNotBlank() }
        }
    }

    val isReadyEnabled = validCardsCount >= 4 && moduleName.isNotBlank()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            CreateModuleHeader(onExit)

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = moduleName,
                onValueChange = { moduleName = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                textStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
                label = { Text("Название модуля") },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            if (validCardsCount < 4) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Добавьте еще ${4 - validCardsCount} слов(а), чтобы сохранить модуль",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(localCards) { index, card ->
                    key(card.id) {
                        CreateCard(
                            card = card,
                            onWordChange = { newWord -> localCards[index] = card.copy(word = newWord) },
                            onMeaningChange = { newMeaning -> localCards[index] = card.copy(meaning = newMeaning) }
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }

        Button(
            onClick = {
                if (isReadyEnabled) {
                    module.cards.clear()
                    module.cards.addAll(localCards
                        .filter { it.word.isNotBlank() && it.meaning.isNotBlank() }
                        .map { it.copy(word = it.word.trim(), meaning = it.meaning.trim()) }
                        .distinctBy { it.word.lowercase() })
                    module.name = moduleName
                    onOk()
                }
            },
            enabled = isReadyEnabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.outlineVariant
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Готово",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        FloatingActionButton(
            onClick = {
                val newId = if (localCards.isEmpty()) 1 else localCards.maxOf { it.id } + 1
                localCards.add(FlashCard(newId, "", ""))
            },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 96.dp, end = 24.dp)
                .size(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Добавить карточку",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun CreateCard(card: FlashCard, onWordChange: (String) -> Unit, onMeaningChange: (String) -> Unit) {
    var suggestedTranslation by remember { mutableStateOf("") }
    var isTextFieldFocused by remember { mutableStateOf(false) }

    LaunchedEffect(card.word) {
        val word = card.word
        if (word.isNotBlank()) {
            delay(500)
            if (word.trim() == card.word.trim()) {
                suggestedTranslation = TranslationService.translate(word)
            }
        } else {
            suggestedTranslation = ""
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            TextField(
                value = card.word,
                onValueChange = onWordChange,
                label = { Text("Термин", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                textStyle = TextStyle(fontSize = 16.sp),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                color = MaterialTheme.colorScheme.outlineVariant,
                thickness = 1.dp
            )

            AnimatedVisibility(
                visible = suggestedTranslation.isNotBlank() && isTextFieldFocused,
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
                        .clickable {
                            onMeaningChange(suggestedTranslation)
                            suggestedTranslation = ""
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back), // Сюда лучше поставить иконку перевода/магии, если есть
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

            TextField(
                value = card.meaning,
                onValueChange = onMeaningChange,
                label = { Text("Значение", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                textStyle = TextStyle(fontSize = 16.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isTextFieldFocused = it.isFocused },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
fun CreateModuleHeader(onBack: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Back",
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .clickable { onBack() },
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Редактирование",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

           Spacer(modifier = Modifier)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditModulePreview() {
    FlushCardsTheme() {
        EditModuleScreen(
            module = Module("English", mutableListOf(FlashCard(1, "test", "тестовый")), true),
            onOk = {}, onExit = {}
        )
    }
}