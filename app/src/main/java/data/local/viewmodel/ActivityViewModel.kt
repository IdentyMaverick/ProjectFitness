package com.example.projectfitness.data.local.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectfitness.data.local.entity.WorkoutEntity
import com.example.projectfitness.data.local.repository.WorkoutRepository
import kotlinx.coroutines.launch

class ActivityViewModel(
    private val repo: WorkoutRepository,
    userId: String
): ViewModel() {
    val workoutsFlow = repo.observeWorkouts()
    val myWorkoutsFlow = repo.observeMyWorkouts(userId)

    fun refreshWorkouts(uuid: String) {
        viewModelScope.launch {
            repo.syncMyWorkouts(uuid)
        }
    }

    fun deleteWorkouts(workoutId: String) {
        viewModelScope.launch {
            try {
                repo.deleteWorkoutFirebase(workoutId)
                repo.deleteWorkout(workoutId)
            } catch (e: Exception) {
                Log.e("DeleteError", "Silinemedi: ${e.message}")
            }

        }
    }

}