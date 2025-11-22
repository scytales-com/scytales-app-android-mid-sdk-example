package com.scytales.mid.sdk.example.app.ui.screens.dcapi

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import eu.europa.ec.eudi.iso18013.transfer.response.RequestedDocument
import eu.europa.ec.eudi.wallet.document.IssuedDocument
import eu.europa.ec.eudi.wallet.document.format.MsoMdocFormat
import eu.europa.ec.eudi.wallet.document.format.SdJwtVcFormat

/**
 * DCAPI Presentation Screen
 *
 * Handles browser-initiated credential verification via Digital Credentials API.
 * This screen is triggered by system intent (GET_CREDENTIAL) from the browser.
 *
 * Flow:
 * 1. Browser requests credentials
 * 2. Android shows credential provider picker
 * 3. User selects wallet app
 * 4. This screen appears with the request
 * 5. User approves/denies
 * 6. Activity returns result to browser
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DCAPIPresentationScreen(
    intent: Intent,
    onFinishWithResult: (resultCode: Int, resultIntent: Intent?) -> Unit,
    viewModel: DCAPIPresentationViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    // Start DCAPI presentation on launch
    LaunchedEffect(intent) {
        viewModel.startDCAPIPresentation(intent)
    }

    // Handle ResponseReady and Error states - finish activity with result
    LaunchedEffect(state) {
        when (val currentState = state) {
            is DCAPIState.ResponseReady -> {
                // SUCCESS - Return result intent and finish
                onFinishWithResult(Activity.RESULT_OK, currentState.intent)
            }

            is DCAPIState.Error -> {
                if (currentState.errorIntent != null) {
                    // DCAPIException - Return error intent
                    onFinishWithResult(Activity.RESULT_OK, currentState.errorIntent)
                } else {
                    // Other error - Cancel
                    onFinishWithResult(Activity.RESULT_CANCELED, null)
                }
            }

            else -> { /* Continue showing UI */ }
        }
    }

    // Stop presentation on dispose
    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopDCAPIPresentation()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Verification Request") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.denyRequest()
                        onFinishWithResult(Activity.RESULT_CANCELED, null)
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Cancel"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val currentState = state) {
                DCAPIState.ProcessingIntent -> {
                    ProcessingContent()
                }

                is DCAPIState.RequestReceived -> {
                    RequestReceivedContent(
                        requestedDocuments = currentState.requestedDocuments,
                        verifierName = currentState.verifierName,
                        onApprove = {
                            viewModel.approveRequest(currentState.requestedDocuments.keys.toList())
                        },
                        onDeny = {
                            viewModel.denyRequest()
                        }
                    )
                }

                DCAPIState.SendingResponse -> {
                    SendingResponseContent()
                }

                // ResponseReady and Error handled by LaunchedEffect above
                is DCAPIState.ResponseReady,
                is DCAPIState.Error -> {
                    // These states trigger activity finish in LaunchedEffect
                    // Show loading while finishing
                    ProcessingContent()
                }
            }
        }
    }
}

/**
 * Content shown while processing the DCAPI intent
 */
@Composable
private fun ProcessingContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Processing request...",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Please wait while we verify the request",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Content shown when request is received - displays requested documents
 */
@Composable
private fun RequestReceivedContent(
    requestedDocuments: Map<RequestedDocument, IssuedDocument>,
    verifierName: String?,
    onApprove: () -> Unit,
    onDeny: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Verification Request",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (verifierName != null) {
            Text(
                text = "Verifier: $verifierName",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Text(
            text = "The verifier is requesting the following documents:",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(requestedDocuments.toList()) { (requestedDoc, issuedDoc) ->
                DocumentCard(
                    requestedDocument = requestedDoc,
                    issuedDocument = issuedDoc
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onDeny,
                modifier = Modifier.weight(1f)
            ) {
                Text("Deny")
            }

            Button(
                onClick = onApprove,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text("Approve")
            }
        }
    }
}

/**
 * Content shown while sending response
 */
@Composable
private fun SendingResponseContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Sending response...",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your approved credentials are being sent",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Card displaying a requested document with its details
 */
@Composable
private fun DocumentCard(
    requestedDocument: RequestedDocument,
    issuedDocument: IssuedDocument
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = issuedDocument.issuerMetadata?.display?.first()?.name ?: issuedDocument.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = when (val f = issuedDocument.format) {
                    is MsoMdocFormat -> f.docType
                    is SdJwtVcFormat -> f.vct
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

