package data.remote

import com.google.firebase.firestore.FirebaseFirestore

class WorkoutRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
}