package com.example.homewokoutapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.homewokoutapp.data.MuscleGroup
import com.example.homewokoutapp.screens.ExercisesScreen
import com.example.homewokoutapp.screens.MuscleGroupsScreen
import com.example.homewokoutapp.screens.WorkoutScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WorkoutApp()
                }
            }
        }
    }
}

@Composable
fun WorkoutApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "muscleGroups") {
        composable("muscleGroups") {
            MuscleGroupsScreen(navController)
        }
        composable("exercises/{muscleGroup}") { backStackEntry ->
            val muscleGroup = MuscleGroup.valueOf(backStackEntry.arguments?.getString("muscleGroup") ?: "CHEST")
            ExercisesScreen(muscleGroup, navController)
        }
        composable("workout/{muscleGroup}/{restDuration}") { backStackEntry ->
            val muscleGroupStr = backStackEntry.arguments?.getString("muscleGroup") ?: "CHEST"
            val muscleGroup = try {
                MuscleGroup.valueOf(muscleGroupStr.uppercase())  // Biztonságos uppercase
            } catch (e: IllegalArgumentException) {
                MuscleGroup.CHEST  // Fallback
            }
            val restDurationStr = backStackEntry.arguments?.getString("restDuration") ?: "60"
            val restDuration = restDurationStr.toIntOrNull() ?: 60
            WorkoutScreen(muscleGroup, restDuration, navController)
        }
    }
}



