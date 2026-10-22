package com.example.textcount.domain.tokenizer

data class TextWord(
    val text: String,
    val startIndex: Int,
    val endIndex: Int,
    val characterLength: Int
)
