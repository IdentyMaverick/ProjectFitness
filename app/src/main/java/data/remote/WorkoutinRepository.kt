package data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.grozzbear.projectfitness.data.remote.Workoutin
import kotlinx.coroutines.tasks.await

class WorkoutinRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun getExercises(limit: Long = 100): List<Workoutin> {
        val snap = firestore.collection("googlecloud")
            .whereEqualTo("isActive", true)
            .limit(limit)
            .get()
            .await()

        return snap.documents.mapNotNull { it.toObject((Workoutin::class.java)) }
    }

    suspend fun getExercisesByBodyPart(bodyPart: List<String>, limit: Long = 200): List<Workoutin> {
        val snap = firestore.collection("googlecloud")
            .whereEqualTo("isActive", true)
            .whereIn("bodyPart", bodyPart)
            .limit(limit)
            .get()
            .await()

        return snap.documents.mapNotNull { it.toObject((Workoutin::class.java)) }
    }
}