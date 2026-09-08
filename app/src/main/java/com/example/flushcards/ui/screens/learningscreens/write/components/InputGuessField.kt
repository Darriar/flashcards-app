package com.example.flushcards.ui.screens.learningscreens.write.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.flushcards.util.optionButtonBorderColor
import com.example.flushcards.util.optionButtonColor

@Composable
fun InputGuessField(
    value: String,
    answerState: Boolean?,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit
) {
    val textFieldBorderColor = optionButtonBorderColor(isCorrect = answerState)
    val textFieldContainerColor = optionButtonColor(isCorrect = answerState).containerColor

    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        placeholder = {
            Text(
                text = "Значение...",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = textFieldContainerColor,
            unfocusedContainerColor = textFieldContainerColor,
            focusedBorderColor = textFieldBorderColor,
            unfocusedBorderColor = textFieldBorderColor
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onDone() })
    )
}