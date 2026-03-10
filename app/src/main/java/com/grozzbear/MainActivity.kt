package com.grozzbear

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.grozzbear.projectfitness.data.local.db.DbProvider
import com.grozzbear.projectfitness.data.local.repository.WorkoutRepository
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
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        setContent {
            var showSplash by remember { mutableStateOf(true) }

            if (showSplash) {
                SplashScreen(onTimeout = { showSplash = false })
            } else {
                SetSystemBarsColor()
                Main(workoutRepository)
            }
        }
    }
}

@Composable
fun SetSystemBarsColor() {
    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color(0xFF121417),
            darkIcons = false
        )

        systemUiController.setNavigationBarColor(
            color = Color(0xFF121417),
            darkIcons = false
        )
    }
}

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun Main(workoutRepository: WorkoutRepository) {
    Navigation(workoutRepository)
}