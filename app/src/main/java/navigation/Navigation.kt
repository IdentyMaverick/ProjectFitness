package navigation

import HistoryExerciseScreen
import OtherScreenProfile
import ProjectChallangesScreen
import ProjectCoachScreen
import SocialViewModel
import activity.inside.ChooseExercises
import activity.inside.CreateWorkout
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.projectfitness.R
import com.example.projectfitness.WorkoutDetails
import com.example.projectfitness.WorkoutSettingDetails
import com.example.projectfitness.WorkoutSettingScreenWorkoutDetails
import database.FirestoreRepository
import homes.inside.Profile
import homes.inside.ProfileEdit
import homes.inside.ProjectFollowScreen
import homes.inside.ProjectFollowersScreen
import homes.inside.WorkoutCompleteScreen
import homes.inside.WorkoutLog
import homes.inside.WorkoutSettingScreen
import loginscreens.ForgetPasswordScreen
import loginscreens.Login
import loginscreens.Register
import mainpages.Activity
import mainpages.Home
import mainpages.LeaderBoard
import mainpages.Meal
import openscreen.Info
import openscreen.SecondInfo
import openscreen.ThirdInfo
import viewmodel.ProjectFitnessViewModel
import viewmodel.ViewModelHomes
import viewmodel.ViewModelProfile
import viewmodel.ViewModelSave

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Navigation() {
    val viewModel : ViewModelSave = viewModel() // For holding list of exercise , preserve every class
    val projectFitnessViewModel : ProjectFitnessViewModel = ProjectFitnessViewModel()
    val viewModelProfile : ViewModelProfile = ViewModelProfile()
    val viewModelHomes : ViewModelHomes = ViewModelHomes()
    val firestoreRepository : FirestoreRepository = FirestoreRepository()
    val socialViewModel : SocialViewModel = SocialViewModel(repository = firestoreRepository)
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screens.FirstInfoScreen.route) {
        composable(route = Screens.FirstInfoScreen.route) {
            //DataScreen()
            Info(navController = navController)
        }
        composable(route = Screens.SecondInfoScreen.route) {
            SecondInfo(navController = navController)
        }
        composable(route = Screens.ThirdInfoScreen.route) {
            ThirdInfo(navController = navController)
        }
        composable(route = Screens.LoginScreen.route) {
            Login().LoginScreen(navController = navController , viewModelProfile = viewModelProfile)
        }
        composable(route = Screens.LoginScreen.ForgetPasswordScreen.route) {
            ForgetPasswordScreen(navController = navController)
        }
        composable(route = Screens.LoginScreen.RegisterScreen.route) {
            Register().RegisterScreen(navController = navController , viewModelProfile)
        }
        composable(route = Screens.Home.route) {
            Home(navController = navController,viewModel , projectFitnessViewModel ,viewModelProfile = viewModelProfile)
        }
        composable(route = Screens.Activity.route) {
            Activity(navController = navController , viewModelSave = viewModel)
        }
        composable(route = Screens.Home.Profile.route) {
            Profile(navController = navController , viewModelProfile = viewModelProfile , socialViewModel = socialViewModel)
        }
        composable(route = Screens.Home.Profile.ProfileEdit.route) {
            ProfileEdit()
        }
        composable(route = Screens.LeaderBoard.route) {
            LeaderBoard(navController = navController)
        }
        composable(route = Screens.CreateWorkout.route) {
            CreateWorkout(navController = navController,viewModel)
        }
        composable(route = Screens.ChooseExercises.route, arguments = listOf(navArgument("name"){type = NavType.StringType})){
            val arg = it.arguments?.getString("name")
            ChooseExercises(navController = navController, arg,viewModel)
        }
        composable(route = Screens.WorkoutSettingScreen.route)
        { WorkoutSettingScreen(navController = navController,viewModel) }
        composable(route = Screens.WorkoutDetails.route)
        {
            WorkoutDetails(navController = navController, projectFitnessViewModel = projectFitnessViewModel , viewModel)
        }
        composable(route = Screens.WorkoutSettingScreenWorkoutDetails.route)
        {
            WorkoutSettingScreenWorkoutDetails(navController = navController, projectFitnessViewModel = projectFitnessViewModel , viewModel)
        }
        composable(route = Screens.WorkoutLog.route)
        {
            WorkoutLog(navController = navController,viewModel)
        }
        composable(route = Screens.Meal.route)
        {
            Meal(navController = navController)
        }
        composable(route = Screens.WorkoutCompleteScreen.route){

            WorkoutCompleteScreen(navController = navController,viewModel)
        }
        composable(route = Screens.WorkoutSettingDetails.route)
        {
            WorkoutSettingDetails(navController = navController, projectFitnessViewModel = projectFitnessViewModel ,viewModel)
        }
        composable(route = Screens.ProjectChallangesScreen.route)
        {
            ProjectChallangesScreen(navController = navController , viewModelSave = viewModel)
        }
        composable(route = Screens.ProjectCoachScreen.route)
        {
            ProjectCoachScreen(navController = navController , viewModelSave = viewModel)
        }
        composable(route = Screens.HistoryExerciseScreen.route)
        {
            HistoryExerciseScreen(navController = navController , viewModelProfile = viewModelProfile)
        }
        composable(route = Screens.ProjectFollowersScreen.route)
        {
            ProjectFollowersScreen(navController = navController , socialViewModel = socialViewModel)
        }
        composable(route = Screens.ProjectFollowScreen.route)
        {
            ProjectFollowScreen(navController = navController , socialViewModel = socialViewModel)
        }
        composable(route = Screens.OtherScreenProfile.route, arguments = listOf(navArgument("nickname"){type = NavType.StringType})){
            val nickname = it.arguments?.getString("nickname")
            if (nickname != null) {
                OtherScreenProfile(navController = navController, socialViewModel , nickname)
            }else {
                Log.d("seeees","looool")}
        }


    }

    }
@Composable
fun NavigationBar(navController: NavController, indexs: Int, flag: Boolean, flag2: Boolean,flag3: Boolean,flag4 : Boolean) {

    val items = listOf("Home", "Activity", "LeaderBoard", "Meal")
    androidx.compose.material3.NavigationBar(
        Modifier
            .fillMaxHeight()
            .height(50.dp)
            .padding(top = 670.dp), containerColor = Color(0xFF181F26)
    ) {

        var flag = flag
        var flag2 = flag2
        var flag3 = flag3
        var flag4 = flag4


        var color = if (flag) {
            Color(0xFFF1C40F)
        } else Color(0xFFB5B5B5)

        var color2 = if (flag2) {
            Color(0xFFF1C40F)
        }else Color(0xFFB5B5B5)
        var color3 = if (flag3) {
            Color(0xFFF1C40F)
        }else Color(0xFFB5B5B5)
        var color4 = if (flag4) {
            Color(0xFFF1C40F)
        }else Color(0xFFB5B5B5)

        items.forEachIndexed { index, item ->
            if (index == 0) { // Click Home Button
                NavigationBarItem(
                    selected = flag,
                    onClick = {
                        if (indexs == 0) { // When inside Home screen
                            flag = false
                        } else if (indexs == 1) {
                            flag = true
                            flag2 = false
                            flag3 = false
                            flag4 = false
                            navController.navigate(Screens.Home.route)

                        } else if (indexs == 2) {
                            flag = true
                            flag2 = false
                            flag3 = false
                            flag4 = false
                            navController.navigate(Screens.Home.route)
                        } else if (indexs == 3) {
                            flag = true
                            flag2 = false
                            flag3 = false
                            flag4 = false
                            navController.navigate(Screens.Home.route)
                        }
                    },
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.home),
                            contentDescription = null,
                            Modifier
                                .size(30.dp)
                        )
                    },
                    Modifier.background(Color(0xFF181F26)),
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color(0xFF181F26),
                        unselectedIconColor = color,
                        selectedIconColor = color
                    ),
                )
            } else if (index == 1) {
                NavigationBarItem(
                    selected = flag2,
                    onClick = {
                        if (indexs == 0) {
                            flag = false
                            flag2 = true
                            flag3 = false
                            flag4 = false
                            navController.navigate(Screens.Activity.route)
                        } else if (indexs == 1) {
                            // Do nothing
                        } else if (indexs == 2) {
                            flag = false
                            flag2 = true
                            flag3 = false
                            flag4 = false
                            navController.navigate(Screens.Activity.route)
                        } else if (indexs == 3) {
                            flag = false
                            flag2 = true
                            flag3 = false
                            flag4 = false
                            navController.navigate(Screens.Activity.route)
                        }
                    },
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.run),
                            contentDescription = null,
                            Modifier
                                .size(30.dp)
                        )
                    },
                    Modifier.background(Color(0xFF181F26)),
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color(0xFF181F26),
                        unselectedIconColor = color2,
                        selectedIconColor = color2
                    ),
                )
            } else if (index == 2) {
                NavigationBarItem(
                    selected = flag3,
                    onClick = {
                        if (indexs == 0) {
                            navController.navigate(Screens.LeaderBoard.route)
                            flag = false
                            flag2 = false
                            flag3 = true
                            flag4 = false
                        } else if (indexs == 1) {
                            flag = false
                            flag2 = false
                            flag3 = true
                            flag4 = false
                            navController.navigate(Screens.LeaderBoard.route)
                        } else if (indexs == 2) {
                            // nothing
                        } else if (indexs == 3) {
                            flag = false
                            flag2 = false
                            flag3 = true
                            flag4 = false
                            navController.navigate(Screens.LeaderBoard.route)
                        }
                    },
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.leaderboard),
                            contentDescription = null,
                            Modifier
                                .size(30.dp)
                        )
                    },
                    Modifier.background(Color(0xFF181F26)),
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color(0xFF181F26),
                        unselectedIconColor = color3,
                        selectedIconColor = color3
                    ),
                )
            } else if (index == 3) {
                NavigationBarItem(
                    selected = flag4,
                    onClick = {
                        if (indexs == 0) {
                            navController.navigate(Screens.Meal.route)
                            flag = false
                            flag2 = false
                            flag3 = false
                            flag4 = true
                        } else if (indexs == 1) {
                            navController.navigate(Screens.Meal.route)
                            flag = false
                            flag2 = false
                            flag3 = false
                            flag4 = true
                        } else if (indexs == 2) {
                            navController.navigate(Screens.Meal.route)
                            flag = false
                            flag2 = false
                            flag3 = false
                            flag4 = true
                        } else if (indexs == 3) {
                            // nothing
                        }
                    },
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.meal),
                            contentDescription = null,
                            Modifier
                                .size(30.dp)
                        )
                    },
                    Modifier.background(Color(0xFF181F26)),
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color(0xFF181F26),
                        unselectedIconColor = color4,
                        selectedIconColor = color4
                    ),
                )
            }

        }
    }
}