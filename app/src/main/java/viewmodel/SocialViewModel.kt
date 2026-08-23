package viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.Keep
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.Query
import data.remote.FirestorePaths
import data.remote.FirestoreRepository
import data.remote.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

@Keep
data class NotificationModel(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    // Firestore'a "isRead" alanını gördüğünde bu değişkene yazmasını zorla söylüyoruz
    @get:PropertyName("isRead")
    @set:PropertyName("isRead")
    var isRead: Boolean = false,
    val time: Long = System.currentTimeMillis()
)

class SocialViewModel(private val repository: FirestoreRepository) : ViewModel() {
    // Mevcut kullanıcının nickname'ini tutar
    val _nickname = MutableStateFlow("")
    val nickname: StateFlow<String> = _nickname

    fun setNickname(newNickname: String) {
        _nickname.value = newNickname
    }

    fun followUser(followerNickname: String, followingNickname: String) {
        repository.followUser(followerNickname, followingNickname)

        val db = FirebaseFirestore.getInstance()
        db.collection("googlecloudusers")
            .whereEqualTo("nickname", followingNickname)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val userId = querySnapshot.documents.firstOrNull()?.id
                if (userId != null) {
                    val notification = hashMapOf(
                        "title" to "Follow",
                        "message" to "$followerNickname has followed you.",
                        "isRead" to false,
                        "time" to System.currentTimeMillis()
                    )

                    db.collection("googlecloudusers")
                        .document(userId)
                        .collection("notifications")
                        .add(notification)
                }
            }
    }

    fun unfollowUser(followerNickname: String, followingNickname: String) {
        repository.unfollowUser(followerNickname, followingNickname)
    }

    fun markAllAsRead(nickname: String) {
        if (nickname.isEmpty()) return

        viewModelScope.launch {
            val db = FirebaseFirestore.getInstance()
            db.collection("googlecloudusers")
                .whereEqualTo("nickname", nickname)
                .get()
                .addOnSuccessListener { snapshots ->
                    val userId = snapshots?.documents?.firstOrNull()?.id
                    if (userId != null) {
                        val notificationsRef = db.collection("googlecloudusers")
                            .document(userId)
                            .collection("notifications")

                        // Tüm bildirimleri çek ve kod içinde filtrele
                        notificationsRef.get().addOnSuccessListener { allDocs ->
                            for (doc in allDocs) {
                                val isRead = doc.getBoolean("isRead") ?: false
                                if (!isRead) { // Eğer false ise true yap
                                    doc.reference.update("isRead", true)
                                        .addOnSuccessListener {
                                            Log.d(
                                                "Fix",
                                                "Okundu yapıldı: ${doc.id}"
                                            )
                                        }
                                }
                            }
                        }
                    }
                }
        }
    }

    fun getFollowers(nickname: String): Flow<List<String>> {
        return repository.observeFollowers(nickname)
    }

    fun getFollowing(nickname: String): Flow<List<String>> {
        return repository.observeFollowing(nickname)
    }

    @SuppressLint("RestrictedApi")
    fun getUserByNicknameLive(nickname: String): LiveData<User?> {
        val userLiveData = MutableLiveData<User?>()
        viewModelScope.launch {
            val user = repository.getUserByNickname(nickname)
            userLiveData.postValue(user)
        }
        return userLiveData
    }

    fun getAllUsers(): Flow<List<User>> {
        return repository.observeAllUsers()
    }

    fun getNotification(nickname: String): Flow<List<NotificationModel>> = callbackFlow {
        if (nickname.isBlank()) {
            trySend(emptyList())
            awaitClose { }
            return@callbackFlow
        }
        val db = FirebaseFirestore.getInstance()
        var notificationsRegistration: ListenerRegistration? = null
        val userRegistration = db.collection(FirestorePaths.USERS)
            .whereEqualTo("nickname", nickname)
            .addSnapshotListener { snapshots, exception ->
                if (exception != null) {
                    Log.e("Firestore", "Kullanıcı sorgusu hatası", exception)
                    return@addSnapshotListener
                }

                notificationsRegistration?.remove()
                notificationsRegistration = null

                val userId = snapshots?.documents?.firstOrNull()?.id
                if (userId == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                notificationsRegistration = db.collection(FirestorePaths.USERS)
                    .document(userId)
                    .collection("notifications")
                    .orderBy("time", Query.Direction.DESCENDING)
                    .addSnapshotListener { notificationSnapshots, notificationException ->
                        if (notificationException != null) {
                            Log.e("Firestore", "Notification Error", notificationException)
                            return@addSnapshotListener
                        }
                        val list = notificationSnapshots?.documents?.mapNotNull { doc ->
                            doc.toObject(NotificationModel::class.java)?.copy(id = doc.id)
                        } ?: emptyList()
                        trySend(list)
                    }
            }
        awaitClose {
            notificationsRegistration?.remove()
            userRegistration.remove()
        }
    }
}