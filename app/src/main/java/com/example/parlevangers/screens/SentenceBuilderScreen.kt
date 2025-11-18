package com.example.parlevangers.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SentenceBuilderScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Sentence Builder (Coming Soon)")
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onBack) { Text("Back") }
    }
}
