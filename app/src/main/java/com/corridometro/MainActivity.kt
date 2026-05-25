package com.corridometro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.corridometro.BuildConfig
import com.corridometro.ui.CorridometroViewModel
import com.corridometro.ui.navigation.CorridometroNavHost
import com.corridometro.ui.theme.CorridometroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as CorridometroApp
        app.billingManager.bindActivity(this)

        setContent {
            var darkTheme by remember { mutableStateOf(app.appSettings.darkTheme) }
            CorridometroTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background,
                ) {
                    val viewModel: CorridometroViewModel = viewModel(
                        factory = CorridometroViewModel.Factory(
                            app.repository,
                            app.googleAuth,
                            app.billingManager,
                            BuildConfig.REQUIRE_GOOGLE_LOGIN,
                            BuildConfig.HAS_GOOGLE_SERVICES_FILE,
                            BuildConfig.APPLICATION_ID,
                        ),
                    )
                    CorridometroNavHost(
                        viewModel = viewModel,
                        darkTheme = darkTheme,
                        onDarkThemeChange = { darkTheme = it },
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (application as CorridometroApp).billingManager.bindActivity(this)
    }

    override fun onPause() {
        (application as CorridometroApp).billingManager.bindActivity(null)
        super.onPause()
    }
}
