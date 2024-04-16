package io.github.dzivko1.recap

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.FirebaseUiException
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {

  @Inject lateinit var auth: FirebaseAuth
  @Inject lateinit var authUi: AuthUI

  private val signInLauncher = registerForActivityResult(
    FirebaseAuthUIActivityResultContract()
  ) { res ->
    onSignInResult(res)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if (auth.currentUser != null) onSignInSuccess()
    else startSignIn()
  }

  private fun startSignIn() {
    val providers = arrayListOf(
      AuthUI.IdpConfig.GoogleBuilder().build()
    )

    val signInIntent = authUi.createSignInIntentBuilder()
      .setAvailableProviders(providers)
      .build()

    signInLauncher.launch(signInIntent)
  }

  private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
    val response = result.idpResponse
    if (result.resultCode == RESULT_OK) {
      onSignInSuccess()
    } else {
      // Sign in failed. If response is null the user canceled the
      // sign-in flow using the back button. Otherwise check
      // response.getError().getErrorCode() and handle the error.
      onSignInFail(response?.error)
    }
  }

  private fun onSignInSuccess() {
    startActivity(
      Intent(this, MainActivity::class.java)
    )
    finish()
  }

  private fun onSignInFail(error: FirebaseUiException?) {
    if (error == null) {
      Timber.i("Sign in cancelled by user.")
      startSignIn()
    } else {
      Timber.e("Sign in failed. Error code: ${error.errorCode}. Message: ${error.message}")
    }
  }
}