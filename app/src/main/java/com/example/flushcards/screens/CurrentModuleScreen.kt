package com.example.flushcards.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flushcards.model.FlashCard
import com.example.flushcards.model.Module
import com.example.flushcards.model.Screen

@Composable
fun CurrentModuleScreen(currentModule: Module, onNavigate: (Screen) -> Unit, setCurrentModule: (Module) -> Unit, onDelete: (Module) -> Unit) {

    var menuExpanded by remember { mutableStateOf(false) }

    Box() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = currentModule.name,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Button(
                onClick = {
                    onNavigate(Screen.Main)
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Изучать")
            }

        }

        Box(
            modifier = Modifier
                .align(alignment = Alignment.TopEnd)
                .padding(top = 20.dp, end = 16.dp)
        ) {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Показать меню"
                )
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false}
            ) {
                DropdownMenuItem(
                    text = { Text("Редактировать") },
                    onClick = {
                        menuExpanded = false
                        onNavigate(Screen.EditModule)
                    }
                )

                DropdownMenuItem(
                    text = {Text("Удалить")},
                    onClick = {
                        menuExpanded = false
                        onNavigate(Screen.MyCards)
                        onDelete(currentModule)
                    }
                )
            }

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
    val modules = remember { mutableStateListOf(Module("English words", cards)) }
    var currentModule = Module("textModule", mutableListOf())
    CurrentModuleScreen(currentModule, onNavigate = {}, setCurrentModule = {}, onDelete = {})
}