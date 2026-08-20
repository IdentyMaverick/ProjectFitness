package com.grozzbear.projectfitness.viewmodel

import SocialViewModel
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
import data.local.viewmodel.ActivityInsideViewModel
import data.local.viewmodel.CreateWorkoutViewModel
import data.local.viewmodel.FaqcontactfeedbackScreenViewModel
import data.local.viewmodel.LeaderboardViewModel
import data.local.viewmodel.OldWorkoutDetailsViewModel
import data.local.viewmodel.PersonalInformationsScreenViewModel
import data.local.viewmodel.WorkoutCompleteAnalysisScreenViewModel
import data.local.viewmodel.WorkoutCompleteScreenViewModel
import data.remote.AuthRepository
import data.remote.FirestoreRepository
import data.remote.StorageRepository
import data.remote.UserRepository
import data.remote.WorkoutinRepository
import viewmodel.AuthViewModel
import viewmodel.ProfileViewModel
import viewmodel.WorkoutinViewModel

class WorkoutViewModelFactory(
    private val repository: WorkoutRepository,
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val profileViewModel: ProfileViewModel? = null,
    private val authViewModel: AuthViewModel? = null,
    private val firestoreRepository: FirestoreRepository = FirestoreRepository(),
    private val storageRepository: StorageRepository = StorageRepository(),
    private val workoutinRepository: WorkoutinRepository = WorkoutinRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModelProvider.Factory {

    @RequiresApi(Build.VERSION_CODES.O)
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) ->
                AuthViewModel(authRepository, userRepository, repository) as T

            modelClass.isAssignableFrom(ProfileViewModel::class.java) ->
                ProfileViewModel(userRepository, repository, storageRepository) as T

            modelClass.isAssignableFrom(SocialViewModel::class.java) ->
                SocialViewModel(firestoreRepository) as T

            modelClass.isAssignableFrom(WorkoutinViewModel::class.java) ->
                WorkoutinViewModel(workoutinRepository) as T

            modelClass.isAssignableFrom(ActivityInsideViewModel::class.java) ->
                ActivityInsideViewModel(repository) as T

            modelClass.isAssignableFrom(WorkoutViewModel::class.java) ->
                WorkoutViewModel(repository) as T

            modelClass.isAssignableFrom(HomesViewModel::class.java) ->
                HomesViewModel(
                    repository,
                    userRepository,
                    requireNotNull(authViewModel) { "AuthViewModel required for HomesViewModel" }
                ) as T

            modelClass.isAssignableFrom(ActivityViewModel::class.java) ->
                ActivityViewModel(repository, auth) as T

            modelClass.isAssignableFrom(CreateWorkoutViewModel::class.java) ->
                CreateWorkoutViewModel(repository) as T

            modelClass.isAssignableFrom(WorkoutCompleteScreenViewModel::class.java) ->
                WorkoutCompleteScreenViewModel(repository, userRepository) as T

            modelClass.isAssignableFrom(WorkoutCompleteAnalysisScreenViewModel::class.java) ->
                WorkoutCompleteAnalysisScreenViewModel(repository, userRepository) as T

            modelClass.isAssignableFrom(LeaderboardViewModel::class.java) ->
                LeaderboardViewModel(
                    repository,
                    requireNotNull(profileViewModel) { "ProfileViewModel required for LeaderboardViewModel" }
                ) as T

            modelClass.isAssignableFrom(PersonalInformationsScreenViewModel::class.java) ->
                PersonalInformationsScreenViewModel(
                    repository,
                    requireNotNull(profileViewModel) { "ProfileViewModel required for PersonalInformationsScreenViewModel" },
                    userRepository
                ) as T

            modelClass.isAssignableFrom(FaqcontactfeedbackScreenViewModel::class.java) ->
                FaqcontactfeedbackScreenViewModel(userRepository) as T

            modelClass.isAssignableFrom(OldWorkoutDetailsViewModel::class.java) ->
                OldWorkoutDetailsViewModel(repository) as T

            modelClass.isAssignableFrom(WorkoutSettingViewModel::class.java) ->
                WorkoutSettingViewModel(repository, auth.uid.orEmpty()) as T

            else -> error("Unknown ViewModel: ${modelClass.name}")
        }
    }
}
