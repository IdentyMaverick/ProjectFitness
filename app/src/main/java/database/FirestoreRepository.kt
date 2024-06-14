package database

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class User(
    val id: String = "",
    val first: String = "",
    val email: String = "",
    val nickname: String = "",
    val password: String = "",
    val isOnline: Boolean = false,
    val userPhotoUri : String = ""
) {
    constructor() : this("", "", "", "", "", false)
}

data class Follow(
    val followerId: String = "", // takipçi id
    val followingId: String = "" // takip edilen id
)

class FirestoreRepository {

    val firestore = FirebaseFirestore.getInstance()
    val collectionReference = FirebaseFirestore.getInstance().collection("users")

    fun getFirestoreUser(){
        collectionReference.get()
            .addOnSuccessListener { querySnapshot ->
                for (documentSnapshot in querySnapshot) {
                    // Her bir belgeyi işleyin
                    // documentSnapshot nesnesi, koleksiyondaki her bir belgeyi temsil eder
                    // Belgeden verileri alabilir ve işleyebilirsiniz
                    val userId = documentSnapshot.id
                    val user = documentSnapshot.toObject(database.User::class.java)

                    // Örneğin, kullanıcı adını yazdırma
                    println("User ID: $userId, Name: ${user?.first}")
                }
            }
            .addOnFailureListener { exception ->
                // Belge alınırken bir hata oluştuğunda burası çalışır
                // Hata mesajını exception.message ile alabilirsiniz
                println("Error getting documents: ${exception.message}")
            }
    }


    fun followUser(followerId: String, followingId: String) {
        val follow = Follow(followerId, followingId)
        firestore.collection("followers").add(follow)
    }

    fun unfollowUser(followerId: String, followingId: String) {
        firestore.collection("followers")
            .whereEqualTo("followerId", followerId)
            .whereEqualTo("followingId", followingId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    firestore.collection("followers").document(document.id).delete()
                }
            }
    }

    fun getFollowers(userId: String): LiveData<List<String>> {
        val followersLiveData = MutableLiveData<List<String>>()
        firestore.collection("followers")
            .whereEqualTo("followingId", userId)
            .addSnapshotListener { snapshots, e ->
                if (e != null || snapshots == null) {
                    return@addSnapshotListener
                }
                val followers = snapshots.documents.map { it.getString("followerId")!! }
                followersLiveData.value = followers
            }
        return followersLiveData
    }

    fun getFollowing(userId: String): LiveData<List<String>> {
        val followingLiveData = MutableLiveData<List<String>>()
        firestore.collection("followers")
            .whereEqualTo("followerId", userId)
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
    suspend fun getUserByNickname(nickname: String): database.User? {
        return try {
            val querySnapshot = firestore.collection("users")
                .whereEqualTo("nickname", nickname)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val documentSnapshot = querySnapshot.documents.first()
                val user = documentSnapshot.toObject(database.User::class.java)
                user
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    @SuppressLint("RestrictedApi")
    suspend fun getUserById(id: String): database.User? {
        return try {
            val querySnapshot = firestore.collection("users")
                .whereEqualTo("id", id)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val documentSnapshot = querySnapshot.documents.first()
                val user = documentSnapshot.toObject(database.User::class.java)
                user
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }


    @SuppressLint("RestrictedApi")
    fun getAllUsers(): LiveData<List<database.User>> {
        val usersLiveData = MutableLiveData<List<database.User>>()
        firestore.collection("users").addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                // Hata yönetimi
                return@addSnapshotListener
            }
            val users = mutableListOf<database.User>()
            snapshot?.documents?.forEach { document ->
                val user = document.toObject(database.User::class.java)
                user?.let {
                    users.add(it)
                }
            }
            usersLiveData.value = users
        }
        return usersLiveData
    }



}
