package com.example.projectfitness

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.example.projectfitness.data.local.db.DbProvider
import com.example.projectfitness.data.local.repository.WorkoutRepository
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import ui.mainpages.navigation.Navigation

class MainActivity : ComponentActivity() {

    private val db by lazy { DbProvider.get(applicationContext) }
    private val workoutRepository by lazy {
        WorkoutRepository(
            db.workoutDao(),
            db.exerciseCatalogDao()
        )
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        setContent {
            SetSystemBarsColor()
            Main(workoutRepository)
        }
    }
}

@Composable
fun SetSystemBarsColor() {
    val systemUiController = rememberSystemUiController()

    SideEffect {
        // Status Bar (üst bar)
        systemUiController.setStatusBarColor(
            color = Color(0xFF121417), // Uygulamanızın arka plan rengi
            darkIcons = false // Açık renkli iconlar için false, koyu için true
        )

        // Navigation Bar (alt bar)
        systemUiController.setNavigationBarColor(
            color = Color(0xFF121417), // Uygulamanızın arka plan rengi
            darkIcons = false
        )
    }
}

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun Main(workoutRepository: WorkoutRepository) {
    Navigation(workoutRepository)
}