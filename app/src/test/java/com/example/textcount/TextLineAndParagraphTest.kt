package com.example.textcount

import com.example.textcount.domain.analysis.ParagraphDetector
import com.example.textcount.domain.analysis.TextLineCounter
import org.junit.Assert.assertEquals
import org.junit.Test

class TextLineCounterTest {

    @Test
    fun testEmptyText() {
        val (lines, newlines) = TextLineCounter.countLinesAndNewlines("")
        assertEquals(0, lines)
        assertEquals(0, newlines)
    }

    @Test
    fun testSingleLine() {
        val (lines, newlines) = TextLineCounter.countLinesAndNewlines("Hello")
        assertEquals(1, lines)
        assertEquals(0, newlines)
    }

    @Test
    fun testMultipleLinesLF() {
        val (lines, newlines) = TextLineCounter.countLinesAndNewlines("Hello\nWorld\nAgain")
        assertEquals(3, lines)
        assertEquals(2, newlines)
    }

    @Test
    fun testMultipleLinesCRLF() {
        val (lines, newlines) = TextLineCounter.countLinesAndNewlines("Hello\r\nWorld\r\nAgain")
        assertEquals(3, lines)
        assertEquals(2, newlines)
    }

    @Test
    fun testMultipleLinesCR() {
        val (lines, newlines) = TextLineCounter.countLinesAndNewlines("Hello\rWorld")
        assertEquals(2, lines)
        assertEquals(1, newlines)
    }

    @Test
    fun testTrailingNewline() {
        val (lines, newlines) = TextLineCounter.countLinesAndNewlines("Hello\nWorld\n")
        assertEquals(3, lines)
        assertEquals(2, newlines)
    }

    @Test
    fun testMultipleEmptyLines() {
        // Hello\n\n\nWorld (3 newlines = 4 lines)
        val (lines, newlines) = TextLineCounter.countLinesAndNewlines("Hello\n\n\nWorld")
        assertEquals(4, lines)
        assertEquals(3, newlines)
    }

    @Test
    fun testOnlyNewlines() {
        val (lines, newlines) = TextLineCounter.countLinesAndNewlines("\n\n")
        assertEquals(3, lines)
        assertEquals(2, newlines)
    }
}

class ParagraphDetectorTest {

    @Test
    fun testEmptyText() {
        assertEquals(0, ParagraphDetector.countParagraphs(""))
        assertEquals(0, ParagraphDetector.countParagraphs("   \n\n  \t"))
    }

    @Test
    fun testSingleParagraph() {
        val text = "First paragraph line 1\nFirst paragraph line 2"
        assertEquals(1, ParagraphDetector.countParagraphs(text))
    }

    @Test
    fun testMultipleParagraphs() {
        val text = "Paragraph one.\n\nParagraph two."
        assertEquals(2, ParagraphDetector.countParagraphs(text))

        val textThree = "Paragraph one.\n\nParagraph two.\n\nParagraph three."
        assertEquals(3, ParagraphDetector.countParagraphs(textThree))

        val textWithSpacesBetween = "First paragraph\n\n   \n\nSecond paragraph"
        assertEquals(2, ParagraphDetector.countParagraphs(textWithSpacesBetween))
    }
}
