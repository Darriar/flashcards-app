package com.example.flushcards.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flushcards.R
import com.example.flushcards.model.FlashCard
import com.example.flushcards.model.Module
import com.example.flushcards.model.Screen

@Composable
fun CurrentModuleScreen(
    currentModule: Module,
    onNavigate: (Screen) -> Unit,
    onDelete: (Module) -> Unit,
    onExit: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 32.dp)
            .verticalScroll(rememberScrollState())
    ) {

        CurrentScreenHeader(currentModule, onNavigate, onDelete, onExit)


        Text(
            text = "В этом модуле карточек: ${currentModule.cards.size}",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 24.dp, top = 12.dp)
        )

        Text(
            text = "Режимы обучения",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
        )

        ModeCard(
            title = "Флеш-карточки",
            description = "Повторяйте термины и проверяйте себя в классическом режиме",
            iconRes = R.drawable.ic_back, // Замените на иконку карточек (например, слои или блокнот)
            onClick = { onNavigate(Screen.FlipCards) }
        )

        ModeCard(
            title = "Тест",
            description = "Выбирайте правильное значение из нескольких вариантов",
            iconRes = R.drawable.ic_back, // Замените на иконку теста (например, галочка с пунктами)
            onClick = { onNavigate(Screen.Quiz) }
        )

        ModeCard(
            title = "Мэтчинг",
            description = "Соединяйте слова и их значения",
            iconRes = R.drawable.ic_back, // Замените на иконку пазла или стрелочек соединения
            onClick = { onNavigate(Screen.Match) }
        )
    }
}

@Composable
fun ModeCard(
    title: String,
    description: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
@Composable
fun CurrentScreenHeader(
    module: Module,
    onNavigate: (Screen) -> Unit,
    onDelete: (Module) -> Unit,
    onBack: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Row (
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Back",
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .clickable { onBack() },
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = module.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 40.dp)
            )

            Menu(module, onNavigate, onDelete)
        }
    }
}

@Composable
fun Menu(module: Module, onNavigate: (Screen) -> Unit, onDelete: (Module) -> Unit,) {
    var menuExpanded by remember { mutableStateOf(false) }
    var subMenuExpanded by remember { mutableStateOf(false) }

    Box() {
        IconButton(onClick = { menuExpanded = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Показать меню",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Box {
            val isTermFirst = module.isTermFirst
            IconButton(onClick = { menuExpanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Настройки модуля")
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = {
                    menuExpanded = false
                    subMenuExpanded = false
                },
                shape = RoundedCornerShape(16.dp)
            ) {
                DropdownMenuItem(
                    text = {
                        Text(
                            "Повторить все сначала",
                            fontWeight = FontWeight.Medium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        menuExpanded = false
                        module.resetProgress()
                    }
                )

                Box {
                    DropdownMenuItem(
                        text = { Text("Режим карточек", fontWeight = FontWeight.Medium) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Style,
                                contentDescription = null
                            )
                        },
                        trailingIcon = {
                            Icon(
                                Icons.Default.ArrowRight,
                                contentDescription = null
                            )
                        },
                        onClick = { subMenuExpanded = !subMenuExpanded }
                    )

                    DropdownMenu(
                        expanded = subMenuExpanded,
                        onDismissRequest = { subMenuExpanded = false },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Термин ➔ Определение",
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = if (isTermFirst) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                leadingIconColor = if (isTermFirst) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.background(
                                if (isTermFirst) MaterialTheme.colorScheme.primaryContainer.copy(
                                    alpha = 0.4f
                                ) else MaterialTheme.colorScheme.surface
                            ),
                            onClick = {
                                subMenuExpanded = false
                                menuExpanded = false
                                module.showTermFirst()
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Определение ➔ Термин",
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = if (!isTermFirst) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                leadingIconColor = if (!isTermFirst) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.background(
                                if (!isTermFirst) MaterialTheme.colorScheme.primaryContainer.copy(
                                    alpha = 0.4f
                                ) else MaterialTheme.colorScheme.surface
                            ),
                            onClick = {
                                subMenuExpanded = false
                                menuExpanded = false
                                module.showMeaningFirst()
                            }
                        )
                    }
                }

                DropdownMenuItem(
                    text = { Text("Редактировать", fontWeight = FontWeight.Medium) },
                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                    onClick = {
                        menuExpanded = false
                        onNavigate(Screen.EditModule)
                    }
                )

                DropdownMenuItem(
                    text = { Text("Удалить", fontWeight = FontWeight.Medium) },
                    leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) },
                    colors = MenuDefaults.itemColors(
                        textColor = MaterialTheme.colorScheme.error,
                        leadingIconColor = MaterialTheme.colorScheme.error
                    ),
                    onClick = {
                        menuExpanded = false
                        onNavigate(Screen.MyModules)
                        onDelete(module)
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
    var currentModule = Module("textModule", cards, true)
    MaterialTheme() {
        CurrentModuleScreen(currentModule, onNavigate = {}, onDelete = {}, onExit = {})
    }
}