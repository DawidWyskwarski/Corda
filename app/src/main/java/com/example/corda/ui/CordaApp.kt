package com.example.corda.ui

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.corda.ui.components.DrawerMenuContent
import com.example.corda.ui.navigation.Screen
import com.example.corda.ui.screen.help.HelpAndFeedbackScreen
import com.example.corda.ui.screen.inspirations.InspirationsScreen
import com.example.corda.ui.screen.metronome.MetronomeScreen
import com.example.corda.ui.screen.metronome.settings.MetronomeSettingsScreen
import com.example.corda.ui.screen.settings.SettingsScreen
import com.example.corda.ui.screen.tuner.TunerScreen
import com.example.corda.ui.screen.tuner.settings.TunerSettingsScreen
import kotlinx.coroutines.launch

@Composable
fun CordaApp(
    modifier: Modifier = Modifier
) {
    // The default screen could be set in settings and later read from shared preferences
    // or something like that, so the user can choose which screen to start with
    val backStack = remember { mutableStateListOf<Screen>(Screen.Tuner) }
    val scope = rememberCoroutineScope()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val openDrawer: () -> Unit = {
        scope.launch {
            drawerState.open()
        }
    }

    val navigateTo: (Screen) -> Unit = { screen ->
        scope.launch {
            if (backStack.lastOrNull() == screen) {
                drawerState.close()
                return@launch
            }

            if (backStack.first() == screen) {
                backStack.clear()
            }

            if (backStack.find { it == screen } != null) {
                backStack.remove(screen)
            }

            backStack.add(screen)
            drawerState.close()
        }
    }
    val navigateBack: () -> Unit = {
        backStack.removeLastOrNull()
    }

    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        // you can close it by swiping/clicking away,
        // but you can't swipe to open it
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            DrawerMenuContent(
                currentScreen = backStack.lastOrNull() ?: Screen.Tuner,
                onScreenSelected = navigateTo
            )
        }
    ) {
        NavDisplay(
            backStack = backStack,
            onBack = navigateBack,
            entryProvider = entryProvider {
                entry<Screen.Tuner> {
                    TunerScreen(
                        openDrawer = openDrawer,
                        openSettings = { navigateTo(Screen.TunerSettings) }
                    )
                }
                entry<Screen.TunerSettings> {
                    TunerSettingsScreen(
                        onBack = navigateBack
                    )
                }
                entry<Screen.Metronome> {
                    MetronomeScreen(
                        openDrawer = openDrawer,
                        openSettings = { navigateTo(Screen.MetronomeSettings) }
                    )
                }
                entry<Screen.MetronomeSettings> {
                    MetronomeSettingsScreen(
                        onBack = navigateBack
                    )
                }
                entry<Screen.Inspirations> {
                    InspirationsScreen(
                        openDrawer = openDrawer
                    )
                }
                //TODO add inspiration details/edit/add screen,
                // it will be a little more complicated
                // they all will share the same layout
                // and i don't want to implement it right now
                entry<Screen.Settings> {
                    SettingsScreen(
                        onBack = navigateBack
                    )
                }
                entry<Screen.Help> {
                    HelpAndFeedbackScreen(
                        onBack = navigateBack
                    )
                }
            }
        )
    }
}