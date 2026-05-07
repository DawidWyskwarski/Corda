package com.example.corda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.corda.ui.CordaApp
import com.example.corda.ui.screen.tuner.TunerViewModelFactory
import com.example.corda.ui.theme.CordaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val tunerViewModelFactory = TunerViewModelFactory(
            (application as CordaApplication).tunerRepository
        )

        setContent {
            CordaTheme {
                CordaApp(
                    tunerViewModelFactory = tunerViewModelFactory
                )
            }
        }
    }
}
