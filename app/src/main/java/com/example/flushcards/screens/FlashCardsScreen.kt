package com.example.flushcards.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.flushcards.ui.theme.FlushCardsTheme

@Composable
fun FlashCardsScreen(module: Module) {

    if (module.cards.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "В этом модуле нет карточек",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }
        return
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    val currentCard = module.cards[currentIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 10.dp)
        ) {
            Text(
                text = "Запоминание слов",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Карточка ${currentIndex + 1} из ${module.cards.size}",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .clickable{ isFlipped = !isFlipped},
            colors = CardDefaults.cardColors(
                containerColor = if (isFlipped) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = if (isFlipped) currentCard.meaning else currentCard.word,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isFlipped) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedButton(
                onClick = {
                    currentIndex = if (currentIndex > 0) currentIndex - 1 else module.cards.size - 1
                    isFlipped = false
                },
                modifier = Modifier.weight(1f)
            ) { Text("Назад") }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    currentIndex = if (currentIndex >= module.cards.size - 1)  0 else currentIndex + 1
                    isFlipped = false
                },
                modifier = Modifier.weight(1f)
            ) { Text("Дальше")}
        }
    }

}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    FlushCardsTheme {
        FlashCardsScreen(
            Module("testModule", MutableList<FlashCard>(1) { FlashCard(1, "test", "тестовый") }
            ))
    }
} 