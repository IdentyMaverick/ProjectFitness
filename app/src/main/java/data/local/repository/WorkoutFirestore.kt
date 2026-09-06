package com.grozzbear.projectfitness.data.local.repository

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.WriteBatch
import data.remote.FirestorePaths
import kotlinx.coroutines.tasks.await

/**
 * Shared Firestore document paths for templates vs sessions.
 *
 * Collection IDs are historical and must not be renamed (existing user data).
 * Templates live at root [FirestorePaths.TEMPLATES]; sessions live under
 * [FirestorePaths.USERS]/{uid}/[FirestorePaths.HISTORY].
 */
internal class WorkoutFirestore(private val firestore: FirebaseFirestore) {
    fun templateRef(workoutId: String): DocumentReference =
        firestore.collection(FirestorePaths.TEMPLATES).document(workoutId)

    fun historyRef(userId: String, sessionId: String): DocumentReference = firestore
        .collection(FirestorePaths.USERS)
        .document(userId)
        .collection(FirestorePaths.HISTORY)
        .document(sessionId)

    fun historyCol(userId: String) = firestore
        .collection(FirestorePaths.USERS)
        .document(userId)
        .collection(FirestorePaths.HISTORY)

    suspend fun loadSetDocuments(exerciseRef: DocumentReference): QuerySnapshot {
        val current = exerciseRef.collection(FirestorePaths.SETS).get().await()
        if (!current.isEmpty) return current
        return exerciseRef.collection(FirestorePaths.SETS_LEGACY).get().await()
    }

    suspend fun deleteSetDocuments(exerciseRef: DocumentReference, batch: WriteBatch) {
        val current = exerciseRef.collection(FirestorePaths.SETS).get().await()
        for (doc in current) batch.delete(doc.reference)
        val legacy = exerciseRef.collection(FirestorePaths.SETS_LEGACY).get().await()
        for (doc in legacy) batch.delete(doc.reference)
    }

    suspend fun resolveSetDocument(exerciseRef: DocumentReference, setId: String): DocumentReference {
        val currentCol = exerciseRef.collection(FirestorePaths.SETS)
        val current = currentCol.get().await()
        if (!current.isEmpty) return currentCol.document(setId)
        val legacyCol = exerciseRef.collection(FirestorePaths.SETS_LEGACY)
        val legacy = legacyCol.get().await()
        if (!legacy.isEmpty) return legacyCol.document(setId)
        return currentCol.document(setId)
    }
}
