package com.example.corda.ui.screen.inspirations.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.corda.R
import com.example.corda.data.inspirations.model.InspirationAttribute

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun InspirationDetailFields(
    name: String,
    description: String,
    labels: List<String>,
    attributes: List<InspirationAttribute>,
    onCopyAttribute: (String) -> Unit
) {
    Text(
        text = name,
        style = MaterialTheme.typography.headlineMedium
    )

    if (labels.isNotEmpty()) {
        Spacer(modifier = Modifier.height(InspirationFormSpacing.afterTitle))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(InspirationFormSpacing.chipGap)) {
            labels.forEach { label ->
                FilterChip(
                    selected = false,
                    onClick = {},
                    label = { Text(label) }
                )
            }
        }
    }

    if (description.isNotBlank()) {
        Spacer(modifier = Modifier.height(InspirationFormSpacing.section))
        Text(
            text = stringResource(R.string.inspiration_description),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(InspirationFormSpacing.labelToField))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

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
                    isEditing = false,
                    onCopyClick = { onCopyAttribute(attribute.url) }
                )
            }
        }
    }
}
