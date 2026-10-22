package com.example.textcount.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.textcount.data.model.TextCountHistory
import com.example.textcount.util.TextCountFormatter
import java.util.UUID

class HistoryRepository(
    private val prefs: SharedPreferences,
    private val maxEntries: Int = MAX_HISTORY_ENTRIES,
    private val maxSavedTextLength: Int = MAX_SAVED_TEXT_LENGTH
) {

    companion object {
        const val PREFS_NAME = "text_count_history_prefs"
        const val KEY_HISTORY = "history_entries_json"
        const val MAX_HISTORY_ENTRIES = 10
        const val MAX_SAVED_TEXT_LENGTH = 10_000
    }

    constructor(context: Context) : this(
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    )

    fun getHistory(): List<TextCountHistory> {
        val raw = prefs.getString(KEY_HISTORY, null) ?: return emptyList()
        return parseHistoryJson(raw)
    }

    fun saveEntry(text: String): Boolean {
        if (text.isBlank()) return false

        val currentList = getHistory().toMutableList()

        // Check for consecutive duplicate (most recent item is at index 0)
        if (currentList.isNotEmpty() && currentList.first().text == text) {
            return false
        }

        val storedText = if (text.length > maxSavedTextLength) {
            text.take(maxSavedTextLength)
        } else {
            text
        }

        val preview = TextCountFormatter.formatPreviewText(text, 60)
        val newItem = TextCountHistory(
            id = UUID.randomUUID().toString(),
            textPreview = preview,
            text = storedText,
            savedAt = System.currentTimeMillis()
        )

        currentList.add(0, newItem)

        while (currentList.size > maxEntries) {
            currentList.removeAt(currentList.lastIndex)
        }

        prefs.edit().putString(KEY_HISTORY, serializeHistoryJson(currentList)).apply()
        return true
    }

    fun deleteEntry(id: String): Boolean {
        val currentList = getHistory().toMutableList()
        val removed = currentList.removeAll { it.id == id }
        if (removed) {
            prefs.edit().putString(KEY_HISTORY, serializeHistoryJson(currentList)).apply()
        }
        return removed
    }

    fun clearHistory() {
        prefs.edit().remove(KEY_HISTORY).apply()
    }

    private fun serializeHistoryJson(list: List<TextCountHistory>): String {
        val sb = StringBuilder()
        sb.append("[")
        for (i in list.indices) {
            val item = list[i]
            sb.append("{")
            sb.append("\"id\":\"").append(escapeJson(item.id)).append("\",")
            sb.append("\"textPreview\":\"").append(escapeJson(item.textPreview)).append("\",")
            sb.append("\"text\":\"").append(escapeJson(item.text)).append("\",")
            sb.append("\"savedAt\":").append(item.savedAt)
            sb.append("}")
            if (i < list.size - 1) sb.append(",")
        }
        sb.append("]")
        return sb.toString()
    }

    private fun parseHistoryJson(json: String): List<TextCountHistory> {
        val results = mutableListOf<TextCountHistory>()
        var i = 0
        while (i < json.length) {
            val objStart = json.indexOf('{', i)
            if (objStart == -1) break
            val objEnd = findMatchingBrace(json, objStart)
            if (objEnd == -1) break

            val block = json.substring(objStart + 1, objEnd)
            var id = ""
            var preview = ""
            var text = ""
            var savedAt = 0L

            var curr = 0
            while (curr < block.length) {
                val keyQuoteStart = block.indexOf('"', curr)
                if (keyQuoteStart == -1) break
                val keyQuoteEnd = block.indexOf('"', keyQuoteStart + 1)
                if (keyQuoteEnd == -1) break
                val key = block.substring(keyQuoteStart + 1, keyQuoteEnd)

                val colon = block.indexOf(':', keyQuoteEnd)
                if (colon == -1) break

                curr = colon + 1
                while (curr < block.length && block[curr].isWhitespace()) curr++

                if (curr < block.length && block[curr] == '"') {
                    val valStart = curr + 1
                    var valEnd = valStart
                    while (valEnd < block.length) {
                        if (block[valEnd] == '\\') {
                            valEnd += 2
                        } else if (block[valEnd] == '"') {
                            break
                        } else {
                            valEnd++
                        }
                    }
                    val strValue = if (valEnd <= block.length) unescapeJson(block.substring(valStart, valEnd)) else ""
                    when (key) {
                        "id" -> id = strValue
                        "textPreview" -> preview = strValue
                        "text" -> text = strValue
                    }
                    curr = valEnd + 1
                } else {
                    val numStart = curr
                    while (curr < block.length && (block[curr].isDigit() || block[curr] == '-')) curr++
                    val numStr = block.substring(numStart, curr)
                    if (key == "savedAt") {
                        savedAt = numStr.toLongOrNull() ?: 0L
                    }
                }
            }

            if (id.isNotEmpty()) {
                results.add(
                    TextCountHistory(
                        id = id,
                        textPreview = preview,
                        text = text,
                        savedAt = savedAt
                    )
                )
            }

            i = objEnd + 1
        }
        return results
    }

    private fun findMatchingBrace(json: String, startIdx: Int): Int {
        var inString = false
        var escape = false
        for (idx in startIdx until json.length) {
            val c = json[idx]
            if (escape) {
                escape = false
            } else if (c == '\\') {
                escape = true
            } else if (c == '"') {
                inString = !inString
            } else if (c == '}' && !inString) {
                return idx
            }
        }
        return -1
    }

    private fun escapeJson(str: String): String {
        val sb = StringBuilder()
        for (c in str) {
            when (c) {
                '\\' -> sb.append("\\\\")
                '"' -> sb.append("\\\"")
                '\n' -> sb.append("\\n")
                '\r' -> sb.append("\\r")
                '\t' -> sb.append("\\t")
                else -> sb.append(c)
            }
        }
        return sb.toString()
    }

    private fun unescapeJson(str: String): String {
        val sb = StringBuilder()
        var i = 0
        while (i < str.length) {
            if (str[i] == '\\' && i + 1 < str.length) {
                when (str[i + 1]) {
                    '\\' -> sb.append('\\')
                    '"' -> sb.append('"')
                    'n' -> sb.append('\n')
                    'r' -> sb.append('\r')
                    't' -> sb.append('\t')
                    else -> sb.append(str[i + 1])
                }
                i += 2
            } else {
                sb.append(str[i])
                i++
            }
        }
        return sb.toString()
    }
}
