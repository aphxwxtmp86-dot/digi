# Digital Answer Sheet — Android starter

A native Android starter project written in Kotlin + Jetpack Compose. It is not an HTML/web app.

## Current prototype
- Formal, minimal purple/white visual direction
- Create an exam set with title, 1–1,000 questions, 2–5 choices
- Choose single-answer or multiple-answer mode per exam
- Choose 1, 2, or 3 answer-sheet columns
- Optional timer toggle is represented in exam setup (timer behavior still needs implementation)
- Tap bubble answers and submit confirmation

## Important implementation status
This is an initial native UI prototype, not a finished exam app. Answer persistence, answer-key editor, grading modes, timer countdown/auto-submit, history, export/backup, and full result reports remain to be implemented.

The current execution environment has Java but does not have the Android SDK or Gradle installed, so an APK could not be compiled or verified here.

## Build with Android Studio
1. Open this folder in Android Studio.
2. Allow Gradle sync and install Android SDK Platform 35 if prompted.
3. Build > Build Bundle(s) / APK(s) > Build APK(s).
4. The debug APK is normally at `app/build/outputs/apk/debug/app-debug.apk`.

Package: `com.example.digitalanswersheet`
Minimum Android: API 26 (Android 8.0)
