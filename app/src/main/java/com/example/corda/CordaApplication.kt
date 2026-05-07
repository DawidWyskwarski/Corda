package com.example.corda

import android.app.Application
import com.example.corda.data.tuner.local.TunerDatabase
import com.example.corda.data.tuner.repository.TunerRepository
import kotlin.getValue

class CordaApplication : Application() {

    val tunerRepository: TunerRepository by lazy {
        TunerRepository(TunerDatabase.getInstance(this).tunerDao)
    }
}

