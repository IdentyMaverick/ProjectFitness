package viewmodel

import com.grozzbear.projectfitness.data.remote.Workoutin


interface WorkoutUiState {
    data object Idle : WorkoutUiState
    data object Loading : WorkoutUiState
    data class Success(val exercises: List<Workoutin>) : WorkoutUiState
    data class Error(val message: String) : WorkoutUiState
}