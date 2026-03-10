package com.grozzbear.projectfitness.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.grozzbear.projectfitness.data.local.repository.WorkoutRepository
import com.grozzbear.projectfitness.data.local.viewmodel.ActivityViewModel
import com.grozzbear.projectfitness.data.local.viewmodel.HomesViewModel
import com.grozzbear.projectfitness.data.local.viewmodel.WorkoutSettingViewModel
import com.grozzbear.projectfitness.data.local.viewmodel.WorkoutViewModel
import data.local.viewmodel.CreateWorkoutViewModel
import data.local.viewmodel.FaqcontactfeedbackScreenViewModel
import data.local.viewmodel.LeaderboardViewModel
import data.local.viewmodel.OldWorkoutDetailsViewModel
import data.local.viewmodel.PersonalInformationsScreenViewModel
import data.local.viewmodel.WorkoutCompleteAnalysisScreenViewModel
import data.local.viewmodel.WorkoutCompleteScreenViewModel
import data.remote.UserRepository
import viewmodel.AuthViewModel
import viewmodel.ProfileViewModel

class WorkoutViewModelFactory(
    private val repository: WorkoutRepository,
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val profileViewModel: ProfileViewModel,
    private val authViewModel: AuthViewModel
) : ViewModelProvider.Factory {
    val currentUserId = auth.uid ?: ""

    @RequiresApi(Build.VERSION_CODES.O)
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(WorkoutViewModel::class.java) ->
                WorkoutViewModel(repository) as T

            modelClass.isAssignableFrom(HomesViewModel::class.java) ->
                HomesViewModel(repository, userRepository, authViewModel) as T

            modelClass.isAssignableFrom(ActivityViewModel::class.java) ->
                ActivityViewModel(repository, currentUserId) as T

            modelClass.isAssignableFrom(CreateWorkoutViewModel::class.java) ->
                CreateWorkoutViewModel(repository) as T

            modelClass.isAssignableFrom(WorkoutCompleteScreenViewModel::class.java) ->
                WorkoutCompleteScreenViewModel(repository, userRepository) as T

            modelClass.isAssignableFrom(WorkoutCompleteAnalysisScreenViewModel::class.java) ->
                WorkoutCompleteAnalysisScreenViewModel(repository, userRepository) as T

            modelClass.isAssignableFrom(LeaderboardViewModel::class.java) ->
                LeaderboardViewModel(repository, profileViewModel) as T

            modelClass.isAssignableFrom(PersonalInformationsScreenViewModel::class.java) ->
                PersonalInformationsScreenViewModel(
                    repository,
                    profileViewModel,
                    userRepository
                ) as T

            modelClass.isAssignableFrom(FaqcontactfeedbackScreenViewModel::class.java) ->
                FaqcontactfeedbackScreenViewModel(userRepository) as T

            modelClass.isAssignableFrom(OldWorkoutDetailsViewModel::class.java) ->
                OldWorkoutDetailsViewModel(repository) as T

            modelClass.isAssignableFrom(WorkoutSettingViewModel::class.java) ->
                WorkoutSettingViewModel(repository, currentUserId) as T

            else -> error("Unknown ViewModel: ${modelClass.name}")
        }
    }
}
