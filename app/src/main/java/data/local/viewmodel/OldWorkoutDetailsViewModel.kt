package data.local.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grozzbear.projectfitness.data.local.entity.ExerciseCatalogEntity
import com.grozzbear.projectfitness.data.local.repository.WorkoutRepository
import data.local.entity.ExerciseLogEntity
import data.local.entity.ExerciseLogWithSets
import data.local.entity.SetLogEntity
import data.local.entity.WorkoutHistoryFull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class OldWorkoutDetailsViewModel(private val repo: WorkoutRepository) : ViewModel() {
    private val sessionId = MutableStateFlow("")
    private val targetUserId = MutableStateFlow<String?>(null)
    private val _canManage = MutableStateFlow(false)
    val canManage = _canManage.asStateFlow()
    private val _isEditModeEnabled = MutableStateFlow(false)
    val isEditModeEnabled = _isEditModeEnabled.asStateFlow()
    private val _draft = MutableStateFlow<WorkoutHistoryFull?>(null)
    val draft = _draft.asStateFlow()
    val catalogExercises = repo.catalog.observeAllActive()
    private var original: WorkoutHistoryFull? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    val workoutDetails =
        sessionId.flatMapLatest { id ->
            if (id.isEmpty()) {
                emptyFlow()
            } else {
                val targetUid = targetUserId.value
                if (targetUid != null) {
                    flow {
                        val remoteData = repo.sessions.fetchOtherUserWorkoutDetails(targetUid, id)
                        if (remoteData != null) {
                            emit(remoteData)
                        }
                    }
                } else {
                    repo.sessions.observeWorkoutHistoryFull(id)
                }
            }
        }

    fun setSessionId(id: String) {
        sessionId.value = id
    }

    fun setTargetUserId(userId: String?) {
        targetUserId.value = userId
    }

    fun setCanManage(canManage: Boolean) {
        _canManage.value = canManage
    }

    fun clearTargetUser() {
        targetUserId.value = null
        exitEditMode()
    }

    fun deleteHistoricalWorkoutById() {
        viewModelScope.launch {
            repo.sessions.deleteHistoricalWorkoutById(sessionId.value)
        }
    }

    fun enterEditMode(current: WorkoutHistoryFull) {
        if (!_canManage.value) return
        val ordered = current.sortedByExerciseOrder()
        original = ordered
        _draft.value =
            ordered.copy(
                exerciseWithSets =
                    ordered.exerciseWithSets.map { ex ->
                        ex.copy(setLogs = ex.setLogs.map { it.copy() })
                    },
            )
        _isEditModeEnabled.value = true
    }

    fun exitEditMode() {
        _draft.value = null
        original = null
        _isEditModeEnabled.value = false
    }

    fun saveEdits() {
        val draft = _draft.value ?: return
        val before = original ?: return
        viewModelScope.launch {
            val logIdRemap = mutableMapOf<Long, Long>()
            draft.exerciseWithSets.forEachIndexed { index, ex ->
                if (ex.exerciseLog.logId < 0) {
                    val newId =
                        repo.sessions.addExerciseLog(
                            sessionId = sessionId.value,
                            exerciseName = ex.exerciseLog.exerciseName,
                            bodyPart = ex.exerciseLog.bodyPart,
                            secondaryMuscles = ex.exerciseLog.secondaryMuscles,
                            setOrder = index,
                        )
                    logIdRemap[ex.exerciseLog.logId] = newId
                } else {
                    val oldIndex =
                        before.exerciseWithSets
                            .indexOfFirst { it.exerciseLog.logId == ex.exerciseLog.logId }
                    if (oldIndex != index || ex.exerciseLog.setOrder != index) {
                        repo.sessions.updateExerciseLogOrder(ex.exerciseLog.logId, index)
                    }
                }
            }

            val originalSets = before.exerciseWithSets.flatMap { it.setLogs }
            val draftSets = draft.exerciseWithSets.flatMap { it.setLogs }
            val draftIds = draftSets.map { it.setId }.toSet()

            val toDelete = originalSets.filter { it.setId !in draftIds }
            val toInsert = draftSets.filter { it.setId < 0 }
            val toUpdate =
                draftSets.filter { set ->
                    val old = originalSets.find { it.setId == set.setId }
                    old != null &&
                        (
                            old.reps != set.reps ||
                                old.weight != set.weight ||
                                old.setIndex != set.setIndex ||
                                old.clicked != set.clicked
                            )
                }

            toDelete.forEach { repo.sessions.deleteHistoricalSet(it) }
            toUpdate.forEach {
                repo.sessions.addSetLog(
                    it.logOwnerId,
                    it.setId,
                    it.reps,
                    it.weight,
                    it.setIndex,
                    it.clicked,
                )
            }
            toInsert.forEach { set ->
                val ownerId = logIdRemap[set.logOwnerId] ?: set.logOwnerId
                if (ownerId <= 0L) return@forEach
                repo.sessions.addSetLog(
                    ownerId,
                    0L,
                    set.reps,
                    set.weight,
                    set.setIndex,
                    set.clicked,
                )
            }

            if (draft.workoutHistory.totalDuration != before.workoutHistory.totalDuration) {
                repo.sessions.updateSessionDuration(
                    sessionId.value,
                    draft.workoutHistory.totalDuration,
                )
            }

            exitEditMode()
        }
    }

    fun updateDraftSet(setId: Long, reps: Int, weight: Float) {
        val current = _draft.value ?: return
        _draft.value =
            current.copy(
                exerciseWithSets =
                    current.exerciseWithSets.map { exercise ->
                        exercise.copy(
                            setLogs =
                                exercise.setLogs.map { set ->
                                    if (set.setId == setId) {
                                        set.copy(reps = reps, weight = weight, clicked = true)
                                    } else {
                                        set
                                    }
                                },
                        )
                    },
            )
    }

    fun addDraftSet(logId: Long) {
        val current = _draft.value ?: return
        val tempId =
            current.exerciseWithSets
                .flatMap { it.setLogs }
                .minOfOrNull { it.setId }
                ?.let { if (it < 0) it - 1 else -1L }
                ?: -1L
        _draft.value =
            current.copy(
                exerciseWithSets =
                    current.exerciseWithSets.map { exercise ->
                        if (exercise.exerciseLog.logId != logId) return@map exercise
                        val last = exercise.setLogs.lastOrNull()
                        exercise.copy(
                            setLogs =
                                exercise.setLogs +
                                    SetLogEntity(
                                        setId = tempId,
                                        logOwnerId = logId,
                                        reps = last?.reps ?: 0,
                                        weight = last?.weight ?: 0f,
                                        setIndex = (last?.setIndex ?: -1) + 1,
                                        clicked = true,
                                    ),
                        )
                    },
            )
    }

    fun deleteDraftSet(setId: Long) {
        val current = _draft.value ?: return
        _draft.value =
            current.copy(
                exerciseWithSets =
                    current.exerciseWithSets.map { exercise ->
                        if (exercise.setLogs.none { it.setId == setId }) {
                            exercise
                        } else {
                            val remaining =
                                exercise.setLogs
                                    .filter { it.setId != setId }
                                    .mapIndexed { i, set -> set.copy(setIndex = i) }
                            exercise.copy(setLogs = remaining)
                        }
                    },
            )
    }

    fun updateDraftDuration(totalSeconds: Long) {
        val current = _draft.value ?: return
        _draft.value =
            current.copy(
                workoutHistory =
                    current.workoutHistory.copy(
                        totalDuration = totalSeconds.coerceAtLeast(0),
                    ),
            )
    }

    fun addDraftExercisesFromCatalog(catalogExercises: List<ExerciseCatalogEntity>) {
        if (catalogExercises.isEmpty()) return
        val current = _draft.value ?: return
        val existingNames =
            current.exerciseWithSets
                .map { it.exerciseLog.exerciseName.lowercase() }
                .toSet()

        var nextLogId =
            current.exerciseWithSets
                .minOfOrNull { it.exerciseLog.logId }
                ?.let { if (it < 0) it - 1 else -1L }
                ?: -1L
        var nextSetId =
            current.exerciseWithSets
                .flatMap { it.setLogs }
                .minOfOrNull { it.setId }
                ?.let { if (it < 0) it - 1 else -1L }
                ?: -1L

        var nextOrder = current.exerciseWithSets.size
        val additions =
            catalogExercises.mapNotNull { catalog ->
                if (catalog.name.lowercase() in existingNames) return@mapNotNull null
                val logId = nextLogId--
                val setId = nextSetId--
                ExerciseLogWithSets(
                    exerciseLog =
                        ExerciseLogEntity(
                            logId = logId,
                            sessionOwnerId = current.workoutHistory.sessionId,
                            exerciseName = catalog.name,
                            bodyPart = catalog.bodyPart,
                            secondaryMuscles = catalog.secondaryMuscles,
                            imageUrl = catalog.gifUrl,
                            setOrder = nextOrder++,
                        ),
                    setLogs =
                        listOf(
                            SetLogEntity(
                                setId = setId,
                                logOwnerId = logId,
                                reps = 0,
                                weight = 0f,
                                setIndex = 0,
                                clicked = true,
                            ),
                        ),
                )
            }
        if (additions.isEmpty()) return
        _draft.value =
            current.copy(
                exerciseWithSets = current.exerciseWithSets + additions,
            )
    }

    fun moveDraftExercises(fromIndex: Int, toIndex: Int) {
        val current = _draft.value ?: return
        val list = current.exerciseWithSets.toMutableList()
        if (fromIndex !in list.indices || toIndex !in list.indices || fromIndex == toIndex) return
        list.add(toIndex, list.removeAt(fromIndex))
        _draft.value =
            current.copy(
                exerciseWithSets =
                    list.mapIndexed { index, item ->
                        item.copy(exerciseLog = item.exerciseLog.copy(setOrder = index))
                    },
            )
    }

    private fun WorkoutHistoryFull.sortedByExerciseOrder() = copy(
        exerciseWithSets = exerciseWithSets.sortedBy { it.exerciseLog.setOrder },
    )
}
