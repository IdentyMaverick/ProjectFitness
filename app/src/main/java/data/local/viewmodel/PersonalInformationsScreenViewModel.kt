package data.local.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.grozzbear.projectfitness.data.local.repository.WorkoutRepository
import data.remote.UserRepository
import kotlinx.coroutines.launch
import viewmodel.ProfileViewModel

class PersonalInformationsScreenViewModel(
    repository: WorkoutRepository,
    private val profileViewModel: ProfileViewModel,
    private val userRepo: UserRepository
) : ViewModel() {
    val currentUserUid: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid?.takeIf { it.isNotBlank() }
    val profileState = profileViewModel.profileState

    fun loadUid() {
        currentUserUid?.let { uid ->
            profileViewModel.load(uid)
        }
    }

    fun updateUserInformation(
        first: String,
        gender: Boolean,
        birthDate: String,
        height: String,
        weight: String
    ) {
        val uid = currentUserUid ?: return
        viewModelScope.launch {
            userRepo.updateUserInformation(
                uid,
                first,
                gender,
                birthDate,
                height,
                weight
            )
        }
    }
}