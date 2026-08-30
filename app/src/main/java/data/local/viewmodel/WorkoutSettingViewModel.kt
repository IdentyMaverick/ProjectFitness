package com.grozzbear.projectfitness.data.local.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grozzbear.projectfitness.data.local.entity.SetEntity
import com.grozzbear.projectfitness.data.local.repository.WorkoutRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class WorkoutSettingViewModel(
    private val repo: WorkoutRepository,
    private val workoutId: String
) : ViewModel() {

    val workoutFlow = repo.observeWorkoutFull(workoutId)
        .map { full ->
            full.copy(
                exercises = full.exercises.sortedBy { it.exercise.orderIndex }
            )
        }

    val catalogExercises = repo.getAllCatalogExercises()

    fun addExercisesFromCatalog(catalogIds: Set<String>) {
        if (catalogIds.isEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            val full = runCatching { workoutFlow.first() }.getOrNull() ?: return@launch
            val existingCatalogIds = full.exercises
                .mapNotNull { it.exercise.catalogExerciseId }
                .toSet()
            val newCatalogIds = catalogIds.filter { it !in existingCatalogIds }
            if (newCatalogIds.isEmpty()) return@launch
            repo.addExercisesFromCatalog(workoutId, newCatalogIds)
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

    fun moveExercise(fromIndex: Int, toIndex: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val full = runCatching { workoutFlow.first() }.getOrNull() ?: return@launch

            val list = full.exercises.toMutableList()
            if (fromIndex !in list.indices || toIndex !in list.indices) return@launch
            if (fromIndex == toIndex) return@launch

            list.add(toIndex, list.removeAt(fromIndex))
            Log.d("Log List", list.toList().toString())
            list.forEachIndexed { index, item ->
                if (item.exercise.orderIndex != index) {
                    repo.updateExerciseOrder(
                        exerciseId = item.exercise.exerciseId,
                        workoutId = workoutId,
                        orderIndex = index
                    )
                }
            }
        }
    }

    fun removeExercise(exerciseId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteSelectedExercise(exerciseId, workoutId)
        }
    }
}
