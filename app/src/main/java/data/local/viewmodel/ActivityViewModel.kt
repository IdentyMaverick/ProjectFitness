package com.grozzbear.projectfitness.data.local.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.grozzbear.projectfitness.data.local.entity.WorkoutEntity
import com.grozzbear.projectfitness.data.local.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class ActivityViewModel(
    private val repo: WorkoutRepository,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val userId = MutableStateFlow(auth.currentUser?.uid.orEmpty())

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        userId.value = firebaseAuth.currentUser?.uid.orEmpty()
    }

    val workoutsFlow = repo.templates.observeWorkouts()
    val myWorkoutsFlow: Flow<List<WorkoutEntity>> = userId.flatMapLatest { uid ->
        if (uid.isBlank()) flowOf(emptyList())
        else repo.templates.observeMyWorkouts(uid)
    }

    init {
        auth.addAuthStateListener(authStateListener)
    }

    fun refreshWorkouts(uuid: String) {
        if (uuid.isBlank()) return
        userId.value = uuid
        viewModelScope.launch {
            repo.templates.syncMyWorkouts(uuid)
        }
    }

    fun deleteWorkouts(workoutId: String) {
        viewModelScope.launch {
            try {
                repo.templates.deleteWorkoutFirebase(workoutId)
                repo.templates.deleteWorkout(workoutId)
            } catch (e: Exception) {
                Log.e("DeleteError", "Silinemedi: ${e.message}")
            }
        }
    }

    override fun onCleared() {
        auth.removeAuthStateListener(authStateListener)
        super.onCleared()
    }
}
