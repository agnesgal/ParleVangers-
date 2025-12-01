# ParleVangers – French Learning App
**Assignment 3 – Mobile and Pervasive Computing**  
**Student:** Agnes Gal  
**Student ID:** 200575611

---

## 📘 Overview
ParleVangers is a simple French vocabulary learning app.  
Users can log in with Google and review flashcards retrieved from Firebase Firestore in real time.  
This submission includes UI upgrades, database security, and app publication as required for Assignment 3.

---

## 🔥 Upgrades Since Assignment 2

### ✔ 1. UI Improvements (Login, Register, Flashcards)
All main screens were visually upgraded using Material Design 3:
- Added centered titles and better typography
- Added rounded Card containers with elevation
- Improved spacing and padding
- More consistent buttons and layout
- Cleaner Flashcard UI with polished card design

---

## 🔐 2. Firebase Database Security
Firestore rules were updated to restrict access:

- Each user can only read/write their own `/users/{uid}` document
- Flashcards (`user_vocabulary`) are read-only for all users
- All other documents are denied by default

These rules secure the database and meet Assignment 3 requirements.

---

## 🔤 3. Authentication
Google Sign-In is fully functional using Firebase Authentication.  
Successful logins automatically create a user profile document in Firestore.

(Email registration is disabled in this version.)

---

## 📚 4. Firestore Flashcards
Flashcards are loaded in real time from the `user_vocabulary` collection and displayed through a modern flashcard UI.

No logic was changed for Assignment 3 — only UI enhancements.

---

## 🎨 5. Custom Launcher Icon
A new launcher icon was generated using Android Studio’s Image Asset tool.  
The app now includes adaptive and legacy icons across all mipmap densities.

---

## 📦 6. App Publication (APK)
A compiled APK is included directly in the D2L submission:

**File:** `app-debug.apk`

The app can be installed on any Android device or emulator via the APK.

---

## 🔧 Version Control
The project includes descriptive and meaningful commits for Assignment 3 updates.

GitHub mirror (optional):  
https://github.com/agnesgal/ParleVangers-

---

## ✅ Summary
All Assignment 3 requirements are completed:
- Notable UI upgrades
- Improved UX
- Firebase secured
- Custom launcher icon
- APK published
- README updated

ParleVangers is now a polished and functional prototype ready for review.
