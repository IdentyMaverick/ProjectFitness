package data.local.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grozzbear.projectfitness.data.local.repository.WorkoutRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class OldWorkoutDetailsViewModel(
    private val repo: WorkoutRepository
) : ViewModel() {
    val _sessionId = MutableStateFlow<String>("")
    val _targetUserId = MutableStateFlow<String?>(null)
    val _flag = MutableStateFlow<Boolean>(false)


    @OptIn(ExperimentalCoroutinesApi::class)
    val workoutDetails = _sessionId.flatMapLatest { id ->
        if (id.isEmpty()) {
            emptyFlow()
        } else {
            val targetUid = _targetUserId.value
            if (targetUid != null) {
                flow {
                    val remoteData = repo.fetchOtherUserWorkoutDetails(targetUid, id)
                    if (remoteData != null) {
                        emit(remoteData)
                    }

                }
            } else {
                repo.observeWorkoutHistoryFull(id)
            }
        }
    }

    fun clearTargetUser() {
        _targetUserId.value = null
    }

    fun deleteHistorcialWorkoutById() {
        viewModelScope.launch {
            repo.deleteHistoricalWorkoutById(_sessionId.value)
        }
    }
}