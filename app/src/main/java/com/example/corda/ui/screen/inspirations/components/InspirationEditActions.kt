package com.example.corda.ui.screen.inspirations.components

import com.example.corda.data.inspirations.model.InspirationAttribute

data class InspirationEditActions(
    val onNameChange: (String) -> Unit,
    val onDescriptionChange: (String) -> Unit,
    val onRemoveLabel: (String) -> Unit,
    val onAddLabel: (String) -> Unit,
    val onAttributeEdit: (InspirationAttribute) -> Unit,
    val onAttributeDelete: (String) -> Unit,
    val onAddAttribute: () -> Unit,
    val onDelete: () -> Unit
)
