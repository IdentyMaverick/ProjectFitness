package data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class AuthRepository(private val auth: FirebaseAuth = FirebaseAuth.getInstance()) {

    val currentUser get() = auth.currentUser

    val currentUid: String?
        get() = auth.currentUser?.uid?.takeIf { it.isNotBlank() }

    suspend fun register(email: String, password: String): String {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        return result.user?.uid ?: error("UID is null")
    }

    suspend fun login(email: String, password: String): String {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return result.user?.uid ?: error("UID is null")
    }

    suspend fun loginWithGoogle(idToken: String): String {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        return result.user?.uid ?: error("UID is null")
    }

    fun logout() = auth.signOut()

    /** Removes the currently signed-in Firebase Auth user (e.g. rollback after failed profile write). */
    suspend fun deleteCurrentUser() {
        val user = auth.currentUser ?: error("No signed-in user to delete")
        user.delete().await()
    }

    suspend fun sendPasswordReset(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }
}
