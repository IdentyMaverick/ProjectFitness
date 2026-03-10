package data.local.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.grozzbear.projectfitness.data.local.entity.SetEntity
import data.local.entity.SetLogEntity
import data.remote.LeaderboardEntry
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class WorkoutLogViewModel(
    val repo: com.grozzbear.projectfitness.data.local.repository.WorkoutRepository,
    workoutId: String,
    val workoutCompleteScreenViewModel: WorkoutCompleteScreenViewModel,
    val workoutCompleteAnalysisScreenViewModel: WorkoutCompleteAnalysisScreenViewModel
) : ViewModel() {
    val workoutFlow = repo.observeWorkoutFull(workoutId)
    val addedExerciseMap = mutableMapOf<String, String>()
    var savedSetIds = mutableMapOf<String, Long>()

    private val initialDataLoaded = mutableSetOf<String>()

    var activeSessionId: String? = null
    var activeExerciseId: String? = null

    private var _elapsedTime = MutableStateFlow(0L)
    val elapsedTime: StateFlow<Long> = _elapsedTime
    private var startTime = 0L
    private var isRunning = false
    val exerciseSetsMap = mutableMapOf<String, SnapshotStateList<SetEntity>>()
    val calculateMaxMap = mutableMapOf<String, Double>()
    val _max = MutableStateFlow(0.0)
    private val _newRecords = MutableStateFlow<List<String>>(emptyList())
    val newRecords: StateFlow<List<String>> = _newRecords

    fun getOrInitSets(
        exerciseName: String,
        defaultSets: List<SetEntity>
    ): SnapshotStateList<SetEntity> {
        return exerciseSetsMap.getOrPut(exerciseName) {
            defaultSets.toMutableStateList()
        }
    }

    fun startWorkout() {
        if (isRunning) return
        isRunning = true
        startTime = System.currentTimeMillis() - (_elapsedTime.value * 1000)
        viewModelScope.launch {
            while (isRunning) {
                _elapsedTime.value = (System.currentTimeMillis() - startTime) / 1000
                delay(1000L)
            }
        }
    }

    fun stopWorkout() {
        isRunning = false
    }

    fun startWorkout(workoutId: String, workoutName: String) {
        viewModelScope.launch {
            val sessionId = repo.startHistoricalWorkout(workoutName, workoutId)
            activeSessionId = sessionId
        }
    }

    fun prepareInitialWorkoutData(workoutFull: com.grozzbear.projectfitness.data.local.entity.WorkoutFull) {
        viewModelScope.launch {
            while (activeSessionId == null) delay(100)

            workoutFull.exercises.forEach { exercise ->
                if (!initialDataLoaded.contains(exercise.exercise.exerciseId)) {
                    val logId = addExercise(
                        exercise.exercise.exerciseName,
                        exercise.exercise.bodyPart,
                        exercise.exercise.secondaryMuscles
                    )
                    if (logId != null) {
                        exercise.sets.forEachIndexed { index, set ->
                            saveSetToDbManual(
                                logId,
                                set.reps.toString(),
                                set.weight.toString(),
                                index
                            )
                        }
                    }
                    initialDataLoaded.add(exercise.exercise.exerciseId)
                }
            }
        }
    }

    suspend fun addExercise(
        exerciseName: String,
        bodyPart: String,
        secondaryMuslces: List<String>
    ): String? {
        activeSessionId?.let { id ->
            if (addedExerciseMap.containsKey(exerciseName)) return addedExerciseMap[exerciseName]

            val exerciseId = repo.addExerciseLog(
                sessionId = id,
                exerciseName = exerciseName,
                bodyPart = bodyPart,
                secondaryMuscles = secondaryMuslces
            )
            val stringId = exerciseId.toString()
            addedExerciseMap[exerciseName] = stringId
            activeExerciseId = stringId
            return stringId
        }
        return null
    }

    private suspend fun saveSetToDbManual(
        logId: String,
        reps: String,
        weight: String,
        setIndex: Int
    ) {
        val key = "${logId}_$setIndex"
        val r = reps.toIntOrNull() ?: 0
        val w = weight.toFloatOrNull() ?: 0f
        val newId = repo.addSetLog(
            logOwnerId = logId.toLong(),
            setId = 0L,
            reps = r,
            weight = w,
            setIndex = setIndex
        )
        savedSetIds[key] = newId
    }

    suspend fun saveSetToDb(reps: String, weight: String, setIndex: Int, exerciseName: String) {
        val logId = addedExerciseMap[exerciseName] ?: return

        val key = "${logId}_$setIndex"
        val existingSetId = savedSetIds[key] ?: 0L
        val r = reps.toIntOrNull() ?: 0
        val w = weight.toFloatOrNull() ?: 0f
        calculateAndStoreMax(exerciseName, w, r)

        val newId = repo.addSetLog(
            logOwnerId = logId.toLong(),
            setId = existingSetId,
            reps = r,
            weight = w,
            setIndex = setIndex
        )
        savedSetIds[key] = newId

        val currentList = exerciseSetsMap[exerciseName]
        if (currentList != null) {
            if (setIndex >= currentList.size) {
                currentList.add(
                    SetEntity(
                        setId = newId.toString(),
                        reps = r,
                        weight = w,
                        exerciseOwnerId = logId
                    )
                )
            } else {
                val oldSet = currentList[setIndex]
                currentList[setIndex] = oldSet.copy(reps = r, weight = w)
            }
        }
    }

    fun deleteSet(setIndex: Int, exerciseName: String) {
        viewModelScope.launch {
            val logId = addedExerciseMap[exerciseName] ?: return@launch
            val key = "${logId}_$setIndex"
            val setIdToDelete = savedSetIds[key] ?: return@launch

            repo.deleteHistoricalSet(
                SetLogEntity(
                    setId = setIdToDelete,
                    logOwnerId = logId.toLong(),
                    reps = 0,
                    weight = 0f,
                    log = "",
                    setIndex = setIndex
                )
            )
            savedSetIds.remove(key)

            val currentSets = exerciseSetsMap[exerciseName] ?: return@launch

            val keysToUpdate = savedSetIds.keys
                .filter { it.startsWith("${logId}_") }
                .map { it.split("_")[1].toInt() }
                .filter { it > setIndex }
                .sorted()

            keysToUpdate.forEach { oldIdx ->
                val oldKey = "${logId}_$oldIdx"
                val newIdx = oldIdx - 1
                val newKey = "${logId}_$newIdx"
                val setId = savedSetIds[oldKey]

                if (setId != null) {
                    val currentSetData = currentSets?.getOrNull(newIdx)
                    val reps = currentSetData?.reps ?: 0
                    val weight = currentSetData?.weight ?: 0f


                    repo.addSetLog(
                        logOwnerId = logId.toLong(),
                        setId = setId,
                        reps = reps,
                        weight = weight,
                        setIndex = newIdx
                    )
                    savedSetIds.remove(oldKey)
                    savedSetIds[newKey] = setId
                }
            }
        }
    }

    fun cancelAndExitWorkout(onComplete: () -> Unit) {
        viewModelScope.launch {
            stopWorkout()
            activeSessionId?.let { repo.deleteHistoricalWorkoutById(it) }
            onComplete()
        }
    }

    fun updateExerciseNote(log: String) {
        activeExerciseId?.let {
            viewModelScope.launch {
                repo.updateExerciseNote(
                    it.toLong(),
                    log
                )
            }
        }
    }

    fun updateSetNote(log: String, setIndex: Int) {
        activeExerciseId?.let {
            viewModelScope.launch {
                repo.updateSetNote(
                    log,
                    it.toLong(),
                    setIndex
                )
            }
        }
    }

    suspend fun loadProgress(updateProgress: (Float) -> Unit) {
        for (i in 1..100) {
            updateProgress(i.toFloat() / 100); delay(100)
        }
    }

    fun toggleSetDone(exerciseName: String, setIndex: Int, isClicked: Boolean) {
        viewModelScope.launch {
            val logId = addedExerciseMap[exerciseName] ?: return@launch
            val key = "${logId}_$setIndex"
            val setId = savedSetIds[key] ?: return@launch
            repo.updateSetLogClick(isClicked, setId)

            val currentList = exerciseSetsMap[exerciseName]
            if (currentList != null && setIndex < currentList.size) {
                currentList[setIndex] = currentList[setIndex].copy(isClicked = isClicked)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun finishWorkout(onComplete: () -> Unit) {
        viewModelScope.launch {
            stopWorkout()
            val prExercises = mutableListOf<String>()
            val setsToDelete = mutableListOf<Long>()
            exerciseSetsMap.forEach { (exerciseName, sets) ->
                val logId = addedExerciseMap[exerciseName]
                val emptySets = sets.filter { it.reps == 0 && it.weight == 0f }
                emptySets.forEach { emptySet ->
                    val setIndex = sets.indexOf(emptySet)
                    val key = "${logId}_$setIndex"
                    savedSetIds[key]?.let { dbId ->
                        setsToDelete.add(dbId)
                    }
                }
            }
            if (setsToDelete.isNotEmpty()) {
                repo.deleteMultipleSets(setsToDelete)
            }

            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            if (currentUserId != null) {
                viewModelScope.launch {
                    val userDoc = FirebaseFirestore.getInstance()
                        .collection("googlecloudusers")
                        .document(currentUserId)
                        .get()
                        .await()

                    val username = userDoc.getString("nickname") ?: ""
                    val userPhotoUri = userDoc.getString("userPhotoUri") ?: ""

                    calculateMaxMap.forEach { (exerciseName, weight) ->
                        if (weight > 0) {
                            val entry = LeaderboardEntry(
                                currentUserId,
                                exerciseName,
                                weight,
                                username,
                                userPhotoUri
                            )
                            val isPr = repo.updateLeaderboard(entry)
                            if (isPr) {
                                prExercises.add(exerciseName)
                            }
                        }
                    }
                    _newRecords.value = prExercises
                    workoutCompleteScreenViewModel.setPrExercises(prExercises)
                }
            }

            activeSessionId?.let {
                workoutCompleteScreenViewModel.setWorkoutData(
                    System.currentTimeMillis(),
                    _elapsedTime.value
                )
                try {
                    saveWorkoutHistoryToFirebase(currentUserId.toString(), it)
                } catch (e: Exception) {
                    Log.d("Firebase Error", e.message.toString())
                }
                workoutCompleteAnalysisScreenViewModel._activeWorkoutId.value = it
                repo.finishWorkout(it, System.currentTimeMillis(), _elapsedTime.value, true)
            }
            onComplete()
        }
    }

    fun calculateAndStoreMax(exerciseName: String, weight: Float, reps: Int) {
        if (weight <= 0f || reps <= 0) return

        val current1RM = if (reps == 1) {
            weight.toDouble()
        } else {
            weight * (1 + reps / 30.0)
        }

        val previousMax = calculateMaxMap[exerciseName] ?: 0.0
        if (current1RM > previousMax) {
            calculateMaxMap[exerciseName] = current1RM
            _max.value = current1RM
        }
    }

    fun saveWorkoutHistoryToFirebase(userId: String, sessionId: String) {
        viewModelScope.launch {
            repo.saveWorkoutHistoryToFirebase(userId, sessionId)
        }
    }
}