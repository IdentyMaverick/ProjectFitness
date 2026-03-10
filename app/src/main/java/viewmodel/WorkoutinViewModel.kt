package viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.remote.WorkoutinRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WorkoutinViewModel(
    private val workoutinRepository: WorkoutinRepository
) : ViewModel() {
    private val _workoutUiState = MutableStateFlow<WorkoutUiState>(WorkoutUiState.Idle)
    val workoutUiState: StateFlow<WorkoutUiState> = _workoutUiState

    fun loadExercises(limit: Long = 100) {
        viewModelScope.launch {
            _workoutUiState.value = WorkoutUiState.Loading
            try {
                val list = workoutinRepository.getExercises(limit)
                _workoutUiState.value = WorkoutUiState.Success(list)
            } catch (e: Exception) {
                _workoutUiState.value =
                    WorkoutUiState.Error(e.message ?: "Failed to load exercises")
            }
        }
    }

    fun loadSpecificExercises(bodyPart: Set<String>, limit: Long = 200) {
        viewModelScope.launch {
            _workoutUiState.value = WorkoutUiState.Loading
            try {
                val list = workoutinRepository.getExercisesByBodyPart(bodyPart.toList(), limit)
                Log.d("selected", list.toString())
                _workoutUiState.value = WorkoutUiState.Success(list)
            } catch (e: Exception) {
                _workoutUiState.value =
                    WorkoutUiState.Error(e.message ?: "Failed to load specific exercises")
            }
        }
    }
}