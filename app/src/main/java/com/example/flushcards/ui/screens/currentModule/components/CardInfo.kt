package com.example.flushcards.ui.screens.currentModule.components

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flushcards.data.model.FlashCard
import com.example.flushcards.data.preview.SampleData
import com.example.flushcards.ui.screens.currentModule.CurrentModuleScreen
import com.example.flushcards.ui.theme.FlushCardsTheme
import com.example.flushcards.util.cardHighlightBackgroundColor
import com.example.flushcards.util.cardHighlightBorderColor
import com.example.flushcards.util.convertTextToSpeech
import java.util.Locale

@Composable
fun CardInfo(
    card: FlashCard,
    isHighlighted: Boolean,
    onSpeakClick: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = cardColors(containerColor = cardHighlightBackgroundColor(isHighlighted)),
        border = BorderStroke(2.dp, cardHighlightBorderColor(isHighlighted)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                PronounceButton(card)

                Text(
                    text = card.word,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Spacer(modifier = Modifier.width(24.dp))
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
            )

            Text(
                text = card.meaning,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun PronounceButton(
    card: FlashCard,
    onSpeakClick: (FlashCard) -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = { onSpeakClick(card) },
        modifier = modifier.size(32.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
            contentDescription = "Произнести",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}
@Composable
private fun PronounceButton(card: FlashCard) {
    val tts = if (LocalInspectionMode.current) null else convertTextToSpeech()
    IconButton(
        onClick = {
            tts?.let { engine ->
                engine.language = Locale.UK
                engine.speak(
                    card.word,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "UtteranceId_${card.word}"
                )
            }

            tts?.let { engine ->
                engine.language = Locale.forLanguageTag("ru-RU")
                engine.speak(
                    card.meaning,
                    TextToSpeech.QUEUE_ADD,
                    null,
                    "UtteranceId_${card.meaning}"
                )
            }
        },
        modifier = Modifier.size(32.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
            contentDescription = "Произнести",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CardInfoPreview() {
    FlushCardsTheme {
        Column( verticalArrangement = Arrangement.spacedBy(12.dp) )
        {
            CardInfo(
                SampleData.vocabularyModule.cards.first(),
                true,
                {}
            )

            CardInfo(
                SampleData.vocabularyModule.cards.get(1),
                false,
                {}
            )
        }
    }
}