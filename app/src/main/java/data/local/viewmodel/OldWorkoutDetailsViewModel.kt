package data.local.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grozzbear.projectfitness.data.local.repository.WorkoutRepository
import data.local.entity.WorkoutHistoryFull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val _isEditModeEnabled = MutableStateFlow<Boolean>(false)
    val isEditModeEnabled = _isEditModeEnabled.asStateFlow()
    private val _draft = MutableStateFlow<WorkoutHistoryFull?>(null)
    val draft = _draft.asStateFlow()

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
        exitEditMode()
    }

    fun deleteHistorcialWorkoutById() {
        viewModelScope.launch {
            repo.deleteHistoricalWorkoutById(_sessionId.value)
        }
    }

    fun enterEditMode(current: WorkoutHistoryFull) {
        if (!_flag.value) return
        _draft.value = current.copy(
            exerciseWithSets = current.exerciseWithSets.map { ex ->
                ex.copy(setLogs = ex.setLogs.map { it.copy() })
            }
        )
        _isEditModeEnabled.value = true
    }

    fun exitEditMode() {
        _draft.value = null
        _isEditModeEnabled.value = false
    }

    fun saveEdits() {
        _isEditModeEnabled.value = false
    }

    fun updateDraftSet(setId: Long, reps: Int, weight: Float) {
        val current = _draft.value ?: return
        _draft.value = current.copy(
            exerciseWithSets = current.exerciseWithSets.map { exercise ->
                exercise.copy(
                    setLogs = exercise.setLogs.map { set ->
                        if (set.setId == setId) set.copy(reps = reps, weight = weight) else set
                    }
                )
            }
        )
    }
}