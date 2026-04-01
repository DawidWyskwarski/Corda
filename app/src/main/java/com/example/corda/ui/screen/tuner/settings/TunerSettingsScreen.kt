package com.example.corda.ui.screen.tuner.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.example.corda.ui.components.SimpleSingleChoiceButtonGroup

enum class TuningMode {
    STANDARD,
    CHROMATIC;

    override fun toString(): String {
        return name.lowercase().replaceFirstChar { it.uppercase() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
                },
            )
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

            SimpleSingleChoiceButtonGroup<TuningMode>(
                modifier = Modifier
                    .fillMaxWidth(),
                selectedItem = selectedMode,
                items = modes,
                onItemSelected = {
                    selectedMode = it
                }
            )

            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (selectedMode == TuningMode.STANDARD) {
                    Text(
                        text = "Standard"
                    )
                } else {
                    Text(
                        text = "Chromatic"
                    )
                }
            }
        }
    }
}
