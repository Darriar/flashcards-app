package com.example.flushcards.ui.screens.learningscreens.match.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flushcards.data.preview.SampleData.verbsModule
import com.example.flushcards.ui.components.AppTopBar
import com.example.flushcards.ui.components.LearningCardsProgress
import com.example.flushcards.ui.screens.learningscreens.match.MatchViewModel
import com.example.flushcards.ui.theme.FlushCardsTheme

@Composable
fun MatchHeader(
    viewModel: MatchViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppTopBar(
            title = uiState.moduleName,
            onBack = onBack,
            rightPartContent = {
                LearningCardsProgress(
                    currentCardIndex = uiState.learnedCardsCount,
                    total = uiState.totalCardsCount
                )
            }
        )

        Spacer(modifier = Modifier.height(74.dp))

        Text(
            text = "Соедините термины и их значения",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 16.dp),
            textAlign = TextAlign.Center
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
private fun MatchHeaderPreview() {
    FlushCardsTheme {
        MatchHeader(
            MatchViewModel(
                verbsModule
            ),
            onBack = {}
        )
    }
}