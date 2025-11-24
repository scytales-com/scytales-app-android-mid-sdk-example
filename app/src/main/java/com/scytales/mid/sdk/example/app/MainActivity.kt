package com.scytales.mid.sdk.example.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.scytales.mid.sdk.example.app.ui.screens.home.HomeScreen
import com.scytales.mid.sdk.example.app.ui.screens.manager.DocumentTypesScreen
import com.scytales.mid.sdk.example.app.ui.theme.ScytalesappandroidmidsdkexampleTheme
import androidx.core.net.toUri

class MainActivity : ComponentActivity() {

    // State to trigger recomposition when a new intent arrives
    private val intentTrigger = mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ScytalesappandroidmidsdkexampleTheme {
                ScytalesappandroidmidsdkexampleApp(
                    activity = this,
                    intentTrigger = intentTrigger.value
                )
            }
        }
    }

    /**
     * Called when a new intent is delivered to an already-running activity.
     *
     * This is crucial for handling deep links when the app is already open:
     * - OpenID4VCI authorization redirects (from browser after user authorizes)
     * - OpenID4VP verification requests (from verifier app/website)
     * - DCAPI requests (from browser via Digital Credentials API)
     *
     * Without this, deep links would only work on fresh app launches.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        // Update the activity's intent so it can be processed
        setIntent(intent)

        // Trigger recomposition to handle the new intent
        intentTrigger.value++

        android.util.Log.d("MainActivity", "onNewIntent - New intent received, triggering navigation")
    }
}

@Composable
fun ScytalesappandroidmidsdkexampleApp(
    activity: ComponentActivity,
    intentTrigger: Int // Changes when new intent arrives, triggering LaunchedEffect
) {
    // Navigation stack: maintains screen history for proper back navigation
    val navigationStack = remember { mutableStateListOf<Screen>(Screen.Home) }
    val currentScreen = navigationStack.lastOrNull() ?: Screen.Home

    // ViewModels to manage state
    val documentListViewModel: com.scytales.mid.sdk.example.app.ui.screens.documents.DocumentListViewModel =
        androidx.lifecycle.viewmodel.compose.viewModel()

    // Navigation functions
    fun navigateTo(screen: Screen) {
        navigationStack.add(screen)
        android.util.Log.d("ScytalesApp", "Navigated to: $screen, Stack size: ${navigationStack.size}")
    }

    fun navigateBack() {
        if (navigationStack.size > 1) {
            val removed = navigationStack.removeLast()
            android.util.Log.d("ScytalesApp", "Navigated back from: $removed, Stack size: ${navigationStack.size}")
        } else {
            android.util.Log.d("ScytalesApp", "Already on home screen, back button does nothing")
        }
    }

    fun navigateBackTo(screen: Screen) {
        // Remove screens until we reach the target screen
        while (navigationStack.size > 1 && navigationStack.last() != screen) {
            navigationStack.removeLast()
        }
        android.util.Log.d("ScytalesApp", "Navigated back to: $screen, Stack size: ${navigationStack.size}")
    }

    // Handle system back button - only enabled when not on home screen
    BackHandler(enabled = navigationStack.size > 1) {
        navigateBack()
    }

    /**
     * Handle incoming intents (deep links and DCAPI requests)
     *
     * This effect runs:
     * 1. On first composition (initial app launch)
     * 2. When intentTrigger changes (new intent arrives via onNewIntent)
     *
     * This ensures deep links work both on cold starts and when app is already running.
     */
    LaunchedEffect(intentTrigger) {
        val intent = activity.intent

        // Handle deep link URIs (OpenID4VCI and OpenID4VP)
        intent?.data?.let { uri ->
            when {
                // OpenID4VCI: Browser redirects after user authorizes credential issuance
                uri.scheme == "eudi-openid4ci" && uri.host == "authorize" -> {
                    android.util.Log.d("ScytalesApp", "OpenID4VCI authorization redirect: $uri")
                    // Clear stack and rebuild with Home as base
                    navigationStack.clear()
                    navigationStack.add(Screen.Home)
                    navigateTo(Screen.OfferReview(offerUri = null, authorizationUri = uri))
                }

                // OpenID4VP: Verifier requests credential presentation
                uri.scheme == "mdoc-openid4vp" -> {
                    android.util.Log.d("ScytalesApp", "OpenID4VP presentation request: $uri")
                    // Clear stack and rebuild with Home as base
                    navigationStack.clear()
                    navigationStack.add(Screen.Home)
                    navigateTo(Screen.RemotePresentation(uri.toString()))
                }
            }
        }

        // Handle DCAPI: Browser-initiated credential requests (W3C Digital Credentials API)
        if (intent?.action == "androidx.credentials.registry.provider.action.GET_CREDENTIAL" ||
            intent?.action == "androidx.identitycredentials.action.GET_CREDENTIALS") {
            android.util.Log.d("ScytalesApp", "DCAPI credential request from browser")
            // Clear stack and rebuild with Home as base
            navigationStack.clear()
            navigationStack.add(Screen.Home)
            navigateTo(Screen.DCAPIPresentation(intent))
        }
    }

    when (currentScreen) {
        Screen.Home -> {
            HomeScreen(
                onNavigateToScytalesManager = { navigateTo(Screen.DocumentTypes) },
                onNavigateToOpenId4Vci = { navigateTo(Screen.QRScanner) },
                onNavigateToDocuments = { navigateTo(Screen.DocumentList) },
                onNavigateToProximity = { navigateTo(Screen.ProximityPresentation) },
                onNavigateToRemote = { navigateTo(Screen.RemoteQRScanner) },
                onNavigateToDCAPI = { navigateTo(Screen.DCAPIQRScanner) }
            )
        }

        Screen.DocumentList -> {
            com.scytales.mid.sdk.example.app.ui.screens.documents.DocumentListScreen(
                onNavigateBack = { navigateBack() },
                onDocumentClick = { documentId ->
                    navigateTo(Screen.DocumentDetails(documentId))
                },
                viewModel = documentListViewModel
            )
        }

        Screen.DocumentTypes -> {
            DocumentTypesScreen(
                activity = activity,
                onNavigateBack = { navigateBack() },
                onDocumentIssued = {
                    // Navigate back to home, clearing intermediate screens
                    navigateBackTo(Screen.Home)
                }
            )
        }

        Screen.QRScanner -> {
            com.scytales.mid.sdk.example.app.ui.screens.common.QRScannerScreen(
                title = "Scan Credential Offer",
                instructions = "Point camera at credential offer QR code",
                onNavigateBack = { navigateBack() },
                onQrCodeScanned = { offerUri ->
                    navigateTo(Screen.OfferReview(offerUri))
                },
                uriValidator = { uri ->
                    // Only accept OpenID4VCI credential offers
                    uri.startsWith("openid-credential-offer://") ||
                    uri.contains("credential_offer")
                }
            )
        }

        is Screen.OfferReview -> {
            val screenData = currentScreen as Screen.OfferReview

            com.scytales.mid.sdk.example.app.ui.screens.openid4vci.OfferReviewScreen(
                offerUri = screenData.offerUri,
                authorizationUri = screenData.authorizationUri,
                onNavigateBack = { navigateBack() },
                onDocumentsIssued = {
                    // Navigate back to home, clearing intermediate screens
                    navigateBackTo(Screen.Home)
                }
            )
        }

        is Screen.DocumentDetails -> {
            val screenData = currentScreen as Screen.DocumentDetails

            com.scytales.mid.sdk.example.app.ui.screens.documentdetails.DocumentDetailsScreen(
                documentId = screenData.documentId,
                onNavigateBack = { navigateBack() },
                onDocumentDeleted = {
                    // Navigate back to document list (will auto-refresh)
                    navigateBack()
                }
            )
        }

        Screen.ProximityPresentation -> {
            com.scytales.mid.sdk.example.app.ui.screens.proximity.ProximityPresentationScreen(
                onNavigateBack = { navigateBack() }
            )
        }

        Screen.RemoteQRScanner -> {
            com.scytales.mid.sdk.example.app.ui.screens.common.QRScannerScreen(
                title = "Scan Verification Request",
                instructions = "Point camera at verifier's QR code",
                onNavigateBack = { navigateBack() },
                onQrCodeScanned = { uri ->
                    // Navigate to remote presentation with scanned URI
                    navigateTo(Screen.RemotePresentation(uri))
                },
                uriValidator = { uri ->
                    // Only accept OpenID4VP verification requests
                    uri.startsWith("mdoc-openid4vp://") ||
                    uri.startsWith("openid4vp://") ||
                    uri.startsWith("openid-vc://")
                }
            )
        }

        is Screen.RemotePresentation -> {
            val screenData = currentScreen as Screen.RemotePresentation

            com.scytales.mid.sdk.example.app.ui.screens.remote.RemotePresentationScreen(
                requestUri = screenData.requestUri,
                onNavigateBack = { navigateBack() }
            )
        }

        is Screen.DCAPIQRScanner -> {
            com.scytales.mid.sdk.example.app.ui.screens.common.QRScannerScreen(
                title = "Scan DCAPI Request",
                instructions = "Point camera at DCAPI request QR code",
                onNavigateBack = { navigateBack() },
                onQrCodeScanned = { uri ->
                    // Try with Google Play Services (WebAuthn/caBLE capable)
                    if (activity.tryStart(Intent(Intent.ACTION_VIEW, uri.toUri()).apply {
                            setPackage("com.google.android.gms")
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        })) {
                        android.util.Log.d(
                            "ScytalesApp", "Opened DCAPI request with Google Play Services"
                        )
                    }

                    // Try with Chrome
                    else if (activity.tryStart(Intent(Intent.ACTION_VIEW, uri.toUri()).apply {
                            setPackage("com.android.chrome")
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        })) {
                        android.util.Log.d("ScytalesApp", "Opened DCAPI request with Chrome")
                    }

                    // Try with any capable handler
                    else if (activity.tryStart(Intent(Intent.ACTION_VIEW, uri.toUri()).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        })) {
                        android.util.Log.d(
                            "ScytalesApp",
                            "Opened DCAPI request with default handler"
                        )
                    }

                    // No handler found
                    else {
                        Toast.makeText(
                            activity,
                            "No app available to open DCAPI request",
                            Toast.LENGTH_LONG
                        ).show()
                        android.util.Log.e(
                            "ScytalesApp",
                            "No app available to open DCAPI request URI: $uri"
                        )
                    }
                    navigateBack()
                },
                uriValidator = { uri ->
                    // DCAPI request URIs typically start with "FIDO:/"
                    uri.startsWith("FIDO:/")
                }
            )
        }

        is Screen.DCAPIPresentation -> {
            val screenData = currentScreen as Screen.DCAPIPresentation

            com.scytales.mid.sdk.example.app.ui.screens.dcapi.DCAPIPresentationScreen(
                intent = screenData.intent,
                onFinishWithResult = { resultCode, resultIntent ->
                    // Set result and finish activity to return to browser
                    activity.setResult(resultCode, resultIntent)
                    activity.finish()
                }
            )
        }
    }
}

/**
 * Sealed class representing different screens in the app
 */
sealed class Screen {
    data object Home : Screen()
    data object DocumentList : Screen()
    data object DocumentTypes : Screen()
    data object QRScanner : Screen()
    data class OfferReview(
        val offerUri: String? = null,
        val authorizationUri: android.net.Uri? = null
    ) : Screen()

    data class DocumentDetails(val documentId: String) : Screen()
    data object ProximityPresentation : Screen()
    data object RemoteQRScanner : Screen()
    data class RemotePresentation(val requestUri: String) : Screen()
    data object DCAPIQRScanner : Screen()
    data class DCAPIPresentation(val intent: Intent) : Screen()
}

/**
 * Safely attempts to start an activity using the provided intent.
 *
 * @param intent The [Intent] to start. * @return `true` if the activity was successfully started, `false` otherwise.
 */
private fun ComponentActivity.tryStart(intent: Intent): Boolean {
    return runCatching {
        startActivity(intent)
        true
    }.getOrElse {
        android.util.Log.e("ScytalesApp", "No activity found to handle intent: $intent", it)
        false
    }
}
