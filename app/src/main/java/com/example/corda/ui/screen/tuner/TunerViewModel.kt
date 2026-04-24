package com.example.corda.ui.screen.tuner

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DummyTuning(
    val name: String,
    val instrument: String,
    val sounds: List<String>,
)

class TunerViewModel : ViewModel() {

    val tunings: List<DummyTuning> = listOf(
        DummyTuning("Standard",  "Guitar (6-string)", listOf("E₂", "A₂", "D₃", "G₃", "B₃", "E₄")),
        DummyTuning("Drop D",    "Guitar (6-string)", listOf("D₂", "A₂", "D₃", "G₃", "B₃", "E₄")),
        DummyTuning("Open G",    "Guitar (6-string)", listOf("D₂", "G₂", "D₃", "G₃", "B₃", "D₄")),
        DummyTuning("Open D",    "Guitar (6-string)", listOf("D₂", "A₂", "D₃", "F#₃", "A₃", "D₄")),
        DummyTuning("DADGAD",    "Guitar (6-string)", listOf("D₂", "A₂", "D₃", "G₃", "A₃", "D₄")),
        DummyTuning("Half Step Down", "Guitar (6-string)", listOf("Eb₂", "Ab₂", "Db₃", "Gb₃", "Bb₃", "Eb₄")),
        DummyTuning("Standard",  "Guitar (7-string)", listOf("B₁", "E₂", "A₂", "D₃", "G₃", "B₃", "E₄")),
        DummyTuning("Drop A",    "Guitar (7-string)", listOf("A₁", "E₂", "A₂", "D₃", "G₃", "B₃", "E₄")),
        DummyTuning("Standard",  "Bass (4-string)",   listOf("E₁", "A₁", "D₂", "G₂")),
        DummyTuning("Drop D",    "Bass (4-string)",   listOf("D₁", "A₁", "D₂", "G₂")),
        DummyTuning("Standard",  "Bass (5-string)",   listOf("B₀", "E₁", "A₁", "D₂", "G₂")),
        DummyTuning("Standard",  "Ukulele",           listOf("G₄", "C₄", "E₄", "A₄")),
        DummyTuning("Low G",     "Ukulele",           listOf("G₃", "C₄", "E₄", "A₄")),
    )

    private val _selectedTuning = MutableStateFlow(tunings.first())
    val selectedTuning: StateFlow<DummyTuning> = _selectedTuning.asStateFlow()

    fun selectTuning(tuning: DummyTuning) {
        _selectedTuning.value = tuning
    }
}
