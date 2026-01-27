package com.example.projectfitness.data.local.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectfitness.data.local.repository.WorkoutRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ActivityWorkoutOverviewViewModel(
    private val repo: WorkoutRepository,
    private val workoutId: String
): ViewModel() {
    val workoutFlow = repo.observeWorkoutFull(workoutId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun deleteSelectedExercise(exerciseId: String) {
        viewModelScope.launch {
            repo.deleteSelectedExerciseFirebase(workoutId, exerciseId)
            repo.deleteSelectedExercise(exerciseId, workoutId)
        }
    }
}