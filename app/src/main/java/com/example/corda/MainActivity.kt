package com.example.corda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.corda.data.SettingsManager
import com.example.corda.ui.CordaApp
import com.example.corda.ui.screen.settings.SettingsViewModel
import com.example.corda.ui.theme.CordaTheme
import android.view.WindowManager
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingsManager = SettingsManager(applicationContext)
        val settingsViewModel = SettingsViewModel(settingsManager)

        enableEdgeToEdge()
        setContent {
            // WithLifecycle ensures the app wont waste battery refreshing settings when screen is off
            val isDark by settingsViewModel.isDarkMode.collectAsStateWithLifecycle()
            val keepFocus by settingsViewModel.keepFocus.collectAsStateWithLifecycle()
            val language by settingsViewModel.language.collectAsStateWithLifecycle()

            LaunchedEffect(keepFocus) {
                if (keepFocus) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }

            LaunchedEffect(language) {
                val langCode = if (language == "Polski") "pl" else "en" // if more languages are added, create helper conversion function
                updateLocale(langCode)
            }

            CordaTheme(darkTheme = isDark) {
                CordaApp(settingsViewModel = settingsViewModel)
            }
        }
    }

    private fun updateLocale(langCode: String) {
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val resources = this.resources
        val config = resources.configuration
        config.setLocale(locale)

        resources.updateConfiguration(config, resources.displayMetrics)
    }
}