package com.example.corda.ui.navigation

import androidx.activity.ComponentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import com.example.corda.ui.screen.help.HelpAndFeedbackScreen
import com.example.corda.ui.screen.settings.SettingsScreen
import com.example.corda.ui.screen.settings.SettingsViewModel

fun EntryProviderScope<Screen>.utilityEntries(
    activity: ComponentActivity,
    navigateBack: () -> Unit,
) {
    entry<Screen.Settings> {
        val settingsViewModel: SettingsViewModel = hiltViewModel(viewModelStoreOwner = activity)

        SettingsScreen(
            viewModel = settingsViewModel,
            onBack = navigateBack,
        )
    }
    entry<Screen.Help> {
        HelpAndFeedbackScreen(
            onBack = navigateBack,
        )
    }
}
