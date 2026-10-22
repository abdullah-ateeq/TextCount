package com.example.textcount.ui

import com.example.textcount.data.model.CharacterCountMode
import com.example.textcount.data.model.TextCountHistory
import com.example.textcount.data.model.TextCountResult
import com.example.textcount.data.model.WordCountMode

data class TextCountUiState(
    val text: String = "",
    val result: TextCountResult? = null,
    val errorMessage: String? = null,
    val isCalculating: Boolean = false,
    val history: List<TextCountHistory> = emptyList(),
    val characterMode: CharacterCountMode = CharacterCountMode.USER_PERCEIVED,
    val wordMode: WordCountMode = WordCountMode.SMART_UNICODE,
    val liveCounting: Boolean = true,
    val hapticFeedback: Boolean = true,
    val animations: Boolean = true,
    val confirmDelete: Boolean = true,
    val restoreLastText: Boolean = false,
    val theme: String = "System",
    val dynamicColor: Boolean = true,
    val averagePrecision: Int = 2,
    val userMessage: String? = null
)
