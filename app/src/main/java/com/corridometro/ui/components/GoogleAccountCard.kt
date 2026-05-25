package com.corridometro.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.corridometro.ui.CorridometroViewModel
import com.corridometro.ui.theme.Border
import com.corridometro.ui.theme.Primary
import com.corridometro.ui.theme.Surface as SurfaceColor
import com.corridometro.ui.theme.TextSecondary

@Composable
fun GoogleAccountCard(
    viewModel: CorridometroViewModel,
    cloudConfigured: Boolean,
    signedInEmail: String?,
    syncMessage: String?,
    isSyncing: Boolean,
    modifier: Modifier = Modifier,
) {
    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        viewModel.onGoogleSignInResult(result.data)
    }

    GoogleAccountCardContent(
        cloudConfigured = cloudConfigured,
        signedInEmail = signedInEmail,
        syncMessage = syncMessage,
        isSyncing = isSyncing,
        onSignIn = {
            val intent = viewModel.signInIntent
            if (intent != null) signInLauncher.launch(intent)
        },
        onSync = viewModel::syncNow,
        onSignOut = viewModel::signOut,
        modifier = modifier,
    )
}

@Composable
fun GoogleAccountCardContent(
    cloudConfigured: Boolean,
    signedInEmail: String?,
    syncMessage: String?,
    isSyncing: Boolean,
    onSignIn: () -> Unit,
    onSync: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = SurfaceColor,
        border = BorderStroke(1.dp, Border),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("Conta Google", fontWeight = FontWeight.Bold)
            Text(
                "Salve jornadas e gastos na nuvem (opcional).",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )

            if (signedInEmail != null) {
                Text(
                    "Conectado: $signedInEmail",
                    style = MaterialTheme.typography.bodySmall,
                    color = Primary,
                )
                OutlinedButton(
                    onClick = onSync,
                    enabled = !isSyncing,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(if (isSyncing) "Sincronizando..." else "Sincronizar agora")
                }
                TextButton(
                    onClick = onSignOut,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Sair da conta Google")
                }
            } else {
                Button(
                    onClick = onSignIn,
                    enabled = !isSyncing,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(if (isSyncing) "Aguarde..." else "Entrar com Google")
                }
            }

            syncMessage?.let { msg ->
                Text(
                    msg,
                    style = MaterialTheme.typography.bodySmall,
                    color = Primary,
                )
            }
        }
    }
}
