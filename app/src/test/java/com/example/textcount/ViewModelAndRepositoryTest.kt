package com.example.textcount

import com.example.textcount.data.model.CharacterCountMode
import com.example.textcount.data.model.WordCountMode
import com.example.textcount.data.repository.HistoryRepository
import com.example.textcount.data.repository.SettingsRepository
import com.example.textcount.domain.analysis.TextAnalyzer
import com.example.textcount.ui.TextCountViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelAndRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakePrefs: FakeSharedPreferences
    private lateinit var historyRepository: HistoryRepository
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var viewModel: TextCountViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakePrefs = FakeSharedPreferences()
        historyRepository = HistoryRepository(prefs = fakePrefs)
        settingsRepository = SettingsRepository(prefs = fakePrefs)
        viewModel = TextCountViewModel(
            textAnalyzer = TextAnalyzer(),
            historyRepository = historyRepository,
            settingsRepository = settingsRepository,
            ioDispatcher = testDispatcher
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testHistorySaveAndLimit() {
        // Save 12 different items
        for (i in 1..12) {
            val saved = historyRepository.saveEntry("Sample text analysis number $i")
            assertTrue("Item $i should be saved", saved)
        }

        val history = historyRepository.getHistory()
        assertEquals("History must not exceed 10 entries", 10, history.size)
        // Most recent should be number 12
        assertTrue(history.first().text.contains("12"))
        // Oldest retained should be number 3 (1 and 2 dropped)
        assertTrue(history.last().text.contains("3"))
    }

    @Test
    fun testDuplicateHistoryPrevention() {
        val firstSave = historyRepository.saveEntry("Identical text")
        assertTrue(firstSave)

        // Consecutive duplicate
        val secondSave = historyRepository.saveEntry("Identical text")
        assertFalse("Consecutive duplicate should not be saved", secondSave)

        val history = historyRepository.getHistory()
        assertEquals(1, history.size)
    }

    @Test
    fun testHistoryDeleteAndClear() {
        historyRepository.saveEntry("Item 1")
        historyRepository.saveEntry("Item 2")
        val items = historyRepository.getHistory()
        assertEquals(2, items.size)

        val deleted = historyRepository.deleteEntry(items[0].id)
        assertTrue(deleted)
        assertEquals(1, historyRepository.getHistory().size)

        historyRepository.clearHistory()
        assertEquals(0, historyRepository.getHistory().size)
    }

    @Test
    fun testHistoryMaxSavedTextLength() {
        val hugeText = "a".repeat(15_000)
        historyRepository.saveEntry(hugeText)
        val saved = historyRepository.getHistory().first()
        assertEquals(HistoryRepository.MAX_SAVED_TEXT_LENGTH, saved.text.length)
    }

    @Test
    fun testSettingsPersistenceAndReset() {
        // Mutate settings
        settingsRepository.theme = "Dark"
        settingsRepository.isLiveCounting = false
        settingsRepository.characterCountMode = CharacterCountMode.CODE_POINT
        settingsRepository.wordCountMode = WordCountMode.WHITESPACE_TOKENS
        settingsRepository.averagePrecision = 4

        assertEquals("Dark", settingsRepository.theme)
        assertFalse(settingsRepository.isLiveCounting)
        assertEquals(CharacterCountMode.CODE_POINT, settingsRepository.characterCountMode)
        assertEquals(WordCountMode.WHITESPACE_TOKENS, settingsRepository.wordCountMode)
        assertEquals(4, settingsRepository.averagePrecision)

        // Reset settings
        settingsRepository.resetSettings()
        assertEquals(SettingsRepository.DEFAULT_THEME, settingsRepository.theme)
        assertEquals(SettingsRepository.DEFAULT_LIVE_COUNTING, settingsRepository.isLiveCounting)
        assertEquals(SettingsRepository.DEFAULT_CHARACTER_MODE, settingsRepository.characterCountMode)
        assertEquals(SettingsRepository.DEFAULT_WORD_MODE, settingsRepository.wordCountMode)
        assertEquals(SettingsRepository.DEFAULT_PRECISION, settingsRepository.averagePrecision)
    }

    @Test
    fun testViewModelLiveCountingAndClear() = runTest(testDispatcher) {
        viewModel.onTextChanged("Hello world")
        advanceUntilIdle()

        var state = viewModel.uiState.value
        assertEquals("Hello world", state.text)
        assertNotNull(state.result)
        assertEquals(11, state.result?.characterCount)
        assertEquals(2, state.result?.wordCount)

        // Clear
        viewModel.onClearClicked()
        advanceUntilIdle()

        state = viewModel.uiState.value
        assertEquals("", state.text)
        assertEquals(0, state.result?.characterCount)
        assertEquals(0, state.result?.wordCount)
    }

    @Test
    fun testViewModelSaveAndRestoreHistory() = runTest(testDispatcher) {
        viewModel.onTextChanged("Text to be saved")
        advanceUntilIdle()

        val saved = viewModel.onSaveClicked()
        assertTrue(saved)
        assertEquals(1, viewModel.uiState.value.history.size)

        // Clear text
        viewModel.onClearClicked()
        advanceUntilIdle()
        assertEquals("", viewModel.uiState.value.text)

        // Restore history
        val historyItem = viewModel.uiState.value.history.first()
        viewModel.onRestoreHistory(historyItem)
        advanceUntilIdle()

        assertEquals("Text to be saved", viewModel.uiState.value.text)
        assertEquals(16, viewModel.uiState.value.result?.characterCount)
        assertEquals(4, viewModel.uiState.value.result?.wordCount)
    }

    @Test
    fun testViewModelModeChanges() = runTest(testDispatcher) {
        viewModel.onTextChanged("Hello, world!")
        advanceUntilIdle()
        assertEquals(2, viewModel.uiState.value.result?.wordCount)

        // Switch to whitespace tokens mode
        viewModel.setWordMode(WordCountMode.WHITESPACE_TOKENS)
        advanceUntilIdle()
        assertEquals(2, viewModel.uiState.value.result?.wordCount)
    }
}
