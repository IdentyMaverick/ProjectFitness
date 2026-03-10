package viewmodel

import androidx.annotation.Keep
import data.remote.UserProfile

interface ProfileUiState {
    data object Idle : ProfileUiState
    data object Loading : ProfileUiState

    @Keep
    data class Ready(val profile: UserProfile) : ProfileUiState

    @Keep
    data class Error(val message: String) : ProfileUiState
}