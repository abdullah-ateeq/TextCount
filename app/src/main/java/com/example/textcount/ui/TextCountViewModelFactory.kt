package com.example.textcount.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.textcount.data.repository.HistoryRepository
import com.example.textcount.data.repository.SettingsRepository
import com.example.textcount.domain.analysis.TextAnalyzer

class TextCountViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TextCountViewModel::class.java)) {
            val textAnalyzer = TextAnalyzer()
            val historyRepository = HistoryRepository(context.applicationContext)
            val settingsRepository = SettingsRepository(context.applicationContext)
            return TextCountViewModel(
                textAnalyzer = textAnalyzer,
                historyRepository = historyRepository,
                settingsRepository = settingsRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
