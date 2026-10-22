package com.example.textcount.ui.home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.textcount.data.model.CharacterCountMode
import com.example.textcount.data.model.TextCountResult
import com.example.textcount.ui.TextCountViewModel
import com.example.textcount.util.TextCountFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: TextCountViewModel,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val view = LocalView.current
    var moreStatisticsExpanded by remember { mutableStateOf(false) }

    fun performHaptic() {
        if (uiState.hapticFeedback) {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
    }

    fun copyToClipboard(label: String, textToCopy: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, textToCopy)
        clipboard.setPrimaryClip(clip)
        performHaptic()
        Toast.makeText(context, "Copied $label: $textToCopy", Toast.LENGTH_SHORT).show()
    }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "TextCount",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Analyze your text instantly.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        performHaptic()
                        onNavigateToHistory()
                    }) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "History"
                        )
                    }
                    IconButton(onClick = {
                        performHaptic()
                        onNavigateToSettings()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                    IconButton(onClick = {
                        performHaptic()
                        onNavigateToAbout()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                            contentDescription = "About"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
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
            // Text Editor Section
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "YOUR TEXT",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )

                        if (uiState.isCalculating) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(12.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Analyzing…",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.text,
                        onValueChange = { viewModel.onTextChanged(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .semantics { contentDescription = "Text to analyze." },
                        placeholder = {
                            Text(
                                text = "Type or paste your text here…",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            )
                        },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 22.sp
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Compact counter at bottom right of editor
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, end = 4.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        val chars = uiState.result?.characterCount ?: 0
                        val words = uiState.result?.wordCount ?: 0
                        Text(
                            text = "$chars characters • $words words",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        )
                    }

                    if (uiState.errorMessage != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = uiState.errorMessage ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.error
                            )
                        )
                    }
                }
            }

            // Primary 4 Statistics Cards (2x2 Grid)
            item {
                val result = uiState.result ?: TextCountResult(
                    characterCount = 0,
                    characterCountWithoutWhitespace = 0,
                    wordCount = 0,
                    lineCount = 0,
                    spaceCount = 0,
                    newlineCount = 0,
                    whitespaceCount = 0,
                    tabCount = 0,
                    letterCount = 0,
                    digitCount = 0,
                    punctuationCount = 0,
                    symbolCount = 0,
                    paragraphCount = 0,
                    averageWordLength = null,
                    longestWord = null,
                    longestWordLength = null,
                    shortestWord = null,
                    shortestWordLength = null
                )

                val charLabel = if (uiState.characterMode == CharacterCountMode.CODE_POINT) {
                    "UNICODE CODE POINTS"
                } else {
                    "CHARACTERS"
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PrimaryStatCard(
                            label = charLabel,
                            value = result.characterCount.toString(),
                            accessibilityDescription = "Character count.",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                copyToClipboard("Characters", result.characterCount.toString())
                            }
                        )
                        PrimaryStatCard(
                            label = "WORDS",
                            value = result.wordCount.toString(),
                            accessibilityDescription = "Word count.",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                copyToClipboard("Words", result.wordCount.toString())
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PrimaryStatCard(
                            label = "LINES",
                            value = result.lineCount.toString(),
                            accessibilityDescription = "Line count.",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                copyToClipboard("Lines", result.lineCount.toString())
                            }
                        )
                        PrimaryStatCard(
                            label = "SPACES",
                            value = result.spaceCount.toString(),
                            accessibilityDescription = "Space count.",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                copyToClipboard("Spaces", result.spaceCount.toString())
                            }
                        )
                    }
                }
            }

            // Expandable More Statistics Section
            item {
                val result = uiState.result
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    performHaptic()
                                    moreStatisticsExpanded = !moreStatisticsExpanded
                                }
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                                .semantics { contentDescription = "Show additional text statistics." },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "MORE STATISTICS",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                            Icon(
                                imageVector = if (moreStatisticsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (moreStatisticsExpanded) "Collapse" else "Expand"
                            )
                        }

                        AnimatedVisibility(
                            visible = moreStatisticsExpanded,
                            enter = if (uiState.animations) expandVertically(tween(250)) + fadeIn(tween(250)) else fadeIn(tween(0)),
                            exit = if (uiState.animations) shrinkVertically(tween(250)) + fadeOut(tween(250)) else fadeOut(tween(0))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                HorizontalDivider(modifier = Modifier.padding(bottom = 12.dp))

                                DetailStatRow(
                                    label = "Characters without spaces",
                                    value = (result?.characterCountWithoutWhitespace ?: 0).toString(),
                                    onCopy = { copyToClipboard("Characters without spaces", (result?.characterCountWithoutWhitespace ?: 0).toString()) }
                                )
                                DetailStatRow(
                                    label = "Newlines",
                                    value = (result?.newlineCount ?: 0).toString(),
                                    onCopy = { copyToClipboard("Newlines", (result?.newlineCount ?: 0).toString()) }
                                )
                                DetailStatRow(
                                    label = "Whitespace",
                                    value = (result?.whitespaceCount ?: 0).toString(),
                                    onCopy = { copyToClipboard("Whitespace", (result?.whitespaceCount ?: 0).toString()) }
                                )
                                DetailStatRow(
                                    label = "Tabs",
                                    value = (result?.tabCount ?: 0).toString(),
                                    onCopy = { copyToClipboard("Tabs", (result?.tabCount ?: 0).toString()) }
                                )
                                DetailStatRow(
                                    label = "Letters",
                                    value = (result?.letterCount ?: 0).toString(),
                                    onCopy = { copyToClipboard("Letters", (result?.letterCount ?: 0).toString()) }
                                )
                                DetailStatRow(
                                    label = "Digits",
                                    value = (result?.digitCount ?: 0).toString(),
                                    onCopy = { copyToClipboard("Digits", (result?.digitCount ?: 0).toString()) }
                                )
                                DetailStatRow(
                                    label = "Punctuation",
                                    value = (result?.punctuationCount ?: 0).toString(),
                                    onCopy = { copyToClipboard("Punctuation", (result?.punctuationCount ?: 0).toString()) }
                                )
                                DetailStatRow(
                                    label = "Symbols",
                                    value = (result?.symbolCount ?: 0).toString(),
                                    onCopy = { copyToClipboard("Symbols", (result?.symbolCount ?: 0).toString()) }
                                )
                                DetailStatRow(
                                    label = "Paragraphs",
                                    value = (result?.paragraphCount ?: 0).toString(),
                                    onCopy = { copyToClipboard("Paragraphs", (result?.paragraphCount ?: 0).toString()) }
                                )
                                DetailStatRow(
                                    label = "Average word length",
                                    value = TextCountFormatter.formatAverageWordLength(result?.averageWordLength, uiState.averagePrecision),
                                    onCopy = { copyToClipboard("Average word length", TextCountFormatter.formatAverageWordLength(result?.averageWordLength, uiState.averagePrecision)) }
                                )
                                DetailStatRow(
                                    label = "Longest word",
                                    value = TextCountFormatter.formatWordDisplay(result?.longestWord),
                                    onCopy = { copyToClipboard("Longest word", TextCountFormatter.formatWordDisplay(result?.longestWord)) }
                                )
                                DetailStatRow(
                                    label = "Shortest word",
                                    value = TextCountFormatter.formatWordDisplay(result?.shortestWord),
                                    onCopy = { copyToClipboard("Shortest word", TextCountFormatter.formatWordDisplay(result?.shortestWord)) }
                                )
                            }
                        }
                    }
                }
            }

            // Quick Actions Buttons Section
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Row 1: Paste and Clear
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                performHaptic()
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clipData = clipboard.primaryClip
                                if (clipData != null && clipData.itemCount > 0) {
                                    val text = clipData.getItemAt(0).coerceToText(context).toString()
                                    viewModel.onPaste(text)
                                } else {
                                    Toast.makeText(context, "Clipboard is empty", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentPaste,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "PASTE", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                performHaptic()
                                viewModel.onClearClicked()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "CLEAR", fontWeight = FontWeight.Bold)
                        }
                    }

                    // Row 2: Copy Results, Share, Save
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                performHaptic()
                                val report = viewModel.getCopyReport()
                                if (report != null) {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("TextCount Results", report)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Results copied to clipboard", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1.3f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "COPY RESULTS", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                performHaptic()
                                val report = viewModel.getCopyReport()
                                if (report != null) {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, report)
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share text analysis"))
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "SHARE", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                performHaptic()
                                viewModel.onSaveClicked()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "SAVE", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Bottom Spacing
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun PrimaryStatCard(
    label: String,
    value: String,
    accessibilityDescription: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .semantics { contentDescription = accessibilityDescription },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.primary
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun DetailStatRow(
    label: String,
    value: String,
    onCopy: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCopy)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface
            ),
            textAlign = TextAlign.End,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
