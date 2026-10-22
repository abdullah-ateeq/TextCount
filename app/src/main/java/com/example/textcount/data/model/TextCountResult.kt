package com.example.textcount.data.model

import java.math.BigDecimal

data class TextCountResult(
    val characterCount: Int,
    val characterCountWithoutWhitespace: Int,
    val wordCount: Int,
    val lineCount: Int,
    val spaceCount: Int,
    val newlineCount: Int,
    val whitespaceCount: Int,
    val tabCount: Int,
    val letterCount: Int,
    val digitCount: Int,
    val punctuationCount: Int,
    val symbolCount: Int,
    val paragraphCount: Int,
    val averageWordLength: BigDecimal?,
    val longestWord: String?,
    val longestWordLength: Int?,
    val shortestWord: String?,
    val shortestWordLength: Int?,
    val calculatedAt: Long = System.currentTimeMillis()
)
