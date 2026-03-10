package viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grozzbear.projectfitness.data.local.repository.WorkoutRepository
import data.local.entity.WorkoutHistoryEntity
import data.local.entity.WorkoutHistoryFull
import data.remote.StorageRepository
import data.remote.UserProfile
import data.remote.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val repo: WorkoutRepository,
    private val storageRepository: StorageRepository
) : ViewModel() {
    private val _profileState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val profileState: StateFlow<ProfileUiState> = _profileState
    private val _workoutHistoryFull = MutableStateFlow<List<WorkoutHistoryFull>>(emptyList())
    private val userId = MutableStateFlow("")
    val workoutHistoryFull = userId.flatMapLatest { uid ->
        repo.observeWorkoutHistoryOther(uid)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    private val _userHistory = MutableStateFlow<List<WorkoutHistoryEntity>>(emptyList())
    val userHistory: StateFlow<List<WorkoutHistoryEntity>> = _userHistory

    fun setUserId(uid: String) {
        userId.value = uid
    }

    fun load(uid: String) {
        viewModelScope.launch {
            _profileState.value = ProfileUiState.Loading
            try {
                val profile = userRepository.getUserProfile(uid) ?: UserProfile()
                _profileState.value = ProfileUiState.Ready(profile)
            } catch (e: Exception) {
                _profileState.value = ProfileUiState.Error(e.message ?: "Profile laod failed")
            }
        }
    }

    fun changePhoto(uid: String, uri: Uri) {
        viewModelScope.launch {
            try {
                val url = storageRepository.uploadProfilePhoto(uid, uri) // Upload
                userRepository.updatePhotoUrl(uid, url)

                val updated = userRepository.getUserProfile(uid) ?: UserProfile()
                _profileState.value = ProfileUiState.Ready(updated)
            } catch (e: Exception) {
                _profileState.value = ProfileUiState.Error(e.message ?: "Photo upload failed")
            }
        }
    }

    fun loadUserWorkouts(targetUserId: String) {
        viewModelScope.launch {
            repo.observeUserWorkoutHistory(targetUserId).collect { history ->
                _userHistory.value = history
            }
        }
    }
}