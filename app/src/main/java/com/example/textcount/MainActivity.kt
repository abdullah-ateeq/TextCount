package com.example.textcount

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.textcount.ui.TextCountViewModel
import com.example.textcount.ui.TextCountViewModelFactory
import com.example.textcount.ui.navigation.TextCountNavGraph
import com.example.textcount.ui.theme.TextCountTheme

class MainActivity : ComponentActivity() {

    private val viewModel: TextCountViewModel by viewModels {
        TextCountViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsState()
            val systemInDark = isSystemInDarkTheme()
            val isDarkTheme = when (uiState.theme) {
                "Light" -> false
                "Dark" -> true
                else -> systemInDark
            }

            TextCountTheme(
                darkTheme = isDarkTheme,
                dynamicColor = uiState.dynamicColor
            ) {
                TextCountNavGraph(viewModel = viewModel)
            }
        }
    }
}
