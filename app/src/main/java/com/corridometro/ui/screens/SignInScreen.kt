package com.corridometro.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.corridometro.ui.CorridometroViewModel
import com.corridometro.ui.theme.Primary
import com.corridometro.ui.theme.TextSecondary

@Composable
fun SignInScreen(viewModel: CorridometroViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    if (!uiState.cloudConfigured) {
        FirebaseSetupScreen(
            applicationId = uiState.applicationId,
            steps = uiState.firebaseSetupSteps,
        )
        return
    }

    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        viewModel.onGoogleSignInResult(result.data)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Corridômetro",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Entre com sua conta Google para registrar jornadas e sincronizar seus dados na nuvem.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val intent = viewModel.signInIntent
                if (intent != null) signInLauncher.launch(intent)
            },
            enabled = !uiState.isSyncing,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (uiState.isSyncing) "Conectando..." else "Continuar com Google")
        }

        uiState.syncMessage?.let { msg ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = msg,
                style = MaterialTheme.typography.bodySmall,
                color = Primary,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun AuthLoadingScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator(color = Primary)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Verificando conta Google...", color = TextSecondary)
    }
}
