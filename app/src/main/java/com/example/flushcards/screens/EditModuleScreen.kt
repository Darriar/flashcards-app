package com.example.flushcards.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flushcards.model.FlashCard
import com.example.flushcards.model.Module

@Composable
fun EditModuleScreen(module: Module, onOk: () -> Unit) {

    val localCards = remember { mutableStateListOf<FlashCard>().apply{addAll(module.cards)} }
    var moduleName by remember { mutableStateOf(module.name) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = moduleName,
                onValueChange = { 
                    moduleName = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                textStyle = TextStyle(
                    fontSize = 24.sp
                )
            )
            LazyColumn() {
                itemsIndexed(localCards) { index, card ->
                    CreateCard(card,
                        onWordChange = { newWord ->
                            localCards[index] = card.copy(word = newWord)
                        },
                        onMeaningChange = { newMeaning ->
                            localCards[index] = card.copy(meaning = newMeaning)
                        })
                }
            }
        }

        Button(
            onClick = {
                module.cards.clear()
                module.cards.addAll(localCards
                    .filter { it.word.isNotBlank() && it.meaning.isNotBlank() }
                    .map { it.copy(word = it.word.trim(), meaning = it.meaning.trim()) }
                    .distinctBy { it.word.lowercase() })
                module.name = moduleName
                onOk()
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .height(48.dp)
                .width(150.dp)

        ) {
            Text(
                textAlign = TextAlign.Center,
                text = "Готово",
                fontSize = 18.sp
            )
        }

        FloatingActionButton(onClick = {
            val newCard = FlashCard(0, "", "")
            localCards.add(newCard)
        },
            modifier = Modifier
                .padding( bottom = 70.dp, end = 30.dp)
                .size(64.dp)
                .align(Alignment.BottomEnd))
        {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Добавить карточку",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Composable
fun CreateCard(card: FlashCard, onWordChange: (String) -> Unit, onMeaningChange: (String) -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            value = card.word,
            onValueChange =  onWordChange,
            label = { Text(
                    text = "Термин",
                    fontSize = 12.sp
                )
            },
        )

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            value = card.meaning,
            onValueChange = onMeaningChange,
            label = {
                Text(
                    text = "Значение",
                    fontSize = 12.sp
                )
            },
        )
    }

    Spacer(modifier = Modifier.height(8.dp))
}

@Preview(showBackground = true)
@Composable
fun EditModulePreview() {
    EditModuleScreen(Module("English", mutableListOf(FlashCard(1, "test", "тестовый")))
    ) {}
}