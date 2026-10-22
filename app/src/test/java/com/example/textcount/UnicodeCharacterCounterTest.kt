package com.example.textcount

import com.example.textcount.data.model.CharacterCountMode
import com.example.textcount.domain.analysis.UnicodeCharacterCounter
import org.junit.Assert.assertEquals
import org.junit.Test

class UnicodeCharacterCounterTest {

    @Test
    fun testEmptyText() {
        assertEquals(0, UnicodeCharacterCounter.countCharacters("", CharacterCountMode.USER_PERCEIVED))
        assertEquals(0, UnicodeCharacterCounter.countCharactersWithoutWhitespace("", CharacterCountMode.USER_PERCEIVED))
        assertEquals(0, UnicodeCharacterCounter.countCharacters("", CharacterCountMode.CODE_POINT))
        assertEquals(0, UnicodeCharacterCounter.countCharactersWithoutWhitespace("", CharacterCountMode.CODE_POINT))
    }

    @Test
    fun testSingleCharacter() {
        assertEquals(1, UnicodeCharacterCounter.countCharacters("a", CharacterCountMode.USER_PERCEIVED))
        assertEquals(1, UnicodeCharacterCounter.countCharactersWithoutWhitespace("a", CharacterCountMode.USER_PERCEIVED))
    }

    @Test
    fun testAsciiText() {
        val text = "Hello"
        assertEquals(5, UnicodeCharacterCounter.countCharacters(text, CharacterCountMode.USER_PERCEIVED))
        assertEquals(5, UnicodeCharacterCounter.countCharactersWithoutWhitespace(text, CharacterCountMode.USER_PERCEIVED))
    }

    @Test
    fun testUnicodeAndAccents() {
        val text = "Café"
        assertEquals(4, UnicodeCharacterCounter.countCharacters(text, CharacterCountMode.USER_PERCEIVED))
        assertEquals(4, UnicodeCharacterCounter.countCharactersWithoutWhitespace(text, CharacterCountMode.USER_PERCEIVED))
    }

    @Test
    fun testEmoji() {
        // Single emoji (surrogate pair)
        val emoji = "😀"
        assertEquals(1, UnicodeCharacterCounter.countCharacters(emoji, CharacterCountMode.USER_PERCEIVED))
        assertEquals(1, UnicodeCharacterCounter.countCharactersWithoutWhitespace(emoji, CharacterCountMode.USER_PERCEIVED))

        // Text with emoji and space
        val text = "Hello 😀"
        assertEquals(7, UnicodeCharacterCounter.countCharacters(text, CharacterCountMode.USER_PERCEIVED))
        assertEquals(6, UnicodeCharacterCounter.countCharactersWithoutWhitespace(text, CharacterCountMode.USER_PERCEIVED))
    }

    @Test
    fun testCombiningUnicodeCharacters() {
        // 'e' with combining acute accent '\u0301'
        val combining = "e\u0301"
        assertEquals(1, UnicodeCharacterCounter.countCharacters(combining, CharacterCountMode.USER_PERCEIVED))
        assertEquals(1, UnicodeCharacterCounter.countCharactersWithoutWhitespace(combining, CharacterCountMode.USER_PERCEIVED))
    }

    @Test
    fun testSurrogatePairs() {
        // Mathematical bold capital A: U+1D400 (encoded as 2 UTF-16 chars)
        val mathChar = "\uD835\uDC00"
        assertEquals(1, UnicodeCharacterCounter.countCharacters(mathChar, CharacterCountMode.USER_PERCEIVED))
        assertEquals(1, UnicodeCharacterCounter.countCharacters(mathChar, CharacterCountMode.CODE_POINT))
    }

    @Test
    fun testCharacterCountWithoutWhitespace() {
        val text = "Hello World"
        assertEquals(11, UnicodeCharacterCounter.countCharacters(text, CharacterCountMode.USER_PERCEIVED))
        assertEquals(10, UnicodeCharacterCounter.countCharactersWithoutWhitespace(text, CharacterCountMode.USER_PERCEIVED))

        val textWithSpaces = "Hello   World"
        assertEquals(13, UnicodeCharacterCounter.countCharacters(textWithSpaces, CharacterCountMode.USER_PERCEIVED))
        assertEquals(10, UnicodeCharacterCounter.countCharactersWithoutWhitespace(textWithSpaces, CharacterCountMode.USER_PERCEIVED))

        val onlySpaces = "     "
        assertEquals(5, UnicodeCharacterCounter.countCharacters(onlySpaces, CharacterCountMode.USER_PERCEIVED))
        assertEquals(0, UnicodeCharacterCounter.countCharactersWithoutWhitespace(onlySpaces, CharacterCountMode.USER_PERCEIVED))
    }

    @Test
    fun testCodePointCounting() {
        val text = "Hello 😀"
        // 5 letters + 1 space + 1 code point for 😀 = 7 code points
        assertEquals(7, UnicodeCharacterCounter.countCharacters(text, CharacterCountMode.CODE_POINT))
        assertEquals(6, UnicodeCharacterCounter.countCharactersWithoutWhitespace(text, CharacterCountMode.CODE_POINT))
    }
}
