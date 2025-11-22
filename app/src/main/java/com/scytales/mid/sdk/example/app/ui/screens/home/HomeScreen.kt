package com.scytales.mid.sdk.example.app.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scytales.mid.sdk.example.app.ui.theme.ScytalesappandroidmidsdkexampleTheme

/**
 * Home screen with main menu buttons for SDK features
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToScytalesManager: () -> Unit,
    onNavigateToOpenId4Vci: () -> Unit,
    onNavigateToDocuments: () -> Unit,
    onNavigateToProximity: () -> Unit,
    onNavigateToRemote: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scytales MID SDK Demo") }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Issuance Section
            Text(
                text = "Document Issuance",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Button(
                onClick = onNavigateToScytalesManager,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Issue via Scytales Manager")
            }

            Button(
                onClick = onNavigateToOpenId4Vci,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Issue via OpenID4VCI")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Document Management Section
            Text(
                text = "Document Management",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Button(
                onClick = onNavigateToDocuments,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("My Documents")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Presentation Section
            Text(
                text = "Document Presentation",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Button(
                onClick = onNavigateToProximity,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Present via Proximity (BLE)")
            }

            Button(
                onClick = onNavigateToRemote,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Present via Remote (OpenID4VP)")
            }

            // DCAPI Status
            val dcapiStatus = when (state) {
                HomeState.DcapiEnabled -> "W3C DCAPI - Annex C: Enabled"
                HomeState.DcapiDisabled -> "W3C DCAPI - Annex C: Disabled"
            }

            Button(
                onClick = { /* Show info dialog */ },
                modifier = Modifier.fillMaxWidth(),
                enabled = false
            ) {
                Text(dcapiStatus)
            }
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    ScytalesappandroidmidsdkexampleTheme {
        HomeScreen(
            onNavigateToScytalesManager = {},
            onNavigateToOpenId4Vci = {},
            onNavigateToDocuments = {},
            onNavigateToProximity = {},
            onNavigateToRemote = {}
        )
    }
}



