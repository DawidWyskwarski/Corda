package com.example.corda.ui.screen.tuner.settings

enum class TuningMode {
    STANDARD,
    CHROMATIC;

    override fun toString(): String {
        return name.lowercase().replaceFirstChar { it.uppercase() }
    }
}