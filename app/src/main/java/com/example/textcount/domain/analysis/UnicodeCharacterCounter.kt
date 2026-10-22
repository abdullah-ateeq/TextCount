package com.example.textcount.domain.analysis

import com.example.textcount.data.model.CharacterCountMode
import java.text.BreakIterator
import java.util.Locale

object UnicodeCharacterCounter {

    /**
     * Counts characters in [text] based on [mode].
     */
    fun countCharacters(text: String, mode: CharacterCountMode = CharacterCountMode.USER_PERCEIVED): Int {
        if (text.isEmpty()) return 0
        return when (mode) {
            CharacterCountMode.USER_PERCEIVED -> countUserPerceivedCharacters(text)
            CharacterCountMode.CODE_POINT -> text.codePointCount(0, text.length)
        }
    }

    /**
     * Counts characters in [text] excluding whitespace based on [mode].
     */
    fun countCharactersWithoutWhitespace(
        text: String,
        mode: CharacterCountMode = CharacterCountMode.USER_PERCEIVED
    ): Int {
        if (text.isEmpty()) return 0
        return when (mode) {
            CharacterCountMode.USER_PERCEIVED -> countUserPerceivedWithoutWhitespace(text)
            CharacterCountMode.CODE_POINT -> countCodePointsWithoutWhitespace(text)
        }
    }

    fun countUserPerceivedCharacters(text: String): Int {
        if (text.isEmpty()) return 0
        val boundary = BreakIterator.getCharacterInstance(Locale.ROOT)
        boundary.setText(text)
        var count = 0
        while (boundary.next() != BreakIterator.DONE) {
            count++
        }
        return count
    }

    fun countUserPerceivedWithoutWhitespace(text: String): Int {
        if (text.isEmpty()) return 0
        val boundary = BreakIterator.getCharacterInstance(Locale.ROOT)
        boundary.setText(text)
        var count = 0
        var start = boundary.first()
        var end = boundary.next()
        while (end != BreakIterator.DONE) {
            val cluster = text.substring(start, end)
            if (!isClusterWhitespace(cluster)) {
                count++
            }
            start = end
            end = boundary.next()
        }
        return count
    }

    private fun countCodePointsWithoutWhitespace(text: String): Int {
        var count = 0
        var i = 0
        while (i < text.length) {
            val cp = text.codePointAt(i)
            if (!UnicodeCharacterClassifier.isWhitespace(cp)) {
                count++
            }
            i += Character.charCount(cp)
        }
        return count
    }

    private fun isClusterWhitespace(cluster: String): Boolean {
        var i = 0
        while (i < cluster.length) {
            val cp = cluster.codePointAt(i)
            if (!UnicodeCharacterClassifier.isWhitespace(cp)) {
                return false
            }
            i += Character.charCount(cp)
        }
        return true
    }
}
