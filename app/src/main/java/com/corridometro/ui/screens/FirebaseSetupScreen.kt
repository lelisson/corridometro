package com.corridometro.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.corridometro.data.auth.FirebaseSetupStep
import com.corridometro.ui.theme.Border
import com.corridometro.ui.theme.Primary
import com.corridometro.ui.theme.Surface as SurfaceColor
import com.corridometro.ui.theme.TextSecondary

@Composable
fun FirebaseSetupScreen(
    applicationId: String,
    steps: List<FirebaseSetupStep>,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scroll = rememberScrollState()
    val doneInApp = steps.count { it.isVerifiedInApp && it.isComplete }
    val totalInApp = steps.count { it.isVerifiedInApp }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .padding(20.dp),
    ) {
        Text(
            text = "Configurar Firebase",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "O app com login precisa do Firebase configurado uma vez. Depois disso, o motorista so toca em Continuar com Google.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )
        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = SurfaceColor,
            border = androidx.compose.foundation.BorderStroke(1.dp, Border),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Pacote deste APK", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelLarge)
                Text(
                    text = applicationId,
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Primary,
                )
            }
        }

        if (totalInApp > 0) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No APK: $doneInApp de $totalInApp verificacoes OK",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        steps.forEach { step ->
            SetupStepRow(
                step = step,
                onOpenConsole = step.consoleUrl?.let { url ->
                    {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    }
                },
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://console.firebase.google.com/")),
                )
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Abrir Firebase Console")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Documentacao no repositorio: docs/guides/integrations/firebase-login.md",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
        )
    }
}

@Composable
private fun SetupStepRow(
    step: FirebaseSetupStep,
    onOpenConsole: (() -> Unit)?,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        color = SurfaceColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, Border),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(
                    imageVector = if (step.isComplete) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (step.isComplete) Primary else TextSecondary,
                    modifier = Modifier.size(22.dp),
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${step.id}. ${step.title}",
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Text(
                        text = step.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                    if (!step.isVerifiedInApp) {
                        Text(
                            text = "Feito no Firebase Console",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                }
            }
            if (onOpenConsole != null) {
                TextButton(
                    onClick = onOpenConsole,
                    modifier = Modifier.padding(top = 4.dp),
                ) {
                    Text("Abrir no Console")
                }
            }
        }
    }
}
