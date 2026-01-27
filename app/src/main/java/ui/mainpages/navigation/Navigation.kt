package ui.mainpages.navigation

import HistoryExerciseScreen
import OtherScreenProfile
import ProjectCoachScreen
import SecondInfo
import SocialViewModel
import activity.inside.ChooseExercises
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.projectfitness.R
import com.example.projectfitness.activity.inside.CreateWorkout
import com.example.projectfitness.data.local.repository.WorkoutRepository

// ✅ DÜZELTİLDİ: WorkoutRepository import yolu

// ✅ DÜZELTİLDİ: ViewModel import yolları
import com.example.projectfitness.data.local.viewmodel.ActivityViewModel
import com.example.projectfitness.data.local.viewmodel.ActivityWorkoutOverviewViewModel
import com.example.projectfitness.data.local.viewmodel.HomesViewModel
import com.example.projectfitness.data.local.viewmodel.WorkoutSettingViewModel

// Factory aynı kalıyor (sen bunu com.example.projectfitness.viewmodel altında tutmuştun)
import com.example.projectfitness.viewmodel.WorkoutViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import data.local.viewmodel.ChooseExercisesViewModel
import data.local.viewmodel.CreateWorkoutViewModel

import data.remote.AuthRepository
import data.remote.FirestoreRepository
import data.remote.StorageRepository
import data.remote.UserRepository
import data.remote.WorkoutinRepository
import ui.mainpages.inside.HomesSettings
import ui.mainpages.inside.Profile
import ui.mainpages.inside.ProfileEdit
import homes.inside.ProjectChallangesScreen
import ui.mainpages.inside.ActivityWorkoutOverview
import ui.mainpages.inside.ProjectFollowScreen
import ui.mainpages.inside.ProjectFollowersScreen
import ui.mainpages.inside.WorkoutLog
import ui.mainpages.inside.WorkoutSettingScreen
import ui.mainpages.loginscreens.ForgetPasswordScreen
import ui.mainpages.loginscreens.LoginScreen
import ui.mainpages.loginscreens.RegisterScreen
import ui.mainpages.mainpages.Activity
import ui.mainpages.mainpages.AllWorkouts
import ui.mainpages.mainpages.Home
import ui.mainpages.mainpages.LeaderBoard
import ui.mainpages.mainpages.Meal
import ui.mainpages.openscreen.Info
import ui.mainpages.openscreen.ThirdInfo
import ui.mainpages.other.WorkoutDetails
import ui.mainpages.other.WorkoutSettingDetails
import ui.mainpages.other.WorkoutSettingScreenWorkoutDetails
import viewmodel.AuthViewModel
import viewmodel.ProfileViewModel
import viewmodel.ProjectFitnessViewModel
import viewmodel.ViewModelProfile
import viewmodel.ViewModelSave
import viewmodel.WorkoutinViewModel

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Navigation(workoutRepository: WorkoutRepository) {
    val viewModel : ViewModelSave = viewModel()
    val projectFitnessViewModel : ProjectFitnessViewModel = ProjectFitnessViewModel()
    val viewModelProfile : ViewModelProfile = ViewModelProfile()
    val firestoreRepository : FirestoreRepository = FirestoreRepository()
    val socialViewModel : SocialViewModel = SocialViewModel(repository = firestoreRepository)
    val navController = rememberNavController()
    val authViewModel : AuthViewModel = AuthViewModel(AuthRepository(), UserRepository())
    val profileViewModel : ProfileViewModel = ProfileViewModel(UserRepository(), StorageRepository())
    val workoutinModel : WorkoutinViewModel = WorkoutinViewModel(WorkoutinRepository())
    val currentUser: FirebaseAuth = FirebaseAuth.getInstance()
    val chooseExercisesViewModel: ChooseExercisesViewModel = ChooseExercisesViewModel()

    // ✅ DOĞRU YÖNTEm - ViewModelFactory ile
    val homesViewModel: HomesViewModel = viewModel(
        factory = remember {
            WorkoutViewModelFactory(workoutRepository, currentUser)
        }
    )

    val activityViewModel: ActivityViewModel = viewModel(
        factory = remember { WorkoutViewModelFactory(
            repository = workoutRepository,
            auth = currentUser) },
    )

    NavHost(navController = navController, startDestination = Screens.FirstInfoScreen.route) {

        navigation(
            route = "create_workout_graph",
            startDestination = Screens.ChooseExercises.route
        ) {

            composable(Screens.ChooseExercises.route) { backStackEntry ->

                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("create_workout_graph")
                }

                val createWorkoutViewModel: CreateWorkoutViewModel = viewModel(
                    parentEntry,
                    factory = WorkoutViewModelFactory(workoutRepository, currentUser)
                )

                ChooseExercises(
                    navController = navController,
                    createWorkoutViewModel = createWorkoutViewModel,
                    chooseExercisesViewModel = chooseExercisesViewModel
                )
            }

            composable(Screens.CreateWorkout.route) { backStackEntry ->

                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("create_workout_graph")
                }

                val createWorkoutViewModel: CreateWorkoutViewModel = viewModel(
                    parentEntry,
                    factory = WorkoutViewModelFactory(workoutRepository, currentUser)
                )

                CreateWorkout(
                    navController = navController,
                    viewModelSave = viewModel,
                    workoutinViewModel = workoutinModel,
                    createWorkoutViewModel = createWorkoutViewModel
                )
            }
        }
        composable(route = Screens.HomesSettings.route) {
            HomesSettings(navController = navController, viewModel, projectFitnessViewModel, viewModelProfile)
        }
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
            LoginScreen(navController = navController , authViewModel = authViewModel)
        }
        composable(route = Screens.LoginScreen.ForgetPasswordScreen.route) {
            ForgetPasswordScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(route = Screens.LoginScreen.RegisterScreen.route) {
            RegisterScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(route = Screens.Home.route) {
            Home(navController = navController,viewModel , projectFitnessViewModel ,viewModelProfile = viewModelProfile, authViewModel = authViewModel, homesViewModel = homesViewModel)
        }
        composable(route = Screens.Activity.route) {
            Activity(navController = navController , viewModelSave = viewModel, projectFitnessViewModel, activityViewModel = activityViewModel, authViewModel = authViewModel)
        }
        composable(route = Screens.Home.Profile.route) {
            Profile(navController = navController , viewModelProfile = viewModelProfile , socialViewModel = socialViewModel, authViewModel = authViewModel, profileViewModel = profileViewModel)
        }
        composable(route = Screens.Home.Profile.ProfileEdit.route) {
            ProfileEdit()
        }
        composable(route = Screens.LeaderBoard.route) {
            LeaderBoard(navController = navController, authViewModel = authViewModel)
        }
        composable(
            route = Screens.WorkoutSettingScreen.routeWithArg,
            arguments = listOf(navArgument("workoutId") { type = NavType.StringType })
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getString("workoutId").toString()

            val workoutSettingViewModel: WorkoutSettingViewModel = viewModel(
                key = "WorkoutSettingViewModel_$workoutId",
                factory = remember {
                    object : androidx.lifecycle.ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                            return WorkoutSettingViewModel(workoutRepository, workoutId) as T
                        }
                    }
                }
            )

            WorkoutSettingScreen(
                navController = navController,
                viewModelSave = viewModel,
                workoutSettingViewModel = workoutSettingViewModel
            )
        }

        composable(
            route = Screens.ActivityWorkoutOverview.routeWithArg,
            arguments = listOf(navArgument("workoutId") { type = NavType.StringType })
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getString("workoutId").toString()
            val eoId = backStackEntry.arguments?.getString("eoId").toString()

            val activityWorkoutOverviewViewModel: ActivityWorkoutOverviewViewModel = viewModel(
                key = "ActivityWorkoutOverviewViewModel_$workoutId",
                factory = remember {
                    object : androidx.lifecycle.ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                            return ActivityWorkoutOverviewViewModel(workoutRepository, workoutId) as T
                        }
                    }
                }
            )

            ActivityWorkoutOverview(
                navController = navController,
                viewModelSave = viewModel,
                activityWorkoutOverviewViewModel = activityWorkoutOverviewViewModel
            )
        }


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
        /*composable(route = Screens.WorkoutCompleteScreen.route){

            WorkoutCompleteScreen(navController = navController,viewModel)
        }*/
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
        composable(route = Screens.AllWorkouts.route)
        {
            AllWorkouts(navController = navController, homesViewModel = homesViewModel )
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
    NavigationBar(
        Modifier
            .height(64.dp)
            .fillMaxWidth()
            .background(Color(0xFF121417)),
        containerColor = Color(0xFF121417)
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
                    Modifier.background(Color(0xFF121417)),
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color(0xFF121417),
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
                            painterResource(id = R.drawable.dumbbellicon128),
                            contentDescription = null,
                            Modifier
                                .size(30.dp)
                        )
                    },
                    Modifier.background(Color(0xFF121417)),
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color(0xFF121417),
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
                    Modifier.background(Color(0xFF121417)),
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color(0xFF121417),
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
                    Modifier.background(Color(0xFF121417)),
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color(0xFF121417),
                        unselectedIconColor = color4,
                        selectedIconColor = color4
                    ),
                )
            }

        }
    }
} // Navigasyon barın tüm detayları