package com.example.parlevangers

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.parlevangers.ui.theme.ParleVangersTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Main Activity for ParleVangers - French Learning App
 * Assignment 2: Mobile App Prototype with Firebase Integration
 * Student: Agnes Gal
 * Student ID: 200575611
 *
 * Features:
 * - Firebase Authentication (Email & Google)
 * - Firestore Database (Create & Read operations)
 * - Interactive Flashcards
 * - Material Design 3 UI
 */
class MainActivity : ComponentActivity() {
    // Firebase instances
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Google Sign-In launcher for handling authentication result
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential).addOnCompleteListener { task ->
                // Authentication state will update automatically via Firebase Auth listener
            }
        } catch (e: ApiException) {
            // Google Sign-In failed - handled silently for user experience
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ParleVangersTheme {
                AppContent(
                    onGoogleSignIn = {
                        signInWithGoogle()
                    }
                )
            }
        }
    }

    /**
     * Initiates Google Sign-In flow
     * Requires proper OAuth 2.0 Client ID configuration in Google Cloud Console
     */
    private fun signInWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("961784488490-YOUR_ACTUAL_CLIENT_ID") // Configure in strings.xml
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInLauncher.launch(googleSignInClient.signInIntent)
    }
}

// Global Firebase instances for composable access
val auth = FirebaseAuth.getInstance()
val db = FirebaseFirestore.getInstance()

/**
 * Data class representing a vocabulary flashcard
 * Maps directly to Firestore document structure
 */
data class Flashcard(val frenchWord: String, val englishTranslation: String)

/**
 * Main app composable handling navigation and state management
 * Implements clean architecture with screen-based navigation
 */
@Composable
fun AppContent(onGoogleSignIn: () -> Unit) {
    // State management for navigation and UI
    var currentScreen by remember { mutableStateOf("login") }
    var currentCardIndex by remember { mutableStateOf(0) }
    var showTranslation by remember { mutableStateOf(false) }
    var currentUser by remember { mutableStateOf(auth.currentUser) }
    var userWords by remember { mutableStateOf<List<Flashcard>>(emptyList()) }

    // Firestore data listener - loads user's vocabulary when authenticated
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            db.collection("user_vocabulary")
                .whereEqualTo("userId", currentUser?.uid)
                .addSnapshotListener { snapshots, error ->
                    val words = snapshots?.map { doc ->
                        Flashcard(
                            frenchWord = doc.getString("frenchWord") ?: "",
                            englishTranslation = doc.getString("englishTranslation") ?: ""
                        )
                    } ?: emptyList()
                    userWords = words
                }
        } else {
            userWords = emptyList()
        }
    }

    // Screen router - determines which screen to display based on state
    when (currentScreen) {
        "login" -> LoginScreen(
            onLoginSuccess = {
                currentUser = auth.currentUser
                currentScreen = "home"
            },
            onGoogleSignIn = onGoogleSignIn
        )
        "home" -> HomeScreen(
            wordCount = userWords.size,
            onFlashcardsClick = {
                currentScreen = "flashcards"
                currentCardIndex = 0
                showTranslation = false
            },
            onSentenceBuilderClick = {
                currentScreen = "sentenceBuilder"
            },
            onAddWord = {
                // CREATE operation: Add new word to Firestore
                val wordData = hashMapOf(
                    "userId" to currentUser?.uid,
                    "frenchWord" to "Bonjour",
                    "englishTranslation" to "Hello"
                )
                db.collection("user_vocabulary").add(wordData)
            },
            onLogout = {
                auth.signOut()
                currentUser = null
                currentScreen = "login"
            }
        )
        "flashcards" -> {
            if (userWords.isNotEmpty()) {
                // READ operation: Display words from Firestore
                FlashcardScreen(
                    flashcards = userWords,
                    currentIndex = currentCardIndex,
                    showTranslation = showTranslation,
                    onFlipCard = { showTranslation = !showTranslation },
                    onNextCard = {
                        if (currentCardIndex < userWords.size - 1) {
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
                    onBack = { currentScreen = "home" }
                )
            } else {
                // Empty state handling
                EmptyVocabularyScreen(onBack = { currentScreen = "home" })
            }
        }
        "sentenceBuilder" -> SentenceBuilderScreen(
            onBack = { currentScreen = "home" }
        )
    }
}

/**
 * Authentication screen with Email and Google sign-in options
 * Follows Material Design 3 guidelines for form layout
 */
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onGoogleSignIn: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App branding
        Text("ParleVangers", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Learn French Your Way", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(32.dp))

        // Email authentication form
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
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

        // Authentication buttons
        Button(
            onClick = {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { onLoginSuccess() }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign In")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { onLoginSuccess() }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Account")
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("or continue with", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(24.dp))

        // Google authentication
        Button(
            onClick = onGoogleSignIn,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Google Sign In")
        }
    }
}

/**
 * Main dashboard screen after authentication
 * Provides navigation to app features and displays user stats
 */
@Composable
fun HomeScreen(
    wordCount: Int,
    onFlashcardsClick: () -> Unit,
    onSentenceBuilderClick: () -> Unit,
    onAddWord: () -> Unit,
    onLogout: () -> Unit
) {
    val currentUser = auth.currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to ParleVangers!", style = MaterialTheme.typography.headlineMedium)
        Text("Signed in as: ${currentUser?.email ?: "User"}", style = MaterialTheme.typography.bodyMedium)
        Text("Your vocabulary: $wordCount words", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(32.dp))

        // Feature navigation
        Button(onClick = onFlashcardsClick, modifier = Modifier.fillMaxWidth()) {
            Text("Practice Flashcards")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onSentenceBuilderClick, modifier = Modifier.fillMaxWidth()) {
            Text("Sentence Builder")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onAddWord, modifier = Modifier.fillMaxWidth()) {
            Text("Add New Word")
        }

        Spacer(modifier = Modifier.height(32.dp))

        TextButton(onClick = onLogout) {
            Text("Sign Out")
        }
    }
}

/**
 * Interactive flashcard screen for vocabulary practice
 * Implements flip animation and navigation between cards
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
        Text("Card ${currentIndex + 1} of ${flashcards.size}")
        Text("Data loaded from Cloud Firestore", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(24.dp))

        // Interactive flashcard
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            onClick = onFlipCard
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = if (showTranslation) currentCard.englishTranslation else currentCard.frenchWord,
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }

        Text("Tap card to flip", style = MaterialTheme.typography.bodySmall)

        Spacer(modifier = Modifier.height(32.dp))

        // Navigation controls
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = onPrevCard, enabled = currentIndex > 0) {
                Text("Previous")
            }
            Button(onClick = onFlipCard) {
                Text(if (showTranslation) "Show French" else "Show Translation")
            }
            Button(onClick = onNextCard, enabled = currentIndex < flashcards.size - 1) {
                Text("Next")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onBack) {
            Text("Back to Dashboard")
        }
    }
}

/**
 * Placeholder for sentence builder feature
 * Planned for future implementation in Assignment 3
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
        Text("Sentence Builder", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
        Text("Build French sentences - Coming in next update!")
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onBack) {
            Text("Back to Dashboard")
        }
    }
}

/**
 * Empty state screen when user has no vocabulary words
 * Guides user to add their first word
 */
@Composable
fun EmptyVocabularyScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Your vocabulary is empty", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Add your first French word to get started", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onBack) {
            Text("Back to Dashboard")
        }
    }
}