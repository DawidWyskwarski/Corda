package com.example.corda.ui.screen.inspirations.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun InspirationFormTopBar(
    isEditing: Boolean,
    onBack: () -> Unit,
    onEdit: (() -> Unit)?,
    onSave: (() -> Unit)?,
    saveEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        InspirationOverlayIconButton(
            onClick = onBack,
            icon = Icons.AutoMirrored.Rounded.ArrowBack,
            contentDescription = null
        )

        Spacer(modifier = Modifier.weight(1f))

        when {
            isEditing && onSave != null -> {
                InspirationSavePill(
                    onClick = onSave,
                    enabled = saveEnabled
                )
            }
            !isEditing && onEdit != null -> {
                InspirationOverlayIconButton(
                    onClick = onEdit,
                    icon = Icons.Outlined.Edit,
                    contentDescription = null
                )
            }
        }
    }
}
