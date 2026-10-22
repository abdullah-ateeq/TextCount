package com.example.textcount

import com.example.textcount.data.model.CharacterCountMode
import com.example.textcount.data.model.WordCountMode
import com.example.textcount.domain.tokenizer.TextWordTokenizer
import org.junit.Assert.assertEquals
import org.junit.Test

class TextWordTokenizerTest {

    @Test
    fun testWordCountingBasic() {
        val words = TextWordTokenizer.tokenize("Hello world")
        assertEquals(2, words.size)
        assertEquals("Hello", words[0].text)
        assertEquals("world", words[1].text)
    }

    @Test
    fun testWordsWithPunctuation() {
        val words = TextWordTokenizer.tokenize("Hello, world!")
        assertEquals(2, words.size)
        assertEquals("Hello", words[0].text)
        assertEquals("world", words[1].text)
    }

    @Test
    fun testNumbersAsWords() {
        val words = TextWordTokenizer.tokenize("I have 25 apples.")
        assertEquals(4, words.size)
        assertEquals("I", words[0].text)
        assertEquals("have", words[1].text)
        assertEquals("25", words[2].text)
        assertEquals("apples", words[3].text)
    }

    @Test
    fun testMixedLetterNumberTokens() {
        val words = TextWordTokenizer.tokenize("Android 15")
        assertEquals(2, words.size)
        assertEquals("Android", words[0].text)
        assertEquals("15", words[1].text)
    }

    @Test
    fun testContractions() {
        val words = TextWordTokenizer.tokenize("I don't know.")
        assertEquals(3, words.size)
        assertEquals("I", words[0].text)
        assertEquals("don't", words[1].text)
        assertEquals("know", words[2].text)

        val wordsCurly = TextWordTokenizer.tokenize("user’s guide")
        assertEquals(2, wordsCurly.size)
        assertEquals("user’s", wordsCurly[0].text)
        assertEquals("guide", wordsCurly[1].text)
    }

    @Test
    fun testHyphenatedWords() {
        val words = TextWordTokenizer.tokenize("well-known")
        assertEquals(1, words.size)
        assertEquals("well-known", words[0].text)

        val wordsSentence = TextWordTokenizer.tokenize("This is well-known.")
        assertEquals(3, wordsSentence.size)
        assertEquals("well-known", wordsSentence[2].text)
    }

    @Test
    fun testEmailAddresses() {
        val words = TextWordTokenizer.tokenize("Contact test@example.com")
        assertEquals(2, words.size)
        assertEquals("Contact", words[0].text)
        assertEquals("test@example.com", words[1].text)
    }

    @Test
    fun testUrls() {
        val words = TextWordTokenizer.tokenize("Visit https://example.com")
        assertEquals(2, words.size)
        assertEquals("Visit", words[0].text)
        assertEquals("https://example.com", words[1].text)
    }

    @Test
    fun testMultilingualText() {
        // Arabic
        val arabicWords = TextWordTokenizer.tokenize("مرحبا بالعالم")
        assertEquals(2, arabicWords.size)

        // Mixed Latin, Urdu, Numbers
        val mixedWords = TextWordTokenizer.tokenize("Hello دنیا 123!")
        assertEquals(3, mixedWords.size)
        assertEquals("Hello", mixedWords[0].text)
        assertEquals("دنیا", mixedWords[1].text)
        assertEquals("123", mixedWords[2].text)
    }

    @Test
    fun testWhitespaceTokenWordCounting() {
        val words = TextWordTokenizer.tokenize(
            "Hello, world! 123",
            wordMode = WordCountMode.WHITESPACE_TOKENS
        )
        assertEquals(3, words.size)
        assertEquals("Hello,", words[0].text)
        assertEquals("world!", words[1].text)
        assertEquals("123", words[2].text)
    }

    @Test
    fun testOnlyPunctuation() {
        val words = TextWordTokenizer.tokenize("!!!")
        assertEquals(0, words.size)
    }
}
