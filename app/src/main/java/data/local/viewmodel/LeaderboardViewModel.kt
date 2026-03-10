package data.local.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.grozzbear.projectfitness.data.local.repository.WorkoutRepository
import data.remote.LeaderboardEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import viewmodel.ProfileViewModel
import java.util.UUID

class LeaderboardViewModel(
    val repository: WorkoutRepository,
    val profileViewModel: ProfileViewModel
) : ViewModel() {
    private val _leaderboardData = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val leaderboardData: StateFlow<List<LeaderboardEntry>> = _leaderboardData

    val currentUserRankInfo: StateFlow<Pair<Int, LeaderboardEntry>?> =
        leaderboardData.map { entries ->
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            val index = entries.indexOfFirst { it.userId == currentUserId }
            entries.forEach {
                it.userPhotoUri = fetchUserImage(it.userId)
            }

            if (index != -1) {
                Pair(index + 1, entries[index])
            } else {
                null
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)


    fun uploadPrProof(uri: android.net.Uri, userId: String, exerciseName: String) {
        viewModelScope.launch {
            val storageRef = FirebaseStorage.getInstance().reference
            val fileName = "proofs/${userId}_${exerciseName}_${UUID.randomUUID()}.mp4"
            val proofRef = storageRef.child(fileName)

            try {
                proofRef.putFile(uri).await()
                val downloadUrl = proofRef.downloadUrl.await().toString()

                updateFirestoreProof(userId, exerciseName, downloadUrl)
            } catch (e: Exception) {
                Log.d("error", e.toString())
            }
        }
    }

    private suspend fun updateFirestoreProof(userId: String, exercise: String, url: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection("googlecloudleaderboard")
            .document("${userId}_${exercise}")
            .update(
                mapOf(
                    "proofUrl" to url,
                    "verificationStatus" to "pendent"
                )
            ).await()
    }

    fun fetchLeaderboard(exerciseName: String) {
        viewModelScope.launch {
            repository.getLeaderboard(exerciseName).collect { entries ->
                val updatedEntries = entries.map { entry ->
                    val photo = fetchUserImage(entry.userId)
                    entry.copy(userPhotoUri = photo)
                }
                _leaderboardData.value = updatedEntries
            }
        }
    }

    suspend fun fetchUserImage(userId: String): String {
        return try {
            val db = FirebaseFirestore.getInstance()
            val document = db.collection("googlecloudusers").document(userId).get().await()
            document.getString("userPhotoUri") ?: ""
        } catch (e: Exception) {
            Log.e("LeaderboardVM", "Fotoğraf çekilemedi: ${e.message}")
            ""
        }
    }
}