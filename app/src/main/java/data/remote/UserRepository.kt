package data.remote

import android.util.Log
import androidx.annotation.Keep
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun createUserProfile(uid: String, profile: UserProfile) {
        firestore.collection("googlecloudusers")
            .document(uid)
            .set(profile)
            .await()
    }

    suspend fun getUserProfile(uid: String): UserProfile? {
        val doc = firestore.collection("googlecloudusers").document(uid).get().await()
        return doc.toObject(UserProfile::class.java)
    }

    suspend fun setUserOnline(uid: String, isOnline: Boolean) {
        firestore.collection("googlecloudusers")
            .document(uid)
            .update("isOnline", isOnline)
            .await()
    }

    suspend fun updatePhotoUrl(uid: String, url: String) {
        firestore.collection("googlecloudusers").document(uid).update("userPhotoUri", url).await()
    }

    suspend fun updateUserInformation(
        uid: String,
        first: String,
        gender: Boolean,
        birthDate: String,
        height: String,
        weight: String
    ) {
        firestore.collection("googlecloudusers").document(uid)
            .update(
                "first",
                first,
                "gender",
                gender,
                "birthDate",
                birthDate,
                "height",
                height,
                "weight",
                weight
            )
            .await()
    }

    fun updateUserIdea(selectedRating: String) {
        val db = FirebaseFirestore.getInstance()
        val currentUser = Firebase.auth.currentUser
        if (currentUser != null) {
            val updateUserIdea = UpdateUserIdea(
                userId = currentUser.uid,
                rating = selectedRating,
                userEmail = currentUser.email ?: ""
            )

            db.collection("googlecloudidea")
                .add(updateUserIdea)
                .addOnSuccessListener {
                    Log.d("Idea", "Idea added successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("Idea", "Error adding idea", e)
                }
        }
    }
}

@Keep
data class UpdateUserIdea(
    val userId: String = "",
    val rating: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val userEmail: String = ""
)