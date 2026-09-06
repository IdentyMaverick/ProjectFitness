package viewmodel

import android.content.Context
import android.util.Log
import androidx.annotation.Keep
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.grozzbear.R
import com.grozzbear.projectfitness.data.local.repository.WorkoutRepository
import data.local.entity.WorkoutHistoryEntity
import data.local.entity.WorkoutHistoryFull
import data.remote.AuthRepository
import data.remote.FirestorePaths
import data.remote.UserProfile
import data.remote.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(
    private var authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val repo: WorkoutRepository
) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    private val _loginUiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    private val _resetUiState = MutableStateFlow<ResetUiState>(ResetUiState.Idle)
    val registerState: StateFlow<RegisterUiState> = _registerState
    val loginState: StateFlow<LoginUiState> = _loginUiState
    val resetUiState: StateFlow<ResetUiState> = _resetUiState
    val allHistoricalWorkouts = repo.sessions.observeHistoricalWorkouts()
    val _totalWorkoutNumber = MutableStateFlow<Long>(0L)
    val totalWorkoutNumber: StateFlow<Long> = _totalWorkoutNumber
    val _totalLiftedWeight = MutableStateFlow<Float>(0F)
    val totalLiftedWeight: StateFlow<Float> = _totalLiftedWeight
    val _totalSpentTime = MutableStateFlow(0L)
    val totalSpentTime: StateFlow<Long> = _totalSpentTime
    val _target = MutableStateFlow<UserStats>(UserStats(0, 0f, 0L))
    val target: StateFlow<UserStats> = _target

    @Keep
    data class UserStats(
        val count: Int,
        val weight: Float,
        val time: Long
    )

    fun register(fullName: String, nickname: String, email: String, password: String) {
        viewModelScope.launch {
            _registerState.value = RegisterUiState.Loading
            try {
                val uid = authRepository.register(email, password)
                val profile = UserProfile(
                    first = fullName,
                    nickname = nickname,
                    email = email,
                    userPhotoUri = ""
                )
                try {
                    userRepository.createUserProfile(uid, profile)
                } catch (profileError: Exception) {
                    try {
                        authRepository.deleteCurrentUser()
                    } catch (deleteError: Exception) {
                        Log.e(
                            "Auth",
                            "Profile write failed and auth rollback also failed",
                            deleteError
                        )
                    }
                    throw profileError
                }
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
                ensureUserProfile(uid, displayName = null, email = email)
                userRepository.setUserOnline(uid, true)
                _loginUiState.value = LoginUiState.Success
                saveUserFcmToken(uid)
            } catch (e: Exception) {
                authRepository.logout()
                _loginUiState.value = LoginUiState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun resetLoginState() {
        _loginUiState.value = LoginUiState.Idle
    }

    fun loginWithGoogle(
        idToken: String,
        displayName: String?,
        email: String?,
        photoUrl: String?
    ) {
        viewModelScope.launch {
            _loginUiState.value = LoginUiState.Loading
            try {
                val uid = authRepository.loginWithGoogle(idToken)
                ensureUserProfile(uid, displayName, email, photoUrl)
                userRepository.setUserOnline(uid, true)
                _loginUiState.value = LoginUiState.Success
                saveUserFcmToken(uid)
            } catch (e: Exception) {
                authRepository.logout()
                _loginUiState.value = LoginUiState.Error(e.message ?: "Google sign-in failed")
            }
        }
    }

    fun onGoogleSignInFailed(message: String) {
        _loginUiState.value = LoginUiState.Error(message)
    }

    private suspend fun ensureUserProfile(
        uid: String,
        displayName: String?,
        email: String?,
        photoUrl: String? = null
    ) {
        if (userRepository.getUserProfile(uid) != null) return
        userRepository.createUserProfile(
            uid,
            UserProfile(
                first = displayName?.takeIf { it.isNotBlank() } ?: "Sporcu",
                nickname = googleNickname(displayName, email),
                email = email.orEmpty(),
                userPhotoUri = photoUrl.orEmpty()
            )
        )
    }

    private fun googleNickname(displayName: String?, email: String?): String {
        val fromName = displayName?.replace("\\s+".toRegex(), "")?.take(20)
        if (!fromName.isNullOrBlank()) return fromName
        val fromEmail = email?.substringBefore("@")?.take(20)
        if (!fromEmail.isNullOrBlank()) return fromEmail
        return "user${System.currentTimeMillis() % 100_000}"
    }

    fun getGoogleSignInClient(context: Context): com.google.android.gms.auth.api.signin.GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    fun logout() {
        viewModelScope.launch {
            val uid = authRepository.currentUid
            if (uid != null) {
                try {
                    userRepository.setUserOnline(uid, false)
                } catch (e: Exception) {
                    Log.w("Auth", "Failed to set offline status", e)
                }
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
                    _resetUiState.value =
                        ResetUiState.Error(e.message ?: "Reset mail cannot be sent")
                }
            }
        }
    }

    fun resetState() {
        _resetUiState.value = ResetUiState.Idle
    }

    fun saveUserFcmToken(userId: String) {
        if (userId.isBlank()) return
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            val db = FirebaseFirestore.getInstance()
            db.collection(FirestorePaths.USERS).document(userId).update("fcmToken", token)
        }
    }

    fun getTotalWorkoutNumber(userId: String) {
        if (userId.isBlank()) return
        viewModelScope.launch {
            try {
                _totalWorkoutNumber.value = repo.sessions.getUserTotalWorkoutNumber(userId)
            } catch (e: Exception) {
                Log.d("TAG", "getTotalWorkoutNumber: ${e.message}")
            }
        }
    }

    fun syncWorkoutsFromFirebase(userId: String) {
        if (userId.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val db = FirebaseFirestore.getInstance()
                val result = db.collection(FirestorePaths.USERS).document(userId)
                    .collection(FirestorePaths.HISTORY).get().await()

                for (document in result) {
                    val workout = document.toObject(WorkoutHistoryEntity::class.java) ?: continue

                    val existingRoomWorkout = repo.sessions.checkWorkoutExists(workout.sessionId)

                    if (existingRoomWorkout == null) {
                        val fullData = repo.sessions.fetchOtherUserWorkoutDetails(userId, document.id)

                        fullData?.let {
                            repo.sessions.insertFullHistory(it)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("SyncError", "Hata: ${e.message}")
            }
        }
    }

    fun getTotalLiftedWeight(userId: String) {
        if (userId.isBlank()) return
        viewModelScope.launch {
            try {
                _totalLiftedWeight.value = repo.sessions.getTotalLiftedWeight(userId).toFloat()
            } catch (e: Exception) {
                Log.d("TAG", "getTotalWorkoutNumber: ${e.message}")
            }
        }
    }

    fun getTotalSpentTime(userId: String) {
        if (userId.isBlank()) return
        viewModelScope.launch {
            try {
                _totalSpentTime.value = repo.sessions.getTotalSpentTime(userId)
            } catch (e: Exception) {
                Log.d("TAG", "getTotalWorkoutNumber: ${e.message}")
            }
        }
    }

    fun calculateConsistency(workout: List<WorkoutHistoryFull>): Int {
        if (workout.isEmpty()) return 0

        val currentTime = System.currentTimeMillis()
        val thirtyDaysAgo = currentTime - (30L * 24 * 60 * 60 * 1000)

        val uniqueDaysActive = workout
            .filter { it.workoutHistory.dateTimestamp > thirtyDaysAgo }
            .map {
                val sdf = java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault())
                sdf.format(java.util.Date(it.workoutHistory.dateTimestamp))
            }
            .distinct()
            .size

        val targetDays = 12.0
        val score = (uniqueDaysActive / targetDays) * 100

        return score.toInt().coerceIn(0, 100)
    }

    fun loadOtherUserStats(targetUserUid: String) {
        if (targetUserUid.isBlank()) return
        val db = FirebaseFirestore.getInstance()
        viewModelScope.launch {
            try {
                var count = 0
                var weight = 0f
                var time = 0L

                // 1. Antrenmanları çek
                val userDoc = db.collection(FirestorePaths.USERS)
                    .document(targetUserUid)
                    .collection(FirestorePaths.HISTORY)
                    .get()
                    .await()

                for (doc in userDoc) {
                    count++
                    time += doc.getLong("totalDuration") ?: 0L

                    val exerciseDoc = doc.reference.collection(FirestorePaths.EXERCISES).get().await()
                    for (exercisedoc in exerciseDoc) {
                        val setDoc =
                            exercisedoc.reference.collection(FirestorePaths.SETS).get().await()
                        for (setdoc in setDoc) {
                            // Güvenli okuma: !! yerine 0.0 kullan
                            weight += setdoc.getDouble("weight")?.toFloat() ?: 0f
                        }
                    }
                }
                Log.e("FirestoreError", count.toString())
                _target.value = UserStats(count, weight, (time / 60))
            } catch (e: Exception) {
                Log.e("FirestoreError", e.message.toString())
            }
        }
    }
}
