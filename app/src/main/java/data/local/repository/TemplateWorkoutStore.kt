package com.grozzbear.projectfitness.data.local.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.grozzbear.R
import com.grozzbear.projectfitness.data.local.dao.ExerciseCatalogDao
import com.grozzbear.projectfitness.data.local.dao.WorkoutDao
import com.grozzbear.projectfitness.data.local.entity.SetEntity
import com.grozzbear.projectfitness.data.local.entity.WorkoutEntity
import com.grozzbear.projectfitness.data.local.entity.WorkoutExerciseEntity
import data.remote.FirestorePaths
import java.util.UUID
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

/**
 * Planned workouts: Room tables `workout`, `exercise`, `exercise_set`
 * and Firestore root `googlecloudworkouts/{workoutId}`.
 *
 * Write rule: persist Room first, then the matching template Firestore docs.
 */
class TemplateWorkoutStore internal constructor(
    private val dao: WorkoutDao,
    private val catalogDao: ExerciseCatalogDao,
    private val firestore: FirebaseFirestore,
    private val fs: WorkoutFirestore,
) {
    fun observeWorkouts() = dao.observeWorkouts()

    fun observeWorkoutFull(id: String) = dao.observeWorkoutFull(id).map { full ->
        full.copy(
            exercises =
                full.exercises
                    .sortedBy { it.exercise.orderIndex }
                    .map { item -> item.copy(sets = item.sets.sortedBy { it.setIndex }) },
        )
    }

    fun observeMyWorkouts(uid: String) = dao.observeMyWorkouts(uid)

    suspend fun createWorkout(
        workoutId: String,
        name: String,
        workoutType: String,
        workoutRating: Int,
        ownerUid: String?,
        syncState: Boolean,
        image: Int,
    ): String {
        dao.insertWorkout(
            WorkoutEntity(
                workoutId = workoutId,
                workoutName = name,
                workoutType = workoutType,
                workoutRating = workoutRating,
                ownerUid = ownerUid,
                syncState = syncState,
                image = image,
            ),
        )
        return workoutId
    }

    suspend fun addExercise(
        exerciseId: String,
        workoutId: String,
        name: String,
        catalogExerciseId: String,
        bodyPart: String,
        secondaryMuscles: List<String> = emptyList(),
    ) {
        val nextOrderIndex = (dao.getMaxOrderIndex(workoutId) ?: -1) + 1
        dao.insertExercise(
            WorkoutExerciseEntity(
                exerciseId = exerciseId,
                workoutOwnerId = workoutId,
                exerciseName = name,
                catalogExerciseId = catalogExerciseId,
                bodyPart = bodyPart,
                secondaryMuscles = secondaryMuscles,
                orderIndex = nextOrderIndex,
            ),
        )
    }

    suspend fun addExercisesFromCatalog(workoutId: String, catalogIds: List<String>) {
        if (catalogIds.isEmpty()) return

        val catalogById = catalogDao.observeAllActive().first().associateBy { it.id }
        val batch = firestore.batch()
        val workoutRef = fs.templateRef(workoutId)

        for (catalogId in catalogIds) {
            val catalog = catalogById[catalogId] ?: continue
            val exerciseId = UUID.randomUUID().toString()
            val nextOrderIndex = (dao.getMaxOrderIndex(workoutId) ?: -1) + 1

            val exercise =
                WorkoutExerciseEntity(
                    exerciseId = exerciseId,
                    workoutOwnerId = workoutId,
                    exerciseName = catalog.name,
                    catalogExerciseId = catalog.id,
                    bodyPart = catalog.bodyPart,
                    secondaryMuscles = catalog.secondaryMuscles,
                    orderIndex = nextOrderIndex,
                )
            dao.insertExercise(exercise)

            val defaultSets =
                List(3) { index ->
                    SetEntity(
                        setId = UUID.randomUUID().toString(),
                        exerciseOwnerId = exerciseId,
                        reps = 10,
                        weight = 0f,
                        setIndex = index,
                    )
                }
            defaultSets.forEach { dao.insertSet(it) }

            val exerciseRef = workoutRef.collection(FirestorePaths.EXERCISES).document(exerciseId)
            batch.set(exerciseRef, exercise)
            for (set in defaultSets) {
                val setRef = exerciseRef.collection(FirestorePaths.SETS).document(set.setId)
                batch.set(setRef, set)
            }
        }

        dao.touchWorkout(workoutId)
        batch.commit().await()
    }

    suspend fun addSet(
        setId: String,
        exerciseId: String,
        reps: Int,
        weight: Float,
        note: String? = null,
        workoutId: String? = null,
        setIndex: Int,
    ) {
        val set =
            SetEntity(
                setId = setId,
                exerciseOwnerId = exerciseId,
                reps = reps,
                weight = weight,
                note = note,
                setIndex = setIndex,
            )
        try {
            dao.insertSet(set)
            if (workoutId.isNullOrBlank()) return

            dao.touchWorkout(workoutId)
            val exerciseRef =
                fs
                    .templateRef(workoutId)
                    .collection(FirestorePaths.EXERCISES)
                    .document(exerciseId)
            fs.resolveSetDocument(exerciseRef, setId).set(set).await()
        } catch (e: Exception) {
            Log.e("Exceptionstime", e.message.toString())
        }
    }

    suspend fun updateSet(setId: String, exerciseOwnerId: String, reps: Int, weight: Float, workoutId: String) {
        try {
            dao.updateSet(setId, reps, weight)
            dao.touchWorkout(workoutId)

            val exerciseRef =
                fs
                    .templateRef(workoutId)
                    .collection(FirestorePaths.EXERCISES)
                    .document(exerciseOwnerId)
            fs
                .resolveSetDocument(exerciseRef, setId)
                .set(
                    mapOf(
                        "setId" to setId,
                        "exerciseOwnerId" to exerciseOwnerId,
                        "reps" to reps,
                        "weight" to weight,
                    ),
                    SetOptions.merge(),
                ).await()
        } catch (e: Exception) {
            Log.e("WorkoutRepository", "Set güncellenemedi: ${e.message}")
        }
    }

    suspend fun deleteSet(set: SetEntity, workoutId: String? = null) {
        try {
            dao.deleteSet(set)
            if (workoutId.isNullOrBlank()) return

            dao.touchWorkout(workoutId)
            val exerciseRef =
                fs
                    .templateRef(workoutId)
                    .collection(FirestorePaths.EXERCISES)
                    .document(set.exerciseOwnerId)
            val batch = firestore.batch()
            batch.delete(exerciseRef.collection(FirestorePaths.SETS).document(set.setId))
            batch.delete(exerciseRef.collection(FirestorePaths.SETS_LEGACY).document(set.setId))
            batch.commit().await()
        } catch (e: Exception) {
            Log.e("WorkoutRepository", "Set silinirken hata: ${e.message}")
        }
    }

    suspend fun deleteWorkout(id: String) = dao.deleteWorkout(id)

    suspend fun deleteWorkoutFirebase(workoutId: String) {
        try {
            val workoutRef = fs.templateRef(workoutId)
            val batch = firestore.batch()

            val exerciseSnapshot = workoutRef.collection(FirestorePaths.EXERCISES).get().await()
            for (exerciseDoc in exerciseSnapshot) {
                fs.deleteSetDocuments(exerciseDoc.reference, batch)
                batch.delete(exerciseDoc.reference)
            }
            batch.delete(workoutRef)
            batch.commit().await()
        } catch (e: Exception) {
            Log.e("DeleteFirebase", "Silme hatası: ${e.message}")
        }
    }

    suspend fun deleteSelectedExercise(exerciseId: String, workoutId: String) {
        dao.deleteSelectedExercise(exerciseId)
        dao.touchWorkout(workoutId)

        try {
            val exerciseDocRef =
                fs
                    .templateRef(workoutId)
                    .collection(FirestorePaths.EXERCISES)
                    .document(exerciseId)
            val batch = firestore.batch()
            fs.deleteSetDocuments(exerciseDocRef, batch)
            batch.delete(exerciseDocRef)
            batch.commit().await()
        } catch (e: Exception) {
            Log.e("DeleteSelectedExercise", "Silme hatası: ${e.message}")
        }
    }

    suspend fun workoutCount(): Int = dao.workoutCount()

    suspend fun seedDefaultsIfEmpty() {
        dao.migrateChallengeSpelling()
        if (workoutCount() > 0) return

        val pushId = "push"
        createWorkout(
            pushId,
            "Push Challenge",
            "challenge",
            3,
            null,
            false,
            R.drawable.registerandforgetpasswordscreenphoto,
        )

        val benchId = UUID.randomUUID().toString()
        addExercise(
            benchId,
            pushId,
            "Dumbbell Bench Press",
            "9u9UAvGkJZPNuINgoaBR",
            "Chest",
            listOf("Triceps", "Front delts"),
        )

        addSet(UUID.randomUUID().toString(), benchId, 12, 30f, setIndex = 0)
        addSet(UUID.randomUUID().toString(), benchId, 10, 32.5f, setIndex = 1)
        addSet(UUID.randomUUID().toString(), benchId, 8, 35f, setIndex = 2)

        val ohpId = UUID.randomUUID().toString()
        addExercise(
            ohpId,
            pushId,
            "Standing Barbell Overhead Press",
            "okwXFxp3bLE5GCq65CsT",
            "Shoulders",
            listOf("Triceps", "Upper chest", "Core"),
        )
        addSet(UUID.randomUUID().toString(), ohpId, 12, 20f, setIndex = 0)
        addSet(UUID.randomUUID().toString(), ohpId, 10, 22.5f, setIndex = 1)
        addSet(UUID.randomUUID().toString(), ohpId, 8, 25f, setIndex = 2)

        val pullId = "pull"
        createWorkout(
            pullId,
            "Pull Challenge",
            "coach",
            4,
            null,
            false,
            R.drawable.infohorizontalscreensecondphoto,
        )

        val latId = UUID.randomUUID().toString()
        addExercise(
            latId,
            pullId,
            "Lat Pulldown",
            "pSyUAfMjZYildFc3s3vi",
            "Back",
            listOf("Biceps", "Rear delts"),
        )
        addSet(UUID.randomUUID().toString(), latId, 12, 30f, setIndex = 0)
        addSet(UUID.randomUUID().toString(), latId, 10, 32.5f, setIndex = 1)
        addSet(UUID.randomUUID().toString(), latId, 8, 35f, setIndex = 2)
    }

    suspend fun saveAndSyncWorkout(
        workout: WorkoutEntity,
        exercises: List<WorkoutExerciseEntity>,
        sets: List<SetEntity>,
    ) {
        try {
            val batch = firestore.batch()
            val workoutRef = fs.templateRef(workout.workoutId)
            batch.set(workoutRef, workout)

            for (exercise in exercises) {
                val exerciseRef =
                    workoutRef.collection(FirestorePaths.EXERCISES).document(exercise.exerciseId)
                batch.set(exerciseRef, exercise)

                val relatedSets =
                    sets
                        .filter { it.exerciseOwnerId == exercise.exerciseId }
                        .sortedBy { it.setIndex }
                for (set in relatedSets) {
                    val setRef = exerciseRef.collection(FirestorePaths.SETS).document(set.setId)
                    batch.set(setRef, set)
                }
            }

            batch.commit().await()
        } catch (e: Exception) {
            Log.e("Sync", "Yükleme hatası ${e.message}")
        }
    }

    suspend fun syncMyWorkouts(userId: String) {
        if (userId.isBlank()) return
        try {
            val snap =
                firestore
                    .collection(FirestorePaths.TEMPLATES)
                    .whereEqualTo("ownerUid", userId)
                    .get()
                    .await()

            val remoteWorkouts = snap.toObjects(WorkoutEntity::class.java)
            if (remoteWorkouts.isEmpty()) return

            dao.insertAllWorkouts(remoteWorkouts)

            for (workout in remoteWorkouts) {
                val exerciseSnap =
                    fs
                        .templateRef(workout.workoutId)
                        .collection(FirestorePaths.EXERCISES)
                        .get()
                        .await()

                val remoteExercises =
                    exerciseSnap.documents.map { doc ->
                        val ownerIdFromFirebase =
                            doc.getString("workoutId")
                                ?: doc.getString("workoutOwnerId")
                                ?: ""

                        WorkoutExerciseEntity(
                            exerciseId = doc.id,
                            workoutOwnerId = ownerIdFromFirebase,
                            exerciseName = doc.getString("exerciseName") ?: "",
                            catalogExerciseId = doc.getString("catalogExerciseId"),
                            orderIndex = doc.getLong("orderIndex")?.toInt() ?: 0,
                        )
                    }

                for (ex in remoteExercises) {
                    val finalExercise =
                        if (ex.workoutOwnerId.isBlank()) {
                            ex.copy(workoutOwnerId = workout.workoutId)
                        } else {
                            ex
                        }

                    dao.insertExercise(finalExercise)
                    val setsSnap =
                        fs.loadSetDocuments(
                            fs
                                .templateRef(workout.workoutId)
                                .collection(FirestorePaths.EXERCISES)
                                .document(finalExercise.exerciseId),
                        )

                    val remoteSets =
                        setsSnap.documents.map { setDoc ->
                            SetEntity(
                                setId = setDoc.id,
                                exerciseOwnerId = finalExercise.exerciseId,
                                reps = setDoc.getLong("reps")?.toInt() ?: 0,
                                weight = setDoc.getDouble("weight")?.toFloat() ?: 0f,
                                note = setDoc.getString("note"),
                                setIndex = setDoc.getLong("setIndex")?.toInt() ?: 0,
                            )
                        }

                    for (set in remoteSets) {
                        dao.insertSet(set)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("SyncWorkouts", "Firebase hatası: ${e.message}")
        }
    }

    suspend fun updateExerciseOrder(exerciseId: String, workoutId: String, orderIndex: Int) {
        dao.updateExerciseOrder(exerciseId, orderIndex)

        try {
            fs
                .templateRef(workoutId)
                .collection(FirestorePaths.EXERCISES)
                .document(exerciseId)
                .update("orderIndex", orderIndex)
                .await()
        } catch (e: Exception) {
            Log.e("Error", e.message.toString())
        }
    }

    suspend fun getMaxOfExerciseSet(exerciseId: String): Int? = dao.getMaxOfExerciseSet(exerciseId)
}
