package com.example.corda.ui.screen.tuner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.corda.data.tuner.audio.AudioProcessingService
import com.example.corda.data.tuner.audio.PitchSmoother
import com.example.corda.data.tuner.audio.ToneGenerator
import com.example.corda.data.tuner.local.entities.Sound
import com.example.corda.data.tuner.local.entities.relations.TuningWithInstrumentAndSounds
import com.example.corda.data.tuner.model.NoteReference
import com.example.corda.data.tuner.model.PitchResult
import com.example.corda.data.tuner.repository.TunerRepository
import com.example.corda.ui.screen.tuner.settings.TuningMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.abs

data class TunerUiState(
    val detectedNote: String? = null,
    val detectedFrequency: Float? = null,
    val centsOff: Float? = null,
    val isListening: Boolean = false,
    val focusedSoundIndex: Int? = null,
    val tunedSoundIndices: Set<Int> = emptySet(),
    val autoSelectedSoundIndex: Int? = null,
    val isEarModeEnabled: Boolean = false,
    /** Index of the string chip toggled on in STANDARD ear mode (null = none playing). */
    val earPlayingStandardIndex: Int? = null,
    /** Selected chromatic note in CHROMATIC ear mode (UI model, not persisted). */
    val earSelectedChromaticSound: Sound? = null,
    val isPlayingChromaticNote: Boolean = false,
)

class TunerViewModel(
    private val repository: TunerRepository
) : ViewModel() {

    private val audioService = AudioProcessingService()
    private val pitchSmoother = PitchSmoother()
    private val toneGenerator = ToneGenerator()

    private var pitchCollectionJob: Job? = null
    private var inTuneFrameCount = 0

    val tunings: StateFlow<List<TuningWithInstrumentAndSounds>> = repository
        .getTunings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    private val _selectedTuning = MutableStateFlow<TuningWithInstrumentAndSounds?>(null)

    val selectedTuning: StateFlow<TuningWithInstrumentAndSounds?> = combine(
        _selectedTuning,
        tunings
    ) { selected, tuningList ->
        val stillExists = selected != null && tuningList.any { it.tuningId == selected.tuningId }
        if (stillExists) selected else tuningList.firstOrNull()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    fun selectTuning(tuning: TuningWithInstrumentAndSounds) {
        toneGenerator.stop()
        _selectedTuning.value = tuning
        _tunerUiState.value = TunerUiState()
    }

    fun updateSelectedTuningLastUsed() {
        val currentTuning = _selectedTuning.value ?: tunings.value.firstOrNull()
        currentTuning?.let { tuning ->
            viewModelScope.launch {
                repository.updateTuningLastUsed(tuning.tuningId, System.currentTimeMillis())
            }
        }
    }

    private val _selectedMode = MutableStateFlow(TuningMode.STANDARD)
    val selectedMode: StateFlow<TuningMode> = _selectedMode.asStateFlow()

    fun selectMode(mode: TuningMode) {
        toneGenerator.stop()
        _selectedMode.value = mode
        _tunerUiState.value = TunerUiState()
    }

    private val _tunerUiState = MutableStateFlow(TunerUiState())
    val tunerUiState: StateFlow<TunerUiState> = _tunerUiState.asStateFlow()

    /** Chromatic ladder as [Sound] rows for ear-mode carousel (MIDI id as [Sound.soundId]). */
    val chromaticSoundsForEar: List<Sound> get() = chromaticSounds

    fun onNoteChipClicked(index: Int?) {
        val current = _tunerUiState.value
        val newFocused = if (current.focusedSoundIndex == index) null else index
        inTuneFrameCount = 0
        _tunerUiState.value = current.copy(focusedSoundIndex = newFocused)
    }

    fun setEarModeEnabled(enabled: Boolean) {
        val cur = _tunerUiState.value
        if (cur.isEarModeEnabled == enabled) return

        if (enabled) {
            stopMicrophoneOnly()
            toneGenerator.stop()
            val defaultChromatic = defaultChromaticSound()
            _tunerUiState.value = cur.copy(
                isEarModeEnabled = true,
                detectedNote = null,
                detectedFrequency = null,
                centsOff = null,
                isListening = false,
                earPlayingStandardIndex = null,
                isPlayingChromaticNote = false,
                earSelectedChromaticSound = cur.earSelectedChromaticSound ?: defaultChromatic,
            )
        } else {
            toneGenerator.stop()
            _tunerUiState.value = cur.copy(
                isEarModeEnabled = false,
                earPlayingStandardIndex = null,
                isPlayingChromaticNote = false,
            )
            startListening()
        }
    }

    /** Tap a chip to start its reference tone; tap again (or another chip) to stop / switch. */
    fun onEarModeStandardNoteToggled(index: Int) {
        val tuning = selectedTuning.value ?: return
        val sounds = tuning.sounds
        if (index !in sounds.indices) return
        val cur = _tunerUiState.value
        if (cur.earPlayingStandardIndex == index) {
            toneGenerator.stop()
            _tunerUiState.value = cur.copy(earPlayingStandardIndex = null)
        } else {
            toneGenerator.play(sounds[index].frequency, viewModelScope)
            _tunerUiState.value = cur.copy(earPlayingStandardIndex = index)
        }
    }

    fun onEarChromaticSoundSelected(sound: Sound) {
        val cur = _tunerUiState.value
        _tunerUiState.value = cur.copy(earSelectedChromaticSound = sound)
        if (cur.isPlayingChromaticNote) {
            toneGenerator.play(sound.frequency, viewModelScope)
        }
    }

    fun toggleChromaticPlayback() {
        val cur = _tunerUiState.value
        val sound = cur.earSelectedChromaticSound ?: return
        if (cur.isPlayingChromaticNote) {
            toneGenerator.stop()
            _tunerUiState.value = _tunerUiState.value.copy(isPlayingChromaticNote = false)
        } else {
            toneGenerator.play(sound.frequency, viewModelScope)
            _tunerUiState.value = _tunerUiState.value.copy(isPlayingChromaticNote = true)
        }
    }

    fun startListening() {
        if (_tunerUiState.value.isEarModeEnabled) return
        if (audioService.isListening.value) return

        pitchSmoother.reset()
        inTuneFrameCount = 0
        audioService.start(viewModelScope)

        pitchCollectionJob = viewModelScope.launch {
            audioService.pitchFlow.collect { rawFreq ->
                val smoothedFreq = pitchSmoother.process(rawFreq)
                updateUiFromPitch(smoothedFreq)
            }
        }

        viewModelScope.launch {
            audioService.isListening.collect { listening ->
                _tunerUiState.value = _tunerUiState.value.copy(isListening = listening)
            }
        }
    }

    fun stopListening() {
        pitchCollectionJob?.cancel()
        pitchCollectionJob = null
        audioService.stop()
        pitchSmoother.reset()
        inTuneFrameCount = 0
        toneGenerator.stop()
        _tunerUiState.value = TunerUiState()
    }

    private fun stopMicrophoneOnly() {
        pitchCollectionJob?.cancel()
        pitchCollectionJob = null
        audioService.stop()
        pitchSmoother.reset()
        inTuneFrameCount = 0
        val micOff = _tunerUiState.value
        _tunerUiState.value = micOff.copy(
            detectedNote = null,
            detectedFrequency = null,
            centsOff = null,
            isListening = false,
        )
    }

    private fun updateUiFromPitch(smoothedFreq: Float?) {
        if (_tunerUiState.value.isEarModeEnabled) return

        if (smoothedFreq == null) {
            _tunerUiState.value = _tunerUiState.value.copy(
                detectedNote = null,
                detectedFrequency = null,
                centsOff = null
            )
            inTuneFrameCount = 0
            return
        }

        val tuning = selectedTuning.value
        val current = _tunerUiState.value

        when (_selectedMode.value) {
            TuningMode.CHROMATIC -> {
                val result = NoteReference.findClosestNote(smoothedFreq)
                _tunerUiState.value = current.copy(
                    detectedNote = result.noteName,
                    detectedFrequency = result.frequencyHz,
                    centsOff = result.centsOff
                )
            }

            TuningMode.STANDARD if tuning != null -> {
                val sounds = tuning.sounds

                if (sounds.isEmpty()) return

                val focusedIndex = current.focusedSoundIndex

                if (focusedIndex != null && focusedIndex in sounds.indices) {
                    val targetSound = sounds[focusedIndex]

                    val result = NoteReference.findClosestToSingleTarget(
                        smoothedFreq, targetSound
                    )

                    val newTuned = checkAndMarkTuned(
                        result, focusedIndex, current.tunedSoundIndices
                    )

                    _tunerUiState.value = current.copy(
                        detectedNote = result.noteName,
                        detectedFrequency = result.frequencyHz,
                        centsOff = result.centsOff,
                        tunedSoundIndices = newTuned
                    )
                } else {
                    val result = NoteReference.findClosestNoteInSet(
                        smoothedFreq, sounds
                    )

                    val matchingIndex = sounds.indexOfFirst { it.name == result.noteName }

                    val newTuned = if (matchingIndex >= 0) {
                        checkAndMarkTuned(result, matchingIndex, current.tunedSoundIndices)
                    } else {
                        current.tunedSoundIndices
                    }

                    _tunerUiState.value = current.copy(
                        detectedNote = result.noteName,
                        detectedFrequency = result.frequencyHz,
                        centsOff = result.centsOff,
                        autoSelectedSoundIndex = matchingIndex.takeIf { it >= 0 },
                        tunedSoundIndices = newTuned
                    )
                }
            }
            else -> {}
        }
    }

    private companion object {
        const val IN_TUNE_THRESHOLD_CENTS = 5f
        const val IN_TUNE_FRAMES_REQUIRED = 64

        private val chromaticSounds: List<Sound> by lazy {
            NoteReference.allNotes.map { cn ->
                Sound(soundId = cn.midiNote, name = cn.name, frequency = cn.frequency)
            }
        }

        private fun defaultChromaticSound(): Sound =
            chromaticSounds.firstOrNull { it.name == "A4" }
                ?: chromaticSounds.first()
    }

    private fun checkAndMarkTuned(
        result: PitchResult,
        index: Int,
        currentTuned: Set<Int>
    ): Set<Int> {
        if (abs(result.centsOff) < IN_TUNE_THRESHOLD_CENTS) {
            inTuneFrameCount++
            if (inTuneFrameCount >= IN_TUNE_FRAMES_REQUIRED) {
                return currentTuned + index
            }
        } else {
            inTuneFrameCount = 0
        }
        return currentTuned
    }

    override fun onCleared() {
        super.onCleared()
        pitchCollectionJob?.cancel()
        audioService.stop()
        toneGenerator.release()
    }
}

class TunerViewModelFactory(
    private val repository: TunerRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TunerViewModel::class.java)) {
            return TunerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
