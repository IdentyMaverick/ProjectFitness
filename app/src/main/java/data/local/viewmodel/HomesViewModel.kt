package com.grozzbear.projectfitness.data.local.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.grozzbear.projectfitness.data.local.repository.WorkoutRepository
import data.remote.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import viewmodel.AuthViewModel

class HomesViewModel(
    private val repo: WorkoutRepository,
    private val userRepository: UserRepository,
    authViewModel: AuthViewModel,
) : ViewModel() {
    val workoutsFlow = repo.templates.observeWorkouts()
    val currentUserUid: String?
        get() =
            Firebase.auth.currentUser
                ?.uid
                ?.takeIf { it.isNotBlank() }
    private val _userName = kotlinx.coroutines.flow.MutableStateFlow("Yükleniyor...")
    var userName: StateFlow<String> = _userName
    private val _nickname = kotlinx.coroutines.flow.MutableStateFlow("Yükleniyor...")
    var nickname: StateFlow<String> = _nickname

    init {
        viewModelScope.launch {
            repo.templates.seedDefaultsIfEmpty()
            repo.catalog.syncCatalog()
            val uid = currentUserUid ?: return@launch
            getUserName(uid)
            authViewModel.saveUserFcmToken(uid)
        }
    }

    fun refreshExercises() {
        viewModelScope.launch {
            repo.catalog.syncExerciseImagesFromCatalog()
        }
    }

    fun getUserName(currentUid: String) {
        if (currentUid.isBlank()) return
        viewModelScope.launch {
            val profile = userRepository.getUserProfile(currentUid)
            _userName.value = profile?.first ?: "Sporcu"
            _nickname.value = profile?.nickname ?: "Sporcu"
        }
    }
}
