package com.example.flushcards.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.flushcards.data.model.FlashCard
import com.example.flushcards.ui.theme.FlushCardsTheme

@Composable
fun SearchCard(
    cards: MutableList<FlashCard>,
    onDismiss: () -> Unit,
    onSelectCard: (index: Int) -> Unit
) {
    Popup(
        alignment = Alignment.TopCenter,
        onDismissRequest = onDismiss,
        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        SearchCardContent(
            cards = cards,
            onDismiss = onDismiss,
            onSelectCard = onSelectCard
        )
    }
}

@Composable
fun SearchCardContent(
    cards: List<FlashCard>,
    onDismiss: () -> Unit,
    onSelectCard: (index: Int) -> Unit,
) {
    var searchWord by remember { mutableStateOf("") }

    val resultIndexes by remember(searchWord, cards) {
        derivedStateOf {
            if (searchWord.isBlank()) emptyList()
            else cards.mapIndexedNotNull { index, card ->
                if (card.word.contains(searchWord.trim(), ignoreCase = true) ||
                    card.meaning.contains(searchWord.trim(), ignoreCase = true)
                ) index else null
            }
        }
    }

    var currentIndex by remember(resultIndexes) { mutableIntStateOf(0) }

    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(resultIndexes) {
        currentIndex = 0
        if (resultIndexes.isNotEmpty()) {
            onSelectCard(resultIndexes[0])
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    TextField(
                        value = searchWord,
                        onValueChange = {
                            searchWord = it
                        },
                        placeholder = {
                            Text(
                                text = "Поиск по карточкам...",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        textStyle = TextStyle(
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = { keyboardController?.hide() }
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Закрыть поиск",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            AnimatedVisibility(
                visible = searchWord.isNotBlank(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column {
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .fillMaxWidth(),
                        color = MaterialTheme.colorScheme.outlineVariant,
                        thickness = 1.dp
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val statusText =
                            if (resultIndexes.isEmpty()) "Ничего не найдено" else "${currentIndex + 1} из ${resultIndexes.size}"

                        Text(
                            text = statusText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (resultIndexes.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary
                        )

                        Row {

                            IconButton(
                                onClick = {
                                    keyboardController?.hide()
                                    if (resultIndexes.isNotEmpty()) {
                                        currentIndex =
                                            if (currentIndex > 0) currentIndex - 1 else resultIndexes.size - 1
                                        onSelectCard(resultIndexes[currentIndex])
                                    }
                                },
                                enabled = resultIndexes.isNotEmpty(),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowUpward,
                                    contentDescription = "Предыдущее совпадение",
                                    tint = if (resultIndexes.isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                )
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            IconButton(
                                onClick = {
                                    keyboardController?.hide()
                                    if (resultIndexes.isNotEmpty()) {
                                        currentIndex = (currentIndex + 1) % resultIndexes.size
                                        onSelectCard(resultIndexes[currentIndex])

                                    }
                                },
                                enabled = resultIndexes.isNotEmpty(),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDownward,
                                    contentDescription = "Следующее совпадение",
                                    tint = if (resultIndexes.isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchCardContentPreview() {
    FlushCardsTheme {
        SearchCardContent(
            cards = mutableListOf(FlashCard(1, "test", "тестовый")),
            onDismiss = {},
            onSelectCard = {}
        )
    }
}