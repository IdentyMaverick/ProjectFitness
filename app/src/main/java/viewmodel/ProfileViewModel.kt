package viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.remote.StorageRepository
import data.remote.UserProfile
import data.remote.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val storageRepository: StorageRepository
) : ViewModel() {
    private val _profileState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val profileState: StateFlow<ProfileUiState> = _profileState

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
}