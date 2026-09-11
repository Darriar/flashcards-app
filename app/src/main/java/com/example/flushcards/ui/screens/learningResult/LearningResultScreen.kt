package com.example.flushcards.ui.screens.learningResult

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flushcards.R
import com.example.flushcards.ui.screens.learningResult.components.DetailedStatCard
import com.example.flushcards.ui.screens.learningResult.components.LearnedWordsCard
import com.example.flushcards.ui.screens.learningResult.components.PrimaryActionButton
import com.example.flushcards.ui.screens.learningResult.components.SecondaryActionButton
import com.example.flushcards.ui.theme.FlushCardsTheme

@Composable
fun LearningResultScreen(
    correctCount: Int,
    wrongCount: Int,
    onRetry: () -> Unit,
    onExit: () -> Unit,
) {
    BackHandler { onExit() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.weight(0.4f))

        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(color = MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "OK",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(56.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.you_passed_module),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 34.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Text(
            text = stringResource(R.string.great_job),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp)
        )

        Spacer(modifier = Modifier.weight(0.6f))

        DetailedStatCard(
            correctCount = correctCount,
            wrongCount = wrongCount
        )

        Spacer(modifier = Modifier.height(14.dp))

        LearnedWordsCard(
            count = correctCount + wrongCount,
            label = stringResource(R.string.total_learned_words)
        )

        Spacer(modifier = Modifier.weight(1f))

        PrimaryActionButton(
            text = stringResource(R.string.continue_learning),
            onClick = onRetry,
        )

        Spacer(modifier = Modifier.height(12.dp))

        SecondaryActionButton(
            text = stringResource(R.string.enough_for_today),
            onClick = onExit,
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun LearningResultScreenPreview() {
    FlushCardsTheme {
        LearningResultScreen(
            correctCount = 4,
            wrongCount = 1,
            onExit = {},
            onRetry = {}
        )
    }
}