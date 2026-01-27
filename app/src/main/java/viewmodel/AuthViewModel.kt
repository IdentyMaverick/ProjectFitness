package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.remote.AuthRepository
import data.remote.UserProfile
import data.remote.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private var authRepository: AuthRepository, private val userRepository: UserRepository) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    private val _loginUiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    private val _resetUiState = MutableStateFlow<ResetUiState>(ResetUiState.Idle)
    val registerState: StateFlow<RegisterUiState> = _registerState
    val loginState: StateFlow<LoginUiState> = _loginUiState
    val resetUiState: StateFlow<ResetUiState> = _resetUiState

    fun register(fullName: String, nickname: String, email: String, password: String) {
        viewModelScope.launch {
            _registerState.value = RegisterUiState.Loading
            try {
                // 1) User oluştur
                val uid = authRepository.register(email, password)

                // 2) Profil yaz
                val profile = UserProfile(
                    first = fullName,
                    nickname = nickname,
                    email = email,
                    userPhotoUri = "gs://projectfitness-ddfeb.appspot.com/profile_photos/$uid/profile.jpg"
                )
                userRepository.createUserProfile(uid, profile)

                _registerState.value = RegisterUiState.Success
            } catch (e: Exception) {
                _registerState.value = RegisterUiState.Error(e.message ?: "Register failed")
            }
        }
    }

    fun resetRegisterState() {
        _registerState.value = RegisterUiState.Idle
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginUiState.value = LoginUiState.Loading
            try {
                val uid = authRepository.login(email, password)
                var profile = userRepository.getUserProfile(uid)
                userRepository.setUserOnline(uid, true)

                _loginUiState.value = LoginUiState.Success
            } catch (e: Exception) {
                _loginUiState.value = LoginUiState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun resetLoginState() {
        _loginUiState.value = LoginUiState.Idle
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.currentUser?.uid.let { uid ->
                userRepository.setUserOnline(uid.toString(), false)
            }
            authRepository.logout()
        }
    }

    fun reset(email: String) {
        viewModelScope.launch {
            val trimmed = email.trim()
            if (trimmed.isEmpty()) {
                _resetUiState.value = ResetUiState.Error("E-mail space cannot be empty")
                return@launch
            }

            viewModelScope.launch {
                _resetUiState.value = ResetUiState.Loading
                try {
                    authRepository.sendPasswordReset(trimmed)
                    _resetUiState.value = ResetUiState.Success
                } catch (e: Exception) {
                    _resetUiState.value = ResetUiState.Error(e.message ?: "Reset mail cannot be sent")
                }
            }
        }
    }
    fun resetState() {_resetUiState.value = ResetUiState.Idle
    }
}