package com.example.corda.data.tuner.local.entities.relations

import androidx.room.ColumnInfo
import androidx.room.Junction
import androidx.room.Relation
import com.example.corda.data.tuner.local.entities.Sound

data class TuningWithInstrumentAndSounds (
    @ColumnInfo(name = "tuning_id")
    val tuningId: Int,
    val tuningName: String,
    val instrumentName: String,
    @ColumnInfo(name = "last_used")
    val lastUsed: Long,
    @Relation(
        parentColumn = "tuning_id",
        entityColumn = "sound_id",
        associateBy = Junction(TuningSoundCrossRef::class)
    )
    val sounds: List<Sound>
)
