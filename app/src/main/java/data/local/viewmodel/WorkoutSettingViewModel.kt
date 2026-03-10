package com.grozzbear.projectfitness.data.local.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grozzbear.projectfitness.data.local.entity.SetEntity

import com.grozzbear.projectfitness.data.local.repository.WorkoutRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class WorkoutSettingViewModel(

    private val repo: WorkoutRepository,

    workoutId: String

) : ViewModel() {
    val workoutFlow = repo.observeWorkoutFull(workoutId)

    fun deleteSelectedExercise(exerciseId: String, workutId: String) {
        viewModelScope.launch {
            repo.deleteSelectedExercise(exerciseId, workutId)
        }
    }

    fun addSet(setId: String, exerciseId: String) {
        viewModelScope.launch {
            repo.addSet(setId, exerciseId, 0, 0f)
        }
    }

    fun updateSet(setId: String, exerciseOwnerId: String, newReps: Int, newWeight: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateSet(setId, exerciseOwnerId, newReps, newWeight.toFloat())
        }
    }

    fun deleteSet(set: SetEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteSet(set)
        }
    }
}