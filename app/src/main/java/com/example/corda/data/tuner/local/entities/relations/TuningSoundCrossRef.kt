package com.example.corda.data.tuner.local.entities.relations

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.corda.data.tuner.local.entities.Sound
import com.example.corda.data.tuner.local.entities.Tuning

@Entity (
    foreignKeys = [
        ForeignKey(
            entity = Tuning::class,
            parentColumns = ["tuning_id"],
            childColumns = ["tuning_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Sound::class,
            parentColumns = ["sound_id"],
            childColumns = ["sound_id"]
        )
    ]
)
data class TuningSoundCrossRef(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "tuning_id")
    val tuningId: Int,
    @ColumnInfo(name = "sound_id")
    val soundId: Int
)