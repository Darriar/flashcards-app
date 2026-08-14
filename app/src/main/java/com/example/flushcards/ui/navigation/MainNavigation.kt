package com.example.flushcards.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.flushcards.data.constants.ModuleConfig
import com.example.flushcards.data.model.FlashCard
import com.example.flushcards.data.model.Module
import com.example.flushcards.data.storage.ModuleStorageService
import com.example.flushcards.data.storage.ModuleStorageService.deleteModule
import com.example.flushcards.ui.components.EditModuleDialogState
import com.example.flushcards.ui.components.EditModuleDialogs
import com.example.flushcards.ui.screens.CurrentModuleScreen
import com.example.flushcards.ui.screens.EditModuleScreen
import com.example.flushcards.ui.screens.MyModulesScreen
import com.example.flushcards.ui.screens.learningscreens.flashCards.FlashCardsScreen
import com.example.flushcards.ui.screens.learningscreens.match.MatchScreen
import com.example.flushcards.ui.screens.learningscreens.QuizScreen
import com.example.flushcards.ui.screens.learningscreens.WriteScreen
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun FlipCardsNavigation() {
    val currentScreen = remember { mutableStateOf(Screen.MyModules) }

    val initialCards = remember {
        mutableStateListOf(
            FlashCard(1, "assess", "оценивать"),
            FlashCard(2, "overrated", "переоцененный"),
            FlashCard(3, "eternal", "вечный, неизменный"),
            FlashCard(4, "invading", "вторжение")
        )
    }
    val modules = remember { mutableStateListOf(Module(1, "English words", initialCards)) }

    val currentModule = remember {
        mutableStateOf(modules.firstOrNull() ?: Module(1, "", mutableListOf()))
    }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val loadedStrings = ModuleStorageService.loadModule(context)
        loadedStrings.forEach { string ->
            val module = Json.decodeFromString<Module>(string)
            modules.add(module)
        }
    }

    when (currentScreen.value) {
        Screen.MyModules -> MyModulesScreen(
            modules = modules,
            onModuleCLick = { module ->
                currentModule.value = module
                currentScreen.value = Screen.CurrentModule
            },
            onAddModule = { newModule ->
                currentModule.value = newModule
                currentScreen.value = Screen.EditModule
            }
        )

        Screen.CurrentModule -> SetCurrentModule(
            currentModule = currentModule.value,
            onNavigate = { screen -> currentScreen.value = screen },
            onModuleDeleted = { deletedModule ->
                modules.remove(deletedModule)
                if (currentModule.value == deletedModule) {
                    currentModule.value = modules.firstOrNull() ?: Module(1, "", mutableListOf())
                }
            },
            onExit = { currentScreen.value = Screen.MyModules }
        )

        Screen.EditModule -> SetEditModule(
            currentModule = currentModule.value,
            modules = modules,
            onModuleSaved = { savedModule ->
                currentModule.value = savedModule
                val index = modules.indexOfFirst { it.id == savedModule.id }
                if (index == -1) modules.add(savedModule) else modules[index] = savedModule
            },
            onNavigateToMyModules = { currentScreen.value = Screen.MyModules },
            onNavigateToCurrentModule = { currentScreen.value = Screen.CurrentModule },
            onRemoveModule = { module -> modules.remove(module) }
        )

        Screen.FlipCards -> FlashCardsScreen(
            module = currentModule.value,
            onExit = { currentScreen.value = Screen.CurrentModule }
        )

        Screen.Quiz -> QuizScreen(
            module = currentModule.value,
            onExit = { currentScreen.value = Screen.CurrentModule }
        )

        Screen.Match -> MatchScreen(
            module = currentModule.value,
            onExit = { currentScreen.value = Screen.CurrentModule }
        )

        Screen.Write -> WriteScreen(
            module = currentModule.value,
            onExit = { currentScreen.value = Screen.CurrentModule }
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
