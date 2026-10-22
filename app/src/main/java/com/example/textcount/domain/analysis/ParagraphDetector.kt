package com.example.textcount.domain.analysis

object ParagraphDetector {

    /**
     * A paragraph is defined as a contiguous block of non-empty lines
     * separated from another block by one or more empty/blank lines.
     * Empty or whitespace-only text returns 0 paragraphs.
     */
    fun countParagraphs(text: String): Int {
        if (text.isBlank()) return 0

        val lines = text.split(Regex("\r\n|\r|\n"))
        var count = 0
        var inParagraph = false

        for (line in lines) {
            if (line.isNotBlank()) {
                if (!inParagraph) {
                    count++
                    inParagraph = true
                }
            } else {
                inParagraph = false
            }
        }
        return count
    }
}
