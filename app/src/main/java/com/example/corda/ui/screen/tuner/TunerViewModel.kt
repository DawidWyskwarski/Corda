package com.example.corda.ui.screen.tuner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.corda.data.tuner.local.entities.Instrument
import com.example.corda.data.tuner.local.entities.relations.TuningWithInstrumentAndSounds
import com.example.corda.data.tuner.repository.TunerRepository
import com.example.corda.ui.screen.tuner.settings.TuningMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TunerViewModel(
    private val repository: TunerRepository
) : ViewModel() {

    val instruments: StateFlow<List<Instrument>> = repository
        .getInstruments()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val tunings: StateFlow<List<TuningWithInstrumentAndSounds>> = repository
        .getTunings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private val _selectedInstrument = MutableStateFlow<String?>(null)
    val selectedInstrument: StateFlow<String?> = _selectedInstrument.asStateFlow()

    fun setSelectedInstrument(instrument: String?) {
        _selectedInstrument.value = instrument
    }

    val filteredTunings: StateFlow<List<TuningWithInstrumentAndSounds>> = combine(
        tunings,
        _searchQuery,
        _selectedInstrument
    ) { tuningList, query, instrument ->
        tuningList.filter { tuning ->
            val matchesInstrument = instrument == null ||
                    tuning.instrumentName == instrument
            val matchesQuery = query.isEmpty() ||
                    tuning.tuningName.contains(query, ignoreCase = true)
            matchesInstrument && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    private val _selectedTuning = MutableStateFlow<TuningWithInstrumentAndSounds?>(null)

    val selectedTuning: StateFlow<TuningWithInstrumentAndSounds?> = combine(
        _selectedTuning,
        tunings
    ) { selected, tuningList ->
        selected ?: tuningList.firstOrNull()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    fun selectTuning(tuning: TuningWithInstrumentAndSounds) {
        _selectedTuning.value = tuning
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
        _selectedMode.value = mode
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
