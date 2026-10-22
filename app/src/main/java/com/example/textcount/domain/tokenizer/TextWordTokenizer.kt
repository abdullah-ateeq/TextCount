package com.example.textcount.domain.tokenizer

import com.example.textcount.data.model.CharacterCountMode
import com.example.textcount.data.model.WordCountMode
import com.example.textcount.domain.analysis.UnicodeCharacterCounter

object TextWordTokenizer {

    private val SMART_WORD_PATTERN = Regex(
        "(https?://[a-zA-Z0-9\\-._~:/?#\\[\\]@!$&'*+,;=%]+[a-zA-Z0-9_~/#])|" +
        "([a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-]+)|" +
        "([\\p{L}\\p{N}]+(?:['’\\-][\\p{L}\\p{N}]+)*)"
    )

    private val WHITESPACE_TOKEN_PATTERN = Regex("\\S+")

    /**
     * Tokenizes [text] into a list of [TextWord]s based on [wordMode] and calculates
     * character length using [characterMode].
     */
    fun tokenize(
        text: String,
        wordMode: WordCountMode = WordCountMode.SMART_UNICODE,
        characterMode: CharacterCountMode = CharacterCountMode.USER_PERCEIVED
    ): List<TextWord> {
        if (text.isEmpty()) return emptyList()

        val pattern = when (wordMode) {
            WordCountMode.SMART_UNICODE -> SMART_WORD_PATTERN
            WordCountMode.WHITESPACE_TOKENS -> WHITESPACE_TOKEN_PATTERN
        }

        val results = mutableListOf<TextWord>()
        val matches = pattern.findAll(text)

        for (match in matches) {
            val wordText = match.value
            val charLen = UnicodeCharacterCounter.countCharacters(wordText, characterMode)
            results.add(
                TextWord(
                    text = wordText,
                    startIndex = match.range.first,
                    endIndex = match.range.last + 1,
                    characterLength = charLen
                )
            )
        }

        return results
    }
}
