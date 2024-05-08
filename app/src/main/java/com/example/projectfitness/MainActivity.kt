package com.example.projectfitness

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.auth.FirebaseAuth
import database.Exercise
import database.ProjectFitnessContainer
import database.ProjectFitnessExerciseEntity
import database.ProjectFitnessWorkoutEntity
import database.SetRep
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import navigation.Navigation

class MainActivity : ComponentActivity() {
    private lateinit var auth : FirebaseAuth

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            var projectFitnessContainer = ProjectFitnessContainer(context)
            val itemRepo = projectFitnessContainer.itemsRepository

            val systemUiController = rememberSystemUiController()
            SideEffect {
                systemUiController.setStatusBarColor(Color(0xFF181F26))
                systemUiController.setSystemBarsColor(Color(0xFF181F26))
            }

            LaunchedEffect(key1 = Unit) {
                CoroutineScope(Dispatchers.IO).launch {
                if (itemRepo.getWorkoutWithExercises("Base").isNotEmpty()){
                    // Pass
                }
                else
                itemRepo.insertItem(ProjectFitnessWorkoutEntity(workoutId = 0,workoutName = "Base", exercises = mutableStateListOf(Exercise("Base",0,0))))
                delay(5000)
                itemRepo.insertItems(ProjectFitnessExerciseEntity( ids = 2, exerciseId = 1, exercisesName ="Base", exercisesRep=0, exercisesSet = 0,setrepList = mutableStateListOf(
                    SetRep("Base", setRep = 0,ticked = false, weight = 0f)
                )))
                }
            }
            Main()
        }

    }

}

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Main() {
    Navigation()
}