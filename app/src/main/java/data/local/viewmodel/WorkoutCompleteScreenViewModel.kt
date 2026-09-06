package data.local.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.grozzbear.projectfitness.data.local.repository.WorkoutRepository
import data.remote.UserRepository
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class WorkoutCompleteScreenViewModel(repository: WorkoutRepository, private val userRepository: UserRepository) :
    ViewModel() {
    val currentUserUid: String?
        get() =
            Firebase.auth.currentUser
                ?.uid
                ?.takeIf { it.isNotBlank() }
    private val _userName = MutableStateFlow("Yükleniyor...")
    val userName: StateFlow<String> = _userName
    private val _elapsedTime = MutableStateFlow("0 Minutes")
    val elapsedTime: StateFlow<String> = _elapsedTime
    private val _formattedDate = MutableStateFlow("")
    val formattedDate: StateFlow<String> = _formattedDate
    private val _totalSetsCompleted = MutableStateFlow(0)
    val totalSetsCompleted: StateFlow<Int> = _totalSetsCompleted
    private val _totalRepsCompleted = MutableStateFlow(0)
    val totalRepsCompleted: StateFlow<Int> = _totalRepsCompleted
    private val _prExercises = MutableStateFlow<List<String>>(emptyList())
    val prExercises: StateFlow<List<String>> = _prExercises

    init {
        getUserName()
    }

    fun setCompletedTotals(sets: Int, reps: Int) {
        _totalSetsCompleted.value = sets
        _totalRepsCompleted.value = reps
    }

    fun setPrExercises(prExercises: List<String>) {
        _prExercises.value = prExercises
    }

    fun getUserName() {
        viewModelScope.launch {
            if (currentUserUid != null) {
                val profile = userRepository.getUserProfile(currentUserUid!!)
                _userName.value = profile?.first ?: "Sporcu"
            }
        }
    }

    fun formatElapsedTime(durationSeconds: Long): String {
        val formattedTime =
            String.format(
                "%02d:%02d:%02d",
                durationSeconds / 3600,
                (durationSeconds % 3600) / 60,
                durationSeconds % 60,
            )
        return formattedTime
    }

    fun setWorkoutData(timestamp: Long, durationSeconds: Long) {
        _formattedDate.value = dateConvert(timestamp)
        _elapsedTime.value = formatElapsedTime(durationSeconds)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun dateConvert(dateTimeStamp: Long): String {
        // Eğer timestamp 0 ise boş dön veya hata yönetimini yap
        if (dateTimeStamp == 0L) return ""

        val instant = Instant.ofEpochMilli(dateTimeStamp)
        val formatter =
            DateTimeFormatter
                .ofPattern("EEEE, MMM d", Locale.ENGLISH)
                .withZone(ZoneId.systemDefault())
        return formatter.format(instant)
    }
}
