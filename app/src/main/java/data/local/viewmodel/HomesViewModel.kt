package com.example.projectfitness.data.local.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectfitness.data.local.repository.WorkoutRepository
import kotlinx.coroutines.launch

class HomesViewModel(
    private val repo: WorkoutRepository
) : ViewModel() {
    val workoutsFlow = repo.observeWorkouts()

    init {
        viewModelScope.launch {
            repo.seedDefaultsIfEmpty()
            repo.syncCatalog()
        }
    }
}
