package com.example.flushcards.ui.screens.learningResult.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flushcards.R
import com.example.flushcards.ui.theme.FlushCardsTheme

@Composable
fun DetailedStatCard(
    correctCount: Int,
    wrongCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(96.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                DetailedInfoColumn(
                    count = correctCount,
                    text = stringResource(R.string.correct_answers),
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            VerticalDivider(
                modifier = Modifier
                    .height(48.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                DetailedInfoColumn(
                    count = wrongCount,
                    text = stringResource(R.string.wrong_answers),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun DetailedInfoColumn(
    count: Int,
    text: String,
    color: Color
) {
    Text(
        text = count.toString(),
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = color
    )
    Spacer(modifier = Modifier.height(2.dp))
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        textAlign = TextAlign.Center
    )
}

@Preview(showBackground = true)
@Composable
fun DetailedStatCardPreview() {
    FlushCardsTheme {
        DetailedStatCard(
            correctCount = 4,
            wrongCount = 1,
        )
    }
}

@Preview(showBackground = true, widthDp = 150, heightDp = 100)
@Composable
fun DetailedInfoColumnPreview() {
    FlushCardsTheme {
        Column(
            modifier = Modifier
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            DetailedInfoColumn(
                count = 98,
                text = "text",
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}