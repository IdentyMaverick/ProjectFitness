package data.remote

import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.Keep
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

@Keep
data class User(
    var id: String = "",
    val first: String = "",
    val email: String = "",
    val nickname: String = "",
    val password: String = "",
    val isOnline: Boolean = false,
    val userPhotoUri: String = ""
) {
    constructor() : this("", "", "", "", "", false)
}

@Keep
data class Follow(
    val followerId: String = "",
    val followingId: String = ""
)

class FirestoreRepository {

    val firestore = FirebaseFirestore.getInstance()
    val collectionReference = FirebaseFirestore.getInstance().collection("users")

    fun getFirestoreUser() {
        collectionReference.get()
            .addOnSuccessListener { querySnapshot ->
                for (documentSnapshot in querySnapshot) {
                    val userId = documentSnapshot.id
                    val user = documentSnapshot.toObject(User::class.java)

                    println("User ID: $userId, Name: ${user?.first}")
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: ${exception.message}")
            }
    }

    fun followUser(followerNickname: String, followingNickname: String) {
        val follow = Follow(followerNickname, followingNickname)
        firestore.collection("followers").add(follow)
    }

    fun unfollowUser(followerNickname: String, followingNickname: String) {
        firestore.collection("followers")
            .whereEqualTo("followerId", followerNickname)
            .whereEqualTo("followingId", followingNickname)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    firestore.collection("followers").document(document.id).delete()
                }
            }
    }

    fun observeFollowers(nickname: String): Flow<List<String>> = callbackFlow {
        if (nickname.isBlank()) {
            trySend(emptyList())
            awaitClose { }
            return@callbackFlow
        }
        val registration = firestore.collection("followers")
            .whereEqualTo("followingId", nickname)
            .addSnapshotListener { snapshots, e ->
                if (e != null || snapshots == null) {
                    if (e != null) Log.e("Firestore", "Followers listener error", e)
                    return@addSnapshotListener
                }
                trySend(snapshots.documents.mapNotNull { it.getString("followerId") })
            }
        awaitClose { registration.remove() }
    }

    fun observeFollowing(nickname: String): Flow<List<String>> = callbackFlow {
        if (nickname.isBlank()) {
            trySend(emptyList())
            awaitClose { }
            return@callbackFlow
        }
        val registration = firestore.collection("followers")
            .whereEqualTo("followerId", nickname)
            .addSnapshotListener { snapshots, e ->
                if (e != null || snapshots == null) {
                    if (e != null) Log.e("Firestore", "Following listener error", e)
                    return@addSnapshotListener
                }
                trySend(snapshots.documents.mapNotNull { it.getString("followingId") })
            }
        awaitClose { registration.remove() }
    }

    @SuppressLint("RestrictedApi")
    suspend fun getUserByNickname(nickname: String): User? {
        return try {
            val querySnapshot = firestore.collection(FirestorePaths.USERS)
                .whereEqualTo("nickname", nickname)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val documentSnapshot = querySnapshot.documents.first()
                documentSnapshot.toObject(User::class.java)?.apply {
                    id = documentSnapshot.id
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    @SuppressLint("RestrictedApi")
    suspend fun getUserById(id: String): User? {
        if (id.isBlank()) return null
        return try {
            val documentSnapshot = firestore.collection(FirestorePaths.USERS)
                .document(id)
                .get()
                .await()
            if (!documentSnapshot.exists()) return null
            documentSnapshot.toObject(User::class.java)?.apply {
                this.id = documentSnapshot.id
            }
        } catch (e: Exception) {
            Log.e("Firestore", "getUserById failed", e)
            null
        }
    }

    fun observeAllUsers(): Flow<List<User>> = callbackFlow {
        val registration = firestore.collection("googlecloudusers")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("Firestore", "Users listener error", exception)
                    return@addSnapshotListener
                }
                val users = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(User::class.java)?.apply { id = document.id }
                } ?: emptyList()
                trySend(users)
            }
        awaitClose { registration.remove() }
    }
}
