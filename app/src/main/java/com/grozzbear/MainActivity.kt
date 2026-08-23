package com.grozzbear

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import com.grozzbear.projectfitness.data.local.db.DbProvider
import com.grozzbear.projectfitness.data.local.repository.WorkoutRepository
import com.grozzbear.ui.theme.ProjectFitnessTheme
import ui.mainpages.navigation.Navigation
import ui.mainpages.openscreen.SplashScreen

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
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars =
            false
        setContent {
            ProjectFitnessTheme {
                var showSplash by remember { mutableStateOf(true) }

                if (showSplash) {
                    SplashScreen(onTimeout = { showSplash = false })
                } else {
                    Main(workoutRepository)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun Main(workoutRepository: WorkoutRepository) {
    Navigation(workoutRepository)
}