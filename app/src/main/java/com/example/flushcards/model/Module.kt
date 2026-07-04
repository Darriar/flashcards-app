package com.example.flushcards.model

data class Module(
    var name: String,
    var cards: MutableList<FlashCard>
)
