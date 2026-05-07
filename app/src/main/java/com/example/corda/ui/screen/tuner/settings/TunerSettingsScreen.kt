package com.example.corda.ui.screen.tuner.settings

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Piano
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.corda.ui.components.FABMenu
import com.example.corda.ui.components.FABMenuItem
import com.example.corda.ui.components.FilterChipGroup
import com.example.corda.ui.components.SimpleSingleChoiceButtonGroup
import com.example.corda.ui.components.TuningListItem
import com.example.corda.ui.screen.tuner.TunerViewModel

/**
 * Screen for the tuner settings.
 *
 * ### TODO
 * - Make the FAB menu do something
 *
 * @param onBack lambda reporting an event to `CordaApp` to go back
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TunerSettingsScreen(
    viewModel: TunerViewModel,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    val selectedMode by viewModel.selectedMode.collectAsStateWithLifecycle()

    val modes = remember { TuningMode.entries.toList() }

    var isFabMenuOpen by remember { mutableStateOf(false) }

    val fabMenuItems = remember {
        listOf(
            FABMenuItem(Icons.AutoMirrored.Rounded.QueueMusic, "New custom tuning") {
                isFabMenuOpen = false
            },
            FABMenuItem(Icons.Rounded.Piano, "Manage Instruments") { isFabMenuOpen = false }
        )
    }

    BackHandler(isFabMenuOpen) { isFabMenuOpen = false }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Tuner Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = selectedMode == TuningMode.STANDARD,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                FABMenu(
                    isExpanded = isFabMenuOpen,
                    onExpandedChange = { isFabMenuOpen = it },
                    items = fabMenuItems
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Text(
                text = "Mode",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            SimpleSingleChoiceButtonGroup(
                modifier = Modifier.fillMaxWidth(),
                selectedItem = selectedMode,
                items = modes,
                onItemSelected = { viewModel.selectMode(it) }
            )

            AnimatedContent(
                targetState = selectedMode,
                modifier = Modifier.weight(1f),
                transitionSpec = {
                    if (targetState == TuningMode.STANDARD) {
                        slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                    } else {
                        slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                    }
                },
                label = "Mode Animation"
            ) { mode ->
                when (mode) {
                    TuningMode.STANDARD -> TuningsContent(viewModel)
                    TuningMode.CHROMATIC -> ChromaticContent()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TuningsContent(
    viewModel: TunerViewModel,
    modifier: Modifier = Modifier,
) {
    val filteredTunings by viewModel.filteredTunings.collectAsStateWithLifecycle()
    val selectedTuning by viewModel.selectedTuning.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val instruments by viewModel.instruments.collectAsStateWithLifecycle()
    val selectedInstrument by viewModel.selectedInstrument.collectAsStateWithLifecycle()

    val count by remember { derivedStateOf { filteredTunings.size } }

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            modifier = Modifier.padding(vertical = 8.dp),
            text = "Tunings",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        SearchBar(
            modifier = Modifier.fillMaxWidth(),
            windowInsets = WindowInsets(top = 0.dp),
            inputField = {
                SearchBarDefaults.InputField(
                    query = searchQuery,
                    onQueryChange = { viewModel.setSearchQuery(it) },
                    onSearch = { },
                    expanded = false,
                    onExpandedChange = { },
                    placeholder = { Text("Search tunings") },
                    leadingIcon = {
                        Icon(Icons.Rounded.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Rounded.Close, contentDescription = "Clear search")
                            }
                        }
                    },
                )
            },
            expanded = false,
            onExpandedChange = { },
        ) {}

        Spacer(modifier = Modifier.height(8.dp))

        FilterChipGroup(
            items = instruments.map { it.name },
            selectedItem = selectedInstrument,
            onItemSelected = { viewModel.setSelectedInstrument(it) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (filteredTunings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (searchQuery.isEmpty() && selectedInstrument == null) {
                        "No tunings available.\nTap + to add one."
                    } else {
                        "No tunings match your search."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)
            ) {
                itemsIndexed(
                    items = filteredTunings,
                    key = { _, tuning -> tuning.tuningId },
                ) { index, tuning ->
                    TuningListItem(
                        tuning = tuning,
                        shapes = ListItemDefaults.segmentedShapes(
                            index = index,
                            count = count
                        ),
                        isSelected = tuning == selectedTuning,
                        onClick = { viewModel.selectTuning(tuning) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ChromaticContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Chromatic mode detects the note automatically.",
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = "No need to select a tuning.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
