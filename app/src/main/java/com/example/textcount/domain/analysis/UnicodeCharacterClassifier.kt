package com.example.textcount.domain.analysis

object UnicodeCharacterClassifier {

    fun isWhitespace(codePoint: Int): Boolean {
        return Character.isWhitespace(codePoint) ||
                Character.isSpaceChar(codePoint) ||
                codePoint == 0x00A0 || // Non-breaking space
                codePoint == 0x200B || // Zero-width space
                codePoint == 0xFEFF    // Zero-width no-break space / BOM
    }

    fun isLetter(codePoint: Int): Boolean {
        return Character.isLetter(codePoint)
    }

    fun isDigit(codePoint: Int): Boolean {
        return Character.isDigit(codePoint)
    }

    fun isPunctuation(codePoint: Int): Boolean {
        val type = Character.getType(codePoint)
        return type == Character.START_PUNCTUATION.toInt() ||
                type == Character.END_PUNCTUATION.toInt() ||
                type == Character.OTHER_PUNCTUATION.toInt() ||
                type == Character.CONNECTOR_PUNCTUATION.toInt() ||
                type == Character.DASH_PUNCTUATION.toInt() ||
                type == Character.INITIAL_QUOTE_PUNCTUATION.toInt() ||
                type == Character.FINAL_QUOTE_PUNCTUATION.toInt()
    }

    fun isSymbol(codePoint: Int): Boolean {
        val type = Character.getType(codePoint)
        return type == Character.MATH_SYMBOL.toInt() ||
                type == Character.CURRENCY_SYMBOL.toInt() ||
                type == Character.MODIFIER_SYMBOL.toInt() ||
                type == Character.OTHER_SYMBOL.toInt()
    }
}
