package com.example.textcount.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.textcount.data.model.CharacterCountMode
import com.example.textcount.data.model.WordCountMode

class SettingsRepository(private val prefs: SharedPreferences) {

    constructor(context: Context) : this(
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    )

    companion object {
        const val PREFS_NAME = "text_count_settings_prefs"

        const val KEY_THEME = "theme"
        const val KEY_DYNAMIC_COLOR = "dynamic_color"
        const val KEY_LIVE_COUNTING = "live_counting"
        const val KEY_HAPTICS = "haptics"
        const val KEY_ANIMATIONS = "animations"
        const val KEY_CONFIRM_DELETE = "confirm_delete"
        const val KEY_CHARACTER_MODE = "character_mode"
        const val KEY_WORD_MODE = "word_mode"
        const val KEY_LINE_MODE = "line_mode"
        const val KEY_RESTORE_LAST_TEXT = "restore_last_text"
        const val KEY_LAST_TEXT = "last_text"
        const val KEY_PRECISION = "average_precision"

        const val DEFAULT_THEME = "System"
        const val DEFAULT_DYNAMIC_COLOR = true
        const val DEFAULT_LIVE_COUNTING = true
        const val DEFAULT_HAPTICS = true
        const val DEFAULT_ANIMATIONS = true
        const val DEFAULT_CONFIRM_DELETE = true
        val DEFAULT_CHARACTER_MODE = CharacterCountMode.USER_PERCEIVED
        val DEFAULT_WORD_MODE = WordCountMode.SMART_UNICODE
        const val DEFAULT_LINE_MODE = "Logical lines"
        const val DEFAULT_RESTORE_LAST_TEXT = false
        const val DEFAULT_PRECISION = 2
        const val MAX_LAST_TEXT_LENGTH = 50_000
    }

    var theme: String
        get() = prefs.getString(KEY_THEME, DEFAULT_THEME) ?: DEFAULT_THEME
        set(value) = prefs.edit().putString(KEY_THEME, value).apply()

    var isDynamicColor: Boolean
        get() = prefs.getBoolean(KEY_DYNAMIC_COLOR, DEFAULT_DYNAMIC_COLOR)
        set(value) = prefs.edit().putBoolean(KEY_DYNAMIC_COLOR, value).apply()

    var isLiveCounting: Boolean
        get() = prefs.getBoolean(KEY_LIVE_COUNTING, DEFAULT_LIVE_COUNTING)
        set(value) = prefs.edit().putBoolean(KEY_LIVE_COUNTING, value).apply()

    var isHapticFeedback: Boolean
        get() = prefs.getBoolean(KEY_HAPTICS, DEFAULT_HAPTICS)
        set(value) = prefs.edit().putBoolean(KEY_HAPTICS, value).apply()

    var isAnimations: Boolean
        get() = prefs.getBoolean(KEY_ANIMATIONS, DEFAULT_ANIMATIONS)
        set(value) = prefs.edit().putBoolean(KEY_ANIMATIONS, value).apply()

    var isConfirmDelete: Boolean
        get() = prefs.getBoolean(KEY_CONFIRM_DELETE, DEFAULT_CONFIRM_DELETE)
        set(value) = prefs.edit().putBoolean(KEY_CONFIRM_DELETE, value).apply()

    var characterCountMode: CharacterCountMode
        get() {
            val name = prefs.getString(KEY_CHARACTER_MODE, DEFAULT_CHARACTER_MODE.name)
            return try {
                CharacterCountMode.valueOf(name ?: DEFAULT_CHARACTER_MODE.name)
            } catch (_: Exception) {
                DEFAULT_CHARACTER_MODE
            }
        }
        set(value) = prefs.edit().putString(KEY_CHARACTER_MODE, value.name).apply()

    var wordCountMode: WordCountMode
        get() {
            val name = prefs.getString(KEY_WORD_MODE, DEFAULT_WORD_MODE.name)
            return try {
                WordCountMode.valueOf(name ?: DEFAULT_WORD_MODE.name)
            } catch (_: Exception) {
                DEFAULT_WORD_MODE
            }
        }
        set(value) = prefs.edit().putString(KEY_WORD_MODE, value.name).apply()

    var lineMode: String
        get() = prefs.getString(KEY_LINE_MODE, DEFAULT_LINE_MODE) ?: DEFAULT_LINE_MODE
        set(value) = prefs.edit().putString(KEY_LINE_MODE, value).apply()

    var isRestoreLastText: Boolean
        get() = prefs.getBoolean(KEY_RESTORE_LAST_TEXT, DEFAULT_RESTORE_LAST_TEXT)
        set(value) = prefs.edit().putBoolean(KEY_RESTORE_LAST_TEXT, value).apply()

    var lastText: String
        get() = prefs.getString(KEY_LAST_TEXT, "") ?: ""
        set(value) {
            val truncated = if (value.length > MAX_LAST_TEXT_LENGTH) {
                value.take(MAX_LAST_TEXT_LENGTH)
            } else {
                value
            }
            prefs.edit().putString(KEY_LAST_TEXT, truncated).apply()
        }

    var averagePrecision: Int
        get() = prefs.getInt(KEY_PRECISION, DEFAULT_PRECISION)
        set(value) = prefs.edit().putInt(KEY_PRECISION, value).apply()

    fun resetSettings() {
        prefs.edit()
            .putString(KEY_THEME, DEFAULT_THEME)
            .putBoolean(KEY_DYNAMIC_COLOR, DEFAULT_DYNAMIC_COLOR)
            .putBoolean(KEY_LIVE_COUNTING, DEFAULT_LIVE_COUNTING)
            .putBoolean(KEY_HAPTICS, DEFAULT_HAPTICS)
            .putBoolean(KEY_ANIMATIONS, DEFAULT_ANIMATIONS)
            .putBoolean(KEY_CONFIRM_DELETE, DEFAULT_CONFIRM_DELETE)
            .putString(KEY_CHARACTER_MODE, DEFAULT_CHARACTER_MODE.name)
            .putString(KEY_WORD_MODE, DEFAULT_WORD_MODE.name)
            .putString(KEY_LINE_MODE, DEFAULT_LINE_MODE)
            .putBoolean(KEY_RESTORE_LAST_TEXT, DEFAULT_RESTORE_LAST_TEXT)
            .putString(KEY_LAST_TEXT, "")
            .putInt(KEY_PRECISION, DEFAULT_PRECISION)
            .apply()
    }
}
