package data.local.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.grozzbear.projectfitness.data.local.repository.WorkoutRepository
import data.remote.FirestorePaths
import data.remote.LeaderboardEntry
import java.util.UUID
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import viewmodel.ProfileViewModel

class LeaderboardViewModel(val repository: WorkoutRepository, requireNotNull: ProfileViewModel) : ViewModel() {
    private val _leaderboardData = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val leaderboardData: StateFlow<List<LeaderboardEntry>> = _leaderboardData

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var fetchJob: Job? = null

    val currentUserRankInfo: StateFlow<Pair<Int, LeaderboardEntry>?> =
        leaderboardData
            .map { entries ->
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

    fun uploadPrProof(uri: android.net.Uri, userId: String, exerciseName: String, weight: Double, nickname: String) {
        if (userId.isBlank()) return
        viewModelScope.launch {
            val storageRef = FirebaseStorage.getInstance().reference
            val fileName = "proofs/${userId}_${exerciseName}_${UUID.randomUUID()}.mp4"
            val proofRef = storageRef.child(fileName)

            try {
                proofRef.putFile(uri).await()
                val downloadUrl = proofRef.downloadUrl.await().toString()
                updateFirestoreProof(userId, exerciseName, downloadUrl, weight, nickname)
            } catch (e: Exception) {
                Log.e("Leaderboard", "Proof upload failed", e)
            }
        }
    }

    private suspend fun updateFirestoreProof(
        userId: String,
        exercise: String,
        url: String,
        weight: Double,
        nickname: String,
    ) {
        val db = FirebaseFirestore.getInstance()

        db
            .collection(FirestorePaths.LEADERBOARD)
            .document("${userId}_$exercise")
            .set(
                mapOf(
                    "userId" to userId,
                    "userName" to nickname,
                    "exerciseName" to exercise,
                    "proofUrl" to url,
                    "verificationStatus" to "pendent",
                    "weight" to weight,
                ),
                SetOptions.merge(),
            ).await()
    }

    fun fetchLeaderboard(exerciseName: String) {
        fetchJob?.cancel()
        fetchJob =
            viewModelScope.launch {
                _isLoading.value = true
                _leaderboardData.value = emptyList()
                try {
                    repository.getLeaderboard(exerciseName).collect { entries ->
                        val updatedEntries =
                            entries.map { entry ->
                                val photo = fetchUserImage(entry.userId)
                                entry.copy(userPhotoUri = photo)
                            }
                        _leaderboardData.value = updatedEntries
                        _isLoading.value = false
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.e("LeaderboardVM", "Leaderboard fetch failed: ${e.message}")
                    _isLoading.value = false
                }
            }
    }

    suspend fun fetchUserImage(userId: String): String = try {
        val db = FirebaseFirestore.getInstance()
        val document =
            db
                .collection(FirestorePaths.USERS)
                .document(userId)
                .get()
                .await()
        document.getString("userPhotoUri") ?: ""
    } catch (e: Exception) {
        Log.e("LeaderboardVM", "Fotoğraf çekilemedi: ${e.message}")
        ""
    }
}
