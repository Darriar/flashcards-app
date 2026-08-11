package com.example.flushcards.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flushcards.R
import com.example.flushcards.model.FlashCard
import com.example.flushcards.model.Module
import com.example.flushcards.model.Screen
import com.example.flushcards.ui.theme.FlushCardsTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CurrentModuleScreen(
    currentModule: Module,
    onNavigate: (Screen) -> Unit,
    onDelete: (Module) -> Unit,
    onExit: () -> Unit
) {
    val cardsInfoState = rememberLazyListState()

    var highlightedCardIndex by remember { mutableIntStateOf(-1) }

    LaunchedEffect(highlightedCardIndex) {
        if (highlightedCardIndex < 0)  return@LaunchedEffect
        cardsInfoState.animateScrollToItem(highlightedCardIndex)

        delay(1500)
        highlightedCardIndex = -1
    }

    BackHandler { onExit() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp
                )
                ) {
                    CurrentScreenHeader(
                        module = currentModule,
                        cardsInfoState = cardsInfoState,
                        onNavigate = onNavigate,
                        onDelete = onDelete,
                        onBack = onExit,
                        onSelectedCard = {index ->
                            highlightedCardIndex = index
                        })
                }
            }
        ) { innerPadding ->

            LazyColumn(
                state = cardsInfoState,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = innerPadding
            ) {
                item {
                    Text(
                        text = "Режимы обучения",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                    )

                    ModeCard(
                        title = "Флеш-карточки",
                        description = "Повторяйте термины и проверяйте себя в классическом режиме",
                        icon = Icons.Default.Layers,
                        onClick = { onNavigate(Screen.FlipCards) }
                    )

                    ModeCard(
                        title = "Тест",
                        description = "Выбирайте правильное значение из нескольких вариантов",
                        icon = Icons.Default.Quiz,
                        onClick = { onNavigate(Screen.Quiz) }
                    )

                    ModeCard(
                        title = "Мэтчинг",
                        description = "Соединяйте слова и их значения",
                        icon = Icons.Default.Extension,
                        onClick = { onNavigate(Screen.Match) }
                    )

                    ModeCard(
                        title = "Письменная практика",
                        description = "Вводите точные переводы для максимального запоминания",
                        icon = Icons.Default.Keyboard,
                        onClick = { onNavigate(Screen.Write) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Карточки",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )

                    Text(
                        text = "В этом модуле карточек: ${currentModule.cards.size}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp)

                    )
                }

                itemsIndexed(
                    items = currentModule.cards,
                    key = { _, card -> card.id }
                ) { index, card ->
                    CardInfo(card = card,
                        isHighlighted = index == highlightedCardIndex)
                }

            }
        }
        ButtonScrollDown(
            state = cardsInfoState,
            cards = currentModule.cards,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 40.dp)
        )
    }
}

@Composable
fun ModeCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun CardInfo(card: FlashCard, isHighlighted: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = cardColors(containerColor = getBackgroundCardColor(isHighlighted)),
        border = BorderStroke(2.dp, getBorderCardColor(isHighlighted)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = card.word,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                thickness = 1.dp, // 1.dp смотрится аккуратнее
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
            )

            Text(
                text = card.meaning,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}


@Composable
fun CurrentScreenHeader(
    module: Module,
    cardsInfoState: LazyListState,
    onNavigate: (Screen) -> Unit,
    onDelete: (Module) -> Unit,
    onBack: () -> Unit,
    onSelectedCard: (Int) -> Unit
) {
    var isSearchActive by remember { mutableStateOf(false) }

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
                text = module.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .clickable { isSearchActive = true },
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Menu(module, onNavigate, onDelete)
            }
        }
    }
    if (isSearchActive) {
        SearchCard(
            cards = module.cards,
            onDismiss = { isSearchActive = false },
            onSelectCard = onSelectedCard)
    }
}

@Composable
fun Menu(
    module: Module,
    onNavigate: (Screen) -> Unit,
    onDelete: (Module) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var subMenuExpanded by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    var isTermFirst by remember(module) { mutableStateOf(module.isTermFirst) }

    Box {
        IconButton(onClick = { menuExpanded = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Настройки модуля",
            )
        }

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = {
                menuExpanded = false
                subMenuExpanded = false
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier.border(
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shape = RoundedCornerShape(16.dp)
            )
        ) {
            DropdownMenuItem(
                text = { Text("Редактировать", fontWeight = FontWeight.Medium) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null
                    )
                },
                colors = MenuDefaults.itemColors(
                    textColor = MaterialTheme.colorScheme.onSurface,
                    leadingIconColor = MaterialTheme.colorScheme.primary
                ),
                onClick = {
                    menuExpanded = false
                    onNavigate(Screen.EditModule)
                }
            )

            DropdownMenuItem(
                text = { Text("Повторить все сначала", fontWeight = FontWeight.Medium) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null
                    )
                },
                colors = MenuDefaults.itemColors(
                    textColor = MaterialTheme.colorScheme.onSurface,
                    leadingIconColor = MaterialTheme.colorScheme.primary
                ),
                onClick = {
                    menuExpanded = false
                    module.resetProgress()
                }
            )

            Box {
                DropdownMenuItem(
                    text = { Text("Режим карточек", fontWeight = FontWeight.Medium) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Style,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowRight,
                            contentDescription = null
                        )
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = MaterialTheme.colorScheme.onSurface,
                        leadingIconColor = MaterialTheme.colorScheme.primary,
                        trailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    onClick = { subMenuExpanded = !subMenuExpanded }
                )

                DropdownMenu(
                    expanded = subMenuExpanded,
                    onDismissRequest = { subMenuExpanded = false },
                    shape = RoundedCornerShape(16.dp),
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.border(
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        shape = RoundedCornerShape(16.dp)
                    )
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Термин ➔ Определение",
                                fontWeight = if (isTermFirst) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        leadingIcon = {
                            if (isTermFirst) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null
                                )
                            }
                        },
                        colors = MenuDefaults.itemColors(
                            textColor = if (isTermFirst) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            leadingIconColor = MaterialTheme.colorScheme.primary
                        ),
                        onClick = {
                            isTermFirst = true
                            module.showTermFirst()

                            scope.launch {
                                delay(300)
                                subMenuExpanded = false
                                menuExpanded = false
                            }
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Определение ➔ Термин",
                                fontWeight = if (!isTermFirst) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        leadingIcon = {
                            if (!isTermFirst) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null
                                )
                            }
                        },
                        colors = MenuDefaults.itemColors(
                            textColor = if (!isTermFirst) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            leadingIconColor = MaterialTheme.colorScheme.primary
                        ),
                        onClick = {
                            isTermFirst = false
                            module.showMeaningFirst()

                            scope.launch {
                                delay(300)
                                subMenuExpanded = false
                                menuExpanded = false
                            }
                        }
                    )
                }
            }

            DropdownMenuItem(
                text = { Text("Удалить", fontWeight = FontWeight.Medium) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null
                    )
                },
                colors = MenuDefaults.itemColors(
                    textColor = MaterialTheme.colorScheme.error,
                    leadingIconColor = MaterialTheme.colorScheme.error
                ),
                onClick = {
                    menuExpanded = false
                    onNavigate(Screen.MyModules)
                    onDelete(module)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CurrentModulePreview() {
    val cards = remember {
        mutableStateListOf(
            FlashCard(1, "assess", "оценивать"),
            FlashCard(2, "overrated", "переоцененный"),
            FlashCard(3, "eternal", "вечный, неизменный"),
            FlashCard(4, "invading", "вторжение")
        )
    }
    val currentModule = Module(1, "textModule", cards, true)
    FlushCardsTheme {
        CurrentModuleScreen(currentModule, onNavigate = {}, onDelete = {}, onExit = {})
    }
}

@Preview(showBackground = true)
@Composable
fun CardInfoPreview() {
    FlushCardsTheme {
        CardInfo(FlashCard(1, "assess", "оценивать"), true)
    }
}

