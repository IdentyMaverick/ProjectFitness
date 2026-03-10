package data.remote

object FirebaseStorageHelper {

    private const val BUCKET_NAME = "projectfitness-ddfeb.appspot.com"
    private const val BASE_URL = "https://firebasestorage.googleapis.com/v0/b/$BUCKET_NAME/o/"

    fun getImageUrl(fileName: String): String {
        if (fileName.isNullOrEmpty()) return ""

        val folder = "cloudgoogle%2F"
        return "$BASE_URL$folder$fileName?alt=media"
    }
}