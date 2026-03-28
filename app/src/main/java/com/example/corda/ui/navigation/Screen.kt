package com.example.corda.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed class Screen: NavKey {
    @Serializable
    data object Tuner: Screen()
    @Serializable
    data object TunerSettings: Screen()
    @Serializable
    data object Metronome: Screen()
    @Serializable
    data object MetronomeSettings: Screen()
    @Serializable
    data object Inspirations: Screen()
    @Serializable
    data object Settings: Screen()
    @Serializable
    data object Help: Screen()
}