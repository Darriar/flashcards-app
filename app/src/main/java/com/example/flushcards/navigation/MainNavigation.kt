package com.example.flushcards.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.example.flushcards.model.FlashCard
import com.example.flushcards.model.Screen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.flushcards.data.ModuleStorageService
import com.example.flushcards.data.ModuleStorageService.deleteModule
import com.example.flushcards.model.Module
import com.example.flushcards.screens.CurrentModuleScreen
import com.example.flushcards.screens.EditModuleScreen
import com.example.flushcards.screens.learningScreens.FlashCardsScreen
import com.example.flushcards.screens.MyModulesScreen
import com.example.flushcards.screens.learningScreens.MatchScreen
import com.example.flushcards.screens.learningScreens.QuizScreen
import com.example.flushcards.ui.theme.FlushCardsTheme
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun FlipCardsNavigation() {

    var currentScreen by remember {mutableStateOf(Screen.MyModules)}
    val cards = remember {
        mutableStateListOf(
            FlashCard(1, "assess", "оценивать"),
            FlashCard(2, "overrated", "переоцененный"),
            FlashCard(3, "eternal", "вечный, неизменный"),
            FlashCard(4, "invading", "вторжение")
        )
    }
    val modules = remember { mutableStateListOf(Module("English words", cards)) }
    var currentModule by remember { mutableStateOf(if (modules.isNotEmpty()) modules[0] else Module("", mutableListOf())) }

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

        Screen.MyModules -> MyModulesScreen (modules,
            onModuleCLick = {module ->
                currentModule = module
                currentScreen = Screen.CurrentModule},
            onAddModule = {newModule ->
                currentModule = newModule
                currentScreen = Screen.EditModule
            })

        Screen.FlipCards -> FlashCardsScreen(currentModule,
            onExit = { currentScreen = Screen.CurrentModule })

        Screen.Quiz -> {
            QuizScreen(currentModule) {currentScreen = Screen.CurrentModule }
        }

        Screen.Match -> {
            MatchScreen(currentModule) {currentScreen = Screen.CurrentModule }
        }

        Screen.CurrentModule -> CurrentModuleScreen(currentModule,
            onNavigate = { screen -> currentScreen = screen },
            onDelete = { module ->

                scope.launch {
                    val isSucceed = deleteModule(context, module.name)
                }
                modules.remove(module)
                if (currentModule == module) {
                    currentModule = if (modules.isNotEmpty()) modules[0] else Module("", mutableListOf())
                }
                currentScreen = Screen.MyModules
            },
            onExit = { currentScreen = Screen.MyModules })

        Screen.EditModule -> EditModuleScreen(currentModule,
            onOk = {
                scope.launch {
                    val jsonContent = Json.encodeToString(currentModule)
                    val isSucceed = ModuleStorageService.saveModule(context, currentModule.name, jsonContent)
                }
                currentScreen = Screen.MyModules
            },
            onExit = {
                if (currentModule.cards.isEmpty()) {
                    modules.remove(currentModule)
                    currentScreen = Screen.MyModules
                } else {
                    currentScreen = Screen.CurrentModule
                }
            })

    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FlushCardsTheme {
        FlipCardsNavigation()
    }
}