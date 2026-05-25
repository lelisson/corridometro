package com.corridometro.data.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchasesAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class BillingManager(
    context: Context,
) : PurchasesUpdatedListener {

    private val appContext = context.applicationContext
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    private val _planPriceLabels = MutableStateFlow<Map<String, String>>(emptyMap())
    val planPriceLabels: StateFlow<Map<String, String>> = _planPriceLabels.asStateFlow()

    private val _selectedProductId = MutableStateFlow(PREMIUM_MONTHLY_ID)
    val selectedProductId: StateFlow<String> = _selectedProductId.asStateFlow()

    private val _isBillingReady = MutableStateFlow(false)
    val isBillingReady: StateFlow<Boolean> = _isBillingReady.asStateFlow()

    private val _billingMessage = MutableStateFlow<String?>(null)
    val billingMessage: StateFlow<String?> = _billingMessage.asStateFlow()

    private val _isPurchasing = MutableStateFlow(false)
    val isPurchasing: StateFlow<Boolean> = _isPurchasing.asStateFlow()

    private val productDetailsById = mutableMapOf<String, ProductDetails>()
    private var activityRef: WeakReference<Activity>? = null

    private val billingClient: BillingClient = BillingClient.newBuilder(appContext)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build(),
        )
        .build()

    fun bindActivity(activity: Activity?) {
        activityRef = activity?.let { WeakReference(it) }
    }

    fun start() {
        if (billingClient.isReady) {
            onConnected()
            return
        }
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    onConnected()
                } else {
                    _isBillingReady.value = false
                    _billingMessage.value = "Play Store indisponível (${result.debugMessage})"
                }
            }

            override fun onBillingServiceDisconnected() {
                _isBillingReady.value = false
            }
        })
    }

    private fun onConnected() {
        _isBillingReady.value = true
        scope.launch {
            queryProductDetails()
            refreshPurchases()
        }
    }

    fun selectPlan(productId: String) {
        if (productId in PREMIUM_SUBSCRIPTION_IDS) {
            _selectedProductId.value = productId
        }
    }

    fun refreshPurchases() {
        if (!billingClient.isReady) {
            _billingMessage.value = "Play Store ainda não conectou. Tente em instantes."
            return
        }
        scope.launch {
            val params = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
            val result = billingClient.queryPurchasesAsync(params)
            if (result.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                applyPurchases(result.purchasesList)
                _billingMessage.value = if (_isPremium.value) {
                    "Assinatura Premium ativa nesta conta Google Play."
                } else {
                    "Nenhuma assinatura ativa encontrada nesta conta."
                }
            } else {
                _billingMessage.value = "Não foi possível verificar a assinatura."
            }
        }
    }

    fun launchPremiumPurchase(productId: String = _selectedProductId.value) {
        val activity = activityRef?.get()
        if (activity == null) {
            _billingMessage.value = "Não foi possível abrir a compra. Tente de novo."
            return
        }
        val details = productDetailsById[productId]
        if (details == null) {
            _billingMessage.value =
                "Plano ainda não disponível na Play Store. Configure os três produtos na Console."
            scope.launch { queryProductDetails() }
            return
        }
        val offerToken = details.subscriptionOfferDetails?.firstOrNull()?.offerToken
        if (offerToken == null) {
            _billingMessage.value = "Oferta de assinatura não encontrada na Play Store."
            return
        }
        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(details)
                        .setOfferToken(offerToken)
                        .build(),
                ),
            )
            .build()
        _isPurchasing.value = true
        val result = billingClient.launchBillingFlow(activity, flowParams)
        if (result.responseCode != BillingClient.BillingResponseCode.OK) {
            _isPurchasing.value = false
            _billingMessage.value = "Não foi possível iniciar a compra."
        }
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        _isPurchasing.value = false
        when (result.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.let { applyPurchases(it) }
                if (_isPremium.value) {
                    _billingMessage.value = "Premium ativo! Sem anúncios e com benefícios futuros."
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                _billingMessage.value = "Compra cancelada."
            }
            else -> {
                _billingMessage.value = result.debugMessage.ifBlank {
                    "Erro na compra (código ${result.responseCode})"
                }
            }
        }
    }

    fun clearBillingMessage() {
        _billingMessage.value = null
    }

    private suspend fun queryProductDetails() {
        val products = PREMIUM_SUBSCRIPTION_IDS.map { id ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(id)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        }
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(products)
            .build()
        val result = billingClient.queryProductDetails(params)
        if (result.billingResult.responseCode != BillingClient.BillingResponseCode.OK) return

        productDetailsById.clear()
        val prices = mutableMapOf<String, String>()
        result.productDetailsList?.forEach { details ->
            productDetailsById[details.productId] = details
            val formatted = details.subscriptionOfferDetails
                ?.firstOrNull()
                ?.pricingPhases
                ?.pricingPhaseList
                ?.firstOrNull()
                ?.formattedPrice
            if (formatted != null) {
                prices[details.productId] = formatted
            }
        }
        _planPriceLabels.value = prices
    }

    private fun applyPurchases(purchases: List<Purchase>) {
        var active = false
        for (purchase in purchases) {
            val hasPremiumProduct = purchase.products.any { it in PREMIUM_SUBSCRIPTION_IDS }
            if (!hasPremiumProduct) continue
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                active = true
                if (!purchase.isAcknowledged) {
                    acknowledge(purchase)
                }
            }
        }
        _isPremium.value = active
    }

    private fun acknowledge(purchase: Purchase) {
        scope.launch {
            val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            billingClient.acknowledgePurchase(params)
        }
    }
}
