package viewmodel

import androidx.annotation.Keep
import com.grozzbear.projectfitness.data.remote.Workoutin


interface WorkoutUiState {
    data object Idle : WorkoutUiState
    data object Loading : WorkoutUiState

    @Keep
    data class Success(val exercises: List<Workoutin>) : WorkoutUiState

    @Keep
    data class Error(val message: String) : WorkoutUiState
}