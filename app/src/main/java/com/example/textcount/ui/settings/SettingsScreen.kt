package com.example.textcount.ui.settings

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.textcount.data.model.CharacterCountMode
import com.example.textcount.data.model.WordCountMode
import com.example.textcount.ui.TextCountViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: TextCountViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val view = LocalView.current

    var showClearHistoryDialog by remember { mutableStateOf(false) }
    var showResetSettingsDialog by remember { mutableStateOf(false) }

    fun performHaptic() {
        if (uiState.hapticFeedback) {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
    }

    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text(text = "Clear all saved text analyses?") },
            text = { Text(text = "All history entries will be permanently removed.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        performHaptic()
                        viewModel.onClearHistory()
                        showClearHistoryDialog = false
                    }
                ) {
                    Text(text = "Clear", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    if (showResetSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showResetSettingsDialog = false },
            title = { Text(text = "Reset all settings?") },
            text = { Text(text = "This will restore default configuration. Your saved history will not be deleted.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        performHaptic()
                        viewModel.resetSettings()
                        showResetSettingsDialog = false
                    }
                ) {
                    Text(text = "Reset", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetSettingsDialog = false }) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Settings") },
                navigationIcon = {
                    IconButton(onClick = {
                        performHaptic()
                        onNavigateBack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Home"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Appearance Section
            item {
                SettingsSectionHeader(title = "Appearance")
            }

            item {
                Column {
                    Text(
                        text = "Theme",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    listOf("System", "Light", "Dark").forEach { themeOption ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    performHaptic()
                                    viewModel.setTheme(themeOption)
                                }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = uiState.theme == themeOption,
                                onClick = {
                                    performHaptic()
                                    viewModel.setTheme(themeOption)
                                }
                            )
                            Text(
                                text = themeOption,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }

            item {
                SettingsSwitchRow(
                    title = "Dynamic Color",
                    subtitle = "Use colors derived from wallpaper (Android 12+)",
                    checked = uiState.dynamicColor,
                    onCheckedChange = {
                        performHaptic()
                        viewModel.setDynamicColor(it)
                    }
                )
            }

            item { HorizontalDivider() }

            // Behavior Section
            item {
                SettingsSectionHeader(title = "Behavior")
            }

            item {
                SettingsSwitchRow(
                    title = "Live Counting",
                    subtitle = "Calculate results immediately as you type or paste",
                    checked = uiState.liveCounting,
                    onCheckedChange = {
                        performHaptic()
                        viewModel.setLiveCounting(it)
                    }
                )
            }

            item {
                SettingsSwitchRow(
                    title = "Haptic Feedback",
                    subtitle = "Vibrate lightly on actions and buttons",
                    checked = uiState.hapticFeedback,
                    onCheckedChange = {
                        performHaptic()
                        viewModel.setHapticFeedback(it)
                    }
                )
            }

            item {
                SettingsSwitchRow(
                    title = "Animations",
                    subtitle = "Enable smooth UI transitions and expands",
                    checked = uiState.animations,
                    onCheckedChange = {
                        performHaptic()
                        viewModel.setAnimations(it)
                    }
                )
            }

            item {
                SettingsSwitchRow(
                    title = "Confirm Delete",
                    subtitle = "Prompt for confirmation when deleting items",
                    checked = uiState.confirmDelete,
                    onCheckedChange = {
                        performHaptic()
                        viewModel.setConfirmDelete(it)
                    }
                )
            }

            item {
                SettingsSwitchRow(
                    title = "Restore Last Text",
                    subtitle = "Save and reload editor content on app launch (up to 50k chars)",
                    checked = uiState.restoreLastText,
                    onCheckedChange = {
                        performHaptic()
                        viewModel.setRestoreLastText(it)
                    }
                )
            }

            item { HorizontalDivider() }

            // Counting Section
            item {
                SettingsSectionHeader(title = "Counting Rules")
            }

            item {
                Column {
                    Text(
                        text = "Character Counting",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                performHaptic()
                                viewModel.setCharacterMode(CharacterCountMode.USER_PERCEIVED)
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = uiState.characterMode == CharacterCountMode.USER_PERCEIVED,
                            onClick = {
                                performHaptic()
                                viewModel.setCharacterMode(CharacterCountMode.USER_PERCEIVED)
                            }
                        )
                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text(text = "User-perceived characters (Default)", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Accurate grapheme clusters, combining marks and emoji", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                performHaptic()
                                viewModel.setCharacterMode(CharacterCountMode.CODE_POINT)
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = uiState.characterMode == CharacterCountMode.CODE_POINT,
                            onClick = {
                                performHaptic()
                                viewModel.setCharacterMode(CharacterCountMode.CODE_POINT)
                            }
                        )
                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text(text = "Unicode Code Points", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Counts individual Unicode scalar values", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            item {
                Column {
                    Text(
                        text = "Word Counting",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                performHaptic()
                                viewModel.setWordMode(WordCountMode.SMART_UNICODE)
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = uiState.wordMode == WordCountMode.SMART_UNICODE,
                            onClick = {
                                performHaptic()
                                viewModel.setWordMode(WordCountMode.SMART_UNICODE)
                            }
                        )
                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text(text = "Smart Unicode (Default)", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Handles contractions, hyphens, emails, URLs, Unicode", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                performHaptic()
                                viewModel.setWordMode(WordCountMode.WHITESPACE_TOKENS)
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = uiState.wordMode == WordCountMode.WHITESPACE_TOKENS,
                            onClick = {
                                performHaptic()
                                viewModel.setWordMode(WordCountMode.WHITESPACE_TOKENS)
                            }
                        )
                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text(text = "Whitespace Tokens", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Sequences separated by whitespace", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            item {
                Column {
                    Text(
                        text = "Line Handling",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Logical lines (Default)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Lines separated by \\n, \\r\\n, or \\r. Trailing newline creates a final empty line.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                Column {
                    Text(
                        text = "Average Word Length Precision",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(0, 1, 2, 3, 4, 6).forEach { prec ->
                            RadioButton(
                                selected = uiState.averagePrecision == prec,
                                onClick = {
                                    performHaptic()
                                    viewModel.setAveragePrecision(prec)
                                }
                            )
                            Text(
                                text = prec.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                    }
                }
            }

            item { HorizontalDivider() }

            // Data Section
            item {
                SettingsSectionHeader(title = "Data & Storage")
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            performHaptic()
                            showClearHistoryDialog = true
                        }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Clear History",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "Delete all stored analyses (${uiState.history.size} items)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            performHaptic()
                            showResetSettingsDialog = true
                        }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Reset Settings",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "Restore default settings while preserving history",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelMedium.copy(
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp
        ),
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
