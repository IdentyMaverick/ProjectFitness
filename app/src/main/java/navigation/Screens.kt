package navigation

sealed class Screens(val route: String) {
    object FirstInfoScreen : Screens("fis")
    object SecondInfoScreen : Screens("sis")
    object ThirdInfoScreen : Screens("tis")
    object LoginScreen : Screens("login"){
        object ForgetPasswordScreen : Screens("fps")
        object RegisterScreen : Screens("Register")
    }
    object Home : Screens("home"){
        object Profile : Screens("profile"){
            object ProfileEdit : Screens("profileedit")
        }
    }
    object Activity : Screens("activity")
    object ChooseExercises : Screens(route = "chooseexercises/{name}")
    object CreateWorkout : Screens(route = "createworkout")
    object LeaderBoard : Screens("leaderboard")
    object WorkoutSettingScreen : Screens(route = "workoutsettingscreen")
    object WorkoutDetails : Screens(route = "workoutdetails" )
    object WorkoutSettingScreenWorkoutDetails : Screens(route = "workoutsettingscreenworkoutdetails")
    object WorkoutLog : Screens(route = "workoutlog")
    object Meal : Screens(route = "meal")
}
