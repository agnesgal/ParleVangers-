package com.example.parlevangers.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.parlevangers.auth.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore

data class Flashcard(
    val frenchWord: String = "",
    val englishTranslation: String = "",
    val userId: String = ""
)

@Composable
fun FlashcardScreen(
    onBack: () -> Unit,
    viewModel: AuthViewModel
) {
    var cards by remember { mutableStateOf<List<Flashcard>>(emptyList()) }
    var index by remember { mutableStateOf(0) }
    var showEnglish by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("user_vocabulary")
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapNotNull { doc ->
                    val fr = doc.getString("frenchWord") ?: return@mapNotNull null
                    val en = doc.getString("englishTranslation") ?: ""
                    val uid = doc.getString("userId") ?: ""
                    Flashcard(frenchWord = fr, englishTranslation = en, userId = uid)
                }
                cards = list
                index = 0
                showEnglish = false
                loading = false
                error = null
            }
            .addOnFailureListener {
                loading = false
                error = "Failed to load flashcards."
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("Back")
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Flashcards",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        when {
            loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(error!!, color = MaterialTheme.colorScheme.error)
                }
            }

            cards.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Your vocabulary is empty.",
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Add your first word to get started.")
                    }
                }
            }

            else -> {
                val current = cards[index]

                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = "Card ${index + 1} of ${cards.size}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Polished Flashcard UI
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(vertical = 40.dp, horizontal = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (showEnglish) current.englishTranslation else current.frenchWord,
                                style = MaterialTheme.typography.headlineLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Polished button row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Button(
                            onClick = {
                                if (cards.isNotEmpty()) {
                                    index = if (index == 0) cards.lastIndex else index - 1
                                    showEnglish = false
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Previous")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { showEnglish = !showEnglish },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Flip")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (cards.isNotEmpty()) {
                                    index = if (index == cards.lastIndex) 0 else index + 1
                                    showEnglish = false
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Next")
                        }
                    }
                }
            }
        }
    }
}
