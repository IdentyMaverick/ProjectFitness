package com.grozzbear.projectfitness.data.local.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.grozzbear.projectfitness.data.local.repository.WorkoutRepository
import data.remote.UserRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import viewmodel.AuthViewModel

class HomesViewModel(
    private val repo: WorkoutRepository,
    private val userRepository: UserRepository,
    authViewModel: AuthViewModel
) : ViewModel() {
    val workoutsFlow = repo.observeWorkouts()
    val currentUserUid = Firebase.auth.currentUser?.uid
    private val _userName = kotlinx.coroutines.flow.MutableStateFlow("Yükleniyor...")
    var userName: StateFlow<String> = _userName
    private val _nickname = kotlinx.coroutines.flow.MutableStateFlow("Yükleniyor...")
    var nickname: StateFlow<String> = _nickname


    init {
        viewModelScope.launch {
            repo.seedDefaultsIfEmpty()
            repo.syncCatalog()
            getUserName(currentUserUid.toString())
            authViewModel.saveUserFcmToken(currentUserUid.toString())
        }
    }

    suspend fun refreshExercises() {
        viewModelScope.launch {
            repo.syncExercisesFromFirestore()
        }
    }

    fun getUserName(currentUid: String) {
        viewModelScope.launch {
            if (currentUid != null) {
                val profile = userRepository.getUserProfile(currentUid)
                _userName.value = profile?.first ?: "Sporcu"
                _nickname.value = profile?.nickname ?: "Sporcu"
            }
        }
    }
}
