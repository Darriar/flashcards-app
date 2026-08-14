package com.example.flushcards.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flushcards.data.model.ParsedCard
import com.example.flushcards.ui.theme.FlushCardsTheme
import com.example.flushcards.util.parseTextToCards
import com.example.flushcards.util.readTextFromUri
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


private enum class ImportState {
    INPUT, LOADING, SUCCESS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportModalSheet(onDismiss: () -> Unit, onImport: (List<ParsedCard>) -> Unit) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.32f),
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = MaterialTheme.colorScheme.primary
            )
        }
    ) {
        ImportSheetContent(
            sheetState = sheetState,
            onImport = onImport,
            onDismiss = onDismiss
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImportSheetContent(
    sheetState: SheetState,
    onImport: (List<ParsedCard>) -> Unit,
    onDismiss: () -> Unit
) {
    val showInfoDialog = remember { mutableStateOf(false) }
    var isImporting by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }

    var inputText by remember { mutableStateOf("") }
    val parsedCards by remember(inputText) { derivedStateOf { parseTextToCards(inputText) } }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Box(modifier = Modifier.fillMaxWidth()) {
            AnimatedContent(
                targetState = when {
                    isSuccess -> ImportState.SUCCESS
                    isImporting -> ImportState.LOADING
                    else -> ImportState.INPUT
                },
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(
                        animationSpec = tween(
                            300
                        )
                    )
                }
            ) { state ->
                when (state) {
                    ImportState.INPUT -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ImportHeader(onInfoClick = { showInfoDialog.value = true })

                            FilePickerButton(onTextLoaded = { inputText = it })

                            OrDivider()

                            ManualCardInputField(
                                text = inputText,
                                onTextChange = { inputText = it }
                            )

                            ImportBottomBar(
                                cardsCount = parsedCards.size,
                                onImportClick = {
                                    scope.launch {
                                        isImporting = true
                                        delay(800)
                                        onImport(parsedCards)
                                        isImporting = false
                                        isSuccess = true
                                        delay(1000)
                                        sheetState.hide()
                                        onDismiss()
                                    }
                                }
                            )
                        }
                    }

                    ImportState.LOADING -> {
                        ImportLoadingContent()
                    }

                    ImportState.SUCCESS -> {
                        ImportSuccessContent()
                    }
                }
            }
        }
    }

    if (showInfoDialog.value) {
        ImportInfoAlertDialog(onDismiss = { showInfoDialog.value = false })
    }
}

@Composable
private fun ImportHeader(onInfoClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Импорт карточек",
            style = MaterialTheme.typography.titleLarge
        )

        IconButton(onClick = onInfoClick) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "Формат файла и справка",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun FilePickerButton(onTextLoaded: (String) -> Unit) {
    val context = LocalContext.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onTextLoaded(readTextFromUri(context, it)) }
    }

    OutlinedButton(
        onClick = { filePickerLauncher.launch("text/*") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.FileOpen,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Выбрать файл на устройстве (.txt, .csv)")
    }
}

@Composable
private fun OrDivider() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f))
        Text(
            text = "ИЛИ ВВЕДИТЕ ВРУЧНУЮ",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        HorizontalDivider(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun ManualCardInputField(text: String, onTextChange: (String) -> Unit) {
    OutlinedTextField(
        value = text,
        onValueChange = onTextChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        label = {
            Text(
                text = "Список карточек",
            )
        },
        placeholder = {
            Text(
                text = "Каждая строка — новая карточка:\napple ; яблоко\nbanana ; банан",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )

        },
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
private fun ImportBottomBar(cardsCount: Int, onImportClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (cardsCount != 0) {
                "Готово к импорту: $cardsCount"
            } else {
                "Введите текст или выберите файл"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = if (cardsCount != 0) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline
            },
            modifier = Modifier.weight(1f)
        )

        Button(
            onClick = onImportClick,
            enabled = cardsCount != 0,
            modifier = Modifier.weight(1f)
        ) {
            Text("Импортировать")
        }
    }
}


@Composable
private fun ImportSuccessContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Карточки успешно добавлены!",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ImportLoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ImportInfoAlertDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },

        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.padding(16.dp),

        title = { Text("Формат импорта") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Вы можете загрузить файл (.txt, .csv) или вставить скопированный текст.",
                    style = MaterialTheme.typography.bodyMedium
                )
                HorizontalDivider()
                Text(
                    text = "Правила оформления:",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "• Одна строка = одна карточка\n" +
                            "• Слово и перевод разделяются точкой с запятой (;)",
                    style = MaterialTheme.typography.bodySmall
                )
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Пример:\ncat ; кошка\ndog ; собака\nrun ; бежать",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Понятно")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ImportBottomSheetPreview() {
    FlushCardsTheme {
        ImportSheetContent(
            sheetState = rememberModalBottomSheetState(),
            onImport = {},
            onDismiss = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ImportLoadingContentPreview() {
    FlushCardsTheme {
        ImportLoadingContent()
    }
}

@Preview(showBackground = true)
@Composable
fun ImportSuccessContentPreview() {
    FlushCardsTheme {
        ImportSuccessContent()
    }
}

@Preview(showBackground = true)
@Composable
fun ImportInfoAlertDialogPreview() {
    FlushCardsTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ImportInfoAlertDialog(onDismiss = {})
        }
    }
}