package com.example.flushcards.navigation

import EditModuleScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.example.flushcards.data.ModuleStorageService
import com.example.flushcards.data.ModuleStorageService.deleteModule
import com.example.flushcards.model.FlashCard
import com.example.flushcards.model.Module
import com.example.flushcards.model.ModuleConfig
import com.example.flushcards.model.Screen
import com.example.flushcards.screens.CurrentModuleScreen
import com.example.flushcards.screens.MyModulesScreen
import com.example.flushcards.screens.learningScreens.FlashCardsScreen
import com.example.flushcards.screens.learningScreens.MatchScreen
import com.example.flushcards.screens.learningScreens.QuizScreen
import com.example.flushcards.screens.learningScreens.WriteScreen
import com.example.flushcards.ui.theme.FlushCardsTheme
import com.example.flushcards.ui.theme.components.NotificationCard
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun FlipCardsNavigation() {

    var currentScreen by remember { mutableStateOf(Screen.MyModules) }
    val cards = remember {
        mutableStateListOf(
            FlashCard(1, "assess", "оценивать"),
            FlashCard(2, "overrated", "переоцененный"),
            FlashCard(3, "eternal", "вечный, неизменный"),
            FlashCard(4, "invading", "вторжение")
        )
    }
    val modules = remember { mutableStateListOf(Module(1,"English words", cards)) }
    var currentModule by remember {
        mutableStateOf(
            if (modules.isNotEmpty()) modules[0] else Module(1,
                "",
                mutableListOf()
            )
        )
    }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val loadedStrings = ModuleStorageService.loadModule(context)
        loadedStrings.forEach { string ->
            val module = Json.decodeFromString<Module>(string)
            modules.add(module)
        }
    }

    when (currentScreen) {

        Screen.MyModules -> MyModulesScreen(
            modules,
            onModuleCLick = { module ->
                currentModule = module
                currentScreen = Screen.CurrentModule
            },
            onAddModule = { newModule ->
                currentModule = newModule
                currentScreen = Screen.EditModule
            })

        Screen.FlipCards -> FlashCardsScreen(
            currentModule,
            onExit = { currentScreen = Screen.CurrentModule })

        Screen.Quiz -> {
            QuizScreen(currentModule) { currentScreen = Screen.CurrentModule }
        }

        Screen.Match -> {
            MatchScreen(currentModule) { currentScreen = Screen.CurrentModule }
        }

        Screen.Write -> {
            WriteScreen(currentModule) { currentScreen = Screen.CurrentModule }
        }

        Screen.CurrentModule -> CurrentModuleScreen(
            currentModule,
            onNavigate = { screen -> currentScreen = screen },
            onDelete = { module ->

                scope.launch {
                    val isSucceed = deleteModule(context, module.id)
                }
                modules.remove(module)
                if (currentModule == module) {
                    currentModule =
                        if (modules.isNotEmpty()) modules[0] else Module(1,"", mutableListOf())
                }
                currentScreen = Screen.MyModules
            },
            onExit = { currentScreen = Screen.MyModules })

        Screen.EditModule -> {
            var showExitDialog by remember { mutableStateOf(false) }
            var showOkDialog by remember { mutableStateOf(false) }
            var showSameNameDialog by remember { mutableStateOf(false) }
            var showNotEnoughCards by remember { mutableStateOf(false) }

            var missingCardsCount by remember { mutableStateOf(0) }
            var tempLocalModule by remember { mutableStateOf<Module?>(null) }

            val onExit = {
                if (currentModule.cards.isEmpty()) {
                    modules.remove(currentModule)
                    currentScreen = Screen.MyModules
                } else {
                    currentScreen = Screen.CurrentModule
                }
            }

            fun processSave(localModule: Module) {
                val cleanedCards = localModule.cards
                    .filter { it.word.isNotBlank() && it.meaning.isNotBlank() }
                    .distinctBy { it.word.lowercase() }

                localModule.cards.clear()
                localModule.cards.addAll(cleanedCards)

                if (localModule.cards.size < ModuleConfig.MIN_CARDS_COUNT) {
                    missingCardsCount = ModuleConfig.MIN_CARDS_COUNT - localModule.cards.size
                    showNotEnoughCards = true
                    return
                }

                currentModule = localModule.copy()

                val index = modules.indexOfFirst { it.id == currentModule.id }
                if (index == -1) modules.add(currentModule) else modules[index] = currentModule

                scope.launch {
                    val jsonContent = Json.encodeToString(currentModule)
                    ModuleStorageService.saveModule(context, currentModule.id, jsonContent)
                }

                currentScreen = Screen.CurrentModule
            }

            EditModuleScreen(
                currentModule,
                onOk = { localModule ->
                    localModule.trim()
                    tempLocalModule = localModule

                    if (modules.any {(it.name == localModule.name) && (it != currentModule)}) {
                        showSameNameDialog = true
                    } else {
                        val hasInvalidCards = localModule.cards.any {
                            it.word.isBlank() || it.meaning.isBlank() } ||
                                localModule.cards.distinctBy { it.word.lowercase() }.size != localModule.cards.size

                        if (hasInvalidCards) {
                            showOkDialog = true
                        } else {
                            processSave(localModule)
                        }
                    }



                },
                onExit = { localModule ->
                    localModule.trim()
                    if (localModule != currentModule) {
                        showExitDialog = true
                    } else {
                        onExit()
                    }
                })

            if (showExitDialog) {
                Dialog(onDismissRequest = {}) {
                    NotificationCard(
                        title = "У вас есть несохраненные изменения. Вы уверены, что хотите выйти?",
                        agreeText = "Выйти без сохранения",
                        disagreeText = "Отмена",
                        onAgree = {
                            onExit()
                            showExitDialog = false
                        },
                        onDisagree = { showExitDialog = false }
                    )
                }


            }

            if (showOkDialog) {
                Dialog(onDismissRequest = {}) {
                    NotificationCard(
                        title = "При сохранении карточки с пустыми полями или с одинаковыми терминами будут удалены, вы уверены?",
                        agreeText = "Сохранить изменения",
                        disagreeText = "Отмена",
                        onAgree = {
                            tempLocalModule?.let { processSave(it) }
                            showOkDialog = false
                        },
                        onDisagree = { showOkDialog = false }
                    )
                }
            }

            if (showSameNameDialog) {
                Dialog(onDismissRequest = {}) {
                    NotificationCard(
                        title = "Модуль с таким названием уже существует. Переименуйте модуль",
                        agreeText = "Хорошо",
                        disagreeText = null,
                        onAgree = { showSameNameDialog = false },
                        onDisagree = {  }
                    )
                }
            }

            if (showNotEnoughCards) {
                Dialog(onDismissRequest = {}) {
                    NotificationCard(
                        title = "Недостаточно карточек, добавьте еще ${missingCardsCount}",
                        agreeText = "Хорошо",
                        disagreeText = null,
                        onAgree = {  showNotEnoughCards = false },
                        onDisagree = {  }
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FlushCardsTheme {
        FlipCardsNavigation()
    }
}