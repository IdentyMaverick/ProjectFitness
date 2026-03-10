package viewmodel

import androidx.annotation.Keep

interface ResetUiState {
    data object Idle : ResetUiState
    data object Success : ResetUiState
    data object Loading : ResetUiState

    @Keep
    data class Error(val message: String) : ResetUiState
}