package com.example.projectfitness.data.local.repository

import android.util.Log
import com.example.projectfitness.data.local.dao.ExerciseCatalogDao
import com.example.projectfitness.data.local.dao.WorkoutDao
import com.example.projectfitness.data.local.entity.SetEntity
import com.example.projectfitness.data.local.entity.WorkoutEntity
import com.example.projectfitness.data.local.entity.WorkoutExerciseEntity
import com.example.projectfitness.data.remote.Workoutin
import com.example.projectfitness.data.remote.toEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class WorkoutRepository(
    private val dao: WorkoutDao,
    private val catalogdao: ExerciseCatalogDao,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    fun observeWorkouts() = dao.observeWorkouts()

    fun observeWorkoutFull(id: String) = dao.observeWorkoutFull(id)

    suspend fun createWorkout(
        workoutId: String,
        name: String,
        workoutType: String,
        workoutRating: Int,
        ownerUid: String?,
        syncState: Boolean
    ): String {
        dao.insertWorkout(
            WorkoutEntity(
                workoutId = workoutId,
                workoutName = name,
                workoutType = workoutType,
                workoutRating = workoutRating,
                ownerUid = ownerUid,
                syncState = syncState
            )
        )
        return workoutId
    }

    // ARTIK ID'Yİ DIŞARIDAN ALIYORUZ VE RETURN YOK
    suspend fun addExercise(
        exerciseId: String,
        workoutId: String,
        name: String,
        catalogExerciseId: String
    ) {
        dao.insertExercise(
            WorkoutExerciseEntity(
                exerciseId = exerciseId,
                workoutOwnerId = workoutId,
                exerciseName = name,
                catalogExerciseId = catalogExerciseId
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
        } catch (e: Exception) {Log.e("Exceptionstime", e.message.toString())}

    }
    // Delete Function
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
        }catch (e: Exception) {
            Log.e("DeleteFirebase", "Silme hatası: ${e.message}")
        }
    }

    suspend fun deleteSelectedExercise(exerciseId: String, workoutId: String) {

        // 1. Önce egzersizi sil (Artık doğru ID ile silecek)
        dao.deleteSelectedExercise(exerciseId)

        // 2. 🔥 TETİKLEYİCİ: Bunu eklemeyi unutmuşsun!
        // Bu satır olmazsa liste kendini yenilemez.
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

    // 🔥 GÜNCELLENEN SEED (VARSAYILAN VERİ) FONKSİYONU
    suspend fun seedDefaultsIfEmpty() {
        if (workoutCount() > 0) return

        // 1. Challenge Workout
        val pushId = UUID.randomUUID().toString()
        createWorkout(pushId, "Push Challenge", "challange", 3, null, false)

        // 2. Bench Press Ekle
        val benchId = UUID.randomUUID().toString()
        addExercise(benchId, pushId, "Dumbbell Bench Press", "9u9UAvGkJZPNuINgoaBR")

        // 3. Setleri Ekle
        addSet(UUID.randomUUID().toString(), benchId, 12, 30f)
        addSet(UUID.randomUUID().toString(), benchId, 10, 32.5f)
        addSet(UUID.randomUUID().toString(), benchId, 8, 35f)

        // 4. Diğer Egzersiz (OHP)
        val ohpId = UUID.randomUUID().toString()
        addExercise(ohpId, pushId, "Barbell Overhead Press", "okwXFxp3bLE5GCq65CsT")
        addSet(UUID.randomUUID().toString(), ohpId, 12, 20f)
        addSet(UUID.randomUUID().toString(), ohpId, 10, 22.5f)
        addSet(UUID.randomUUID().toString(), ohpId, 8, 25f)

        // Challenge 2 (Pull)
        val pullId = UUID.randomUUID().toString()
        createWorkout(pullId, "Pull Challenge", "coach", 4, null, false)

        val latId = UUID.randomUUID().toString()
        addExercise(latId, pullId, "Lat Pulldown", "pSyUAfMjZYildFc3s3vi")
        addSet(UUID.randomUUID().toString(), latId, 12, 30f)
        addSet(UUID.randomUUID().toString(), latId, 10, 32.5f)
        addSet(UUID.randomUUID().toString(), latId, 8, 35f)
    }

    suspend fun syncCatalog() {
        try {
            val snap = firestore.collection("googlecloud").whereEqualTo("isActive", true).get().await()
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

    fun getAllCatalogExercises() = catalogdao.observeAllActive()

    fun observeMyWorkouts(uid: String) = dao.observeMyWorkouts(uid)

    suspend fun saveAndSyncWorkout(workout: WorkoutEntity, exercises: List<WorkoutExerciseEntity>, sets: List<SetEntity>) {
        try {
            val batch = firestore.batch()

            val workoutRef = firestore.collection("googlecloudworkouts").document(workout.workoutId)
            batch.set(workoutRef, workout)

            for (exercise in exercises) {
                val exerciseRef = workoutRef.collection("googlecloudexercises").document(exercise.exerciseId)
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
                        // 1. Önce "workoutId" var mı bak, yoksa "workoutOwnerId"ye bak
                        val ownerIdFromFirebase = doc.getString("workoutId")
                            ?: doc.getString("workoutOwnerId")
                            ?: ""

                        WorkoutExerciseEntity(
                            // ID'yi doküman ID'sinden alıyoruz
                            exerciseId = doc.id,

                            // Yakaladığımız Owner ID'yi buraya koyuyoruz
                            workoutOwnerId = ownerIdFromFirebase,

                            exerciseName = doc.getString("exerciseName") ?: "",
                            catalogExerciseId = doc.getString("catalogExerciseId")
                        )
                    }
                    // --- DEĞİŞİKLİK BURADA BİTİYOR ---

                    if (remoteExercises.isNotEmpty()) {
                        for (ex in remoteExercises) {
                            // ID Düzeltme Mantığı (Senin kodun)
                            val finalExercise = if (ex.workoutOwnerId.isBlank()) {
                                ex.copy(workoutOwnerId = workout.workoutId)
                            } else {
                                ex
                            }

                            // Egzersizi Lokale Kaydet
                            dao.insertExercise(finalExercise)
                    val setsSnap = firestore.collection("googlecloudworkouts")
                        .document(workout.workoutId) // Hangi Workout
                        .collection("googlecloudexercises")
                        .document(finalExercise.exerciseId) // Hangi Exercise
                        .collection("sets") // Altındaki "sets" koleksiyonu
                        .get()
                        .await()

                            val remoteSets = setsSnap.documents.map { setDoc ->
                                SetEntity(
                                    setId = setDoc.id,
                                    exerciseOwnerId = finalExercise.exerciseId, // Egzersize bağlıyoruz
                                    reps = setDoc.getLong("reps")?.toInt() ?: 0,
                                    weight = setDoc.getDouble("weight")?.toFloat() ?: 0f,
                                    note = setDoc.getString("note")
                                )
                            }

                            // Setleri Lokale Kaydet
                            for (set in remoteSets) {
                                dao.insertSet(set)
                            }
                            Log.d("SyncDebug", "${finalExercise.exerciseName} için ${remoteSets.size} set indirildi.")
                }
            }}}
        } catch (e: Exception) {
            Log.e("SyncWorkouts", "Firebase hatası: ${e.message}")
        }
    }

    fun observeMySelectedWorkout(id: String) = dao.observeMySelectedWorkout(id)
}