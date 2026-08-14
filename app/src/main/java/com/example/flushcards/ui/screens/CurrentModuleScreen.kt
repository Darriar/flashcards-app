package com.example.flushcards.ui.screens

import android.speech.tts.TextToSpeech
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flushcards.data.model.FlashCard
import com.example.flushcards.data.model.Module
import com.example.flushcards.data.model.defaultStudyModes
import com.example.flushcards.ui.components.AppTopBar
import com.example.flushcards.ui.components.ScrollDownButton
import com.example.flushcards.ui.components.SearchCard
import com.example.flushcards.ui.navigation.Screen
import com.example.flushcards.ui.theme.FlushCardsTheme
import com.example.flushcards.util.cardHighlightBackgroundColor
import com.example.flushcards.util.cardHighlightBorderColor
import com.example.flushcards.util.convertTextToSpeech
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun CurrentModuleScreen(
    currentModule: Module,
    onNavigate: (Screen) -> Unit,
    onDelete: (Module) -> Unit,
    onExit: () -> Unit
) {
    val cardsInfoState = rememberLazyListState()

    var highlightedCardIndex by remember { mutableIntStateOf(-1) }
    val isSearchActive = remember { mutableStateOf(false) }

    LaunchedEffect(highlightedCardIndex) {
        if (highlightedCardIndex < 0) return@LaunchedEffect
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
                AppTopBar(
                    title = currentModule.name,
                    onBack = onExit,
                    rightPartContent = {
                        RightPartContent(
                            module = currentModule,
                            onSearchClick = { isSearchActive.value = true },
                            onNavigate = onNavigate,
                            onDelete = onDelete
                        )
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
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

                    defaultStudyModes.forEach { mode ->
                        ModeCard(
                            title = mode.title,
                            description = mode.description,
                            icon = mode.icon,
                            onClick = { onNavigate(mode.targetScreen) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    CardsSectionHeader(size = currentModule.cards.size)
                }

                itemsIndexed(
                    items = currentModule.cards,
                    key = { _, card -> card.id }
                ) { index, card ->
                    CardInfo(
                        card = card,
                        isHighlighted = index == highlightedCardIndex
                    )
                }

            }
        }

        ScrollDownButton(
            state = cardsInfoState,
            cards = currentModule.cards,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 40.dp)
        )
    }

    if (isSearchActive.value) {
        SearchCard(
            cards = currentModule.cards,
            onDismiss = { isSearchActive.value = false },
            onSelectCard = { index -> highlightedCardIndex = index }
        )
    }
}

@Composable
private fun RightPartContent(
    module: Module,
    onSearchClick: () -> Unit,
    onNavigate: (Screen) -> Unit,
    onDelete: (Module) -> Unit
) {
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
                .clickable(onClick = onSearchClick),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        ModuleOptionsMenu(module, onNavigate, onDelete)
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
fun CardsSectionHeader(size: Int) {
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
        text = "В этом модуле карточек: $size",
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)

    )
}

@Composable
fun CardInfo(card: FlashCard, isHighlighted: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = cardColors(containerColor = cardHighlightBackgroundColor(isHighlighted)),
        border = BorderStroke(2.dp, cardHighlightBorderColor(isHighlighted)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                PronounceButton(card)

                Text(
                    text = card.word,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Spacer(modifier = Modifier.width(24.dp))
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                thickness = 1.dp,
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
fun PronounceButton(card: FlashCard) {
    val tts = if (LocalInspectionMode.current) null else convertTextToSpeech()
    IconButton(
        onClick = {
            tts?.let { engine ->
                engine.language = Locale.UK
                engine.speak(
                    card.word,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "UtteranceId_${card.word}"
                )
            }

            tts?.let { engine ->
                engine.language = Locale.forLanguageTag("ru-RU")
                engine.speak(
                    card.meaning,
                    TextToSpeech.QUEUE_ADD,
                    null,
                    "UtteranceId_${card.meaning}"
                )
            }
        },
        modifier = Modifier.size(32.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
            contentDescription = "Произнести",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ModuleOptionsMenu(
    module: Module,
    onNavigate: (Screen) -> Unit,
    onDelete: (Module) -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {

        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "Настройки модуля",
            modifier = Modifier
                .clip(CircleShape)
                .size(28.dp)
                .clickable(onClick = { menuExpanded = true })
        )

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier.border(
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shape = RoundedCornerShape(16.dp)
            )
        ) {
            DropdownMenuItem(
                text = { Text("Редактировать", fontWeight = FontWeight.Medium) },
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
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
                leadingIcon = { Icon(Icons.Default.Refresh, contentDescription = null) },
                colors = MenuDefaults.itemColors(
                    textColor = MaterialTheme.colorScheme.onSurface,
                    leadingIconColor = MaterialTheme.colorScheme.primary
                ),
                onClick = {
                    menuExpanded = false
                    module.resetProgress()
                }
            )

            CardModeSubMenuItem(
                module = module,
                onCloseMainMenu = { menuExpanded = false }
            )

            DropdownMenuItem(
                text = { Text("Удалить", fontWeight = FontWeight.Medium) },
                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) },
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

@Composable
private fun CardModeSubMenuItem(
    module: Module,
    onCloseMainMenu: () -> Unit
) {
    var subMenuExpanded by remember { mutableStateOf(false) }
    var isTermFirst by remember(module) { mutableStateOf(module.isTermFirst) }
    val scope = rememberCoroutineScope()

    fun selectMode(termFirst: Boolean, action: () -> Unit) {
        isTermFirst = termFirst
        action()
        scope.launch {
            delay(300)
            subMenuExpanded = false
            onCloseMainMenu()
        }
    }

    Box {
        DropdownMenuItem(
            text = { Text("Режим карточек", fontWeight = FontWeight.Medium) },
            leadingIcon = { Icon(Icons.Default.Style, contentDescription = null) },
            trailingIcon = {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowRight,
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
            CardModeOptionItem(
                text = "Термин ➔ Определение",
                isSelected = isTermFirst,
                onClick = { selectMode(termFirst = true) { module.showTermFirst() } }
            )

            CardModeOptionItem(
                text = "Определение ➔ Термин",
                isSelected = !isTermFirst,
                onClick = { selectMode(termFirst = false) { module.showMeaningFirst() } }
            )
        }
    }
}

@Composable
private fun CardModeOptionItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Text(
                text = text,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        },
        leadingIcon = {
            if (isSelected) {
                Icon(Icons.Default.Check, contentDescription = null)
            }
        },
        colors = MenuDefaults.itemColors(
            textColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            leadingIconColor = MaterialTheme.colorScheme.primary
        ),
        onClick = onClick
    )
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

