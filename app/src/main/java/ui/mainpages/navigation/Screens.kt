package ui.mainpages.navigation

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
    object ChooseExercises : Screens(route = "chooseexercises")
    object CreateWorkout : Screens(route = "createworkout")
    object LeaderBoard : Screens("leaderboard")
    object WorkoutSettingScreen : Screens(route = "workoutsettingscreen") {
        const val routeWithArg = "workoutsettingscreen/{workoutId}"
    }

    object WorkoutLog : Screens(route = "workoutlog") {
        const val routeWithArg = "workoutlog/{workoutId}"
    }

    object Meal : Screens(route = "meal")
    object WorkoutCompleteScreen : Screens(route = "workoutcompletescreen")
    object ProjectFollowersScreen : Screens(route = "projectfollowersscreen")
    object ProjectFollowScreen : Screens(route = "projectfollowscreen")
    object OtherScreenProfile : Screens(route = "otherscreenprofile/{nickname}")
    object HomesSettings : Screens(route = "settings")
    object AllWorkouts : Screens(route = "allworkouts")
    object FaqcontactfeedbackScreen : Screens(route = "faqcontactfeedbackscreen")
    object PersonalInformationsScreen : Screens(route = "personalinformationsscreen")
    object OldWorkoutDetails : Screens(route = "oldworkoutdetails")
    object NotificationScreen : Screens(route = "notification")
    object ActivityInside : Screens(route = "activityinside")
}
