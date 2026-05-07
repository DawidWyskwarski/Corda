package com.example.corda.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.corda.data.tuner.local.entities.Sound

@Composable
fun TuningSoundGrid(
    sounds: List<Sound>,
    onNoteSelected: (Sound?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedNote by remember { mutableStateOf<Sound?>(null) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 32.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(
            items = sounds,
            key = { it.soundId },
        ) { sound ->
            TuningNoteChip(
                note = sound.name,
                isSelected = selectedNote == sound,
                onClick = {
                    selectedNote = if (selectedNote == sound) null else sound
                    onNoteSelected(selectedNote)
                },
            )
        }
    }
}