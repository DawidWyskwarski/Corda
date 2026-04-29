package com.example.corda.data.tuner.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.corda.data.tuner.local.entities.Instrument
import com.example.corda.data.tuner.local.entities.Sound
import com.example.corda.data.tuner.local.entities.Tuning
import com.example.corda.data.tuner.local.entities.relations.TuningSoundCrossRef
import com.example.corda.data.tuner.local.entities.relations.TuningWithInstrumentAndSounds

@Dao
interface TunerDao {

    // Inserts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstrument(instrument: Instrument): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTuning(tuning: Tuning): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSound(sound: Sound): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTuningSoundCrossRef(tuningSoundCrossRef: TuningSoundCrossRef)

    // Updates
    @Update
    suspend fun updateSound(vararg sound: Sound)

    @Update
    suspend fun updateTuning(vararg tuning: Tuning)

    @Update
    suspend fun updateInstrument(vararg instrument: Instrument)

    @Update
    suspend fun updateTuningSoundCrossRef(vararg tuningSoundCrossRef: TuningSoundCrossRef)

    // Deletes
    @Delete
    suspend fun deleteSound(vararg sound: Sound)

    @Delete
    suspend fun deleteTuning(vararg tuning: Tuning)

    @Delete
    suspend fun deleteInstrument(vararg instrument: Instrument)

    @Delete
    suspend fun deleteTuningSoundCrossRef(vararg tuningSoundCrossRef: TuningSoundCrossRef)

    // Queries
    @Query("""
        SELECT * 
        FROM Instrument
        """)
    suspend fun getInstruments(): List<Instrument>

    @Transaction
    @Query("""
        SELECT 
            Tuning.tuning_id,
            Tuning.name AS tuningName,
            Instrument.name AS instrumentName
        FROM Tuning
        INNER JOIN Instrument ON Tuning.instrument_id = Instrument.instrument_id
        INNER JOIN TuningSoundCrossRef ON Tuning.tuning_id = TuningSoundCrossRef.tuning_id
    """)
    suspend fun getTunings(): List<TuningWithInstrumentAndSounds>

    @Query("""
        SELECT * 
        FROM Sound 
        WHERE Sound.name = "A4"
        """)
    suspend fun getReferencePitch(): Sound
}