package viewmodel

interface ResetUiState {
    data object Idle : ResetUiState
    data object Success : ResetUiState
    data object Loading : ResetUiState
    data class Error(val message: String) : ResetUiState
}