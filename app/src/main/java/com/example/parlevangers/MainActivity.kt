package com.example.parlevangers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import com.example.parlevangers.auth.AuthViewModel
import com.example.parlevangers.screens.FlashcardScreen
import com.example.parlevangers.screens.LoginScreen
import com.example.parlevangers.screens.RegisterScreen
import com.example.parlevangers.ui.theme.ParleVangersTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

/**
 * Main Activity for ParleVangers - French Learning App
 * Student: Agnes Gal
 * Student ID: 200575611
 * Course: COMP3025-25F-12715 - Mobile and Pervasive Computing
 * Assignment 2 – App Prototype
 */
class MainActivity : ComponentActivity() {

    private val authViewModel = AuthViewModel()
    private lateinit var googleSignInClient: GoogleSignInClient

    // Modern Google Sign-In Result Launcher
    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken

                if (idToken != null) {
                    authViewModel.signInWithGoogle(idToken) {
                        // UI updates through Compose state
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            ParleVangersTheme {
                AppContent()
            }
        }
    }

    @Composable
    fun AppContent() {
        var currentScreen by remember { mutableStateOf("login") }

        when (currentScreen) {

            "login" -> {
                LoginScreen(
                    onRegisterClick = { currentScreen = "register" },
                    onLoginSuccess = { currentScreen = "flashcards" },
                    onSkip = { currentScreen = "flashcards" },
                    onGoogleSignIn = {
                        val intent = googleSignInClient.signInIntent
                        googleSignInLauncher.launch(intent)
                    },
                    viewModel = authViewModel
                )
            }

            "register" -> {
                RegisterScreen(
                    onBackToLogin = { currentScreen = "login" },
                    onRegisterSuccess = { currentScreen = "flashcards" },
                    onGoogleSignIn = {
                        val intent = googleSignInClient.signInIntent
                        googleSignInLauncher.launch(intent)
                    },
                    viewModel = authViewModel
                )
            }

            "flashcards" -> {
                FlashcardScreen(
                    onBack = { currentScreen = "login" },
                    viewModel = authViewModel
                )
            }
        }
    }
}
