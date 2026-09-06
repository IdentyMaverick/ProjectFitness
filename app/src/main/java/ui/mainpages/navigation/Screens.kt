package ui.mainpages.navigation

import android.net.Uri
import androidx.navigation.NavController

sealed class Screens(val route: String) {
    object WorkoutCompleteAnalysisScreen : Screens("workoutcompleteanalysisscreen")

    object InfoHorizontalScreen : Screens("infohorizontalscreen")

    object LoginScreen : Screens("login") {
        object ForgetPasswordScreen : Screens("fps")

        object RegisterScreen : Screens("Register")
    }

    object Home : Screens("home") {
        object Profile : Screens("profile")
    }

    object Activity : Screens("activity")

    object ChooseExercises : Screens(route = "chooseexercises/{workoutId}") {
        const val ARG_WORKOUT_ID = "workoutId"
        const val MODE_CREATE = "new"
        const val MODE_HISTORY = "history"

        fun createRoute(workoutId: String? = null): String {
            val id = workoutId?.takeIf { it.isNotBlank() } ?: MODE_CREATE
            return "chooseexercises/$id"
        }

        fun isEditMode(workoutId: String?): Boolean =
            !workoutId.isNullOrBlank() && workoutId != MODE_CREATE && workoutId != MODE_HISTORY

        fun isHistoryMode(workoutId: String?): Boolean = workoutId == MODE_HISTORY
    }

    object CreateWorkout : Screens(route = "createworkout")

    object LeaderBoard : Screens("leaderboard")

    object WorkoutSettingScreen : Screens(route = "workoutsettingscreen") {
        const val ROUTE_WITH_ARG = "workoutsettingscreen/{workoutId}"
    }

    object WorkoutLog : Screens(route = "workoutlog") {
        const val ROUTE_WITH_ARG = "workoutlog/{workoutId}"
    }

    object Meal : Screens(route = "meal")

    object WorkoutCompleteScreen : Screens(route = "workoutcompletescreen")

    object ProjectFollowersScreen : Screens(route = "projectfollowersscreen/{nickname}") {
        fun createRoute(nickname: String) = "projectfollowersscreen/${Uri.encode(nickname)}"
    }

    object ProjectFollowScreen : Screens(route = "projectfollowscreen/{nickname}") {
        fun createRoute(nickname: String) = "projectfollowscreen/${Uri.encode(nickname)}"
    }

    object FindUsersScreen : Screens(route = "findusersscreen")

    object OtherScreenProfile : Screens(route = "otherscreenprofile/{nickname}") {
        fun createRoute(nickname: String) = "otherscreenprofile/${Uri.encode(nickname)}"
    }

    object HomesSettings : Screens(route = "settings")

    object AllWorkouts : Screens(route = "allworkouts/{filter}") {
        const val ARG_FILTER = "filter"
        const val FILTER_ALL = "all"
        const val FILTER_COACH = "coach"
        const val FILTER_CHALLENGE = "challenge"

        fun createRoute(filter: String = FILTER_ALL) = "allworkouts/$filter"
    }

    object FaqcontactfeedbackScreen : Screens(route = "faqcontactfeedbackscreen")

    object PersonalInformationsScreen : Screens(route = "personalinformationsscreen")

    object CompleteAthleteScreen : Screens(route = "completeathletescreen")

    object OldWorkoutDetails : Screens(route = "oldworkoutdetails")

    object NotificationScreen : Screens(route = "notification")

    object ActivityInside : Screens(route = "activityinside")
}

fun NavController.navigateToLoginAfterLogout() {
    navigate(Screens.LoginScreen.route) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }
}
