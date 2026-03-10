package viewmodel

import androidx.annotation.Keep

interface LoginUiState {
    data object Idle : LoginUiState
    data object Loading : LoginUiState
    data object Success : LoginUiState

    @Keep
    data class Error(val message: String) : LoginUiState
}