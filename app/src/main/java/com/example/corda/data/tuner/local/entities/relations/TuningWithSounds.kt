package com.example.corda.data.tuner.local.entities.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.corda.data.tuner.local.entities.Sound
import com.example.corda.data.tuner.local.entities.Tuning

data class TuningWithSounds (
    @Embedded val tuning: Tuning,
    @Relation(
        parentColumn = "tuningId",
        entityColumn = "soundId",
        associateBy = Junction(TuningSoundCrossRef::class)
    )
    val sounds: List<Sound>
)
