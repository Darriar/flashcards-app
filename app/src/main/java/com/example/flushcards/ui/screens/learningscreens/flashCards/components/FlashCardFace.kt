package com.example.flushcards.ui.screens.learningscreens.flashCards.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flushcards.R
import com.example.flushcards.data.model.FlashCard
import com.example.flushcards.data.preview.SampleData
import com.example.flushcards.ui.theme.FlushCardsTheme

@Composable
fun FlashCardFace(
    card: FlashCard,
    isTermFirst: Boolean,
    rotationValue: Float,
    density: Density,
    modifier: Modifier = Modifier
) {
    val isFlipped = rotationValue >= 90f

    Card(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(28.dp))
            .graphicsLayer {
                rotationY = rotationValue
                cameraDistance = 12f * density.density
            },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (!isFlipped) card.getFront(isTermFirst) else card.getBack(isTermFirst),
                fontSize = 25.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 16.dp)
                    .graphicsLayer { rotationY = if (isFlipped) 180f else 0f }
            )

            Text(
                text = stringResource(id = R.string.tap_to_flip),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                letterSpacing = 1.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .graphicsLayer { rotationY = if (isFlipped) 180f else 0f }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FlashCardFacePreview() {
    FlushCardsTheme {
        Column (
            modifier = Modifier
                .width(350.dp)
                .height(700.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FlashCardFace(
                card = SampleData.englishVocabularyCards[0],
                isTermFirst = true,
                rotationValue = 0f,
                density = LocalDensity.current,
                modifier = Modifier.weight(1f)
            )

            FlashCardFace(
                card = SampleData.englishVocabularyCards[0],
                isTermFirst = true,
                rotationValue = 180f,
                density = LocalDensity.current,
                modifier = Modifier.weight(1f)
            )
        }
    }
}