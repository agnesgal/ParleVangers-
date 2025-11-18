package com.example.parlevangers.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.parlevangers.auth.AuthViewModel

/**
 * Register screen for ParleVangers.
 * Allows users to create an account with email/password or Google.
 */
@Composable
fun RegisterScreen(
    onBackToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    onGoogleSignIn: () -> Unit,
    viewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Register", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

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

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (password != confirmPassword) {
                    error = "Passwords don't match"
                    return@Button
                }
                if (password.length < 6) {
                    error = "Password should be at least 6 characters"
                    return@Button
                }
                viewModel.register(email, password) { success ->
                    if (success) {
                        error = ""
                        onRegisterSuccess()
                    } else {
                        error = "Registration failed"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register with Email")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onGoogleSignIn,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Sign up with Google")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onBackToLogin) {
            Text("Already have an account? Login")
        }

        if (error.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(error, color = MaterialTheme.colorScheme.error)
        }
    }
}
