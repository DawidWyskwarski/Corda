package com.example.corda.ui.navigation

import androidx.activity.ComponentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import com.example.corda.ui.screen.metronome.MetronomeScreen
import com.example.corda.ui.screen.metronome.MetronomeViewModel
import com.example.corda.ui.screen.metronome.settings.MetronomeSettingsScreen

fun EntryProviderScope<Screen>.metronomeEntries(
    activity: ComponentActivity,
    openDrawer: () -> Unit,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    entry<Screen.Metronome> {
        // Activity-scoped so MetronomeScreen and MetronomeSettingsScreen share one ViewModel.
        val viewModel: MetronomeViewModel = hiltViewModel(viewModelStoreOwner = activity)
        MetronomeScreen(
            viewModel = viewModel,
            openDrawer = openDrawer,
            openSettings = { navigateTo(Screen.MetronomeSettings) },
        )
    }
    entry<Screen.MetronomeSettings> {
        val viewModel: MetronomeViewModel = hiltViewModel(viewModelStoreOwner = activity)
        MetronomeSettingsScreen(
            viewModel = viewModel,
            onBack = navigateBack,
        )
    }
}
