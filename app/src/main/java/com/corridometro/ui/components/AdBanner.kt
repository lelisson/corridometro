package com.corridometro.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.corridometro.data.ads.hasRealAdMobConfig
import com.corridometro.data.ads.isRealBannerUnitId
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun AdBanner(
    adUnitId: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    if (!context.hasRealAdMobConfig() || !isRealBannerUnitId(adUnitId)) return
    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        factory = {
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                this.adUnitId = adUnitId
                loadAd(AdRequest.Builder().build())
            }
        },
        update = { view ->
            if (view.adUnitId != adUnitId) {
                view.adUnitId = adUnitId
                view.loadAd(AdRequest.Builder().build())
            }
        },
    )
}
