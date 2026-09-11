package com.example.flushcards.ui.screens.editModule

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flushcards.data.constants.ModuleConfig
import com.example.flushcards.data.model.FlashCard
import com.example.flushcards.data.model.Module
import com.example.flushcards.ui.components.AppTopBar
import com.example.flushcards.ui.components.ImportModalSheet
import com.example.flushcards.ui.components.ScrollDownButton
import com.example.flushcards.ui.components.SearchCard
import com.example.flushcards.ui.screens.editModule.components.EditCard
import com.example.flushcards.ui.screens.editModule.components.MinCardsWarningCard
import com.example.flushcards.ui.screens.editModule.components.ModuleNameInput
import com.example.flushcards.ui.screens.editModule.components.ReadyButton
import com.example.flushcards.ui.theme.FlushCardsTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds



@Composable
fun EditModuleScreen(
    module: Module,
    onOk: (localModule: Module) -> Unit,
    onExit: (localModule: Module) -> Unit,
    viewModel: EditModuleViewModel = viewModel(
        key = module.id.toString(),
        factory = viewModelFactory {
            initializer { EditModuleViewModel(module) }
        }
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    val cardsListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.highlightedCardIndex) {
        if (uiState.highlightedCardIndex < 0) return@LaunchedEffect
        cardsListState.animateScrollToItem(uiState.highlightedCardIndex)

        delay(1500.milliseconds)
        viewModel.canselSelectCard()
    }

    BackHandler { onExit(uiState.localModule) }

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
                    onBack = { onExit(uiState.localModule) },
                    rightPartContent = {
                        RightPartContent(
                            onSearchClick = { viewModel.onSearchClick() },
                            onImportClick = { viewModel.onImportClick() }
                        )
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            },
            bottomBar = {
                ReadyButton(
                    isReadyEnabled = uiState.isReadyEnabled,
                    onReady = { onOk(uiState.localModule) }
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
                        name = uiState.localModule.name,
                        onNameChange = { viewModel.onNameChange(newName = it) },
                    )
                }

                item {
                    if (uiState.validCardsCount < ModuleConfig.MIN_CARDS_COUNT) {
                        MinCardsWarningCard(remainingCardsCount = ModuleConfig.MIN_CARDS_COUNT - uiState.validCardsCount)
                    }
                }

                itemsIndexed(
                    items = uiState.localModule.cards,
                    key = { _, card -> card.id }
                ) { index, card ->
                    EditCard(
                        card = card,
                        isHighlighted = index == uiState.highlightedCardIndex,
                        onWordChange = { newWord -> viewModel.onWordChange(index, card, newWord)},
                        onMeaningChange = { newMeaning -> viewModel.onMeaningChange(index, card, newMeaning)},
                        onDeleteCard = { viewModel.onDeleteCard(index) }
                    )

                }

            }
        }

        ScrollDownButton(
            state = cardsListState,
            cards = uiState.localModule.cards,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 160.dp)
        )

        AddCardFab(
            onClick = {
                viewModel.onAddCard()
                scope.launch {
                    cardsListState.animateScrollToItem(uiState.localModule.cards.size - 1)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 96.dp, end = 24.dp)
        )
    }

    if (uiState.isSearchActive) {
        SearchCard(
            uiState.localModule.cards,
            onDismiss = { viewModel.canselSearch() },
            onSelectCard = { index -> viewModel.onSelectCard(index)}
        )
    }
    if (uiState.isImportActive) {
        ImportModalSheet(
            onDismiss = { viewModel.canselImport() },
            onImport = { parsedCards -> viewModel.onImportCards(parsedCards) }
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