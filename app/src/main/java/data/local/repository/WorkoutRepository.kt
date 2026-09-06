package com.grozzbear.projectfitness.data.local.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.grozzbear.projectfitness.data.local.dao.ExerciseCatalogDao
import com.grozzbear.projectfitness.data.local.dao.WorkoutDao
import data.remote.FirestorePaths
import data.remote.LeaderboardEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

/**
 * Single entry point for workout data.
 *
 * Domain split (Room tables stay separate; do not merge them):
 * - [templates] planned workouts (`workout` / `exercise` / `exercise_set`)
 * - [sessions] logged sessions (`workout_history` / `exercise_logs` / `set_logs`)
 * - [catalog] exercise catalog (`exercise_catalog`)
 */
class WorkoutRepository(
    dao: WorkoutDao,
    catalogdao: ExerciseCatalogDao,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val fs = WorkoutFirestore(firestore)

    val templates = TemplateWorkoutStore(dao, catalogdao, firestore, fs)
    val sessions = SessionWorkoutStore(dao, firestore, fs)
    val catalog = CatalogStore(dao, catalogdao, firestore)

    suspend fun updateLeaderboard(entry: LeaderboardEntry): Boolean {
        val docId = "${entry.userId}_${entry.exerciseName}"
        return try {
            val document = firestore.collection(FirestorePaths.LEADERBOARD).document(docId).get().await()
            if (document.exists()) {
                val oldWeight = document.getDouble("weight") ?: 0.0
                if (entry.weight > oldWeight) {
                    firestore.collection(FirestorePaths.LEADERBOARD).document(docId).set(entry).await()
                    true
                } else {
                    false
                }
            } else {
                firestore.collection(FirestorePaths.LEADERBOARD).document(docId).set(entry).await()
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
                val snapshot = firestore.collection(FirestorePaths.LEADERBOARD)
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
}
