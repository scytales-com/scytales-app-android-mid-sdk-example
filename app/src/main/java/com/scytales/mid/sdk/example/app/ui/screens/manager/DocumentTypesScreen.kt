package com.scytales.mid.sdk.example.app.ui.screens.manager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scytales.mid.sdk.example.app.ui.components.DocumentTypeCard
import com.scytales.mid.sdk.example.app.ui.theme.ScytalesappandroidmidsdkexampleTheme

/**
 * Screen displaying available document types for issuance
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentTypesScreen(
    activity: androidx.activity.ComponentActivity,
    onNavigateBack: () -> Unit,
    onDocumentIssued: () -> Unit,
    viewModel: DocumentTypesViewModel = viewModel(),
    issuanceViewModel: DocumentIssuanceViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val issuanceState by issuanceViewModel.issuanceState.collectAsStateWithLifecycle()

    // Handle issuance success - navigate back and refresh
    LaunchedEffect(issuanceState) {
        if (issuanceState is IssuanceState.Success) {
            onDocumentIssued()
            issuanceViewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Document") },
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
        when (val currentState = state) {
            is DocumentTypesState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    LoadingContent()
                }
            }

            is DocumentTypesState.Success -> {
                PullToRefreshBox(
                    isRefreshing = false,
                    onRefresh = { viewModel.loadDocumentTypes() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    DocumentTypeListContent(
                        documentTypes = currentState.documentTypes,
                        onDocumentTypeClick = { documentTypeItem ->
                            issuanceViewModel.issueDocument(
                                activity,
                                documentTypeItem.availableDocumentType
                            )
                        }
                    )
                }
            }

            is DocumentTypesState.Empty -> {
                PullToRefreshBox(
                    isRefreshing = false,
                    onRefresh = { viewModel.loadDocumentTypes() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    EmptyContent()
                }
            }

            is DocumentTypesState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    ErrorContent(
                        message = currentState.message,
                        onRetry = { viewModel.loadDocumentTypes() }
                    )
                }
            }
        }

        // Issuance loading dialog
        if (issuanceState is IssuanceState.Issuing) {
            androidx.compose.ui.window.Dialog(
                onDismissRequest = { /* Not dismissible */ }
            ) {
                androidx.compose.material3.Card {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Issuing document...",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Please complete the authentication flow",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Issuance error dialog
        if (issuanceState is IssuanceState.Error) {
            val errorState = issuanceState as IssuanceState.Error
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { issuanceViewModel.resetState() },
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
                    androidx.compose.material3.TextButton(
                        onClick = { issuanceViewModel.resetState() }
                    ) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

/**
 * Loading state UI
 */
@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading document types...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Document type list UI
 */
@Composable
private fun DocumentTypeListContent(
    documentTypes: List<DocumentTypeItem>,
    onDocumentTypeClick: (DocumentTypeItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(documentTypes, key = { it.id }) { documentType ->
            DocumentTypeCard(
                documentType = documentType,
                onClick = { onDocumentTypeClick(documentType) }
            )
        }
    }
}

/**
 * Empty state UI
 */
@Composable
private fun EmptyContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "No Document Types Available",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "There are no document types available to add at this time.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Error state UI
 */
@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
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
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
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

@Preview
@Composable
private fun DocumentTypesScreenLoadingPreview() {
    ScytalesappandroidmidsdkexampleTheme {
        LoadingContent()
    }
}

@Preview
@Composable
private fun DocumentTypesScreenEmptyPreview() {
    ScytalesappandroidmidsdkexampleTheme {
        EmptyContent()
    }
}

@Preview
@Composable
private fun DocumentTypesScreenErrorPreview() {
    ScytalesappandroidmidsdkexampleTheme {
        ErrorContent(
            message = "Failed to load document types. Please try again.",
            onRetry = {}
        )
    }
}

