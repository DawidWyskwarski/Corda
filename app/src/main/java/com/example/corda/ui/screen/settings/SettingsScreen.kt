package com.example.corda.ui.screen.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.corda.R
import com.example.corda.ui.theme.LANGUAGE_EN
import com.example.corda.ui.theme.LANGUAGE_PL

private val screenPadding = 16.dp
private val dividerPadding = Modifier.padding(vertical = 8.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val languageTag by viewModel.language.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings), fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = screenPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            SettingsSectionHeader(stringResource(R.string.settings_display))

            SettingsClickableItem(
                title = stringResource(R.string.dark_mode),
                icon = Icons.Outlined.DarkMode,
                trailingContent = {
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { viewModel.toggleDarkMode(it) }
                    )
                }
            )

//            SettingsClickableItem(
//                title = stringResource(R.string.keep_focus),
//                icon = Icons.Outlined.Visibility,
//                trailingContent = {
//                    Switch(
//                        checked = keepFocus,
//                        onCheckedChange = { viewModel.toggleKeepFocus(it) }
//                    )
//                }
//            )

            HorizontalDivider(modifier = dividerPadding)

            SettingsSectionHeader(stringResource(R.string.settings_calibration))

            OutlinedTextField(
                value = viewModel.frequencyInput,
                onValueChange = { viewModel.updateFrequency(it) },
                label = { Text(stringResource(R.string.base_frequency)) },
                isError = viewModel.isFrequencyError,
                supportingText = {
                    if (viewModel.isFrequencyError) Text(stringResource(R.string.frequency_error))
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    if (viewModel.isFrequencyError) {
                        Icon(
                            Icons.Default.Error,
                            stringResource(R.string.error),
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                },
            )

            HorizontalDivider(modifier = dividerPadding)

            SettingsSectionHeader(stringResource(R.string.settings_localisation))

            SettingsLanguageDropdown(
                selectedLanguageTag = languageTag,
                onLanguageSelected = { viewModel.setLanguage(it) },
            )

//            SettingsDropdown(
//                label = stringResource(R.string.notation),
//                selectedOption = notation,
//                options = listOf(stringResource(R.string.european), stringResource(R.string.american)),
//                onOptionSelected = { viewModel.setNotation(it) }
//            )
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
    )
}

@Composable
fun SettingsClickableItem(
    title: String,
    icon: ImageVector,
    trailingContent: @Composable () -> Unit
) {
    ListItem(
        headlineContent = { Text(title, fontSize = 16.sp) },
        leadingContent = { Icon(icon, contentDescription = null) },
        trailingContent = trailingContent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsLanguageDropdown(
    selectedLanguageTag: String,
    onLanguageSelected: (String) -> Unit,
) {
    val options = remember {
        listOf(
            LANGUAGE_EN to R.string.language_english,
            LANGUAGE_PL to R.string.language_polish,
        )
    }
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = stringResource(
        options.first { it.first == selectedLanguageTag }.second,
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.language)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { (tag, labelRes) ->
                DropdownMenuItem(
                    text = { Text(stringResource(labelRes)) },
                    onClick = {
                        onLanguageSelected(tag)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDropdown(
    label: String,
    selectedOption: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}
