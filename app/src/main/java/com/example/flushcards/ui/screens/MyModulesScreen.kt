package com.example.flushcards.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.ModeNight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flushcards.data.model.FlashCard
import com.example.flushcards.data.model.Module
import com.example.flushcards.ui.theme.FlushCardsTheme
import com.example.flushcards.ui.theme.ThemeManager

@Composable
fun MyModulesScreen(
    modules: MutableList<Module>,
    onModuleCLick: (module: Module) -> Unit,
    onAddModule: (Module) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            MyModulesHeader()

            if (modules.isEmpty()) {
                EmptyModulesContent(modifier = Modifier.weight(1f))
            } else {
                ModulesColumn(
                    modules = modules,
                    modifier = Modifier.weight(1f),
                    onModuleCLick = onModuleCLick
                )
            }
        }

        AddNewModuleButton(
            modules,
            onAddModule = onAddModule,
            modifier = Modifier.align(
                Alignment.BottomCenter,
            )
        )
    }
}

@Composable
private fun MyModulesHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 24.dp,
                end = 24.dp,
                top = 32.dp,
                bottom = 16.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Мои модули",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Icon(
            imageVector = if (ThemeManager.isDarkTheme) Icons.Default.ModeNight else Icons.Default.LightMode,
            contentDescription = "Theme",
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .clickable { ThemeManager.changeTheme() },
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EmptyModulesContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Здесь будут ваши карточки.\nСоздайте свой первый модуль!",
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}

@Composable
private fun ModulesColumn(
    modules: MutableList<Module>,
    modifier: Modifier = Modifier,
    onModuleCLick: (module: Module) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(modules) { module ->
            Card(
                onClick = { onModuleCLick(module) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = module.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = when (module.cards.size) {
                            0 -> "Нет карточек"
                            else -> "${module.cards.size} слов(а)"
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(100.dp)) }
    }
}

@Composable
private fun AddNewModuleButton(
    modules: MutableList<Module>,
    modifier: Modifier = Modifier,
    onAddModule: (Module) -> Unit
) {
    Button(
        onClick = {
            val id = if (modules.isEmpty()) 1 else modules.maxOf { it.id } + 1
            val newModule = Module(id, "Новый модуль", mutableListOf())
            onAddModule(newModule)
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp)
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Создать новый модуль",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyCardsPreview() {
    val cards = remember {
        mutableStateListOf(
            FlashCard(1, "assess", "оценивать"),
            FlashCard(2, "overrated", "переоцененный")
        )
    }
    FlushCardsTheme {
        MyModulesScreen(
            modules = remember {
                mutableStateListOf(
                    Module(1, "English Words", cards),
                    Module(1, "Испанский базовый", mutableListOf())
                )
            },
            onModuleCLick = {},
            onAddModule = {}
        )
    }
}