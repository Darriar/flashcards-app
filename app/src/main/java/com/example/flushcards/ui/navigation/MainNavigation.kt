package com.example.flushcards.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flushcards.data.constants.ModuleConfig
import com.example.flushcards.data.model.Module
import com.example.flushcards.data.storage.ModuleStorageService
import com.example.flushcards.data.storage.ModuleStorageService.deleteModule
import com.example.flushcards.ui.components.EditModuleDialogState
import com.example.flushcards.ui.components.EditModuleDialogs
import com.example.flushcards.ui.screens.currentModule.CurrentModuleScreen
import com.example.flushcards.ui.screens.editModule.EditModuleScreen
import com.example.flushcards.ui.screens.learningscreens.flashCards.FlashCardsScreen
import com.example.flushcards.ui.screens.learningscreens.match.MatchScreen
import com.example.flushcards.ui.screens.learningscreens.quiz.QuizScreen
import com.example.flushcards.ui.screens.learningscreens.write.WriteScreen
import com.example.flushcards.ui.screens.myModules.MyModulesScreen
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun FlipCardsNavigation(
    viewModel: MainViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadModules(context)
    }

    when (uiState.currentScreen) {
        Screen.MyModules -> MyModulesScreen(
            modules = uiState.modules,
            onModuleCLick = { viewModel.selectModule(it) },
            onAddModule = { viewModel.addNewModule() }
        )

        Screen.CurrentModule -> SetCurrentModule(
            currentModule = uiState.currentModule,
            onNavigate = { viewModel.navigateTo(it) },
            onModuleDeleted = { viewModel.deleteModuleFromList(it) },
            onExit = { viewModel.navigateTo(Screen.MyModules) }
        )

        Screen.EditModule -> SetEditModule(
            currentModule = uiState.currentModule,
            modules = uiState.modules,
            onModuleSaved = { viewModel.saveModule(it) },
            onNavigateToMyModules = { viewModel.navigateTo(Screen.MyModules) },
            onNavigateToCurrentModule = { viewModel.navigateTo(Screen.CurrentModule) },
            onRemoveModule = { viewModel.deleteModuleFromList(it) }
        )

        Screen.FlipCards -> FlashCardsScreen(
            module = uiState.currentModule,
            onExit = { viewModel.navigateTo(Screen.CurrentModule) }
        )

        Screen.Quiz -> QuizScreen(
            module = uiState.currentModule,
            onExit = { viewModel.navigateTo(Screen.CurrentModule) }
        )

        Screen.Match -> MatchScreen(
            module = uiState.currentModule,
            onExit = { viewModel.navigateTo(Screen.CurrentModule) }
        )

        Screen.Write -> WriteScreen(
            module = uiState.currentModule,
            onExit = { viewModel.navigateTo(Screen.CurrentModule) }
        )
    }
}

@Composable
fun SetEditModule(
    currentModule: Module,
    modules: List<Module>,
    onModuleSaved: (Module) -> Unit,
    onNavigateToMyModules: () -> Unit,
    onNavigateToCurrentModule: () -> Unit,
    onRemoveModule: (Module) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val activeDialog = remember { mutableStateOf<EditModuleDialogState>(EditModuleDialogState.None) }
    var tempLocalModule by remember { mutableStateOf<Module?>(null) }

    val handleExit = {
        if (currentModule.cards.isEmpty()) {
            onRemoveModule(currentModule)
            onNavigateToMyModules()
        } else {
            onNavigateToCurrentModule()
        }
    }

    fun processSave(localModule: Module) {
        val cleanedCards = localModule.cards
            .filter { it.word.isNotBlank() && it.meaning.isNotBlank() }
            .distinctBy { it.word.lowercase() }

        localModule.cards.clear()
        localModule.cards.addAll(cleanedCards)

        if (localModule.cards.size < ModuleConfig.MIN_CARDS_COUNT) {
            val missingCount = ModuleConfig.MIN_CARDS_COUNT - localModule.cards.size
            activeDialog.value = EditModuleDialogState.NotEnoughCards(missingCount)
            return
        }

        val updatedModule = localModule.copy()

        scope.launch {
            val jsonContent = Json.encodeToString(updatedModule)
            ModuleStorageService.saveModule(context, updatedModule.id, jsonContent)
        }

        onModuleSaved(updatedModule)
        onNavigateToCurrentModule()
    }

    EditModuleScreen(
        module = currentModule,
        onOk = { localModule ->
            localModule.trim()
            tempLocalModule = localModule

            if (modules.any { (it.name == localModule.name) && (it != currentModule) }) {
                activeDialog.value = EditModuleDialogState.DuplicateName
            } else {
                val hasInvalidCards = localModule.cards.any {
                    it.word.isBlank() || it.meaning.isBlank()
                } || localModule.cards.distinctBy { it.word.lowercase() }.size != localModule.cards.size

                if (hasInvalidCards) {
                    activeDialog.value = EditModuleDialogState.SaveWithInvalidCards
                } else {
                    processSave(localModule)
                }
            }
        },
        onExit = { localModule ->
            localModule.trim()
            if (localModule != currentModule) {
                activeDialog.value = EditModuleDialogState.ExitConfirm
            } else {
                handleExit()
            }
        }
    )

    EditModuleDialogs(
        dialogState = activeDialog.value,
        onDismiss = { activeDialog.value = EditModuleDialogState.None },
        onConfirmExit = { handleExit() },
        onConfirmSaveInvalid = { tempLocalModule?.let { processSave(it) } }
    )
}

@Composable
fun SetCurrentModule(
    currentModule: Module,
    onNavigate: (Screen) -> Unit,
    onModuleDeleted: (Module) -> Unit,
    onExit: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    CurrentModuleScreen(
        currentModule = currentModule,
        onNavigate = onNavigate,
        onDelete = { module ->
            scope.launch {
                deleteModule(context, module.id)
            }
            onModuleDeleted(module)
            onExit()
        },
        onExit = onExit
    )
}
