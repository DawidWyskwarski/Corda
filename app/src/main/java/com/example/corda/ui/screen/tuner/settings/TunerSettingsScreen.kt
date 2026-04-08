package com.example.corda.ui.screen.tuner.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.corda.ui.components.FABMenu
import com.example.corda.ui.components.FABMenuItem
import com.example.corda.ui.components.SimpleSingleChoiceButtonGroup

enum class TuningMode {
    STANDARD,
    CHROMATIC;

    override fun toString(): String {
        return name.lowercase().replaceFirstChar { it.uppercase() }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TunerSettingsScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    var selectedMode by remember { mutableStateOf(TuningMode.STANDARD) }
    val modes = listOf(
        TuningMode.STANDARD,
        TuningMode.CHROMATIC
    )

    var searchQuery by remember { mutableStateOf("") }

    var isFabMenuOpen by remember { mutableStateOf(false) }

    val fabMenuItems = listOf(
        FABMenuItem(
            Icons.AutoMirrored.Rounded.QueueMusic,
            "New custom tuning",
            { isFabMenuOpen = false } // TODO change this to actually do things
        ),
        FABMenuItem(
            Icons.Rounded.Piano,
            "Manage Instruments",
            { isFabMenuOpen = false } // TODO change this to actually do things
        )
    )

    BackHandler(isFabMenuOpen) { isFabMenuOpen = false }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Tuner Settings") },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            null
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedMode == TuningMode.STANDARD) {
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
                modifier = Modifier
                    .fillMaxWidth(),
                selectedItem = selectedMode,
                items = modes,
                onItemSelected = {
                    selectedMode = it
                }
            )

            if (selectedMode == TuningMode.CHROMATIC) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Chromatic mode detects the note automatically.",
                        style = MaterialTheme.typography.bodyMediumEmphasized,
                    )

                    Text(
                        text = "No need to select a tuning.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = "Tunings",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                SearchBar(
                    modifier = Modifier
                        .fillMaxWidth(),
                    windowInsets = WindowInsets(top = 0.dp),
                    inputField = {
                        SearchBarDefaults.InputField(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                            onSearch = { },
                            expanded = false,
                            onExpandedChange = { },
                            placeholder = { Text("Search tunings") },
                            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Rounded.Close, contentDescription = null)
                                    }
                                }
                            },
                        )
                    },
                    expanded = false,
                    onExpandedChange = { },
                ) {
                }
            }
        }
    }
}
