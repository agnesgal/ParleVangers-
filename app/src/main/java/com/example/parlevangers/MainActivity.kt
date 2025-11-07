package com.example.parlevangers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.parlevangers.ui.theme.ParleVangersTheme

/**
 * Main Activity for ParleVangers - French Learning App
 * Student: Agnes Gal
 * Student ID: 200575611
 * Course: COMP3025-25F-12715 - Mobile and Pervasive Computing
 * Assignment: 1b - App Planning and Shell
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ParleVangersTheme {
                // This is the root of our composable app
                AppContent()
            }
        }
    }
}

// DATA MODELS
// In a real app, this would come from Firebase Firestore
// For Assignment 1b, we use sample data to demonstrate the structure
data class Flashcard(val frenchWord: String, val englishTranslation: String)

// SAMPLE DATA - Simulates what will be stored in Firebase collections
// Collections planned: 'user_vocabulary', 'sentences', 'user_progress'
val sampleFlashcards = listOf(
    Flashcard("Bonjour", "Hello"),
    Flashcard("Merci", "Thank you"),
    Flashcard("Au revoir", "Goodbye"),
    Flashcard("S'il vous plaît", "Please"),
    Flashcard("Excusez-moi", "Excuse me")
)

/**
 * MAIN APP CONTROLLER
 * Handles navigation between different screens using state management
 * This demonstrates the multi-activity structure required for the assignment
 */
@Composable
fun AppContent() {
    // State management for screen navigation - mimics Android's navigation component
    var currentScreen by remember { mutableStateOf("home") }
    var currentCardIndex by remember { mutableStateOf(0) }
    var showTranslation by remember { mutableStateOf(false) }

    // Screen router - determines which screen to show based on state
    when (currentScreen) {
        "home" -> HomeScreen(
            onFlashcardsClick = {
                currentScreen = "flashcards"
                currentCardIndex = 0
                showTranslation = false
            },
            onSentenceBuilderClick = {
                // TODO: Implement in Assignment 2 - this shows planned feature
                currentScreen = "sentenceBuilder"
            }
        )
        "flashcards" -> FlashcardScreen(
            flashcards = sampleFlashcards,
            currentIndex = currentCardIndex,
            showTranslation = showTranslation,
            onFlipCard = { showTranslation = !showTranslation },
            onNextCard = {
                if (currentCardIndex < sampleFlashcards.size - 1) {
                    currentCardIndex++
                    showTranslation = false
                }
            },
            onPrevCard = {
                if (currentCardIndex > 0) {
                    currentCardIndex--
                    showTranslation = false
                }
            },
            onBack = {
                currentScreen = "home"
            }
        )
        "sentenceBuilder" -> SentenceBuilderScreen(
            onBack = { currentScreen = "home" }
        )
    }
}

/**
 * HOME SCREEN - Main menu of the application
 * Demonstrates Material Design principles with clean layout
 * Shows the two main features: Flashcards and Sentence Builder
 */
@Composable
fun HomeScreen(
    onFlashcardsClick: () -> Unit,
    onSentenceBuilderClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App title following Material Design typography scale
        Text(
            text = "Welcome to ParleVangers!",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Flashcards feature button
        Button(
            onClick = onFlashcardsClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start Flashcards")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sentence Builder feature button (planned for Assignment 2)
        Button(
            onClick = onSentenceBuilderClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sentence Builder")
        }
    }
}

/**
 * FLASHCARD SCREEN - Interactive vocabulary learning
 * Shows the flashcard implementation that will connect to Firebase in Assignment 2
 * Features: card flipping, navigation, progress tracking
 */
@Composable
fun FlashcardScreen(
    flashcards: List<Flashcard>,
    currentIndex: Int,
    showTranslation: Boolean,
    onFlipCard: () -> Unit,
    onNextCard: () -> Unit,
    onPrevCard: () -> Unit,
    onBack: () -> Unit
) {
    val currentCard = flashcards[currentIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress indicator - shows user their position in the deck
        Text("Card ${currentIndex + 1} of ${flashcards.size}")
        Spacer(modifier = Modifier.height(24.dp))

        // Interactive flashcard - click to flip between French and English
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            onClick = onFlipCard
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (showTranslation) currentCard.englishTranslation else currentCard.frenchWord,
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }

        // User instruction following UX best practices
        Text(
            text = "Tap card to flip",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Navigation controls - enable/disable based on position
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onPrevCard,
                enabled = currentIndex > 0
            ) {
                Text("Previous")
            }

            Button(onClick = onFlipCard) {
                Text(if (showTranslation) "Show French" else "Show English")
            }

            Button(
                onClick = onNextCard,
                enabled = currentIndex < flashcards.size - 1
            ) {
                Text("Next")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Back navigation
        Button(onClick = onBack) {
            Text("Back to Home")
        }
    }
}

/**
 * SENTENCE BUILDER SCREEN - Placeholder for Assignment 2 feature
 * This demonstrates the planned multi-activity structure mentioned in the assignment
 * Will implement drag-and-drop functionality in the next phase
 */
@Composable
fun SentenceBuilderScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sentence Builder",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text("Coming in Assignment 2!") // Placeholder for future implementation
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onBack) {
            Text("Back to Home")
        }
    }
}

/**
 * PREVIEW FUNCTION - For Android Studio design tool
 * Helps with UI development without running the app
 */
@Preview(showBackground = true)
@Composable
fun AppPreview() {
    ParleVangersTheme {
        AppContent()
    }
}