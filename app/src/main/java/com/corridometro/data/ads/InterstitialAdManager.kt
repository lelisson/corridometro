package com.corridometro.data.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

/**
 * Carrega e exibe anúncios intersticiais AdMob (contexto de Application).
 * Sempre chame [preload] após [show] para manter o próximo anúncio pronto.
 */
class InterstitialAdManager(
    private val appContext: Context,
) {
    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false
    private var adUnitId: String? = null

    fun preload(adUnitId: String) {
        if (this.adUnitId != adUnitId) {
            this.adUnitId = adUnitId
            interstitialAd = null
        }
        if (interstitialAd != null || isLoading) return
        isLoading = true
        InterstitialAd.load(
            appContext,
            adUnitId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isLoading = false
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    isLoading = false
                }
            },
        )
    }

    fun show(activity: Activity, onFinished: () -> Unit) {
        val ad = interstitialAd
        val unitId = adUnitId
        if (ad == null) {
            onFinished()
            if (unitId != null) preload(unitId)
            return
        }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                if (unitId != null) preload(unitId)
                onFinished()
            }
        }
        interstitialAd = null
        ad.show(activity)
    }
}
