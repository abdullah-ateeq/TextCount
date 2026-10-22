package com.example.textcount.util

import com.example.textcount.data.model.TextCountResult
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TextCountFormatter {

    private val numberFormat = NumberFormat.getIntegerInstance(Locale.US)

    fun formatNumber(number: Int): String {
        return numberFormat.format(number)
    }

    fun formatAverageWordLength(average: BigDecimal?, precision: Int = 2): String {
        if (average == null) return "—"
        val scaled = average.setScale(precision, RoundingMode.HALF_UP)
        val stripped = scaled.stripTrailingZeros()
        // If it's a whole number, e.g. 5, toPlainString() gives "5"
        // If scaled is 0.00, stripped might be 0, toPlainString() gives "0"
        return stripped.toPlainString()
    }

    fun formatWordDisplay(word: String?): String {
        return word ?: "—"
    }

    fun formatReport(result: TextCountResult, precision: Int = 2): String {
        val avg = formatAverageWordLength(result.averageWordLength, precision)
        val longest = formatWordDisplay(result.longestWord)
        val shortest = formatWordDisplay(result.shortestWord)

        return buildString {
            appendLine("TextCount")
            appendLine()
            appendLine("Characters: ${result.characterCount}")
            appendLine("Characters without spaces: ${result.characterCountWithoutWhitespace}")
            appendLine("Words: ${result.wordCount}")
            appendLine("Lines: ${result.lineCount}")
            appendLine("Spaces: ${result.spaceCount}")
            appendLine("Newlines: ${result.newlineCount}")
            appendLine("Whitespace: ${result.whitespaceCount}")
            appendLine("Tabs: ${result.tabCount}")
            appendLine("Letters: ${result.letterCount}")
            appendLine("Digits: ${result.digitCount}")
            appendLine("Punctuation: ${result.punctuationCount}")
            appendLine("Symbols: ${result.symbolCount}")
            appendLine("Paragraphs: ${result.paragraphCount}")
            appendLine("Average word length: $avg")
            appendLine("Longest word: $longest")
            append("Shortest word: $shortest")
        }
    }

    fun formatPreviewText(text: String, maxChars: Int = 60): String {
        val singleLine = text.replace(Regex("\\s+"), " ").trim()
        return if (singleLine.length <= maxChars) {
            singleLine
        } else {
            "${singleLine.take(maxChars)}..."
        }
    }

    fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        val dateFormat = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault())
        return dateFormat.format(date)
    }
}
