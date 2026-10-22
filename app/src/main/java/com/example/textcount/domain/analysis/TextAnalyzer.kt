package com.example.textcount.domain.analysis

import com.example.textcount.data.model.CharacterCountMode
import com.example.textcount.data.model.TextCountResult
import com.example.textcount.data.model.WordCountMode

class TextAnalyzer(
    private val maxCharacterLimit: Int = MAX_ANALYSIS_CHARACTERS
) {

    companion object {
        const val MAX_ANALYSIS_CHARACTERS = 1_000_000
    }

    fun analyze(
        text: String,
        characterMode: CharacterCountMode = CharacterCountMode.USER_PERCEIVED,
        wordMode: WordCountMode = WordCountMode.SMART_UNICODE
    ): Result<TextCountResult> {
        if (text.length > maxCharacterLimit) {
            return Result.failure(
                IllegalArgumentException("Text is too large to analyze. Maximum supported length is $maxCharacterLimit characters.")
            )
        }

        return try {
            val result = TextStatisticsCalculator.calculate(
                text = text,
                characterMode = characterMode,
                wordMode = wordMode
            )
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun analyze(text: String): Result<TextCountResult> {
        return analyze(
            text = text,
            characterMode = CharacterCountMode.USER_PERCEIVED,
            wordMode = WordCountMode.SMART_UNICODE
        )
    }
}
