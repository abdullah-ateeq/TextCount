package com.example.textcount.domain.analysis

object TextLineCounter {

    /**
     * Counts logical lines and newline sequences in [text].
     * Supports \n, \r\n, and \r.
     * Empty text returns Pair(0, 0).
     * Non-empty text returns Pair(lines, newlines) where lines = newlines + 1.
     */
    fun countLinesAndNewlines(text: String): Pair<Int, Int> {
        if (text.isEmpty()) {
            return Pair(0, 0)
        }
        var newlines = 0
        var i = 0
        val length = text.length
        while (i < length) {
            val c = text[i]
            if (c == '\r') {
                newlines++
                if (i + 1 < length && text[i + 1] == '\n') {
                    i++ // Skip '\n' in CRLF pair
                }
            } else if (c == '\n') {
                newlines++
            }
            i++
        }
        val lines = newlines + 1
        return Pair(lines, newlines)
    }
}
