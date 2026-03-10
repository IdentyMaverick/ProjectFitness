package data.remote

import androidx.annotation.Keep

@Keep
data class LeaderboardEntry(
    val userId: String = "",
    val exerciseName: String = "",
    val weight: Double = 0.0,
    val userName: String = "Anonymous",
    var userPhotoUri: String? = null,
    val hasPro: Boolean = false,
    val verificationStatus: String = "notVerified"
)