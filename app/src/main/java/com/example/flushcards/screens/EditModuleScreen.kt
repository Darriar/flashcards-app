import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastCoerceIn
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.flushcards.R
import com.example.flushcards.api.TranslationService
import com.example.flushcards.model.FlashCard
import com.example.flushcards.model.Module
import com.example.flushcards.model.ModuleConfig
import com.example.flushcards.screens.CurrentScreenHeader
import com.example.flushcards.ui.theme.FlushCardsTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun EditModuleScreen(module: Module, onOk: (localModule: Module) -> Unit, onExit: (localModule: Module) -> Unit) {

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

    val cardsListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    BackHandler { onExit(localModule) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)

    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                ) {
                    CreateModuleHeader(onExit, localModule, cardsListState)
                }
            },
            bottomBar = {
                Button(
                    onClick = {
                        if (isReadyEnabled) {
                            onOk(localModule)
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
                    OutlinedTextField(
                        value = localModule.name,
                        onValueChange = { localModule = localModule.copy(name = it) },
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


                item {
                    if (validCardsCount < ModuleConfig.MIN_CARDS_COUNT) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "Добавьте еще ${ModuleConfig.MIN_CARDS_COUNT - validCardsCount} слов(а), чтобы сохранить модуль",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                itemsIndexed(
                    items = localModule.cards,

                    key = { _, card -> card.id }
                ) { index, card ->

                    CreateCard(
                        card = card,
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

        ButtonScrollDown(
            state = cardsListState,
            cards = localModule.cards,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 160.dp)
        )

        FloatingActionButton(
            onClick = {
                val newId =
                    if (localModule.cards.isEmpty()) 1 else localModule.cards.maxOf { it.id } + 1
                localModule.cards.add(FlashCard(newId, "", ""))
                scope.launch {
                    cardsListState.animateScrollToItem(localModule.cards.size - 1)
                }
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
fun ButtonScrollDown(
    state: LazyListState,
    cards: List<FlashCard>,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    val isLastCardVisible by remember(state, cards.size) {
        derivedStateOf {
            if (cards.isEmpty()) return@derivedStateOf false

            val lastCardId = cards.last().id
            val lastCardInfo = state.layoutInfo.visibleItemsInfo.find { it.key == lastCardId }
            if (lastCardInfo != null) {
                val cardBottom = lastCardInfo.offset + lastCardInfo.size
                cardBottom <= state.layoutInfo.viewportEndOffset
            } else {
                false
            }
        }
    }

    AnimatedVisibility(
        visible = !isLastCardVisible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        if (state.layoutInfo.totalItemsCount > 0) {
                            state.animateScrollToItem(state.layoutInfo.totalItemsCount - 1)
                        }
                    }
                },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.shadow(
                    elevation = 6.dp,
                    shape = CircleShape
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = "Прокрутить вниз",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun CreateCard(
    card: FlashCard,
    onWordChange: (String) -> Unit,
    onMeaningChange: (String) -> Unit,
    onDeleteCard: () -> Unit
) {
    var suggestedTranslation by remember { mutableStateOf("") }
    var isTextFieldFocused by remember { mutableStateOf(false) }

    val dragProgress = remember { Animatable(0f) }
    val minCardWeight = 0.75f
    val maxDeleteWeight = 1f - minCardWeight
    val scope = rememberCoroutineScope()


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

    Row(
        modifier = Modifier
            .height(intrinsicSize = IntrinsicSize.Min)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Card(
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
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                TextField(
                    value = card.word,
                    onValueChange = { newWord -> onWordChange(newWord) },
                    label = { Text("Термин", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    textStyle = TextStyle(fontSize = 16.sp),
                    modifier = Modifier
                        .fillMaxWidth(),
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

                TextField(
                    value = card.meaning,
                    onValueChange = { newMeaning -> onMeaningChange(newMeaning) },
                    label = { Text("Значение", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    textStyle = TextStyle(fontSize = 16.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            isTextFieldFocused = focusState.isFocused
                        },
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

        if (dragProgress.value > 0.001f) {
            Card(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .weight(dragProgress.value.fastCoerceIn(0.0001f, maxDeleteWeight))
                    .clipToBounds(),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.errorContainer),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(onClick = { onDeleteCard() }),
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
    }
}


@Composable
fun CreateModuleHeader(
    onBack: (localModule: Module) -> Unit,
    module: Module,
    cardsListState: LazyListState
) {
    var isSearchActive by remember { mutableStateOf(false) }

    Box {
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
                        .clickable { onBack(module) },
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Редактирование",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .clickable { isSearchActive = true },
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (isSearchActive) {
            SearchCard(module.cards, cardsListState, onDismiss = { isSearchActive = false })
        }
    }
}

@Composable
fun SearchCard(
    cards: MutableList<FlashCard>,
    cardsListState: LazyListState,
    onDismiss: () -> Unit
) {

    var searchWord by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val resultIndexes by remember(searchWord, cards) {
        derivedStateOf {
            if (searchWord.isBlank()) emptyList()
            else cards.mapIndexedNotNull { index, card ->
                val query = searchWord.trim()
                if (card.word.contains(query, ignoreCase = true) ||
                    card.meaning.contains(query, ignoreCase = true)
                ) index else null
            }
        }
    }

    var currentIndex by remember(resultIndexes) { mutableIntStateOf(0) }

    Popup(
        alignment = Alignment.TopCenter,
        onDismissRequest = onDismiss,
        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
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
                            onValueChange = { searchWord = it },
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
                                        if (resultIndexes.isNotEmpty()) {
                                            currentIndex =
                                                if (currentIndex > 0) currentIndex - 1 else resultIndexes.size - 1
                                            scope.launch {
                                                cardsListState.animateScrollToItem(resultIndexes[currentIndex])
                                            }
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
                                        if (resultIndexes.isNotEmpty()) {
                                            currentIndex = (currentIndex + 1) % resultIndexes.size
                                            scope.launch {
                                                cardsListState.animateScrollToItem(resultIndexes[currentIndex])
                                            }
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
}

@Preview(showBackground = true)
@Composable
fun EditModulePreview() {
    FlushCardsTheme {
        EditModuleScreen(
            module = Module(1,"English", mutableListOf(FlashCard(1, "test", "тестовый")), true),
            onOk = {}, onExit = {}
        )
    }
}