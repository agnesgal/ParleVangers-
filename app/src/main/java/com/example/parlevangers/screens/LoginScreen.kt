package com.example.parlevangers.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.parlevangers.auth.AuthViewModel

/**
 * Login screen for ParleVangers.
 * Allows users to login with email/password or Google, or skip to flashcards.
 */
@Composable
fun LoginScreen(
    onRegisterClick: () -> Unit,
    onLoginSuccess: () -> Unit,
    onSkip: () -> Unit,
    onGoogleSignIn: () -> Unit,
    viewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "ParleVangers",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Login to practice your French",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    error = "Please enter email and password"
                    return@Button
                }
                loading = true
                viewModel.login(email, password) { success ->
                    loading = false
                    if (success) {
                        error = ""
                        onLoginSuccess()
                    } else {
                        error = "Login failed"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading
        ) {
            Text(if (loading) "Logging in..." else "Login with Email")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onGoogleSignIn,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign in with Google")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onRegisterClick) {
            Text("Create an account")
        }

        TextButton(onClick = onSkip) {
            Text("Skip to Flashcards")
        }

        if (error.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
