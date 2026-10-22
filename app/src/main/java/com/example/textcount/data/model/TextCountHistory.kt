package com.example.textcount.data.model

data class TextCountHistory(
    val id: String,
    val textPreview: String,
    val text: String,
    val savedAt: Long
)
