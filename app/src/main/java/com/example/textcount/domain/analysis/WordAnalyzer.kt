package com.example.textcount.domain.analysis

import com.example.textcount.data.model.CharacterCountMode
import com.example.textcount.data.model.WordCountMode
import com.example.textcount.domain.tokenizer.TextWord
import com.example.textcount.domain.tokenizer.TextWordTokenizer
import java.math.BigDecimal
import java.math.RoundingMode

object WordAnalyzer {

    data class WordAnalysis(
        val words: List<TextWord>,
        val wordCount: Int,
        val totalWordCharacters: Int,
        val averageWordLength: BigDecimal?,
        val longestWord: String?,
        val longestWordLength: Int?,
        val shortestWord: String?,
        val shortestWordLength: Int?
    )

    fun analyze(
        text: String,
        wordMode: WordCountMode = WordCountMode.SMART_UNICODE,
        characterMode: CharacterCountMode = CharacterCountMode.USER_PERCEIVED,
        scale: Int = 4
    ): WordAnalysis {
        val words = TextWordTokenizer.tokenize(text, wordMode, characterMode)
        if (words.isEmpty()) {
            return WordAnalysis(
                words = emptyList(),
                wordCount = 0,
                totalWordCharacters = 0,
                averageWordLength = null,
                longestWord = null,
                longestWordLength = null,
                shortestWord = null,
                shortestWordLength = null
            )
        }

        var totalChars = 0
        var longestWord: TextWord? = null
        var shortestWord: TextWord? = null

        for (word in words) {
            totalChars += word.characterLength

            if (longestWord == null || word.characterLength > longestWord.characterLength) {
                longestWord = word
            }

            if (shortestWord == null || word.characterLength < shortestWord.characterLength) {
                shortestWord = word
            }
        }

        val averageWordLength = BigDecimal(totalChars).divide(
            BigDecimal(words.size),
            scale,
            RoundingMode.HALF_UP
        )

        return WordAnalysis(
            words = words,
            wordCount = words.size,
            totalWordCharacters = totalChars,
            averageWordLength = averageWordLength,
            longestWord = longestWord?.text,
            longestWordLength = longestWord?.characterLength,
            shortestWord = shortestWord?.text,
            shortestWordLength = shortestWord?.characterLength
        )
    }
}
