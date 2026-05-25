package com.corridometro.ui.ads

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.corridometro.CorridometroApp
import com.corridometro.R
import com.corridometro.data.ads.hasRealAdMobConfig
import com.corridometro.data.ads.isRealInterstitialUnitId
import com.corridometro.util.findActivity

/**
 * Pré-carrega intersticial para usuários gratuitos (Premium ignora anúncios).
 */
@Composable
fun PreloadInterstitialEffect(isPremium: Boolean) {
    val context = LocalContext.current
    val unitId = stringResource(R.string.admob_interstitial_unit_id)
    LaunchedEffect(isPremium, unitId) {
        if (!isPremium && context.hasRealAdMobConfig() && isRealInterstitialUnitId(unitId)) {
            (context.applicationContext as CorridometroApp)
                .interstitialAdManager
                .preload(unitId)
        }
    }
}

/**
 * Exibe intersticial após salvar jornada ([journeySavedAdSignal] incrementa no ViewModel).
 */
@Composable
fun JourneySavedInterstitialEffect(
    isPremium: Boolean,
    journeySavedAdSignal: Int,
) {
    val context = LocalContext.current
    var lastHandledSignal by remember { mutableIntStateOf(0) }
    LaunchedEffect(isPremium, journeySavedAdSignal) {
        if (
            isPremium ||
            journeySavedAdSignal <= lastHandledSignal ||
            !context.hasRealAdMobConfig()
        ) {
            return@LaunchedEffect
        }
        lastHandledSignal = journeySavedAdSignal
        val activity = context.findActivity() ?: return@LaunchedEffect
        (context.applicationContext as CorridometroApp)
            .interstitialAdManager
            .show(activity) { }
    }
}

/** Executa [onContinue] após intersticial (ou imediatamente se Premium / sem Activity). */
fun runWithOptionalInterstitial(
    context: Context,
    isPremium: Boolean,
    onContinue: () -> Unit,
) {
    if (isPremium || !context.hasRealAdMobConfig()) {
        onContinue()
        return
    }
    val activity = context.findActivity()
    if (activity == null) {
        onContinue()
        return
    }
    (context.applicationContext as CorridometroApp)
        .interstitialAdManager
        .show(activity, onContinue)
}
