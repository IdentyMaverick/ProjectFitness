package com.example.projectfitness.data.local.viewmodel

import androidx.lifecycle.ViewModel
import com.example.projectfitness.data.local.repository.WorkoutRepository

class WorkoutSettingViewModel(
    repo: WorkoutRepository,
    workoutId: String
): ViewModel() {
    val workoutFlow = repo.observeWorkoutFull(workoutId)
}