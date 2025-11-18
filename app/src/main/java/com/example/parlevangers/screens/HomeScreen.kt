package com.example.parlevangers.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onFlashcardsClick: () -> Unit,
    onSentenceBuilderClick: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome to ParleVangers")

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onFlashcardsClick, modifier = Modifier.fillMaxWidth()) {
            Text("Flashcards")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = onSentenceBuilderClick, modifier = Modifier.fillMaxWidth()) {
            Text("Sentence Builder")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
            Text("Logout")
        }
    }
}
