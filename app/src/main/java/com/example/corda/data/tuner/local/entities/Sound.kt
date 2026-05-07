package com.example.corda.data.tuner.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Sound (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "sound_id")
    val soundId: Int = 0,
    val name: String,
    var frequency: Float
)