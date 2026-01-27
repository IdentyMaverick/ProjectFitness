package com.example.projectfitness.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.projectfitness.data.local.repository.WorkoutRepository
import com.example.projectfitness.data.local.viewmodel.ActivityViewModel
import com.example.projectfitness.data.local.viewmodel.HomesViewModel
import com.example.projectfitness.data.local.viewmodel.WorkoutViewModel
import com.google.firebase.auth.FirebaseAuth
import data.local.viewmodel.CreateWorkoutViewModel

class WorkoutViewModelFactory(
    private val repository: WorkoutRepository,
    private val auth: FirebaseAuth
) : ViewModelProvider.Factory {
    val currentUserId = auth.uid ?: ""
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(WorkoutViewModel::class.java) ->
                WorkoutViewModel(repository) as T

            modelClass.isAssignableFrom(HomesViewModel::class.java) ->
                HomesViewModel(repository) as T

            modelClass.isAssignableFrom(ActivityViewModel::class.java) ->
                ActivityViewModel(repository, currentUserId) as T

            modelClass.isAssignableFrom(CreateWorkoutViewModel::class.java) ->
                CreateWorkoutViewModel(repository) as T

            else -> error("Unknown ViewModel: ${modelClass.name}")
        }
    }
}
