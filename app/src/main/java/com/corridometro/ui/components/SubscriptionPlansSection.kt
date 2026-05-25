package com.corridometro.ui.components



import androidx.compose.foundation.BorderStroke

import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Button

import androidx.compose.material3.ButtonDefaults

import androidx.compose.material3.HorizontalDivider

import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.OutlinedButton

import androidx.compose.material3.Surface

import androidx.compose.material3.Text

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier

import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp

import com.corridometro.data.billing.PremiumPlan

import com.corridometro.ui.theme.AppColors



data class SubscriptionPlanUi(

    val plan: PremiumPlan,

    val priceLabel: String,

)



@Composable

fun SubscriptionPlansSection(

    isPremium: Boolean,

    plans: List<SubscriptionPlanUi>,

    selectedProductId: String,

    isBillingReady: Boolean,

    isPurchasing: Boolean,

    billingMessage: String?,

    onSelectPlan: (String) -> Unit,

    onSubscribe: () -> Unit,

    onRestore: () -> Unit,

    modifier: Modifier = Modifier,

) {

    DashboardContentBlock(

        title = "Planos de assinatura",

        subtitle = if (isPremium) {

            "Premium ativo nesta conta Google Play"

        } else {

            "Sem anúncios e benefícios futuros. Cobrança pela Google Play."

        },

        modifier = modifier,

    ) {

        if (isPremium) {

            Text(

                text = "Você já tem acesso Premium. Novos recursos serão liberados automaticamente.",

                style = MaterialTheme.typography.bodyMedium,

                color = AppColors.onSurfaceVariant(),

            )

        } else {

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                plans.forEach { item ->

                    PlanOptionRow(

                        plan = item,

                        selected = item.plan.productId == selectedProductId,

                        onSelect = { onSelectPlan(item.plan.productId) },

                    )

                }

            }



            val selected = plans.firstOrNull { it.plan.productId == selectedProductId }

            Button(

                onClick = onSubscribe,

                enabled = isBillingReady && !isPurchasing && selected != null,

                modifier = Modifier

                    .fillMaxWidth()

                    .padding(top = 12.dp),

                colors = ButtonDefaults.buttonColors(

                    containerColor = AppColors.primary(),

                    contentColor = AppColors.onPrimary(),

                ),

            ) {

                val price = selected?.priceLabel ?: ""

                Text(

                    if (isPurchasing) "Abrindo Play Store..." else "Assinar ${selected?.plan?.title ?: "Premium"} — $price",

                )

            }



            OutlinedButton(

                onClick = onRestore,

                enabled = isBillingReady && !isPurchasing,

                modifier = Modifier.fillMaxWidth(),

            ) {

                Text("Restaurar assinatura", color = AppColors.primary())

            }

        }



        HorizontalDivider(

            color = AppColors.outline(),

            modifier = Modifier.padding(vertical = 14.dp),

        )



        Text(

            text = "Renovação da assinatura",

            style = MaterialTheme.typography.titleSmall,

            fontWeight = FontWeight.SemiBold,

            color = AppColors.onSurface(),

        )

        Text(

            text = "A renovação é automática pela Google Play no fim de cada período (mês, semestre ou ano), " +

                "usando o plano que você escolheu. Cancele quando quiser em " +

                "Play Store → Pagamentos e assinaturas → Assinaturas.",

            style = MaterialTheme.typography.bodySmall,

            color = AppColors.onSurfaceVariant(),

            modifier = Modifier.padding(top = 6.dp),

        )



        billingMessage?.let { msg ->

            Text(

                text = msg,

                style = MaterialTheme.typography.bodySmall,

                color = AppColors.primary(),

                modifier = Modifier.padding(top = 10.dp),

            )

        }

    }

}



@Composable

private fun PlanOptionRow(

    plan: SubscriptionPlanUi,

    selected: Boolean,

    onSelect: () -> Unit,

) {

    Surface(

        modifier = Modifier

            .fillMaxWidth()

            .clickable(onClick = onSelect),

        shape = MaterialTheme.shapes.medium,

        color = if (selected) AppColors.primaryContainer() else AppColors.surfaceVariant(),

        border = BorderStroke(

            width = if (selected) 2.dp else 1.dp,

            color = if (selected) AppColors.primary() else AppColors.outline(),

        ),

    ) {

        Row(

            modifier = Modifier

                .fillMaxWidth()

                .padding(14.dp),

            verticalAlignment = Alignment.CenterVertically,

            horizontalArrangement = Arrangement.SpaceBetween,

        ) {

            Column(modifier = Modifier.weight(1f)) {

                Text(

                    text = plan.plan.title,

                    fontWeight = FontWeight.SemiBold,

                    style = MaterialTheme.typography.titleSmall,

                    color = AppColors.onSurface(),

                )

                Text(

                    text = plan.plan.billingPeriodLabel,

                    style = MaterialTheme.typography.bodySmall,

                    color = AppColors.onSurfaceVariant(),

                )

                plan.plan.savingsHint?.let { hint ->

                    Text(

                        text = hint,

                        style = MaterialTheme.typography.labelMedium,

                        color = AppColors.primary(),

                        modifier = Modifier.padding(top = 2.dp),

                    )

                }

            }

            Column(horizontalAlignment = Alignment.End) {

                Text(

                    text = plan.priceLabel,

                    fontWeight = FontWeight.Bold,

                    color = AppColors.primary(),

                    style = MaterialTheme.typography.titleMedium,

                )

            }

        }

    }

}


