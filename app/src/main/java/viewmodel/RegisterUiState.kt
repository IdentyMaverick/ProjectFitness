package viewmodel

import androidx.annotation.Keep

sealed interface RegisterUiState {
    data object Idle : RegisterUiState
    data object Loading : RegisterUiState
    data object Success : RegisterUiState

    @Keep
    data class Error(val message: String) : RegisterUiState
}