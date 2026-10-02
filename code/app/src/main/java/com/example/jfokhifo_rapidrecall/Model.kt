package com.example.jfokhifo_rapidrecall

import kotlin.random.Random

/**
Stores the details of one completed RapidRecall round. Keeps the
round's length, target, guess, result, and timestamp together as
an immutable record for the session log. Timestamp is stored as
milliseconds and formatted by the View for display. No known
outstanding issues.
 */
data class Attempt(
    val sequenceLength: Int,
    val userInput: String,
    val targetSequence: String,
    val isCorrect: Boolean,
    val timestampMillis: Long
)

/**
Holds RapidRecall's session data and game rules. Generates
target sequences, records attempts, and calculates summary
values separately from the Controller and Compose Views.
Sequence generation assumes the Controller provides a length
from 1 to 10. Outstanding issues: attempt history is kept in
memory only, as allowed by the assignment.
 */
class RapidRecallModel {
    private val attemptHistory = mutableListOf<Attempt>()

    /**
    Generates a random digit string of the requested length.
    Returning a String preserves leading zeroes.
     */
    fun generateTargetSequence(length: Int): String =
        List(length) { Random.nextInt(0, 10) }.joinToString("")

    /**
    Creates an attempt with its result and timestamp, adds it
    to session history, and returns the recorded attempt.
     */
    fun recordAttempt(
        sequenceLength: Int,
        userInput: String,
        targetSequence: String
    ): Attempt {
        val attempt = Attempt(
            sequenceLength = sequenceLength,
            userInput = userInput,
            targetSequence = targetSequence,
            isCorrect = userInput == targetSequence,
            timestampMillis = System.currentTimeMillis()
        )

        attemptHistory.add(attempt)
        return attempt
    }

    /**
    Returns a read-only copy so callers cannot modify the
    Model's internal history.
     */
    fun getAttempts(): List<Attempt> = attemptHistory.toList()

    /**
    Returns the number of attempts whose guesses matched their
    target sequence.
     */
    fun getCorrectCount(): Int =
        attemptHistory.count { it.isCorrect }

    /**
    Returns correct attempts as a whole-number percentage of
    all attempts. Returns zero when there are no attempts.
     */
    fun getAccuracyPercent(): Int =
        if (attemptHistory.isEmpty()) 0
        else getCorrectCount() * 100 / attemptHistory.size
}