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
import com.example.corda.ui.components.NavigationPill

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TunerScreen(
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit,
    openSettings: () -> Unit
) {
    var isEarModeEnabled by remember{ mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    NavigationPill(
                        text = "A standard",
                        supportingText = "7 string guitar",
                        onClick = openSettings
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = openDrawer
                    ) {
                        Icon(
                            Icons.Rounded.Menu,
                            null
                        )
                    }
                },
                actions = {
                    IconToggleButton(
                        checked = isEarModeEnabled,
                        onCheckedChange = {
                            isEarModeEnabled = it
                        }
                    ) {
                        if (isEarModeEnabled) {
                            Icon(
                                Icons.AutoMirrored.Rounded.VolumeUp,
                                "Disable ear mode"
                            )
                        } else {
                            Icon(
                                Icons.AutoMirrored.Rounded.VolumeOff,
                                "Enable ear mode"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Tuner Screen"
            )
        }
    }
}