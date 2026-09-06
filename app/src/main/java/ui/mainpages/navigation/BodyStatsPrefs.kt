package ui.mainpages.navigation

import android.content.Context
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

object BodyStatsPrefs {
    private const val PREFS = "app_prefs"

    private fun key(uid: String) = "body_stats_prompted_$uid"

    fun isPrompted(context: Context, uid: String): Boolean {
        if (uid.isBlank()) return true
        return context
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(key(uid), false)
    }

    fun markPrompted(context: Context, uid: String) {
        if (uid.isBlank()) return
        context
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(key(uid), true)
            .apply()
    }
}

fun NavController.navigateAfterAuth(context: Context) {
    val uid =
        FirebaseAuth
            .getInstance()
            .currentUser
            ?.uid
            .orEmpty()
    val route =
        if (!BodyStatsPrefs.isPrompted(context, uid)) {
            Screens.CompleteAthleteScreen.route
        } else {
            Screens.Home.route
        }
    navigate(route) {
        popUpTo(Screens.LoginScreen.route) { inclusive = true }
        launchSingleTop = true
    }
}
