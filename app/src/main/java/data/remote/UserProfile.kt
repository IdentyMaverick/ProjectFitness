package data.remote

data class UserProfile(
    val first: String = "",
    val nickname: String = "",
    val email: String = "",
    val userPhotoUri: String = "",
    var isOnline: Boolean = false
)
