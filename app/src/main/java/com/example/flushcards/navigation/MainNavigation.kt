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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.flushcards.model.Module
import com.example.flushcards.screens.CurrentModuleScreen
import com.example.flushcards.screens.EditModuleScreen
import com.example.flushcards.screens.learningScreens.FlashCardsScreen
import com.example.flushcards.screens.MyCardsScreen
import com.example.flushcards.screens.learningScreens.QuizScreen
import com.example.flushcards.ui.theme.FlushCardsTheme

@Composable
fun FlipCardsNavigation() {

    var currentScreen by remember {mutableStateOf(Screen.FlipCards)}
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

    Scaffold(
        bottomBar = {
            if ( currentScreen != Screen.EditModule) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentScreen == Screen.FlipCards,
                        onClick = { currentScreen = Screen.FlipCards },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Основное") },
                        label = { Text("Основное") }
                    )

                    NavigationBarItem(
                        selected = currentScreen == Screen.MyCards,
                        onClick = { currentScreen = Screen.MyCards },
                        icon = {
                            Icon(
                                Icons.Default.LocalLibrary,
                                contentDescription = "Мои карточки"
                            )
                        },
                        label = { Text("Карточки") }
                    )
                    NavigationBarItem(
                        selected = currentScreen == Screen.Profile,
                        onClick = { currentScreen = Screen.Profile },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Профиль") },
                        label = { Text("Профиль") }
                    )
                }
            }
        }
    ) {
        innerPadding -> Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.FlipCards -> FlashCardsScreen(currentModule,
                    onExit = { currentScreen = Screen.MyCards })

                Screen.Quiz -> {
                    QuizScreen(currentModule) {currentScreen = Screen.MyCards}
                }

                Screen.MyCards -> MyCardsScreen(modules,
                    onModuleCLick = {module ->
                        currentModule = module
                        currentScreen = Screen.CurrentModule},
                    onAddModule = {newModule ->
                        currentModule = newModule
                        currentScreen = Screen.EditModule
                    })

                Screen.CurrentModule -> CurrentModuleScreen(currentModule,
                    onNavigate = { screen -> currentScreen = screen },
                    setCurrentModule = {module -> currentModule = module},
                    onDelete = { module ->
                        modules.remove(module)
                        // If we deleted the current module, update currentModule to the first one left, or a blank one
                        if (currentModule == module) {
                            currentModule = if (modules.isNotEmpty()) modules[0] else Module("", mutableListOf())
                        }
                        currentScreen = Screen.MyCards
                    })

                Screen.Profile -> {}

                Screen.EditModule -> EditModuleScreen(currentModule) { currentScreen = Screen.MyCards }
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