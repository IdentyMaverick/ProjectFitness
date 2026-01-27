package data.remote

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository (
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun createUserProfile(uid: String, profile: UserProfile) {
        firestore.collection("users")
            .document(uid)
            .set(profile)
            .await()
    }
    suspend fun getUserProfile(uid: String): UserProfile? {
        val doc = firestore.collection("users").document(uid).get().await()
        return doc.toObject(UserProfile::class.java)
    }
    suspend fun setUserOnline(uid: String, isOnline: Boolean) {
        firestore.collection("users")
            .document(uid)
            .update("isOnline", isOnline)
            .await()
    }
    suspend fun updatePhotoUrl(uid: String, url: String) {
        firestore.collection("users").document(uid).update("userPhotoUri", url).await()
    }
}