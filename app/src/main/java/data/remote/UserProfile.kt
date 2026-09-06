package data.remote

import androidx.annotation.Keep

@Keep
data class UserProfile(
    val first: String = "",
    val nickname: String = "",
    val email: String = "",
    val userPhotoUri: String = "",
    var isOnline: Boolean = false,
    val gender: Boolean = false,
    val birthDate: String = "",
    val height: String = "",
    val weight: String = "",
    val hasPro: Boolean = false
)
