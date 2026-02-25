package com.example.habitflow.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.habitflow.presentation.navigation.NavigationRoot
import com.example.habitflow.presentation.screens.settings.SettingsViewModel
import com.example.habitflow.presentation.ui.theme.HabitFlowTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: SettingsViewModel = hiltViewModel()
            val screenState by viewModel.state.collectAsState()
            HabitFlowTheme(
                darkTheme = screenState.isDarkTheme
            ) {
                NavigationRoot()
            }
        }
    }
}