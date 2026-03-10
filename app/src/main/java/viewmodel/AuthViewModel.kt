package viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.grozzbear.projectfitness.data.local.repository.WorkoutRepository
import data.local.entity.WorkoutHistoryEntity
import data.local.entity.WorkoutHistoryFull
import data.remote.AuthRepository
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
    val allHistoricalWorkouts = repo.observeHistoricalWorkouts()
    val _totalWorkoutNumber = MutableStateFlow<Long>(0L)
    val totalWorkoutNumber: StateFlow<Long> = _totalWorkoutNumber
    val _totalLiftedWeight = MutableStateFlow<Float>(0F)
    val totalLiftedWeight: StateFlow<Float> = _totalLiftedWeight
    val _totalSpentTime = MutableStateFlow(0L)
    val totalSpentTime: StateFlow<Long> = _totalSpentTime
    val _target = MutableStateFlow<UserStats>(UserStats(0, 0f, 0L))
    val target: StateFlow<UserStats> = _target

    data class UserStats(
        val count: Int,
        val weight: Float,
        val time: Long
    )

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
                    userPhotoUri = ""
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
                saveUserFcmToken(uid)
            } catch (e: Exception) {
                _loginUiState.value = LoginUiState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun resetLoginState() {
        _loginUiState.value = LoginUiState.Idle
    }

    fun getGoogleSignInClient(context: Context): com.google.android.gms.auth.api.signin.GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("975521849945-lfab2m5gilchjcsd4iq7nrlnrj4oanls.apps.googleusercontent.com")
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
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
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            val db = FirebaseFirestore.getInstance()
            db.collection("googlecloudusers").document(userId).update("fcmToken", token)
        }
    }

    fun getTotalWorkoutNumber(userId: String) {
        viewModelScope.launch {
            try {
                _totalWorkoutNumber.value = repo.getUserTotalWorkoutNumber(userId)
            } catch (e: Exception) {
                Log.d("TAG", "getTotalWorkoutNumber: ${e.message}")
            }
        }
    }

    fun syncWorkoutsFromFirebase(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val db = FirebaseFirestore.getInstance()
                val result = db.collection("googlecloudusers").document(userId)
                    .collection("googlecloudworkouts").get().await()

                for (document in result) {
                    val workout = document.toObject(WorkoutHistoryEntity::class.java) ?: continue

                    val existingRoomWorkout = repo.checkWorkoutExists(workout.sessionId)

                    if (existingRoomWorkout == null) {
                        val fullData = repo.fetchOtherUserWorkoutDetails(userId, document.id)

                        fullData?.let {
                            repo.insertFullHistory(it)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("SyncError", "Hata: ${e.message}")
            }
        }
    }

    fun getTotalLiftedWeight(userId: String) {
        viewModelScope.launch {
            try {
                _totalLiftedWeight.value = repo.getTotalLiftedWeight(userId).toFloat()
            } catch (e: Exception) {
                Log.d("TAG", "getTotalWorkoutNumber: ${e.message}")
            }
        }
    }

    fun getTotalSpentTime(userId: String) {
        viewModelScope.launch {
            try {
                _totalSpentTime.value = repo.getTotalSpentTime(userId)
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
        val db = FirebaseFirestore.getInstance()
        viewModelScope.launch {
            try {
                var count = 0
                var weight = 0f
                var time = 0L

                // 1. Antrenmanları çek
                val userDoc = db.collection("googlecloudusers")
                    .document(targetUserUid)
                    .collection("googlecloudworkouts")
                    .get()
                    .await()

                for (doc in userDoc) {
                    count++
                    // Güvenli okuma: !! yerine ?: 0L kullan
                    time += doc.getLong("totalDuration") ?: 0L

                    // 2. Egzersizleri çek
                    val exerciseDoc = doc.reference.collection("googlecloudexercises").get().await()
                    for (exercisedoc in exerciseDoc) {

                        // 3. Setleri çek
                        val setDoc =
                            exercisedoc.reference.collection("googlecloudsets").get().await()
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