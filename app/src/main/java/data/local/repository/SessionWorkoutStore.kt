package com.grozzbear.projectfitness.data.local.repository

import android.util.Log
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.grozzbear.projectfitness.data.local.dao.WorkoutDao
import data.local.entity.ExerciseLogEntity
import data.local.entity.ExerciseLogWithSets
import data.local.entity.SetLogEntity
import data.local.entity.WorkoutHistoryEntity
import data.local.entity.WorkoutHistoryFull
import data.remote.FirestorePaths
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Completed / in-progress sessions: Room tables `workout_history`, `exercise_logs`,
 * `set_logs` and Firestore `googlecloudusers/{uid}/googlecloudworkouts/{sessionId}`.
 *
 * Write rule: persist Room first. Cloud history is written via
 * [saveWorkoutHistoryToFirebase] after the session finishes.
 */
class SessionWorkoutStore internal constructor(
    private val dao: WorkoutDao,
    private val firestore: FirebaseFirestore,
    private val fs: WorkoutFirestore,
) {
    fun observeHistoricalWorkouts() = dao.observeWorkoutHistory()

    fun observeHistoricalWorkoutExercise(sessionId: String) = dao.observeWorkoutHistoryExerciseList(sessionId)

    fun observeWorkoutHistoryFull(sessionId: String) =
        dao.observeWorkoutHistoryFull(sessionId).map { it.sortedByExerciseOrder() }

    fun observeWorkoutHistoryOther(uid: String): Flow<List<WorkoutHistoryFull>> = dao.observeWorkoutHistoryOther(uid)

    suspend fun fetchOtherUserWorkoutDetails(userId: String, sessionId: String): WorkoutHistoryFull? {
        return withContext(Dispatchers.IO) {
            try {
                val workoutRef = fs.historyRef(userId, sessionId)

                val historyDoc = workoutRef.get().await()
                val history =
                    historyDoc.toObject(WorkoutHistoryEntity::class.java) ?: return@withContext null

                val exercisesSnap = workoutRef.collection(FirestorePaths.EXERCISES).get().await()
                val exerciseList =
                    exercisesSnap
                        .map { exDoc ->
                            val exLog = exDoc.toObject(ExerciseLogEntity::class.java)
                            val setsSnap = fs.loadSetDocuments(exDoc.reference)
                            val sets =
                                setsSnap
                                    .toObjects(SetLogEntity::class.java)
                                    .sortedBy { it.setIndex }
                            ExerciseLogWithSets(exLog, sets)
                        }.sortedBy { it.exerciseLog.setOrder }

                WorkoutHistoryFull(history, exerciseList)
            } catch (e: Exception) {
                Log.e("FetchDetails", "Hata: ${e.message}")
                null
            }
        }
    }

    suspend fun startHistoricalWorkout(workoutName: String, workoutId: String): String {
        val newSessionId = UUID.randomUUID().toString()
        dao.insertHistoricalWorkout(
            WorkoutHistoryEntity(
                sessionId = newSessionId,
                workoutId = workoutId,
                workoutName = workoutName,
                dateTimestamp = System.currentTimeMillis(),
                totalDuration = 0,
                syncState = false,
                isCompleted = false,
            ),
        )
        return newSessionId
    }

    suspend fun deleteHistoricalWorkoutById(sessionId: String) {
        dao.deleteHistoricalWorkoutById(sessionId)
    }

    suspend fun addExerciseLog(
        sessionId: String,
        exerciseName: String,
        bodyPart: String,
        secondaryMuscles: List<String>,
        setOrder: Int = 0,
    ): Long = dao.insertHistoricalExercise(
        ExerciseLogEntity(
            sessionOwnerId = sessionId,
            exerciseName = exerciseName,
            weight = 0.0,
            reps = 0,
            setOrder = setOrder,
            log = "",
            imageUrl = "",
            bodyPart = bodyPart,
            secondaryMuscles = secondaryMuscles,
        ),
    )

    suspend fun updateExerciseLogOrder(logId: Long, setOrder: Int) {
        dao.updateExerciseLogOrder(logId, setOrder)
    }

    suspend fun addSetLog(
        logOwnerId: Long,
        setId: Long,
        reps: Int,
        weight: Float,
        setIndex: Int,
        clicked: Boolean = false,
    ): Long = dao.insertHistoricalSet(
        SetLogEntity(
            logOwnerId = logOwnerId,
            reps = reps,
            weight = weight,
            log = "",
            setIndex = setIndex,
            setId = setId,
            clicked = clicked,
        ),
    )

    suspend fun deleteHistoricalSet(setLog: SetLogEntity): Int = dao.deleteHistoricalSet(setLog)

    suspend fun finishWorkout(sessionId: String, dateTimestamp: Long, duration: Long, isCompleted: Boolean) {
        dao.completeWorkout(sessionId, dateTimestamp, duration, false, isCompleted)
    }

    suspend fun observeHistoricalExercise(exerciseId: String) = dao.observeHistoricalExercise(exerciseId)

    suspend fun updateExerciseNote(logId: Long, log: String) {
        dao.updateExerciseNote(logId, log)
    }

    suspend fun updateSetNote(log: String, logOwnerId: Long, setIndex: Int) {
        dao.updateSetNote(log, logOwnerId, setIndex)
    }

    suspend fun updateSetLogClick(isClicked: Boolean, setId: Long) {
        dao.updateSetLogClick(isClicked, setId)
    }

    suspend fun deleteMultipleSets(setId: List<Long>) {
        dao.deleteMultipleSets(setId)
    }

    suspend fun saveWorkoutHistoryToFirebase(userId: String, sessionId: String) {
        if (userId.isBlank()) return
        withContext(Dispatchers.IO) {
            try {
                val historyFull = dao.observeWorkoutHistory(sessionId)
                val batch = firestore.batch()
                val workoutRef = fs.historyRef(userId, sessionId)

                batch.set(workoutRef, historyFull.workoutHistory)

                historyFull.exerciseWithSets.forEach { exerciseWith ->
                    val exerciseLog = exerciseWith.exerciseLog
                    val exerciseRef =
                        workoutRef
                            .collection(FirestorePaths.EXERCISES)
                            .document(exerciseLog.logId.toString())

                    batch.set(exerciseRef, exerciseLog)

                    exerciseWith.setLogs.sortedBy { it.setIndex }.forEach { setLog ->
                        val setRef =
                            exerciseRef
                                .collection(FirestorePaths.SETS)
                                .document(setLog.setId.toString())
                        batch.set(setRef, setLog)
                    }
                }

                batch.commit().await()
            } catch (e: Exception) {
                Log.e("FirebaseError", "Hata: ${e.localizedMessage}")
            }
        }
    }

    fun observeUserWorkoutHistory(nickname: String): Flow<List<WorkoutHistoryEntity>> = callbackFlow {
        if (nickname.isBlank()) {
            trySend(emptyList())
            awaitClose { }
            return@callbackFlow
        }
        var workoutsListener: ListenerRegistration? = null
        val userQuery =
            firestore
                .collection(FirestorePaths.USERS)
                .whereEqualTo("nickname", nickname)

        val userListener =
            userQuery.addSnapshotListener { userSnapshot, userError ->
                if (userError != null) {
                    Log.e("Firebase", "Kullanıcı sorgulama hatası: ${userError.message}")
                    close(userError)
                    return@addSnapshotListener
                }

                workoutsListener?.remove()
                workoutsListener = null

                val userId = userSnapshot?.documents?.firstOrNull()?.id
                if (userId != null) {
                    workoutsListener =
                        fs
                            .historyCol(userId)
                            .orderBy("dateTimestamp", Query.Direction.DESCENDING)
                            .addSnapshotListener { workoutSnapshot, workoutError ->
                                if (workoutError != null) {
                                    Log.e("Firebase", "Antrenman çekme hatası: ${workoutError.message}")
                                    return@addSnapshotListener
                                }
                                val history =
                                    workoutSnapshot?.toObjects(WorkoutHistoryEntity::class.java)
                                        ?: emptyList()
                                trySend(history)
                            }
                } else {
                    trySend(emptyList())
                }
            }

        awaitClose {
            workoutsListener?.remove()
            userListener.remove()
        }
    }

    suspend fun getUserTotalWorkoutNumber(userId: String): Long = try {
        val query = fs.historyCol(userId).count()
        val snapshot = query.get(AggregateSource.SERVER).await()
        snapshot.count
    } catch (e: Exception) {
        0L
    }

    suspend fun getTotalLiftedWeight(userId: String): Long {
        var totalWeight = 0.0

        try {
            val workoutsSnapshot = fs.historyCol(userId).get().await()

            for (workoutDoc in workoutsSnapshot.documents) {
                val exercisesSnapshot =
                    workoutDoc.reference
                        .collection(FirestorePaths.EXERCISES)
                        .get()
                        .await()

                for (exerciseDoc in exercisesSnapshot.documents) {
                    val setSnapshot = fs.loadSetDocuments(exerciseDoc.reference)

                    for (setDoc in setSnapshot.documents) {
                        val weight = (setDoc.get("weight") as? Number)?.toDouble() ?: 0.0
                        val reps = (setDoc.get("reps") as? Number)?.toDouble() ?: 0.0
                        totalWeight += (weight * reps)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseStats", "Ağırlık hesaplanırken hata oluştu: ${e.message}")
        }

        return totalWeight.toLong()
    }

    suspend fun getTotalSpentTime(userId: String): Long = try {
        val querySnapshot = fs.historyCol(userId).get().await()
        querySnapshot.documents.sumOf { document ->
            val seconds = document.getLong("totalDuration") ?: 0L
            seconds / 60
        }
    } catch (e: Exception) {
        Log.e("FirebaseStats", "Dakika hesaplama hatası: ${e.message}")
        0L
    }

    suspend fun checkWorkoutExists(sessionId: String): WorkoutHistoryEntity? = dao.getWorkoutByIdDirect(sessionId)

    suspend fun insertFullHistory(historyFull: WorkoutHistoryFull) {
        withContext(Dispatchers.IO) {
            dao.insertHistoricalWorkout(historyFull.workoutHistory)

            historyFull.exerciseWithSets.forEach { exerciseWith ->
                val newLogId = dao.insertHistoricalExercise(exerciseWith.exerciseLog)

                exerciseWith.setLogs.forEach { setLog ->
                    dao.insertHistoricalSet(setLog.copy(logOwnerId = newLogId))
                }
            }
        }
    }

    suspend fun updateSessionDuration(sessionId: String, duration: Long) {
        dao.updateSessionDuration(sessionId, duration)
    }

    private fun WorkoutHistoryFull.sortedByExerciseOrder() = copy(
        exerciseWithSets = exerciseWithSets.sortedBy { it.exerciseLog.setOrder },
    )
}
