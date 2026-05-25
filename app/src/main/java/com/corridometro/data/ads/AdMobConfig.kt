package com.corridometro.data.ads

import android.content.Context
import com.corridometro.R

private const val TEST_APP_ID = "ca-app-pub-3940256099942544~3347511713"
private const val TEST_BANNER_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
private const val TEST_INTERSTITIAL_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

data class AdMobIds(
    val appId: String,
    val bannerUnitId: String,
    val interstitialUnitId: String,
)

fun Context.adMobIds(): AdMobIds = AdMobIds(
    appId = getString(R.string.admob_app_id).trim(),
    bannerUnitId = getString(R.string.admob_banner_unit_id).trim(),
    interstitialUnitId = getString(R.string.admob_interstitial_unit_id).trim(),
)

fun Context.hasRealAdMobConfig(): Boolean {
    val ids = adMobIds()
    return isRealAdMobAppId(ids.appId) &&
        isRealBannerUnitId(ids.bannerUnitId) &&
        isRealInterstitialUnitId(ids.interstitialUnitId)
}

fun isRealAdMobAppId(appId: String): Boolean =
    appId.isNotBlank() && appId != TEST_APP_ID

fun isRealBannerUnitId(adUnitId: String): Boolean =
    adUnitId.isNotBlank() && adUnitId != TEST_BANNER_UNIT_ID

fun isRealInterstitialUnitId(adUnitId: String): Boolean =
    adUnitId.isNotBlank() && adUnitId != TEST_INTERSTITIAL_UNIT_ID
