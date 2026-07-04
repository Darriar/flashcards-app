package com.example.flushcards.model

import java.time.LocalDateTime


data class FlashCard (
    val id: Int,
    var word: String,
    var meaning: String,
    var status: ProgressStatus = ProgressStatus.LEARNING,
    var intervalDays: Long = 0,
    var nextReviewTime: LocalDateTime = LocalDateTime.now()
)
