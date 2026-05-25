package com.corridometro.ui.mockup.appa

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.corridometro.ui.CorridometroViewModel
import com.corridometro.ui.components.SubscriptionPlansSection

/** Tela cheia com os planos de assinatura (aberta pelo Menu). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppASubscriptionsOverlay(
    viewModel: CorridometroViewModel,
    onClose: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    BackHandler(onBack = onClose)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Planos de assinatura", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        ) {
            item {
                SubscriptionPlansSection(
                    isPremium = uiState.isPremium,
                    plans = uiState.subscriptionPlans,
                    selectedProductId = uiState.selectedSubscriptionProductId.orEmpty(),
                    isBillingReady = uiState.isBillingReady,
                    isPurchasing = uiState.isPurchasing,
                    billingMessage = uiState.billingMessage,
                    onSelectPlan = viewModel::selectSubscriptionPlan,
                    onSubscribe = viewModel::purchasePremium,
                    onRestore = viewModel::restorePremium,
                )
            }
        }
    }
}
