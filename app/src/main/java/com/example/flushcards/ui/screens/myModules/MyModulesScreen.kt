package com.example.flushcards.ui.screens.myModules

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.ModeNight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flushcards.data.model.Module
import com.example.flushcards.data.preview.SampleData
import com.example.flushcards.ui.screens.myModules.components.AddNewModuleButton
import com.example.flushcards.ui.screens.myModules.components.ModulesColumn
import com.example.flushcards.ui.theme.FlushCardsTheme
import com.example.flushcards.ui.theme.ThemeManager

@Composable
fun MyModulesScreen(
    modules: MutableList<Module>,
    onModuleCLick: (module: Module) -> Unit,
    onAddModule: () -> Unit,
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

@Preview(showBackground = true)
@Composable
fun MyCardsPreview() {
    FlushCardsTheme {
        MyModulesScreen(
            modules = mutableListOf(
                SampleData.vocabularyModule,
                SampleData.spanishModule,
                SampleData.verbsModule
            ),
            onModuleCLick = {},
            onAddModule = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MyCardsEmptyPreview() {
    FlushCardsTheme {
        MyModulesScreen(
            modules = mutableListOf(),
            onModuleCLick = {},
            onAddModule = {}
        )
    }
}