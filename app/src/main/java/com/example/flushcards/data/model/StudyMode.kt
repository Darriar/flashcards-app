package com.example.flushcards.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.flushcards.ui.navigation.Screen

data class StudyMode(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val targetScreen: Screen
)

val defaultStudyModes = listOf(
    StudyMode(
        title = "Флеш-карточки",
        description = "Повторяйте термины и проверяйте себя в классическом режиме",
        icon = Icons.Default.Layers,
        targetScreen = Screen.FlipCards
    ),
    StudyMode(
        title = "Тест",
        description = "Выбирайте правильное значение из нескольких вариантов",
        icon = Icons.Default.Quiz,
        targetScreen = Screen.Quiz
    ),
    StudyMode(
        title = "Мэтчинг",
        description = "Соединяйте слова и их значения",
        icon = Icons.Default.Extension,
        targetScreen = Screen.Match
    ),
    StudyMode(
        title = "Письменная практика",
        description = "Вводите точные переводы для максимального запоминания",
        icon = Icons.Default.Keyboard,
        targetScreen = Screen.Write
    )
)