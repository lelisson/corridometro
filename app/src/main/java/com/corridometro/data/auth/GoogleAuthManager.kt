package com.corridometro.data.auth

import android.content.Context
import android.content.Intent
import com.corridometro.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class GoogleAuthManager(context: Context) {

    private val appContext = context.applicationContext
    private val auth: FirebaseAuth? = runCatching { FirebaseAuth.getInstance() }.getOrNull()

    private val webClientId: String = runCatching {
        appContext.getString(R.string.default_web_client_id)
    }.getOrDefault("")

    val hasWebClientId: Boolean
        get() = webClientId.isNotBlank()

    val firebaseInitialized: Boolean
        get() = runCatching { FirebaseApp.getInstance() }.isSuccess

    val isCloudConfigured: Boolean
        get() = firebaseInitialized && hasWebClientId

    private val _userEmail = MutableStateFlow<String?>(auth?.currentUser?.email)
    val userEmail: StateFlow<String?> = _userEmail.asStateFlow()

    private val _displayName = MutableStateFlow<String?>(auth?.currentUser?.displayName)
    val displayName: StateFlow<String?> = _displayName.asStateFlow()

    private val _isSignedIn = MutableStateFlow(auth?.currentUser != null)
    val isSignedInFlow: StateFlow<Boolean> = _isSignedIn.asStateFlow()

    val isSignedIn: Boolean
        get() = _isSignedIn.value

    val userId: String?
        get() = auth?.currentUser?.uid

    private val googleSignInClient: GoogleSignInClient? by lazy {
        if (!isCloudConfigured) return@lazy null
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .requestProfile()
            .build()
        GoogleSignIn.getClient(appContext, options)
    }

    fun getSignInIntent(): Intent? = googleSignInClient?.signInIntent

    fun refreshAuthState() {
        val user = auth?.currentUser
        _isSignedIn.value = user != null
        _userEmail.value = user?.email
        _displayName.value = user?.displayName ?: user?.email?.substringBefore('@')
    }

    suspend fun handleSignInResult(data: Intent?): Result<Unit> {
        if (!isCloudConfigured) {
            return Result.failure(IllegalStateException("Firebase nao configurado. Adicione google-services.json."))
        }
        val firebaseAuth = auth
            ?: return Result.failure(IllegalStateException("Firebase Auth indisponivel."))
        return try {
            val account = GoogleSignIn.getSignedInAccountFromIntent(data)
                .getResult(ApiException::class.java)
            signInWithGoogleAccount(account, firebaseAuth)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun signInWithGoogleAccount(
        account: GoogleSignInAccount,
        firebaseAuth: FirebaseAuth,
    ) {
        val token = account.idToken
            ?: throw IllegalStateException("Token Google ausente. Verifique o Web Client ID no Firebase.")
        val credential = GoogleAuthProvider.getCredential(token, null)
        firebaseAuth.signInWithCredential(credential).await()
        _userEmail.value = firebaseAuth.currentUser?.email ?: account.email
        _displayName.value = firebaseAuth.currentUser?.displayName
            ?: account.displayName
            ?: account.email?.substringBefore('@')
        _isSignedIn.value = true
    }

    suspend fun signOut() {
        auth?.signOut()
        googleSignInClient?.signOut()?.await()
        _userEmail.value = null
        _displayName.value = null
        _isSignedIn.value = false
    }
}
