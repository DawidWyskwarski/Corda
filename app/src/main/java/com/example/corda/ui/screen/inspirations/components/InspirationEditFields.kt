package com.example.corda.ui.screen.inspirations.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.corda.R
import com.example.corda.data.inspirations.model.InspirationAttribute

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun InspirationEditFields(
    name: String,
    description: String,
    labels: List<String>,
    attributes: List<InspirationAttribute>,
    availableLabels: List<String>,
    showDeleteButton: Boolean,
    actions: InspirationEditActions
) {
    var showLabelPicker by remember { mutableStateOf(false) }

    InspirationTitleTextField(
        value = name,
        onValueChange = actions.onNameChange,
        placeholder = {
            Text(
                text = stringResource(R.string.inspiration_name_hint),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )

    Spacer(modifier = Modifier.height(InspirationFormSpacing.afterTitle))

    FlowRow(horizontalArrangement = Arrangement.spacedBy(InspirationFormSpacing.chipGap)) {
        labels.forEach { label ->
            InputChip(
                selected = true,
                onClick = { actions.onRemoveLabel(label) },
                label = { Text(label) }
            )
        }
        Box {
            FilterChip(
                selected = false,
                onClick = { showLabelPicker = true },
                label = { Text("+") }
            )
            DropdownMenu(
                expanded = showLabelPicker,
                onDismissRequest = { showLabelPicker = false }
            ) {
                val unusedLabels = availableLabels.filter { it !in labels }
                if (unusedLabels.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.inspiration_no_labels_available)) },
                        onClick = { showLabelPicker = false },
                        enabled = false
                    )
                } else {
                    unusedLabels.forEach { label ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                actions.onAddLabel(label)
                                showLabelPicker = false
                            }
                        )
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(InspirationFormSpacing.section))

    Text(
        text = stringResource(R.string.inspiration_description),
        style = MaterialTheme.typography.titleMedium
    )
    Spacer(modifier = Modifier.height(InspirationFormSpacing.labelToField))
    InspirationOutlinedTextField(
        value = description,
        onValueChange = actions.onDescriptionChange,
        placeholder = { Text(stringResource(R.string.inspiration_description_hint)) },
        maxLines = 8,
        modifier = Modifier.height(160.dp)
    )

    if (attributes.isNotEmpty()) {
        Spacer(modifier = Modifier.height(InspirationFormSpacing.section))
        Text(
            text = stringResource(R.string.inspiration_additional_attributes),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(InspirationFormSpacing.attributeGap))
        Column(verticalArrangement = Arrangement.spacedBy(InspirationFormSpacing.attributeGap)) {
            attributes.forEach { attribute ->
                InspirationAttributeItem(
                    attribute = attribute,
                    isEditing = true,
                    onEditClick = { actions.onAttributeEdit(attribute) },
                    onDeleteClick = { actions.onAttributeDelete(attribute.id) }
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(InspirationFormSpacing.section))

    FilledTonalButton(
        onClick = actions.onAddAttribute,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Text(stringResource(R.string.inspiration_add_attribute))
    }

    if (showDeleteButton) {
        Spacer(modifier = Modifier.height(InspirationFormSpacing.section))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Spacer(modifier = Modifier.height(InspirationFormSpacing.beforeDelete))
        OutlinedButton(
            onClick = actions.onDelete,
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.inspiration_delete))
        }
    }
}
