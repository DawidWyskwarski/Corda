package com.example.corda.ui.screen.tuner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.corda.ui.components.NavigationPill
import com.example.corda.ui.components.PitchArc
import com.example.corda.ui.components.TuningNoteChip
import com.example.corda.ui.screen.tuner.settings.TuningMode
import kotlin.math.ceil

/**
 * Screen for the tuner.
 *
 * This screen serves as a "Reference Screen" for our UI architecture.
 *
 * ### TODO:
 * - Implement real-time frequency analysis and pitch detection.
 * - Add visual feedback for "In Tune" vs "Out of Tune" states.
 * - Decide in which order should the sounds be displayed
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
    val selectedMode by viewModel.selectedMode.collectAsStateWithLifecycle()

    // State for the ear mode toggle
    var isEarModeEnabled by remember { mutableStateOf(false) }

    // We use the Scaffold to handle the heavy lifting of Material Design layout,
    // like placing the TopAppBar and managing window insets.
    Scaffold(
        modifier = modifier, topBar = {
            CenterAlignedTopAppBar(title = {
                // Custom component for the tuning selection
                NavigationPill(
                    text = if (selectedMode == TuningMode.STANDARD) {
                        selectedTuning.name
                    } else {
                        "Chromatic Mode"
                    }, supportingText = if (selectedMode == TuningMode.STANDARD) {
                        selectedTuning.instrument
                    } else {
                        ""
                    }, onClick = openSettings
                )
            }, navigationIcon = {
                IconButton(onClick = openDrawer) {
                    Icon(Icons.Rounded.Menu, contentDescription = "Open drawer")
                }
            }, actions = {
                IconToggleButton(
                    checked = isEarModeEnabled, onCheckedChange = { isEarModeEnabled = it }) {
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
            })
        }) { innerPadding ->
        // innerPadding accounts for the top bar and system bars so your content doesn't get clipped!
        Column(
            modifier = Modifier
                .padding(top = 48.dp)
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = !isEarModeEnabled,
                enter = slideInVertically { -it } + expandVertically(
                    expandFrom = Alignment.Top
                ) + fadeIn(),
                exit = slideOutVertically { -it } + shrinkVertically() + fadeOut(),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "82,41 Hz", // placeholder
                        style = MaterialTheme.typography.labelMedium
                    )

                    Box {
                        // Simple animation to check if the pitch arc works properly
                        // To be removed later
                        val infiniteTransition = rememberInfiniteTransition()

                        val cents by infiniteTransition.animateFloat(
                            initialValue = -50f,
                            targetValue = 50f,
                            label = "Cents Animation",
                            animationSpec = infiniteRepeatable(
                                animation = tween(durationMillis = 5000, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            )

                        )

                        PitchArc(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp), centsOff = cents
                        )

                        Text(
                            modifier = Modifier.align(Alignment.Center), text = "E₂", // placeholder
                            style = MaterialTheme.typography.displayLargeEmphasized
                        )
                    }
                }
            }

            if (selectedMode == TuningMode.STANDARD) {
                var selectedNote by remember { mutableStateOf<String?>(null) }

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 32.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val firstHalf = selectedTuning.sounds.take(
                            ceil(((selectedTuning.sounds.size + 1) / 2).toDouble()).toInt()
                        )
                        items(firstHalf) { item ->
                            TuningNoteChip(
                                note = item,
                                isSelected = selectedNote == item,
                                onClick = {
                                    selectedNote = if (selectedNote == item) {
                                        null
                                    } else {
                                        item
                                    }
                                }
                            )
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val secondHalf = selectedTuning.sounds.drop(
                            ceil(((selectedTuning.sounds.size + 1) / 2).toDouble()).toInt()
                        )
                        items(secondHalf) { item ->
                            TuningNoteChip(
                                note = item,
                                isSelected = selectedNote == item,
                                onClick = {
                                    selectedNote = if (selectedNote == item) {
                                        null
                                    } else {
                                        item
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


