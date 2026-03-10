package data.remote

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

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

    fun getFollowers(nickname: String): LiveData<List<String>> {
        val followersLiveData = MutableLiveData<List<String>>()
        firestore.collection("followers")
            .whereEqualTo("followingId", nickname)
            .addSnapshotListener { snapshots, e ->
                if (e != null || snapshots == null) {
                    return@addSnapshotListener
                }
                val followers = snapshots.documents.map { it.getString("followerId")!! }
                followersLiveData.value = followers
            }
        return followersLiveData
    }

    fun getFollowing(nickname: String): LiveData<List<String>> {
        val followingLiveData = MutableLiveData<List<String>>()
        firestore.collection("followers")
            .whereEqualTo("followerId", nickname)
            .addSnapshotListener { snapshots, e ->
                if (e != null || snapshots == null) {
                    return@addSnapshotListener
                }
                val following = snapshots.documents.map { it.getString("followingId")!! }
                followingLiveData.value = following
            }
        return followingLiveData
    }

    @SuppressLint("RestrictedApi")
    suspend fun getUserByNickname(nickname: String): User? {
        return try {
            val querySnapshot = firestore.collection("googlecloudusers")
                .whereEqualTo("nickname", nickname)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val documentSnapshot = querySnapshot.documents.first()
                val user = documentSnapshot.toObject(User::class.java)
                user?.id = documentSnapshot.id
                user
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    @SuppressLint("RestrictedApi")
    suspend fun getUserById(id: String): User? {
        return try {
            val querySnapshot = firestore.collection("googlecloudusers")
                .whereEqualTo("id", id)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val documentSnapshot = querySnapshot.documents.first()
                val user = documentSnapshot.toObject(User::class.java)
                user
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }


    @SuppressLint("RestrictedApi")
    fun getAllUsers(): LiveData<List<User>> {
        val usersLiveData = MutableLiveData<List<User>>()
        firestore.collection("googlecloudusers").addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                return@addSnapshotListener
            }
            val users = mutableListOf<User>()
            snapshot?.documents?.forEach { document ->
                val user = document.toObject(User::class.java)
                user?.let {
                    users.add(it)
                }
            }
            usersLiveData.value = users
        }
        return usersLiveData
    }


}
