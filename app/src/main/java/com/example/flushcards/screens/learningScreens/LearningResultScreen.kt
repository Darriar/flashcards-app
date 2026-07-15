package com.example.flushcards.screens.learningScreens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flushcards.R
import com.example.flushcards.ui.theme.*

@Composable
fun LearningResultScreen(
    correctCount: Int = 18,
    wrongCount: Int = 2,
    onRetry: () -> Unit = {},
    onExit: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.primary
                        )//Color(0xFFF5F7FF), Color(0xFFFFFFFF))
                )
            )
    ) {

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0xFFE8EBFA).copy(alpha = 0.5f),
                radius = 374.dp.toPx(),
                center = center.copy(y = -115.dp.toPx())
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

        Box(
            modifier = Modifier
                .size(145.dp)
                .padding(16.dp)
                .clip(CircleShape)
                .background(color = ButtonGradientStart),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = "OK",
                tint = Color.White,
                modifier = Modifier.size(50.dp)
                )
        }

        Text(
            text = stringResource(R.string.you_passed_module),
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 36.sp,
            textAlign = TextAlign.Center,
            color = Black,
        )

        Text(
            text = stringResource(R.string.great_job),
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 18.dp, bottom = 40.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                count = correctCount,
                label = stringResource(R.string.correct_answers),
                color = CorrectGreen,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                count = wrongCount,
                label = stringResource(R.string.wrong_answers),
                color = WrongRed,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LearnedWordsCard(
            count = correctCount + wrongCount,
            label = stringResource(R.string.total_learned_words)
        )

        Spacer(modifier = Modifier.weight(1f))

        GradientActionButton(
            text = stringResource(R.string.continue_learning),
            onClick = onRetry,
        )

        Spacer(modifier = Modifier.height(16.dp))

        SecondaryActionButton(
            text = stringResource(R.string.enough_for_today),
            onClick = onExit,
        )
        
        Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun StatCard(
    count: Int,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = count.toString(),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun LearnedWordsCard(
    count: Int,
    label: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(LearnedBlueBg, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Icon Placeholder
                Icon(Icons.AutoMirrored.Default.MenuBook, contentDescription = label)
               // Box(modifier = Modifier.size(28.dp).background(color, RoundedCornerShape(4.dp)))
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(
                    text = count.toString(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Black
                )
                Text(
                    text = label,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun GradientActionButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(ButtonGradientStart, ButtonGradientEnd)
                )
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Spacer(modifier = Modifier)

            Text(
                text = text,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            

            Icon(Icons.AutoMirrored.Default.KeyboardArrowRight,
                contentDescription = "exit",
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
fun SecondaryActionButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(color = LearnedBlueBg)
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color(0xFF1E2235),
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LearningResultScreenPreview() {
    FlushCardsTheme {
        LearningResultScreen()
    }
}
