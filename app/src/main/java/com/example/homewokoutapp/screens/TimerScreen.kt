package com.example.homewokoutapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.homewokoutapp.data.Exercise
import kotlinx.coroutines.delay

@Composable
fun TimerScreen(exerciseName: String, navController: NavController) {
    var timeLeft by remember { mutableIntStateOf(30) }
    var isTimerRunning by remember { mutableStateOf(false) }
    var completedReps by remember { mutableStateOf("") }
    val exercise = remember {
        Exercise(exerciseName, 15)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Gyakorlat: $exerciseName",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Cél: ${exercise.repsGoal} ismétlés",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Visszaszámoló kijelző
        Text(
            text = "${timeLeft}s",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(32.dp)
        )

        // Timer gombok
        Row {
            Button(
                onClick = {
                    if (!isTimerRunning) {
                        isTimerRunning = true
                        timeLeft = exercise.durationSeconds
                    }
                },
                enabled = !isTimerRunning
            ) {
                Text("Indítsd a timert!")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    isTimerRunning = false
                    timeLeft = exercise.durationSeconds
                },
                enabled = isTimerRunning
            ) {
                Text("Stop")
            }
        }

        // Timer logika (Coroutine-nel)
        LaunchedEffect(isTimerRunning) {
            if (isTimerRunning) {
                while (timeLeft > 0 && isTimerRunning) {
                    delay(1000L)
                    timeLeft--
                }
                if (timeLeft == 0) isTimerRunning = false
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Reps input
        TextField(
            value = completedReps,
            onValueChange = { completedReps = it },
            label = { Text("Megtett ismétlések") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Itt mentheted el a reps-et, pl. logba vagy adatbázisba
                println("Mentve: $exerciseName - $completedReps reps")
                navController.popBackStack()
            }
        ) {
            Text("Mentés és vissza")
        }
    }
}