package com.example.textcount.domain.analysis

import com.example.textcount.data.model.CharacterCountMode
import com.example.textcount.data.model.TextCountResult
import com.example.textcount.data.model.WordCountMode

object TextStatisticsCalculator {

    fun calculate(
        text: String,
        characterMode: CharacterCountMode = CharacterCountMode.USER_PERCEIVED,
        wordMode: WordCountMode = WordCountMode.SMART_UNICODE
    ): TextCountResult {
        if (text.isEmpty()) {
            return TextCountResult(
                characterCount = 0,
                characterCountWithoutWhitespace = 0,
                wordCount = 0,
                lineCount = 0,
                spaceCount = 0,
                newlineCount = 0,
                whitespaceCount = 0,
                tabCount = 0,
                letterCount = 0,
                digitCount = 0,
                punctuationCount = 0,
                symbolCount = 0,
                paragraphCount = 0,
                averageWordLength = null,
                longestWord = null,
                longestWordLength = null,
                shortestWord = null,
                shortestWordLength = null
            )
        }

        val characterCount = UnicodeCharacterCounter.countCharacters(text, characterMode)
        val characterCountWithoutWhitespace = UnicodeCharacterCounter.countCharactersWithoutWhitespace(text, characterMode)

        val (lineCount, newlineCount) = TextLineCounter.countLinesAndNewlines(text)
        val wordAnalysis = WordAnalyzer.analyze(text, wordMode, characterMode)
        val paragraphCount = ParagraphDetector.countParagraphs(text)

        var spaceCount = 0
        var tabCount = 0
        var whitespaceCount = 0
        var letterCount = 0
        var digitCount = 0
        var punctuationCount = 0
        var symbolCount = 0

        var i = 0
        val length = text.length
        while (i < length) {
            val char = text[i]
            if (char == ' ') spaceCount++
            if (char == '\t') tabCount++

            val cp = text.codePointAt(i)
            if (UnicodeCharacterClassifier.isWhitespace(cp)) {
                whitespaceCount++
            } else if (UnicodeCharacterClassifier.isLetter(cp)) {
                letterCount++
            } else if (UnicodeCharacterClassifier.isDigit(cp)) {
                digitCount++
            } else if (UnicodeCharacterClassifier.isPunctuation(cp)) {
                punctuationCount++
            } else if (UnicodeCharacterClassifier.isSymbol(cp)) {
                symbolCount++
            }
            i += Character.charCount(cp)
        }

        return TextCountResult(
            characterCount = characterCount,
            characterCountWithoutWhitespace = characterCountWithoutWhitespace,
            wordCount = wordAnalysis.wordCount,
            lineCount = lineCount,
            spaceCount = spaceCount,
            newlineCount = newlineCount,
            whitespaceCount = whitespaceCount,
            tabCount = tabCount,
            letterCount = letterCount,
            digitCount = digitCount,
            punctuationCount = punctuationCount,
            symbolCount = symbolCount,
            paragraphCount = paragraphCount,
            averageWordLength = wordAnalysis.averageWordLength,
            longestWord = wordAnalysis.longestWord,
            longestWordLength = wordAnalysis.longestWordLength,
            shortestWord = wordAnalysis.shortestWord,
            shortestWordLength = wordAnalysis.shortestWordLength,
            calculatedAt = System.currentTimeMillis()
        )
    }
}
