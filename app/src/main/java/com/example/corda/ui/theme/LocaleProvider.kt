package com.example.corda.ui.theme

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

const val LANGUAGE_EN = "en"
const val LANGUAGE_PL = "pl"

fun normalizeLanguageTag(stored: String?): String = when (stored) {
    LANGUAGE_PL, "Polski" -> LANGUAGE_PL
    else -> LANGUAGE_EN
}

@Composable
fun ProvideAppLocale(
    languageTag: String,
    content: @Composable () -> Unit,
) {
    val baseContext = LocalContext.current
    val localizedConfiguration = remember(languageTag) {
        Configuration(baseContext.resources.configuration).apply {
            setLocale(Locale.forLanguageTag(languageTag))
        }
    }
    val localizedContext = remember(languageTag) {
        baseContext.createConfigurationContext(localizedConfiguration)
    }

    CompositionLocalProvider(
        LocalContext provides localizedContext,
        LocalConfiguration provides localizedConfiguration,
    ) {
        content()
    }
}
