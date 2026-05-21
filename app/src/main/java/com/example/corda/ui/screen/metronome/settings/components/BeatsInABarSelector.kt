package com.example.corda.ui.screen.metronome.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BeatsInABarSelector(
    selectedBeats: Int,
    onBeatsSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedBackground = MaterialTheme.colorScheme.primaryContainer
    val selectedContent = MaterialTheme.colorScheme.onPrimaryContainer
    val unselectedContent = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)

    val listState = rememberLazyListState()
    LazyRow(
        state = listState,
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        items((1..12).toList()) { beat ->
            val isSelected = beat == selectedBeats
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) selectedBackground else Color.Transparent)
                    .clickable { onBeatsSelected(beat) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "$beat",
                    fontSize = 22.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) selectedContent else unselectedContent,
                )
            }
        }
    }
}