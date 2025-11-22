package com.scytales.mid.sdk.example.app.openid4vci

import android.net.Uri
import android.util.Log
import com.scytales.mid.sdk.example.app.sdk.ScytalesSdkInitializer
import eu.europa.ec.eudi.wallet.issue.openid4vci.IssueEvent
import eu.europa.ec.eudi.wallet.issue.openid4vci.Offer
import eu.europa.ec.eudi.wallet.issue.openid4vci.OfferResult
import eu.europa.ec.eudi.wallet.issue.openid4vci.OpenId4VciManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn

/**
 * Coordinator for OpenID4VCI credential issuance flows.
 *
 * This singleton manages the [OpenId4VciManager] lifecycle and provides Flow-based APIs
 * for credential issuance. Uses shared flows to survive configuration changes during
 * the authorization code flow.
 *
 * ## Key Features
 * - Lazy creation of OpenId4VciManager
 * - Shared flows that survive ViewModel recreation
 * - Support for authorization code flow with resumption
 * - Automatic cleanup on issuance completion or failure
 *
 * ## Typical Flow
 * 1. Call [resolveOffer] with credential offer URI
 * 2. Call [issueDocuments] with resolved offer
 * 3. If authorization needed, handle redirect and call [resumeWithAuthorization]
 * 4. Collect [IssueEvent]s until completion
 *
 * @see OpenId4VciManager for the underlying SDK manager
 * @see IssueEvent for issuance event types
 */
object OpenId4VciCoordinator {

    private const val TAG = "OpenId4VciCoordinator"

    private var manager: OpenId4VciManager? = null
    private var activeIssueFlow: Flow<IssueEvent>? = null

    /**
     * Get or lazily create the OpenId4VciManager instance.
     *
     * @return The manager instance
     */
    private fun getOrCreateManager(): OpenId4VciManager {
        return manager ?: run {
            Log.d(TAG, "Creating OpenId4VciManager")
            ScytalesSdkInitializer.getSdk()
                .createOpenId4VciManager()
                .also { manager = it }
        }
    }

    /**
     * Resolve a credential offer from URI.
     *
     * Parses the offer URI and retrieves the credential offer details from the issuer.
     *
     * @param offerUri The OpenID4VCI offer URI (e.g., from QR code or deep link)
     * @return Flow emitting [OfferResult] with the resolved offer or error
     */
    fun resolveOffer(offerUri: String): Flow<OfferResult> = callbackFlow {
        Log.d(TAG, "Resolving offer")
        getOrCreateManager().resolveDocumentOffer(offerUri) { result ->
            trySend(result)
            close()
        }
        awaitClose { }
    }

    /**
     * Get the active issuance flow for reconnecting after authorization.
     *
     * Used to re-collect events after returning from authorization redirect.
     *
     * @return The active issuance flow, or null if no issuance in progress
     */
    fun getActiveIssueFlow(): Flow<IssueEvent>? = activeIssueFlow

    /**
     * Issue documents from a resolved offer.
     *
     * Returns a shared Flow that survives ViewModel recreation during authorization.
     * If called during an active issuance, returns the existing flow.
     *
     * @param offer The resolved credential offer
     * @param txCode Optional transaction code (PIN) if required by issuer
     * @return Shared Flow emitting [IssueEvent]s throughout the issuance process
     */
    fun issueDocuments(offer: Offer, txCode: String?): Flow<IssueEvent> {
        activeIssueFlow?.let {
            Log.d(TAG, "Returning existing active flow")
            return it
        }

        val flow = callbackFlow {
            Log.d(TAG, "Starting issuance")

            getOrCreateManager().issueDocumentByOffer(offer, txCode) { event ->
                trySend(event)

                when (event) {
                    is IssueEvent.Finished -> {
                        Log.d(TAG, "Issuance finished")
                        cleanup()
                        close()
                    }
                    is IssueEvent.Failure -> {
                        Log.e(TAG, "Issuance failed", event.cause)
                        cleanup()
                        close(event.cause)
                    }
                    else -> { /* Flow continues */ }
                }
            }

            awaitClose {
                Log.d(TAG, "Collector detached")
            }
        }.shareIn(
            scope = CoroutineScope(Dispatchers.Default),
            started = SharingStarted.Lazily,
            replay = 10
        )

        activeIssueFlow = flow
        return flow
    }

    /**
     * Resume authorization with the redirect URI.
     *
     * Call this when the app receives the authorization callback deep link.
     * Must be called with the URI from the authorization redirect.
     *
     * @param uri The authorization redirect URI received via deep link
     * @return `true` if authorization resumed successfully, `false` if no active manager
     */
    fun resumeWithAuthorization(uri: Uri): Boolean {
        Log.d(TAG, "Resuming authorization")

        return manager?.let { mgr ->
            try {
                mgr.resumeWithAuthorization(uri)
                Log.d(TAG, "Authorization resumed")
                true
            } catch (e: Exception) {
                Log.e(TAG, "Resume failed", e)
                false
            }
        } ?: run {
            Log.w(TAG, "No active manager")
            false
        }
    }

    /**
     * Clean up manager and active flow after issuance completion or failure.
     */
    private fun cleanup() {
        Log.d(TAG, "Cleanup")
        manager = null
        activeIssueFlow = null
    }
}

