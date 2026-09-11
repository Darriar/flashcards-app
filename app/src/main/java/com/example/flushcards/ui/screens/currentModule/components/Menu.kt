package com.example.flushcards.ui.screens.currentModule.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flushcards.ui.theme.FlushCardsTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun ModuleOptionsMenu(
    isTermFirst: Boolean,
    onEditClick: () -> Unit,
    onResetClick: () -> Unit,
    onModeSelect: (isTermFirst: Boolean) -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {

        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "Настройки модуля",
            modifier = Modifier
                .clip(CircleShape)
                .size(28.dp)
                .clickable(onClick = { menuExpanded = true })
        )

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier.border(
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shape = RoundedCornerShape(16.dp)
            )
        ) {
            DropdownMenuItem(
                text = { Text("Редактировать", fontWeight = FontWeight.Medium) },
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                colors = MenuDefaults.itemColors(
                    textColor = MaterialTheme.colorScheme.onSurface,
                    leadingIconColor = MaterialTheme.colorScheme.primary
                ),
                onClick = {
                    menuExpanded = false
                    onEditClick()
                }
            )

            DropdownMenuItem(
                text = { Text("Повторить все сначала", fontWeight = FontWeight.Medium) },
                leadingIcon = { Icon(Icons.Default.Refresh, contentDescription = null) },
                colors = MenuDefaults.itemColors(
                    textColor = MaterialTheme.colorScheme.onSurface,
                    leadingIconColor = MaterialTheme.colorScheme.primary
                ),
                onClick = {
                    menuExpanded = false
                    onResetClick()
                }
            )

            CardModeSubMenuItem(
                isTermFirst = isTermFirst,
                onModeSelect = onModeSelect,
                onCloseMainMenu = { menuExpanded = false }
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
                    onDeleteClick()
                }
            )
        }
    }
}

@Composable
private fun CardModeSubMenuItem(
    isTermFirst: Boolean,
    onModeSelect: (isTermFirst: Boolean) -> Unit,
    onCloseMainMenu: () -> Unit
) {
    var subMenuExpanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun selectMode(isTermFirst: Boolean) {
        onModeSelect(isTermFirst)
        scope.launch {
            delay(300.milliseconds)
            subMenuExpanded = false
            onCloseMainMenu()
        }
    }

    Box {
        DropdownMenuItem(
            text = { Text("Режим карточек", fontWeight = FontWeight.Medium) },
            leadingIcon = { Icon(Icons.Default.Style, contentDescription = null) },
            trailingIcon = {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowRight,
                    contentDescription = null
                )
            },
            colors = MenuDefaults.itemColors(
                textColor = MaterialTheme.colorScheme.onSurface,
                leadingIconColor = MaterialTheme.colorScheme.primary,
                trailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            onClick = { subMenuExpanded = !subMenuExpanded }
        )

        DropdownMenu(
            expanded = subMenuExpanded,
            onDismissRequest = { subMenuExpanded = false },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.border(
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shape = RoundedCornerShape(16.dp)
            )
        ) {
            CardModeOptionItem(
                text = "Термин ➔ Определение",
                isSelected = isTermFirst,
                onClick = { selectMode(isTermFirst = true) }
            )

            CardModeOptionItem(
                text = "Определение ➔ Термин",
                isSelected = !isTermFirst,
                onClick = { selectMode(isTermFirst = false) }
            )
        }
    }
}
@Composable
fun CardModeOptionItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Text(
                text = text,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        },
        leadingIcon = {
            if (isSelected) {
                Icon(Icons.Default.Check, contentDescription = null)
            }
        },
        colors = MenuDefaults.itemColors(
            textColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            leadingIconColor = MaterialTheme.colorScheme.primary
        ),
        onClick = onClick
    )
}

@Preview(showBackground = true)
@Composable
private fun CardModeOptionItemSelectedPreview() {
    FlushCardsTheme {
        Column( verticalArrangement = Arrangement.spacedBy(12.dp) )
        {
            CardModeOptionItem(
                text = "Термин ➔ Определение",
                isSelected = true,
                onClick = {}
            )

            CardModeOptionItem(
                text = "Определение ➔ Термин",
                isSelected = false,
                onClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CardModeSubMenuItemPreview() {
    FlushCardsTheme {
        CardModeSubMenuItem(
            isTermFirst = true,
            onModeSelect = {},
            onCloseMainMenu = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ModuleOptionsMenuPreview() {
    FlushCardsTheme {
        ModuleOptionsMenu(
            isTermFirst = true,
            onEditClick = {},
            onResetClick = {},
            onModeSelect = {},
            onDeleteClick = {}
        )
    }
}