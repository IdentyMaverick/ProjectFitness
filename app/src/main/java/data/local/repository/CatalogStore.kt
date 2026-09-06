package com.grozzbear.projectfitness.data.local.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.grozzbear.projectfitness.data.local.dao.ExerciseCatalogDao
import com.grozzbear.projectfitness.data.local.dao.WorkoutDao
import com.grozzbear.projectfitness.data.remote.Workoutin
import com.grozzbear.projectfitness.data.remote.toEntity
import data.remote.FirestorePaths
import kotlinx.coroutines.tasks.await

/**
 * Movement catalog: Room `exercise_catalog`, sourced from Firestore [FirestorePaths.CATALOG].
 * Read-mostly. Does not write workout templates or session logs.
 */
class CatalogStore(
    private val dao: WorkoutDao,
    private val catalogDao: ExerciseCatalogDao,
    private val firestore: FirebaseFirestore
) {
    fun observeAllActive() = catalogDao.observeAllActive()

    suspend fun syncCatalog() {
        try {
            val snap = firestore.collection(FirestorePaths.CATALOG)
                .whereEqualTo("isActive", true)
                .get()
                .await()
            val entities = snap.documents.mapNotNull { doc ->
                val dto = doc.toObject(Workoutin::class.java)
                dto?.toEntity(doc.id)
            }
            if (catalogDao.count() == 0 || catalogDao.count() != entities.size) {
                catalogDao.upsertAll(entities)
            }
        } catch (e: Exception) {
            Log.e("SyncCatalog", "Hata: ${e.message}")
        }
    }

    suspend fun syncExerciseImagesFromCatalog() {
        try {
            val snapshot = firestore.collection(FirestorePaths.CATALOG).get().await()
            for (doc in snapshot) {
                val imageName = doc.getString("exerciseImage") ?: ""
                val name = doc.getString("name") ?: ""
                Log.d("exercise and name", "Hata: $imageName & $name")
                dao.updateExerciseLogImage(name = name, imageUrl = imageName)
            }
        } catch (e: Exception) {
            Log.e("SyncExercises", "Hata: ${e.message}")
        }
    }
}
