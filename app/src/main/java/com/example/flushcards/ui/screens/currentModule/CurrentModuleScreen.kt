package com.example.flushcards.ui.screens.currentModule

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flushcards.data.model.Module
import com.example.flushcards.data.model.defaultStudyModes
import com.example.flushcards.data.preview.SampleData
import com.example.flushcards.ui.components.AppTopBar
import com.example.flushcards.ui.components.ScrollDownButton
import com.example.flushcards.ui.components.SearchCard
import com.example.flushcards.ui.navigation.Screen
import com.example.flushcards.ui.screens.currentModule.components.CardInfo
import com.example.flushcards.ui.screens.currentModule.components.ModeCard
import com.example.flushcards.ui.screens.currentModule.components.ModuleOptionsMenu
import com.example.flushcards.ui.theme.FlushCardsTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun CurrentModuleScreen(
    currentModule: Module,
    onNavigate: (Screen) -> Unit,
    onDelete: (Module) -> Unit,
    onExit: () -> Unit,
    viewModel: CurrentModuleViewModel = viewModel(
     key = currentModule.id.toString(),
        factory = viewModelFactory {
            initializer { CurrentModuleViewModel(module = currentModule)}
        }
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val cardsInfoState = rememberLazyListState()

    LaunchedEffect(uiState.highlightedCardIndex) {
        if (uiState.highlightedCardIndex < 0) return@LaunchedEffect
        cardsInfoState.animateScrollToItem(uiState.highlightedCardIndex)

        delay(1500.milliseconds)
        viewModel.onDoNotSelectCard()
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
                    title = uiState.moduleName,
                    onBack = onExit,
                    rightPartContent = {
                        RightPartContent(
                            isTermFirst = uiState.isTermFirst,
                            onSearchClick = { viewModel.onSearchClick() },
                            onEditClick = { onNavigate(Screen.EditModule) },
                            onResetClick = { viewModel.resetProgress() },
                            onModeSelect = { isTermFirst -> viewModel.setCardMode(isTermFirst) },
                            onDeleteClick = {
                                onNavigate(Screen.MyModules)
                                onDelete(currentModule)
                            }
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

                    CardsSectionHeader(size = uiState.totalCardsCount)
                }

                itemsIndexed(
                    items = uiState.cards,
                    key = { _, card -> card.id }
                ) { index, card ->
                    CardInfo(
                        card = card,
                        isHighlighted = index == uiState.highlightedCardIndex,
                    )
                }

            }
        }

        ScrollDownButton(
            state = cardsInfoState,
            cards = uiState.cards,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 40.dp)
        )
    }

    if (uiState.isSearchActive) {
        SearchCard(
            cards = currentModule.cards,
            onDismiss = { viewModel.onDoNotSearch() },
            onSelectCard = { index -> viewModel.onSelectCard(index) }
        )
    }
}

@Composable
private fun RightPartContent(
    isTermFirst: Boolean,
    onSearchClick: () -> Unit,
    onEditClick: () -> Unit,
    onResetClick: () -> Unit,
    onModeSelect: (isTermFirst: Boolean) -> Unit,
    onDeleteClick: () -> Unit
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

        ModuleOptionsMenu(
            isTermFirst = isTermFirst,
            onEditClick = onEditClick,
            onResetClick = onResetClick,
            onModeSelect = onModeSelect,
            onDeleteClick = onDeleteClick
        )
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

@Preview(showBackground = true)
@Composable
fun CurrentModulePreview() {
    FlushCardsTheme {
        CurrentModuleScreen(SampleData.vocabularyModule, onNavigate = {}, onDelete = {}, onExit = {})
    }
}

