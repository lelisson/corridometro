package com.corridometro

import android.app.Application
import com.corridometro.R
import com.corridometro.data.CorridometroRepository
import com.corridometro.data.ads.hasRealAdMobConfig
import com.corridometro.data.ads.InterstitialAdManager
import com.corridometro.data.auth.GoogleAuthManager
import com.corridometro.data.billing.BillingManager
import com.corridometro.data.local.AppDatabase
import com.corridometro.data.remote.CloudSyncService
import com.corridometro.data.settings.AppSettings
import com.google.android.gms.ads.MobileAds

class CorridometroApp : Application() {
    val appSettings by lazy { AppSettings(this) }
    val database by lazy { AppDatabase.get(this) }
    val googleAuth by lazy { GoogleAuthManager(this) }
    val billingManager by lazy { BillingManager(this) }
    val interstitialAdManager by lazy { InterstitialAdManager(this) }
    val cloudSync by lazy { CloudSyncService(database, googleAuth) }
    val repository by lazy { CorridometroRepository(database, cloudSync) }

    override fun onCreate() {
        super.onCreate()
        if (hasRealAdMobConfig()) {
            MobileAds.initialize(this) {}
            interstitialAdManager.preload(getString(R.string.admob_interstitial_unit_id))
        }
        billingManager.start()
    }
}
