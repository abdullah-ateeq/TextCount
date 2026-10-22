package com.example.textcount.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.textcount.data.model.CharacterCountMode
import com.example.textcount.data.model.TextCountHistory
import com.example.textcount.data.model.WordCountMode
import com.example.textcount.data.repository.HistoryRepository
import com.example.textcount.data.repository.SettingsRepository
import com.example.textcount.domain.analysis.TextAnalyzer
import com.example.textcount.util.TextCountFormatter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TextCountViewModel(
    private val textAnalyzer: TextAnalyzer = TextAnalyzer(),
    private val historyRepository: HistoryRepository,
    private val settingsRepository: SettingsRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {

    private val _uiState = MutableStateFlow(TextCountUiState())
    val uiState: StateFlow<TextCountUiState> = _uiState.asStateFlow()

    private var analysisJob: Job? = null

    init {
        loadSettingsAndHistory()
    }

    private fun loadSettingsAndHistory() {
        val initialText = if (settingsRepository.isRestoreLastText) {
            settingsRepository.lastText
        } else {
            ""
        }

        _uiState.update {
            it.copy(
                text = initialText,
                history = historyRepository.getHistory(),
                characterMode = settingsRepository.characterCountMode,
                wordMode = settingsRepository.wordCountMode,
                liveCounting = settingsRepository.isLiveCounting,
                hapticFeedback = settingsRepository.isHapticFeedback,
                animations = settingsRepository.isAnimations,
                confirmDelete = settingsRepository.isConfirmDelete,
                restoreLastText = settingsRepository.isRestoreLastText,
                theme = settingsRepository.theme,
                dynamicColor = settingsRepository.isDynamicColor,
                averagePrecision = settingsRepository.averagePrecision
            )
        }

        if (initialText.isNotEmpty()) {
            triggerAnalysis(initialText, isImmediate = true)
        } else {
            // Analyze empty text to set 0 stats
            triggerAnalysis("", isImmediate = true)
        }
    }

    fun onTextChanged(newText: String) {
        _uiState.update { it.copy(text = newText) }

        if (settingsRepository.isRestoreLastText) {
            settingsRepository.lastText = newText
        }

        if (_uiState.value.liveCounting) {
            val isLarge = newText.length > 5_000
            triggerAnalysis(newText, isImmediate = !isLarge)
        }
    }

    fun triggerManualAnalysis() {
        triggerAnalysis(_uiState.value.text, isImmediate = true)
    }

    private fun triggerAnalysis(text: String, isImmediate: Boolean) {
        analysisJob?.cancel()

        if (text.isEmpty()) {
            val emptyResult = textAnalyzer.analyze("").getOrNull()
            _uiState.update {
                it.copy(
                    result = emptyResult,
                    errorMessage = null,
                    isCalculating = false
                )
            }
            return
        }

        analysisJob = viewModelScope.launch {
            if (!isImmediate) {
                delay(150) // Debounce for large text
            }

            _uiState.update { it.copy(isCalculating = true) }

            val characterMode = _uiState.value.characterMode
            val wordMode = _uiState.value.wordMode

            try {
                val result = withContext(ioDispatcher) {
                    textAnalyzer.analyze(
                        text = text,
                        characterMode = characterMode,
                        wordMode = wordMode
                    )
                }

                result.fold(
                    onSuccess = { analysisResult ->
                        _uiState.update {
                            it.copy(
                                result = analysisResult,
                                errorMessage = null,
                                isCalculating = false
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                errorMessage = error.message ?: "Analysis failed",
                                isCalculating = false
                            )
                        }
                    }
                )
            } catch (_: CancellationException) {
                // Job cancelled due to new input, do not update UI with stale state
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = e.message ?: "Unexpected error",
                        isCalculating = false
                    )
                }
            }
        }
    }

    fun onClearClicked() {
        onTextChanged("")
    }

    fun onPaste(pasted: String) {
        val currentText = _uiState.value.text
        val newText = if (currentText.isEmpty()) pasted else currentText + pasted
        onTextChanged(newText)
    }

    fun onSaveClicked(): Boolean {
        val currentText = _uiState.value.text
        if (currentText.isBlank()) {
            _uiState.update { it.copy(userMessage = "Nothing to save") }
            return false
        }

        val saved = historyRepository.saveEntry(currentText)
        if (saved) {
            _uiState.update {
                it.copy(
                    history = historyRepository.getHistory(),
                    userMessage = "Analysis saved to history"
                )
            }
            return true
        } else {
            _uiState.update { it.copy(userMessage = "Already saved in history") }
            return false
        }
    }

    fun onRestoreHistory(item: TextCountHistory) {
        onTextChanged(item.text)
        _uiState.update { it.copy(userMessage = "Restored from history") }
    }

    fun onDeleteHistoryEntry(id: String) {
        val removed = historyRepository.deleteEntry(id)
        if (removed) {
            _uiState.update {
                it.copy(
                    history = historyRepository.getHistory(),
                    userMessage = "Item deleted"
                )
            }
        }
    }

    fun onClearHistory() {
        historyRepository.clearHistory()
        _uiState.update {
            it.copy(
                history = emptyList(),
                userMessage = "History cleared"
            )
        }
    }

    fun getCopyReport(): String? {
        val result = _uiState.value.result ?: return null
        return TextCountFormatter.formatReport(result, _uiState.value.averagePrecision)
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }

    // Settings mutators
    fun setTheme(theme: String) {
        settingsRepository.theme = theme
        _uiState.update { it.copy(theme = theme) }
    }

    fun setDynamicColor(enabled: Boolean) {
        settingsRepository.isDynamicColor = enabled
        _uiState.update { it.copy(dynamicColor = enabled) }
    }

    fun setLiveCounting(enabled: Boolean) {
        settingsRepository.isLiveCounting = enabled
        _uiState.update { it.copy(liveCounting = enabled) }
        if (enabled && _uiState.value.text.isNotEmpty()) {
            triggerAnalysis(_uiState.value.text, isImmediate = true)
        }
    }

    fun setHapticFeedback(enabled: Boolean) {
        settingsRepository.isHapticFeedback = enabled
        _uiState.update { it.copy(hapticFeedback = enabled) }
    }

    fun setAnimations(enabled: Boolean) {
        settingsRepository.isAnimations = enabled
        _uiState.update { it.copy(animations = enabled) }
    }

    fun setConfirmDelete(enabled: Boolean) {
        settingsRepository.isConfirmDelete = enabled
        _uiState.update { it.copy(confirmDelete = enabled) }
    }

    fun setRestoreLastText(enabled: Boolean) {
        settingsRepository.isRestoreLastText = enabled
        if (enabled) {
            settingsRepository.lastText = _uiState.value.text
        } else {
            settingsRepository.lastText = ""
        }
        _uiState.update { it.copy(restoreLastText = enabled) }
    }

    fun setCharacterMode(mode: CharacterCountMode) {
        settingsRepository.characterCountMode = mode
        _uiState.update { it.copy(characterMode = mode) }
        triggerAnalysis(_uiState.value.text, isImmediate = true)
    }

    fun setWordMode(mode: WordCountMode) {
        settingsRepository.wordCountMode = mode
        _uiState.update { it.copy(wordMode = mode) }
        triggerAnalysis(_uiState.value.text, isImmediate = true)
    }

    fun setAveragePrecision(precision: Int) {
        settingsRepository.averagePrecision = precision
        _uiState.update { it.copy(averagePrecision = precision) }
    }

    fun resetSettings() {
        settingsRepository.resetSettings()
        _uiState.update {
            it.copy(
                theme = SettingsRepository.DEFAULT_THEME,
                dynamicColor = SettingsRepository.DEFAULT_DYNAMIC_COLOR,
                liveCounting = SettingsRepository.DEFAULT_LIVE_COUNTING,
                hapticFeedback = SettingsRepository.DEFAULT_HAPTICS,
                animations = SettingsRepository.DEFAULT_ANIMATIONS,
                confirmDelete = SettingsRepository.DEFAULT_CONFIRM_DELETE,
                restoreLastText = SettingsRepository.DEFAULT_RESTORE_LAST_TEXT,
                characterMode = SettingsRepository.DEFAULT_CHARACTER_MODE,
                wordMode = SettingsRepository.DEFAULT_WORD_MODE,
                averagePrecision = SettingsRepository.DEFAULT_PRECISION,
                userMessage = "Settings reset to defaults"
            )
        }
        triggerAnalysis(_uiState.value.text, isImmediate = true)
    }
}
