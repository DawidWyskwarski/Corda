package com.example.corda.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Help
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.corda.ui.navigation.Screen

private data class NavigationItem(
    val icon: ImageVector,
    val label: String,
    val screen: Screen
)

/**
 * Contents of the drawer menu
 *
 * @param currentScreen The currently selected screen. Used to highlight the corresponding item.
 * @param onScreenSelected Callback to invoke when a screen is selected.
 */
@Composable
fun DrawerMenuContent(
    modifier: Modifier = Modifier,
    currentScreen: Screen,
    onScreenSelected: (Screen) -> Unit
) {
    val tools = remember {
        listOf(
            NavigationItem(
                icon = Icons.Rounded.MusicNote,
                label = "Tuner",
                screen = Screen.Tuner
            ),
            NavigationItem(
                icon = Icons.Rounded.Speed,
                label = "Metronome",
                screen = Screen.Metronome
            ),
            NavigationItem(
                icon = Icons.AutoMirrored.Rounded.QueueMusic,
                label = "Inspirations",
                screen = Screen.Inspirations
            )
        )
    }

    val utilities = remember{
        listOf(
            NavigationItem(
                icon = Icons.Rounded.Settings,
                label = "Settings",
                screen = Screen.Settings
            ),
            NavigationItem(
                icon = Icons.AutoMirrored.Rounded.Help,
                label = "Help & feedback",
                screen = Screen.Help
            )
        )
    }

    ModalDrawerSheet(
        modifier = modifier,
        windowInsets = DrawerDefaults.windowInsets
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // LOGO SECTION

            // Change later to something more interesting
            // Something with a logo for example
            Spacer(Modifier.height(12.dp))
            Text(
                modifier = Modifier
                    .padding(16.dp),
                text = "Corda",
                style = MaterialTheme.typography.headlineMedium
            )

            // TOOLS SECTION
            Text(
                text = "Tools",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
                color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.7f)

            )

            tools.forEach { tool ->
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = tool.icon,
                            contentDescription = null
                        )
                    },
                    label = { Text(tool.label) },
                    selected = currentScreen == tool.screen,
                    onClick = {
                        onScreenSelected(tool.screen)
                    }
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // UTILITIES SECTION
            utilities.forEach { utility ->
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = utility.icon,
                            contentDescription = null
                        )
                    },
                    label = { Text(utility.label) },
                    selected = currentScreen == utility.screen,
                    onClick = {
                        onScreenSelected(utility.screen)
                    }
                )
            }
        }
    }
}
