package com.grozzbear.projectfitness.data.local.repository

import android.util.Log
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.grozzbear.R
import com.grozzbear.projectfitness.data.local.dao.ExerciseCatalogDao
import com.grozzbear.projectfitness.data.local.dao.WorkoutDao
import com.grozzbear.projectfitness.data.local.entity.SetEntity
import com.grozzbear.projectfitness.data.local.entity.WorkoutEntity
import com.grozzbear.projectfitness.data.local.entity.WorkoutExerciseEntity
import com.grozzbear.projectfitness.data.remote.Workoutin
import com.grozzbear.projectfitness.data.remote.toEntity
import data.local.entity.ExerciseLogEntity
import data.local.entity.SetLogEntity
import data.local.entity.WorkoutHistoryEntity
import data.local.entity.WorkoutHistoryFull
import data.remote.LeaderboardEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class WorkoutRepository(
    private val dao: WorkoutDao,
    private val catalogdao: ExerciseCatalogDao,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun observeWorkouts() = dao.observeWorkouts()
    fun observeWorkoutFull(id: String) = dao.observeWorkoutFull(id)
    fun observeHistoricalWorkouts() = dao.observeWorkoutHistory()
    fun observeHistoricalWorkoutExercise(sessionId: String) =
        dao.observeWorkoutHistoryExerciseList(sessionId)

    fun observeWorkoutHistoryFull(sessionId: String) = dao.observeWorkoutHistoryFull(sessionId)


    suspend fun createWorkout(
        workoutId: String,
        name: String,
        workoutType: String,
        workoutRating: Int,
        ownerUid: String?,
        syncState: Boolean,
        image: Int
    ): String {
        dao.insertWorkout(
            WorkoutEntity(
                workoutId = workoutId,
                workoutName = name,
                workoutType = workoutType,
                workoutRating = workoutRating,
                ownerUid = ownerUid,
                syncState = syncState,
                image = image
            )
        )
        return workoutId
    }


    suspend fun addExercise(
        exerciseId: String,
        workoutId: String,
        name: String,
        catalogExerciseId: String,
        bodyPart: String,
        secondaryMuscles: List<String> = emptyList()
    ) {
        dao.insertExercise(
            WorkoutExerciseEntity(
                exerciseId = exerciseId,
                workoutOwnerId = workoutId,
                exerciseName = name,
                catalogExerciseId = catalogExerciseId,
                bodyPart = bodyPart,
                secondaryMuscles = secondaryMuscles
            )
        )
    }

    suspend fun addSet(
        setId: String,
        exerciseId: String,
        reps: Int,
        weight: Float,
        note: String? = null
    ) {
        try {
            dao.insertSet(
                SetEntity(
                    setId = setId,
                    exerciseOwnerId = exerciseId,
                    reps = reps,
                    weight = weight,
                    note = note
                )
            )
        } catch (e: Exception) {
            Log.e("Exceptionstime", e.message.toString())
        }

    }


    suspend fun fetchOtherUserWorkoutDetails(
        userId: String,
        sessionId: String
    ): WorkoutHistoryFull? {
        return withContext(Dispatchers.IO) {
            try {
                val db = FirebaseFirestore.getInstance()
                val workoutRef = db.collection("googlecloudusers").document(userId)
                    .collection("googlecloudworkouts").document(sessionId)

                val historyDoc = workoutRef.get().await()
                val history =
                    historyDoc.toObject(WorkoutHistoryEntity::class.java) ?: return@withContext null

                val exercisesSnap = workoutRef.collection("googlecloudexercises").get().await()
                val exerciseList = exercisesSnap.map { exDoc ->
                    val exLog = exDoc.toObject(ExerciseLogEntity::class.java)
                    val setsSnap = exDoc.reference.collection("googlecloudsets").get().await()
                    val sets = setsSnap.toObjects(SetLogEntity::class.java)
                    Log.d("whfullin", sets.toString())
                    data.local.entity.ExerciseLogWithSets(exLog, sets)
                }

                WorkoutHistoryFull(history, exerciseList)
            } catch (e: Exception) {
                Log.e("FetchDetails", "Hata: ${e.message}")
                null
            }
        }
    }

    suspend fun deleteWorkout(id: String) = dao.deleteWorkout(id)

    suspend fun deleteWorkoutFirebase(workoutId: String) {
        try {
            val workoutRef = firestore.collection("googlecloudworkouts").document(workoutId)
            val batch = firestore.batch()

            val exerciseSnapshot = workoutRef.collection("googlecloudexercises").get().await()
            for (exerciseDoc in exerciseSnapshot) {
                val setsSnapshot = exerciseDoc.reference.collection("sets").get().await()
                for (setsDoc in setsSnapshot) {
                    batch.delete(setsDoc.reference)
                }
                batch.delete(exerciseDoc.reference)
            }
            batch.delete(workoutRef)
            batch.commit().await()
            Log.d("DeleteFirebase", "Workout ve tüm alt verileri başarıyla silindi.")
        } catch (e: Exception) {
            Log.e("DeleteFirebase", "Silme hatası: ${e.message}")
        }
    }

    suspend fun deleteSelectedExercise(exerciseId: String, workoutId: String) {
        dao.deleteSelectedExercise(exerciseId)

        dao.touchWorkout(workoutId)
    }

    suspend fun deleteSelectedExerciseFirebase(workoutId: String, exerciseId: String) {
        try {
            val exerciseDocRef = firestore.collection("googlecloudworkouts")
                .document(workoutId)
                .collection("googlecloudexercises")
                .document(exerciseId)
            val batch = firestore.batch()
            val setSnapshot = exerciseDocRef.collection("sets").get().await()
            for (setDoc in setSnapshot) {
                batch.delete(setDoc.reference)
            }
            batch.delete(exerciseDocRef)
            batch.commit().await()
            dao.touchWorkout(workoutId)
            Log.d("DeleteFirebase", "Egzersiz ve tüm alt verileri başarıyla silindi.")
        } catch (e: Exception) {
            Log.e("DeleteFirebase", "Silme hatası: ${e.message}")
        }
    }


    suspend fun workoutCount(): Int = dao.workoutCount()


    suspend fun seedDefaultsIfEmpty() {
        if (workoutCount() > 0) return

        val pushId = "push"
        createWorkout(
            pushId,
            "Push Challenge",
            "challange",
            3,
            null,
            false,
            R.drawable.registerandforgetpasswordscreenphoto
        )

        val benchId = UUID.randomUUID().toString()
        addExercise(
            benchId,
            pushId,
            "Dumbbell Bench Press",
            "9u9UAvGkJZPNuINgoaBR",
            "Chest",
            listOf("Triceps", "Front delts")
        )

        addSet(UUID.randomUUID().toString(), benchId, 12, 30f)
        addSet(UUID.randomUUID().toString(), benchId, 10, 32.5f)
        addSet(UUID.randomUUID().toString(), benchId, 8, 35f)

        val ohpId = UUID.randomUUID().toString()
        addExercise(
            ohpId,
            pushId,
            "Standing Barbell Overhead Press",
            "okwXFxp3bLE5GCq65CsT",
            "Shoulders",
            listOf("Triceps", "Upper chest", "Core")
        )
        addSet(UUID.randomUUID().toString(), ohpId, 12, 20f)
        addSet(UUID.randomUUID().toString(), ohpId, 10, 22.5f)
        addSet(UUID.randomUUID().toString(), ohpId, 8, 25f)

        val pullId = "pull"
        createWorkout(
            pullId,
            "Pull Challenge",
            "coach",
            4,
            null,
            false,
            R.drawable.infohorizontalscreensecondphoto
        )

        val latId = UUID.randomUUID().toString()
        addExercise(
            latId,
            pullId,
            "Lat Pulldown",
            "pSyUAfMjZYildFc3s3vi",
            "Back",
            listOf("Biceps", "Rear delts")
        )
        addSet(UUID.randomUUID().toString(), latId, 12, 30f)
        addSet(UUID.randomUUID().toString(), latId, 10, 32.5f)
        addSet(UUID.randomUUID().toString(), latId, 8, 35f)
    }

    fun getAllCatalogExercises() = catalogdao.observeAllActive()

    fun observeMyWorkouts(uid: String) = dao.observeMyWorkouts(uid)

    suspend fun saveAndSyncWorkout(
        workout: WorkoutEntity,
        exercises: List<WorkoutExerciseEntity>,
        sets: List<SetEntity>
    ) {
        try {
            val batch = firestore.batch()

            val workoutRef = firestore.collection("googlecloudworkouts").document(workout.workoutId)
            batch.set(workoutRef, workout)

            for (exercise in exercises) {
                val exerciseRef =
                    workoutRef.collection("googlecloudexercises").document(exercise.exerciseId)
                batch.set(exerciseRef, exercise)

                val relatedSets = sets.filter { it.exerciseOwnerId == exercise.exerciseId }

                for (set in relatedSets) {
                    val setRef = exerciseRef.collection("sets").document(set.setId)
                    batch.set(setRef, set)
                }

            }

            batch.commit().await()
        } catch (e: Exception) {
            Log.e("Sync", "Yükleme hatası ${e.message}")
        }
    }

    suspend fun syncMyWorkouts(userId: String) {
        try {
            val snap = firestore.collection("googlecloudworkouts")
                .whereEqualTo("ownerUid", userId)
                .get()
                .await()

            val remoteWorkouts = snap.toObjects(WorkoutEntity::class.java)

            if (remoteWorkouts.isNotEmpty()) {
                dao.insertAllWorkouts(remoteWorkouts)

                for (workout in remoteWorkouts) {
                    val exerciseSnap = firestore.collection("googlecloudworkouts")
                        .document(workout.workoutId)
                        .collection("googlecloudexercises")
                        .get()
                        .await()

                    val remoteExercises = exerciseSnap.documents.map { doc ->
                        val ownerIdFromFirebase = doc.getString("workoutId")
                            ?: doc.getString("workoutOwnerId")
                            ?: ""

                        WorkoutExerciseEntity(
                            exerciseId = doc.id,

                            workoutOwnerId = ownerIdFromFirebase,

                            exerciseName = doc.getString("exerciseName") ?: "",
                            catalogExerciseId = doc.getString("catalogExerciseId")
                        )
                    }

                    if (remoteExercises.isNotEmpty()) {
                        for (ex in remoteExercises) {
                            val finalExercise = if (ex.workoutOwnerId.isBlank()) {
                                ex.copy(workoutOwnerId = workout.workoutId)
                            } else {
                                ex
                            }

                            dao.insertExercise(finalExercise)
                            val setsSnap = firestore.collection("googlecloudworkouts")
                                .document(workout.workoutId)
                                .collection("googlecloudexercises")
                                .document(finalExercise.exerciseId)
                                .collection("sets")
                                .get()
                                .await()

                            val remoteSets = setsSnap.documents.map { setDoc ->
                                SetEntity(
                                    setId = setDoc.id,
                                    exerciseOwnerId = finalExercise.exerciseId,
                                    reps = setDoc.getLong("reps")?.toInt() ?: 0,
                                    weight = setDoc.getDouble("weight")?.toFloat() ?: 0f,
                                    note = setDoc.getString("note")
                                )
                            }

                            for (set in remoteSets) {
                                dao.insertSet(set)
                            }
                            Log.d(
                                "SyncDebug",
                                "${finalExercise.exerciseName} için ${remoteSets.size} set indirildi."
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("SyncWorkouts", "Firebase hatası: ${e.message}")
        }
    }

    suspend fun startHistoricalWorkout(
        workoutName: String,
        workoutId: String
    ): String {
        val newSessionId = UUID.randomUUID().toString()

        dao.insertHistoricalWorkout(
            WorkoutHistoryEntity(
                sessionId = newSessionId,
                workoutId = workoutId,
                workoutName = workoutName,
                dateTimestamp = System.currentTimeMillis(),
                totalDuration = 0,
                syncState = false,
                isCompleted = false

            )
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
        secondaryMuscles: List<String>
    ): Long {
        return dao.insertHistoricalExercise(
            ExerciseLogEntity(
                sessionOwnerId = sessionId,
                exerciseName = exerciseName,
                weight = 0.0,
                reps = 0,
                setOrder = 0,
                log = "",
                imageUrl = "",
                bodyPart = bodyPart,
                secondaryMuscles = secondaryMuscles
            )
        )
    }

    suspend fun addSetLog(
        logOwnerId: Long,
        setId: Long,
        reps: Int,
        weight: Float,
        setIndex: Int
    ): Long {
        return dao.insertHistoricalSet(
            SetLogEntity(
                logOwnerId = logOwnerId,
                reps = reps,
                weight = weight,
                log = "",
                setIndex = setIndex,
                setId = setId
            )
        )
    }

    suspend fun deleteHistoricalSet(setLog: SetLogEntity): Int {
        return dao.deleteHistoricalSet(setLog)
    }

    suspend fun finishWorkout(
        sessionId: String,
        dateTimestamp: Long,
        duration: Long,
        isCompleted: Boolean
    ) {
        dao.completeWorkout(sessionId, dateTimestamp, duration, false, isCompleted)
    }

    suspend fun observeHistoricalExercise(exerciseId: String) =
        dao.observeHistoricalExercise(exerciseId)

    suspend fun syncCatalog() {
        try {
            val snap =
                firestore.collection("googlecloud").whereEqualTo("isActive", true).get().await()
            val entities = snap.documents.mapNotNull { doc ->
                val dto = doc.toObject(Workoutin::class.java)
                dto?.toEntity(doc.id)
            }
            if (catalogdao.count() == 0 || catalogdao.count() != entities.size) {
                catalogdao.upsertAll(entities)
            }
        } catch (e: Exception) {
            Log.e("SyncCatalog", "Hata: ${e.message}")
        }
    }

    suspend fun syncExercisesFromFirestore() {
        try {
            val snapshot = firestore.collection("googlecloud").get().await()
            for (doc in snapshot) {
                val imageName = doc.getString("exerciseImage") ?: ""
                val name = doc.getString("name") ?: ""
                Log.d("exercise and name", "Hata: ${imageName} & ${name}")
                dao.updateExerciseLogImage(name = name, imageUrl = imageName)
            }
        } catch (e: Exception) {
            Log.e("SyncExercises", "Hata: ${e.message}")
        }
    }

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

    suspend fun updateLeaderboard(entry: LeaderboardEntry): Boolean {
        val db = FirebaseFirestore.getInstance()
        val docId = "${entry.userId}_${entry.exerciseName}"

        return try {
            val document = db.collection("googlecloudleaderboard").document(docId).get().await()
            if (document.exists()) {
                val oldWeight = document.getDouble("weight") ?: 0.0
                if (entry.weight > oldWeight) {
                    db.collection("googlecloudleaderboard").document(docId).set(entry).await()
                    true
                } else {
                    false
                }
            } else {
                db.collection("googlecloudleaderboard").document(docId).set(entry).await()
                true
            }
        } catch (e: Exception) {
            Log.e("PR_Check", "Hata: ${e.message}")
            false
        }
    }

    fun getLeaderboard(exerciseName: String): Flow<List<LeaderboardEntry>> =
        kotlinx.coroutines.flow.flow {
            try {
                val snapshot = firestore.collection("googlecloudleaderboard")
                    .whereEqualTo("exerciseName", exerciseName)
                    .orderBy(
                        "weight",
                        com.google.firebase.firestore.Query.Direction.DESCENDING
                    )
                    .limit(20)
                    .get()
                    .await()

                val entries = snapshot.toObjects(LeaderboardEntry::class.java)
                emit(entries)
            } catch (e: Exception) {
                Log.e("LeaderboardError", "Veri çekme hatası: ${e.message}")
                emit(emptyList())
            }
        }

    suspend fun updateSet(setId: String, exerciseOwnerId: String, reps: Int, weight: Float) {
        try {
            dao.updateSet(
                SetEntity(
                    setId = setId,
                    exerciseOwnerId = exerciseOwnerId,
                    reps = reps,
                    weight = weight
                )
            )
        } catch (e: Exception) {
            Log.e("WorkoutRepository", "Set güncellenemedi: ${e.message}")
        }
    }

    suspend fun deleteSet(set: SetEntity) {
        try {
            dao.deleteSet(set)
        } catch (e: Exception) {
            Log.e("WorkoutRepository", "Set silinirken hata: ${e.message}")
        }
    }

    fun observeWorkoutHistoryOther(uid: String): Flow<List<WorkoutHistoryFull>> {
        return dao.observeWorkoutHistoryOther(uid)
    }

    suspend fun saveWorkoutHistoryToFirebase(userId: String, sessionId: String) {
        withContext(Dispatchers.IO) {
            try {
                val historyFull = dao.observeWorkoutHistory(sessionId)

                val db = FirebaseFirestore.getInstance()
                val batch = db.batch()

                val workoutRef = db.collection("googlecloudusers")
                    .document(userId)
                    .collection("googlecloudworkouts")
                    .document(sessionId)

                batch.set(workoutRef, historyFull.workoutHistory)

                historyFull.exerciseWithSets.forEach { exerciseWith ->
                    val exerciseLog = exerciseWith.exerciseLog
                    val exerciseRef = workoutRef.collection("googlecloudexercises")
                        .document(exerciseLog.logId.toString())

                    batch.set(exerciseRef, exerciseLog)

                    exerciseWith.setLogs.forEach { setLog ->
                        val setRef = exerciseRef.collection("googlecloudsets")
                            .document(setLog.setId.toString())
                        batch.set(setRef, setLog)
                    }
                }

                Log.d("Firebase", "Gönderiliyor...")
                batch.commit().await()
                Log.d("Firebase Success", "Antrenman başarıyla Firebase'e kaydedildi!")

            } catch (e: Exception) {
                Log.e("Firebase Errorer", "Hata: ${e.localizedMessage}")
            }
        }
    }


    fun observeUserWorkoutHistory(nickname: String): Flow<List<WorkoutHistoryEntity>> =
        callbackFlow {
            val db = FirebaseFirestore.getInstance()

            val userQuery = db.collection("googlecloudusers")
                .whereEqualTo("nickname", nickname)

            val userListener = userQuery.addSnapshotListener { userSnapshot, userError ->
                if (userError != null) {
                    Log.e("Firebase", "Kullanıcı sorgulama hatası: ${userError.message}")
                    close(userError)
                    return@addSnapshotListener
                }

                val userDoc = userSnapshot?.documents?.firstOrNull()
                val userId = userDoc?.id

                if (userId != null) {
                    val workoutsListener = db.collection("googlecloudusers")
                        .document(userId)
                        .collection("googlecloudworkouts")
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
                userListener.remove()
                Log.d("Firebase", "Akış kapatıldı, dinleyiciler temizlendi.")
            }
        }

    suspend fun getUserTotalWorkoutNumber(userId: String): Long {
        val db = FirebaseFirestore.getInstance()
        return try {
            val query = db.collection("googlecloudusers")
                .document(userId)
                .collection("googlecloudworkouts")
                .count()
            val snapshot = query.get(AggregateSource.SERVER).await()
            snapshot.count
        } catch (e: Exception) {
            0L
        }
    }

    suspend fun getTotalLiftedWeight(userId: String): Long {
        val db = FirebaseFirestore.getInstance()
        var totalWeight = 0.0

        try {
            val workoutsSnapshot = db.collection("googlecloudusers")
                .document(userId)
                .collection("googlecloudworkouts")
                .get()
                .await()

            for (workoutDoc in workoutsSnapshot.documents) {
                val exercisesSnapshot = workoutDoc.reference
                    .collection("googlecloudexercises")
                    .get()
                    .await()

                for (exerciseDoc in exercisesSnapshot.documents) {
                    val setSnapshot = exerciseDoc.reference
                        .collection("googlecloudsets")
                        .get()
                        .await()

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

    suspend fun getTotalSpentTime(userId: String): Long {
        val db = FirebaseFirestore.getInstance()

        return try {
            val querySnapshot = db.collection("googlecloudusers")
                .document(userId)
                .collection("googlecloudworkouts")
                .get()
                .await()

            querySnapshot.documents.sumOf { document ->
                val seconds = document.getLong("totalDuration") ?: 0L
                seconds / 60
            }
        } catch (e: Exception) {
            Log.e("FirebaseStats", "Dakika hesaplama hatası: ${e.message}")
            0L
        }
    }

    suspend fun checkWorkoutExists(sessionId: String): WorkoutHistoryEntity? {
        return dao.getWorkoutByIdDirect(sessionId)
    }

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
}