package com.example.corda.ui.screen.tuner

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.corda.ui.components.NavigationPill

/**
 * Screen for the tuner.
 *
 * This screen serves as a "Reference Screen" for our UI architecture.
 *
 * ### TODO:
 * - Implement real-time frequency analysis and pitch detection.
 * - Add visual feedback for "In Tune" vs "Out of Tune" states.
 *
 * @param openDrawer lambda reporting an event to `CordaApp` to open a drawer
 * @param openSettings lambda reporting an event to `CordaApp` to open the settings
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TunerScreen(
    viewModel: TunerViewModel,
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit,
    openSettings: () -> Unit
) {
    val selectedTuning by viewModel.selectedTuning.collectAsStateWithLifecycle()
    // State for the ear mode toggle
    var isEarModeEnabled by remember{ mutableStateOf(false) }

    // We use the Scaffold to handle the heavy lifting of Material Design layout,
    // like placing the TopAppBar and managing window insets.
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    // Custom component for the tuning selection
                    NavigationPill(
                        text = selectedTuning.name,
                        supportingText = selectedTuning.instrument,
                        onClick = openSettings
                    )
                },
                navigationIcon = {
                    IconButton(onClick = openDrawer) {
                        Icon(Icons.Rounded.Menu, contentDescription = "Open drawer")
                    }
                },
                actions = {
                    IconToggleButton(
                        checked = isEarModeEnabled,
                        onCheckedChange = { isEarModeEnabled = it }
                    ) {
                        if (isEarModeEnabled) {
                            Icon(
                                Icons.AutoMirrored.Rounded.VolumeUp,
                                contentDescription = "Disable ear mode"
                            )
                        } else {
                            Icon(
                                Icons.AutoMirrored.Rounded.VolumeOff,
                                contentDescription = "Enable ear mode"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        // innerPadding accounts for the top bar and system bars so your content doesn't get clipped!
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Tuner Screen")
        }
    }
}
