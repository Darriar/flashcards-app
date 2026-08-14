package com.example.flushcards.util

import android.content.Context
import android.net.Uri
import com.example.flushcards.data.constants.ModuleConfig
import com.example.flushcards.data.model.ParsedCard

fun parseTextToCards(
    text: String,
    separator: String = ModuleConfig.IMPORT_TEXT_SEPARATOR
): List<ParsedCard> {

    return text
        .lineSequence()
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .mapNotNull { line ->
            val parts = line.split(separator, limit = 2)

            if (parts.size == 2) {
                val word = parts[0].trim()
                val meaning = parts[1].trim()

                if (word.isNotEmpty() && meaning.isNotEmpty()) {
                    ParsedCard(word, meaning)
                } else null
            } else null
        }.toList()
}

fun readTextFromUri(context: Context, uri: Uri): String {
    return try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            inputStream.bufferedReader(Charsets.UTF_8).use { reader ->
                reader.readText()
            }
        } ?: ""
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}