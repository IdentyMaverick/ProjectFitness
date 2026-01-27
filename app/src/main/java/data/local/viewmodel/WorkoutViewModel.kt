package com.example.projectfitness.data.local.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectfitness.data.local.repository.WorkoutRepository
import kotlinx.coroutines.launch

class WorkoutViewModel(
    private val repo: WorkoutRepository
) : ViewModel() {

    val workoutsFlow = repo.observeWorkouts()

    fun addWorkout(workoutId: String, name: String, workoutType: String, workoutRating: Int, ownerUid: String, syncState: Boolean) {
        viewModelScope.launch {
            repo.createWorkout(workoutId, name, workoutType, workoutRating, ownerUid, syncState)
        }
    }

    fun deleteWorkout(id: String) {
        viewModelScope.launch {
            repo.deleteWorkout(id)
        }
    }
}
