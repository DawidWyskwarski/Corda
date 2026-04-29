package com.example.corda.data.tuner.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.corda.data.tuner.local.dao.TunerDao
import com.example.corda.data.tuner.local.entities.Instrument
import com.example.corda.data.tuner.local.entities.Sound
import com.example.corda.data.tuner.local.entities.Tuning
import com.example.corda.data.tuner.local.entities.relations.TuningSoundCrossRef

@Database(
    entities = [
        Instrument::class,
        Tuning::class,
        Sound::class,
        TuningSoundCrossRef::class
    ],
    version = 1
)
abstract class TunerDatabase: RoomDatabase() {

    abstract val tunerDao: TunerDao

    companion object {
        @Volatile
        private var INSTANCE: TunerDatabase? = null

        fun getInstance(ctx: Context): TunerDatabase {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    ctx.applicationContext,
                    TunerDatabase::class.java,
                    "tuner_db"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}