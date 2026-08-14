package com.example.flushcards.data.preview

import com.example.flushcards.data.model.FlashCard
import com.example.flushcards.data.model.Module

object SampleData {

    val englishVocabularyCards = listOf(
        FlashCard(
            id = 1,
            word = "Serendipity",
            meaning = "Счастливая случайность, способность делать приятные открытия случайно"
        ),
        FlashCard(
            id = 2,
            word = "Ephemeral",
            meaning = "Мимолетный, недолговечный, быстро исчезающий"
        ),
        FlashCard(
            id = 3,
            word = "Resilience",
            meaning = "Устойчивость к трудностям, способность быстро восстанавливаться"
        ),
        FlashCard(
            id = 4,
            word = "Ubiquitous",
            meaning = "Вездесущий, находящийся повсюду одновременно"
        ),
        FlashCard(
            id = 5,
            word = "Eloquent",
            meaning = "Красноречивый, выразительный и убедительный"
        )
    )

    val englishVerbsCards = listOf(
        FlashCard(id = 1, word = "Go - Went - Gone", meaning = "Идти, ехать"),
        FlashCard(id = 2, word = "Take - Took - Taken", meaning = "Брать, брать с собой"),
        FlashCard(id = 3, word = "Write - Wrote - Written", meaning = "Писать"),
        FlashCard(id = 4, word = "Think - Thought - Thought", meaning = "Думать, мыслить"),
        FlashCard(id = 5, word = "Buy - Bought - Bought", meaning = "Покупать")
    )

    val spanishCards = listOf(
        FlashCard(id = 1, word = "Hola", meaning = "Привет"),
        FlashCard(id = 2, word = "Gracias", meaning = "Спасибо"),
        FlashCard(id = 3, word = "Por favor", meaning = "Пожалуйста"),
        FlashCard(id = 4, word = "Buenos días", meaning = "Доброе утро"),
        FlashCard(id = 5, word = "Hasta luego", meaning = "До свидания")
    )

    val vocabularyModule = Module(
        id = 1,
        name = "english Vocabulary",
        cards = englishVocabularyCards as MutableList<FlashCard>,
        isTermFirst = true
    )

    val verbsModule = Module(
        id = 2,
        name = "Неправильные глаголы",
        cards = englishVerbsCards as MutableList<FlashCard>,
        isTermFirst = true
    )

    val spanishModule = Module(
        id = 3,
        name = "Испанский базовый",
        cards = spanishCards as MutableList<FlashCard>,
        isTermFirst = true
    )


    val sampleModules: List<Module> = listOf(
        vocabularyModule,
        verbsModule,
        spanishModule,
    )
}