package com.scytales.mid.sdk.example.app.ui.screens.openid4vci

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scytales.mid.sdk.example.app.ui.theme.ScytalesappandroidmidsdkexampleTheme

/**
 * Screen to review and accept a credential offer
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfferReviewScreen(
    offerUri: String? = null,
    authorizationUri: android.net.Uri? = null,
    onNavigateBack: () -> Unit,
    onDocumentsIssued: () -> Unit,
    viewModel: OpenId4VciViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val offerState by viewModel.offerState.collectAsStateWithLifecycle()
    val issuanceProgress by viewModel.issuanceProgress.collectAsStateWithLifecycle()

    var txCode by remember { mutableStateOf("") }

    // Determine the actual offer URI to use
    val actualOfferUri = remember(offerUri, authorizationUri) {
        if (offerUri == null && authorizationUri != null) {
            // Authorization redirect scenario - get saved URI from ViewModel
            val saved = viewModel.getSavedOfferUri()
            android.util.Log.d("OfferReviewScreen", "Using saved offer URI: $saved")
            saved
        } else {
            offerUri
        }
    }

    // Resolve offer when screen loads (only if we have a valid URI and not an auth redirect)
    LaunchedEffect(actualOfferUri) {
        if (actualOfferUri != null && authorizationUri == null) {
            android.util.Log.d("OfferReviewScreen", "Resolving offer: $actualOfferUri")
            viewModel.resolveOffer(actualOfferUri)
        }
    }

    // Handle authorization redirect - resume the issuance process
    LaunchedEffect(authorizationUri) {
        if (authorizationUri != null) {
            android.util.Log.d("OfferReviewScreen", "Authorization redirect detected, resuming issuance")
            viewModel.resumeAuthorization(authorizationUri)
        }
    }

    // Handle issuance success
    LaunchedEffect(issuanceProgress) {
        if (issuanceProgress is IssuanceProgress.Success) {
            onDocumentsIssued()
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Review Offer") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = offerState) {
                is OfferState.Idle -> {
                    // Should not happen
                }

                is OfferState.Resolving -> {
                    LoadingContent("Resolving offer...")
                }

                is OfferState.Resolved -> {
                    OfferContent(
                        offerDisplay = state.offer.toOfferDisplay(),
                        txCode = txCode,
                        onTxCodeChange = { txCode = it },
                        onAccept = {
                            viewModel.issueDocuments(
                                offer = state.offer,
                                txCode = txCode.ifBlank { null }
                            )
                        },
                        onCancel = onNavigateBack
                    )
                }

                is OfferState.Error -> {
                    ErrorContent(
                        message = state.message,
                        onRetry = { actualOfferUri?.let { viewModel.resolveOffer(it) } },
                        onCancel = onNavigateBack
                    )
                }
            }

            // Issuance loading dialog
            if (issuanceProgress is IssuanceProgress.Issuing) {
                val progressState = issuanceProgress as IssuanceProgress.Issuing
                androidx.compose.ui.window.Dialog(
                    onDismissRequest = { /* Not dismissible */ }
                ) {
                    Card {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Issuing documents...",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            if (progressState.total > 0) {
                                Text(
                                    text = "${progressState.issued} of ${progressState.total} documents issued",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                Text(
                                    text = "Please complete authentication if prompted",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Issuance error dialog
            if (issuanceProgress is IssuanceProgress.Error) {
                val errorState = issuanceProgress as IssuanceProgress.Error
                AlertDialog(
                    onDismissRequest = { viewModel.resetState() },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    },
                    title = { Text("Issuance Failed") },
                    text = { Text(errorState.message) },
                    confirmButton = {
                        TextButton(onClick = { viewModel.resetState() }) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun LoadingContent(message: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun OfferContent(
    offerDisplay: OfferDisplay,
    txCode: String,
    onTxCodeChange: (String) -> Unit,
    onAccept: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Issuer info
        Text(
            text = "Issuer",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = offerDisplay.issuerName,
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Documents offered
        Text(
            text = "Documents Offered",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        offerDisplay.documents.forEach { doc ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBox,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = doc.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = doc.format,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // TX Code input if required
        if (offerDisplay.requiresTxCode) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Transaction Code",
                style = MaterialTheme.typography.titleMedium
            )

            offerDisplay.txCodeDescription?.let { description ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = txCode,
                onValueChange = onTxCodeChange,
                label = { Text("Enter transaction code") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }

            Button(
                onClick = onAccept,
                modifier = Modifier.weight(1f),
                enabled = !offerDisplay.requiresTxCode || txCode.isNotBlank()
            ) {
                Text("Accept")
            }
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Error",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(onClick = onCancel) {
                    Text("Cancel")
                }
                Button(onClick = onRetry) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Retry")
                }
            }
        }
    }
}

@Preview
@Composable
private fun OfferContentPreview() {
    ScytalesappandroidmidsdkexampleTheme {
        OfferContent(
            offerDisplay = OfferDisplay(
                issuerName = "Example Issuer",
                documents = listOf(
                    OfferedDocumentDisplay(
                        name = "National ID",
                        docType = "eu.europa.ec.eudi.pid.1",
                        format = "mso_mdoc"
                    )
                ),
                requiresTxCode = true,
                txCodeDescription = "Enter the code provided by the issuer"
            ),
            txCode = "",
            onTxCodeChange = {},
            onAccept = {},
            onCancel = {}
        )
    }
}

