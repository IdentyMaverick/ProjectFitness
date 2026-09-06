package com.grozzbear.projectfitness.data.local.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grozzbear.projectfitness.data.local.entity.SetEntity
import com.grozzbear.projectfitness.data.local.repository.WorkoutRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class WorkoutSettingViewModel(
    private val repo: WorkoutRepository,
    private val workoutId: String
) : ViewModel() {

    val workoutFlow = repo.templates.observeWorkoutFull(workoutId)

    val catalogExercises = repo.catalog.observeAllActive()

    fun addExercisesFromCatalog(catalogIds: Set<String>) {
        if (catalogIds.isEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            val full = runCatching { workoutFlow.first() }.getOrNull() ?: return@launch
            val existingCatalogIds = full.exercises
                .mapNotNull { it.exercise.catalogExerciseId }
                .toSet()
            val newCatalogIds = catalogIds.filter { it !in existingCatalogIds }
            if (newCatalogIds.isEmpty()) return@launch
            repo.templates.addExercisesFromCatalog(workoutId, newCatalogIds)
        }
    }

    fun addSet(setId: String, exerciseId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val lastIndex = findLastMember(exerciseId)
            repo.templates.addSet(setId, exerciseId, 0, 0f, workoutId = workoutId, setIndex = lastIndex)
        }
    }

    fun updateSet(setId: String, exerciseOwnerId: String, newReps: Int, newWeight: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.templates.updateSet(setId, exerciseOwnerId, newReps, newWeight.toFloat(), workoutId)
        }
    }

    fun deleteSet(set: SetEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.templates.deleteSet(set, workoutId)
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
                    repo.templates.updateExerciseOrder(
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
            repo.templates.deleteSelectedExercise(exerciseId, workoutId)
        }
    }

    private suspend fun findLastMember(exerciseId: String): Int {
        return (repo.templates.getMaxOfExerciseSet(exerciseId) ?: -1) + 1
    }
}
