package com.example.corda.data.tuner.local

import android.content.ContentValues
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.corda.data.tuner.local.dao.TunerDao
import com.example.corda.data.tuner.local.entities.Instrument
import com.example.corda.data.tuner.local.entities.Sound
import com.example.corda.data.tuner.local.entities.Tuning
import com.example.corda.data.tuner.local.entities.relations.TuningSoundCrossRef
import kotlin.math.pow

@Database(
    entities = [
        Instrument::class,
        Tuning::class,
        Sound::class,
        TuningSoundCrossRef::class
    ],
    version = 1
)
abstract class TunerDatabase : RoomDatabase() {

    abstract val tunerDao: TunerDao

    companion object {

        private val NOTE_NAMES = listOf(
            "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"
        )

        private const val REFERENCE_FREQUENCY = 440.0
        private const val REFERENCE_MIDI_NOTE = 69 // A4

        private fun midiNote(octave: Int, noteIndex: Int) = (octave + 1) * 12 + noteIndex

        private fun frequencyFromMidi(midiNote: Int): Float =
            (REFERENCE_FREQUENCY * 2.0.pow((midiNote - REFERENCE_MIDI_NOTE) / 12.0)).toFloat()

        @Volatile
        private var INSTANCE: TunerDatabase? = null

        fun getInstance(ctx: Context): TunerDatabase {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    ctx.applicationContext,
                    TunerDatabase::class.java,
                    "tuner_db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        db.beginTransaction()
                        try {
                            populateSounds(db)
                            populateInstrumentsAndTunings(db)
                            db.setTransactionSuccessful()
                        } finally {
                            db.endTransaction()
                        }
                    }
                }).build().also {
                    INSTANCE = it
                }
            }
        }

        // Sounds
        private fun populateSounds(db: SupportSQLiteDatabase) {
            for (octave in 0..8) {
                for ((noteIndex, noteName) in NOTE_NAMES.withIndex()) {
                    val frequency = frequencyFromMidi(midiNote(octave, noteIndex))
                    db.insert("Sound", 0, ContentValues().apply {
                        put("name", "$noteName$octave")
                        put("frequency", frequency)
                    })
                }
            }
        }

        // Instruments and Tunings
        private fun populateInstrumentsAndTunings(db: SupportSQLiteDatabase) {
            val guitar6 = insertInstrument(db, name = "Guitar (6-string)", soundsCount = 6)
            val guitar7 = insertInstrument(db, name = "Guitar (7-string)", soundsCount = 7)
            val bass4   = insertInstrument(db, name = "Bass (4-string)",   soundsCount = 4)
            val bass5   = insertInstrument(db, name = "Bass (5-string)",   soundsCount = 5)

            // Guitar 6-string
            insertTuningWithSounds(db, "Standard",   guitar6, listOf("E2", "A2", "D3", "G3", "B3", "E4"))
            insertTuningWithSounds(db, "Drop D",     guitar6, listOf("D2", "A2", "D3", "G3", "B3", "E4"))
            insertTuningWithSounds(db, "D Standard", guitar6, listOf("D2", "G2", "C3", "F3", "A3", "D4"))

            // Guitar 7-string
            insertTuningWithSounds(db, "Standard",   guitar7, listOf("B1", "E2", "A2", "D3", "G3", "B3", "E4"))
            insertTuningWithSounds(db, "Drop A",     guitar7, listOf("A1", "E2", "A2", "D3", "G3", "B3", "E4"))
            insertTuningWithSounds(db, "A Standard", guitar7, listOf("A1", "D2", "G2", "C3", "F3", "A3", "D4"))

            // Bass 4-string
            insertTuningWithSounds(db, "Standard",   bass4, listOf("E1", "A1", "D2", "G2"))
            insertTuningWithSounds(db, "D Standard", bass4, listOf("D1", "G1", "C2", "F2"))

            // Bass 5-string
            insertTuningWithSounds(db, "Standard",   bass5, listOf("B0", "E1", "A1", "D2", "G2"))
            insertTuningWithSounds(db, "A Standard", bass5, listOf("A0", "D1", "G1", "C2", "F2"))
        }

        // Helpers
        private fun insertInstrument(db: SupportSQLiteDatabase, name: String, soundsCount: Int): Long =
            db.insert("Instrument", 0, ContentValues().apply {
                put("name", name)
                put("sounds_count", soundsCount)
            })

        private fun insertTuningWithSounds(
            db: SupportSQLiteDatabase,
            name: String,
            instrumentId: Long,
            noteNames: List<String>
        ) {
            val tuningId = db.insert("Tuning", 0, ContentValues().apply {
                put("name", name)
                put("instrument_id", instrumentId)
                put("last_used", 0L)
            })

            noteNames.forEach { noteName ->
                val soundId = querySoundId(db, noteName)
                checkNotNull(soundId) { "Sound '$noteName' not found — check the note name or the seeded octave range." }
                db.insert("TuningSoundCrossRef", 0, ContentValues().apply {
                    put("tuning_id", tuningId)
                    put("sound_id", soundId)
                })
            }
        }

        /**
         * Returns the sound_id for a given note name (e.g. "E2"), or null if not found.
         */
        private fun querySoundId(db: SupportSQLiteDatabase, noteName: String): Long? {
            val cursor = db.query("SELECT sound_id FROM Sound WHERE name = ?", arrayOf(noteName))
            return if (cursor.moveToFirst()) {
                cursor.getLong(0).also { cursor.close() }
            } else {
                cursor.close()
                null
            }
        }
    }
}

