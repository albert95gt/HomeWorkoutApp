package com.example.homewokoutapp.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.homewokoutapp.components.WorkoutVideoPlayer
import com.example.homewokoutapp.data.MuscleGroup
import com.example.homewokoutapp.data.SetData
import com.example.homewokoutapp.data.muscleGroupsExercises
import kotlinx.coroutines.delay

@Composable
fun WorkoutScreen(muscleGroup: MuscleGroup, restDuration: Int, navController: NavHostController) {
    val exercises = remember { muscleGroupsExercises[muscleGroup] ?: emptyList() }
    var currentIndex by remember { mutableIntStateOf(0) }
    var currentSetIndex by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(0) }  // Szünet visszaszámláló
    var isTimerRunning by remember { mutableStateOf(false) }  // Szünet timer futása
    var isWorkoutStarted by remember { mutableStateOf(false) }  // Teljes edzés állapot
    var isWorkoutPaused by remember { mutableStateOf(false) }  // Teljes edzés pause
    var workoutTime by remember { mutableLongStateOf(0L) }  // Teljes edzés idő
    var repsText by remember { mutableStateOf("") }
    var weightText by remember { mutableStateOf("") }
    var currentSets by remember { mutableStateOf<List<SetData>>(emptyList()) }
    val restTime = restDuration
    val currentExercise = exercises.getOrNull(currentIndex)
    val coroutineScope = rememberCoroutineScope()

    // Teljes edzés idő számláló (pause-olható)
    LaunchedEffect(isWorkoutStarted, isWorkoutPaused) {
        if (isWorkoutStarted && !isWorkoutPaused) {
            while (isWorkoutStarted && !isWorkoutPaused) {
                delay(1000L)
                workoutTime++
            }
        }
    }

    // Szünet timer logika (set mentés után indul, csak set-ek között)
    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning) {
            while (timeLeft > 0 && isTimerRunning) {
                delay(1000L)
                timeLeft--
            }
            if (timeLeft == 0) {
                isTimerRunning = false
                // Szünet vége: Next set (csak ha set-ek között vagyunk)
                if (currentSetIndex < 2) {
                    currentSetIndex++
                    repsText = ""
                    weightText = ""
                } else {
                    // Ha 3. set után vagyunk, ez itt nem fut, mert a mentés kezeli a next gyakorlatot
                }
            }
        }
    }

    // Force recompose videó frissítéshez gyakorlatváltáskor
    LaunchedEffect(currentIndex) {
        // Üres, de a key biztosítja a recompose-t
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (currentExercise != null) {
            item {
                // Fejléc Card (teljes idővel)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${muscleGroup.displayName} edzés - ${currentIndex + 1}/${exercises.size}",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "Edzés ideje: ${String.format("%02d:%02d", workoutTime / 60, workoutTime % 60)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = currentExercise.name,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "Set ${currentSetIndex + 1}/3 - Cél: ${currentExercise.repsGoal} ismétlés",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            item {
                // Videó Card (currentIndex átadva)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            currentExercise.videoResId?.let { resId ->
                                key(currentIndex) {
                                    Box(modifier = Modifier
                                        .fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ){
                                        WorkoutVideoPlayer(
                                            videoResId = resId,
                                            currentIndex = currentIndex,
                                            modifier = Modifier
                                                .padding(bottom = 8.dp)
                                                .size(width = 260.dp, height = 200.dp)
                                        )
                                    }

                                }
                            } ?: Text(
                                text = "Nincs videó ehhez a gyakorlathoz",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            item {
                // Korábbi set-ek összefoglalója (ha van)
                if (currentSets.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            text = "Korábbi set-ek: ${currentSets.joinToString { "${it.first}x${it.second}kg" }}",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            item {
                // Szünet visszaszámláló Card (csak set-ek között, ha fut)
                AnimatedVisibility(
                    visible = isTimerRunning,
                    enter = scaleIn(),
                    exit = scaleOut()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Szünet: ${timeLeft}s",
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = {
                                    isTimerRunning = false  // Megszakítás
                                    if (currentSetIndex < 2) {
                                        currentSetIndex++
                                    }
                                },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text("Szünet megszakítás")
                            }
                        }
                    }
                }
            }

            item {
                // Gombok Row Card (edzés indítás/pause/stop)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                if (!isWorkoutStarted) {
                                    // Első indítás
                                    isWorkoutStarted = true
                                    workoutTime = 0L
                                } else if (isWorkoutPaused) {
                                    // Resume
                                    isWorkoutPaused = false
                                } else {
                                    // Pause
                                    isWorkoutPaused = true
                                }
                            },
                            enabled = true,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (!isWorkoutStarted) "Indítsd az edzést!" else if (isWorkoutPaused) "Folytatás" else "Pause")
                        }
                        OutlinedButton(
                            onClick = {
                                if (isWorkoutStarted) {
                                    // Stop: Megállít, mutat időt, vissza lép
                                    println("Edzés leállítva: ${muscleGroup.displayName} (${String.format("%02d:%02d", workoutTime / 60, workoutTime % 60)})")
                                    isWorkoutStarted = false
                                    isWorkoutPaused = false
                                    workoutTime = 0L
                                    navController.popBackStack("muscleGroups", inclusive = false)
                                }
                            },
                            enabled = isWorkoutStarted,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Megállítás (${String.format("%02d:%02d", workoutTime / 60, workoutTime % 60)})")
                        }
                    }
                }
            }

            item {
                // Inputok Card (set input, látható edzés alatt, nem szünetben)
                AnimatedVisibility(
                    visible = isWorkoutStarted && !isTimerRunning,
                    enter = scaleIn(),
                    exit = scaleOut()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Írd be a teljesítményt (Set ${currentSetIndex + 1}/3):",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                TextField(
                                    value = repsText,
                                    onValueChange = { if (it.matches(Regex("\\d*"))) repsText = it },
                                    label = { Text("Ismétlések") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                                TextField(
                                    value = weightText,
                                    onValueChange = {
                                        if (it.matches(Regex("\\d*([.]\\d{0,1})?"))) {
                                            weightText = it
                                        }
                                    },
                                    label = { Text("Súly (kg)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    val reps = repsText.toIntOrNull() ?: 0
                                    val weight = weightText.toDoubleOrNull() ?: 0.0
                                    if (reps > 0 && weight > 0) {
                                        val newSets = currentSets + SetData(reps, weight)
                                        currentSets = newSets
                                        repsText = ""
                                        weightText = ""
                                        if (currentSetIndex < 2) {
                                            // Set-ek között: Szünet indul
                                            timeLeft = restTime
                                            isTimerRunning = true
                                        } else {
                                            // 3. set után: Next gyakorlat (szünet nélkül, auto-váltás)
                                            println("Mentve: ${currentExercise.name} - Sets: $currentSets")
                                            currentSets = emptyList()
                                            if (currentIndex < exercises.size - 1) {
                                                currentIndex++
                                                currentSetIndex = 0
                                            } else {
                                                // Teljes edzés vége
                                                println("Edzés kész: ${muscleGroup.displayName} (${String.format("%02d:%02d", workoutTime / 60, workoutTime % 60)})")
                                                isWorkoutStarted = false
                                                isWorkoutPaused = false
                                                workoutTime = 0L
                                            }
                                        }
                                    }
                                },
                                enabled = (repsText.toIntOrNull() ?: 0 > 0) && (weightText.toDoubleOrNull() ?: 0.0 > 0)
                            ) {
                                Text(if (currentSetIndex < 2) "Mentés és szünet" else "Mentés és következő gyakorlat")
                            }
                        }
                    }
                }
            }
        } else {
            item {
                Text("Nincs gyakorlat.", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}