package com.example.corda.data.tuner.local.entities.relations

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.corda.data.tuner.local.entities.Sound
import com.example.corda.data.tuner.local.entities.Tuning

@Entity (
    foreignKeys = [
        ForeignKey(
            entity = Tuning::class,
            parentColumns = ["id"],
            childColumns = ["tuningId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Sound::class,
            parentColumns = ["id"],
            childColumns = ["soundId"]
        )
    ]
)
data class TuningSoundCrossRef(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val tuningId: Int,
    val soundId: Int
)