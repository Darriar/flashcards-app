package com.example.flushcards.ui.screens.editModule.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceIn
import com.example.flushcards.data.model.FlashCard
import com.example.flushcards.data.preview.SampleData
import com.example.flushcards.ui.theme.FlushCardsTheme
import com.example.flushcards.util.cardHighlightBackgroundColor
import com.example.flushcards.util.cardHighlightBorderColor
import kotlinx.coroutines.launch

@Composable
fun EditCard(
    card: FlashCard,
    isHighlighted: Boolean,
    onWordChange: (String) -> Unit,
    onMeaningChange: (String) -> Unit,
    onDeleteCard: () -> Unit
) {
    val dragProgress = remember { Animatable(0f) }
    val minCardWeight = 0.75f
    val maxDeleteWeight = 1f - minCardWeight
    val scope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .height(intrinsicSize = IntrinsicSize.Min)
            .fillMaxWidth(),

        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .weight(((1f - dragProgress.value)).coerceAtLeast(minCardWeight))
                .clipToBounds()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                val target =
                                    if (dragProgress.value > maxDeleteWeight / 2) 1f else 0f
                                dragProgress.animateTo(
                                    targetValue = target,
                                    animationSpec = tween(durationMillis = 200)
                                )
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            val newProgress = (dragProgress.value - dragAmount / 500f).coerceIn(
                                0f,
                                maxDeleteWeight
                            )
                            scope.launch {
                                dragProgress.snapTo(newProgress)
                            }
                        }
                    )
                },
            colors = cardColors(
                containerColor = cardHighlightBackgroundColor(
                    isHighlighted
                )
            ),
            border = BorderStroke(2.dp, cardHighlightBorderColor(isHighlighted)),
        ) {
            CardContent(
                card,
                onWordChange = onWordChange,
                onMeaningChange = onMeaningChange,
            )
        }

        if (dragProgress.value > 0.001f) {
            DeleteCard(
                modifier = Modifier.weight(
                    dragProgress.value.fastCoerceIn(
                        0.0001f,
                        maxDeleteWeight
                    )
                ),
                onDeleteClick = onDeleteCard
            )
        }
    }
}

@Composable
private fun DeleteCard(
    modifier: Modifier = Modifier,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .clipToBounds(),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.errorContainer),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onDeleteClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Удалить",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun EditCardPreview() {
    FlushCardsTheme {
        Column( verticalArrangement = Arrangement.spacedBy(12.dp) )
        {
            EditCard(
                card = SampleData.vocabularyModule.cards.first(),
                isHighlighted = true,
                onWordChange = {},
                onMeaningChange = {},
                onDeleteCard = {}
            )

            EditCard(
                card = SampleData.vocabularyModule.cards.last(),
                isHighlighted = false,
                onWordChange = {},
                onMeaningChange = {},
                onDeleteCard = {}
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun DeleteCardPreview() {
    FlushCardsTheme {
        Box(modifier = Modifier.size(width = 80.dp, height = 80.dp)) {
            DeleteCard(
                onDeleteClick = {}
            )
        }
    }
}