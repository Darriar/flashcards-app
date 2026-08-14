package com.example.flushcards.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastCoerceIn
import com.example.flushcards.R
import com.example.flushcards.data.constants.ModuleConfig
import com.example.flushcards.data.model.FlashCard
import com.example.flushcards.data.model.Module
import com.example.flushcards.data.services.TranslationService
import com.example.flushcards.ui.components.AppTopBar
import com.example.flushcards.ui.components.ImportModalSheet
import com.example.flushcards.ui.components.ScrollDownButton
import com.example.flushcards.ui.components.SearchCard
import com.example.flushcards.ui.theme.FlushCardsTheme
import com.example.flushcards.util.cardHighlightBackgroundColor
import com.example.flushcards.util.cardHighlightBorderColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun EditModuleScreen(
    module: Module,
    onOk: (localModule: Module) -> Unit,
    onExit: (localModule: Module) -> Unit
) {

    var localModule by remember {
        val initialCards = buildList {
            if (module.cards.isEmpty()) {
                repeat(ModuleConfig.MIN_CARDS_COUNT) { index ->
                    add(FlashCard(id = index + 1, word = "", meaning = ""))
                }
            } else {
                addAll(module.cards.map { it.copy() })
            }
        }
        mutableStateOf(module.copy(cards = initialCards.toMutableStateList()))
    }

    val validCardsCount by remember {
        derivedStateOf {
            localModule.cards.count { it.word.isNotBlank() && it.meaning.isNotBlank() }
        }
    }

    val isReadyEnabled =
        validCardsCount >= ModuleConfig.MIN_CARDS_COUNT && localModule.name.isNotBlank()
    val isSearchActive = remember { mutableStateOf(false) }
    val isImportCards = remember { mutableStateOf(false) }

    val cardsListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var highlightedCardIndex by remember { mutableIntStateOf(-1) }

    LaunchedEffect(highlightedCardIndex) {
        if (highlightedCardIndex < 0) return@LaunchedEffect
        cardsListState.animateScrollToItem(highlightedCardIndex)

        delay(1500)
        highlightedCardIndex = -1
    }

    BackHandler { onExit(localModule) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                AppTopBar(
                    title = "Редактировать",
                    onBack = { onExit(localModule) },
                    rightPartContent = {
                        RightPartContent(
                            onSearchClick = { isSearchActive.value = true },
                            onImportClick = { isImportCards.value = true }
                        )
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            },
            bottomBar = {
                ReadyButton(
                    isReadyEnabled = isReadyEnabled,
                    modifier = Modifier.align(Alignment.BottomCenter),
                    onReady = { onOk(localModule) }
                )
            }
        ) { innerPadding ->

            LazyColumn(
                state = cardsListState,
                contentPadding = innerPadding,
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    ModuleNameInput(
                        name = localModule.name,
                        onNameChange = { localModule = localModule.copy(name = it) },
                    )
                }

                item {
                    if (validCardsCount < ModuleConfig.MIN_CARDS_COUNT) {
                        MinCardsWarningCard(remainingCardsCount = ModuleConfig.MIN_CARDS_COUNT - validCardsCount)
                    }
                }

                itemsIndexed(
                    items = localModule.cards,
                    key = { _, card -> card.id }
                ) { index, card ->
                    EditCard(
                        card = card,
                        isHighlighted = index == highlightedCardIndex,
                        onWordChange = { newWord ->
                            localModule.cards[index] = card.copy(word = newWord)
                        },
                        onMeaningChange = { newMeaning ->
                            localModule.cards[index] = card.copy(meaning = newMeaning)
                        },
                        onDeleteCard = {
                            localModule.cards.remove(card)
                        }
                    )

                }

            }
        }

        ScrollDownButton(
            state = cardsListState,
            cards = localModule.cards,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 160.dp)
        )

        AddCardFab(
            onClick = {
                val newId =
                    if (localModule.cards.isEmpty()) 1 else localModule.cards.maxOf { it.id } + 1
                localModule.cards.add(FlashCard(newId, "", ""))
                scope.launch {
                    cardsListState.animateScrollToItem(localModule.cards.size - 1)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 96.dp, end = 24.dp)
        )
    }

    if (isSearchActive.value) {
        SearchCard(
            module.cards,
            onDismiss = { isSearchActive.value = false },
            onSelectCard = { index ->
                highlightedCardIndex = index
            }
        )
    }
    if (isImportCards.value) {
        ImportModalSheet(
            onDismiss = { isImportCards.value = false },
            onImport = { parsedCards ->
                parsedCards.forEach { localModule.addCard(it) }
            }
        )
    }
}

@Composable
private fun RightPartContent(
    onSearchClick: () -> Unit,
    onImportClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            modifier = Modifier
                .clip(CircleShape)
                .size(28.dp)
                .clickable(onClick = onSearchClick),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Icon(
            imageVector = Icons.Default.UploadFile,
            contentDescription = "Cards Import",
            modifier = Modifier
                .clip(CircleShape)
                .size(28.dp)
                .clickable(onClick = onImportClick),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ReadyButton(isReadyEnabled: Boolean, onReady: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = {
            if (isReadyEnabled) {
                onReady()
            }
        },
        enabled = isReadyEnabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = modifier
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
}

@Composable
private fun ModuleNameInput(
    name: String,
    onNameChange: (String) -> Unit
) {
    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        textStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
        label = { Text("Название модуля") },
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        )
    )
}

@Composable
private fun MinCardsWarningCard(
    remainingCardsCount: Int,
) {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = "Добавьте еще $remainingCardsCount слов(а), чтобы сохранить модуль",
            color = MaterialTheme.colorScheme.onErrorContainer,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AddCardFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        shape = CircleShape,
        modifier = modifier.size(56.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Добавить карточку",
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
private fun EditCard(
    card: FlashCard,
    isHighlighted: Boolean,
    onWordChange: (String) -> Unit,
    onMeaningChange: (String) -> Unit,
    onDeleteCard: () -> Unit
) {
    val dragProgress = remember { Animatable(0f) }
    val minCardWeight = 0.75f
    val maxDeleteWeight = 1f - minCardWeight
    val scope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .height(intrinsicSize = IntrinsicSize.Min)
            .fillMaxWidth(),

        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .weight(((1f - dragProgress.value)).coerceAtLeast(minCardWeight))
                .clipToBounds()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                val target =
                                    if (dragProgress.value > maxDeleteWeight / 2) 1f else 0f
                                dragProgress.animateTo(
                                    targetValue = target,
                                    animationSpec = tween(durationMillis = 200)
                                )
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            val newProgress = (dragProgress.value - dragAmount / 500f).coerceIn(
                                0f,
                                maxDeleteWeight
                            )
                            scope.launch {
                                dragProgress.snapTo(newProgress)
                            }
                        }
                    )
                },
            colors = CardDefaults.cardColors(
                containerColor = cardHighlightBackgroundColor(
                    isHighlighted
                )
            ),
            border = BorderStroke(2.dp, cardHighlightBorderColor(isHighlighted)),
        ) {
            CardContent(
                card,
                onWordChange = onWordChange,
                onMeaningChange = onMeaningChange
            )
        }

        if (dragProgress.value > 0.001f) {
            DeleteCard(
                modifier = Modifier.weight(
                    dragProgress.value.fastCoerceIn(
                        0.0001f,
                        maxDeleteWeight
                    )
                ),
                onDeleteClick = onDeleteCard
            )
        }
    }
}

@Composable
private fun CardContent(
    card: FlashCard,
    onWordChange: (String) -> Unit,
    onMeaningChange: (String) -> Unit
) {
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
private fun DeleteCard(
    modifier: Modifier = Modifier,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .clipToBounds(),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.errorContainer),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onDeleteClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Удалить",
                tint = MaterialTheme.colorScheme.error
            )
        }
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
fun EditModulePreview() {
    FlushCardsTheme {
        EditModuleScreen(
            module = Module(1, "English", mutableListOf(FlashCard(1, "test", "тестовый")), true),
            onOk = {}, onExit = {}
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

@Preview(showBackground = true, widthDp = 100, heightDp = 80)
@Composable
fun DeleteCardPreview() {
    FlushCardsTheme {
        Box(modifier = Modifier.size(width = 80.dp, height = 80.dp)) {
            DeleteCard(
                onDeleteClick = {}
            )
        }
    }
}