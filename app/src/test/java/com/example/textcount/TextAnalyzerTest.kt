package com.example.textcount

import com.example.textcount.data.model.CharacterCountMode
import com.example.textcount.data.model.WordCountMode
import com.example.textcount.domain.analysis.TextAnalyzer
import com.example.textcount.util.TextCountFormatter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class TextAnalyzerTest {

    private val analyzer = TextAnalyzer()

    @Test
    fun testEmptyTextAnalysis() {
        val result = analyzer.analyze("").getOrThrow()
        assertEquals(0, result.characterCount)
        assertEquals(0, result.characterCountWithoutWhitespace)
        assertEquals(0, result.wordCount)
        assertEquals(0, result.lineCount)
        assertEquals(0, result.spaceCount)
        assertEquals(0, result.newlineCount)
        assertEquals(0, result.whitespaceCount)
        assertEquals(0, result.tabCount)
        assertEquals(0, result.letterCount)
        assertEquals(0, result.digitCount)
        assertEquals(0, result.punctuationCount)
        assertEquals(0, result.symbolCount)
        assertEquals(0, result.paragraphCount)
        assertNull(result.averageWordLength)
        assertNull(result.longestWord)
        assertNull(result.shortestWord)
    }

    @Test
    fun testCriticalTestBasicText() {
        val input = "Hello world!"
        val result = analyzer.analyze(input).getOrThrow()

        assertEquals(12, result.characterCount)
        assertEquals(11, result.characterCountWithoutWhitespace)
        assertEquals(2, result.wordCount)
        assertEquals(1, result.lineCount)
        assertEquals(1, result.spaceCount)
        assertEquals(0, result.newlineCount)
        assertEquals(1, result.whitespaceCount)
        assertEquals(10, result.letterCount)
        assertEquals(0, result.digitCount)
        assertEquals(1, result.punctuationCount)
        assertEquals(0, result.symbolCount)
    }

    @Test
    fun testCriticalTestMultiline() {
        val input = "Hello\nWorld\nAgain"
        val result = analyzer.analyze(input).getOrThrow()

        assertEquals(3, result.wordCount)
        assertEquals(3, result.lineCount)
        assertEquals(0, result.spaceCount)
        assertEquals(2, result.newlineCount)
    }

    @Test
    fun testCriticalTestMultipleSpaces() {
        val input = "Hello   World"
        val result = analyzer.analyze(input).getOrThrow()

        assertEquals(2, result.wordCount)
        assertEquals(3, result.spaceCount)
    }

    @Test
    fun testCriticalTestTabs() {
        val input = "Hello\tWorld"
        val result = analyzer.analyze(input).getOrThrow()

        assertEquals(2, result.wordCount)
        assertEquals(0, result.spaceCount)
        assertEquals(1, result.tabCount)
        assertEquals(1, result.whitespaceCount)
    }

    @Test
    fun testCriticalTestNumbers() {
        val input = "I have 25 apples."
        val result = analyzer.analyze(input).getOrThrow()

        assertEquals(4, result.wordCount)
        assertEquals(2, result.digitCount)
    }

    @Test
    fun testCriticalTestEmoji() {
        val input = "Hello 😀"
        val result = analyzer.analyze(input).getOrThrow()

        assertEquals(7, result.characterCount)
        assertEquals(6, result.characterCountWithoutWhitespace)
        assertEquals(1, result.wordCount)
        assertEquals(1, result.spaceCount)
    }

    @Test
    fun testCriticalTestLongestWord() {
        val input = "The quick brown elephant"
        val result = analyzer.analyze(input).getOrThrow()

        assertEquals("elephant", result.longestWord)
        assertEquals(8, result.longestWordLength)
    }

    @Test
    fun testCriticalTestShortestWord() {
        val input = "The quick brown fox"
        val result = analyzer.analyze(input).getOrThrow()

        // "The" and "fox" both have length 3, returns first occurrence: "The"
        assertEquals("The", result.shortestWord)
        assertEquals(3, result.shortestWordLength)
    }

    @Test
    fun testCriticalTestAverageWordLength() {
        val input = "Hello world"
        val result = analyzer.analyze(input).getOrThrow()

        assertNotNull(result.averageWordLength)
        assertEquals("5", TextCountFormatter.formatAverageWordLength(result.averageWordLength))
    }

    @Test
    fun testNoWordsStats() {
        val input = "!!!"
        val result = analyzer.analyze(input).getOrThrow()

        assertEquals(0, result.wordCount)
        assertNull(result.averageWordLength)
        assertNull(result.longestWord)
        assertNull(result.shortestWord)
        assertEquals("—", TextCountFormatter.formatAverageWordLength(result.averageWordLength))
        assertEquals("—", TextCountFormatter.formatWordDisplay(result.longestWord))
        assertEquals("—", TextCountFormatter.formatWordDisplay(result.shortestWord))
    }

    @Test
    fun testOnlySpaces() {
        val input = "     "
        val result = analyzer.analyze(input).getOrThrow()

        assertEquals(5, result.characterCount)
        assertEquals(0, result.characterCountWithoutWhitespace)
        assertEquals(0, result.wordCount)
        assertEquals(5, result.spaceCount)
    }

    @Test
    fun testOnlyNewlines() {
        val input = "\n\n"
        val result = analyzer.analyze(input).getOrThrow()

        assertEquals(0, result.wordCount)
        assertEquals(0, result.spaceCount)
        assertEquals(2, result.newlineCount)
        assertEquals(2, result.whitespaceCount)
        assertEquals(3, result.lineCount)
    }

    @Test
    fun testSymbolsClassification() {
        val input = "$100 + €50"
        val result = analyzer.analyze(input).getOrThrow()

        // '$', '+', '€' are symbols
        assertEquals(3, result.symbolCount)
        assertEquals(5, result.digitCount)
    }

    @Test
    fun testCopyReportFormatting() {
        val input = "Hello world!\nThis is TextCount."
        val result = analyzer.analyze(input).getOrThrow()
        val report = TextCountFormatter.formatReport(result)

        assertTrue(report.startsWith("TextCount\n"))
        assertTrue(report.contains("Characters: 31"))
        assertTrue(report.contains("Characters without spaces: 27"))
        assertTrue(report.contains("Words: 5"))
        assertTrue(report.contains("Lines: 2"))
        assertTrue(report.contains("Spaces: 3"))
        assertTrue(report.contains("Longest word: TextCount"))
        assertTrue(report.contains("Shortest word: is"))
    }

    @Test
    fun testInputSizeProtection() {
        val shortLimitAnalyzer = TextAnalyzer(maxCharacterLimit = 100)
        val textTooLarge = "a".repeat(101)
        val result = shortLimitAnalyzer.analyze(textTooLarge)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("too large") == true)
    }

    @Test
    fun testLargeTextPerformance() {
        val largeBuilder = StringBuilder(100_000)
        for (i in 0 until 10_000) {
            largeBuilder.append("Word$i ")
        }
        val largeText = largeBuilder.toString()
        val startTime = System.currentTimeMillis()
        val result = analyzer.analyze(largeText).getOrThrow()
        val duration = System.currentTimeMillis() - startTime

        assertEquals(10_000, result.wordCount)
        assertTrue("Analysis of 100k chars took $duration ms", duration < 2000)
    }
}
