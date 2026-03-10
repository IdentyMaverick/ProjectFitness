package data.local.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.grozzbear.projectfitness.data.local.repository.WorkoutRepository
import data.remote.UserRepository
import kotlinx.coroutines.launch
import viewmodel.ProfileViewModel

class PersonalInformationsScreenViewModel(
    private val repo: WorkoutRepository,
    private val profileViewModel: ProfileViewModel,
    private val userRepo: UserRepository
) : ViewModel() {
    val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
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
        viewModelScope.launch {
            userRepo.updateUserInformation(
                currentUserUid!!,
                first,
                gender,
                birthDate,
                height,
                weight
            )
        }
    }
}