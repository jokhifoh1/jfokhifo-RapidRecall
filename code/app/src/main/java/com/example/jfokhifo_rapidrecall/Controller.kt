package com.example.jfokhifo_rapidrecall

import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

/**
Coordinates RapidRecall UI actions and game state, delegating
game data and rules to RapidRecallModel. Exposes observable
state for Compose Views while keeping sequence generation,
attempt history, and summary calculations in the Model.
Outstanding issues: session data is not persisted after the app
process ends, as allowed by the assignment.
 */
class RapidRecallController(
    private val model: RapidRecallModel = RapidRecallModel()
) {
    var currentScreen by mutableStateOf("home")
        private set

    var selectedLength by mutableIntStateOf(1)
        private set

    var targetSequence by mutableStateOf("")
        private set

    val attempts: List<Attempt>
        get() = model.getAttempts()

    var displayedDigit by mutableStateOf("")
        private set

    var sequenceFinished by mutableStateOf(false)
        private set

    val correctCount: Int
        get() = model.getCorrectCount()

    val accuracyPercent: Int
        get() = model.getAccuracyPercent()

    var guess by mutableStateOf("")
        private set

    var feedback by mutableStateOf<String?>(null)
        private set

    var hasSubmitted by mutableStateOf(false)
        private set

    /**
    Opens the sequence-length selection screen.
     */
    fun openSetup() {
        currentScreen = "setup"
    }

    /**
    Starts a round for a valid length from 1 to 10 and resets round state.
     */
    fun chooseLength(length: Int) {
        if (length !in 1..10) return

        guess = ""
        feedback = null
        hasSubmitted = false
        selectedLength = length
        targetSequence = model.generateTargetSequence(length)
        displayedDigit = ""
        sequenceFinished = false
        currentScreen = "game"
    }

    /**
    Keeps only digit characters up to the selected length and
    clears old feedback.
     */
    fun updateGuess(input: String) {
        if (hasSubmitted) return

        guess = input.filter { it.isDigit() }.take(selectedLength)
        feedback = null
    }

    /**
    Validates the guess, records one attempt in the Model, and updates feedback.
     */
    fun submitGuess() {
        if (!sequenceFinished || hasSubmitted || guess.length != selectedLength) return

        val attempt = model.recordAttempt(selectedLength, guess, targetSequence)

        feedback = if (attempt.isCorrect) {
            "Correct!"
        } else {
            "Incorrect. Correct sequence: ${attempt.targetSequence}. Your answer: ${attempt.userInput}"
        }

        hasSubmitted = true
    }

    /**
    Opens the session attempt log.
     */
    fun openLog() {
        currentScreen = "log"
    }

    /**
    Opens the session attempt summary.
     */
    fun openSummary() {
        currentScreen = "summary"
    }

    /**
    Returns to the start screen.
     */
    fun returnHome() {
        currentScreen = "home"
    }

    /**
    Reveals the target one digit at a time, pausing between
    digits. Called from the View's LaunchedEffect so it is
    cancelled when the game screen leaves composition.
     */
    suspend fun revealSequence() {
        sequenceFinished = false

        targetSequence.forEach { digit ->
            displayedDigit = digit.toString()
            delay(1000.milliseconds)
        }

        displayedDigit = ""
        sequenceFinished = true
    }
}