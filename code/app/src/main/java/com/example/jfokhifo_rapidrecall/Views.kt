package com.example.jfokhifo_rapidrecall

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
Root View for RapidRecall. Connects the Controller's screen
state and event callbacks to the Compose screen that should
be displayed. No known outstanding issues.
 */
@Composable
fun RapidRecallApp(modifier: Modifier = Modifier) {
    val controller = remember { RapidRecallController() }

    when (controller.currentScreen) {
        "home" -> HomeScreen(
            modifier = modifier,
            onStart = { controller.openSetup() },
            onViewLog = { controller.openLog() },
            onViewSummary = { controller.openSummary() }
        )

        "setup" -> SetupScreen(
            modifier = modifier,
            onLengthSelected = { length -> controller.chooseLength(length) },
            onReturnHome = { controller.returnHome() }
        )

        "game" -> GameScreen(
            modifier = modifier,
            sequenceLength = controller.selectedLength,
            targetSequence = controller.targetSequence,
            displayedDigit = controller.displayedDigit,
            sequenceFinished = controller.sequenceFinished,
            guess = controller.guess,
            feedback = controller.feedback,
            hasSubmitted = controller.hasSubmitted,
            onRevealSequence = { controller.revealSequence() },
            onGuessChange = { controller.updateGuess(it) },
            onSubmitGuess = { controller.submitGuess() },
            onBack = { controller.openSetup() },
            onReturnHome = { controller.returnHome() }
        )

        "log" -> AttemptLogScreen(
            modifier = modifier,
            attempts = controller.attempts,
            onBack = { controller.returnHome() }
        )

        "summary" -> SummaryScreen(
            modifier = modifier,
            totalAttempts = controller.attempts.size,
            correctCount = controller.correctCount,
            accuracyPercent = controller.accuracyPercent,
            onBack = { controller.returnHome() }
        )
    }
}

/**
Displays the app's start screen and forwards navigation choices
to the Controller. Keeps home-screen rendering separate from
navigation behavior. No known outstanding issues.
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onStart: () -> Unit,
    onViewLog: () -> Unit,
    onViewSummary: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "RapidRecall",
            style = MaterialTheme.typography.headlineLarge
        )

        Button(onClick = onStart) {
            Text("Start")
        }

        Button(onClick = onViewLog) {
            Text("Attempt Log")
        }

        Button(onClick = onViewSummary) {
            Text("Attempt Summary")
        }
    }
}

/**
Displays the selectable sequence lengths from 1 to 10 and a way
to return home. A lazy column list keeps every length choosable
on small buttons. callbacks send the player's choice to the
Controller. No known outstanding issues.
 */
@Composable
fun SetupScreen(
    modifier: Modifier = Modifier,
    onLengthSelected: (Int) -> Unit,
    onReturnHome: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Choose a sequence length",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            items(count = 10) { index ->
                val length = index + 1

                Button(
                    onClick = { onLengthSelected(length) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("$length digit${if (length == 1) "" else "s"}")
                }
            }
        }

        Button(
            onClick = onReturnHome,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to start")
        }
    }
}

/**
Displays the active sequence, guess field, feedback, and round
navigation. Renders state supplied by the Controller and
forwards player actions through callbacks. It does not generate
sequences or record attempts. No known outstanding issues.
 */
@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    sequenceLength: Int,
    targetSequence: String,
    displayedDigit: String,
    sequenceFinished: Boolean,
    guess: String,
    feedback: String?,
    hasSubmitted: Boolean,
    onRevealSequence: suspend () -> Unit,
    onGuessChange: (String) -> Unit,
    onSubmitGuess: () -> Unit,
    onBack: () -> Unit,
    onReturnHome: () -> Unit
) {
    LaunchedEffect(targetSequence) {
        onRevealSequence()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Remember the sequence")

        Text(
            text = displayedDigit,
            style = MaterialTheme.typography.displayLarge
        )

        if (sequenceFinished) {
            Text("Sequence finished.")
        }

        OutlinedTextField(
            value = guess,
            onValueChange = onGuessChange,
            label = { Text("Your sequence") },
            singleLine = true,
            enabled = sequenceFinished && !hasSubmitted
        )

        Button(
            onClick = onSubmitGuess,
            enabled = sequenceFinished &&
                    guess.length == sequenceLength &&
                    !hasSubmitted
        ) {
            Text("Check answer")
        }

        feedback?.let { message ->
            Text(message)
        }

        Button(onClick = onBack) {
            Text("Back to length selection")
        }

        Button(onClick = onReturnHome) {
            Text("Return to start screen")
        }
    }
}

/**
Displays completed attempts from the current session, newest
first. Formats the Model's attempt records for the player
without changing or storing them. No known outstanding issues.
 */
@Composable
fun AttemptLogScreen(
    modifier: Modifier = Modifier,
    attempts: List<Attempt>,
    onBack: () -> Unit
) {
    val dateFormatter = remember {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Attempt Log",
            style = MaterialTheme.typography.headlineMedium
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (attempts.isEmpty()) {
                item { Text("No attempts recorded yet.") }
            } else {
                items(attempts.reversed()) { attempt ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Length: ${attempt.sequenceLength} digits")
                        Text("Target: ${attempt.targetSequence}")
                        Text("Your input: ${attempt.userInput}")
                        Text(if (attempt.isCorrect) "Result: Correct" else "Result: Incorrect")
                        Text("Time: ${dateFormatter.format(Date(attempt.timestampMillis))}\n")
                    }
                }
            }
        }

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to start")
        }
    }
}

/**
Displays the current session's total attempts, correct attempts,
and accuracy. Shows summary values supplied by the Controller
instead of calculating game results in the View. No known
outstanding issues.
 */
@Composable
fun SummaryScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    totalAttempts: Int,
    correctCount: Int,
    accuracyPercent: Int
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Attempt Summary",
            style = MaterialTheme.typography.headlineMedium
        )
        Text("Total attempts: $totalAttempts")
        Text("Correct attempts: $correctCount")
        Text("Accuracy: $accuracyPercent%")

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to start")
        }
    }
}